package commands;

import collection.CollectionManager;

/**
 * Команда `clear`.
 * Очищает коллекцию, удаляя все элементы.
 */
public class ClearCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `clear`.
     *
     * @param cm менеджер коллекции, который управляет данными
     */
    public ClearCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `clear`, очищая коллекцию.
     * Если передано неверное количество аргументов, выбрасывается исключение.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.clear();
            System.out.println("Коллекция очищена.");
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "clear: очистить коллекцию";
    }
}
