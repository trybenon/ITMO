package server.commands;

import shared.commands.Command;
import server.collection.CollectionManager;

/**
 * Очищает всю коллекцию.
 */
public class ClearCommand implements Command {
    private final CollectionManager cm;

    /**
     * @param cm менеджер коллекции
     */
    public ClearCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Очищает коллекцию.
     *
     * @param args не должен содержать аргументов
     * @return сообщение о результате выполнения
     */
    @Override
    public String execute(Object[] args) {
        if (args != null && args.length > 0) {
            return "Ошибка: команда clear не принимает аргументы.";
        }
        cm.clear();
        return "Коллекция успешно очищена.";
    }

    /**
     * @return краткое описание команды
     */
    @Override
    public String getDescription() {
        return "clear: очистить коллекцию";
    }
}
