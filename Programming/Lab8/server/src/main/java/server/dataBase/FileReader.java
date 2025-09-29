package server.dataBase;


import server.serverUtil.ServerApp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {
    private static String password;
    private static String user;

    public static void read() {
        String filePath = System.getenv("PASS_PATH");

        if (filePath == null || filePath.isBlank()) {
            ServerApp.logger.warning("Переменная окружения не найдена, подключение к базе данных невозможно!");
            System.exit(0);
        }

        try {
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                ServerApp.logger.warning("Файл не найден: " + filePath + " \n подключение к базе данных невозможно!");
                System.exit(0);
            }
            String[] tmp = Files.readString(path, StandardCharsets.UTF_8).split(" ");

            if (tmp.length < 2) {
                ServerApp.logger.warning("Неверный формат файла с паролем");
                System.exit(0);
            }

            user     = stripBom(tmp[0].trim());
            password = stripBom(tmp[1].trim());

        } catch (IOException e) {
            ServerApp.logger.warning("Ошибка чтения файла");
            System.exit(0);
        }
    }

    private static String stripBom(String s) {
        return s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF'
                ? s.substring(1)
                : s;
    }


    public static String getPassword() {
        return password;
    }

    public static String getUser(){
        return user;
    }
}