package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс, представляющий координаты объекта Person.
 */
@XmlRootElement(name = "coordinates") // Аннотация JAXB для сериализации
public class Coordinates {
    private long x; // Максимальное значение: 59
    private double y; // Максимальное значение: 426

    /**
     * **Конструктор без аргументов (обязателен для JAXB).**
     */
    public Coordinates() {
        // JAXB требует этот пустой конструктор
    }

    /**
     * Основной конструктор.
     *
     * @param x координата X (максимум 59)
     * @param y координата Y (максимум 426)
     */
    public Coordinates(long x, double y) {
        setX(x);
        setY(y);
    }

    @XmlElement(name = "x")
    public long getX() {
        return x;
    }

    public void setX(long x) {
        if (x > 59) {
            throw new IllegalArgumentException("Ошибка: x не может быть больше 59.");
        }
        this.x = x;
    }

    @XmlElement(name = "y")
    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (y > 426) {
            throw new IllegalArgumentException("Ошибка: y не может быть больше 426.");
        }
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
