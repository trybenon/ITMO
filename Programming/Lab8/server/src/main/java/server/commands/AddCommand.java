package server.commands;

import shared.commands.Command;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AddCommand implements Command {
    private final CollectionManager cm;

    public AddCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Object[] args) {
        Person person = (Person) args[0];
        String login = (String) args[1];
        String message = cm.add(person, login);
        LinkedList<Person> people = cm.getPeople();
        return new Response(ResponseStatus.REFRESH, message, CommandType.ADD, people);
    }

    @Override
    public String getDescription() {
        return "add {element}: добавить новый элемент в коллекцию";
    }
}