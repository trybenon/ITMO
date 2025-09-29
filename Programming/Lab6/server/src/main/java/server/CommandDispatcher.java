package server;

import server.commands.*;
import shared.dto.CommandType;
import shared.dto.Request;
import shared.dto.Response;
import shared.model.Person;
import server.collection.CollectionManager;

import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Dispatcher команд: читает Request, выполняет соответствующую команду и формирует Response.
 */
public class CommandDispatcher {
    private static final Logger log = Logger.getLogger(CommandDispatcher.class.getName());
    private final CollectionManager cm;

    public CommandDispatcher(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполнить команду из Request и вернуть результат в Response.
     */
    public Response dispatch(Request request) {
        CommandType type = request.getType();

        Object[] rawArgs = (Object[]) request.getArgs();

        Object[] args = rawArgs != null ? rawArgs : new Object[0];

        String message;
        Object data = null;
        boolean success = true;

        try {
            switch (type) {
                case HELP:
                    message = cm.helpString();
                    break;
                case ADD:
                    message = new AddCommand(cm).execute(args);
                    break;
                case ADD_IF_MAX:
                    message = new AddIfMaxCommand(cm).execute(args);
                    break;
                case REMOVE_BY_ID:
                    message = new RemoveByIdCommand(cm).execute(args);
                    break;
                case UPDATE:
                    message = new UpdateIdCommand(cm).execute(args);
                    break;
                case CLEAR:
                    message = new ClearCommand(cm).execute(args);
                    break;
                case SHOW:
                    String out = new ShowCommand(cm).execute(args);
                    message = out.isEmpty() ? "Коллекция пуста." : out;
                    break;
                case INFO:
                    message = cm.infoString();
                    break;
                case AVERAGE_OF_HEIGHT:
                    message = new AverageOfHeightCommand(cm).execute(args);
                    break;
                case PRINT_ASCENDING:
                    message = new PrintAscendingCommand(cm).execute(args);
                    break;
                case PRINT_FIELD_ASCENDING_HEIGHT:
                    message = new PrintFieldAscendingHeightCommand(cm).execute(args);
                    break;
                case HEAD:
                    message = new HeadCommand(cm).execute(args);
                    break;
                case REMOVE_HEAD:
                    message = new RemoveHeadCommand(cm).execute(args);
                    break;
                case CHECK_ID:
                    CheckIdCommand ci = new CheckIdCommand(cm);
                    message = ci.execute(args);
                    data = ci.getId(args);
                    break;

                default:
                    success = false;
                    message = "Неизвестная команда: " + type;
            }
        } catch (Exception e) {
            log.severe("Ошибка при выполнении " + type + ": " + e.getMessage());
            success = false;
            message = "Ошибка: " + e.getMessage();
        }
        return new Response(success, message, data);
    }
}
