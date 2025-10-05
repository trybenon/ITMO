package client.console;

import shared.model.Coordinates;
import shared.model.Location;
import shared.model.Person;
import shared.model.enums.Color;
import client.ClientApp;

import java.util.Scanner;

/**
 * Класс {@code ClientManager} управляет процессом получения данных о человеке
 * с помощью {@link ReadManager}, создавая объект {@link Person}.
 */
public class ClientManager {
    private final ReadManager readManager = new ReadManager();

    /**
     * Получает информацию о человеке от пользователя и создает объект {@link Person}.
     *
     * @return новый объект {@link Person}, созданный с использованием введенных данных
     */
    public Person getPerson()
    {
        try {
            // Чтение данных

            String name = readManager.readName();
            Integer height = readManager.readHeight();
            long weight = readManager.readWeight();
            Color eyeColor = readManager.readEyeColor(); // Может быть null
            Long coordX = readManager.readCoordinateX(); // Может быть null
            Double coordY = readManager.readCoordinateY(); // Может быть null
            Double locX = readManager.readLocationX(); // Может быть null
            Float locY = readManager.readLocationY(); // Может быть null
            int locZ = readManager.readLocationZ(); // ОБЯЗАТЕЛЬНОЕ поле
            String passportId = readManager.readPassportId();
            String user_login = ClientApp.user.getLogin();// Может быть null


            // Гарантированное создание `Coordinates` (если null, подставляем 0)
            Coordinates coordinates = new Coordinates(
                    (coordX != null) ? coordX : 0L,
                    (coordY != null) ? coordY : 0.0
            );

            // Гарантированное создание `Location` (если null, подставляем 0.0)
            Location location = new Location(
                    (locX != null) ? locX : 0.0,
                    (locY != null) ? locY : 0.0f,
                    locZ // locZ обязателен
            );

            // Создание объекта Person
            return new Person(name, coordinates, height, weight, passportId, eyeColor, location, user_login);
        } catch (Exception e) {
            System.out.println("Ошибка ввода данных. Попробуйте еще раз.");
            return null;
        }
    }}
