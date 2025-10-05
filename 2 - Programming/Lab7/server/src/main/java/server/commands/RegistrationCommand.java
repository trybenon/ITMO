package server.commands;

import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;
import shared.commands.Command;
import shared.dto.Response;
import shared.dto.User;


public class RegistrationCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public RegistrationCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof String)) {
            return "Ошибка: команда registration ожидает login и password.";
        }
       String login = (String) args[0];
       String password = (String) args[1];
       Response response = dbManager.registration(new User(login, password, true));
    return response.getMessage();
    }

    @Override
    public String getDescription() {
        return "";
    }
}