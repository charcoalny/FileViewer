package com.demo.fileviewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader root = new FXMLLoader(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        MainController controller = root.getController();
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if(controller.saveAlert()){
                    windowEvent.consume();
                }
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}