package server.serverUtil;

import shared.dto.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

public class WriteThread extends RecursiveAction {
    Response response;
    SocketChannel clientChannel;
    Logger logger;
    SelectionKey key;

    public WriteThread(SocketChannel clientChannel, Logger logger, SelectionKey key, Response response) {
        this.clientChannel = clientChannel;
        this.logger = logger;
        this.key = key;
        this.response = response;
    }

    @Override
    protected void compute() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            logger.info("Отправка ответа в потоке " + Thread.currentThread().getId() + " для " + ServerApp.getRemoteAddress(clientChannel));
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

            logger.info("Ответ отправлен " + ServerApp.getRemoteAddress(clientChannel));
            key.interestOps(SelectionKey.OP_READ);
            key.attach(new ServerApp.ClientState());
        } catch (IOException e) {
            logger.warning("Ошибка отправки ответа: " + e.getMessage());
            ServerApp.closeClient(clientChannel, key);
        }
    }

    }
