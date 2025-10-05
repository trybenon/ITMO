package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс, представляющий местоположение.
 * Описывает координаты x, y и z (где z не может быть null).
 */
@XmlRootElement(name = "location")
public class Location {
    private double x;
    private float y;
    private Integer z; // Не может быть null

    /**
     * **Конструктор без аргументов (обязателен для JAXB).**
     */
    public Location() {
        // JAXB требует этот пустой конструктор
    }

    /**
     * Конструктор для создания объекта Location.
     *
     * @param x координата X
     * @param y координата Y
     * @param z координата Z (не может быть null)
     * @throws IllegalArgumentException если z равен null
     */
    public Location(double x, float y, Integer z) {
        if (z == null) {
            throw new IllegalArgumentException("Ошибка: координата z не может быть null.");
        }
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @XmlElement(name = "x")
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @XmlElement(name = "y")
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @XmlElement(name = "z") // Z должно быть явно указано, иначе JAXB не сохранит его
    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        if (z == null) {
            throw new IllegalArgumentException("Ошибка: координата z не может быть null.");
        }
        this.z = z;
    }

    /**
     * Возвращает строковое представление объекта Location.
     *
     * @return строковое представление координат
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
