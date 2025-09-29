package fileManager;

import model.Person;
import model.Coordinates;
import model.Location;
import model.enums.Color;

import javax.xml.bind.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * Класс для парсинга данных о Person из/в XML.
 * Использует JAXB для десериализации и сериализации данных.
 */
public class Parser {

    private final String filename;

    /**
     * Конструктор класса для парсинга данных.
     *
     * @param filename имя XML файла, с которым нужно работать.
     */
    public Parser(String filename) {
        this.filename = filename;
    }

    /**
     * Сохраняет коллекцию Person в XML файл.
     *
     * @param people коллекция объектов Person, которые нужно сохранить.
     */
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

                // Исправленные координаты с отдельными тегами
                writer.write("    <coordinates>\n");
                writer.write("      <x>" + person.getCoordinates().getX() + "</x>\n");
                writer.write("      <y>" + person.getCoordinates().getY() + "</y>\n");
                writer.write("    </coordinates>\n");

                writer.write("    <creationDate>" + person.getCreationDate() + "</creationDate>\n");
                writer.write("    <height>" + person.getHeight() + "</height>\n");
                writer.write("    <weight>" + person.getWeight() + "</weight>\n");
                writer.write("    <passportID>" + person.getPassportID() + "</passportID>\n");
                writer.write("    <eyeColor>" + person.getEyeColor() + "</eyeColor>\n");

                // Локация остается без изменений
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


    /**
     * Загружает коллекцию Person из XML файла.
     *
     * @return LinkedList коллекции людей
     * @throws JAXBException если произошла ошибка при парсинге XML
     */
    public LinkedList<Person> loadFromXml() throws JAXBException {
        LinkedList<Person> people = new LinkedList<>();
        try {
            // Создаем контекст JAXB для десериализации
            JAXBContext context = JAXBContext.newInstance(Person.class, PersonListWrapper.class, Coordinates.class, Location.class, Color.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Загружаем данные из XML файла
            File file = new File(this.filename);
            if (!file.exists()) {
                System.out.println("Ошибка: файл не найден. Загружаем пустую коллекцию.");
                return people;
            }

            // Десериализация XML в объект PersonListWrapper
            PersonListWrapper wrapper = (PersonListWrapper) unmarshaller.unmarshal(file);

            // Проверяем, есть ли данные в файле
            if (wrapper.getPeople() != null) {
                people.addAll(wrapper.getPeople());
            } else {
                System.out.println("Предупреждение: XML-файл пуст или не содержит данных.");
            }
        } catch (JAXBException e) {
            System.out.println("Ошибка при загрузке данных из файла: " + e.getMessage());
            e.printStackTrace(); // Выводим полный стек ошибки
        }
        return people;
    }
}
