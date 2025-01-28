package com.fx.curs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main  extends Application{
    private static Stage stg;
    public static String currentUser;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stg = primaryStage;
        primaryStage.getIcons().add(new Image("file:src/main/resources/imges/icon.png"));
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("заработало!!!");
        primaryStage.show();
        primaryStage.sizeToScene();
    }
    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        stg.getScene().setRoot(pane);
        stg.sizeToScene();
    }
    public static void main(String[] args) {
        launch();
    }
}
