package commands;

import collection.CollectionManager;
import collection.IdGenerator;
import collection.Validator;
import model.Person;

/**
 * Команда `add`.
 * Добавляет новый элемент в коллекцию, если его ID уникален.
 */
public class AddCommand implements Command {
    private final CollectionManager cm;

    /**
     * Конструктор команды `add`.
     *
     * @param cm менеджер коллекции
     */
    public AddCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `add`.
     * Запрашивает у пользователя ввод нового объекта `Person` и добавляет его в коллекцию,
     * если его ID уникален.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            try {
                // Получаем новый объект Person через клиентский менеджер
                Person person = cm.getClientManager().getPerson();

                Validator validator = new Validator();
                person = validator.getValidatedElement(person);
                if (person == null){
                    System.out.println("Ошибка, данные не корректны.");
                    return;
                }

                // Проверяем, есть ли уже объект с таким же ID в коллекции
                boolean isDuplicate = false;
                for (Person p : cm.getPeople()) {
                    if (p.getId() == person.getId()) {
                        isDuplicate = true;
                        break;
                    }
                }

                // Добавляем объект, если ID уникален
                if (!isDuplicate) {
                    cm.addPerson(person);
                    System.out.println("Элемент успешно добавлен в коллекцию.");
                } else {
                    System.out.println("Ошибка: элемент с таким ID уже существует.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка ввода данных. Попробуйте еще раз.");
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
        return "add {element}: добавить новый элемент в коллекцию";
    }
}
