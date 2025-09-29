package server;

import server.collection.CollectionManager;
import server.fileManager.Parser;
import shared.dto.Request;
import shared.dto.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.*;

/**
 * Главный класс серверного приложения, реализующий неблокирующий сервер
 * с использованием {@link Selector} и {@link SocketChannel} для обработки клиентских запросов.
 * Управляет коллекцией через {@link CollectionManager}, сохраняет её в файл, указанный
 * в переменной окружения FILENAME, и поддерживает админ-команды (save, exit).
 */
public class ServerApp {
    /** Порт, на котором сервер принимает подключения. */
    private static final int PORT = 4899;
    /** Максимальный размер данных в байтах для одного запроса. */
    private static final int MAX_DATA_LENGTH = 1024 * 1024;
    /** Логгер для записи событий сервера. */
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());

    /**
     * Внутренний класс для хранения состояния клиентского соединения.
     */
    static class ClientState {
        /** Буфер для чтения длины входящих данных (4 байта). */
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        /** Буфер для чтения данных запроса. */
        ByteBuffer dataBuffer = null;
        /** Длина ожидаемых данных в байтах. */
        int dataLength = -1;
    }

    /** Менеджер коллекции для управления данными. */
    private final CollectionManager collectionManager;
    /** Диспетчер команд для обработки запросов. */
    private final CommandDispatcher dispatcher;
    /** Канал сервера для принятия клиентских подключений. */
    private ServerSocketChannel serverChannel;
    /** Селектор для управления событиями ввода-вывода. */
    private Selector selector;
    /** Флаг, указывающий, работает ли сервер. */
    private boolean running = true;
    /** Флаг, указывающий на проблемы с соединением. */
    private boolean connectionProblem = true;

    /**
     * Конструктор сервера, инициализирующий менеджер коллекции и диспетчер команд.
     */
    public ServerApp() {
        this.collectionManager = new CollectionManager();
        this.dispatcher = new CommandDispatcher(collectionManager);
    }

    /**
     * Точка входа для запуска сервера.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        ServerApp server = new ServerApp();
        server.run();
    }

    /**
     * Основной цикл сервера, инициализирующий подключение, загрузку коллекции и обработку событий.
     */
    public void run() {
        setupLogger();
        logger.info("Запуск сервера");

        String filename = System.getenv("FILENAME");
        if (filename == null || filename.isEmpty()) {
            logger.severe("Переменная окружения FILENAME не установлена");
            System.exit(1);
        }

        collectionManager.setFilename(filename);
        try {
            Parser parser = new Parser(filename);
            collectionManager.setCollection(parser.loadFromXml());
            logger.info("Коллекция загружена из файла: " + filename);
        } catch (Exception e) {
            logger.severe("Ошибка загрузки коллекции: " + e.getMessage());
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                collectionManager.save();
                logger.info("Коллекция сохранена при завершении работы");
            } catch (Exception e) {
                logger.severe("Ошибка сохранения коллекции при завершении: " + e.getMessage());
            }
        }));

        try {
            connect();

            while (running) {
                try {
                    int readyChannels = selector.select(500);
                    if (readyChannels > 0) {
                        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                        while (keys.hasNext()) {
                            SelectionKey key = keys.next();
                            keys.remove();
                            if (key.isAcceptable()) {
                                acceptClient(key);
                            } else if (key.isReadable()) {
                                readRequest(key);
                            } else if (key.isWritable()) {
                                writeResponse(key);
                            }
                        }
                    }
                    processConsoleInput();

                } catch (IOException e) {
                    logger.severe("Ошибка сервера: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    logger.severe("Ошибка десериализации: " + e.getMessage());
                }
            }
            closeResources();
            System.exit(0);

        } catch (IOException e) {
            logger.severe("Ошибка инициализации сервера: " + e.getMessage());
        }
    }

    /**
     * Инициализирует серверный канал и регистрирует его в селекторе.
     *
     * @throws IOException если не удается открыть канал или привязать порт
     */
    private void connect() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(PORT));
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Сервер запущен на порту " + PORT);
        connectionProblem = false;
    }

    /**
     * Принимает новое клиентское подключение и регистрирует его в селекторе.
     *
     * @param key ключ селектора для серверного канала
     * @throws IOException если не удается принять подключение
     */
    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = server.accept();
        if (clientChannel == null) {
            logger.warning("Не удалось принять соединение");
            return;
        }
        clientChannel.configureBlocking(false);
        SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
        clientKey.attach(new ClientState());
        logger.info("Подключён клиент: " + getRemoteAddress(clientChannel));
    }

    /**
     * Читает запрос от клиента, десериализует его и передает в диспетчер.
     *
     * @param key ключ селектора для клиентского канала
     * @throws IOException если произошла ошибка чтения
     * @throws ClassNotFoundException если не удалось десериализовать объект
     */
    private void readRequest(SelectionKey key) throws IOException, ClassNotFoundException {
        if (!key.isValid()) {
            logger.warning("Чтение с невалидного ключа");
            return;
        }

        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        if (state == null) {
            logger.severe("State не инициализирован для " + getRemoteAddress(clientChannel));
            closeClient(clientChannel, key);
            return;
        }

        ByteBuffer buffer = (state.dataBuffer != null ? state.dataBuffer : state.lengthBuffer);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            logger.info("Клиент отключился: " + getRemoteAddress(clientChannel));
            try {
                collectionManager.save();
                logger.info("Коллекция сохранена после отключения клиента");
            } catch (Exception e) {
                logger.severe("Ошибка сохранения после отключения: " + e.getMessage());
            }
            closeClient(clientChannel, key);
            return;
        }

        if (buffer.hasRemaining()) {
            logger.fine("Частичное чтение от " + getRemoteAddress(clientChannel));
            return;
        }

        if (state.dataBuffer == null) {
            state.lengthBuffer.flip();
            state.dataLength = state.lengthBuffer.getInt();
            state.lengthBuffer.clear();
            if (state.dataLength <= 0 || state.dataLength > MAX_DATA_LENGTH) {
                logger.warning("Неверный размер данных: " + state.dataLength);
                closeClient(clientChannel, key);
                return;
            }
            state.dataBuffer = ByteBuffer.allocate(state.dataLength);
            logger.info("Ожидаем данные длиной " + state.dataLength + " от " + getRemoteAddress(clientChannel));
        } else {
            state.dataBuffer.flip();
            byte[] bytes = new byte[state.dataLength];
            state.dataBuffer.get(bytes);
            state.dataBuffer = null;
            state.dataLength = -1;

            Response response;
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                Object obj = ois.readObject();
                if (!(obj instanceof Request request)) {
                    logger.warning("Некорректный объект от клиента");
                    response = new Response(false, "Неверный запрос");
                } else {
                    logger.info("Запрос " + request.getType() + " от " + getRemoteAddress(clientChannel));
                    try {
                        response = dispatcher.dispatch(request);
                    } catch (Exception e) {
                        logger.severe("Ошибка обработки: " + e.getMessage());
                        response = new Response(false, "Ошибка сервера: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.warning("Ошибка десериализации: " + e.getMessage());
                response = new Response(false, "Ошибка обработки данных: " + e.getMessage());
            }

            key.attach(response);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    /**
     * Отправляет ответ клиенту, сериализуя объект {@link Response}.
     *
     * @param key ключ селектора для клиентского канала
     * @throws IOException если произошла ошибка записи
     */
    private void writeResponse(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            logger.warning("Запись в невалидный ключ");
            return;
        }

        SocketChannel clientChannel = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        if (!(attachment instanceof Response response)) {
            logger.warning("Нет ответа для клиента");
            closeClient(clientChannel, key);
            return;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            oos.flush();
            byte[] data = baos.toByteArray();

            ByteBuffer lengthBuf = ByteBuffer.allocate(4).putInt(data.length);
            lengthBuf.flip();
            while (lengthBuf.hasRemaining()) {
                clientChannel.write(lengthBuf);
            }

            ByteBuffer dataBuf = ByteBuffer.wrap(data);
            while (dataBuf.hasRemaining()) {
                clientChannel.write(dataBuf);
            }

            logger.info("Ответ отправлен " + getRemoteAddress(clientChannel));
            key.interestOps(SelectionKey.OP_READ);
            key.attach(new ClientState());
        } catch (IOException e) {
            logger.warning("Ошибка отправки ответа: " + e.getMessage());
            closeClient(clientChannel, key);
        }
    }

    /**
     * Обрабатывает консольный ввод для админ-команд (save, exit).
     */
    private void processConsoleInput() {
        try {
            Scanner scanner = new Scanner(System.in);
            if (System.in.available() > 0) {
                logger.info("[server-admin] $ ");
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("save")) {
                    collectionManager.save();
                    logger.info("Коллекция сохранена по админ-команде save");
                } else if (line.equalsIgnoreCase("exit")) {
                    collectionManager.save();
                    logger.info("Коллекция сохранена по админ-команде exit — завершаем работу");
                    closeResources();
                    running = false;
                }
            }
        } catch (IOException e) {
            logger.warning("Ошибка чтения админ-команды: " + e.getMessage());
        }
    }

    /**
     * Закрывает серверный канал и селектор.
     */
    private void closeResources() {
        try {
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close();
            }
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
        } catch (IOException e) {
            logger.severe("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
        connectionProblem = true;
    }

    /**
     * Закрывает клиентский канал и отменяет ключ селектора.
     *
     * @param clientChannel клиентский канал
     * @param key ключ селектора
     */
    private void closeClient(SocketChannel clientChannel, SelectionKey key) {
        try {
            clientChannel.close();
            key.cancel();
        } catch (IOException e) {
            logger.warning("Ошибка при закрытии клиентского соединения: " + e.getMessage());
        }
    }

    /**
     * Настраивает логгер для записи в файл и вывода в консоль.
     */
    private void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(true);
        } catch (IOException e) {
            logger.severe("Ошибка настройки логирования: " + e.getMessage());
        }
    }

    /**
     * Возвращает адрес удаленного клиента.
     *
     * @param channel клиентский канал
     * @return строковое представление адреса или "unknown" при ошибке
     */
    private String getRemoteAddress(SocketChannel channel) {
        try {
            return channel.getRemoteAddress().toString();
        } catch (IOException e) {
            return "unknown";
        }
    }
}