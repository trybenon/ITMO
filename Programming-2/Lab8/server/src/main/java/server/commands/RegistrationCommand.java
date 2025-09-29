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
    public Response execute(Object[] args) {

       String login = (String) args[0];
       String password = (String) args[1];
       Response response = dbManager.registration(new User(login, password));
    return response;
    }

    @Override
    public String getDescription() {
        return "";
    }
}