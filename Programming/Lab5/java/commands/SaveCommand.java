package commands;

import collection.CollectionManager;

/**
 * Command `save`.
 * Команда для сохранения коллекции в файл.
 */
public class SaveCommand implements Command {
    private CollectionManager cm;

    public SaveCommand(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            cm.save();
            System.out.println("Коллекция успешно сохранена.");
        } else {
            throw new IllegalArgumentException("Неверное количество аргументов.");
        }
    }

    @Override
    public String getDescription() {
        return "save: сохранить коллекцию в файл";
    }
}
