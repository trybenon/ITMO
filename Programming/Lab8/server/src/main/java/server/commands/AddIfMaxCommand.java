package server.commands;

import shared.commands.Command;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;

import java.util.LinkedList;

public class AddIfMaxCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public AddIfMaxCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public Response execute(Object[] args) {
        Person person = (Person) args[0];
        String login = (String) args[1];
        String message = cm.addIfMax(person, login);
        LinkedList<Person> people = cm.getPeople();

        return new Response(ResponseStatus.REFRESH, message, CommandType.ADD_IF_MAX, people);
    }

    @Override
    public String getDescription() {
        return "add_if_max {element}: добавить новый элемент, если его рост больше максимального";
    }
}