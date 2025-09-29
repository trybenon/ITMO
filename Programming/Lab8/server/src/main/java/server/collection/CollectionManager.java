package server.collection;

import server.dataBase.DataBaseManager;
import server.logger.DbColorFormatter;
import shared.model.Info;
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
    private static LinkedList<Person> people = new LinkedList<>();
    private static final java.time.LocalDateTime creationDate = java.time.LocalDateTime.now();
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final ReentrantLock lock = new ReentrantLock();
    private final DataBaseManager dbManager;

    public CollectionManager() {
        this.dbManager = new DataBaseManager();
        installLogger();
    }

    public void loadCollection(String login) {
        lock.lock();
        try {
            people.clear();
            people = dbManager.loadCache(login, people);

            logger.info("Коллекция загружена для пользователя: " + login);
        } finally {
            lock.unlock();
        }
    }

    public String add(Person person, String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            boolean alreadyExists = people.stream()
                    .anyMatch(p -> p.equals(person));
            if (alreadyExists) {
                return "AlreadyExists";
            }
            // Добавление в базу данных
            Long id = dbManager.addPerson(person, user_login);
            if (id != null) {
                person.setId(id);
                person.setUser_login(user_login);
                people.add(person);

                return "SuccessAdd";
            } else {
                return "DBerror";
            }
        } finally {
            lock.unlock();
        }
    }

    public String updateId(long id, Person newPerson, String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            boolean updated = dbManager.updatePerson(id, newPerson, user_login);
            if (updated) {
                boolean success = people.removeIf(p -> p.getId() == id && p.getUserLogin().equals(user_login));
                if (success) {
                    newPerson.setId(id);
                    newPerson.setUser_login(user_login);
                    people.add(newPerson);

                    return "PersonDataUpdated";
                } else {
                    return "NoSuchPerson";
                }
            } else {
                return "DBerror";
            }
        } finally {
            lock.unlock();
        }
    }

    public String removeById(long id, String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            boolean removed = dbManager.removePerson(id, user_login);
            if (removed) {
                boolean success = people.removeIf(p -> p.getId() == id && p.getUserLogin().equals(user_login));
                if (success) {
                    return "Deleted";
                } else {
                    return "NoSuchPerson";
                }
            } else {
                return "DBerror";
            }
        } finally {
            lock.unlock();
        }
    }

    public Info info(String user_login) {
        lock.lock();
        try{
            loadCollection(user_login);
            return new Info( people.size(), "LinkedList<Person>", creationDate, people.stream().filter(d -> d.getUserLogin().equals((user_login))).count());
        } finally {
            lock.unlock();
        }
    }

    public String clear(String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            boolean cleared = dbManager.clear(user_login);
            if (cleared) {
                int initialSize = people.size();
                people.removeIf(p -> p.getUserLogin().equals(user_login));
                if (initialSize > people.size()) {
                    return "SuccessClear";
                } else {
                    return "NoOneToClear";
                }
            } else {
                return "DBerror";
            }
        } finally {
            lock.unlock();
        }
    }

    public String addIfMax(Person person, String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            boolean alreadyExists = people.stream()
                    .anyMatch(p -> p.equals(person));
            if (alreadyExists) {
                return "AlreadyExists";
            }
            boolean isMax = people.isEmpty() ||
                    people.stream().allMatch(p -> person.getHeight() > p.getHeight());
            if (isMax) {
                Long id = dbManager.addPerson(person, user_login);
                if (id != null) {
                    person.setId(id);
                    person.setUser_login(user_login);
                    people.add(person);

                    return "SuccessAdd";
                } else {
                    return "DBerror";
                }
            } else {
                return "NotMaximal";
            }
        } finally {
            lock.unlock();
        }
    }

    public String removeHead(String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            Person head = people.stream()
                    .filter(p -> p.getUserLogin().equals(user_login))
                    .findFirst()
                    .orElse(null);
            if (head == null) {
                return "NoOneToRemove";
            }
            boolean removed = dbManager.removePerson(head.getId(), user_login);
            if (removed) {
                people.remove(head);
                return "Deleted";
            } else {
                return "DBerror";
            }
        } finally {
            lock.unlock();
        }
    }

    public String show(){
        lock.lock();
        try {
            LinkedList<Person> showList = dbManager.loadCache(null, new LinkedList<>());
            if (showList.isEmpty()) {
                return "CollectionEmpty";
            }
            return showList.stream()
                    .sorted(Comparator.comparing(Person::getName))
                    .map(Person::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.unlock();
        }
    }

    public String infoString() {
        lock.lock();
        try {
            return String.format("Тип коллекции: %s\nДата инициализации: %s\nКоличество элементов: %d",
                    people.getClass().getName(), localDateTime, people.size());
        } finally {
            lock.unlock();
        }
    }

    public String averageOfHeight(String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            if (people.isEmpty()) {
                return "EmptyCollection";
            }
            double average = people.stream()
                    .mapToDouble(Person::getHeight)
                    .average()
                    .orElse(0.0);
            return String.valueOf(average);
        } finally {
            lock.unlock();
        }
    }

    public String printAscending() {
        lock.lock();
        try {
            if (people.isEmpty()) {
                return "EmptyCollection";
            }
            return people.stream()
                    .sorted(Comparator.comparing(Person::getHeight))
                    .map(Person::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.unlock();
        }
    }

    public String printFieldAscendingHeight(String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            if (people.isEmpty()) {
                return "EmptyCollection";
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

    public String head(String user_login) {
        lock.lock();
        try {
            loadCollection(user_login);
            return people.stream()
                    .findFirst()
                    .map(Person::toString)
                    .orElse("EmptyCollection");
        } finally {
            lock.unlock();
        }
    }

    public LinkedList<Person> getPeople() {
        lock.lock();
        try {
            loadCollection(null);
            return new LinkedList<>(people);
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
                "add_if_max {element} : добавить элемент, если его рост выше максимального");
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