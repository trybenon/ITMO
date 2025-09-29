package server.commands;

import server.dataBase.DataBaseManager;
import shared.commands.Command;
import server.collection.CollectionManager;

/**
 * Удаляет элемент по заданному ID.
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbmanager;


    /**
     * @param cm менеджер коллекции
     */
    public RemoveByIdCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbmanager = new DataBaseManager();
    }

    /**
     * @param args один аргумент — ID (Long)
     * @return сообщение о результате удаления
     */
    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 2) {
            return "Ошибка: ожидался один аргумент — ID.";
        }
        if (!(args[0] instanceof Long)) {
            return "Ошибка: ID должен быть числом.";
        }
        long id = (Long) args[0];
        String login = (String) args[1];
        cm.loadCollection(login);
        boolean removed = cm.removeById(id, login);
        if (removed){dbmanager.removeObject(id, login);}
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
