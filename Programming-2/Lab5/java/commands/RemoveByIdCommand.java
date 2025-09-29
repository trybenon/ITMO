package commands;

import collection.CollectionManager;
import model.Person;

/**
 * Команда `remove_by_id id`.
 * Удаляет элемент коллекции по его ID.
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `remove_by_id`.
     *
     * @param cm менеджер коллекции
     */
    public RemoveByIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `remove_by_id id`.
     * Удаляет элемент с указанным ID из коллекции.
     *
     * @param args аргументы команды (ID элемента)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            try {
                long id = Long.parseLong(args[1]); // Преобразуем аргумент в число

                // Ищем объект с указанным ID
                Person toRemove = null;
                for (Person person : cm.getPeople()) {
                    if (person.getId() == id) {
                        toRemove = person;
                        break;
                    }
                }

                if (toRemove != null) {
                    cm.getPeople().remove(toRemove);
                    System.out.println("Элемент успешно удален.");
                } else {
                    System.out.println("Ошибка: элемент с таким ID не найден.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть числом.");
            }
        } else {
            throw new IllegalArgumentException("Ошибка: неверное количество аргументов.");
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "remove_by_id id: удалить элемент из коллекции по его ID";
    }
}
