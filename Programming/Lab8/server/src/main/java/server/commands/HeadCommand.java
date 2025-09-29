package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;

import java.util.LinkedList;

/**
 * Команда `head`.
 * Выводит первый элемент коллекции.
 */
public class HeadCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public HeadCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `head`.
     * Выводит первый элемент коллекции.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public Response execute(Object[] args) {
            String login = (String) args[0];
            return new Response(ResponseStatus.OK, cm.head(login), CommandType.HEAD);
        }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "head: вывести первый элемент коллекции";
    }
}
