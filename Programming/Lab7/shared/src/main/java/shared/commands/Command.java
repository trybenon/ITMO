package shared.commands;

public interface Command {
    public String execute(Object[] args);

    public String getDescription();
}
