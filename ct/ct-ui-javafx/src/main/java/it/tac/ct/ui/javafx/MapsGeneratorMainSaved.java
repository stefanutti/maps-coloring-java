/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.javafx;

import it.tac.ct.core.COLORS;
import it.tac.ct.core.ColorPalette;
import it.tac.ct.core.F;
import it.tac.ct.core.FCoordinate;
import it.tac.ct.core.Graph4CT;
import it.tac.ct.core.GraphicalObjectCoordinate;
import it.tac.ct.core.Map4CT;
import it.tac.ct.core.MapsGenerator;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;

import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultEdge;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.png.PNGMetadata;

// TODO: Re-factor the code. It is getting too large to maintain!
// TODO: Change swixml2 to JavaFX
// TODO: Change "G" to JavaFX
// TODO: Remove unused libraries: JUNG, ...
// TODO: Spiral chains with sleep(), save to svg, manual vertex selection for spiral start vertex
// TODO: saveAllGraphs ... choose the directory where to save all graphs
// TODO: Load soundbanks on the fly
// TODO: Debug (print) info on demand (button)
//---
//--- HISTORY
//---
// 23/Mar/2016 - Changed swixml2 to JavaFX

/**
 * @author Mario Stefanutti
 * @version September 2007
 */
public class MapsGeneratorMainSaved extends Application implements Initializable {

    // The main class to generate maps
    //
    private MapsGenerator mapsGenerator = new MapsGenerator();

    // These two variables permit navigation through maps
    //
    private Map4CT map4CTCurrent = null;
    private int map4CTCurrentIndex = -1;

    // G Graphic objects to draw maps
    //
    private GScene gScene = null;
    private GWindow gWindow = null;

    // The types of regular maps
    //
    private enum DRAW_METHOD {
        CIRCLES, RECTANGLES, RECTANGLES_NEW_YORK
    };

    // Some graphical properties
    //
    private DRAW_METHOD drawMethodValue = DRAW_METHOD.CIRCLES;
    public static final int LINE_WIDTH = 1;

    // FXML stage
    //
    private Stage theStage;

    // TAB: Main
    //
    @FXML
    private CheckBox logWhilePopulate = null;
    @FXML
    private CheckBox randomElaboration = null;
    @FXML
    private CheckBox processAll = null;
    @FXML
    private TextField slowdownMillisecTextField = null;
    @FXML
    private ComboBox<String> maxMethod = null;
    @FXML
    private CheckBox removeIsoWhilePopulate = null;
    @FXML
    private TextField maxNumberTextField = null;
    @FXML
    private Button startElaborationButton = null;
    @FXML
    private Button pauseElaborationButton = null;
    @FXML
    private Button filterLessThanFourElaborationButton = null;
    @FXML
    private Button filterLessThanFiveElaborationButton = null;
    @FXML
    private Button filterLessThanFacesElaborationButton = null;
    @FXML
    private Button copyMapsToTodoElaborationButton = null;
    @FXML
    private Button resetElaborationButton = null;
    @FXML
    private SwingNode mapExplorerSwingNode = null;
    private JFrame mapExplorerFrame = null;
    private JPanel mapExplorerPanel = null;
    @FXML
    private TextField mapTextRepresentationTextField = null;
    @FXML
    private Button createMapFromTextRepresentationButton = null;
    @FXML
    private Button getTextRepresentationOfCurrentMapButton = null;
    @FXML
    private Button loadMapFromAPreviouslySavedImageButton = null;
    @FXML
    private ComboBox<String> drawMethod = null;
    @FXML
    private Slider transparency = null;
    @FXML
    private CheckBox showFaceCardinality = null;
    @FXML
    private ColorPicker selectColorOnePicker = null;
    @FXML
    private ColorPicker selectColorTwoPicker = null;
    @FXML
    private ColorPicker selectColorThreePicker = null;
    @FXML
    private ColorPicker selectColorFourPicker = null;
    @FXML
    private Button taitButton = null;
    @FXML
    private TextField currentMapTextField = null;
    @FXML
    private TextField mapsSizeTextField = null;
    @FXML
    private TextField mapsRemovedTextField = null;
    @FXML
    private TextField todoListSizeTextField = null;
    @FXML
    private TextField totalMemoryTextField = null;
    @FXML
    private TextField maxMemoryTextField = null;
    @FXML
    private TextField freeMemoryTextField = null;
    @FXML
    private TextField isoOuterLoopTextField = null;
    @FXML
    private TextField isoInnerLoopTextField = null;
    @FXML
    private TextField isoRemovedTextField = null;
    @FXML
    private CheckBox soundWhileColoring = null;
    @FXML
    private ComboBox<String> colorOneInstrument = null;
    @FXML
    private TextField colorOneBaseNoteTextField = null;
    @FXML
    private TextField colorOneBaseDurationTextField = null;
    @FXML
    private TextField colorOneBaseVelocityTextField = null;
    @FXML
    private ComboBox<String> colorTwoInstrument = null;
    @FXML
    private TextField colorTwoBaseNoteTextField = null;
    @FXML
    private TextField colorTwoBaseDurationTextField = null;
    @FXML
    private TextField colorTwoBaseVelocityTextField = null;
    @FXML
    private ComboBox<String> colorThreeInstrument = null;
    @FXML
    private TextField colorThreeBaseNoteTextField = null;
    @FXML
    private TextField colorThreeBaseDurationTextField = null;
    @FXML
    private TextField colorThreeBaseVelocityTextField = null;
    @FXML
    private ComboBox<String> colorFourInstrument = null;
    @FXML
    private TextField colorFourBaseNoteTextField = null;
    @FXML
    private TextField colorFourBaseDurationTextField = null;
    @FXML
    private TextField colorFourBaseVelocityTextField = null;

    // These will be set using a color picker
    //
    private Color colorOne = null;
    private Color colorTwo = null;
    private Color colorThree = null;
    private Color colorFour = null;

    @FXML
    private Thread colorItThread = null;
    @FXML
    private Thread colorAllThread = null;
    @FXML
    private boolean stopColoringRequested = false;
    private int transparencyValue = 255; // TBV: See swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54)

    private Soundbank soundbank = null;
    private Synthesizer synthesizer = null;
    private Instrument[] instruments = null;
    private MidiChannel[] midiChannels = null;

    private FileChooser fileChooser = null;
    private DirectoryChooser directoryChooser = null;

    // TAB: Graph theory
    //
    @FXML
    private SwingNode graphExplorerSwingNode = null;
    private JFrame graphExplorerFrame = null;
    private JPanel graphExplorerPanel = null;
    @FXML
    private CheckBox fitToWindow = null;
    @FXML
    private ComboBox<String> graphLayout;
    @FXML
    private TextField startingVertexTextField = null;
    @FXML
    private CheckBox autoSpiral = null;
    @FXML
    private CheckBox autoColor = null;
    @FXML
    private TextField currentMapTextField2 = null;
    @FXML
    private TextField mapsSizeTextField2 = null;
    @FXML
    private TextField mapsRemovedTextField2 = null;
    @FXML
    private TextField todoListSizeTextField2 = null;
    @FXML
    private TextField totalMemoryTextField2 = null;
    @FXML
    private TextField maxMemoryTextField2 = null;
    @FXML
    private TextField freeMemoryTextField2 = null;
    @FXML
    private TextField isoOuterLoopTextField2 = null;
    @FXML
    private TextField isoInnerLoopTextField2 = null;
    @FXML
    private TextField isoRemovedTextField2 = null;
    @FXML
    private ComboBox<String> secondColorOfKempeSwitch;

    // Variables to solve some swings problem, I don't know how to completely solve
    //
    private int graphExplorerOriginalWidth = -1; // I have many problems with resize(), partially solved this way
    private int graphExplorerOriginalHeight = -1;

    // Various to control graph visualization
    //
    private Graph4CT graph4CTCurrent = null;
    private Thread removeIsoThread = null;

    /**
     * Inner class: The G map to draw
     */
    private class GMap4CTRectangles extends GObject {

        // Local variables
        //
        private GSegment[] rectangles = null;
        private Map4CT map4CT = null;

        /**
         * Constructor
         * 
         * @param map4CTToDraw
         *            The map to draw
         */
        public GMap4CTRectangles(Map4CT map4CTToDraw) {

            // Set the map to draw when draw() will be called
            //
            map4CT = map4CTToDraw;

            // Creates the graphical segments
            //
            rectangles = new GSegment[map4CT.faces.size()];
            for (int i = 0; i < map4CT.faces.size(); i++) {
                rectangles[i] = new GSegment();
                addSegment(rectangles[i]);
                rectangles[i].setUserData(map4CT.faces.get(i));
            }
        }

        /**
         * draw the map
         */
        @Override
        public void draw() {

            // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
            //
            // x = Position in the list (index)
            // y = Number of the F - 1
            // w = distance between End and Begin (of the indexes)
            // h = 1 - y
            //
            // Everything has to be normalized [0, 1]
            //
            double normalizationXFactor = map4CT.sequenceOfCoordinates.sequence.size();
            double normalizationYFactor = map4CT.faces.size();

            List<GraphicalObjectCoordinate> graphicalObjectCoordinates = new ArrayList<GraphicalObjectCoordinate>(map4CT.faces.size());
            for (int i = 0; i < map4CT.faces.size(); i++) {
                graphicalObjectCoordinates.add(new GraphicalObjectCoordinate());
            }

            // Set the F1
            //
            graphicalObjectCoordinates.get(0).x = 0.0f;
            graphicalObjectCoordinates.get(0).y = 0.0f;
            graphicalObjectCoordinates.get(0).w = 1.0f;
            graphicalObjectCoordinates.get(0).h = 1.0f;

            // Computes graphical coordinates for all other Fs
            //
            for (int i = 1; i < map4CT.sequenceOfCoordinates.sequence.size() - 1; i++) {
                GraphicalObjectCoordinate g = graphicalObjectCoordinates.get(map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1);
                if (map4CT.sequenceOfCoordinates.sequence.get(i).type == FCoordinate.TYPE.BEGIN) {
                    g.x = (float) ((i / normalizationXFactor) + (1.0d / (2.0d * normalizationXFactor)));
                    g.y = (float) ((map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1.0d) / normalizationYFactor);
                    g.w = (float) i; // Temporary variable
                    g.h = (float) (1.0d - g.y);

                    if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
                        g.y = 1 - g.y;
                        g.h = -1 * g.h;
                    }
                } else {
                    g.w = (float) ((i - g.w) * (1.0d / normalizationXFactor));
                }
            }

