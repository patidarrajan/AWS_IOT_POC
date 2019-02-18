package com.rajan;

import com.rajan.utils.IotJobUtils;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DeviceApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/JobUI.fxml"));
        primaryStage.setTitle("AWS IOT Jobs Demo");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	IotJobUtils.disconnect();
            }
            });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}