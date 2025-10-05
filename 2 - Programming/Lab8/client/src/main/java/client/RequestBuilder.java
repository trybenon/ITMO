package client;

import client.console.*;
import client.fx.AddPersonWindow;
import client.fx.MainApp;
import client.fx.MainWindow;
import shared.dto.Request;
import shared.dto.CommandType;
import shared.model.Person;
import shared.model.Coordinates;
import shared.model.Location;
import shared.model.enums.Color;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Строит Request из однострочных команд и выполняет скрипты execute_script,
 * где для команд add / add_if_max берутся 10 строк параметров Person,
 * а для update — 1 строка ID + 10 строк параметров.
 * Для интерактивного режима делегирует ввод Person в ClientManager.
 */
public class RequestBuilder {

    /** Скрипты в текущем стеке для защиты от рекурсии */
    private final Set<Path> runningScripts = new HashSet<>();
    /** Текущая папка скрипта, чтобы относительные пути внутри nested execute_script работали */
    private Path currentScriptDir = null;
    private final AddPersonWindow addPersonWindow = new AddPersonWindow();

    public RequestBuilder() {
    }

    /**
     * Строит Request по одной строке из консоли.
     * Поддерживает интерактивные команды add / add_if_max / update,
     * остальные — через buildSimple().
     */
    public  Request build(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return null;

        String[] parts = trimmed.split("\\s+", 3);
        String cmd = parts[0].toLowerCase();


        switch (cmd) {
            case "add" -> {
                MainWindow.getAddPersonWindow().clearFields();
                MainWindow.getAddPersonWindow().show();
                MainWindow.getAddPersonWindow().setLocalizer(MainApp.getLocalizer());
                MainWindow.getAddPersonWindow().changeLanguage();
                Person person = MainWindow.getAddPersonWindow().confirm();
                return new Request(CommandType.ADD, new Object[]{person, ClientApp.user.getLogin()});
            }
            case "add_if_max" -> {
                MainWindow.getAddPersonWindow().clearFields();
                MainWindow.getAddPersonWindow().show();
                MainWindow.getAddPersonWindow().setLocalizer(MainApp.getLocalizer());
                MainWindow.getAddPersonWindow().changeLanguage();
                Person person = MainWindow.getAddPersonWindow().confirm();
                return new Request(CommandType.ADD_IF_MAX, new Object[]{person, ClientApp.user.getLogin()});
            }
            case "login" -> {
                if (parts.length < 3) {
                    System.out.println(parts.length);
                    System.out.println("Нужно: login <login> <password>");
                    return null;
                }
                System.out.println(parts.length);
                String login = parts[1];
                String password = parts[2];
                return new Request(CommandType.AUTHENTICATE, new Object[]{login, password});
            }
            case "registration" -> {
                if (parts.length < 3) {
                    System.out.println("Нужно: registration <login> <password>");
                    return null;
                }
                String login = parts[1];
                String password = parts[2];
                return new Request(CommandType.REGISTRATION, new Object[]{login, password});
            }
            case "remove_by_id" -> {
                if (parts.length == 2) {
                    try {
                        long id = Long.parseLong(parts[1]);
                        return new Request(CommandType.REMOVE_BY_ID, new Object[]{id, ClientApp.user.getLogin()});
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: ID должен быть числом.");
                        return null;
                    }
                }
            }
            case "update" -> {
                MainWindow.getAddPersonWindow().show();
                MainWindow.getAddPersonWindow().setLocalizer(MainApp.getLocalizer());
                MainWindow.getAddPersonWindow().changeLanguage();
                Person person = MainWindow.getAddPersonWindow().confirm();
                try {
                    long id = Long.parseLong(parts[1]);
                    return new Request(CommandType.UPDATE, new Object[]{id, person, ClientApp.user.getLogin()});
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: ID должен быть числом.");
                    return null;
                }
            }
            case "clear" -> {
                return new Request(CommandType.CLEAR, new Object[]{ClientApp.user.getLogin()});
            }
            case "head" -> {
                return new Request(CommandType.HEAD, new Object[]{ClientApp.user.getLogin()});
            }
            case "remove_head" -> {
                return new Request(CommandType.REMOVE_HEAD, new Object[]{ClientApp.user.getLogin()});
            }
            default -> {
                return buildSimple(cmd);
            }
        }
    return buildSimple(cmd);
    }

