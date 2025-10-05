package client;

import client.fx.DialogManager;
import client.fx.DialogWindow;
import client.fx.Localizer;
import client.fx.MainApp;
import shared.dto.CommandType;
import shared.dto.Response;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ScriptResponse {

    public static void read(ConcurrentLinkedQueue<Response> respQueue, Localizer localizer) {
        while (!respQueue.isEmpty()) {
                Response resp = respQueue.poll();
                CommandType type = resp.getType();
                switch (type) {
                    case HELP:
                        DialogManager.helpScr("HelpBtn", resp.getCommandCollection().stream()
                                .map(localizer::getKeyString).collect(Collectors.joining("\n")), localizer);
                        continue;
                    case INFO:
                        String message = (
                                localizer.getKeyString("InfoReturn") +
                                        "Тип коллекции:  " + resp.getInfo().getType() + "\n" +
                                        "Размер общей коллекции:  " + resp.getInfo().getNumberOfPersons() + "\n" +
                                        "Размер вашей коллекции:  " + resp.getInfo().getYourPersons() + "\n" +
                                        "Дата последнего изменения коллекции:  " + localizer.getDate(resp.getInfo().getDateOfInit()));
                        DialogManager.inform("Info", message, localizer);
                        continue;
                    case ADD, ADD_IF_MAX, UPDATE, REMOVE_BY_ID, CLEAR, PRINT_FIELD_ASCENDING_HEIGHT:
                        DialogManager.informScr("Info", localizer.getKeyString(resp.getMessage()), localizer);
                }

        }

    }
}
