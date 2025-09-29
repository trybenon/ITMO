package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;
import shared.commands.*;
/**
 * Команда `add_if_max`.
 * Добавляет новый элемент в коллекцию, если его значение превышает наибольшее значение
 * среди существующих элементов коллекции по определенному критерию.
 */
public class AddIfMaxCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `add_if_max`.
     * @param cm менеджер коллекции
     */
    public AddIfMaxCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `add_if_max`.
     * Добавляет новый элемент в коллекцию, если его значение превышает наибольшее
     * среди всех элементов коллекции по полю {@link Person#getHeight()}.
     *
     * @param args аргументы команды (не используются в данной реализации)
     * @return
     */
    @Override
    public String execute(Object[] args) {
        if (args.length != 1) {
            return "Ошибка: неверное количество аргументов. Ожидается: один Person.";
        }
        if (!(args[0] instanceof Person)) {
            return "Ошибка: аргумент должен быть Person.";
        }

        Person newPerson = (Person) args[0];
        try {
            boolean added = cm.addIfMax(newPerson);  // пусть CollectionManager возвращает true/false
            return added
                    ? "Новый человек добавлен, так как его рост больше максимального."
                    : "Новый человек не был добавлен, так как его рост не превышает максимальный.";
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "add_if_max {element}: добавить новый элемент в коллекцию, если его значение больше максимального";
    }
}
