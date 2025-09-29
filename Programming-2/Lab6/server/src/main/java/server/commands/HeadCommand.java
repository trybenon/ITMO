package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
/**
 * Команда `head`.
 * Выводит первый элемент коллекции.
 */
public class HeadCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public HeadCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `head`.
     * Выводит первый элемент коллекции.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public String execute(Object[] args) {
        if (args.length > 0)  {
            return "head не принимает аргументы";

        } else {
            return cm.head();
        }
    }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "head: вывести первый элемент коллекции";
    }
}
