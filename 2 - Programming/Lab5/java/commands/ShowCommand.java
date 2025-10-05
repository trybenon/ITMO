package commands;

import collection.CollectionManager;

/**
 * Команда `show`.
 * Выводит все элементы коллекции.
 */
public class ShowCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public ShowCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `show`.
     * Выводит все элементы коллекции.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.show();
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
        return "show: вывести все элементы коллекции";
    }
}
