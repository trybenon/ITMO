package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;
import shared.commands.Command;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда `show`.
 * Выводит все элементы коллекции, отсортированные по имени.
 */
public class ShowCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор, который принимает объект {@link CollectionManager}.
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public ShowCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `show`.
     * Выводит все элементы коллекции, отсортированные по имени.
     *
     * @param args аргументы команды (не используются)
     * @return строку с перечислением всех объектов коллекции
     */
    @Override
    public String execute(Object[] args) {
        if (args.length != 0) {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
        return cm.show();
    }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "show: вывести все элементы коллекции, отсортированные по имени";
    }
}
