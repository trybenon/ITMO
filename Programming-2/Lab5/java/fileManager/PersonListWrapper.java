package fileManager;

import model.Person;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

/**
 * Вспомогательный класс для обертки списка людей для JAXB.
 * Этот класс используется для сериализации и десериализации коллекции людей.
 */
@XmlRootElement(name = "people")
public class PersonListWrapper {
    private LinkedList<Person> people = new LinkedList<>(); // Инициализируем список

    @XmlElement(name = "person")  // Каждый объект хранится в теге <person>
    public LinkedList<Person> getPeople() {
        return people;
    }

    public void setPeople(LinkedList<Person> people) {
        if (people != null) {
            this.people = people;
        }
    }
}
