package console;

import collection.CollectionManager;
import model.Person;
import commands.*;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Класс {@code CommandManager} управляет выполнением команд для работы с коллекцией {@link Person}.
 *
 * @author Egorova Varvara
 */
public class CommandManager {
    /**
     * Состояние программы.
     */
    private boolean isWorking = true;

    /**
     * Хэш-карта всех команд.
     */
    private static HashMap<String, Command> commands = new HashMap<>();

    /**
     * Имя файла с основной коллекцией.
     */
    private String filename;

    /**
     * Конструктор, который создает объект класса {@code CommandManager} и заполняет хэш-карту команд.
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public CommandManager(CollectionManager collectionManager) {
        commands.put("help", new HelpCommand(collectionManager));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateIdCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("save", new SaveCommand(collectionManager));
        commands.put("execute_script", new ExecuteScriptCommand(collectionManager));
        commands.put("exit", new ExitCommand(collectionManager));
        commands.put("add_if_max", new AddIfMaxCommand(collectionManager));
        commands.put("average_of_height", new AverageOfHeightCommand(collectionManager));
        commands.put("print_field_ascending_height", new PrintFieldAscendingHeightCommand(collectionManager));
        commands.put("head", new HeadCommand(collectionManager));
        commands.put("remove_head", new RemoveHeadCommand(collectionManager));
        commands.put("print_ascending", new PrintAscendingCommand(collectionManager));

    }

    /**
     * Устанавливает имя файла с основной коллекцией.
     * @param filename имя файла
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Получает хэш-карту команд.
     * @return хэш-карта с командами
     */
    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    /**
     * Возвращает состояние программы.
     * @return состояние программы
     */
    public boolean getWork() {
        return this.isWorking;
    }

    /**
     * Читает команду с консоли и выполняет ее.
     * Программа ожидает ввода команды и аргументов, а затем выполняет соответствующие действия.
     */
    public void existCommand() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.flush();  // Очищает буфер вывода
            System.out.println("Введите команду: ");
            String command = scanner.nextLine().trim().toLowerCase();  // Считывает команду
            String[] args = command.split(" ");  // Разделяет команду и аргументы
            if (commands.containsKey(args[0])) {
                try {
                    // Выполняет команду
                    commands.get(args[0]).execute(args);
                } catch (IllegalArgumentException e) {
                    System.out.println("Что-то пошло не так. " + e.getMessage() + " Попробуйте еще раз.");
                }
            } else {
                System.out.println("Команда \"" + args[0] + "\" не найдена.");
            }
        } catch (Exception e) {
            System.out.println("Что-то пошло не так. " + e.getMessage() + ". До свидания.");
            this.isWorking = false;
            System.exit(0);
        }
    }
}
