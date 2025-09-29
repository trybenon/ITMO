package client;

import client.console.ClientManager;
import client.console.ConsoleManager;
import shared.dto.CommandType;
import shared.dto.Request;
import shared.dto.Response;
import shared.model.Person;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Главный класс клиентского приложения, реализующий неблокирующий клиент
 * с использованием {@link Selector} и {@link SocketChannel} для взаимодействия с сервером.
 * Поддерживает команды для управления коллекцией и выполнение скриптов через консоль.
 */
public class ClientApp {
    /** Хост сервера для подключения. */
    private static final String HOST = "localhost";
    /** Порт сервера для подключения. */
    private static final int PORT = 4899;
    /** Размер буфера для чтения данных в байтах. */
    private static final int BUFFER_SIZE = 18192;
    /** Задержка перед повторной попыткой подключения в миллисекундах. */
    private static final int RECONNECT_DELAY_MS = 1000;
    /** Приглашение командной строки клиента. */
    private static final String PROMPT = "[client] $ ";
    /** Флаг, указывающий, ожидает ли клиент ввода данных (например, для {@link Person}). */
    protected static volatile boolean isWaitingForInput = false;

    /** Менеджер консоли для вывода сообщений. */
    private final ConsoleManager console;
    /** Менеджер клиента для обработки ввода данных. */
    private final ClientManager clientManager;
    /** Построитель запросов для создания объектов {@link Request}. */
    private final RequestBuilder builder;
    /** Канал клиента для связи с сервером. */
    private SocketChannel socketChannel;
    /** Селектор для управления событиями ввода-вывода. */
    private Selector selector;
    /** Флаг, указывающий, подключен ли клиент к серверу. */
    private boolean isConnected = false;
    /** Флаг, указывающий, ожидает ли клиент ответа от сервера. */
    private boolean isWaitingForResponse = false;
    /** Флаг, указывающий, работает ли клиент. */
    private boolean running = true;
    /** Флаг, указывающий на проблемы с соединением. */
    private boolean connectionProblem = true;
    /** Очередь запросов для отправки на сервер. */
    private Queue<Request> requestQueue = new ConcurrentLinkedQueue<>();
    /** Буфер для чтения длины входящих данных (4 байта). */
    private ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
    /** Буфер для чтения данных ответа. */
    private ByteBuffer dataBuffer;
    /** Длина ожидаемых данных в байтах. */
    private int dataLength = -1;

    /**
     * Конструктор клиента, инициализирующий менеджеры консоли, клиента и построитель запросов.
     */
    public ClientApp() {
        this.console = new ConsoleManager();
        this.clientManager = new ClientManager();
        this.builder = new RequestBuilder(clientManager);
    }

