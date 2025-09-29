package server.serverUtil;

import server.commands.*;
import shared.dto.CommandType;
import shared.dto.Request;
import shared.dto.Response;
import server.collection.CollectionManager;
import shared.dto.ResponseStatus;

import java.util.logging.Logger;


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

        Response resp;
        CommandType respType;

        try {
            switch (type) {
                case HELP:
                    resp = new HelpCommand().execute(args);
                    break;
                case ADD:
                    resp = new AddCommand(cm).execute(args);
                    break;
                case ADD_IF_MAX:
                    resp = new AddIfMaxCommand(cm).execute(args);
                    break;
                case REMOVE_BY_ID:
                    resp = new RemoveByIdCommand(cm).execute(args);
                    break;
                case UPDATE:
                    resp = new UpdateIdCommand(cm).execute(args);
                    break;
                case CLEAR:
                    resp = new ClearCommand(cm).execute(args);

                    break;
                case SHOW:
                    resp = new ShowCommand(cm).execute(args);
                    break;
                case INFO:
                    resp = new InfoCommand(cm).execute(args);
                    break;
                case AVERAGE_OF_HEIGHT:
                    resp = new AverageOfHeightCommand(cm).execute(args);
                    break;
                case PRINT_ASCENDING:
                    resp = new PrintAscendingCommand(cm).execute(args);

                    break;
                case PRINT_FIELD_ASCENDING_HEIGHT:
                    resp = new PrintFieldAscendingHeightCommand(cm).execute(args);
                    break;
                case HEAD:
                    resp = new HeadCommand(cm).execute(args);

                    break;
                case REGISTRATION:
                    resp = new RegistrationCommand(cm).execute(args);

                    break;
                case AUTHENTICATE:
                    resp = new AuthenticateCommand(cm).execute(args);

                    break;
                case REMOVE_HEAD:
                    resp = new RemoveHeadCommand(cm).execute(args);

                    break;

                default:
                    resp = new Response(ResponseStatus.ERROR, "Error");
            }
        } catch (Exception e) {
            log.severe("Ошибка при выполнении " + type + ": " + e.getMessage());
            return new Response(ResponseStatus.ERROR, "Ошибка выполнения" + type, type);
        }
        return resp;
    }


}

