package commands;

import collection.CollectionManager;
import console.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Команда `execute_script file_name`.
 * Выполняет команды, записанные в файле, последовательно.
 * Позволяет автоматизировать выполнение команд.
 */
public class ExecuteScriptCommand implements Command {
    private final CollectionManager cm;
    private static final Set<String> executingFiles = new HashSet<>(); // Хранит имена выполняемых файлов

    /**
     * Конструктор команды `execute_script`.
     *
     * @param cm менеджер коллекции
     */
    public ExecuteScriptCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `execute_script file_name`.
     * Читает команды из указанного файла и выполняет их последовательно.
     * Обрабатывает возможные ошибки, такие как циклические вызовы.
     *
     * @param args аргументы команды (должен быть передан путь к файлу)
     */
    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: неверное количество аргументов. Используйте: execute_script file_name");
        }

        String fileName = args[1];

        // Проверяем, не выполняется ли уже этот файл (защита от рекурсии)
        if (executingFiles.contains(fileName)) {
            System.out.println("Ошибка: обнаружен рекурсивный вызов скрипта '" + fileName + "'. Выполнение остановлено.");
            return;
        }

        executingFiles.add(fileName); // Добавляем файл в список выполняемых

        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String[] commandAndArgs = scanner.nextLine().trim().split(" ");

                // Проверяем, существует ли команда
                if (CommandManager.getCommands().containsKey(commandAndArgs[0])) {
                    CommandManager.getCommands().get(commandAndArgs[0]).execute(commandAndArgs);
                } else {
                    System.out.println("Ошибка: команда \"" + commandAndArgs[0] + "\" не найдена.");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл '" + fileName + "' не найден.");
        } finally {
            executingFiles.remove(fileName); // Удаляем файл из списка после выполнения
        }
    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "execute_script file_name: выполнить команды из указанного файла";
    }
}