    /**
     * Точка входа для запуска клиента.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        ClientApp client = new ClientApp();
        client.run();
    }

    /**
     * Инициализирует клиентский канал и регистрирует его в селекторе.
     *
     * @throws IOException если не удается открыть канал или начать подключение
     */
    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(HOST, PORT));

        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    /**
     * Основной цикл клиента, управляющий подключением, обработкой событий и консольным вводом.
     */
    public void run() {
        if (connectionProblem && !isConnected) {
            try {
                connect();
            } catch (IOException e) {
                try {
                    noConnectionHandler();
                } catch (IOException ex) {
                    console.print("Ошибка подключения: " + ex.getMessage());
                    return;
                }
            }
        }

        while (running) {
            try {
                int readyChannels = selector.select(100);
                if (readyChannels == 0 && !socketChannel.isConnected()) {
                    noConnectionHandler();
                }

                if (readyChannels > 0) {
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();
                        if (key.isConnectable()) {
                            successConnect(key);
                        } else if (key.isReadable()) {
                            read(key);
                        } else if (key.isWritable()) {
                            write(key);
                        }
                    }
                }
                processConsoleInput();

            } catch (IOException e) {
                try {
                    noConnectionHandler();
                } catch (IOException ex) {
                    console.print("Ошибка подключения: " + ex.getMessage());
                }
            } catch (ClassNotFoundException e) {
                console.print("Ошибка десериализации: " + e.getMessage());
            }
        }
        System.exit(0);
    }

    /**
     * Обрабатывает отсутствие соединения с сервером, пытаясь переподключиться.
     *
     * @throws IOException если не удается закрыть ресурсы или начать новое подключение
     */
    private void noConnectionHandler() throws IOException {
        closeResources();
        try {
            console.print("Сервер недоступен. Повторная попытка через " + RECONNECT_DELAY_MS + " мс...");
            Thread.sleep(RECONNECT_DELAY_MS);
            connect();
            connectionProblem = true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Завершает подключение к серверу и регистрирует канал для чтения.
     *
     * @param key ключ селектора для клиентского канала
     * @throws IOException если не удается завершить подключение
     */
    private void successConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.finishConnect()) {
            channel.register(selector, SelectionKey.OP_READ);
            console.print("Подключено к серверу: " + HOST + ":" + PORT);
            isConnected = true;
            connectionProblem = false;
        }
    }

    /**
     * Читает ответ от сервера, десериализует его и обрабатывает.
     *
     * @param key ключ селектора для клиентского канала
     * @throws IOException если произошла ошибка чтения
     * @throws ClassNotFoundException если не удалось десериализовать объект
     */
    private void read(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = (dataBuffer != null) ? dataBuffer : lengthBuffer;
        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            noConnectionHandler();
            isConnected = false;
            return;
        }

        if (buffer.hasRemaining()) {
            return;
        }

        if (dataBuffer == null) {
            lengthBuffer.flip();
            dataLength = lengthBuffer.getInt();
            lengthBuffer.clear();
            if (dataLength <= 0 || dataLength > BUFFER_SIZE) {
                console.print("Некорректный размер ответа: " + dataLength);
                closeResources();
                return;
            }
            dataBuffer = ByteBuffer.allocate(dataLength);
        } else {
            dataBuffer.flip();
            byte[] data = new byte[dataLength];
            dataBuffer.get(data);
            dataBuffer = null;
            dataLength = -1;

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Response response = (Response) ois.readObject();
                console.print(response.getMessage());

                if (response.getMessage().startsWith("Найден")) {
                    isWaitingForInput = true;
                    Long id = (Long) response.getData();
                    console.print("Введите новые данные персонажа:");
                    Person p = clientManager.getPerson();
                    if (p != null) {
                        requestQueue.offer(new Request(CommandType.UPDATE, new Object[]{id, p}));
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                    isWaitingForInput = false;
                }
            }

            isWaitingForResponse = false;
            System.out.print(PROMPT);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    /**
     * Отправляет запрос на сервер, сериализуя объект {@link Request}.
     *
     * @param key ключ селектора для клиентского канала
     * @throws IOException если произошла ошибка записи
     */
    private void write(SelectionKey key) throws IOException {
        if (requestQueue.isEmpty()) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        }

        SocketChannel channel = (SocketChannel) key.channel();
        Request request = requestQueue.peek();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            oos.flush();
            byte[] data = baos.toByteArray();

            ByteBuffer lengthBuf = ByteBuffer.allocate(4).putInt(data.length);
            lengthBuf.flip();
            channel.write(lengthBuf);

            ByteBuffer dataBuf = ByteBuffer.wrap(data);
            channel.write(dataBuf);

            requestQueue.poll();
            isWaitingForResponse = true;
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     * Обрабатывает консольный ввод для команд пользователя.
     */
    private void processConsoleInput() {
        if (isConnected && !isWaitingForResponse && !isWaitingForInput) {
            try {
                Scanner scanner = new Scanner(System.in);
                if (System.in.available() > 0) {
                    System.out.print(PROMPT);
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        return;
                    }

                    if (line.equalsIgnoreCase("exit")) {
                        console.print("Клиент завершает работу.");
                        closeResources();
                        running = false;
                        return;
                    }

                    if (line.startsWith("save")) {
                        console.print("Команда save не поддерживается клиентом.");
                        return;
                    }
                    if (line.startsWith("execute_script")) {
                        String[] parts = line.split("\\s+", 2);
                        if (parts.length != 2) {
                            console.print("Нужно: execute_script <путь к файлу>");
                        } else {
                            String filename = parts[1];
                            builder.runScript(filename, req -> {
                                requestQueue.offer(req);
                                try {
                                    SelectionKey key = socketChannel.keyFor(selector);
                                    if (key != null) key.interestOps(SelectionKey.OP_WRITE);
                                } catch (Exception e) {
                                    console.print("Ошибка отправки запроса: " + e.getMessage());
                                }
                            });
                        }
                        return;
                    }

                    Request req = builder.build(line);
                    if (req != null) {
                        requestQueue.offer(req);
                        SelectionKey key = socketChannel.keyFor(selector);
                        if (key != null) key.interestOps(SelectionKey.OP_WRITE);
                    }
                }
            } catch (IOException e) {
                console.print("Ошибка ввода: " + e.getMessage());
            }
        }
    }

    /**
     * Закрывает клиентский канал, селектор и очищает очередь запросов.
     */
    private void closeResources() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
        } catch (IOException e) {
            console.print("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
        isConnected = false;
        connectionProblem = true;
        requestQueue.clear();
    }
}