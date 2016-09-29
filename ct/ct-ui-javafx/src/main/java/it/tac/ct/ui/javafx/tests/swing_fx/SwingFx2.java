package it.tac.ct.ui.javafx.tests.swing_fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingFx2 extends Application {
    private Label javaFxLabel;
    private Button  javaFxButton;
    private JLabel  swingLabel;
    private JButton swingButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void init() {
        javaFxLabel  = new Label("No Button pressed");
        javaFxButton = new Button("click JavaFX");
        javaFxButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent actionEvent) {
                javaFxLabel.setText("JavaFX Button pressed");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        swingLabel.setText("JavaFX Button pressed");
                    }
                });
            }
        });
    }

    @Override
    public void stop() throws Exception {
    }

    @Override public void start(Stage stage) {
        final SwingNode swingNode = new SwingNode();

        // Call on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                swingLabel = new JLabel("no Button pressed");

                swingButton = new JButton("click Swing");
                swingButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        swingLabel.setText("Swing Button pressed");

                        // Call on FX Application Thread
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                javaFxLabel.setText("Swing Button pressed");
                            }
                        });
                    }
                });
                JPanel panel = new JPanel();
                panel.add(swingLabel);
                panel.add(swingButton);
                panel.setVisible(true);
                swingNode.setContent(panel);
            }
        });

        StackPane pane = new StackPane();
        pane.setPrefSize(300, 120);
        // Uncomment if NOT on OS X due to rendering problems with SwingNode
        //swingNode.setTranslateY(-40);

        HBox fxControls = new HBox();
        fxControls.setSpacing(10);
        fxControls.getChildren().addAll(javaFxLabel, javaFxButton);

        fxControls.setTranslateY(40);
        pane.getChildren().addAll(swingNode, fxControls);

        Scene scene = new Scene(pane, 300, 120);

        stage.setScene(scene);
        stage.show();
        stage.setTitle("Swing Migration");
    }
}