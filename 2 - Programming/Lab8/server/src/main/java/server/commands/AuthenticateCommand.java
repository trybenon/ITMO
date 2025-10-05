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
    public Response execute(Object[] args) {

        String login = (String) args[0];
        String hashedPassword = (String) args[1];

        Response response = dbManager.authenticate(login, hashedPassword);

        return response;
    }

    @Override
    public String getDescription() {
        return "Авторизация пользователя";
    }
}