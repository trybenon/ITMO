package server.commands;

import shared.commands.Command;
import shared.dto.Response;
import shared.model.Person;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;

public class AddIfMaxCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public AddIfMaxCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 2 || !(args[0] instanceof Person) || !(args[1] instanceof String)) {
            return "Ошибка: ожидались Person и login.";
        }
        Person person = (Person) args[0];
        String login = (String) args[1];
        cm.loadCollection(login);
        Response response = dbManager.addIfMax(person, login, cm);
        return response.getMessage();
    }

    @Override
    public String getDescription() {
        return "add_if_max {element}: добавить новый элемент, если его рост больше максимального";
    }
}