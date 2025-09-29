package server.commands;

import shared.commands.Command;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand implements Command {
    @Override
    public Response execute(Object[] args) {
         ArrayList<String> descriptions = Arrays.stream(CommandsList.CommandT.values())
                .map(CommandsList.CommandT::getDescription)
                .filter(description -> !description.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        return new Response(ResponseStatus.OK, descriptions, CommandType.HELP);
    }

    @Override
    public String getDescription() {
        return "";
    }
}
