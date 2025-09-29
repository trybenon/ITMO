package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
/**
 * Команда `print_ascending`.
 * Выводит элементы коллекции в порядке возрастания.
 */
public class PrintAscendingCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public PrintAscendingCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `print_ascending`.
     * Выводит элементы коллекции в порядке возрастания.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public String execute(Object[] args) {
        if (args.length > 0) {
            return "PrintAscending не принимает аргументы";
        } else {
            return cm.printAscending();
        }
    }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "print_ascending: вывести элементы коллекции в порядке возрастания";
    }
}
