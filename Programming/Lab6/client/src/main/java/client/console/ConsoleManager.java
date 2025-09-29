package client.console;


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
    public void print(String s){
        System.out.println(s);
    }
}
