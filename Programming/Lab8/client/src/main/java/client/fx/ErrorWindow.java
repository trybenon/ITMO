package client.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Класс для отображения окна ошибок в приложении.
 * Используется для показа сообщений об ошибках с локализованными заголовками и текстом.
 */
public class ErrorWindow {
    private Stage stage;

    @FXML
    private Label error;

    @FXML
    private Label txt;

    @FXML
    private Button okBtn;

    @FXML
    public void initialize() {
        // Закрытие окна при нажатии на кнопку "OK"
        okBtn.setOnAction(event -> stage.close());
    }

    /**
     * Отображает модальное окно с ошибкой.
     * @param title Заголовок ошибки (локализованный ключ).
     * @param content Текст ошибки (локализованный).
     */
    public void alert(String title, String content) {
        error.setText(title);
        txt.setText(content);
        stage.showAndWait();
    }

    /**
     * Отображает немодальное окно с ошибкой (для скриптов).
     * @param title Заголовок ошибки (локализованный ключ).
     * @param content Текст ошибки (локализованный).
     */
    public void alertScr(String title, String content) {
        error.setText(title);
        txt.setText(content);
        stage.show();
    }

    /**
     * Устанавливает сцену для окна.
     * @param stage Объект Stage для окна.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}