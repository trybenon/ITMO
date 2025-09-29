package server.commands;

import shared.commands.Command;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;
import shared.dto.Response;

public class ClearCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public ClearCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 1 || !(args[0] instanceof String)) {
            return "Ошибка: ожидался login.";
        }
        String login = (String) args[0];
        cm.loadCollection(login);
        Response response = dbManager.clear(login, cm);
        return response.getMessage();
    }

    @Override
    public String getDescription() {
        return "clear: очистить коллекцию";
    }
}