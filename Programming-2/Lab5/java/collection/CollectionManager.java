package collection;

import console.ClientManager;
import fileManager.Parser;
import model.Location;
import model.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * Класс {@code CollectionManager} управляет коллекцией людей, предоставляет методы
 * для добавления, обновления, удаления и вывода элементов, а также для работы с файлами.
 */
public class CollectionManager {
    private LinkedList<Person> people = new LinkedList<>();  // Коллекция людей
    private LocalDateTime localDateTime = LocalDateTime.now();  // Время инициализации коллекции
    private ClientManager clientManager = new ClientManager();  // Менеджер для получения данных о человеке
    private String filename;

    /**
     * Выводит справку по доступным командам.
     */
    public void help() {
        System.out.println("Доступные команды:");
        System.out.println("help - вывести справку по доступным командам");
        System.out.println("info - вывести информацию о коллекции");
        System.out.println("show - вывести все элементы коллекции");
        System.out.println("add {element} - добавить новый элемент в коллекцию");
        System.out.println("update {element} - обновить элемент коллекции по id");
        System.out.println("remove_by_id {id} - удалить элемент из коллекции по id");
        System.out.println("clear - очистить коллекцию");
        System.out.println("save - сохранить коллекцию в файл");
        System.out.println("execute_script {file_name} - выполнить скрипт из файла");
        System.out.println("exit - завершить программу");
        System.out.println("head - вывести первый элемент коллекции");
        System.out.println("remove_head - удалить первый элемент коллекции");
        System.out.println("add_if_max {element} - добавить новый элемент в коллекцию, если его значение больше максимального");
        System.out.println("average_of_height - вывести среднее значение поля height для всех элементов коллекции");
        System.out.println("print_ascending - вывести элементы коллекции в порядке возрастания");
        System.out.println("print_field_ascending_height - вывести значения поля height всех элементов коллекции в порядке возрастания");
    }

    /**
     * Выводит информацию о коллекции.
     */
    public void info() {
        System.out.println("Информация о коллекции:");
        System.out.println("Тип коллекции: " + people.getClass().getName());
        System.out.println("Дата инициализации: " + localDateTime);
        System.out.println("Количество элементов: " + people.size());
    }

    /**
     * Выводит все элементы коллекции.
     */
    public void show() {
        if (!people.isEmpty()) {
            for (Person person : people) {
                System.out.println(person);
            }
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Добавляет новый элемент в коллекцию.
     *
     * @param person Новый человек, добавляемый в коллекцию.
     */
    public void addPerson(Person person) {
        people.add(person);
    }

    /**
     * Обновляет элемент коллекции по id.
     *
     * @param id        Идентификатор человека, чьи данные нужно обновить.
     * @param newPerson Новый объект, данные которого должны быть установлены в коллекцию.
     */
    public void updateId(long id, Person newPerson) {
        for (Person person : people) {
            if (person.getId() == id) {
                person.setLocation(newPerson.getLocation());
                person.setName(newPerson.getName());
                person.setEyeColor(newPerson.getEyeColor());
                person.setCoordinates(newPerson.getCoordinates());
                person.setPassportID(newPerson.getPassportID());
                person.setHeight(newPerson.getHeight());
                person.setWeight(newPerson.getWeight());
                System.out.println("Данные успешно обновлены");
                break;
            }
        }
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        people.clear();
    }

    /**
     * Завершает работу программы.
     */
    public void exit() {
        System.out.println("Работа завершена, до связи");
        System.exit(0);
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение больше максимального элемента коллекции.
     *
     * @param newPerson Новый человек, который будет добавлен в коллекцию, если его значение больше максимального.
     */
    public void addIfMax(Person newPerson) {
        Person maxPerson = null;
        for (Person person : people) {
            if (maxPerson == null || person.getHeight() > maxPerson.getHeight()) {
                maxPerson = person;
            }
        }
        if (maxPerson == null || newPerson.getHeight() > maxPerson.getHeight()) {
            people.add(newPerson);
            System.out.println("Новый человек добавлен, так как его рост больше максимального.");
        } else {
            System.out.println("Новый человек не был добавлен, так как его рост не превышает максимальный.");
        }
    }

    /**
     * Выводит все элементы коллекции в порядке возрастания по полю height.
     */
    public void printAscending() {
        people.sort(new HeightComparator());
        for (Person person : people) {
            System.out.println(person);
        }
    }

    /**
     * Вычисляет и выводит среднее значение поля height для всех элементов коллекции.
     */
    public void averageOfHeight() {
        double averageHeight = 0;
        if (people.size() > 0) {
            for (Person person : people) {
                averageHeight += person.getHeight();
            }
            System.out.println(averageHeight / people.size());
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Выводит первый элемент коллекции.
     */
    public void head() {
        if (!people.isEmpty()) {
            System.out.println(people.get(0));
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Удаляет первый элемент коллекции.
     */
    public void removeHead() {
        if (!people.isEmpty()) {
            System.out.println(people.getFirst());
            people.removeFirst();
            System.out.println("Элемент удален.");
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Удаляет элемент коллекции по его ID.
     * Этот метод ищет элемент в коллекции людей с заданным ID и удаляет его,
     * если такой элемент найден. Если коллекция пуста или элемент не найден,
     * будет выведено соответствующее сообщение.
     *
     * @param id идентификатор элемента, который нужно удалить
     */
    public void removeById(int id) {
        if (!people.isEmpty()) {
            Person toRemove = null;
            // Проходим по всем элементам коллекции
            for (Person person : people) {
                if (person.getId() == id) {
                    toRemove = person;  // Найденный элемент сохраняем для удаления
                    break;  // Выход из цикла после нахождения элемента
                }
            }

            // Если элемент найден, удаляем его
            if (toRemove != null) {
                people.remove(toRemove);  // Удаляем найденный элемент
                System.out.println("Элемент успешно удален.");
            } else {
                System.out.println("Элемент с таким ID не найден.");
            }
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Сохраняет коллекцию людей в файл.
     */
    public void save() {
        Parser parser = new Parser(this.filename);
        parser.saveToXml(people);
    }

    /**
     * Выводит значения height всех элементов в порядке возрастания.
     */
    public void printFieldAscendingHeight() {
        if (people.isEmpty()) {
            System.out.println("Ошибка: коллекция пуста.");
            return;
        }

        // Сортируем коллекцию с помощью HeightComparator
        people.sort(new HeightComparator());

        // Выводим только height
        System.out.println("Значения height в порядке возрастания:");
        for (Person person : people) {
            System.out.println(person.getHeight());
        }
    }

    /**
     * Получает объект {@link ClientManager}, управляющий процессом получения данных о человеке.
     *
     * @return объект {@link ClientManager}
     */
    public ClientManager getClientManager() {
        return this.clientManager;
    }

    /**
     * Получает коллекцию людей.
     *
     * @return коллекция людей
     */
    public LinkedList<Person> getPeople() {
        return people;
    }

    /**
     * Устанавливает новую коллекцию людей.
     *
     * @param people новая коллекция людей
     */
    public void setCollection(LinkedList<Person> people) {
        this.people = people;
    }

    /**
     * Устанавливает имя файла для работы с коллекцией.
     *
     * @param filename имя файла
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
