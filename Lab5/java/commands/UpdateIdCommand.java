package commands;

import collection.CollectionManager;
import model.Person;

/**
 * Команда `update id {element}`.
 * Позволяет обновить существующий элемент коллекции по заданному ID.
 */
public class UpdateIdCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `update`.
     *
     * @param cm объект {@link CollectionManager}, управляющий коллекцией
     */
    public UpdateIdCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `update id {element}`.
     * Запрашивает новый объект у пользователя и заменяет существующий элемент с указанным ID.
     * Если элемент с таким ID не найден, выводится сообщение об ошибке.
     *
     * @param args аргументы команды (должен содержать ID элемента)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            try {
                long id = Long.parseLong(args[1]); // Преобразование аргумента в число
                Person person = cm.getClientManager().getPerson(); // Получаем новый объект
                boolean found = false;

                // Поиск элемента по ID
                for (Person person1 : cm.getPeople()) {
                    if (person1.getId() == id) {
                        cm.updateId(id, person);
                        System.out.println("Элемент успешно обновлен.");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("Ошибка: элемент с таким ID не найден.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть числом.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка ввода данных. Попробуйте еще раз.");
            }
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
    }

    /**
     * Описание команды.
     *
     * @return строка с описанием команды
     */
    @Override
    public String getDescription() {
        return "update id {element}: обновить элемент коллекции по заданному ID";
    }
}
