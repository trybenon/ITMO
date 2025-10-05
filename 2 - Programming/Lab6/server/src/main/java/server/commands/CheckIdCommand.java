package server.commands;

import server.collection.CollectionManager;
import shared.commands.Command;

public class CheckIdCommand implements Command {
    private final CollectionManager cm;


    public CheckIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public String execute(Object[] args) {
        long id = (Long) args[0];
        if (cm.existId(id))
            return "Найден Id" + id;
        return "Id " + id + " не найден";
    }
    public Object getId(Object[] arg){
        return arg[0];
    }

    @Override
    public String getDescription() {
        return "";
    }
}
