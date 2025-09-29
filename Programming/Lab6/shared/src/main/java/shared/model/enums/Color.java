package shared.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlEnum // Сообщаем JAXB, что это перечисление
@XmlType(name = "color") // Название в XML
public enum Color implements Serializable {
    RED,
    BLACK,
    BLUE,
    ORANGE,
    BROWN;
}
