package server.commands;

import shared.commands.Command;
import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;

import java.util.LinkedList;

public class RemoveHeadCommand implements Command {
    private final CollectionManager cm;
    private final DataBaseManager dbManager;

    public RemoveHeadCommand(CollectionManager cm) {
        this.cm = cm;
        this.dbManager = new DataBaseManager();
    }

    @Override
    public Response execute(Object[] args) {

        String login = (String) args[0];
        cm.loadCollection(login);
        String message = cm.removeHead(login);
        LinkedList<Person> people = cm.getPeople();
        return new Response(ResponseStatus.REFRESH, message, CommandType.REMOVE_HEAD, people);
    }

    @Override
    public String getDescription() {
        return "remove_head: удалить первый элемент";
    }
}