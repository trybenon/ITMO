package server.commands;

import shared.commands.Command;
import shared.model.Person;
import server.collection.CollectionManager;

/**
 * Команда add: добавляет новый элемент типа {@link Person} в коллекцию,
 * если его ID уникален.
 */
public class AddCommand implements Command {
    private final CollectionManager cm;

    /**
     * Создает экземпляр команды add.
     *
     * @param cm менеджер коллекции, в которую будут добавляться элементы
     */
    public AddCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду добавления нового элемента в коллекцию.
     *
     * @param args массив аргументов команды; ожидается ровно один элемент типа {@link Person}
     * @return сообщение о результате выполнения:
     *         <ul>
     *           <li>успех: "Элемент успешно добавлен: &lt;person&gt;"</li>
     *           <li>ошибка количества/типа аргументов или бизнес-ошибка: соответствующий текст</li>
     *         </ul>
     */
    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 1) {
            return "Ошибка: команда add ожидает один аргумент типа Person.";
        }
        if (!(args[0] instanceof Person)) {
            return "Ошибка: неверный тип аргумента. Ожидается Person.";
        }
        Person person = (Person) args[0];
        try {
            cm.addPerson(person);
            return "Элемент успешно добавлен: " + person;
        } catch (IllegalArgumentException e) {
            return "Ошибка при добавлении элемента: " + e.getMessage();
        }
    }

    /**
     * Возвращает краткое описание команды для справки.
     *
     * @return описание формата и назначения команды
     */
    @Override
    public String getDescription() {
        return "add {element}: добавить новый элемент в коллекцию";
    }
}
