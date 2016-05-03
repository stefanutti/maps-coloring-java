/*
 * Copyright 2012 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.javafx.tests.events;

import com.sun.javafx.scene.control.skin.CustomColorDialog;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class JavaFXEventsTest extends Application {

    @FXML
    private Button buttonColor = null;

    @FXML
    private ColorPicker colorPicker = null;
    
    Stage theStage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception {

        // Read the UI from fxml
        //
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/tests/JavaFXEventsTest.fxml"));
        
        theStage = stage;
        
        colorPicker = new ColorPicker();

        // Set the scene and show
        //
        stage.setTitle("4ct");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // EVENTS EVENTS EVENTS ...
    //
    @FXML
    private void colorPickerAction(ActionEvent event) {
        System.out.println(colorPicker.getValue().getRed() + "-" + colorPicker.getValue().getGreen() + "-" + colorPicker.getValue().getBlue());
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Look, an Error Dialog");
        alert.setContentText("Ooops, there was an error!");
        alert.showAndWait();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(theStage);
    }
}
