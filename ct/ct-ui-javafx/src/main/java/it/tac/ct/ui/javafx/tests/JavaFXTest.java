/*
 * Copyright 2012 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.javafx.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception {

        // Read the UI from fxml
        //
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/4ct.fxml"));

        // Set the scene and show
        //
        stage.setTitle("4ct");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
