/*
 * Copyright 2012 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.javafx.tests.combo_box;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ComboBoxTest extends Application {
    
    @FXML
    private ComboBox<String> comboBoxTest = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception {

        // Read the UI from fxml
        //
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/tests/ComboBoxTest.fxml"));
        
        // Set the scene and show
        //
        stage.setTitle("4ct");
        stage.setScene(new Scene(root));
        stage.show();
        
        System.out.println("Debug: " + comboBoxTest);
    }

    // EVENTS EVENTS EVENTS ...
    //
    @FXML
    private void comboBoxAction(ActionEvent event) {
        System.out.println("Debug: " + comboBoxTest);
    }
}
