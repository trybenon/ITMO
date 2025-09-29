package server.commands;

import shared.commands.Command;
import shared.dto.Response;
import shared.model.Person;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;

public class UpdateIdCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public UpdateIdCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 3) {
            return "Ошибка: ожидались ID, Person и login.";
        }
        long id = (Long) args[0];
        Person newPerson = (Person) args[1];
        String login = (String) args[2];
        cm.loadCollection(login);
        Response response = dbManager.updateObject(id, newPerson, login, cm);
        return response.getMessage();
    }

    @Override
    public String getDescription() {
        return "update {id} {element}: обновить элемент по ID";
    }
}