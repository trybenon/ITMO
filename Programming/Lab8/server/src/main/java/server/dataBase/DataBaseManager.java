package server.dataBase;

import server.collection.CollectionManager;
import server.logger.DbColorFormatter;
import shared.dto.CommandType;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.dto.User;
import shared.model.Coordinates;
import shared.model.Location;
import shared.model.Person;
import shared.model.enums.Color;

import java.io.IOException;

import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class DataBaseManager {
    private static final Logger logger = Logger.getLogger(DataBaseManager.class.getName());
    private final QueryManager queryManager = new QueryManager();
    private final PasswordManager passwordManager = new PasswordManager();
    private final ReentrantLock lock = new ReentrantLock();

    public static Connection connect() {
        installLogger();
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://localhost:25432/studs", FileReader.getUser(), FileReader.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            logger.warning("Ошибка при подключении к базе данных: " + e.getMessage());
            return null;
        }
    }

    public LinkedList<Person> loadCache(String user_login, LinkedList<Person> collection) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    logger.warning("Не удалось подключиться к базе данных для загрузки кэша");
                    return collection;
                }
                String query = user_login == null ? queryManager.selectAllObjects : queryManager.selectAllObjects + " WHERE user_login = ?";
                try (PreparedStatement selectAll = connection.prepareStatement(query)) {
                    if (user_login != null) {
                        selectAll.setString(1, user_login);
                    }
                    try (ResultSet resultSet = selectAll.executeQuery()) {
                        while (resultSet.next()) {
                            Person person = new Person();
                            person.setId(resultSet.getLong("id"));
                            person.setName(resultSet.getString("name"));
                            person.setHeight(resultSet.getInt("height"));
                            person.setWeight(resultSet.getLong("weight"));
                            person.setEyeColor(resultSet.getString("eyeColor") != null ? Color.valueOf(resultSet.getString("eyeColor")) : null);
                            person.setPassportID(resultSet.getString("passportId"));
                            person.setUser_login(resultSet.getString("user_login"));
                            Coordinates coordinates = new Coordinates();
                            coordinates.setX(resultSet.getLong("coordX"));
                            coordinates.setY(resultSet.getDouble("coordY"));
                            person.setCoordinates(coordinates);
                            Location location = new Location();
                            location.setX(resultSet.getDouble("locX"));
                            location.setY(resultSet.getFloat("locY"));
                            location.setZ(resultSet.getInt("locZ"));
                            person.setLocation(location);
                            collection.add(person);
                        }
                        logger.info("Загружено " + collection.size() + " объектов из базы данных" + (user_login != null ? " для пользователя " + user_login : ""));
                        return collection;
                    }
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при загрузке кэша: " + e.getMessage());
                return collection;
            }
        } finally {
            lock.unlock();
        }
    }

    public Long addPerson(Person person, String login) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    return null;
                }
                try (PreparedStatement add = connection.prepareStatement(queryManager.addPerson)) {
                    add.setString(1, person.getName());
                    add.setInt(2, person.getHeight());
                    add.setLong(3, person.getWeight());
                    add.setString(4, person.getEyeColor() != null ? person.getEyeColor().name() : null);
                    add.setLong(5, person.getCoordinates().getX());
                    add.setDouble(6, person.getCoordinates().getY());
                    add.setDouble(7, person.getLocation().getX());
                    add.setFloat(8, person.getLocation().getY());
                    add.setInt(9, person.getLocation().getZ());
                    add.setString(10, person.getPassportID());
                    add.setString(11, login);
                    try (ResultSet resultSet = add.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getLong("id");
                        }
                    }
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при добавлении объекта: " + e.getMessage());
                return null;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean updatePerson(long id, Person newPerson, String login) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    return false;
                }
                try (PreparedStatement checkOwner = connection.prepareStatement(queryManager.selectObject)) {
                    checkOwner.setString(1, login);
                    checkOwner.setLong(2, id);
                    try (ResultSet resultSet = checkOwner.executeQuery()) {
                        if (!resultSet.next()) {
                            return false;
                        }
                    }
                }
                try (PreparedStatement update = connection.prepareStatement(queryManager.updateObject)) {
                    update.setString(1, newPerson.getName());
                    update.setInt(2, newPerson.getHeight());
                    update.setLong(3, newPerson.getWeight());
                    update.setString(4, newPerson.getEyeColor() != null ? newPerson.getEyeColor().name() : null);
                    update.setLong(5, newPerson.getCoordinates().getX());
                    update.setDouble(6, newPerson.getCoordinates().getY());
                    update.setDouble(7, newPerson.getLocation().getX());
                    update.setFloat(8, newPerson.getLocation().getY());
                    update.setInt(9, newPerson.getLocation().getZ());
                    update.setString(10, newPerson.getPassportID());
                    update.setString(11, login);
                    update.setLong(12, id);
                    int updatedRows = update.executeUpdate();
                    return updatedRows > 0;
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при обновлении объекта: " + e.getMessage());
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean removePerson(long id, String login) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    return false;
                }
                try (PreparedStatement remove = connection.prepareStatement(queryManager.deleteObject)) {
                    remove.setString(1, login);
                    remove.setLong(2, id);
                    int deletedRows = remove.executeUpdate();
                    return deletedRows > 0;
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при удалении объекта: " + e.getMessage());
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean clear(String login) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    return false;
                }
                try (PreparedStatement clear = connection.prepareStatement(queryManager.clearCollection)) {
                    clear.setString(1, login);
                    int deletedRows = clear.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при очистке коллекции: " + e.getMessage());
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    private static void installLogger() {
        try {

            for (Handler handler : logger.getHandlers()) {
                if (handler instanceof FileHandler) {
                    return;
                }
            }
            FileHandler fileHandler = new FileHandler("data.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Настройка ConsoleHandler для цветного вывода
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new DbColorFormatter());
            logger.addHandler(consoleHandler);

            // Установка уровня логирования
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false); // Отключаем дефолтный консольный обработчик
        } catch (IOException e) {
            logger.severe("Ошибка настройки логирования базы данных: " + e.getMessage());
        }
    }

    public Response registration(User user) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    return new Response(ResponseStatus.ERROR, "DBerror", CommandType.REGISTRATION);
                }
                try (PreparedStatement findUser = connection.prepareStatement(queryManager.findUser)) {
                    findUser.setString(1, user.getLogin());
                    try (ResultSet resultSet = findUser.executeQuery()) {
                        if (!resultSet.next()) {
                            try (PreparedStatement addUser = connection.prepareStatement(queryManager.addUser)) {
                                addUser.setString(1, user.getLogin());
                                addUser.setString(2, passwordManager.hashPassword(user.getPassword()));
                                addUser.execute();
                                return new Response(ResponseStatus.OK, "SuccessRegister", CommandType.REGISTRATION, user);
                            }
                        } else {
                            return new Response(ResponseStatus.ERROR, "UserExists", CommandType.REGISTRATION);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при регистрации: " + e.getMessage());
                return new Response(ResponseStatus.ERROR, "DBerror", CommandType.REGISTRATION);
            }
        } finally {
            lock.unlock();
        }
    }

    public Response authenticate(String login, String password) {
        lock.lock();
        try {
            try (Connection connection = connect()) {
                if (connection == null) {
                    return new Response(ResponseStatus.ERROR, "DBerror", CommandType.AUTHENTICATE);
                }
                try (PreparedStatement findUser = connection.prepareStatement(queryManager.findUser)) {
                    findUser.setString(1, login);
                    try (ResultSet resultSet = findUser.executeQuery()) {
                        if (resultSet.next()) {
                            String storedHash = resultSet.getString("hash");
                            String inputHash = passwordManager.hashPassword(password);
                            if (storedHash != null && storedHash.equals(inputHash)) {
                                LinkedList<Person> people = new LinkedList<>();
                                loadCache(null, people);
                                return new Response(ResponseStatus.REFRESH, "SuccessAuth", CommandType.AUTHENTICATE, new User(login, password), people);
                            } else {
                                return new Response(ResponseStatus.ERROR, "WrongPassword", CommandType.AUTHENTICATE);
                            }
                        } else {
                            return new Response(ResponseStatus.ERROR, "UserNotFound", CommandType.AUTHENTICATE);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.warning("Ошибка при авторизации: " + e.getMessage());
                return new Response(ResponseStatus.ERROR, "DBerror", CommandType.AUTHENTICATE);
            }
        } finally {
            lock.unlock();
        }
    }


}