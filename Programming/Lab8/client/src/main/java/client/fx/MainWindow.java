package client.fx;


import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import client.ClientApp;
import client.RequestBuilder;
import client.ScriptResponse;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import shared.commands.Command;
import shared.dto.CommandType;
import shared.dto.Request;
import shared.dto.Response;
import shared.dto.ResponseStatus;
import shared.model.Person;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainWindow extends Window implements DisconnectListener, RefreshCollectionListener {
    private SceneSwitchObserver listener;
    private static AddPersonWindow addPersonWindow;
    private ConnectionErrorWindow connectionErrorWindow;
    private boolean isFiltered = false;
    private Person clickedPerson;
    private HashMap<String, Color> colors = new HashMap<>();
    private Random random = new Random();
    private GraphicsContext gc;
    private List<PersonHitBox> personHitBoxes = new ArrayList<>();
    private Map<Person, PersonAnimationState> animationStates = new HashMap<>();
    private AnimationTimer animationTimer;


    private RequestBuilder builder = new RequestBuilder();

    private Localizer localizer;
    private final HashMap<String, Locale> localeHashMap = new HashMap<>() {{
        put("Русский", new Locale("ru"));
        put("Српски", new Locale("sr", "RS")); // Сербский
        put("Svenska", new Locale("sv", "SE")); // Шведский
        put("English (India)", new Locale("en", "IN")); // Английский-Индия
    }};

    @FXML
    private Label userTxt;
    @FXML
    private Button helpBtn;
    @FXML
    private Button printAscendingBtn;
    @FXML
    private Button averageOfHeightBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private Button headBtn;
    @FXML
    private Button infoBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button addIfMaxBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button executeScriptBtn;
    @FXML
    private Button removeByIdBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, Long> idColumn;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, Long> xCoordinateColumn;
    @FXML
    private TableColumn<Person, Double> yCoordinateColumn;
    @FXML
    private TableColumn<Person, Integer> heightColumn;
    @FXML
    private TableColumn<Person, Long> weightColumn;
    @FXML
    private TableColumn<Person, String> passportIDColumn;
    @FXML
    private TableColumn<Person, String> eyeColorColumn;
    @FXML
    private TableColumn<Person, Double> locationXColumn;
    @FXML
    private TableColumn<Person, Float> locationYColumn;
    @FXML
    private TableColumn<Person, Integer> locationZColumn;
    @FXML
    private TableColumn<Person, String> userLoginColumn;
    @FXML
    private Canvas personCanvas;
    @FXML
    private Pane personBase;
    @FXML
    private Label reconnectionText;
    @FXML
    private ProgressIndicator reconnectionBar;
    @FXML
    private Button removeFilterBtn;
    @FXML
    private ImageView image;

    @FXML
    void initialize() {
        this.localizer = MainApp.getLocalizer();

        gc = personCanvas.getGraphicsContext2D();
        Platform.runLater(() -> setCollection(ClientApp.getPersons()));
        changeLanguage();
        fillTable();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!personTable.getItems().isEmpty()) {
                    drawVisual(new ArrayList<>(personTable.getItems()));
                }
            }
        };
        animationTimer.start();


        userTxt.setText(ClientApp.getUser().getLogin());
        languageComboBox.setItems(FXCollections.observableArrayList(localeHashMap.keySet()));
        languageComboBox.setValue(ClientApp.getLanguage() != null ? ClientApp.getLanguage() : "Русский");
        languageComboBox.setOnAction(event -> {
            var newLanguage = languageComboBox.getValue();
            Locale locale = localeHashMap.get(newLanguage);
            localizer.setLocale(locale);
            MainApp.setLocalizer(this.localizer);
            changeLanguage();
            ClientApp.setLanguage(newLanguage);
        });
        image.setImage(new Image(getClass().getResourceAsStream("/user.png")));

        personCanvas.widthProperty().bind(personBase.widthProperty());
        personCanvas.heightProperty().bind(personBase.heightProperty());
        personCanvas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                for (PersonHitBox hitBox : personHitBoxes) {
                    if (hitBox.contains(mouseX, mouseY)) {
                        Person selectedPerson = hitBox.getPerson();
                        personClick(selectedPerson);
                        break;
                    }
                }
            }
        });
    }


    @FXML
    public void exit() {
        System.exit(0);
    }

    @FXML
    public void head() {
        Request request = builder.build("head");
        ClientApp.setRequest(request);
        try {
            int attempts = 0;
            Response response = null;
            while (attempts < 20) {
                response = ClientApp.getResponse();
                if (response != null && response.getType().equals(CommandType.HEAD)) break;
                Thread.sleep(100);
                attempts++;
            }
            if (response == null) {
                DialogManager.alert("TimeoutError", localizer);
                return;
            }
            if (response.getStatus().equals(ResponseStatus.OK)) {
                if (response.getPersons() != null) {
                    isFiltered = true;
                    LinkedList<Person> persons = new LinkedList<>();
                    persons.addAll(response.getPersons());
                    setCollection(persons);
                } else {
                    DialogManager.inform("Info", localizer.getKeyString(response.getMessage()), localizer);
                }
            } else {
                DialogManager.alert("Error", localizer);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void add() {
        Request request = builder.build("add");
        ClientApp.setRequest(request);

        try {
            int attempts = 0;
            Response response = null;
            while (attempts < 20) {
                response = ClientApp.getResponse();
                if (response != null && response.getType().equals(CommandType.ADD)) break;
                Thread.sleep(100);
                attempts++;
            }
            if (response == null) {
                DialogManager.alert("TimeoutError", localizer);
                return;
            }
            if (response.getStatus().equals(ResponseStatus.REFRESH)) {
                DialogManager.inform("Info", localizer.getKeyString(response.getMessage()), localizer);
            } else {
                DialogManager.alert("Error", localizer);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void addIfMax() {
        Request request = builder.build("add_if_max");
        ClientApp.setRequest(request);
        try {
            int attempts = 0;
            Response response = null;
            while (attempts < 20) {
                response = ClientApp.getResponse();
                if (response != null && response.getType().equals(CommandType.ADD_IF_MAX)) break;
                Thread.sleep(100);
                attempts++;
            }
            if (response == null) {
                DialogManager.alert("TimeoutError", localizer);
                return;
            }
            if (response.getStatus().equals(ResponseStatus.REFRESH)) {
                DialogManager.inform("Info", localizer.getKeyString(response.getMessage()), localizer);
            } else {
                DialogManager.alert("Error", localizer);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void update() {
        Long id;
        Person person;
        if (clickedPerson == null) {
            id = DialogManager.getId(localizer);
        } else {
            id = clickedPerson.getId();
            clickedPerson = null;
        }

        if (id != null) {
            person = ClientApp.getPersons().stream()
                    .filter(p -> p.getId() == id && p.getUserLogin().equals(ClientApp.getUser().getLogin()))
                    .findAny()
                    .orElse(null);
            if (person == null) {
                DialogManager.alert("NoSuchPerson", localizer);
                return;
            } else {
                addPersonWindow.fill(person);
            }
            Request req = builder.build("update" + " " + id);
            ClientApp.setRequest(req);
            try {
                int attempts = 0;
                Response response = null;
                while (attempts < 20) {
                    response = ClientApp.getResponse();
                    if (response != null && response.getType().equals(CommandType.UPDATE)) break;
                    Thread.sleep(100);
                    attempts++;
                }
                if (response == null) {
                    DialogManager.alert("TimeoutError", localizer);
                    return;
                }
                if (response.getStatus().equals(ResponseStatus.REFRESH)) {
                    DialogManager.inform("Info", localizer.getKeyString(response.getMessage()), localizer);
                } else {
                    DialogManager.alert("Error", localizer);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @FXML
    public void executeScript() {
        File file;
        file = DialogManager.getScript(localizer);
        ConcurrentLinkedQueue<Response> responseQueue = new ConcurrentLinkedQueue<>();
        if (file != null){
        builder.runScript(file.getPath(), request -> ClientApp.setRequest(request));
            try {
                int attempts = 0;
                Response response = null;
                while (attempts < 50) {
                    response = ClientApp.getResponse();
                    if (response != null) {
                        responseQueue.offer(response);
                            ScriptResponse.read(responseQueue, localizer);
                    }
                    Thread.sleep(100);
                    attempts++;
                }
                if (!responseQueue.isEmpty()) {
                    responseQueue.clear();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void removeById() {
        Long id;
        Person person;
        if (clickedPerson == null) {
            id = DialogManager.getId(localizer);
        } else {
            id = clickedPerson.getId();
            clickedPerson = null;
        }

        if (id != null) {
            person = ClientApp.getPersons().stream()
                    .filter(p -> p.getId() == id && p.getUserLogin().equals(ClientApp.getUser().getLogin()))
                    .findAny()
                    .orElse(null);
            if (person == null) {
                DialogManager.alert("NoSuchPerson", localizer);
                return;
            }
            Request req = builder.build("remove_by_id" + " " + id);
            ClientApp.setRequest(req);
            try {
                int attempts = 0;
                Response response = null;
                while (attempts < 100) {
                    response = ClientApp.getResponse();
                    if (response != null && response.getType().equals(CommandType.REMOVE_BY_ID)) break;
                    Thread.sleep(100);
                    attempts++;
                }
                if (response == null) {
                    DialogManager.alert("TimeoutError", localizer);
                    return;
                }
                if (response.getStatus().equals(ResponseStatus.REFRESH)) {
                    DialogManager.inform("Info", localizer.getKeyString(response.getMessage()), localizer);
                } else {
                    DialogManager.alert("Error", localizer);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @FXML
    public void removeFilter() {
        setCollection(ClientApp.getPersons());
        isFiltered = false;
    }

    @FXML
    public void info() {
        try {
            Request req = builder.build("info");
            ClientApp.setRequest(req);
            Thread.sleep(1000);
            Response response = ClientApp.getResponse();
            String message = (
                    localizer.getKeyString("InfoReturn") +
                            "Тип коллекции:  " + response.getInfo().getType() +"\n"+
                    "Размер общей коллекции:  " + response.getInfo().getNumberOfPersons() +"\n"+
                    "Размер вашей коллекции:  " +response.getInfo().getYourPersons() + "\n"+
                    "Дата последнего изменения коллекции:  " + localizer.getDate(response.getInfo().getDateOfInit()));
            DialogManager.inform("Info", message, localizer);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void help() {
        Request req = builder.build("help");
        ClientApp.setRequest(req);
        try {
            int attempts = 0;
            Response response = null;
            while (attempts < 20) {
                response = ClientApp.getResponse();
                if (response != null && response.getType().equals(CommandType.HELP)) {
                    break;
                }
                Thread.sleep(100);
                attempts++;
            }
            if (response == null) {
                DialogManager.alert("TimeoutError", localizer);
                return;
            }
            if (response.getStatus().equals(ResponseStatus.OK) && response.getType().equals(CommandType.HELP)) {
                DialogManager.help("HelpBtn", response.getCommandCollection().stream()
                        .map(element -> localizer.getKeyString(element)).collect(Collectors.joining("\n")), localizer);

            } else {
                DialogManager.alert("Error", localizer);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void averageOfHeight()  {
        Request request = builder.build("average_of_height");
        ClientApp.setRequest(request);
        try {
            int attempts = 0;
        Response response = null;
        while (attempts < 20) {
            response = ClientApp.getResponse();
            if (response != null && response.getType().equals(CommandType.AVERAGE_OF_HEIGHT)) {
                break;
            }
            Thread.sleep(100);
            attempts++;

        }
        if (response == null) {
            DialogManager.alert("TimeoutError", localizer);
            return;
        }
        if (response.getStatus().equals(ResponseStatus.OK) && response.getType().equals(CommandType.AVERAGE_OF_HEIGHT)) {
            DialogManager.inform("Info", response.getMessage(), localizer);
        }else {
            DialogManager.alert("Error", localizer);
            }
    }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void printAscending()  {
        Request request = builder.build("print_field_ascending_height");
        ClientApp.setRequest(request);
        try {
            int attempts = 0;
            Response response = null;
            while (attempts < 20) {
                response = ClientApp.getResponse();
                if (response != null && response.getType().equals(CommandType.PRINT_FIELD_ASCENDING_HEIGHT)) {
                    break;
                }
                Thread.sleep(100);
                attempts++;

            }
            if (response == null) {
                DialogManager.alert("TimeoutError", localizer);
                return;
            }
            if (response.getStatus().equals(ResponseStatus.OK) && response.getType().equals(CommandType.PRINT_FIELD_ASCENDING_HEIGHT)) {
                DialogManager.inform("Info", response.getMessage(), localizer);
            }else {
                DialogManager.alert("Error", localizer);
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }




    @FXML
    public void clear() {
        Request req = builder.build("clear");
        ClientApp.setRequest(req);
        try {
            int attempts = 0;
            Response response = null;
            while (attempts < 20) {
                response = ClientApp.getResponse();
                if (response != null && response.getType().equals(CommandType.CLEAR)) break;
                Thread.sleep(100);
                attempts++;
            }
            if (response == null) {
                DialogManager.alert("TimeoutError", localizer);
                return;
            }
            if (response.getStatus().equals(ResponseStatus.REFRESH)) {
                DialogManager.inform("Info", localizer.getKeyString(response.getMessage()), localizer);
            } else {
                DialogManager.alert("Error", localizer);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private double normalize(double value, double scale) {
        return (2.0 / Math.PI) * Math.atan(value / scale);
    }

    private void drawVisual(List<Person> lPersons) {
        gc.clearRect(0, 0, personCanvas.getWidth(), personCanvas.getHeight());

        double canvasWidth = personCanvas.getWidth() - 100;
        double canvasHeight = personCanvas.getHeight() - 100;

        PersonVisual.setCanvasHeight(canvasHeight);
        PersonVisual.setCanvasWidth(canvasWidth);

        double scale = 20;

        // Очищаем старые HitBox'ы
        personHitBoxes.clear();

        for (Person person : lPersons) {
            if (!animationStates.containsKey(person)) {
                animationStates.put(person, new PersonAnimationState(-50));
            }
        }
        animationStates.keySet().retainAll(lPersons);

        for (Person person : lPersons) {
            PersonAnimationState state = animationStates.get(person);
            state.update();

            String owner = person.getUserLogin();
            if (!colors.containsKey(owner)) {
                double hue = random.nextDouble() * 360;
                double saturation = 0.7 + random.nextDouble() * (1 - 0.7);
                double brightness = 0.8 + random.nextDouble() * (1 - 0.8);
                colors.put(owner, Color.hsb(hue, saturation, brightness));
            }

            var size = Math.min(Math.max(30, person.getWeight() * 2), 200);
            double radius = size / 6.0;

            double xNorm = normalize(person.getCoordinates().getX(), scale);
            double yNorm = normalize(person.getCoordinates().getY(), scale);

            // Корректировка координат для центра человечка
            double x = xNorm * canvasWidth - 20 + radius; // Смещение на radius для соответствия центру
            double y = canvasHeight - (yNorm * canvasHeight) + 60 + state.getY(); // Учитываем анимацию

            // Ограничиваем координаты в пределах канваса
            x = Math.max(radius, Math.min(canvasWidth - radius, x));
            y = Math.max(radius, Math.min(canvasHeight - radius, y));

            // Добавляем HitBox с центром в точке рисования
            personHitBoxes.add(new PersonHitBox(person, x, y, radius));
            PersonVisual.draw(gc, size, x, y, colors.get(owner)); // Рисуем с правильным центром
        }
    }


    public void setCollection(LinkedList<Person> ps) {
        List<Person> sortedPersons = new ArrayList<>(ps);
        sortedPersons.sort(Comparator.comparingLong(Person::getId));
        personTable.setItems(FXCollections.observableArrayList(sortedPersons));
        personTable.getSortOrder().clear();
        personTable.getSortOrder().add(idColumn);
        personTable.sort();
        drawVisual(sortedPersons);
    }

    private void changeLanguage() {
        fillTable();
        printAscendingBtn.setText(localizer.getKeyString("PrintAscendingBtn"));
        averageOfHeightBtn.setText(localizer.getKeyString("AverageOfHeightBtn"));
        helpBtn.setText(localizer.getKeyString("HelpBtn"));
        exitBtn.setText(localizer.getKeyString("ExitBtn"));
        headBtn.setText(localizer.getKeyString("HeadBtn"));
        infoBtn.setText(localizer.getKeyString("InfoBtn"));
        addBtn.setText(localizer.getKeyString("AddBtn"));
        addIfMaxBtn.setText(localizer.getKeyString("AddIfMaxBtn"));
        updateBtn.setText(localizer.getKeyString("UpdateBtn"));
        removeByIdBtn.setText(localizer.getKeyString("RemoveByIdBtn"));
        clearBtn.setText(localizer.getKeyString("Clear"));
        idColumn.setText(localizer.getKeyString("Id"));
        nameColumn.setText(localizer.getKeyString("Name"));
        xCoordinateColumn.setText(localizer.getKeyString("xCoordinate"));
        yCoordinateColumn.setText(localizer.getKeyString("yCoordinate"));
        heightColumn.setText(localizer.getKeyString("Height"));
        weightColumn.setText(localizer.getKeyString("Weight"));
        passportIDColumn.setText(localizer.getKeyString("PassportID"));
        eyeColorColumn.setText(localizer.getKeyString("EyeColor"));
        locationXColumn.setText(localizer.getKeyString("LocationX"));
        locationYColumn.setText(localizer.getKeyString("LocationY"));
        locationZColumn.setText(localizer.getKeyString("LocationZ"));
        userLoginColumn.setText(localizer.getKeyString("UserLogin"));
        reconnectionText.setText(localizer.getKeyString("Reconnection"));
        removeFilterBtn.setText(localizer.getKeyString("Unfilter"));
        executeScriptBtn.setText(localizer.getKeyString("ExecuteScriptBtn"));
    }

    private void fillTable() {
        idColumn.setCellValueFactory(person -> new SimpleLongProperty(person.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(person -> new SimpleStringProperty(person.getValue().getName()));
        xCoordinateColumn.setCellValueFactory(person -> new SimpleLongProperty(person.getValue().getCoordinates().getX()).asObject());
        yCoordinateColumn.setCellValueFactory(person -> new SimpleDoubleProperty(person.getValue().getCoordinates().getY()).asObject());
        heightColumn.setCellValueFactory(person -> new SimpleIntegerProperty(person.getValue().getHeight()).asObject());
        weightColumn.setCellValueFactory(person -> new SimpleLongProperty(person.getValue().getWeight()).asObject());
        passportIDColumn.setCellValueFactory(person -> new SimpleStringProperty(person.getValue().getPassportID()));
        eyeColorColumn.setCellValueFactory(person -> new SimpleStringProperty(localizer.getKeyString(person.getValue().getEyeColor().toString())));
        locationXColumn.setCellValueFactory(person -> new SimpleDoubleProperty(person.getValue().getLocation().getX()).asObject());
        locationYColumn.setCellValueFactory(person -> new SimpleFloatProperty(person.getValue().getLocation().getY()).asObject());
        locationZColumn.setCellValueFactory(person -> new SimpleIntegerProperty(person.getValue().getLocation().getZ()).asObject());
        userLoginColumn.setCellValueFactory(person -> new SimpleStringProperty(person.getValue().getUserLogin()));
        personTable.getSortOrder().add(idColumn);
        idColumn.setComparator(Long::compareTo);
        personTable.setRowFactory(tv -> {
            var row = new TableRow<Person>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    clickedPerson = row.getItem();
                }
            });
            return row;
        });

        weightColumn.setCellFactory(column -> new TableCell<Person, Long>() {
            @Override
            protected void updateItem(Long weight, boolean empty) {
                super.updateItem(weight, empty);
                setText(empty || weight == null ? "" : weight.toString());
            }
        });

        passportIDColumn.setCellFactory(column -> new TableCell<Person, String>() {
            @Override
            protected void updateItem(String passportID, boolean empty) {
                super.updateItem(passportID, empty);
                setText(empty || passportID == null ? "" : passportID.toString());
            }
        });
    }

    private void personClick(Person person) {
        clickedPerson = person;
        LinkedList<Person> persons = new LinkedList<>();
        persons.add(person);
        setCollection(persons);
    }

    public void setConnectionError(ConnectionErrorWindow connectionError) {
        this.connectionErrorWindow = connectionError;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public void setListener(SceneSwitchObserver listener) {
        this.listener = listener;
    }

    public Localizer getLocalizer() {
        return localizer;
    }

    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
    }

    public void setAddPersonWindow(AddPersonWindow addPersonWindow) {
        this.addPersonWindow = addPersonWindow;
    }

    @Override
    public void disconnect() {
        reconnectionBar.setVisible(true);
        reconnectionText.setVisible(true);
        connectionErrorWindow.show();
    }

    @Override
    public void connect() {
        Platform.runLater(() -> {
            connectionErrorWindow.close();
            reconnectionBar.setVisible(false);
            reconnectionText.setVisible(false);
        });
    }

    @Override
    public void refresh() {
        Platform.runLater(() -> {
            if (!isFiltered) {
                setCollection(ClientApp.getPersons());
            }
        });
    }


    public static AddPersonWindow getAddPersonWindow() {
        return addPersonWindow;

    }
}