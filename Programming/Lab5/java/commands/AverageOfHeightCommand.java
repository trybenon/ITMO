package commands;

import collection.CollectionManager;

/**
 * Команда `average_of_height`.
 * Вычисляет и выводит среднее значение поля height в коллекции.
 */
public class AverageOfHeightCommand implements Command {
    private final CollectionManager cm;

    /**
     * Создает команду для вычисления среднего значения height.
     *
     * @param cm менеджер коллекции
     */
    public AverageOfHeightCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Выполняет команду `average_of_height`.
     * Вызывает метод {@link CollectionManager#averageOfHeight()}.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.averageOfHeight();
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
    }

    /**
     * Описание команды.
     *
     * @return описание команды
     */
    @Override
    public String getDescription() {
        return "average_of_height: вывести среднее значение height";
    }
}
