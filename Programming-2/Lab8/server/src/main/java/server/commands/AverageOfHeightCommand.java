package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;

/**
 * Команда `average_of_height`.
 * Вычисляет и выводит среднее значение поля height в коллекции.
 */
public class AverageOfHeightCommand implements Command {
    private final CollectionManager cm;

    /**
     * Создает команду для вычисления среднего значения height.
     *
     * @param cm менеджер коллекции
     */
    public AverageOfHeightCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `average_of_height`.
     * Вызывает метод {@link CollectionManager#averageOfHeight()}.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public Response execute(Object[] args) {
            String login = (String) args[0];
           return new Response(ResponseStatus.OK, cm.averageOfHeight(login), CommandType.AVERAGE_OF_HEIGHT);
        }


    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "average_of_height: вывести среднее значение height";
    }
}
