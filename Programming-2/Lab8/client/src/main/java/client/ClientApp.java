package client;

import client.console.ClientManager;
import client.console.ConsoleManager;
import client.fx.DisconnectListener;
import client.fx.RefreshCollectionListener;
import shared.dto.*;
import shared.model.Person;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Главный класс клиентского приложения, реализующий неблокирующий клиент
 * с использованием {@link Selector} и {@link SocketChannel} для взаимодействия с сервером.
 * Поддерживает команды, такие как login, logout, registration, и выполнение скриптов.
 */
public class ClientApp {
    /** Хост сервера для подключения. */
    private static final String HOST = "localhost";
    /** Порт сервера для подключения. */
    private static final int PORT = 4899;
    /** Размер буфера для чтения данных в байтах. */
    private static final int BUFFER_SIZE = 18920;
    /** Задержка перед повторной попыткой подключения в миллисекундах. */
    private static final int RECONNECT_DELAY_MS = 5000;
    /** Приглашение командной строки клиента. */
    protected static final String PROMPT = "[client] $ ";

    /** Флаг, указывающий, ожидает ли клиент ввода данных (например, для {@link Person}). */
    protected static volatile boolean isWaitingForInput = false;
    /** Текущий пользователь клиента. */
    public static User user = new User();
    private static volatile Response mainResponse;
    private static ArrayList<DisconnectListener> disconnectListeners = new ArrayList<>();
    private static String language = "Русский";


    /** Менеджер консоли для вывода сообщений. */
    private final ConsoleManager console;

    /** Построитель запросов для создания объектов {@link Request}. */
    public final RequestBuilder builder;
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
    private static Queue<Request> requestQueue = new ConcurrentLinkedQueue<>();
    /** Буфер для чтения длины входящих данных (4 байта). */
    private ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
    /** Буфер для чтения данных ответа. */
    private ByteBuffer dataBuffer;
    /** Длина ожидаемых данных в байтах. */
    private int dataLength = -1;
    private static Request mainRequest;

    private static LinkedList<Person> persons = new LinkedList<>();

    private static ArrayList<RefreshCollectionListener> refreshCollectionListeners = new ArrayList<>();

    private static ClientApp instance;
    /**
     * Конструктор клиента.
     */
    public ClientApp() {
        this.console = new ConsoleManager();
        this.builder = new RequestBuilder();
        instance = this;
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

    public static Response getResponse() {
        Response rep = mainResponse;
        mainResponse = null;
        return rep;
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
                //processConsoleInput();

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
            for (DisconnectListener listener : disconnectListeners){
                listener.disconnect();
            }
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
            for (DisconnectListener listener : disconnectListeners){
                listener.connect();
            }
            console.print("Подключено к серверу: " + HOST + ":" + PORT);
            if (user.getLogin() == null)
            { advice();} else {
                System.out.println("C возвращением " + user.getLogin() + "!");
            }
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
                mainResponse = response;
                if (response.getStatus().equals(ResponseStatus.REFRESH)){
                    persons.clear();
                    persons.addAll(response.getPersons());

                    for (RefreshCollectionListener listener : refreshCollectionListeners){
                        listener.refresh();
                    }

                }

            ClientApp.mainResponse = response;
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
                    if (line.equals("logout")) {
                        if (user.getLogin() != null) {
                            user.setLogin(null);
                            console.print("Вы покинули аккаунт.");
                            System.out.print(PROMPT);
                        } else {
                            console.print("Вы еще не вошли в аккаунт, чтобы его покидать.");
                        }
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



    public static User getUser(){
        return user;
    }
    public void setUser(User user){
        this.user = user;
    }

    public static void addDisconnectListener(DisconnectListener listener){
        disconnectListeners.add(listener);
    }

    public static void removeDisconnectListener(DisconnectListener listener){
        disconnectListeners.remove(listener);
    }

    public static String getLanguage(){
        return language;
    }

    public static void setLanguage(String language) {
        ClientApp.language = language;
    }

    public static LinkedList<Person> getPersons(){
        return persons;
    }
    public static void addRefreshListener(RefreshCollectionListener listener){
        refreshCollectionListeners.add(listener);
    }


        public static void setRequest(Request req) {
            if (req != null) {
                requestQueue.offer(req);
                try {
                    ClientApp client = getInstance();
                    if (client.isConnected && client.socketChannel != null && client.socketChannel.isConnected()) {
                        SelectionKey key = client.socketChannel.keyFor(client.selector);
                        if (key != null && key.isValid()) {
                            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }

    private static ClientApp getInstance() {
        return instance;
    }

    /**
     * Выводит подсказку с доступными командами авторизации.
     */
    private void advice() {
        String advice = String.join("\n",
                "===================================================",
                "              Вы не вошли в аккаунт ",
                "===================================================",
                " login <login> <password> : войти в аккаунт",
                " logout : выйти из аккаунта",
                " registration <login> <password> : создать аккаунт",
                "==================================================="
        );
        console.print(advice);
        System.out.print(PROMPT);
    }
}