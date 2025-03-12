package console;

import collection.CollectionManager;
import fileManager.Parser;

import java.util.Scanner;

/**
 * Класс {@code ConsoleManager} реализует ввод и вывод данных через консоль.
 * Также содержит метод для считывания данных из файла.
 */
public class ConsoleManager implements ReaderWriter {

    /**
     * Конструктор класса {@code ConsoleManager}.
     */
    public ConsoleManager() {
    }

    /**
     * Считывает целое число из консоли.
     *
     * @return введенное число типа {@code int}
     */
    @Override
    public int readInt() {
        Scanner scanner = new Scanner(System.in);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    /**
     * Считывает число типа {@code long} из консоли.
     *
     * @return введенное число типа {@code long}
     */
    @Override
    public long readLong() {
        Scanner scanner = new Scanner(System.in);
        return Long.parseLong(scanner.nextLine().trim());
    }

    /**
     * Считывает число типа {@code float} из консоли.
     *
     * @return введенное число типа {@code float}
     */
    @Override
    public float readFloat() {
        Scanner scanner = new Scanner(System.in);
        return Float.parseFloat(scanner.nextLine().trim());
    }

    /**
     * Считывает число типа {@code double} из консоли.
     *
     * @return введенное число типа {@code double}
     */
    public double readDouble() {
        Scanner scanner = new Scanner(System.in);
        return Double.parseDouble(scanner.nextLine().trim());
    }

    /**
     * Считывает строку из консоли.
     *
     * @return введенная строка без лишних пробелов
     */
    @Override
    public String readLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    /**
     * Выводит текст в консоль.
     *
     * @param text строка, которая будет выведена в консоль
     */
    @Override
    public void write(String text) {
        System.out.println(text);
    }

    /**
     * Запрашивает у пользователя ввод, проверяет, что он не пустой.
     *
     * @param message сообщение перед вводом
     * @return введенное пользователем значение
     */
    @Override
    public String getValidatedValue(String message) {
        write(message);
        while (true) {
            String userPrint = readLine();
            if (!userPrint.isEmpty() && !userPrint.isBlank()) {
                return userPrint;
            }
        }
    }

    /**
     * Метод для считывания файла и управления коллекцией {@code Person}.
     * Запрашивает у пользователя путь к файлу, загружает коллекцию из XML-файла
     * и выполняет команды для работы с коллекцией в интерактивном режиме.
     */
    public void fileRead() {
        while (true) {
            try {
                // Запрашиваем у пользователя путь к файлу
                System.out.println("Введите путь к файлу:");
                Scanner scanner = new Scanner(System.in);
                String path = scanner.nextLine(); // Читаем путь от пользователя

                // Создаем менеджер коллекции и парсер для XML
                CollectionManager collectionManager = new CollectionManager();
                Parser parser = new Parser(path);

                // Устанавливаем имя файла для коллекции
                collectionManager.setFilename(path);

                // Загружаем коллекцию Person из файла
                collectionManager.setCollection(parser.loadFromXml()); // Используем метод loadFromXml для загрузки данных

                // Создаем менеджер команд для работы с коллекцией
                CommandManager commandManager = new CommandManager(collectionManager);

                // Устанавливаем имя файла для менеджера команд
                commandManager.setFilename(path);

                // Выполняем команды до тех пор, пока программа работает
                while (commandManager.getWork()) {
                    commandManager.existCommand(); // Выполняем команду, введенную пользователем
                }

            } catch (IllegalArgumentException e) {
                // Если файл не найден или есть ошибка при его открытии
                System.out.println("Ошибка: файл не найден или недоступен.");
            } catch (Exception e) {
                // Обработка других ошибок
                System.out.println("Что-то пошло не так: " + e.getMessage());
            }
        }
    }
}
