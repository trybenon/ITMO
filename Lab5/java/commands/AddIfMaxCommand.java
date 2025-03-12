package commands;

import collection.CollectionManager;
import model.Person;

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
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            try {
                // Получаем новый объект Person через ClientManager
                Person person = cm.getClientManager().getPerson();

                // Добавляем, если новый элемент больше максимального
                cm.addIfMax(person);
            } catch (IllegalArgumentException e) {
                System.out.println("Введены неверные аргументы. Попробуйте еще раз.");
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
        return "add_if_max {element}: добавить новый элемент в коллекцию, если его значение больше максимального";
    }
}
