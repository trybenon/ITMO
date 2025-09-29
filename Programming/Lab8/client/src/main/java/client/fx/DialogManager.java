package client.fx;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DialogManager {

    public static void alert(String title, Localizer localizer){
        ErrorWindow errorWindow;
        FXMLLoader errorLoader = new FXMLLoader(DialogManager.class.getResource("/markup/error.fxml"));
        Parent errorRoot = loadFxml(errorLoader);
        Scene errorScene = new Scene(errorRoot);
        Stage errorStage = new Stage();
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.setScene(errorScene);
        errorStage.setResizable(false);
        errorWindow = errorLoader.getController();
        errorWindow.setStage(errorStage);
        errorWindow.alert(localizer.getKeyString("Error"), localizer.getKeyString(title));
    }

    public static void alert(ArrayList<String> errors, Localizer localizer){
        ErrorWindow errorWindow;
        FXMLLoader errorLoader = new FXMLLoader(DialogManager.class.getResource("/markup/error.fxml"));
        Parent errorRoot = loadFxml(errorLoader);
        Scene errorScene = new Scene(errorRoot);
        Stage errorStage = new Stage();
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.setScene(errorScene);
        errorStage.setResizable(false);
        errorWindow = errorLoader.getController();
        errorWindow.setStage(errorStage);
        errorWindow.alert(localizer.getKeyString("Error"), String.join("\n", errors));
    }

    public static void inform(String title, String txt, Localizer localizer){
        InfoWindow infoWindow;
        FXMLLoader infoLoader = new FXMLLoader(DialogManager.class.getResource("/markup/info.fxml"));
        Parent infoRoot = loadFxml(infoLoader);
        Scene infoScene = new Scene(infoRoot);
        Stage infoStage = new Stage();
        infoStage.initModality(Modality.APPLICATION_MODAL);
        infoStage.setScene(infoScene);
        infoStage.setResizable(false);
        infoWindow = infoLoader.getController();
        infoWindow.setStage(infoStage);
        infoWindow.inform(localizer.getKeyString(title), txt);
    }

    public static void help(String title, String txt, Localizer localizer){
        HelpWindow helpWindow;
        FXMLLoader helpLoader = new FXMLLoader(DialogManager.class.getResource("/markup/help.fxml"));
        Parent helpRoot = loadFxml(helpLoader);
        Scene helpScene = new Scene(helpRoot);
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setScene(helpScene);
        helpStage.setResizable(false);
        helpWindow = helpLoader.getController();
        helpWindow.setStage(helpStage);
        helpWindow.help(localizer.getKeyString(title), txt);
    }

    public static File getScript(Localizer localizer){
        DialogWindow dialogWindow;
        FXMLLoader dialogLoader = new FXMLLoader(DialogManager.class.getResource("/markup/dialog.fxml"));
        Parent dialogRoot = loadFxml(dialogLoader);
        Scene dialogScene = new Scene(dialogRoot);
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogWindow = dialogLoader.getController();
        dialogWindow.setStage(dialogStage);
        dialogWindow.setLocalizer(localizer);
        dialogWindow.clear();
        dialogWindow.setAction("executeScript");
        dialogWindow.show();
        return dialogWindow.executeScript();
    }

    public static Long getId(Localizer localizer){
        DialogWindow dialogWindow;
        FXMLLoader dialogLoader = new FXMLLoader(DialogManager.class.getResource("/markup/dialog.fxml"));
        Parent dialogRoot = loadFxml(dialogLoader);
        Scene dialogScene = new Scene(dialogRoot);
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogWindow = dialogLoader.getController();
        dialogWindow.setStage(dialogStage);
        dialogWindow.setLocalizer(localizer);
        dialogWindow.clear();
        dialogWindow.setAction("update");
        dialogWindow.show();
        return dialogWindow.getId();
    }

    public static void informScr(String title, String txt, Localizer localizer){
        InfoWindow infoWindow;
        FXMLLoader infoLoader = new FXMLLoader(DialogManager.class.getResource("/markup/info.fxml"));
        Parent infoRoot = loadFxml(infoLoader);
        Scene infoScene = new Scene(infoRoot);
        Stage infoStage = new Stage();
        infoStage.initModality(Modality.APPLICATION_MODAL);
        infoStage.setScene(infoScene);
        infoStage.setResizable(false);
        infoWindow = infoLoader.getController();
        infoWindow.setStage(infoStage);
        infoWindow.informScr(localizer.getKeyString(title), txt);
    }

    public static void helpScr(String title, String txt, Localizer localizer){
        HelpWindow helpWindow;
        FXMLLoader helpLoader = new FXMLLoader(DialogManager.class.getResource("/markup/help.fxml"));
        Parent helpRoot = loadFxml(helpLoader);
        Scene helpScene = new Scene(helpRoot);
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setScene(helpScene);
        helpStage.setResizable(false);
        helpWindow = helpLoader.getController();
        helpWindow.setStage(helpStage);
        helpWindow.helpScr(localizer.getKeyString(title), txt);
    }

    private static Parent loadFxml(FXMLLoader loader) {
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return parent;
    }
}