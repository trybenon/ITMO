package client.fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Класс для отображения окна ошибки соединения.
 * Показывает уведомление о потере связи и предлагает переподключиться или выйти.
 */
public class ConnectionErrorWindow {
    private Localizer localizer;
    private Stage stage;

    @FXML
    private Label txt;

    @FXML
    private Button reconnectBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Label error;

    @FXML
    void initialize() {

        this.localizer = MainApp.getLocalizer();
        changeLanguage();
    }

    /**
     * Завершает работу приложения.
     */
    @FXML
    public void exit() {
        System.exit(0);
    }

    /**
     * Закрывает окно для попытки переподключения.
     */
    @FXML
    public void reconnect() {
        stage.close();
    }

    /**
     * Обновляет текст элементов окна в соответствии с текущей локализацией.
     */
    private void changeLanguage() {
        txt.setText(localizer.getKeyString("NoConnection"));
        error.setText(localizer.getKeyString("Error"));
        exitBtn.setText(localizer.getKeyString("ExitBtn"));
        reconnectBtn.setText(localizer.getKeyString("Reconnect"));

    }

    /**
     * Устанавливает локализатор.
     * @param localizer Объект локализатора.
     */
    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
        changeLanguage();
    }

    /**
     * Отображает окно ошибки соединения.
     */
    public void show() {
        this.localizer = MainApp.getLocalizer();
        Platform.runLater(() -> {
            changeLanguage();
            if (!stage.isShowing()) {
                stage.showAndWait();
            }
        });
    }

    /**
     * Закрывает окно.
     */
    public void close() {
        Platform.runLater(() -> stage.close());
    }

    /**
     * Устанавливает сцену для окна.
     * @param stage Объект Stage для окна.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}