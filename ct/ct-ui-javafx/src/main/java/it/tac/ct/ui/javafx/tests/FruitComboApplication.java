package it.tac.ct.ui.javafx.tests;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Main application class for fruit combo fxml demo application */
public class FruitComboApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws IOException {
        stage.setTitle("Choices");
        AnchorPane layout = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/tests/fruitcombo.fxml"));

        stage.setScene(new Scene(layout));
        stage.show();
    }
}
