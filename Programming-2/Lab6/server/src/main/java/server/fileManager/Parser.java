package server.fileManager;

import shared.model.Person;
import shared.model.Coordinates;
import shared.model.Location;
import shared.model.enums.Color;

import javax.xml.bind.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Parser {

    private final String filename;

    public Parser(String filename) {
        this.filename = filename;
    }

    public void saveToXml(LinkedList<Person> people) {
        if (this.filename == null || this.filename.isEmpty()) {
            System.out.println("Ошибка: путь к файлу не задан.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<people>\n");

            for (Person person : people) {
                writer.write("  <person>\n");
                writer.write("    <id>" + person.getId() + "</id>\n");
                writer.write("    <name>" + person.getName() + "</name>\n");
                writer.write("    <coordinates>\n");
                writer.write("      <x>" + person.getCoordinates().getX() + "</x>\n");
                writer.write("      <y>" + person.getCoordinates().getY() + "</y>\n");
                writer.write("    </coordinates>\n");
                writer.write("    <creationDate>" + person.getCreationDate() + "</creationDate>\n");
                writer.write("    <height>" + person.getHeight() + "</height>\n");
                writer.write("    <weight>" + person.getWeight() + "</weight>\n");
                writer.write("    <passportID>" + person.getPassportID() + "</passportID>\n");
                writer.write("    <eyeColor>" + person.getEyeColor() + "</eyeColor>\n");
                writer.write("    <location>\n");
                writer.write("      <x>" + person.getLocation().getX() + "</x>\n");
                writer.write("      <y>" + person.getLocation().getY() + "</y>\n");
                writer.write("      <z>" + person.getLocation().getZ() + "</z>\n");
                writer.write("    </location>\n");
                writer.write("  </person>\n");
            }
            writer.write("</people>\n");

            System.out.println("Коллекция успешно сохранена в файл: " + this.filename);

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных в файл: " + e.getMessage());
        }
    }

    public LinkedList<Person> loadFromXml() throws JAXBException {
        LinkedList<Person> people = new LinkedList<>();
        File file = new File(this.filename);

        if (!file.exists()) {
            System.out.println("Ошибка: файл не найден. Загружаем пустую коллекцию.");
            return people;
        }

        try {
            // Создаем контекст JAXB
            JAXBContext context = JAXBContext.newInstance(Person.class, PersonListWrapper.class, Coordinates.class, Location.class, Color.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Десериализация
            PersonListWrapper wrapper = (PersonListWrapper) unmarshaller.unmarshal(file);
            if (wrapper.getPeople() != null) {
                people.addAll(wrapper.getPeople());
            } else {
                System.out.println("Предупреждение: XML-файл пуст или не содержит данных.");
                return people;
            }

            // Ручная валидация данных
            for (int i = 0; i < people.size(); i++) {
                Person person = people.get(i);
                try {
                    validatePerson(person, i + 1);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка в записи Person #" + (i + 1) + ": " + e.getMessage());
                    people.remove(i); // Удаляем некорректный элемент
                    i--;
                }
            }

        } catch (JAXBException e) {
            System.out.println("Ошибка синтаксиса XML в файле " + filename + ": " + e.getMessage());
            if (e.getLinkedException() != null) {
                System.out.println("Подробности: " + e.getLinkedException().getMessage());
            }
            throw e; // Передаем исключение дальше, если нужна дополнительная обработка
        }

        return people;
    }

    private void validatePerson(Person person, int index) {
        // Проверка обязательных полей
        if (person.getId() <= 0) {
            throw new IllegalArgumentException("Поле <id> должно быть положительным числом.");
        }
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Поле <name> не может быть пустым.");
        }
        if (person.getCoordinates() == null) {
            throw new IllegalArgumentException("Тег <coordinates> отсутствует или пуст.");
        } else {
            if (person.getCoordinates().getX() > 59) {
                throw new IllegalArgumentException("Поле <coordinates><x> должно быть <= 59.");
            }
            if (person.getCoordinates().getY() > 426) {
                throw new IllegalArgumentException("Поле <coordinates><y> должно быть <= 426.");
            }
        }
        if (person.getCreationDate() == null) {
            throw new IllegalArgumentException("Поле <creationDate> отсутствует или некорректно.");
        }
        if (person.getHeight() <= 0) {
            throw new IllegalArgumentException("Поле <height> должно быть больше 0.");
        }
        if (person.getWeight() <= 0) {
            throw new IllegalArgumentException("Поле <weight> должно быть больше 0.");
        }
        if (person.getLocation() == null) {
            throw new IllegalArgumentException("Тег <location> отсутствует или пуст.");
        } else {
            if (person.getLocation().getZ() == null) {
                throw new IllegalArgumentException("Поле <location><z> не может быть null.");
            }
        }
        // Проверка необязательных полей
        if (person.getPassportID() != null && (person.getPassportID().length() < 6 || person.getPassportID().length() > 41)) {
            throw new IllegalArgumentException("Поле <passportID> должно быть от 6 до 41 символа.");
        }
        if (person.getEyeColor() != null) {
            try {
                Color.valueOf(person.getEyeColor().toString());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Поле <eyeColor> содержит некорректное значение: " + person.getEyeColor());
            }
        }
    }
}