package commands;

import collection.CollectionManager;

/**
 * Команда `info`.
 * Выводит информацию о коллекции (тип, дата инициализации, количество элементов).
 */
public class InfoCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public InfoCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `info`.
     * Выводит информацию о коллекции.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.info();  // Вызов метода info() из CollectionManager
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
        return "info: вывести информацию о коллекции";
    }
}
