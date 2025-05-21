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
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Асинхронный клиент для взаимодействия с сервером коллекции.
 * Подключается к серверу по указанному хосту и порту, отправляет запросы
 * (например, добавление или отображение элементов коллекции) и асинхронно
 * обрабатывает ответы сервера. Поддерживает команды из консоли и выполнение
 * скриптов.
 */
public class ClientApp {
    private static final String HOST = "localhost";
    private static final int PORT = 4899;
    private static final int BUFFER_SIZE = 18192;
    private static final int RECONNECT_DELAY_MS = 1000;
    private static final int CONNECT_TIMEOUT_MS = 10000;
    private static final String PROMPT = "[client] $ ";
    protected static volatile boolean isWaitingForInput = false;

    /**
     * Точка входа в клиентское приложение.
     * Инициализирует консоль и менеджер клиента, затем в цикле пытается
     * установить соединение с сервером, обрабатывать ввод пользователя и
     * получать ответы сервера. При разрыве соединения повторяет попытку
     * подключения.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        ConsoleManager console = new ConsoleManager();
        ClientManager clientManager = new ClientManager();
        RequestBuilder builder = new RequestBuilder(clientManager);

        while (!Thread.currentThread().isInterrupted()) {
            try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open()) {
                connectToServer(channel, console);
                readResponseAsync(channel, console);
                readConsoleInput(console, builder, channel);
            } catch (Exception e) {
                console.print("Сервер недоступен: " +
                        (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка") +
                        ". Повторная попытка через " + RECONNECT_DELAY_MS + " мс...");
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Асинхронно подключается к серверу по указанному хосту и порту.
     * Использует CompletionHandler для обработки успешного подключения
     * или ошибки. Ожидает завершения подключения с таймаутом.
     *
     * @param channel асинхронный канал для подключения
     * @param console менеджер консоли для вывода сообщений
     * @throws InterruptedException если поток прерван
     * @throws ExecutionException если подключение завершилось с ошибкой
     * @throws TimeoutException если превышен таймаут подключения
     */
    private static void connectToServer(AsynchronousSocketChannel channel, ConsoleManager console)
            throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Void> connectFuture = new CompletableFuture<>();
        channel.connect(
                new InetSocketAddress(HOST, PORT),
                null,
                new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        console.print("Подключено к серверу: " + HOST + ":" + PORT);
                        connectFuture.complete(null);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        connectFuture.completeExceptionally(exc);
                    }
                }
        );
        connectFuture.get(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Обрабатывает ввод пользователя из консоли.
     * Поддерживает команды для управления коллекцией, выполнение скриптов
     * (execute_script) и завершение работы клиента (exit). Отправляет
     * сформированные запросы на сервер.
     *
     * @param console менеджер консоли для вывода сообщений
     * @param builder построитель запросов для преобразования ввода в Request
     * @param channel асинхронный канал для отправки запросов
     */
    private static void readConsoleInput(ConsoleManager console,
                                         RequestBuilder builder,
                                         AsynchronousSocketChannel channel) {
        Scanner scanner = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted() && channel.isOpen()) {

            if (isWaitingForInput) continue;

            System.out.print(PROMPT);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("exit")) {
                console.print("Клиент завершает работу.");
                try {
                    channel.close();
                } catch (IOException ignored) {
                }

                System.exit(0);
            }

            if (line.startsWith("save")) {
                console.print("Команда save не поддерживается клиентом.");
                continue;
            }


            if (line.startsWith("execute_script")) {
                String[] parts = line.split("\\s+", 2);
                if (parts.length != 2) {
                    console.print("Нужно: execute_script <путь к файлу>");
                } else {
                    String filename = parts[1];
                    builder.runScript(filename, req -> sendRequest(req, channel, console));
                }
                continue;
            }

            Request req = builder.build(line);
            if (req != null) {
                sendRequest(req, channel, console);
            }

        }
    }

    /**
     * Отправляет запрос на сервер через асинхронный канал.
     * Сериализует запрос, отправляет его длину и данные. Обрабатывает
     * ошибки отправки и закрывает канал при необходимости.
     *
     * @param req запрос для отправки
     * @param channel асинхронный канал для связи с сервером
     * @param console менеджер консоли для вывода сообщений
     */
    private static void sendRequest(Request req,
                                    AsynchronousSocketChannel channel,
                                    ConsoleManager console) {
        try {
            if (!channel.isOpen()) {
                console.print("Канал закрыт, запрос не отправлен.");
                return;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(req);
            oos.flush();
            byte[] data = baos.toByteArray();

            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            lengthBuffer.putInt(data.length);
            lengthBuffer.flip();
            channel.write(lengthBuffer).get();

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            channel.write(dataBuffer).get();
        } catch (IOException e) {
            console.print("Ошибка отправки запроса: " + e.getMessage());
            try {
                channel.close();
            } catch (IOException ignored) {
            }
        } catch (InterruptedException | ExecutionException e) {
            console.print("Ошибка выполнения запроса: " + e.getMessage());
        }
    }

    /**
     * Асинхронно читает ответы от сервера.
     * Сначала читает длину ответа, затем сами данные, десериализует их
     * в объект Response и выводит сообщение. Рекурсивно продолжает
     * чтение следующих ответов.
     *
     * @param channel асинхронный канал для чтения ответов
     * @param console менеджер консоли для вывода сообщений
     */
    private static void readResponseAsync(AsynchronousSocketChannel channel,
                                          ConsoleManager console) {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        ClientManager clientManager = new ClientManager();
        // Читаем длину
        channel.read(lengthBuffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                if (result == -1) {
                    console.print("Сервер закрыл соединение.");
                    try {
                        channel.close();
                    } catch (IOException ignored) {
                    }
                    return;
                }
                if (lengthBuffer.hasRemaining()) {
                    channel.read(lengthBuffer, null, this);
                    return;
                }

                lengthBuffer.flip();
                int dataLength = lengthBuffer.getInt();
                if (dataLength <= 0 || dataLength > BUFFER_SIZE) {
                    console.print("Некорректный размер ответа: " + dataLength);
                    try {
                        channel.close();
                    } catch (IOException ignored) {
                    }
                    return;
                }

                ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength);
                // Читаем данные
                channel.read(dataBuffer, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer bytesRead, Void attachment) {
                        if (dataBuffer.hasRemaining()) {
                            channel.read(dataBuffer, null, this);
                            return;
                        }

                        try {
                            dataBuffer.flip();
                            byte[] data = new byte[dataLength];
                            dataBuffer.get(data);
                            ByteArrayInputStream bais = new ByteArrayInputStream(data);
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            Response resp = (Response) ois.readObject();
                            console.print(resp.getMessage());
                            if (resp.getMessage().startsWith("Найден")) {
                                Long id = (Long) resp.getData();
                                isWaitingForInput = true; // Блокируем обработку команд
                                console.print("Введите новые данные персонажа:");

                                Person p = clientManager.getPerson(); // Теперь будет читать ввод синхронно
                                if (p != null) {
                                    Request req = new Request(CommandType.UPDATE, new Object[]{id, p});
                                    sendRequest(req, channel, console);
                                }

                                isWaitingForInput = false; // Разблокируем
                            }
                            // Рекурсивно ждём следующий ответ
                            readResponseAsync(channel, console);

                        } catch (IOException | ClassNotFoundException e) {
                            console.print("Ошибка десериализации: " + e.getMessage());
                            try {
                                channel.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        String text = exc.getMessage();
                        console.print("Ошибка чтения длины: " + (text == null || text.isBlank()
                                ? "соединение закрыто" : text));
                        try {
                            channel.close();
                        } catch (IOException ignored) {
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                console.print("Ошибка чтения длины: " +
                        (exc != null ? exc.getMessage() : "соединение закрыто"));
                try {
                    channel.close();
                } catch (IOException ignored) {
                }
            }
        });
    }
}