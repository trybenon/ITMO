package commands;

import collection.CollectionManager;

/**
 * Команда `help`.
 * Выводит справку по доступным командам.
 */
public class HelpCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public HelpCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `help`.
     * Выводит список доступных команд.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.help();  // Вызов метода help() из CollectionManager
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
    }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "help: вывести справку по доступным командам";
    }
}
