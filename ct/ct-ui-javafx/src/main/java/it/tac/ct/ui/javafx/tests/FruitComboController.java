package it.tac.ct.ui.javafx.tests;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/** JavaFX fxml controller for fruit combo fxml demo application. */
public class FruitComboController implements Initializable {

    @FXML// fx:id="appleImage"
    private ImageView appleImage; // Value injected by FXMLLoader

    @FXML// fx:id="fruitCombo"
    private ComboBox<String> fruitCombo; // Value injected by FXMLLoader

    @FXML// fx:id="orangeImage"
    private ImageView orangeImage; // Value injected by FXMLLoader

    @FXML// fx:id="pearImage"
    private ImageView pearImage; // Value injected by FXMLLoader

    @FXML// fx:id="selectedFruit"
    private Label selectedFruit; // Value injected by FXMLLoader

    @Override// This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert appleImage != null : "fx:id=\"appleImage\" was not injected: check your FXML file 'fruitcombo.fxml'.";
        assert fruitCombo != null : "fx:id=\"fruitCombo\" was not injected: check your FXML file 'fruitcombo.fxml'.";
        assert orangeImage != null : "fx:id=\"orangeImage\" was not injected: check your FXML file 'fruitcombo.fxml'.";
        assert pearImage != null : "fx:id=\"pearImage\" was not injected: check your FXML file 'fruitcombo.fxml'.";
        assert selectedFruit != null : "fx:id=\"selectedFruit\" was not injected: check your FXML file 'fruitcombo.fxml'.";

        // bind the selected fruit label to the selected fruit in the combo box.
        selectedFruit.textProperty().bind(fruitCombo.getSelectionModel().selectedItemProperty());

        // listen for changes to the fruit combo box selection and update the displayed fruit image accordingly.
        fruitCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> selected, String oldFruit, String newFruit) {
                if (oldFruit != null) {
                    switch (oldFruit) {
                    case "Apple":
                        appleImage.setVisible(false);
                        break;
                    case "Orange":
                        orangeImage.setVisible(false);
                        break;
                    case "Pear":
                        pearImage.setVisible(false);
                        break;
                    }
                }
                if (newFruit != null) {
                    switch (newFruit) {
                    case "Apple":
                        appleImage.setVisible(true);
                        break;
                    case "Orange":
                        orangeImage.setVisible(true);
                        break;
                    case "Pear":
                        pearImage.setVisible(true);
                        break;
                    }
                }
            }
        });
    }
}
