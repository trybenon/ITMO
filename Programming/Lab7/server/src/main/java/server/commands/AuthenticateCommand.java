package server.commands;


import server.collection.CollectionManager;
import server.dataBase.DataBaseManager;
import shared.commands.Command;
import shared.dto.Response;

public class AuthenticateCommand implements Command {

        private final DataBaseManager dbManager;
        private final CollectionManager cm;

        public AuthenticateCommand(CollectionManager cm) {
            this.cm = cm;
            this.dbManager = new DataBaseManager();
        }

    @Override
    public String execute(Object[] args) {
        if (args == null || args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof String)) {
            return "Ошибка: команда authenticate ожидает login и password.";
        }

        String login = (String) args[0];
        String hashedPassword = (String) args[1];

        // Проверяем в базе данных
        Response response = dbManager.authenticate(login, hashedPassword);

        if (response.isSuccess()) {
            cm.loadCollection(login);
            return "Приветствую " + login ;
        } else {
            return "Неверный логин или пароль!";
        }
    }

    @Override
    public String getDescription() {
        return "Авторизация пользователя";
    }
}