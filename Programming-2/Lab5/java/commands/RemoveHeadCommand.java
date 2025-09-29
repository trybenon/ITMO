package commands;

import collection.CollectionManager;
import model.Person;

/**
 * Команда `remove_head`.
 * Выводит первый элемент коллекции и удаляет его.
 */
public class RemoveHeadCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `remove_head`.
     *
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public RemoveHeadCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `remove_head`.
     * Выводит первый элемент коллекции и удаляет его.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Ошибка: неверное количество аргументов. Используйте: remove_head");
        }

        if (cm.getPeople().isEmpty()) {
            System.out.println("Ошибка: коллекция пуста.");
        } else {
            cm.removeHead();
        }
    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "remove_head: вывести первый элемент коллекции и удалить его";
    }
}
