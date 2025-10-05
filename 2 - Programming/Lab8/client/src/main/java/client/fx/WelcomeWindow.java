package client.fx;


import client.ClientApp;
import client.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Locale;

public class WelcomeWindow implements DisconnectListener{
    private SceneSwitchObserver listener;
    private Localizer localizer;
    private ConnectionErrorWindow connectWindow;
    private final HashMap<String, Locale> localeHashMap = new HashMap<>() {{
        put("Русский", new Locale("ru"));
        put("Српски", new Locale("sr", "RS")); // Сербский
        put("Svenska", new Locale("sv", "SE")); // Шведский
        put("English (India)", new Locale("en", "IN")); // Английский-Индия
    }};

    public WelcomeWindow() {
    }

    @FXML
    private Label welcomeText;

    @FXML
    private Button registrationBtn;

    @FXML
    private Button authorizationBtn;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    void initialize(){
        languageComboBox.setItems(FXCollections.observableArrayList(localeHashMap.keySet()));
        languageComboBox.setValue(ClientApp.getLanguage());
        languageComboBox.setOnAction(event -> {
            var newLanguage = languageComboBox.getValue();
            Locale locale = localeHashMap.get(newLanguage);
            localizer.setLocale(locale);
            MainApp.setLocalizer(this.localizer);
            changeLanguage();
            ClientApp.setLanguage(newLanguage);
        });
    }

    @FXML
    public void authorization() {
        if (listener != null){
            listener.onAuthRequest();
        }
    }

    @FXML
    public void registration(){
        if (listener != null){
            listener.onRegRequest();
        }
    }

    public void changeLanguage(){
        welcomeText.setText(localizer.getKeyString("Welcome"));
        authorizationBtn.setText(localizer.getKeyString("Authorization"));
        registrationBtn.setText(localizer.getKeyString("Registration"));
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

public void setConnectionError(ConnectionErrorWindow connectWindow) {
    this.connectWindow = connectWindow;
}
@Override
public void disconnect() {
    connectWindow.show();
}

@Override
public void connect(){
    Platform.runLater(() -> {
        connectWindow.close();
        DialogManager.inform("Reconnected", "", MainApp.getLocalizer());
    });
}
}