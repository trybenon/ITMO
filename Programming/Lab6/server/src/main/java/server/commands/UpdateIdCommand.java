package server.commands;

import shared.commands.Command;
import shared.model.Person;
import server.collection.CollectionManager;

/**
 * Обновляет элемент коллекции по заданному ID.
 */
public class UpdateIdCommand implements Command {
    private final CollectionManager cm;

    /**
     * @param cm менеджер коллекции
     */
    public UpdateIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * @param args массив из двух элементов:
     *             args[0] — ID (Long),
     *             args[1] — новый объект Person
     * @return сообщение о результате выполнения
     */
    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 2) {
            return "Ошибка: ожидались два аргумента — ID и Person.";
        }
        if (!(args[0] instanceof Long) || !(args[1] instanceof Person)) {
            return "Ошибка: некорректные типы аргументов.";
        }

        long id = (Long) args[0];
        Person newPerson = (Person) args[1];

        for (Person existing : cm.getPeople()) {
            if (existing.getId() == id) {
                try {
                    cm.updateId(id, newPerson);
                    return "Элемент с ID " + id + " обновлен.";
                } catch (IllegalArgumentException e) {
                    return "Ошибка при обновлении: " + e.getMessage();
                }
            }
        }
        return "Элемент с ID " + id + " не найден.";
    }

    /**
     * @return краткое описание команды
     */
    @Override
    public String getDescription() {
        return "update {id} {element}: обновить элемент по ID";
    }
}
