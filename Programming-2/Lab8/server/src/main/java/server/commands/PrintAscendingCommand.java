package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;

/**
 * Команда `print_ascending`.
 * Выводит элементы коллекции в порядке возрастания.
 */
public class PrintAscendingCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public PrintAscendingCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `print_ascending`.
     * Выводит элементы коллекции в порядке возрастания.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public Response execute(Object[] args) {

            String login = (String) args[0];
            cm.loadCollection(login);
            return new Response(ResponseStatus.OK, cm.printAscending(), CommandType.PRINT_ASCENDING);
        }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "print_ascending: вывести элементы коллекции в порядке возрастания";
    }
}
