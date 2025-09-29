package commands;

import collection.CollectionManager;

/**
 * Command `exit`.
 * Команда для завершения работы программы.
 */
public class ExitCommand implements Command {
    private CollectionManager cm;

    public ExitCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.exit();
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
    }

    @Override
    public String getDescription() {
        return "exit: завершить программу";
    }
}