            // Plot
            //
            for (int iFace = 0; iFace < map4CT.faces.size(); iFace++) {
                GraphicalObjectCoordinate g = graphicalObjectCoordinates.get(iFace);
                rectangles[iFace].setGeometryXy(Geometry.createRectangle(g.x, g.y, g.w, g.h));
                if (showFaceCardinality.isSelected()) {
                    rectangles[iFace].setText(new GText("" + map4CT.faces.get(iFace).cardinality));
                }

                GStyle faceStyle = gStyleFromFace(map4CT.faces.get(iFace));
                rectangles[iFace].setStyle(faceStyle);
            }

            // Override plot data for F1
            //
            if (showFaceCardinality.isSelected()) {
                rectangles[0].setText(new GText("" + (map4CT.faces.size() + 1) + ": " + map4CT.faces.get(0).cardinality + " - " + map4CT.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders()));
            }
        }

        /**
         * Draw just one rectangle of the map
         * 
         * @param face
         *            The face (rectangle) to show
         */
        public void drawN(int face) {

            // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+,
            // 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
            //
            // x = Position in the list (index)
            // y = Number of the F - 1
            // w = distance between End and Begin (of the indexes)
            // h = 1 - y
            //
            // Everything has to be normalized [0, 1]
            //
            double normalizationXFactor = map4CT.sequenceOfCoordinates.sequence.size();
            double normalizationYFactor = map4CT.faces.size();

            List<GraphicalObjectCoordinate> graphicalObjectCoordinates = new ArrayList<GraphicalObjectCoordinate>(map4CT.faces.size());
            for (int i = 0; i < map4CT.faces.size(); i++) {
                graphicalObjectCoordinates.add(new GraphicalObjectCoordinate());
            }

            // Set the F1
            //
            graphicalObjectCoordinates.get(0).x = 0.0f;
            graphicalObjectCoordinates.get(0).y = 0.0f;
            graphicalObjectCoordinates.get(0).w = 1.0f;
            graphicalObjectCoordinates.get(0).h = 1.0f;

            // Computes graphical coordinates for all other Fs
            //
            for (int i = 1; i < map4CT.sequenceOfCoordinates.sequence.size() - 1; i++) {
                GraphicalObjectCoordinate g = graphicalObjectCoordinates.get(map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1);
                if (map4CT.sequenceOfCoordinates.sequence.get(i).type == FCoordinate.TYPE.BEGIN) {
                    g.x = (float) ((i / normalizationXFactor) + (1.0d / (2.0d * normalizationXFactor)));
                    g.y = (float) ((map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1.0d) / normalizationYFactor);
                    g.w = (float) i; // Temporary variable
                    g.h = (float) (1.0d - g.y);

                    if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
                        g.y = 1 - g.y;
                        g.h = -1 * g.h;
                    }
                } else {
                    g.w = (float) ((i - g.w) * (1.0d / normalizationXFactor));
                }
            }

            GraphicalObjectCoordinate g = graphicalObjectCoordinates.get(face);
            rectangles[face].setGeometryXy(Geometry.createRectangle(g.x, g.y, g.w, g.h));
            if (showFaceCardinality.isSelected()) {
                rectangles[face].setText(new GText("" + map4CT.faces.get(face).cardinality));
            }

            GStyle faceStyle = gStyleFromFace(map4CT.faces.get(face));
            rectangles[face].setStyle(faceStyle);

