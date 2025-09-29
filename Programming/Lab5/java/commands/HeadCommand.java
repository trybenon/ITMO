package commands;

import collection.CollectionManager;

/**
 * Команда `head`.
 * Выводит первый элемент коллекции.
 */
public class HeadCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public HeadCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `head`.
     * Выводит первый элемент коллекции.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.head();
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
        return "head: вывести первый элемент коллекции";
    }
}
