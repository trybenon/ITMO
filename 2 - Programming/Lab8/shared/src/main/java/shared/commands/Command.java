package shared.commands;

import shared.dto.Response;

public interface Command {
    public Response execute(Object[] args);

    public String getDescription();


}
