/*
 * Copyright 2012 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.javafx.tests.swing_fx2;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Stage;

public class JavaFXMLSwing extends Application implements Initializable {

    @FXML
    private SwingNode swingComponentWrapper;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception {

        // Read the UI from fxml
        //
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/tests/JavaFXMLSwing.fxml"));

        // Set the scene and show
        //
        stage.setTitle("4ct");
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SwingUtilities.invokeLater(this::createMxGraph);
    }
 
    private void createMxGraph() {
        mxGraph grafo = new mxGraph();
        Object parent = grafo.getDefaultParent();

        Object v1 = grafo.insertVertex(parent, null, "Brazil", 100, 100, 50, 40);
        Object v2 = grafo.insertVertex(parent, null, "Soccer", 240, 150, 50, 40);
        grafo.insertEdge(parent, null, "loves", v1, v2);

        mxGraphComponent graphComponent = new mxGraphComponent(grafo);

        swingComponentWrapper.setContent(graphComponent);
    }
}
