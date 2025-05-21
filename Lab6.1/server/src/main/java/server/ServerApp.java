package server;

import server.collection.CollectionManager;
import server.fileManager.Parser;
import shared.dto.Request;
import shared.dto.Response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.logging.*;

/**
 * Серверное приложение для управления коллекцией.
 * Обрабатывает клиентские запросы через неблокирующий Selector, поддерживает
 * команды для управления коллекцией (например, добавление, удаление) и серверные
 * команды (save, exit) через консоль. Сохраняет коллекцию в файл, указанный
 * в переменной окружения FILENAME.
 */
public class ServerApp {
    private static final int PORT = 4899;
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final int MAX_DATA_LENGTH = 1024 * 1024;
    private static CollectionManager collectionManager;

    /**
     * Состояние клиента для отслеживания чтения данных.
     * Хранит буферы для длины и данных запроса, а также размер ожидаемых данных.
     */
    static class ClientState {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        ByteBuffer dataBuffer = null;
        int dataLength = -1;
    }

    /**
     * Точка входа в серверное приложение.
     * Инициализирует логирование, загружает коллекцию из файла, указанного в
     * переменной окружения FILENAME, и запускает сервер на указанном порту.
     * Обрабатывает клиентские запросы через Selector и серверные команды (save, exit)
     * через консоль. Сохраняет коллекцию при завершении работы.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Настройка логирования
        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Ошибка настройки логирования: " + e.getMessage());
        }

        logger.info("Запуск сервера");

        String filename = System.getenv("FILENAME");
        if (filename == null || filename.isEmpty()) {
            logger.severe("Переменная окружения FILENAME не установлена");
            System.exit(1);
        }

        // Инициализация менеджера коллекции
        collectionManager = new CollectionManager();
        collectionManager.setFilename(filename);
        try {
            Parser parser = new Parser(filename);
            collectionManager.setCollection(parser.loadFromXml());
            logger.info("Коллекция загружена из файла: " + filename);
        } catch (Exception e) {
            logger.severe("Ошибка загрузки коллекции: " + e.getMessage());
            System.exit(1);
        }

        CommandDispatcher dispatcher = new CommandDispatcher(collectionManager);

        // Сохранение коллекции при завершении JVM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                collectionManager.save();
                logger.info("Коллекция сохранена при завершении работы");
            } catch (Exception e) {
                logger.severe("Ошибка сохранения коллекции при завершении: " + e.getMessage());
            }
        }));

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        boolean promptShown = false;

        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Сервер запущен на порту " + PORT);

            while (true) {
                // Даем селектору проснуться раз в 500 мс
                selector.select(500);

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        logger.warning("Невалидный ключ");
                        continue;
                    }

                    if (key.isAcceptable()) {
                        acceptClient(key, selector);
                    } else if (key.isReadable()) {
                        readRequest(key, dispatcher);
                    } else if (key.isWritable()) {
                        writeResponse(key);
                    }
                }

                // Админ-консоль
                try {
                    if (!promptShown) {
                        System.out.print("[server-admin] $ ");
                        System.out.flush();
                        promptShown = true;
                    }
                    if (consoleReader.ready()) {
                        String line = consoleReader.readLine().trim();
                        promptShown = false;
                        if ("save".equalsIgnoreCase(line)) {
                            collectionManager.save();
                            logger.info("Коллекция сохранена по админ-команде save");
                        } else if ("exit".equalsIgnoreCase(line)) {
                            collectionManager.save();
                            logger.info("Коллекция сохранена по админ-команде exit — завершаем работу");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    logger.warning("Ошибка чтения админ-команды: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка сервера: " + e.getMessage());
        }
    }

    /**
     * Принимает новое клиентское соединение.
     * Настраивает неблокирующий канал клиента, регистрирует его в селекторе
     * для чтения и прикрепляет состояние клиента.
     *
     * @param key ключ селектора для серверного канала
     * @param selector селектор для регистрации клиента
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private static void acceptClient(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
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
     * Читает запрос от клиента.
     * Считывает длину данных, затем сами данные, десериализует запрос
     * и передаёт его в диспетчер команд. Прикрепляет ответ к ключу
     * и переключает ключ на запись.
     *
     * @param key ключ селектора для клиентского канала
     * @param dispatcher диспетчер команд для обработки запроса
     */
    private static void readRequest(SelectionKey key, CommandDispatcher dispatcher) {
        if (!key.isValid()) {
            logger.warning("Чтение с невалидного ключа");
            return;
        }

        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        if (state == null) {
            logger.severe("State не инициализирован для " + getRemoteAddress(clientChannel));
            try {
                clientChannel.close();
                key.cancel();
            } catch (IOException ignored) {}
            return;
        }

        ByteBuffer buffer = (state.dataBuffer != null ? state.dataBuffer : state.lengthBuffer);
        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            logger.warning("Ошибка чтения от " + getRemoteAddress(clientChannel) + ": " + e.getMessage());
            try {
                clientChannel.close();
                key.cancel();
            } catch (IOException ignored) {}
            return;
        }

        // Клиент закрыл соединение
        if (bytesRead == -1) {
            logger.info("Клиент отключился: " + getRemoteAddress(clientChannel));
            try {
                collectionManager.save();
                logger.info("Коллекция сохранена после отключения клиента");
            } catch (Exception e) {
                logger.severe("Ошибка сохранения после отключения: " + e.getMessage());
            }
            try {
                clientChannel.close();
                key.cancel();
            } catch (IOException ignored) {}
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
                try {
                    clientChannel.close();
                    key.cancel();
                } catch (IOException ignored) {}
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
     * Отправляет ответ клиенту.
     * Сериализует ответ, отправляет его длину и данные через клиентский канал.
     * После отправки переключает ключ на чтение и сбрасывает состояние клиента.
     *
     * @param key ключ селектора для клиентского канала
     */
    private static void writeResponse(SelectionKey key) {
        if (!key.isValid()) {
            logger.warning("Запись в невалидный ключ");
            return;
        }

        SocketChannel clientChannel = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        if (!(attachment instanceof Response response)) {
            logger.warning("Нет ответа для клиента");
            try {
                clientChannel.close();
                key.cancel();
            } catch (IOException ignored) {}
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
            try {
                clientChannel.close();
                key.cancel();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Возвращает строковое представление удалённого адреса клиента.
     *
     * @param channel клиентский канал
     * @return строка с адресом клиента или "unknown" при ошибке
     */
    private static String getRemoteAddress(SocketChannel channel) {
        try {
            return channel.getRemoteAddress().toString();
        } catch (IOException e) {
            return "unknown";
        }
    }
}