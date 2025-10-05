package server.commands;

import shared.commands.Command;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;

import java.util.LinkedList;

public class UpdateIdCommand implements Command {
    private final CollectionManager cm;

    public UpdateIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Object[] args) {
        long id = (Long) args[0];
        Person newPerson = (Person) args[1];
        String login = (String) args[2];
        cm.loadCollection(login);
        String message = cm.updateId(id, newPerson, login);
        LinkedList<Person> people = cm.getPeople();
        return new Response(ResponseStatus.REFRESH, message, CommandType.UPDATE, people);
    }

    @Override
    public String getDescription() {
        return "update {id} {element}: обновить элемент по ID";
    }
}