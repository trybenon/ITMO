package server.collection;

import server.fileManager.Parser;
import shared.model.Person;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс {@code CollectionManager} управляет коллекцией людей,
 * предоставляет методы для добавления, обновления, удаления и вывода элементов,
 * а также для работы с файлами.
 */
public class CollectionManager {
    private LinkedList<Person> people = new LinkedList<>();  // Коллекция людей
    private final LocalDateTime localDateTime = LocalDateTime.now();  // Время инициализации
    private String filename;

    /**
     * Выводит справку по доступным командам.
     */
    /**
     * Выводит справку по доступным командам.
     */
    public void help() {
        System.out.println(helpString());
    }

    /**
     * Возвращает справку по доступным командам.
     * @return многострочная строка с описанием команд
     */
    public String helpString() {
        return String.join("\n",
                "Доступные команды:",
                "help : вывести справку по доступным командам",
                "info : вывести информацию о коллекции",
                "show : вывести все элементы коллекции",
                "add {element} : добавить новый элемент",
                "update {id} {element} : обновить элемент по ID",
                "remove_by_id {id} : удалить элемент по ID",
                "clear : очистить коллекцию",
                "head : вывести первый элемент коллекции",
                "remove_head : удалить первый элемент коллекции",
                "add_if_max {element} : добавить элемент, если его рост выше максимального",
                "average_of_height : вывести средний рост всех элементов",
                "print_ascending : вывести элементы в порядке возрастания по height",
                "print_field_ascending_height : вывести значения height в порядке возрастания",
                "execute_script : выполнить скрипт из файла",
                "exit : завершить работу клиента"
        );
    }

    /**
     * Выводит информацию о коллекции.
     */
    public void info() {
        System.out.println(infoString());
    }

    /**
     * Возвращает информацию о текущей коллекции.
     * @return строка с детальной информацией
     */
    public String infoString() {
        return String.format("Информация о коллекции:%nТип коллекции: %s%nДата инициализации: %s%nКоличество элементов: %d",
                people.getClass().getName(), localDateTime, people.size());
    }

    /**
     * Выводит все элементы коллекции.
     */
    public void show() {
        if (people.isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            people.stream()
                    .forEach(System.out::println);
        }
    }

    /**
     * Добавляет новый элемент в коллекцию.
     * @param person новый объект Person
     */
    public void addPerson(Person person) {
        people.add(person);
    }

    /**
     * Обновляет элемент по ID.
     * @param id идентификатор элемента
     * @param newPerson новые данные
     * @return true, если обновление прошло успешно
     */
    public boolean updateId(long id, Person newPerson) {
        Optional<Person> opt = people.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        if (opt.isPresent()) {
            Person p = opt.get();
            p.setName(newPerson.getName());
            p.setCoordinates(newPerson.getCoordinates());
            p.setEyeColor(newPerson.getEyeColor());
            p.setLocation(newPerson.getLocation());
            p.setPassportID(newPerson.getPassportID());
            p.setHeight(newPerson.getHeight());
            p.setWeight(newPerson.getWeight());
            return true;
        }
        return false;
    }

    public boolean existId(Long id){
        for(Person person : people){
            if (person.getId() == id){
                return true;
            }
        }
        return false;
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        people.clear();
    }

    /**
     * Завершает работу приложения.
     */
    public void exit() {
        System.out.println("Работа завершена, до связи");
        System.exit(0);
    }

    /**
     * Добавляет элемент, если его рост больше текущего максимума.
     * @param newPerson объект для возможного добавления
     * @return true, если элемент добавлен
     */
    public boolean addIfMax(Person newPerson) {
        Optional<Person> maxOpt = people.stream()
                .max(Comparator.comparing(Person::getHeight));
        if (maxOpt.isEmpty() || newPerson.getHeight() > maxOpt.get().getHeight()) {
            people.add(newPerson);
            return true;
        }
        return false;
    }

    /**
     * Выводит элементы в порядке возрастания по полю height.
     * @return многострочная строка элементов
     */
    public String printAscending() {
        return people.stream()
                .sorted(Comparator.comparing(Person::getHeight))
                .map(Person::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Вычисляет средний рост всех элементов.
     * @return строка со значением среднего или сообщение об ошибке
     */
    public String averageOfHeight() {
        var averageOpt = people.stream()
                .mapToDouble(Person::getHeight)
                .average();
        return averageOpt.isPresent()
                ? String.valueOf(averageOpt.getAsDouble())
                : "Коллекция пуста.";
    }

    /**
     * Возвращает первый элемент коллекции.
     * @return строковое представление элемента или сообщение об ошибке
     */
    public String head() {
        return people.stream()
                .findFirst()
                .map(Person::toString)
                .orElse("Коллекция пуста.");
    }

    /**
     * Удаляет первый элемент коллекции.
     * @return сообщение о результате операции
     */
    public String removeHead() {
        return people.stream()
                .findFirst()
                .map(p -> {
                    people.removeFirst();
                    return "Элемент удален.";
                })
                .orElse("Коллекция пуста.");
    }

    /**
     * Удаляет элемент по ID.
     * @param id идентификатор
     * @return true, если элемент удален
     */
    public boolean removeById(long id) {
        return people.removeIf(p -> p.getId() == id);
    }

    /**
     * Сохраняет коллекцию в файл.
     */
    public void save() {
        new Parser(this.filename).saveToXml(people);
    }

    /**
     * Выводит значения height всех элементов в порядке возрастания.
     * @return многострочная строка значений или сообщение об ошибке
     */
    public String printFieldAscendingHeight() {
        if (people.isEmpty()) {
            return "Ошибка: коллекция пуста.";
        }
        return people.stream()
                .sorted(Comparator.comparing(Person::getHeight))
                .map(Person::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * @return коллекция людей
     */
    public LinkedList<Person> getPeople() {
        return people;
    }

    /**
     * Устанавливает новую коллекцию.
     * @param people новая коллекция
     */
    public void setCollection(LinkedList<Person> people) {
        this.people = people;
    }

    /**
     * Устанавливает имя файла для сохранения/загрузки.
     * @param filename имя файла
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
