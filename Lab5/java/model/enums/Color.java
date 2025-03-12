package model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum // Сообщаем JAXB, что это перечисление
@XmlType(name = "color") // Название в XML
public enum Color {
    RED,
    BLACK,
    BLUE,
    ORANGE,
    BROWN;
}
