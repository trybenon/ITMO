package server.dataBase;

public class QueryManager {
    String findUser = "SELECT * FROM users WHERE login = ?;";
    String addUser = "INSERT INTO users(login, hash) VALUES (?, ?);";
    String addPerson = """
            INSERT INTO People(name, height, weight, eyeColor, coordX, coordY, locX, locY, locZ, passportId, user_login)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id;
            """;
    String clearCollection = "DELETE FROM People WHERE user_login = ? RETURNING id;";
    String deleteObject = "DELETE FROM People WHERE user_login = ? AND id = ? RETURNING id;";
    String removeHead = "DELETE FROM People WHERE user_login = ? AND id = (SELECT MIN(id) FROM People WHERE user_login = ?) RETURNING id;";
    String updateObject = """
            UPDATE People
            SET name = ?, height = ?, weight = ?, eyeColor = ?, coordX = ?, coordY = ?, locX = ?, locY = ?, locZ = ?, passportId = ?
            WHERE user_login = ? AND id = ?
            RETURNING id;
            """;
    String selectAllObjects = "SELECT * FROM People";
    String selectObject = "SELECT id, user_login FROM People WHERE user_login = ? AND id = ?;";
}