    /**
     * Читает скрипт целиком, защищаясь от рекурсивных вызовов.
     * Команды add/add_if_max берут 10 строк, update — 1 строку ID + 10 строк,
     * остальные — однострочные через buildSimple().
     */
    public void runScript(String filename, Consumer<Request> sender) {
        Path scriptPath = Paths.get(filename);
        if (!scriptPath.isAbsolute() && currentScriptDir != null) {
            scriptPath = currentScriptDir.resolve(scriptPath);
        }
        scriptPath = scriptPath.toAbsolutePath().normalize();

        if (!Files.exists(scriptPath)) {
            System.out.println("Файл не найден: " + scriptPath);
            return;
        }
        if (runningScripts.contains(scriptPath)) {
            System.out.println("Ошибка: рекурсивный вызов скрипта " + scriptPath);
            return;
        }

        runningScripts.add(scriptPath);
        Path prevDir = currentScriptDir;
        currentScriptDir = scriptPath.getParent();
        try {
            List<String> lines = Files.readAllLines(scriptPath).stream()
                    .map(String::trim)
                    .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                    .collect(Collectors.toList());

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).toLowerCase();
                if (line.startsWith("execute_script")) {
                    String[] parts = lines.get(i).split("\\s+", 2);
                    if (parts.length == 2) {
                        runScript(parts[1], sender);
                    } else {
                        System.out.println("Нужно: execute_script <путь к файлу>");
                    }
                }
                else if (line.startsWith("remove_by_id")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length == 2) {
                        try {
                            long id = Long.parseLong(parts[1]);
                            sender.accept(new Request(CommandType.REMOVE_BY_ID, new Object[]{id, ClientApp.user.getLogin()}));
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка: ID должен быть числом.");
                        }
                    }
                }
                else if (line.equals("add") || line.equals("add_if_max")) {
                    boolean isMax = line.equals("add_if_max");
                    if (i + 10 >= lines.size()) {
                        System.out.println("Недостаточно строк для " + line);
                        break;
                    }
                    List<String> params = lines.subList(i + 1, i + 11);
                    Person p = parsePersonFromLines(params);
                    sender.accept(new Request(
                            isMax ? CommandType.ADD_IF_MAX : CommandType.ADD,
                            new Object[]{p, ClientApp.user.getLogin()}
                    ));
                    i += 10;
                }

                else if (line.startsWith("update")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length < 2) {
                        System.out.println("Нужно: update <id>");
                        break;
                    }
                    long id;
                    try {
                        id = Long.parseLong(parts[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: ID должен быть числом.");
                        break;
                    }

                    // Проверяем наличие 10 строк параметров после команды
                    if (i + 10 >= lines.size()) {
                        System.out.println("Недостаточно строк для параметров Person");
                        break;
                    }

                    List<String> params = lines.subList(i + 1, i + 11); // Берем следующие 10 строк
                    Person p = parsePersonFromLines(params);
                    sender.accept(new Request(
                            CommandType.UPDATE,
                            new Object[]{id, p, ClientApp.user.getLogin()}
                    ));
                    i += 10;
                }
                else if (line.equals("exit")) {
                    System.out.println("Завершение работы клиента.");
                    System.exit(0);
                }

                else {
                    Request req = buildSimple(line);
                    if (req != null) sender.accept(req);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения скрипта: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка разбора числа: " + e.getMessage());
        } finally {
            runningScripts.remove(scriptPath);
            currentScriptDir = prevDir;
        }
    }

    /**
     * Парсит Person из 10 строк:
     * [0]=name, [1]=coordX, [2]=coordY, [3]=height, [4]=weight,
     * [5]=eyeColor, [6]=locX, [7]=locY, [8]=locZ, [9]=passportID
     */
    private Person parsePersonFromLines(List<String> p) {
        if (p.size() < 10) {
            throw new IllegalArgumentException("Нужно 10 строк для Person, получили " + p.size());
        }
        String name = p.get(0);
        long coordX = Long.parseLong(p.get(1));
        double coordY = Double.parseDouble(p.get(2));
        int height = Integer.parseInt(p.get(3));
        long weight = Long.parseLong(p.get(4));
        Color eyeColor = Color.valueOf(p.get(5).toUpperCase());
        double locX = Double.parseDouble(p.get(6));
        float locY = Float.parseFloat(p.get(7));
        int locZ = Integer.parseInt(p.get(8));
        String passportID = p.get(9);
        return new Person(
                name,
                new Coordinates(coordX, coordY),
                height,
                weight,
                passportID,
                eyeColor,
                new Location(locX, locY, locZ),
                ClientApp.user.getLogin()
        );
    }

    /**
     * Обрабатывает однострочные команды: help, info, show, clear, head, remove_head,
     * average_of_height, print_ascending, print_field_ascending_height, remove_by_id.
     */
    private Request buildSimple(String line) {
        switch (line) {
            case "help"                       -> { return new Request(CommandType.HELP, new Object[0]); }
            case "info"                       -> { return new Request(CommandType.INFO, new Object[]{ClientApp.user.getLogin()}); }
            case "show"                       -> { return new Request(CommandType.SHOW, new Object[0]); }
            case "clear"                      -> { return new Request(CommandType.CLEAR,new Object[]{ClientApp.user.getLogin()}); }
            case "head"                       -> { return new Request(CommandType.HEAD, new Object[]{ClientApp.user.getLogin()}); }
            case "remove_head"                -> { return new Request(CommandType.REMOVE_HEAD, new Object[]{ClientApp.user.getLogin()}); }
            case "average_of_height"          -> { return new Request(CommandType.AVERAGE_OF_HEIGHT, new Object[]{ClientApp.user.getLogin()}); }
            case "print_ascending"            -> { return new Request(CommandType.PRINT_ASCENDING, new Object[]{ClientApp.user.getLogin()}); }
            case "print_field_ascending_height" -> { return new Request(CommandType.PRINT_FIELD_ASCENDING_HEIGHT, new Object[]{ClientApp.user.getLogin()}); }
            default -> {
                System.out.println("Неизвестная команда: " + line);
                System.out.print(ClientApp.PROMPT);
                return null;
            }
        }
    }
}
