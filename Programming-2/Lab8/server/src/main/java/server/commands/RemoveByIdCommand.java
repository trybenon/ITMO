package server.commands;

import server.dataBase.DataBaseManager;
import shared.commands.Command;
import server.collection.CollectionManager;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;

import java.util.LinkedList;

/**
 * Удаляет элемент по заданному ID.
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager cm;


    /**
     * @param cm менеджер коллекции
     */
    public RemoveByIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * @param args один аргумент — ID (Long)
     * @return сообщение о результате удаления
     */
    @Override
    public Response execute(Object[] args) {

        long id = (Long) args[0];
        String login = (String) args[1];
        cm.loadCollection(login);
        String message = cm.removeById(id, login);
        LinkedList<Person> people = cm.getPeople();
        return new Response(ResponseStatus.REFRESH, message, CommandType.REMOVE_BY_ID, people);
    }

    /**
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "remove_by_id {id}: удалить элемент по ID";
    }
}
