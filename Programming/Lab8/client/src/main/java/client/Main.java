package client;

import client.fx.MainApp;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
            MainApp.launch(MainApp.class, args);
        }).start();

            ClientApp.main(args);

}
}
