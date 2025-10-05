package client.fx;


import client.ClientApp;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shared.model.Coordinates;
import shared.model.Location;
import shared.model.Person;
import shared.model.enums.Color;

import java.util.ArrayList;
import java.util.HashMap;

public  class AddPersonWindow {

    private Runnable callback;
    private Localizer localizer = MainApp.getLocalizer();
    private Color eyeColor;
    private Person person;
    private Stage stage;

    private final HashMap<String, Color> eyeColorHashMap = new HashMap<>();

    @FXML
    private Label titleTxt;

    @FXML
    private Label nameTxt;

    @FXML
    private Label xTxt;

    @FXML
    private Label yTxt;

    @FXML
    private Label heightTxt;

    @FXML
    private Label weightTxt;

    @FXML
    private Label passportIDTxt;

    @FXML
    private Label eyeColorTxt;

    @FXML
    private Label locationXTxt;

    @FXML
    private Label locationYTxt;

    @FXML
    private Label locationZTxt;

    @FXML
    private TextField nameField;

    @FXML
    private TextField xField;

    @FXML
    private TextField yField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField passportIDField;

    @FXML
    private ComboBox<String> eyeColorComboBox;

    @FXML
    private TextField locationXField;

    @FXML
    private TextField locationYField;

    @FXML
    private TextField locationZField;

    @FXML
    private Button confirmBtn;

    @FXML
    public  Person confirm() {
        var errors = new ArrayList<String>();

        // Validate name
        String name = nameField.getText().trim();
        System.out.println(name);
        if (name.isEmpty() || name.isBlank()) {
            errors.add(localizer.getKeyString("Name") + " " + localizer.getKeyString("NameError"));
        }

        // Validate coordinates.x
        Long x = null;
        try {
            if (xField.getText().isEmpty() || xField.getText().isBlank()) {
                errors.add(localizer.getKeyString("xCoordinate") + " " + localizer.getKeyString("XError"));
            } else {
                x = Long.parseLong(xField.getText().trim());
                if (x > 59) {
                    errors.add(localizer.getKeyString("xCoordinate") + " " + localizer.getKeyString("XErrorMax59"));
                }
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("xCoordinate") + " " + localizer.getKeyString("XError"));
        }

        // Validate coordinates.y
        Double y = null;
        try {
            if (yField.getText().isEmpty() || yField.getText().isBlank()) {
                errors.add(localizer.getKeyString("yCoordinate") + " " + localizer.getKeyString("YError"));
            } else {
                y = Double.parseDouble(yField.getText().trim());
                if (y > 426) {
                    errors.add(localizer.getKeyString("yCoordinate") + " " + localizer.getKeyString("YErrorMax426"));
                }
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("yCoordinate") + " " + localizer.getKeyString("YError"));
        }

        // Validate height
        Integer height = null;
        try {
            if (heightField.getText().isEmpty() || heightField.getText().isBlank()) {
                errors.add(localizer.getKeyString("Height") + " " + localizer.getKeyString("HeightError"));
            } else {
                height = Integer.parseInt(heightField.getText().trim());
                if (height <= 0) {
                    errors.add(localizer.getKeyString("Height") + " " + localizer.getKeyString("HeightErrorPositive"));
                }
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("Height") + " " + localizer.getKeyString("HeightError"));
        }

        // Validate weight
        Long weight = null;
        try {
            if (weightField.getText().isEmpty() || weightField.getText().isBlank()) {
                errors.add(localizer.getKeyString("Weight") + " " + localizer.getKeyString("WeightError"));
            } else {
                weight = Long.parseLong(weightField.getText().trim());
                if (weight <= 0) {
                    errors.add(localizer.getKeyString("Weight") + " " + localizer.getKeyString("WeightErrorPositive"));
                }
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("Weight") + " " + localizer.getKeyString("WeightError"));
        }

        // Validate passportID
        String passportID = passportIDField.getText().trim();
        if (passportID.isEmpty() || passportID.isBlank()) {
            passportID = null;
        }

        // Validate eyeColor
        if (eyeColor == null) {
            errors.add(localizer.getKeyString("EyeColor") + " " + localizer.getKeyString("EyeColorError"));
        }

        // Validate location.x
        Double locationX = null;
        try {
            if (locationXField.getText().isEmpty() || locationXField.getText().isBlank()) {
                errors.add(localizer.getKeyString("LocationX") + " " + localizer.getKeyString("LocationXError"));
            } else {
                locationX = Double.parseDouble(locationXField.getText().trim());
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("LocationX") + " " + localizer.getKeyString("LocationXError"));
        }

        // Validate location.y
        Float locationY = null;
        try {
            if (locationYField.getText().isEmpty() || locationYField.getText().isBlank()) {
                errors.add(localizer.getKeyString("LocationY") + " " + localizer.getKeyString("LocationYError"));
            } else {
                locationY = Float.parseFloat(locationYField.getText().trim());
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("LocationY") + " " + localizer.getKeyString("LocationYError"));
        }

        // Validate location.z
        Integer locationZ = null;
        try {
            if (locationZField.getText().isEmpty() || locationZField.getText().isBlank()) {
                errors.add(localizer.getKeyString("LocationZ") + " " + localizer.getKeyString("LocationZError"));
            } else {
                locationZ = Integer.parseInt(locationZField.getText().trim());
            }
        } catch (NumberFormatException e) {
            errors.add(localizer.getKeyString("LocationZ") + " " + localizer.getKeyString("LocationZError"));
        }

        if (errors.isEmpty()) {
            person = new Person(
                    name,
                    new Coordinates(x, y),
                    height,
                    weight,
                    passportID,
                    eyeColor,
                    new Location(locationX, locationY, locationZ),
                    ClientApp.user.getLogin()
            );

            stage.close();
            if (callback != null) {
                callback.run();
            }
        } else {
            DialogManager.alert(errors, localizer);
        }
        return person;
    }

