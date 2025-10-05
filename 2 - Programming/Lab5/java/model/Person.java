package model;

import model.enums.Color;
import fileManager.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий человека.
 */
@XmlRootElement(name = "person")
public class Person implements Serializable {
    private static Set<String> PassportIDs = new HashSet<>();

    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private int height;
    private long weight;
    private String passportID;
    private Color eyeColor;
    private Location location;

    /**
     * **Конструктор без аргументов (обязателен для JAXB).**
     */
    public Person() {
        this.creationDate = LocalDateTime.now(); // Поле должно заполняться автоматически
    }

    /**
     * Основной конструктор.
     */
    public Person(String name, Coordinates coordinates, int height, long weight,
                  String passportID, Color eyeColor, Location location) {
        this.id = System.currentTimeMillis(); // Генерация ID при создании объекта
        this.creationDate = LocalDateTime.now();

        setName(name);
        setCoordinates(coordinates);
        setHeight(height);
        setWeight(weight);
        setPassportID(passportID);
        setEyeColor(eyeColor);
        setLocation(location);
    }

    @XmlElement(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: имя не может быть пустым.");
        }
        this.name = name;
    }

    @XmlElement(name = "coordinates")
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Ошибка: координаты не могут быть null.");
        }
        this.coordinates = coordinates;
    }

    @XmlElement(name = "creationDate")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getCreationDate() {
        return creationDate;
    }



    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) {
            this.creationDate = LocalDateTime.now();
        } else {
            this.creationDate = creationDate;
        }
    }

    @XmlElement(name = "height")
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Ошибка: рост должен быть больше 0.");
        }
        this.height = height;
    }

    @XmlElement(name = "weight")
    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Ошибка: вес должен быть больше 0.");
        }
        this.weight = weight;
    }

    @XmlElement(name = "passportID")
    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    @XmlElement(name = "eyeColor")
    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    @XmlElement(name = "location")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Ошибка: местоположение не может быть null.");
        }
        this.location = location;
    }

    @Override
    public String toString() {
        return "Person{id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", height=" + height +
                ", weight=" + weight +
                ", passportID='" + passportID + '\'' +
                ", eyeColor=" + eyeColor +
                ", location=" + location + '}';
    }
}