            // First face (the biggest rectangle) is treated separately
            //
            if (face == 0) {

                // Override plot data for F1
                //
                if (showFaceCardinality.isSelected()) {
                    rectangles[0].setText(new GText("" + (map4CT.faces.size() + 1) + ": " + map4CT.faces.get(0).cardinality + " - " + map4CT.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders()));
                }
            }
        }
    };

    /**
     * The G map to draw
     */
    private class GMap4CTCircles extends GObject {

        // Local variables
        //
        private GSegment[] rings = null;
        private Map4CT map4CT = null;

        private final float MAX_RADIUS = 0.5f;

        /**
         * Constructor
         * 
         * @param map4CTToDraw
         *            The map to draw
         */
        public GMap4CTCircles(Map4CT map4CTToDraw) {

            // Set the map to draw when draw() will be called
            //
            map4CT = map4CTToDraw;

            // Creates the graphical segments
            //
            // circles = new GSegment[map4CT.faces.size()];
            rings = new GSegment[map4CT.faces.size()];

            // Create all the faces/rings (the first one is an entire circle)
            //
            for (int i = 0; i < map4CT.faces.size(); i++) {
                rings[i] = new GSegment();
                addSegment(rings[i]);
                rings[i].setUserData(map4CT.faces.get(i));
            }
        }

        /**
         * draw the map
         */
        @Override
        public void draw() {

            // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+,
            // 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
            //
            // Everything has to be normalized [0, 1]
            //
            double normalizationAngleFactor = map4CT.sequenceOfCoordinates.sequence.size();
            double spaceBetweenCircles = MAX_RADIUS / map4CT.faces.size();

            List<GraphicalObjectCoordinate> graphicalObjectCoordinates = new ArrayList<GraphicalObjectCoordinate>(map4CT.faces.size());
            for (int i = 0; i < map4CT.faces.size(); i++) {
                graphicalObjectCoordinates.add(new GraphicalObjectCoordinate());
            }

            // Computes graphical coordinates for all other Fs (not considering
            // the first one)
            //
            GraphicalObjectCoordinate g = null;
            for (int i = 1; i < map4CT.sequenceOfCoordinates.sequence.size() - 1; i++) {
                g = graphicalObjectCoordinates.get(map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1);
                if (map4CT.sequenceOfCoordinates.sequence.get(i).type == FCoordinate.TYPE.BEGIN) {
                    g.startAngle = (float) ((i / normalizationAngleFactor) * 2 * Math.PI);
                } else {
                    g.stopAngle = (float) ((i / normalizationAngleFactor) * 2 * Math.PI);
                }
            }

            // Plot the first face
            //
            rings[0].setGeometryXy(createCircle(0.5, 0.5, MAX_RADIUS));

            // Override plot data for F1
            //
            if (showFaceCardinality.isSelected()) {
                rings[0].setText(new GText("" + (map4CT.faces.size() + 1) + ": " + map4CT.faces.get(0).cardinality + " - " + map4CT.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders()));
            }
            GStyle faceStyle = gStyleFromFace(map4CT.faces.get(0));
            rings[0].setStyle(faceStyle);

            // Plot the other rings all starting from the center (0.5, 0.5)
            //
            for (int iFace = 1; iFace < map4CT.faces.size(); iFace++) {
                g = graphicalObjectCoordinates.get(iFace);
                g.startRadius = (float) (iFace * spaceBetweenCircles);
                g.stopRadius = MAX_RADIUS;
                rings[iFace].setGeometryXy(createRing(0.5, 0.5, g.startRadius, g.stopRadius, g.startAngle, g.stopAngle));
                if (showFaceCardinality.isSelected()) {
                    rings[iFace].setText(new GText("" + map4CT.faces.get(iFace).cardinality));
                }

                faceStyle = gStyleFromFace(map4CT.faces.get(iFace));
                rings[iFace].setStyle(faceStyle);
            }
        }

        /**
         * Draw just one ring of the map
         * 
         * @param face
         *            The face (rectangle) to show
         */
        public void drawN(int face) {

            // First face (the whole disk) is treated separately
            //
            if (face == 0) {

                // Plot the first face
                //
                rings[0].setGeometryXy(createCircle(0.5, 0.5, MAX_RADIUS));

                // Override plot data for F1
                //
                if (showFaceCardinality.isSelected()) {
                    rings[0].setText(new GText("" + (map4CT.faces.size() + 1) + ": " + map4CT.faces.get(0).cardinality + " - " + map4CT.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders()));
                }
                GStyle faceStyle = gStyleFromFace(map4CT.faces.get(0));
                rings[0].setStyle(faceStyle);
            } else {

                // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+,
                // 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
                //
                // Everything has to be normalized [0, 1]
                //
                double normalizationAngleFactor = map4CT.sequenceOfCoordinates.sequence.size();
                double spaceBetweenCircles = MAX_RADIUS / map4CT.faces.size();

                List<GraphicalObjectCoordinate> graphicalObjectCoordinates = new ArrayList<GraphicalObjectCoordinate>(map4CT.faces.size());
                for (int i = 0; i < map4CT.faces.size(); i++) {
                    graphicalObjectCoordinates.add(new GraphicalObjectCoordinate());
                }

                // Computes graphical coordinates for all other Fs (not
                // considering the first one)
                //
                GraphicalObjectCoordinate g = null;
                for (int i = 1; i < map4CT.sequenceOfCoordinates.sequence.size() - 1; i++) {
                    g = graphicalObjectCoordinates.get(map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1);
                    if (map4CT.sequenceOfCoordinates.sequence.get(i).type == FCoordinate.TYPE.BEGIN) {
                        g.startAngle = (float) ((i / normalizationAngleFactor) * 2 * Math.PI);
                    } else {
                        g.stopAngle = (float) ((i / normalizationAngleFactor) * 2 * Math.PI);
                    }
                }

                g = graphicalObjectCoordinates.get(face);
                g.startRadius = (float) (face * spaceBetweenCircles);
                g.stopRadius = MAX_RADIUS;
                rings[face].setGeometryXy(createRing(0.5, 0.5, g.startRadius, g.stopRadius, g.startAngle, g.stopAngle));
                if (showFaceCardinality.isSelected()) {
                    rings[face].setText(new GText("" + map4CT.faces.get(face).cardinality));
                }

                GStyle faceStyle = gStyleFromFace(map4CT.faces.get(face));
                rings[face].setStyle(faceStyle);
            }
        }

        /**
         * Create a new ring with the given parameters
         * 
         * @param xCenter
         * @param yCenter
         * @param startRadius
         * @param stopRadius
         * @param startAngle
         * @param stopAngle
         * @return
         */
        public double[] createRing(double xCenter, double yCenter, double startRadius, double stopRadius, double startAngle, double stopAngle) {

            // A point for each degree
            //
            double arcStep = Math.PI / 180.0;
            int internalPointsPerArc = (int) ((stopAngle - startAngle) / arcStep);

            int pointX = 0; // Convenient variable to fill the double[]

            // |......|
            // |......|
            //
            // There are many ways to write down this formula. Pick yours, I
            // decided for this one
            //
            double[] ring = new double[((internalPointsPerArc + 2) * 2 * 2) + 2];

            // Compute the 4 corners: the last one is set as the first one to
            // close the path
            //
            pointX = 0;
            ring[pointX] = (Math.cos(startAngle) * startRadius) + xCenter;
            ring[pointX + 1] = (Math.sin(startAngle) * startRadius) + yCenter;

            pointX += (internalPointsPerArc * 2) + 2;
            ring[pointX] = (Math.cos(stopAngle) * startRadius) + xCenter;
            ring[pointX + 1] = (Math.sin(stopAngle) * startRadius) + yCenter;

            pointX += 2;
            ring[pointX] = (Math.cos(stopAngle) * stopRadius) + xCenter;
            ring[pointX + 1] = (Math.sin(stopAngle) * stopRadius) + yCenter;

            pointX += (internalPointsPerArc * 2) + 2;
            ring[pointX] = (Math.cos(startAngle) * stopRadius) + xCenter;
            ring[pointX + 1] = (Math.sin(startAngle) * stopRadius) + yCenter;

            // I close the path connecting it to the beginning
            //
            pointX += 2;
            ring[pointX] = ring[0];
            ring[pointX + 1] = ring[1];

            // Inner points of the inner arc and of the outer arc
            //
            // for (pointX = 2; pointX < (internalPointsPerArc * 2) + 2; pointX
            // += 2) {
            //
            for (int i = 1; i <= internalPointsPerArc; i++) {
                pointX = i * 2;
                ring[pointX] = (Math.cos(startAngle + (arcStep * i)) * startRadius) + xCenter;
                ring[pointX + 1] = (Math.sin(startAngle + (arcStep * i)) * startRadius) + yCenter;

                pointX += (internalPointsPerArc * 2) + 4;
                ring[pointX] = (Math.cos(stopAngle - (arcStep * i)) * stopRadius) + xCenter;
                ring[pointX + 1] = (Math.sin(stopAngle - (arcStep * i)) * stopRadius) + yCenter;
            }

            return ring;
        }

        /**
         * Create a new circle with the given parameters
         * 
         * @param xCenter
         * @param yCenter
         * @param radius
         * @return
         */
        public double[] createCircle(double xCenter, double yCenter, double radius) {

            // A point for each degree
            //
            int points = 360;

            // The circle
            //
            double[] circle = new double[(points * 2) + 2];
            for (int i = 0; i <= points; i++) {
                circle[i * 2] = (Math.cos(i * (Math.PI / 180)) * radius) + xCenter;
                circle[(i * 2) + 1] = (Math.sin(i * (Math.PI / 180)) * radius) + xCenter;
            }

            return circle;
        }
    };

    /**
     * Utility class to refresh info about memory, etc.
     */
    private final Runnable refreshManager = new Runnable() {
        public void run() {

            while (true) {
                refreshInfo();
                refreshMemoryInfo();
                refreshIsoInfo();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    };

    /**
     * Utility class to run the generate() method of the MapsGenerator
     */
    private final Runnable runnableGenerate = new Runnable() {
        public void run() {
            try {
                mapsGenerator.generate();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                startElaborationButton.setDisable(mapsGenerator.todoList.size() == 0);
                pauseElaborationButton.setDisable(true);
                resetElaborationButton.setDisable(false);
                filterLessThanFourElaborationButton.setDisable(false);
                filterLessThanFiveElaborationButton.setDisable(false);
                filterLessThanFacesElaborationButton.setDisable(false);
                copyMapsToTodoElaborationButton.setDisable(false);
                createMapFromTextRepresentationButton.setDisable(false);
                getTextRepresentationOfCurrentMapButton.setDisable(false);
                loadMapFromAPreviouslySavedImageButton.setDisable(false);

                refreshInfo();
            }
        }
    };

    private final Runnable runnableRemoveIsoMaps = new Runnable() {
        public void run() {
            if (map4CTCurrentIndex != -1) {
                mapsGenerator.removeIsomorphicMaps(mapsGenerator.maps, map4CTCurrentIndex);
            } else {
                mapsGenerator.removeIsomorphicMaps(mapsGenerator.maps, 0);
            }
        }
    };

    private final Runnable runnableRemoveIsoTodoList = new Runnable() {
        public void run() {
            mapsGenerator.removeIsomorphicMaps(mapsGenerator.todoList, 0);
        }
    };

    private final Runnable runnableColorIt = new Runnable() {
        public void run() {
            colorIt();
        }
    };

    private final Runnable runnableColorAll = new Runnable() {
        public void run() {
            colorAll();
        }
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Read the UI from fxml
        //
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/4ct.fxml"));

        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("config/4ct.fxml"));
        
        // Init Map Explorer and Graph Explorer
        //
        initMapExplorerForGraphic();
        initGraphExplorerForGraphic();

        stage.setTitle("4ct");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Set the instruments name taken from an internet bank
     * 
     * @param ComboBox
     * @param instruments
     */
    public void setInstrumentsNames(ComboBox<String> comboBox, Instrument[] instruments) {
        List<String> items = new ArrayList<String>();

        for (int i = 0; i < instruments.length; i++) {
            items.add(instruments[i].getName());
        }
        ObservableList<String> instrumentList = FXCollections.observableList(items);
        comboBox.getItems().clear();
        comboBox.setItems(instrumentList);
    }

    /**
     * Init the MapExplorer to visualize G graphics
     */
    public void initMapExplorerForGraphic() {

        System.out.println("Debug: initMapExplorerForGraphic START");

        // Swing thread
        //
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                // Create the JPanels (Swing into JavaFX) and embed them into the JavaFX SwingNode
                //
                mapExplorerFrame = new JFrame("Map Explorer");
                mapExplorerFrame.setSize(800, 800);

                mapExplorerPanel = new JPanel();
                mapExplorerFrame.getContentPane().add(mapExplorerPanel);
                mapExplorerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mapExplorerFrame.setVisible(true);
                
                Map4CT newMap = MapsGenerator.createMapFromTextRepresentation("1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+", -1);
                mapsGenerator.maps.add(newMap);
                mapsGenerator.todoList.add(newMap);

                // mapExplorerSwingNode.setContent(mapExplorerPanel);
                // graphExplorerSwingNode.setContent(graphExplorerPanel);
                // System.out.println("Debug: mapExplorerPanel.getHeight = " + mapExplorerPanel.getHeight());

                // G lib initialization (link window canvas to JPanel)
                //
                gWindow = new GWindow(new java.awt.Color((int) colorFour.getRed(), (int) colorFour.getGreen(), (int) colorFour.getBlue()));
                gScene = new GScene(gWindow);
                mapExplorerPanel.removeAll();
                mapExplorerPanel.add(gWindow.getCanvas());

                // Use a normalized world extent (adding a safety border)
                //
                // @param x0 X coordinate of world extent origin.
                // @param y0 Y coordinate of world extent origin.
                // @param width Width of world extent.
                // @param height Height of world extent.
                //
                gScene.setWorldExtent(-0.1, -0.1, 1.2, 1.2);
                
                refreshInfo();
                drawCurrentMap();

                // Set interaction
                //
                // window.startInteraction(new ZoomInteraction(scene)); Zoom or mouse selection for coloring. Is it possible to have both?
                //
                // xxx gWindow.startInteraction();
            }
        });
    }

    /**
     * Init the GraphExplorer to visualize mxGraph (jgraph) graphics
     */
    public void initGraphExplorerForGraphic() {

        // Swing thread
        //
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                // Create the JPanels (Swing into JavaFX) and embed them into the JavaFX SwingNode
                //
                graphExplorerFrame = new JFrame("Graph Explorer");
                graphExplorerFrame.setSize(800, 800);

                graphExplorerPanel = new JPanel();
                graphExplorerFrame.getContentPane().add(graphExplorerPanel);
                graphExplorerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                graphExplorerFrame.setVisible(true);


                // Init (or re-init) all
                //
                graph4CTCurrent = new Graph4CT();
                graph4CTCurrent.init();
                graph4CTCurrent.setDimension(graphExplorerOriginalHeight, graphExplorerOriginalWidth);

                if (graphLayout.getValue().compareTo("RECTANGULAR") == 0) {
                    graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.RECTANGULAR);
                } else if (graphLayout.getValue().compareTo("MX_HIERARCHICAL_LAYOUT") == 0) {
                    graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.MX_HIERARCHICAL_LAYOUT);
                }
                // else if (graphLayout.getValue().compareTo("MX_CIRCLE_LAYOUT") == 0) {
                // graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.MX_CIRCLE_LAYOUT);
                // }

                // Attach (or re-attach) it to the JPanel
                //
                graphExplorerPanel.removeAll();
                graphExplorerPanel.setSize(graphExplorerOriginalWidth, graphExplorerOriginalHeight);
                graphExplorerPanel.add(graph4CTCurrent.getComponent());
            }
        });
    }

    @FXML
    private void logWhilePopulateAction(ActionEvent event) {

        mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
    }

    @FXML
    private void removeIsoWhilePopulateAction(ActionEvent event) {

        mapsGenerator.removeIsoWhilePopulate = removeIsoWhilePopulate.isSelected();
    }

    @FXML
    private void startElaborationAction(ActionEvent event) {

        // Read parameters set by user (User Interface)
        //
        mapsGenerator.slowdownMillisec = Integer.parseInt(slowdownMillisecTextField.getText());
        mapsGenerator.randomElaboration = randomElaboration.isSelected();
        mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
        mapsGenerator.processAll = processAll.isSelected();
        mapsGenerator.removeIsoWhilePopulate = removeIsoWhilePopulate.isSelected();

        if ("FIXED_MAPS_LEN".compareTo(maxMethod.getValue().toString()) == 0) {
            mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.FIXED_MAPS_LEN;
        } else if ("MAPS".compareTo(maxMethod.getValue().toString()) == 0) {
            mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.MAPS;
        } else if ("F".compareTo(maxMethod.getValue().toString()) == 0) {
            mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.F;
        }
        mapsGenerator.maxNumber = Integer.parseInt(maxNumberTextField.getText());

        // Set the buttons
        //
        startElaborationButton.setDisable(true);
        pauseElaborationButton.setDisable(false);
        resetElaborationButton.setDisable(true);
        filterLessThanFourElaborationButton.setDisable(true);
        filterLessThanFiveElaborationButton.setDisable(true);
        filterLessThanFacesElaborationButton.setDisable(true);
        copyMapsToTodoElaborationButton.setDisable(true);
        createMapFromTextRepresentationButton.setDisable(true);
        getTextRepresentationOfCurrentMapButton.setDisable(true);
        loadMapFromAPreviouslySavedImageButton.setDisable(true);

        // Execute the thread
        //
        new Thread(runnableGenerate).start();
    }

    @FXML
    private void pauseElaborationAction(ActionEvent event) {

        // I want to stop the elaboration
        // Information on screen will be updated by the thread itself
        //
        mapsGenerator.stopRequested = true;
    }

    @FXML
    private void filterLessThanFourElaborationAction(ActionEvent event) {

        mapsGenerator.removeMapsWithCardinalityLessThanFour();
        refreshInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    @FXML
    private void filterLessThanFiveElaborationAction(ActionEvent event) {

        mapsGenerator.removeMapsWithCardinalityLessThanFive();
        refreshInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    @FXML
    private void filterLessThanFacesElaborationAction(ActionEvent event) {

        mapsGenerator.removeMapsWithLessThanFFaces(Integer.parseInt(maxNumberTextField.getText()));
        refreshInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    @FXML
    private void copyMapsToTodoElaborationAction(ActionEvent event) {

        mapsGenerator.todoList = new ArrayList<Map4CT>();
        mapsGenerator.copyMapsToTodo();
        if (mapsGenerator.todoList.size() != 0) {
            startElaborationButton.setDisable(false);
        }
        refreshInfo();
    }

    @FXML
    private void resetElaborationAction(ActionEvent event) {

        // Creates a brend new generator
        //
        mapsGenerator = new MapsGenerator();
        map4CTCurrent = null;
        map4CTCurrentIndex = -1;

        // Reset buttons
        //
        startElaborationButton.setDisable(false);
        pauseElaborationButton.setDisable(true);
        resetElaborationButton.setDisable(true);
        filterLessThanFourElaborationButton.setDisable(true);
        filterLessThanFiveElaborationButton.setDisable(true);
        filterLessThanFacesElaborationButton.setDisable(true);
        copyMapsToTodoElaborationButton.setDisable(true);
        createMapFromTextRepresentationButton.setDisable(false);
        getTextRepresentationOfCurrentMapButton.setDisable(false);
        loadMapFromAPreviouslySavedImageButton.setDisable(false);

        // Refresh info and redraw (reset in this case) graph
        //
        refreshInfo();
        drawCurrentMap();
    }

    @FXML
    private void drawFirstMapAction(ActionEvent event) {

        // Start the work
        //
        if (mapsGenerator.maps.size() != 0) {

            // Move to the first map
            //
            map4CTCurrentIndex = 0;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            drawCurrentMap();
        }
    }

    @FXML
    private void drawPreviousMapAction(ActionEvent event) {

        // Start the work
        //
        if ((mapsGenerator.maps.size() != 0) && (map4CTCurrentIndex > 0)) {

            // Move to the previous map
            //
            map4CTCurrentIndex = map4CTCurrentIndex - 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            drawCurrentMap();
        }
    }

    @FXML
    private void drawRandomMapAction(ActionEvent event) {

        // Start the work
        //
        if (mapsGenerator.maps.size() != 0) {

            // Random map
            //
            map4CTCurrentIndex = new Random().nextInt(mapsGenerator.maps.size());
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            drawCurrentMap();
        }
    }

    @FXML
    private void drawNextMapAction(ActionEvent event) {

        // Start the work
        //
        if ((mapsGenerator.maps.size() != 0) && (map4CTCurrentIndex < (mapsGenerator.maps.size() - 1))) {

            // Move to the previous map
            //
            map4CTCurrentIndex = map4CTCurrentIndex + 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            drawCurrentMap();
        }
    }

    @FXML
    private void drawLastMapAction(ActionEvent event) {

        // Start the work
        //
        if (mapsGenerator.maps.size() != 0) {

            // Move to the previous map
            //
            map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            drawCurrentMap();
        }
    }

    @FXML
    private void drawCurrentMapSlowMotionAction(ActionEvent event) {

        drawCurrentMapSlowMotion();
    }

    @FXML
    private void createMapFromTextRepresentationAction(ActionEvent event) {

        // Update the map list and the todoList + set the current map
        //
        Map4CT newMap = MapsGenerator.createMapFromTextRepresentation(mapTextRepresentationTextField.getText(), -1);
        mapsGenerator.maps.add(newMap);
        mapsGenerator.todoList.add(newMap);
        map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
        map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

        // It behaves as the play (generation) action - At the end of the generation thread
        //
        startElaborationButton.setDisable(mapsGenerator.todoList.size() == 0);
        pauseElaborationButton.setDisable(true);
        resetElaborationButton.setDisable(false);
        filterLessThanFourElaborationButton.setDisable(false);
        filterLessThanFiveElaborationButton.setDisable(false);
        filterLessThanFacesElaborationButton.setDisable(false);
        copyMapsToTodoElaborationButton.setDisable(false);
        createMapFromTextRepresentationButton.setDisable(false);
        getTextRepresentationOfCurrentMapButton.setDisable(false);
        loadMapFromAPreviouslySavedImageButton.setDisable(false);

        refreshInfo();
        drawCurrentMap();
    }

    @FXML
    private void getTextRepresentationOfCurrentMapAction(ActionEvent event) {

        // "1b+, 2b+, 9b+, 8b-, 3b-, 5b-, 7b-, 4b-, 2e-, 3e-, 5e-, 6b-, 4e-,
        // 8e-, 7e-, 9e+, 6e+, 1e+"
        //
        if (map4CTCurrent != null) {
            mapTextRepresentationTextField.setText(map4CTCurrent.toString());
        }
    }

    @FXML
    private void loadMapFromAPreviouslySavedImageAction(ActionEvent event) {

        // Read the metadata from a saved image
        //
        File fileToRead = fileChooser.showOpenDialog(null);

        // Have you Selected a file?
        //
        if (fileToRead != null) {

            try {

                // Read the image
                //
                ImageInputStream inputStream = ImageIO.createImageInputStream(fileToRead);
                ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();
                imageReader.setInput(inputStream, true);

                // Read the metadata and search for the "map-representation"
                // previously save information
                //
                PNGMetadata metadata = (PNGMetadata) imageReader.getImageMetadata(0);
                Map<String, String> metadataMap = new HashMap<String, String>();
                NodeList childNodes = metadata.getStandardTextNode().getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    String keyword = node.getAttributes().getNamedItem("keyword").getNodeValue();
                    String value = node.getAttributes().getNamedItem("value").getNodeValue();
                    metadataMap.put(keyword, value);
                }
                String mapRepresentation = metadataMap.get("map-representation");

                // Create the map
                //
                Map4CT newMap = MapsGenerator.createMapFromTextRepresentation(mapRepresentation, -1);
                mapsGenerator.maps.add(newMap);
                mapsGenerator.todoList.add(newMap);
                map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
                map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

                // It behaves as the play (generation) action - At the end of
                // the generation thread
                //
                startElaborationButton.setDisable(mapsGenerator.todoList.size() == 0);
                pauseElaborationButton.setDisable(true);
                resetElaborationButton.setDisable(false);
                filterLessThanFourElaborationButton.setDisable(false);
                filterLessThanFiveElaborationButton.setDisable(false);
                filterLessThanFacesElaborationButton.setDisable(false);
                copyMapsToTodoElaborationButton.setDisable(false);
                createMapFromTextRepresentationButton.setDisable(false);
                getTextRepresentationOfCurrentMapButton.setDisable(false);
                loadMapFromAPreviouslySavedImageButton.setDisable(false);

                refreshInfo();
                drawCurrentMap();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @FXML
    private void drawMethodAction(ActionEvent event) {

        drawCurrentMap();

        if (drawMethodValue == DRAW_METHOD.RECTANGLES) {
            taitButton.setDisable(false);
        } else if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
            taitButton.setDisable(false);
        } else if (drawMethodValue == DRAW_METHOD.CIRCLES) {
            taitButton.setDisable(true);
        }
    }

    @FXML
    private void graphLayoutAction(ActionEvent event) {

        drawCurrentGraph(true);
    }

    /**
     * Set the Transparency value
     * 
     * @param value
     */
    public final void setTransparencyValue(int value) {

        // Read the transparency to use
        // FIXME: For a swixml2 bug
        // (http://code.google.com/p/swixml2/issues/detail?id=54) I needed to
        // change this and use setter and getter methods
        //
        drawCurrentMap();
        transparencyValue = value;
    }

    public final int getTransparencyValue() {
        return transparencyValue;
    }

    @FXML
    private void selectColorOneAction(ActionEvent event) {

        colorOne = selectColorOnePicker.getValue();
    }

    @FXML
    private void selectColorTwoAction(ActionEvent event) {

        colorTwo = selectColorTwoPicker.getValue();
    }

    @FXML
    private void selectColorThreeAction(ActionEvent event) {

        colorThree = selectColorThreePicker.getValue();
    }

    @FXML
    private void selectColorFourAction(ActionEvent event) {

        colorFour = selectColorFourPicker.getValue();
    }

    @FXML
    private void selectColorDefaultAction(ActionEvent event) {

        // Initialize colors = Some colors I liked ... instead of using RGBW
        //
        colorOne = Color.rgb(255, 99, 71);
        colorTwo = Color.rgb(50, 205, 50);
        colorThree = Color.rgb(238, 238, 0);
        colorFour = Color.rgb(176, 196, 222);

        selectColorOnePicker.setValue(colorOne);
        selectColorTwoPicker.setValue(colorTwo);
        selectColorThreePicker.setValue(colorThree);
        selectColorFourPicker.setValue(colorFour);
    }

    @FXML
    private void showFaceCardinalityAction(ActionEvent event) {

        drawCurrentMap();
    }

    @FXML
    private void taitAction(ActionEvent event) {

        // Read the original image from screen
        //
        WritableImage inputImageTemp = new WritableImage(gWindow.getCanvas().getWidth(), gWindow.getCanvas().getHeight());
        PixelReader inputImage = inputImageTemp.getPixelReader();

        // Create the image where the 3-edge-coloring has to be painted
        //
        WritableImage outputImageWritableImage = new WritableImage((int) inputImageTemp.getWidth(), (int) inputImageTemp.getHeight());
        PixelWriter outputImagePixelWriter = outputImageWritableImage.getPixelWriter();

        // Scan the image
        //
        Color previousColor = null;
        Color currentColor = null;
        Color nextColor = null;
        for (int iY = 1; iY < inputImageTemp.getHeight() - 1; iY++) {
            for (int iX = 1; iX < inputImageTemp.getWidth() - 1; iX++) {
                currentColor = inputImage.getColor(iX, iY);

                if (currentColor == Color.BLACK) {

                    // Y (Vertical) scan - previous = LEFT, next = RIGHT
                    //
                    previousColor = inputImage.getColor(iX - 1, iY);
                    nextColor = inputImage.getColor(iX + 1, iY);

                    if (((previousColor == colorOne) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorTwo))) {
                        outputImagePixelWriter.setColor(iX - 1, iY, Color.RED);
                        outputImagePixelWriter.setColor(iX, iY, Color.RED);
                        outputImagePixelWriter.setColor(iX + 1, iY, Color.RED);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorTwo)) || ((previousColor == colorTwo) && (nextColor == colorOne)) || ((previousColor == colorThree) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorThree))) {
                        outputImagePixelWriter.setColor(iX - 1, iY, Color.GREEN);
                        outputImagePixelWriter.setColor(iX, iY, Color.GREEN);
                        outputImagePixelWriter.setColor(iX + 1, iY, Color.GREEN);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorTwo))) {
                        outputImagePixelWriter.setColor(iX - 1, iY, Color.BLUE);
                        outputImagePixelWriter.setColor(iX, iY, Color.BLUE);
                        outputImagePixelWriter.setColor(iX + 1, iY, Color.BLUE);
                    }

                    // Y (Vertical) scan - previous = ABOVE, next = BELOW
                    //
                    previousColor = inputImage.getColor(iX, iY - 1);
                    nextColor = inputImage.getColor(iX, iY + 1);

                    if (((previousColor == colorOne) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorTwo))) {
                        outputImagePixelWriter.setColor(iX, iY - 1, Color.RED);
                        outputImagePixelWriter.setColor(iX, iY, Color.RED);
                        outputImagePixelWriter.setColor(iX, iY + 1, Color.RED);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorTwo)) || ((previousColor == colorTwo) && (nextColor == colorOne)) || ((previousColor == colorThree) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorThree))) {
                        outputImagePixelWriter.setColor(iX, iY - 1, Color.GREEN);
                        outputImagePixelWriter.setColor(iX, iY, Color.GREEN);
                        outputImagePixelWriter.setColor(iX, iY + 1, Color.GREEN);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorTwo))) {
                        outputImagePixelWriter.setColor(iX, iY - 1, Color.BLUE);
                        outputImagePixelWriter.setColor(iX, iY, Color.BLUE);
                        outputImagePixelWriter.setColor(iX, iY + 1, Color.BLUE);
                    }
                } else {
                    outputImagePixelWriter.setColor(iX, iY, Color.WHITE);
                }
            }
        }

        // Set the image into the Label
        //
        Label taitImagelabel = new Label();
        taitImagelabel.setGraphic(new ImageView(outputImageWritableImage));

        // Prepare the new Window
        //
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(taitImagelabel);
        Scene secondScene = new Scene(secondaryLayout, 200, 100);

        Stage secondStage = new Stage();
        secondStage.setScene(secondScene);
        secondStage.show();
    }

    @FXML
    private void spiralChainAction(ActionEvent event) {

        if (graph4CTCurrent != null) {
            drawCurrentGraph(true);
            graph4CTCurrent.drawSpiralChain(startingVertexTextField.getText(), autoColor.isSelected());
        }
    }

    @FXML
    private void clearSpiralChainAction(ActionEvent event) {

        if (map4CTCurrent != null) {
            drawCurrentGraph(true);
        }
    }

    @FXML
    private void stopRemovingIsoAction(ActionEvent event) {

        // Stop removing isomorphic maps (request the stop to the thread)
        //
        mapsGenerator.stopRemovingIsoRequested = true;
    }

    @FXML
    private void removeIsoMapsAction(ActionEvent event) {

        // Stop already running threads
        //
        mapsGenerator.stopRemovingIsoRequested = true;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the flag to stop the work
        //
        mapsGenerator.stopRemovingIsoRequested = false;

        // Create the thread ... if not created
        // Execute the thread ... if not already running
        //
        // Note: A java thread cannot be reused ... even if it terminated
        // correctly
        //
        if (removeIsoThread == null) {
            removeIsoThread = new Thread(runnableRemoveIsoMaps);
            removeIsoThread.start();
        } else if (removeIsoThread.isAlive() == false) {
            removeIsoThread = new Thread(runnableRemoveIsoMaps);
            removeIsoThread.start();
        }
    }

    @FXML
    private void removeIsoTodoListAction(ActionEvent event) {

        // Stop already running threads
        //
        mapsGenerator.stopRemovingIsoRequested = true;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the flag to stop the work
        //
        mapsGenerator.stopRemovingIsoRequested = false;

        // Create the thread ... if not created
        // Execute the thread ... if not already running
        //
        // Note: A java thread cannot be reused ... even if it terminated
        // correctly
        //
        if (removeIsoThread == null) {
            removeIsoThread = new Thread(runnableRemoveIsoTodoList);
            removeIsoThread.start();
        } else if (removeIsoThread.isAlive() == false) {
            removeIsoThread = new Thread(runnableRemoveIsoTodoList);
            removeIsoThread.start();
        }
    }

    @FXML
    private void saveMapToImageAction(ActionEvent event) {

        String fileName = null;
        String drawMethodName = "unknown";

        if (drawMethodValue == DRAW_METHOD.RECTANGLES) {
            drawMethodName = "rectangles";
        } else if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
            drawMethodName = "rectangles_new_york";
        } else if (drawMethodValue == DRAW_METHOD.CIRCLES) {
            drawMethodName = "circles";
        }

        if (map4CTCurrent != null) {
            fileName = "save-" + drawMethodName + "-" + map4CTCurrent.hashCode() + ".png";
        } else {
            fileName = "save-" + drawMethodName + "-" + "000" + ".png";
        }

        try {
            File fileToSave = null;

            fileChooser.setInitialDirectory(new File(fileName));
            fileChooser.showOpenDialog(theStage);

            if ((fileToSave = fileChooser.showOpenDialog(theStage)) != null) {

                // Write the image to memory
                //
                WritableImage image = graphExplorerSwingNode.snapshot(null, null);

                // Create & populate png metadata
                //
                PNGMetadata metadata = new PNGMetadata();
                metadata.tEXt_keyword.add("map-representation");
                metadata.tEXt_text.add(map4CTCurrent.toString());

                // Write the image to file and close the file stream
                //
                FileOutputStream imageOutputStream = new FileOutputStream(fileToSave);
                ImageWriter writer = (ImageWriter) ImageIO.getImageWritersBySuffix("png").next();
                writer.setOutput(ImageIO.createImageOutputStream(imageOutputStream));
                writer.write(new IIOImage(SwingFXUtils.fromFXImage(image, null), null, metadata));
                imageOutputStream.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void saveGraphToImageAction(ActionEvent event) {

        if (graph4CTCurrent != null) {
            try {

                File fileToSave = null;

                String fileName = "save-graph-" + graph4CTCurrent.hashCode() + ".png";
                fileChooser.setInitialDirectory(new File(fileName));
                fileChooser.showOpenDialog(theStage);

                if ((fileToSave = fileChooser.showOpenDialog(theStage)) != null) {

                    // Write the image to memory
                    //
                    WritableImage image = graphExplorerSwingNode.snapshot(null, null);

                    // Create & populate png metadata
                    //
                    PNGMetadata metadata = new PNGMetadata();
                    metadata.tEXt_keyword.add("map-representation");
                    metadata.tEXt_text.add(map4CTCurrent.toString());

                    // Write the image to file and close the file stream
                    //
                    FileOutputStream imageOutputStream = new FileOutputStream(fileToSave);
                    ImageWriter writer = (ImageWriter) ImageIO.getImageWritersBySuffix("png").next();
                    writer.setOutput(ImageIO.createImageOutputStream(imageOutputStream));
                    writer.write(new IIOImage(SwingFXUtils.fromFXImage(image, null), null, metadata));
                    imageOutputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @FXML
    private void saveAllMapsAndTodoListAction(ActionEvent event) {

        try {

            File fileToSave = null;

            String fileName = "save-all-maps.serialized";
            fileChooser.setInitialDirectory(new File(fileName));
            fileChooser.showOpenDialog(theStage);

            if ((fileToSave = fileChooser.showOpenDialog(theStage)) != null) {

                // Write the image to memory
                //
                File fileToSaveMaps = new File(fileToSave.getAbsolutePath() + ".maps");
                File fileToSaveTodoList = new File(fileToSave.getAbsolutePath() + ".todoList");

                // Write the maps to the output file and close the file stream
                //
                FileOutputStream outputStreamMaps = new FileOutputStream(fileToSaveMaps);
                ObjectOutputStream objectOutputStreamMaps = new ObjectOutputStream(outputStreamMaps);
                objectOutputStreamMaps.writeObject(mapsGenerator.maps);
                objectOutputStreamMaps.flush();
                outputStreamMaps.close();

                FileOutputStream outputStreamTodoList = new FileOutputStream(fileToSaveTodoList);
                ObjectOutputStream objectOutputStreamTodoList = new ObjectOutputStream(outputStreamTodoList);
                objectOutputStreamTodoList.writeObject(mapsGenerator.todoList);
                objectOutputStreamTodoList.flush();
                outputStreamTodoList.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    private void loadAllMapsAction(ActionEvent event) {

        try {
            File fileToRead = null;

            // Select the file to load
            //
            if ((fileToRead = fileChooser.showOpenDialog(theStage)) != null) {

                // Read the maps
                //
                FileInputStream inputStreamMaps = new FileInputStream(fileToRead);
                ObjectInputStream objectInputStreamMaps = new ObjectInputStream(inputStreamMaps);
                mapsGenerator.maps = (ArrayList<Map4CT>) objectInputStreamMaps.readObject();
                inputStreamMaps.close();

                // setInvariantToSpeedIsoCheck
                // TODO: This one can be removed, because there was a bug in the java program that did not consider the ocean for the invariantToSpeedIsoCheck (saved maps may have the problem, and this will fix it)
                //
                for (int iMap = 0; iMap < mapsGenerator.maps.size(); iMap++) {
                    mapsGenerator.maps.get(iMap).setInvariantToSpeedIsoCheck();
                }

                // It behaves as the play (generation) action - At the end of the generation thread
                //
                filterLessThanFourElaborationButton.setDisable(false);
                filterLessThanFiveElaborationButton.setDisable(false);
                filterLessThanFacesElaborationButton.setDisable(false);
                copyMapsToTodoElaborationButton.setDisable(false);

                refreshInfo();
                drawCurrentMap();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    private void loadAllTodoListAction(ActionEvent event) {

        try {

            File fileToRead = null;

            // Select the file to load
            //
            if ((fileToRead = fileChooser.showOpenDialog(theStage)) != null) {

                // Read the todoList
                //
                FileInputStream inputStreamTodoList = new FileInputStream(fileToRead);
                ObjectInputStream objectInputStreamTodoList = new ObjectInputStream(inputStreamTodoList);
                mapsGenerator.todoList = (ArrayList<Map4CT>) objectInputStreamTodoList.readObject();
                inputStreamTodoList.close();

                // setInvariantToSpeedIsoCheck
                // TODO: This one can be removed, because there was a bug in the java program that did not consider the ocean for the invariantToSpeedIsoCheck (saved maps may have the problem, and this will fix it)
                //
                for (int iMap = 0; iMap < mapsGenerator.todoList.size(); iMap++) {
                    mapsGenerator.todoList.get(iMap).setInvariantToSpeedIsoCheck();
                }

                // It behaves as the play (generation) action - At the end of the generation thread
                //
                startElaborationButton.setDisable(mapsGenerator.todoList.size() == 0);
                pauseElaborationButton.setDisable(true);
                resetElaborationButton.setDisable(false);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void saveGraphToGraphMLAction(ActionEvent event) {

        if (graph4CTCurrent != null) {
            saveGraphToGraphML(graph4CTCurrent);
        }
    }

    public void saveGraphToGraphML(Graph4CT graphToSave) {
        try {
            File fileToSave = null;

            String fileName = "save-graph-" + graphToSave.hashCode() + ".graphml";
            fileChooser.setInitialDirectory(new File(fileName));

            if ((fileToSave = fileChooser.showOpenDialog(theStage)) != null) {

                FileWriter fileToSaveWriter = new FileWriter(fileToSave);
                GraphMLExporter<String, DefaultEdge> graphMLExporter = new GraphMLExporter<String, DefaultEdge>();
                graphMLExporter.export(fileToSaveWriter, graphToSave.getJGraphT());
                fileToSaveWriter.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * TODO: For now is disables. Find a way to choose the directory
     */
    @FXML
    private void saveAllGraphToGraphMLAction(ActionEvent event) {

        // If the map list is not empty
        //
        if (mapsGenerator.maps.size() != 0) {

            File selectedDirectory = null;

            // Select a directory
            //
            if ((selectedDirectory = directoryChooser.showDialog(theStage)) != null) {

                // Loop all graphs (to save all of them)
                //
                for (int iMap = 0; iMap < mapsGenerator.maps.size(); iMap++) {

                    // Move to map
                    //
                    map4CTCurrentIndex = iMap;
                    map4CTCurrent = mapsGenerator.maps.get(iMap);

                    // Draw the graph
                    //
                    drawCurrentGraph(false);

                    // And save the graph
                    //
                    String fileName = selectedDirectory.getAbsolutePath() + "/save-graph-" + (map4CTCurrent.faces.size() + 1) + "-" + map4CTCurrent.hashCode() + ".graphml";
                    File fileToSave = new File(fileName);
                    FileWriter fileToSaveWriter;
                    try {
                        fileToSaveWriter = new FileWriter(fileToSave);
                        GraphMLExporter<String, DefaultEdge> graphMLExporter = new GraphMLExporter<String, DefaultEdge>();
                        graphMLExporter.export(fileToSaveWriter, graph4CTCurrent.getJGraphT());
                        fileToSaveWriter.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @FXML
    private void refreshInfoAction(ActionEvent event) {

        refreshInfo();
    }

    @FXML
    private void colorItAction(ActionEvent event) {

        // Stop auto coloring if running. It waits few millesec to be sure the
        // thread understands the request
        //
        stopColoringRequested = true;
        try {
            Thread.sleep(120);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the stop auto color it request
        //
        stopColoringRequested = false;

        // Create the thread ... if not created
        // If previously executed and not running, create a new thread
        //
        // Note: A java thread cannot be reused ... even if it terminated
        // correctly
        //
        if (colorAllThread == null) {
            colorAllThread = new Thread(runnableColorIt);
            colorAllThread.start();
        } else if (colorAllThread.isAlive() == false) {
            colorAllThread = new Thread(runnableColorIt);
            colorAllThread.start();
        }
    }

    @FXML
    private void colorAllAction(ActionEvent event) {

        // Stop auto coloring if running. It waits few millesec to be sure the
        // thread understands the request
        //
        stopColoringRequested = true;
        try {
            Thread.sleep(120);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the stop auto color it request ("X" button)
        //
        stopColoringRequested = false;

        // Create the thread ... if not created
        // Execute the thread ... if not already running
        //
        // Note: A java thread cannot be reused ... even if it terminated
        // correctly
        //
        if (colorItThread == null) {
            colorItThread = new Thread(runnableColorAll);
            colorItThread.start();
        } else if (colorItThread.isAlive() == false) {
            colorItThread = new Thread(runnableColorAll);
            colorItThread.start();
        }
    }

    @FXML
    private void stopColorAllAction(ActionEvent event) {

        // Stop auto coloring if running. It waits few millesec to be sure the
        // thread understands the request
        //
        stopColoringRequested = true;
        try {
            Thread.sleep(120);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset coloring of the map
        // If a map was already shown, I need to redraw it
        //
        if (map4CTCurrent != null) {
            map4CTCurrent.resetColors();
            drawCurrentMap();
        }
    }

    @FXML
    private void drawFirstGraphAction(ActionEvent event) {

        // Start the work
        //
        if (mapsGenerator.maps.size() != 0) {

            // Move to the first map
            //
            map4CTCurrentIndex = 0;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            // Draw the graph
            //
            drawCurrentGraph(true);
        }
    }

    @FXML
    private void drawPreviousGraphAction(ActionEvent event) {

        // Start the work
        //
        if ((mapsGenerator.maps.size() != 0) && (map4CTCurrentIndex > 0)) {

            // Move to the previous map
            //
            map4CTCurrentIndex = map4CTCurrentIndex - 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            // Draw the graph
            //
            drawCurrentGraph(true);
        }
    }

    @FXML
    private void drawRandomGraphAction(ActionEvent event) {

        // Start the work
        //
        if (mapsGenerator.maps.size() != 0) {

            // Random map
            //
            map4CTCurrentIndex = new Random().nextInt(mapsGenerator.maps.size());
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            // Draw the graph
            //
            drawCurrentGraph(true);
        }
    }

    @FXML
    private void drawNextGraphAction(ActionEvent event) {

        // Start the work
        //
        if ((mapsGenerator.maps.size() != 0) && (map4CTCurrentIndex < (mapsGenerator.maps.size() - 1))) {

            // Move to the previous map
            //
            map4CTCurrentIndex = map4CTCurrentIndex + 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            // Draw the graph
            //
            drawCurrentGraph(true);
        }
    }

    @FXML
    private void drawLastGraphAction(ActionEvent event) {

        // Start the work
        //
        if (mapsGenerator.maps.size() != 0) {

            // Move to the previous map
            //
            map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            // Draw the graph
            //
            drawCurrentGraph(true);
        }
    }

    @FXML
    private void secondColorOfKempeSwitchAction(ActionEvent event) {

        // Read the second color of the chain
        //
        if (secondColorOfKempeSwitch.getValue().compareTo("RED") == 0) {
            graph4CTCurrent.setSecondColorOfKempeSwitch(new java.awt.Color((int) Color.RED.getRed(), (int) Color.RED.getGreen(), (int) Color.RED.getBlue()));
        } else if (secondColorOfKempeSwitch.getValue().compareTo("GREEN") == 0) {
            drawMethodValue = DRAW_METHOD.RECTANGLES;
        } else {
            graph4CTCurrent.setSecondColorOfKempeSwitch(new java.awt.Color((int) Color.BLUE.getRed(), (int) Color.BLUE.getGreen(), (int) Color.BLUE.getBlue()));
        }
    }

    /**
     * Create the graph (update graph4CTCurrent) from the sequence of coordinates of the current map: 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
     */
    public void drawCurrentGraph(boolean validateTheGraphExplorerPanel) {

        // If a map has been selected
        //
        // NOTE
        // if (map4CTCurrent.numberOfFWithGivenCardinality(2) != 0) {
        // Message = "The current graph has multiple edges (faces with 2 edges));
        // }
        //
        if (map4CTCurrent == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Map is not currently set");
        } else {

            // Re-init and redraw the graph
            //
            // xxx initGraphExplorerForGraphic();
            graph4CTCurrent.drawGraph(map4CTCurrent);

            // Update or reset the scale according to the user flag (swing
            // checkbox)
            //
            if (graph4CTCurrent.getGraphLayouts() != Graph4CT.GRAPH_LAYOUT.RECTANGULAR) {
                if (fitToWindow.isSelected()) {
                    graph4CTCurrent.scaleToFitTheWindow();
                } else {
                    graph4CTCurrent.scaleToDefault();
                }
            }

            // Draw the spiral chain
            //
            if (autoSpiral.isSelected()) {
                graph4CTCurrent.drawSpiralChain(startingVertexTextField.getText(), autoColor.isSelected());
            }
        }
    }

    /**
     * Color a single map
     */
    public void colorIt() {

        // Local variables
        //
        boolean endOfJob = false;
        boolean colorFound = false;
        boolean moveBackOneFace = false;
        int currentFaceIndex = 0; // 0 is the first face = central face
        F faceToAnalyze = null;
        ColorPalette colorsFacingTheOcean = new ColorPalette(false); // When it reaches 4 before to add the ocean, the algorithm can break sooner
        List<ColorPalette> mapsPalette = new ArrayList<ColorPalette>();
        List<F> facesWithPinnedColor = new ArrayList<F>();

        // If a map is set
        //
        if (map4CTCurrent != null) {

            // Since this is a separated thread I cannot rely on the
            // map4CTCurrent
            //
            Map4CT mapBeingColored = map4CTCurrent;

            // Prepare and reset palette for all faces and redraw it
            //
            for (int i = 0; i < mapBeingColored.faces.size(); i++) {
                if (mapBeingColored.faces.get(i).color != COLORS.UNCOLORED) { // Pinned
                                                                              // color
                    mapsPalette.add(new ColorPalette(mapBeingColored.faces.get(i).color)); // Set
                                                                                           // a
                                                                                           // palette
                                                                                           // with
                                                                                           // one
                                                                                           // color
                                                                                           // (pinned)
                    facesWithPinnedColor.add(mapBeingColored.faces.get(i));
                } else {
                    mapsPalette.add(new ColorPalette(true)); // Set all four
                                                             // colors
                    mapBeingColored.faces.get(i).color = COLORS.UNCOLORED;
                }
            }

            // Draw the map
            //
            drawCurrentMap();

            // Start from the center
            //
            faceToAnalyze = mapBeingColored.faces.get(0);

            // While not end of job (loop all faces)
            //
            while (!endOfJob && !stopColoringRequested) {

                // Reset colorFound and moveBackOneFace
                //
                colorFound = false;
                moveBackOneFace = false;

                // Try the four colors
                //
                while (!colorFound && !moveBackOneFace) {

                    // Pick a color and remove it the the reusable colors
                    //
                    if (mapsPalette.get(currentFaceIndex).palette.size() == 0) { // Not
                                                                                 // correctly
                                                                                 // colored
                                                                                 // and
                                                                                 // I
                                                                                 // tried
                                                                                 // all
                                                                                 // colors

                        // Move to the previous face
                        //
                        moveBackOneFace = true;
                        mapsPalette.get(currentFaceIndex).setToFull();
                        if (mapsPalette.get(currentFaceIndex).isPinned == false) {
                            faceToAnalyze.color = COLORS.UNCOLORED;
                        }
                        if (mapBeingColored.isFaceFacingTheOcean(currentFaceIndex + 1) == true) {
                            colorsFacingTheOcean.palette.pop(); // Throw away
                                                                // the last
                                                                // color
                                                                // inserted
                        }
                        currentFaceIndex--;
                    } else {

                        // Try a new color
                        //
                        faceToAnalyze.color = mapsPalette.get(currentFaceIndex).palette.pop();

                        // Update the list of colors already facing the ocean
                        // (if the face is facing the ocean)
                        //
                        if (mapBeingColored.isFaceFacingTheOcean(currentFaceIndex + 1) == true) {
                            if (colorsFacingTheOcean.palette.contains(faceToAnalyze.color) == false) {
                                colorsFacingTheOcean.palette.add(faceToAnalyze.color);
                            }
                        }

                        // Check if map is correctly four colored respect to
                        // previous neighbors
                        //
                        if (mapBeingColored.isFaceCorrectlyColoredRespectToPreviousNeighbors(faceToAnalyze) == true) {

                            // Check also if it is correctly colored considering
                            // the ocean (the ocean is not a face. Is treated as
                            // a special face)
                            //
                            if (colorsFacingTheOcean.palette.size() < 4) {

                                // Check also the coloring of pinned faces
                                // (hence: check coloring respect to pinned)
                                //
                                boolean correctColoredRespectToPinned = true;
                                for (int i = 0; (i < facesWithPinnedColor.size()) && (correctColoredRespectToPinned == true); i++) {

                                    if (currentFaceIndex < (facesWithPinnedColor.get(i).id - 1)) {
                                        if (mapBeingColored.isFaceCorrectlyColoredRespectToPreviousNeighbors(facesWithPinnedColor.get(i)) == false) {
                                            correctColoredRespectToPinned = false;
                                        }
                                    }
                                }

                                // If also the coloring of pinned faces is
                                // correct
                                //
                                if (correctColoredRespectToPinned) {

                                    // Move to the next face
                                    //
                                    colorFound = true;
                                    currentFaceIndex++;
                                }
                            }
                        }
                    }

                    // Set the instruments
                    //
                    // NOTE: I hope that changing the instruments too often
                    // won't be a problem
                    //
                    changeInstruments();

                    // Play sounds
                    // If midi note goes out of range 0-127, it it set to
                    // maximum (or it would not play any sound)
                    //
                    if (soundWhileColoring.isSelected()) {
                        if (faceToAnalyze.color == COLORS.ONE) {
                            Integer note = Integer.parseInt(colorOneBaseNoteTextField.getText()) + currentFaceIndex;
                            if (note > 127) {
                                note = 127;
                            }
                            midiChannels[0].noteOn(note, Integer.parseInt(colorOneBaseVelocityTextField.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorOneBaseDurationTextField.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[0].noteOff(note);
                        } else if (faceToAnalyze.color == COLORS.TWO) {
                            Integer note = Integer.parseInt(colorTwoBaseNoteTextField.getText()) + currentFaceIndex;
                            if (note > 127) {
                                note = 127;
                            }
                            midiChannels[1].noteOn(note, Integer.parseInt(colorTwoBaseVelocityTextField.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorTwoBaseDurationTextField.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[1].noteOff(note);
                        } else if (faceToAnalyze.color == COLORS.THREE) {
                            Integer note = Integer.parseInt(colorThreeBaseNoteTextField.getText()) + currentFaceIndex;
                            midiChannels[2].noteOn(note, Integer.parseInt(colorThreeBaseVelocityTextField.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorThreeBaseDurationTextField.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[2].noteOff(note);
                        } else if (faceToAnalyze.color == COLORS.FOUR) {
                            Integer note = Integer.parseInt(colorFourBaseNoteTextField.getText()) + currentFaceIndex;
                            midiChannels[3].noteOn(note, Integer.parseInt(colorFourBaseVelocityTextField.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorFourBaseDurationTextField.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[3].noteOff(note);
                        }
                    }
                }

                // Check if end of job
                //
                if (currentFaceIndex == mapBeingColored.faces.size()) {
                    endOfJob = true;
                } else if (mapBeingColored != map4CTCurrent) {
                    endOfJob = true;
                } else {
                    try {
                        faceToAnalyze = mapBeingColored.faces.get(currentFaceIndex);
                    } catch (Exception e) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error");
                        alert.setContentText("OFour coloring not possible with these pinned colors");
                    }
                }

                // Draw the map
                //
                drawCurrentMap();
            }
        }
    }

    /**
     * Color all maps
     */
    public void colorAll() {
        if ((mapsGenerator.maps.size() != 0) && (map4CTCurrent != null)) {

            // Start from current position to the end
            //
            for (int map4CTCurrentIndexTemp = map4CTCurrentIndex; (map4CTCurrentIndexTemp < mapsGenerator.maps.size()) && (stopColoringRequested == false); map4CTCurrentIndexTemp++) {
                map4CTCurrentIndex = map4CTCurrentIndexTemp;
                map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);
                colorIt();
            }
        }
    }

    /**
     * Refresh runtime info
     */
    public synchronized void refreshInfo() {
        currentMapTextField.setText("" + (map4CTCurrentIndex + 1));
        mapsSizeTextField.setText("" + mapsGenerator.maps.size());
        mapsRemovedTextField.setText("" + mapsGenerator.numberOfRemovedMaps);
        todoListSizeTextField.setText("" + mapsGenerator.todoList.size());

        currentMapTextField2.setText("" + (map4CTCurrentIndex + 1));
        mapsSizeTextField2.setText("" + mapsGenerator.maps.size());
        mapsRemovedTextField2.setText("" + mapsGenerator.numberOfRemovedMaps);
        todoListSizeTextField2.setText("" + mapsGenerator.todoList.size());
    }

    /**
     * Refresh runtime info
     */
    public synchronized void refreshMemoryInfo() {

        // Sets:
        //
        // Get current size of heap in bytes
        // Get maximum size of heap in bytes. The heap cannot grow beyond this
        // size. Any attempt will result in an OutOfMemoryException
        // Get amount of free memory within the heap in bytes. This size will
        // increase after garbage collection and decrease as new objects are
        // created.
        //
        totalMemoryTextField.setText("" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " Mb");
        maxMemoryTextField.setText("" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " Mb");
        freeMemoryTextField.setText("" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " Mb");

        totalMemoryTextField2.setText("" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " Mb");
        maxMemoryTextField2.setText("" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " Mb");
        freeMemoryTextField2.setText("" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " Mb");
    }

    /**
     * Draw current map (or reset graph if map4CTCurrent is null)
     */
    public synchronized void drawCurrentMap() {

        // Read the draw method
        //
        if (drawMethod.getValue().compareTo("CIRCLES") == 0) {
            drawMethodValue = DRAW_METHOD.CIRCLES;
        } else if (drawMethod.getValue().compareTo("RECTANGLES") == 0) {
            drawMethodValue = DRAW_METHOD.RECTANGLES;
        } else {
            drawMethodValue = DRAW_METHOD.RECTANGLES_NEW_YORK;
        }

        // If a map was already shown, I need to redraw it
        //
        if (map4CTCurrent != null) {

            // Clean the scene
            //
            gScene.removeAll();

            // Circle mode or rectangular mode
            //
            if (drawMethodValue != DRAW_METHOD.CIRCLES) {
                GMap4CTRectangles gMap4CTRectangles = new GMap4CTRectangles(map4CTCurrent);
                gScene.add(gMap4CTRectangles);
                gMap4CTRectangles.draw();
            } else {
                GMap4CTCircles gMap4CTCirlces = new GMap4CTCircles(map4CTCurrent);
                gScene.add(gMap4CTCirlces);
                gMap4CTCirlces.draw();
            }

            // Draw & Refresh
            //
            gScene.refresh();
        } else {
            gScene.removeAll();
            gScene.refresh();
        }
    }

    /**
     * Draw current map in slow motion mode
     */
    public synchronized void drawCurrentMapSlowMotion() {

        // Read the draw method
        //
        if (drawMethod.getValue().compareTo("CIRCLES") == 0) {
            drawMethodValue = DRAW_METHOD.CIRCLES;
        } else if (drawMethod.getValue().compareTo("RECTANGLES") == 0) {
            drawMethodValue = DRAW_METHOD.RECTANGLES;
        } else {
            drawMethodValue = DRAW_METHOD.RECTANGLES_NEW_YORK;
        }

        // If a map was already shown, I need to redraw it
        //
        if (map4CTCurrent != null) {

            // Clean the scene
            //
            gScene.removeAll();

            // Circle mode or rectangular mode
            //
            if (drawMethodValue != DRAW_METHOD.CIRCLES) {
                GMap4CTRectangles gMap4CTRectangles = new GMap4CTRectangles(map4CTCurrent);
                gScene.add(gMap4CTRectangles);

                for (int i = 0; i < map4CTCurrent.faces.size(); i++) {
                    gMap4CTRectangles.drawN(i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Draw & Refresh
                    //
                    gScene.refresh();
                }
            } else {
                GMap4CTCircles gMap4CTCircles = new GMap4CTCircles(map4CTCurrent);
                gScene.add(gMap4CTCircles);

                for (int i = 0; i < map4CTCurrent.faces.size(); i++) {
                    gMap4CTCircles.drawN(i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Draw & Refresh
                    //
                    gScene.refresh();
                }
            }
        } else {
            gScene.removeAll();
            gScene.refresh();
        }
    }

    /**
     * Play sounds: set or change the selected instruments
     */
    public void changeInstruments() {
        midiChannels[0].programChange(colorOneInstrument.getSelectionModel().getSelectedIndex());
        midiChannels[1].programChange(colorTwoInstrument.getSelectionModel().getSelectedIndex());
        midiChannels[2].programChange(colorThreeInstrument.getSelectionModel().getSelectedIndex());
        midiChannels[3].programChange(colorFourInstrument.getSelectionModel().getSelectedIndex());
    };

    /**
     * Set the object style from the Face parameters
     * 
     * @param face
     *            The face from which to read the parameters
     * @return The new style (G library)
     */
    public GStyle gStyleFromFace(F face) {

        // Local variables
        //
        Color colorToUse = null;
        GStyle faceStyle = new GStyle();

        // Read the color to use
        //
        // ONE = tomato1 = 255, 99, 71
        // TWO = limegreen = 50, 205, 50
        // THREE = yellow 2 = 238, 238, 0
        // FOUR = lightsteelblue = 176, 196, 222
        //
        if (face.color == COLORS.UNCOLORED) {
            colorToUse = Color.rgb(255, 255, 255, transparency.getValue());
        } else if (face.color == COLORS.ONE) {
            colorToUse = Color.color(colorOne.getRed(), colorOne.getGreen(), colorOne.getBlue(), transparency.getValue());
        } else if (face.color == COLORS.TWO) {
            colorToUse = Color.color(colorTwo.getRed(), colorTwo.getGreen(), colorTwo.getBlue(), transparency.getValue());
        } else if (face.color == COLORS.THREE) {
            colorToUse = Color.color(colorThree.getRed(), colorThree.getGreen(), colorThree.getBlue(), transparency.getValue());
        } else if (face.color == COLORS.FOUR) {
            colorToUse = Color.color(colorFour.getRed(), colorFour.getGreen(), colorFour.getBlue(), transparency.getValue());
        }

        // Set the style of this object (TBV: Cast to int)
        //
        faceStyle.setForegroundColor(new java.awt.Color(0, 0, 0));
        faceStyle.setBackgroundColor(new java.awt.Color((int) colorToUse.getRed(), (int) colorToUse.getGreen(), (int) colorToUse.getBlue()));
        faceStyle.setLineWidth(LINE_WIDTH);

        // Return the style
        //
        return faceStyle;
    }

    /**
     * Refresh runtime iso info
     */
    public synchronized void refreshIsoInfo() {
        isoOuterLoopTextField.setText("" + (mapsGenerator.isoOuterLoop + 1));
        isoInnerLoopTextField.setText("" + (mapsGenerator.isoInnerLoop + 1));
        isoRemovedTextField.setText("" + mapsGenerator.isoRemoved);
    }

    /**
     * Handle mouse events
     */
    public void event(GScene scene, int event, int x, int y) {

        // Color a face
        //
        if (event == GWindow.BUTTON1_DOWN) {

            // Find the face pointed by the mouse
            //
            GSegment interactionSegment = scene.findSegment(x, y);

            // If the mouse was pointed to a face
            //
            if (interactionSegment != null) {

                // The color to use
                //
                Color colorToUse = null;

                // Read the color to use
                //
                F face = (F) interactionSegment.getUserData();

                // Rotate colors and change it
                // If a color has already be used by the neighbours ... don't
                // use it
                //
                // FIXME: neighbors --> List of neighbors of the previous levels
                //
                boolean colorFound = false;
                COLORS originalColor = face.color;
                for (int i = 0; (i < 4) && (colorFound == false); i++) {

                    if (face.color == COLORS.UNCOLORED) {
                        colorFound = !isTheColorInTheNeighood(face, COLORS.ONE);
                        colorToUse = new Color(colorOne.getRed(), colorOne.getGreen(), colorOne.getBlue(), transparency.getValue());
                        face.color = COLORS.ONE;
                    } else if (face.color == COLORS.ONE) {
                        colorFound = !isTheColorInTheNeighood(face, COLORS.TWO);
                        colorToUse = new Color(colorTwo.getRed(), colorTwo.getGreen(), colorTwo.getBlue(), transparency.getValue());
                        face.color = COLORS.TWO;
                    } else if (face.color == COLORS.TWO) {
                        colorFound = !isTheColorInTheNeighood(face, COLORS.THREE);
                        colorToUse = new Color(colorThree.getRed(), colorThree.getGreen(), colorThree.getBlue(), transparency.getValue());
                        face.color = COLORS.THREE;
                    } else if (face.color == COLORS.THREE) {
                        colorFound = !isTheColorInTheNeighood(face, COLORS.FOUR);
                        colorToUse = new Color(colorFour.getRed(), colorFour.getGreen(), colorFour.getBlue(), transparency.getValue());
                        face.color = COLORS.FOUR;
                    } else if (face.color == COLORS.FOUR) {
                        colorToUse = new Color(255, 255, 255, transparency.getValue());
                        face.color = COLORS.UNCOLORED;
                    }
                }

                // If a color has already be used by the neighbors ... don't
                // use it
                // if I tried all possibilities ... reset
                //
                if (colorFound == false) {
                    face.color = originalColor;
                }

                // Set the style of this object
                //
                GStyle style = new GStyle();
                style.setForegroundColor(new java.awt.Color(0, 0, 0));
                style.setBackgroundColor(new java.awt.Color((int) colorToUse.getRed(), (int) colorToUse.getGreen(), (int) colorToUse.getBlue()));
                style.setLineWidth(LINE_WIDTH);
                interactionSegment.setStyle(style);
                scene.refresh();
            }
        }
    }

    private boolean isTheColorInTheNeighood(F face, COLORS colorToCheck) {
        boolean colorFound = false;

        for (int i = 0; ((i < face.neighbors.size()) && (colorFound == false)); i++) {
            if (colorToCheck == map4CTCurrent.faces.get(face.neighbors.get(i)).color) {
                colorFound = true;
            }
        }

        return colorFound;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Initialize file and directory chosers
        //
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Initialize the sound system and load all instruments
        // Check both if Java 1.5, 1.6, 1.7 and if it is running within a jar
        //
        // .sf2 are not supported for 1.5 e 1.6
        // .gm are no longer supported for 1.7
        //
        String soundbankName = null;
        String javaVersion = System.getProperty("java.version");
        System.out.println("Debug: java.version = " + javaVersion);
        if (javaVersion.startsWith("1.4")) {
            soundbankName = "config/soundbank-deluxe.gm";
        } else if (javaVersion.startsWith("1.5")) {
            soundbankName = "config/soundbank-deluxe.gm";
        } else if (javaVersion.startsWith("1.6")) {
            soundbankName = "config/soundbank-deluxe.gm";
        } else {
            soundbankName = "config/soundbank-vintage_dreams_waves.sf2";
        }
        try {
            URL soundbankURL = this.getClass().getClassLoader().getResource(soundbankName);
            if (soundbankURL.getProtocol().equals("jar")) {
                soundbank = MidiSystem.getSoundbank(soundbankURL);
            } else {
                File soundbankFile = new File(soundbankURL.toURI());
                soundbank = MidiSystem.getSoundbank(soundbankFile);
            }
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            synthesizer.loadAllInstruments(soundbank);
            instruments = synthesizer.getLoadedInstruments();
            midiChannels = synthesizer.getChannels();
        } catch (Exception e) {
            // TODO: handle exception
        }

        // Set instruments for the combos
        // I wasn't able to do it through JComboBox's constructor, because soundbanks cannot be closed and then re-opened
        //
        setInstrumentsNames(colorOneInstrument, instruments);
        setInstrumentsNames(colorTwoInstrument, instruments);
        setInstrumentsNames(colorThreeInstrument, instruments);
        setInstrumentsNames(colorFourInstrument, instruments);

        // Initialize colors = Some colors I liked ... instead of using RGBW
        //
        colorOne = Color.rgb(255, 99, 71);
        colorTwo = Color.rgb(50, 205, 50);
        colorThree = Color.rgb(238, 238, 0);
        colorFour = Color.rgb(176, 196, 222);

        selectColorOnePicker.setValue(colorOne);
        selectColorTwoPicker.setValue(colorTwo);
        selectColorThreePicker.setValue(colorThree);
        selectColorFourPicker.setValue(colorFour);

        // StartUp refresh manager (Runtime info, memory, etc.) - poller
        //
        new Thread(refreshManager).start();

        // Init Map Explorer and Graph Explorer
        //
        // xxx initMapExplorerForGraphic();
        // xxx initGraphExplorerForGraphic();
    }
}