    @FXML
    void initialize() {
        eyeColorHashMap.put(localizer.getKeyString("Red"), Color.RED);
        eyeColorHashMap.put(localizer.getKeyString("Black"), Color.BLACK);
        eyeColorHashMap.put(localizer.getKeyString("Blue"), Color.BLUE);
        eyeColorHashMap.put(localizer.getKeyString("Orange"), Color.ORANGE);
        eyeColorHashMap.put(localizer.getKeyString("Brown"), Color.BROWN);

        eyeColorComboBox.setItems(FXCollections.observableArrayList(eyeColorHashMap.keySet()));
        eyeColorComboBox.setValue(localizer.getKeyString("None"));
        eyeColorComboBox.setOnAction(event -> {
            eyeColor = eyeColorHashMap.get(eyeColorComboBox.getValue());
        });

        changeLanguage();
    }

    public void changeLanguage() {
        confirmBtn.setText(localizer.getKeyString("Confirm"));

        eyeColorHashMap.clear();
        eyeColorHashMap.put(localizer.getKeyString("Red"), Color.RED);
        eyeColorHashMap.put(localizer.getKeyString("Black"), Color.BLACK);
        eyeColorHashMap.put(localizer.getKeyString("Blue"), Color.BLUE);
        eyeColorHashMap.put(localizer.getKeyString("Orange"), Color.ORANGE);
        eyeColorHashMap.put(localizer.getKeyString("Brown"), Color.BROWN);

        eyeColorComboBox.setItems(FXCollections.observableArrayList(eyeColorHashMap.keySet()));

        nameTxt.setText(localizer.getKeyString("Name"));
        xTxt.setText(localizer.getKeyString("xCoordinate"));
        yTxt.setText(localizer.getKeyString("yCoordinate"));
        heightTxt.setText(localizer.getKeyString("Height"));
        weightTxt.setText(localizer.getKeyString("Weight"));
        passportIDTxt.setText(localizer.getKeyString("PassportID"));
        eyeColorTxt.setText(localizer.getKeyString("EyeColor"));
        locationXTxt.setText(localizer.getKeyString("LocationX"));
        locationYTxt.setText(localizer.getKeyString("LocationY"));
        locationZTxt.setText(localizer.getKeyString("LocationZ"));
        titleTxt.setText(localizer.getKeyString("AddPerson"));
    }

    public void clearFields() {
        nameField.clear();
        xField.clear();
        yField.clear();
        heightField.clear();
        weightField.clear();
        passportIDField.clear();
        eyeColorComboBox.setValue(localizer.getKeyString("None"));
        locationXField.clear();
        locationYField.clear();
        locationZField.clear();
    }

    public void fill(Person person) {
        nameField.setText(person.getName());
        xField.setText(String.valueOf(person.getCoordinates().getX()));
        yField.setText(String.valueOf(person.getCoordinates().getY()));
        heightField.setText(String.valueOf(person.getHeight()));
        weightField.setText(String.valueOf(person.getWeight()));
        passportIDField.setText(person.getPassportID() != null ? person.getPassportID() : "");
        eyeColorComboBox.setValue(localizer.getKeyString(person.getEyeColor().toString()));
        this.eyeColor = person.getEyeColor();
        locationXField.setText(String.valueOf(person.getLocation().getX()));
        locationYField.setText(String.valueOf(person.getLocation().getY()));
        locationZField.setText(String.valueOf(person.getLocation().getZ()));
        titleTxt.setText(localizer.getKeyString("UpdatePerson"));
    }

    public Person getPerson() {
        return person;
    }

    public void show() {
        this.localizer = MainApp.getLocalizer();
        changeLanguage();
        if (!stage.isShowing()) {
            stage.showAndWait();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Localizer getLocalizer() {
        return localizer;
    }

    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
    }

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
}
