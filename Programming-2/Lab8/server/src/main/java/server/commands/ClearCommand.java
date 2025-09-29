package server.commands;

import shared.commands.Command;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;

import java.awt.event.PaintEvent;
import java.util.LinkedList;

public class ClearCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public ClearCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public Response execute(Object[] args) {

        String login = (String) args[0];
        String message = cm.clear(login);
        LinkedList<Person> people = cm.getPeople();

        return new Response(ResponseStatus.REFRESH, message, CommandType.CLEAR, people);
    }

    @Override
    public String getDescription() {
        return "clear: очистить коллекцию";
    }
}