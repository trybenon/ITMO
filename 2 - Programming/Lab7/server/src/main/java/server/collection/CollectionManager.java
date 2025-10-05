package server.collection;

import server.commands.ShowCommand;
import server.dataBase.DataBaseManager;
import server.logger.DbColorFormatter;
import shared.model.Person;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private static final Logger logger = Logger.getLogger(CollectionManager.class.getName());
    private LinkedList<Person> people = new LinkedList<>();
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final ReentrantLock lock = new ReentrantLock();
    private final DataBaseManager dbManager;


    public CollectionManager() {
        this.dbManager = new DataBaseManager();
    }



    public void loadCollection(String login) {
        lock.lock();
        try {
            installLogger();
            people.clear();
            people = dbManager.loadCache(login, people);
            logger.info("Коллекция загружена для пользователя: " + login);
        } finally {
            lock.unlock();
        }
    }

    public void addPerson(Person person) {
        lock.lock();
        try {
            people.add(person);
        } finally {
            lock.unlock();
        }
    }

    public boolean updateId(long id, Person newPerson, String user_login) {
        lock.lock();
        try {
            for (int i = 0; i < people.size(); i++) {
                Person p = people.get(i);
                if (p.getId() == id && p.getUser_login().equals(user_login)) {
                    newPerson.setId(id);
                    newPerson.setUser_login(user_login);
                    people.set(i, newPerson);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean existId(Long id) {
        lock.lock();
        try {
            return people.stream().anyMatch(p -> p.getId() == id);
        } finally {
            lock.unlock();
        }
    }

    public void clear(String user_login) {
        lock.lock();
        try {
            people.removeIf(p -> p.getUser_login().equals(user_login));
        } finally {
            lock.unlock();
        }
    }

    public boolean addIfMax(Person newPerson) {
        lock.lock();
        try {
            var maxOpt = people.stream()
                    .max(Comparator.comparing(Person::getHeight));
            if (maxOpt.isEmpty() || newPerson.getHeight() > maxOpt.get().getHeight()) {
                people.add(newPerson);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public String printAscending() {
        lock.lock();
        try {
            return people.stream()
                    .sorted(Comparator.comparing(Person::getHeight))
                    .map(Person::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.unlock();
        }
    }

    public String averageOfHeight() {
        lock.lock();
        try {
            var averageOpt = people.stream()
                    .mapToDouble(Person::getHeight)
                    .average();
            return averageOpt.isPresent()
                    ? String.valueOf(averageOpt.getAsDouble())
                    : "Коллекция пуста.";
        } finally {
            lock.unlock();
        }
    }

    public String head() {
        lock.lock();
        try {
            return people.stream()
                    .findFirst()
                    .map(Person::toString)
                    .orElse("Коллекция пуста.");
        } finally {
            lock.unlock();
        }
    }

    public String removeHead() {
        lock.lock();
        try {
            return people.stream()
                    .findFirst()
                    .map(p -> {
                        people.removeFirst();
                        return "Элемент удалён.";
                    })
                    .orElse("Коллекция пуста.");
        } finally {
            lock.unlock();
        }
    }

    public boolean removeById(long id, String user_login) {
        lock.lock();
        try {
            return people.removeIf(p -> p.getId() == id && p.getUser_login().equals(user_login));
        } finally {
            lock.unlock();
        }
    }

    public String printFieldAscendingHeight() {
        lock.lock();
        try {
            if (people.isEmpty()) {
                return "Ошибка: коллекция пуста.";
            }
            return people.stream()
                    .map(Person::getHeight)
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.unlock();
        }
    }

    public LinkedList<Person> getPeople() {
        lock.lock();
        try {
            return people;
        } finally {
            lock.unlock();
        }
    }

    public String helpString() {
        return String.join("\n",
                "\n",
                "===========================================================",
                "                     Доступные команды",
                "===========================================================",
                "login : войти в в аккаунт",
                "logout: выйти из аккаунта",
                "registration : создать аккаунт",
                "help : вывести справку по доступным командам",
                "info : вывести информацию о коллекции",
                "===========================================================",
                "                       Общие команды",
                "         (без регистрации используют общую коллекцию)",
                "===========================================================",
                "show : вывести все элементы коллекции",
                "head : вывести первый элемент коллекции",
                "add_if_max {element} : добавить элемент, если его рост выше максимального",
                "average_of_height : вывести средний рост всех элементов",
                "print_ascending : вывести элементы в порядке возрастания по height",
                "print_field_ascending_height : вывести значения height в порядке возрастания",
                "execute_script : выполнить команды из скрипта",
                "===========================================================",
                "          Только для авторизованных пользователей",
                "          (используют только вашу личную коллекцию)",
                "===========================================================",
                "add {element} : добавить новый элемент",
                "update {id} {element} : обновить элемент по ID",
                "remove_by_id {id} : удалить элемент по ID",
                "clear : очистить коллекцию",
                "remove_head : удалить первый элемент коллекции",
                "execute_script : выполнить команды из скрипта",
                "add_if_max {element} : добавить элемент, если его рост выше максимального");}

    public String infoString() {
        lock.lock();
        try {
            return String.format("Информация о коллекции:%nТип коллекции: %s%nДата инициализации: %s%nКоличество элементов: %d",
                    people.getClass().getName(), localDateTime, people.size());
        } finally {
            lock.unlock();
        }
    }

    public String show() {
        lock.lock();

        try {
            LinkedList<Person> showList = new LinkedList<>();
            showList.clear();
            showList = dbManager.loadCache(null, showList);
            if (showList.isEmpty()) {
                return "Коллекция пуста.";
            }
            return showList.stream()
                    .sorted(Comparator.comparing(Person::getName))
                    .map(Person::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.unlock();
        }
    }
    private static void installLogger() {
        try {

            for (Handler handler : logger.getHandlers()) {
                if (handler instanceof FileHandler) {
                    return;
                }
            }
            FileHandler fileHandler = new FileHandler("data.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Настройка ConsoleHandler для цветного вывода
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new DbColorFormatter());
            logger.addHandler(consoleHandler);

            // Установка уровня логирования
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false); // Отключаем дефолтный консольный обработчик
        } catch (IOException e) {
            logger.severe("Ошибка настройки логирования базы данных: " + e.getMessage());
        }
    }
}