package server.commands;

import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;
import shared.commands.Command;
import shared.model.Person;

import java.util.LinkedList;

public class CheckIdCommand implements Command {
    private final CollectionManager cm;



    public CheckIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public String execute(Object[] args) {
        long id = (Long) args[0];
        String login = (String) args[1];
        cm.loadCollection(login);
        if (cm.existId(id)){
            return "Найден Id " + id;}
        return "Id " + id + " не найден или принадлежит другому пользователю.";
    }
    public Object getId(Object[] arg){
        return arg[0];
    }

    @Override
    public String getDescription() {
        return "";
    }
}
