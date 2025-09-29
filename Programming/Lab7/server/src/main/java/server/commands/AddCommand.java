package server.commands;

import shared.commands.Command;
import shared.dto.Response;
import shared.model.Person;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;

public class AddCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public AddCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 2 || !(args[0] instanceof Person)) {
            return "Ошибка: команда add ожидает Person и login.";
        }
        Person person = (Person) args[0];
        String login = (String) args[1];
        cm.loadCollection(login);
        Response response = dbManager.addObject(person, login, cm);
        return response.getMessage();
    }

    @Override
    public String getDescription() {
        return "add {element}: добавить новый элемент в коллекцию";
    }
}