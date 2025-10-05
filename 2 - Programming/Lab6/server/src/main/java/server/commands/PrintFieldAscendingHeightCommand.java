package server.commands;

import server.collection.CollectionManager;
import shared.commands.*;
/**
 * Команда `print_field_ascending_height`.
 * Выводит значения поля height всех элементов коллекции в порядке возрастания.
 */
public class PrintFieldAscendingHeightCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `print_field_ascending_height`.
     *
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public PrintFieldAscendingHeightCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `print_field_ascending_height`, вызывая соответствующий метод {@link CollectionManager}.
     *
     * @param args аргументы команды (не используются)
     * @return
     */
    @Override
    public String execute(Object[] args) {
        if (args.length > 0) {
           return "PrintFieldAscendingHeight не принимает аргументы";
        }else {
            return cm.printFieldAscendingHeight();
        }
    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "print_field_ascending_height: вывести значения height всех элементов в порядке возрастания";
    }
}
