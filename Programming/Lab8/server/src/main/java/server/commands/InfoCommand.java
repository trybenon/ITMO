package server.commands;

import server.collection.CollectionManager;
import shared.commands.Command;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.dto.User;

public class InfoCommand implements Command {
    private final CollectionManager cm;

    private User user;

    public InfoCommand(CollectionManager cm) {
        this.cm = cm;
    }


    public User getUser() {
        return user;
    }

    @Override
    public Response execute(Object[] args) {
        String login = (String) args[0];
        cm.loadCollection(login);
        return new Response(ResponseStatus.OK, cm.info(login), CommandType.INFO);
    }

    @Override
    public String getDescription() {
        return "";
    }
}