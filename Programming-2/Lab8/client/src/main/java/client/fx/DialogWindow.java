package client.fx;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

public class DialogWindow {

    private Long id;
    private String name;
    private Stage stage;
    private Localizer localizer;
    private String txt;
    private String todo;
    private File file;

    @FXML
    private Label mainTxt;

    @FXML
    private Label whatToDo;

    @FXML
    private TextField infoField;

    @FXML
    private Button okBtn;

    @FXML
    public void initialize(){
        okBtn.setOnMouseClicked(event -> stage.close());
    }

    public Long getId(){
        try{
            id = Long.parseLong(infoField.getText());
            return id;
        } catch (NumberFormatException e){
            DialogManager.alert("idError", localizer);
        }
        return null;
    }

    public String getNamePart(){
        try{
            name = infoField.getText();
            return name;
        } catch (Exception e){
            DialogManager.alert("nameError", localizer);
        }
        return null;
    }

    public File executeScript(){
        try{
            file = new File(infoField.getText());
        } catch (Exception e){
            DialogManager.alert("scriptError", localizer);
        }
        if (file.exists() && file.isFile()){
            return file;
        } else {
            DialogManager.alert("scriptError", localizer);
            return null;
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setLocalizer(Localizer localizer){
        this.localizer = localizer;
    }

    public void show() {
        mainTxt.setText(localizer.getKeyString(txt));
        whatToDo.setText(localizer.getKeyString(todo));
        stage.showAndWait();
    }

    public void clear() {
        id = null;
        name = null;
        txt = "";
        todo = "";
        mainTxt.setText("");
        whatToDo.setText("");
        infoField.clear();
    }

    public void setAction(String action){
        switch (action){
            case "update":
                txt = "EnterUpdId";
                todo = "EnterId";
                return;
            case "remove":
                txt = "EnterDelId";
                todo = "EnterId";
                return;
            case "executeScript":
                txt = "executeScript";
                todo = "executeScriptToDo";
                return;
            default:
                todo = "None";
                txt = "None";
        }
    }
}
