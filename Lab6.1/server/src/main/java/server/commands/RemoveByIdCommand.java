package server.commands;

import shared.commands.Command;
import server.collection.CollectionManager;

/**
 * Удаляет элемент по заданному ID.
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager cm;

    /**
     * @param cm менеджер коллекции
     */
    public RemoveByIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * @param args один аргумент — ID (Long)
     * @return сообщение о результате удаления
     */
    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 1) {
            return "Ошибка: ожидался один аргумент — ID.";
        }
        if (!(args[0] instanceof Long)) {
            return "Ошибка: ID должен быть числом.";
        }
        long id = (Long) args[0];
        boolean removed = cm.removeById(id);
        return removed
                ? "Элемент с ID " + id + " удалён."
                : "Элемент с ID " + id + " не найден.";
    }

    /**
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "remove_by_id {id}: удалить элемент по ID";
    }
}
