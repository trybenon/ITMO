package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;

/**
 * Команда `print_field_ascending_height`.
 * Выводит значения поля height всех элементов коллекции в порядке возрастания.
 */
public class PrintFieldAscendingHeightCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `print_field_ascending_height`.
     *
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public PrintFieldAscendingHeightCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `print_field_ascending_height`, вызывая соответствующий метод {@link CollectionManager}.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public Response execute(Object[] args) {

            String login = (String) args[0];
            return new Response(ResponseStatus.OK, cm.printFieldAscendingHeight(login), CommandType.PRINT_FIELD_ASCENDING_HEIGHT);

    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "print_field_ascending_height: вывести значения height всех элементов в порядке возрастания";
    }
}
