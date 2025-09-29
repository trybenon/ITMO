package shared.model;

import shared.model.enums.Color;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий человека.
 */
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Set<String> PassportIDs = new HashSet<>();

    private long id;
    private String name;
    private Coordinates coordinates;

    private int height;
    private long weight;
    private String passportID;
    private Color eyeColor;
    private Location location;
    private String user_login;


    /**
     * Основной конструктор.
     */
    public Person(String name, Coordinates coordinates, int height, long weight,
                  String passportID, Color eyeColor, Location location, String user_login) {
        this.id = System.currentTimeMillis(); // Генерация ID при создании объекта


        setName(name);
        setCoordinates(coordinates);
        setHeight(height);
        setWeight(weight);
        setPassportID(passportID);
        setEyeColor(eyeColor);
        setLocation(location);
        setUser_login(user_login);

    }
    public Person(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: имя не может быть пустым.");
        }
        this.name = name;
    }


    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Ошибка: координаты не могут быть null.");
        }
        this.coordinates = coordinates;
    }








    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Ошибка: рост должен быть больше 0.");
        }
        this.height = height;
    }


    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Ошибка: вес должен быть больше 0.");
        }
        this.weight = weight;
    }


    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }


    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Ошибка: местоположение не может быть null.");
        }
        this.location = location;
    }
    public String getUser_login(){
        return user_login;
    }
    public void setUser_login(String user_login){
        this.user_login = user_login;
    }

    @Override
    public String toString() {
        return "Person{id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", height=" + height +
                ", weight=" + weight +
                ", passportID='" + passportID + '\'' +
                ", eyeColor=" + eyeColor +
                ", location=" + location +
                ", owner=" + user_login + '}';
    }
}
