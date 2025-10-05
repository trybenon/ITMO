package server.commands;

import shared.commands.Command;

public class CommandsList {

    public enum CommandT {
        HELP(HelpCommand.class, "helpHelp"),
        INFO(InfoCommand.class, "infoHelp"),
        ADD(AddCommand.class, "addHelp"),
        UPDATE(UpdateIdCommand.class, "updateHelp"),
        REMOVE_BY_ID(RemoveByIdCommand.class, "removeByIdHelp"),
        CLEAR(ClearCommand.class, "clearHelp"),
        HEAD(HeadCommand.class, "headHelp"),
        ADD_IF_MAX(AddIfMaxCommand.class, "addIfMaxHelp"),
        AVERAGE_OF_HEIGHT(AverageOfHeightCommand.class, "averageOfHeightHelp"),
        PRINT_ASCENDING(PrintAscendingCommand.class, "printAscendingHelp");



        private final Class<? extends Command> executableClass;
        private final String description;

        CommandT(Class<? extends Command> executableClass, String description) {
            this.executableClass = executableClass;
            this.description = description;
        }

        public Class<? extends Command> getExecutableClass() {
            return executableClass;
        }

        public String getDescription() {
            return description;
        }
    }
}
