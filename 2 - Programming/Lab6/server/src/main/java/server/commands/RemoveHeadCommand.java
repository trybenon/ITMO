package server.commands;

import shared.commands.Command;
import server.collection.CollectionManager;

/**
 * Удаляет первый элемент коллекции.
 */
public class RemoveHeadCommand implements Command {
    private final CollectionManager cm;

    public RemoveHeadCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public String execute(Object[] args) {
        if (args.length > 0) {
            return "Ошибка: команда remove_head не принимает аргументы.";
        }
        return cm.removeHead();  // предполагается, что этот метод возвращает String
    }

    @Override
    public String getDescription() {
        return "remove_head: удалить первый элемент";
    }
}
