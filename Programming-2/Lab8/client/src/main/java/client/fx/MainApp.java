package client.fx;


import client.ClientApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.fx.MainWindow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class MainApp extends Application implements SceneSwitchObserver{

    private static Localizer localizer;
    private static Stage index;
    private static ConnectionErrorWindow connectWindow;
    private final HashMap<String, Locale> localeHashMap = new HashMap<>() {{
        put("Русский", new Locale("ru"));
        put("Српски", new Locale("sr", "RS")); // Сербский
        put("Svenska", new Locale("sv", "SE")); // Шведский
        put("English (India)", new Locale("en", "IN")); // Английский-Индия
    }};

    @Override
    public void start(Stage stage) {
        localizer = new Localizer(ClientApp.getLanguage() != null ? new Locale(ClientApp.getLanguage()) : new Locale("ru"));
        index = stage;

        var connectLoader = new FXMLLoader(getClass().getResource("/markup/connectionError.fxml"));
        Parent connectionRoot = loadFxml(connectLoader);
        Scene connectionScene = new Scene(connectionRoot);
        Stage connectionStage = new Stage();
        connectionStage.setResizable(false);
        connectionStage.setScene(connectionScene);
        connectWindow = connectLoader.getController();
        connectWindow.setLocalizer(localizer);
        connectWindow.setStage(connectionStage);
        welcomeStage();
    }

    private void welcomeStage(){
        var welcomeLoader = new FXMLLoader(getClass().getResource("/markup/welcome.fxml"));
        Parent welcomeRoot = loadFxml(welcomeLoader);
        WelcomeWindow welcomeWindow = welcomeLoader.getController();

        welcomeWindow.setListener(this);
        welcomeWindow.setLocalizer(localizer);
        welcomeWindow.setConnectionError(connectWindow);

        index.setScene(new Scene(welcomeRoot));
        ClientApp.addDisconnectListener(welcomeWindow);
        index.setResizable(false);
        index.show();
    }

    private void authStage(){
        var authLoader = new FXMLLoader(getClass().getResource("/markup/auth.fxml"));
        Parent authRoot = loadFxml(authLoader);
        AuthWindow authWindow = authLoader.getController();
        authWindow.setCallback(this::mainStage);
        authWindow.setLocalizer(localizer);
        authWindow.setConnectionError(connectWindow);
        index.setScene(new Scene(authRoot));
        ClientApp.addDisconnectListener(authWindow);
    }

    private void regStage(){
        var regLoader = new FXMLLoader(getClass().getResource("/markup/reg.fxml"));
        Parent regRoot = loadFxml(regLoader);
        RegWindow regWindow = regLoader.getController();
        regWindow.setCallback(this::welcomeStage);
        regWindow.setLocalizer(localizer);
        regWindow.setConnectionError(connectWindow);
        index.setScene(new Scene(regRoot));
        index.setResizable(false);
        ClientApp.addDisconnectListener(regWindow);
    }

    private void mainStage(){
        var addLoader = new FXMLLoader(getClass().getResource("/markup/addPerson.fxml"));
        Parent addRoot = loadFxml(addLoader);
        Scene addScene = new Scene(addRoot);
        Stage addStage = new Stage();
        addStage.setResizable(true);
        addStage.setScene(addScene);
        AddPersonWindow addPersonWindow = addLoader.getController();
        addPersonWindow.setLocalizer(localizer);
        addPersonWindow.setStage(addStage);

        var mainLoader = new FXMLLoader(getClass().getResource("/markup/main.fxml"));
        Parent mainRoot = loadFxml(mainLoader);
        Scene mainScene = new Scene(mainRoot, 1310, 650);
        MainWindow mainWindow = mainLoader.getController();
        mainWindow.setLocalizer(localizer);
        mainWindow.setListener(this);
        mainWindow.setConnectionError(connectWindow);
        mainWindow.setAddPersonWindow(addPersonWindow);
        ClientApp.addRefreshListener(mainWindow);
        ClientApp.addDisconnectListener(mainWindow);

        index.setScene(mainScene);
        index.setResizable(true);
        index.show();
    }

    private Parent loadFxml(FXMLLoader loader){
        Parent parent = null;
        try{
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }

    @Override
    public void onAuthRequest() {
        authStage();
    }

    @Override
    public void onRegRequest() {
        regStage();
    }

    public static Localizer getLocalizer() {
        return localizer;
    }

    public static void setLocalizer(Localizer localizer) {
        MainApp.localizer = localizer;
    }
}