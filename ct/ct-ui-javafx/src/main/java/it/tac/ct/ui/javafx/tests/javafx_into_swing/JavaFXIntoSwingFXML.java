package it.tac.ct.ui.javafx.tests.javafx_into_swing;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JavaFXIntoSwingFXML {

    private static void initAndShowGUI() throws IOException {
        JFrame frame = new JFrame("Swing and JavaFX");

        final JFXPanel fxPanel = new JFXPanel();

        frame.add(fxPanel);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    initFX(fxPanel);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
       });
    }

    private static void initFX(JFXPanel fxPanel) throws IOException {
        
        Parent root = FXMLLoader.load(fxPanel.getClass().getClassLoader().getResource("config/tests/JavaFXIntoSwing.fxml"));
        Scene scene = new Scene(root);
        fxPanel.setScene(scene);
    }

    private static Scene createScene() {
        Group  root  =  new  Group();
        Scene  scene  =  new  Scene(root, Color.ALICEBLUE);
        Text  text  =  new  Text();
        
        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");

        root.getChildren().add(text);

        return (scene);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    initAndShowGUI();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}