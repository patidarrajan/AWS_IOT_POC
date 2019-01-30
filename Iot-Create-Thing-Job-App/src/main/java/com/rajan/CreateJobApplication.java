package com.rajan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateJobApplication extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/CreateJobUI.fxml"));
		primaryStage.setTitle("AWS IOT Jobs Demo");
		primaryStage.setScene(new Scene(root, 800, 600));
//		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//			public void handle(WindowEvent we) {
//				try {
//					IotJobUtils.getAWS_IOT_CLIENT().disconnect();
//				} catch (AWSIotException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
		primaryStage.show();
	}

	public static void main(String[] args) {
        launch(args);
	}
}