/*
 * Copyright 2012 by Mario Stefanutti, released under GPLv3.
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.png.PNGMetadata;

public class MapsGeneratorMain extends Application implements Initializable {

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

    // FXML Objects
    //
    @FXML private AnchorPane anchorPane;
    @FXML private TextField slowdownMillisecTextField;
    @FXML private CheckBox logWhilePopulate;
    @FXML private CheckBox randomElaboration;
    @FXML private CheckBox processAll;
    @FXML private ComboBox<String> maxMethod;
    @FXML private TextField maxNumberTextField;
    @FXML private Button startElaborationButton;
    @FXML private Button pauseElaborationButton;
    @FXML private Button filterLessThanFourElaborationButton;
    @FXML private Button filterLessThanFiveElaborationButton;
    @FXML private Button filterLessThanFacesElaborationButton;
    @FXML private Button copyMapsToTodoElaborationButton;
    @FXML private Button resetElaborationButton;
    @FXML private TextField mapTextRepresentationTextField;
    @FXML private Button createMapFromTextRepresentationButton;
    @FXML private Button getTextRepresentationOfCurrentMapButton;
    @FXML private Button loadMapFromAPreviouslySavedImageButton;
    @FXML private ComboBox<String> drawMethod;
    @FXML private Slider transparency;
    @FXML private Button selectColorOneButton;
    @FXML private Button selectColorTwoButton;
    @FXML private Button selectColorThreeButton;
    @FXML private Button selectColorFourButton;
    @FXML private Button selectColorDefaultActionButton;
    @FXML private CheckBox showFaceCardinality;
    @FXML private Button taitButton;
    @FXML private TextField currentMapTextField;
    @FXML private TextField mapsSizeTextField;
    @FXML private TextField mapsRemovedTextField;
    @FXML private TextField todoListSizeTextField;
    @FXML private TextField totalMemoryTextField;
    @FXML private TextField maxMemoryTextField;
    @FXML private TextField freeMemoryTextField;
    @FXML private CheckBox soundWhileColoring;
    @FXML private ComboBox<String> colorOneInstrument;
    @FXML private TextField colorOneBaseNoteTextField;
    @FXML private TextField colorOneBaseDurationTextField;
    @FXML private TextField colorOneBaseVelocityTextField;
    @FXML private ComboBox<String> colorTwoInstrument;
    @FXML private TextField colorTwoBaseNoteTextField;
    @FXML private TextField colorTwoBaseDurationTextField;
    @FXML private TextField colorTwoBaseVelocityTextField;
    @FXML private ComboBox<String> colorThreeInstrument;
    @FXML private TextField colorThreeBaseNoteTextField;
    @FXML private TextField colorThreeBaseDurationTextField;
    @FXML private TextField colorThreeBaseVelocityTextField;
    @FXML private ComboBox<String> colorFourInstrument;
    @FXML private TextField colorFourBaseNoteTextField;
    @FXML private TextField colorFourBaseDurationTextField;
    @FXML private TextField colorFourBaseVelocityTextField;
    @FXML private Pane mapExplorerPanel;

    @FXML private CheckBox fitToWindow;
    @FXML private ComboBox<String> graphLayout;
    @FXML private CheckBox autoSpiral;
    @FXML private TextField startingVertexTextField;

    // Other objects for the UI
    //
    private Color colorOne = null;
    private Color colorTwo = null;
    private Color colorThree = null;
    private Color colorFour = null;
    private Thread colorItThread = null;
    private Thread colorAllThread = null;
    private boolean stopColorAllRequested = false;
    private int transparencyValue = 255; // See swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54)
    private Soundbank soundbank = null;
    private Synthesizer synthesizer = null;
    private Instrument[] instruments = null;
    private MidiChannel[] midiChannels = null;
    private FileChooser fileChooser = null;

    // Graph, JGraphX and JGraphT objects
    //
    private Graph4CT graph4CTCurrent = null;
    private boolean stopFindIsomorphicGraphsRequested = false;
    private boolean stopHamiltonialCheckRequested = false;
    private Thread findIsoThread = null;

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
        @Override public void draw() {

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
        @Override public void draw() {

            // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
            //
            // Everything has to be normalized [0, 1]
            //
            double normalizationAngleFactor = map4CT.sequenceOfCoordinates.sequence.size();
            double spaceBetweenCircles = MAX_RADIUS / map4CT.faces.size();

            List<GraphicalObjectCoordinate> graphicalObjectCoordinates = new ArrayList<GraphicalObjectCoordinate>(map4CT.faces.size());
            for (int i = 0; i < map4CT.faces.size(); i++) {
                graphicalObjectCoordinates.add(new GraphicalObjectCoordinate());
            }

            // Computes graphical coordinates for all other Fs (not considering the first one)
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

                // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
                //
                // Everything has to be normalized [0, 1]
                //
                double normalizationAngleFactor = map4CT.sequenceOfCoordinates.sequence.size();
                double spaceBetweenCircles = MAX_RADIUS / map4CT.faces.size();

                List<GraphicalObjectCoordinate> graphicalObjectCoordinates = new ArrayList<GraphicalObjectCoordinate>(map4CT.faces.size());
                for (int i = 0; i < map4CT.faces.size(); i++) {
                    graphicalObjectCoordinates.add(new GraphicalObjectCoordinate());
                }

                // Computes graphical coordinates for all other Fs (not considering the first one)
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

            // |......||
            // |......|
            //
            // There are many ways to write down this formula. Pick yours, I decided for this one
            //
            double[] ring = new double[((internalPointsPerArc + 2) * 2 * 2) + 2];

            // Compute the 4 corners: the last one is set as the first one to close the path
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
            // for (pointX = 2; pointX < (internalPointsPerArc * 2) + 2; pointX += 2) {
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
            colorToUse = Color.color(255, 255, 255, (int) transparency.getValue());
        } else if (face.color == COLORS.ONE) {
            colorToUse = Color.color(colorOne.getRed(), colorOne.getGreen(), colorOne.getBlue(), (int) transparency.getValue());
        } else if (face.color == COLORS.TWO) {
            colorToUse = Color.color(colorTwo.getRed(), colorTwo.getGreen(), colorTwo.getBlue(), (int) transparency.getValue());
        } else if (face.color == COLORS.THREE) {
            colorToUse = Color.color(colorThree.getRed(), colorThree.getGreen(), colorThree.getBlue(), (int) transparency.getValue());
        } else if (face.color == COLORS.FOUR) {
            colorToUse = Color.color(colorFour.getRed(), colorFour.getGreen(), colorFour.getBlue(), (int) transparency.getValue());
        }

        // Set the style of this object
        //
        // xxx faceStyle.setForegroundColor(Color.black);
        // xxx faceStyle.setBackgroundColor(colorToUse);
        // xxx faceStyle.setLineWidth(LINE_WIDTH);

        // Return the style
        //
        return faceStyle;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception {

        // Read the UI from fxml
        //
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("config/4ct.fxml"));

        // Save the stage
        //
        theStage = stage;

        // Initialize the fileChooser to desktop
        //
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(fileSystemView.getRoots()[0]);

        // Initialize the sound system and load all instruments
        //
        URL soundbankURL = this.getClass().getClassLoader().getResource("config/soundbank-vintage_dreams_waves.sf2");
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

        // Set instruments for the combos (I wasn't able to do it through JComboBox's constructor, because soundbanks cannot be closed and then re-opened
        //
        setInstrumentsNames(colorOneInstrument, instruments);
        setInstrumentsNames(colorTwoInstrument, instruments);
        setInstrumentsNames(colorThreeInstrument, instruments);
        setInstrumentsNames(colorFourInstrument, instruments);

        // Initialize colors = Some colors I liked ... instead of using RGBW
        //
        colorOne = Color.color(255, 99, 71);
        colorTwo = Color.color(50, 205, 50);
        colorThree = Color.color(238, 238, 0);
        colorFour = Color.color(176, 196, 222);

        selectColorOneButton.setTextFill(colorOne);
        selectColorTwoButton.setTextFill(colorTwo);
        selectColorThreeButton.setTextFill(colorThree);
        selectColorFourButton.setTextFill(colorFour);
        // xxx selectColorOneButton.setStyle("-fx-background-color=red");

        // StartUp refresh manager (Runtime info, memory, etc.) - poller
        //
        new Thread(refreshManager).start();

        // Set the scene and show
        //
        stage.setTitle("4ct");
        stage.setScene(new Scene(root));
        stage.show();

        // Get visible
        //
        initMapExplorerForGraphic();
    }

    /**
     * Set the instruments name taken from an internet bank
     * 
     * @param jComboBox
     * @param instruments
     */
    public void setInstrumentsNames(ComboBox<String> comboBox, Instrument[] instruments) {
        ObservableList<String> data = FXCollections.observableArrayList("Color.RED", "Color.GREEN", "Color.BLUE");
        comboBox.getItems().addAll(data);
        // for (int i = 0; i < instruments.length; i++) {
        // System.out.println("Debug: " + instruments.length + "i = " + i);
        // // comboBox.getItems().add(instruments[i].getName());
        // comboBox.getItems().addAll("Color.RED", "Color.GREEN", "Color.BLUE");
        // }
    }

    /**
     * Utility class to refresh info about memory, etc.
     */
    private final Runnable refreshManager = new Runnable() {
        public void run() {

            while (true) {
                refreshInfo();
                refreshMemoryInfo();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    };

    /**
     * Refresh runtime info
     */
    public synchronized void refreshInfo() {
        currentMapTextField.setText("" + (map4CTCurrentIndex + 1));
        mapsSizeTextField.setText("" + mapsGenerator.maps.size());
        mapsRemovedTextField.setText("" + mapsGenerator.numberOfRemovedMaps);
        todoListSizeTextField.setText("" + mapsGenerator.todoList.size());
    }

    /**
     * Refresh runtime info
     */
    public synchronized void refreshMemoryInfo() {

        // Sets:
        //
        // Get current size of heap in bytes
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size. Any attempt will result in an OutOfMemoryException
        // Get amount of free memory within the heap in bytes. This size will increase after garbage collection and decrease as new objects are created.
        //
        totalMemoryTextField.setText("" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " Mb");
        maxMemoryTextField.setText("" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " Mb");
        freeMemoryTextField.setText("" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " Mb");
    }

    /**
     * Init the MapExplorer to visualize G graphics
     */
    public void initMapExplorerForGraphic() {

        // G lib initialization (link window canvas to JPanel)
        //
        // xxx gWindow = new GWindow(colorFour); // The background is the ocean, that is always colored with the fourth color ... or the 4ct would be wrong!
        gWindow = new GWindow();
        gScene = new GScene(gWindow);
        // xxx mapExplorerPanel.removeAll();
        // xxx mapExplorerPanel.add(gWindow.getCanvas());

        // Use a normalized world extent (adding a safety border)
        //
        // @param x0 X coordinate of world extent origin.
        // @param y0 Y coordinate of world extent origin.
        // @param width Width of world extent.
        // @param height Height of world extent.
        //
        gScene.setWorldExtent(-0.1, -0.1, 1.2, 1.2);

        // Set interaction
        //
        // window.startInteraction(new ZoomInteraction(scene)); Zoom or mouse selection for coloring. Is it possible to have both?
        // xxx gWindow.startInteraction(this);
    }

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
                startElaborationButton.setDisable((mapsGenerator.todoList.size() == 0));
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

    @Override public void initialize(URL arg0, ResourceBundle arg1) {
    }

    /**
     * Draw current map (or reset graph if map4CTCurrent is null)
     */
    public synchronized void drawCurrentMap() {

        // Read the draw method
        //
        if ("CIRCLES".compareTo(drawMethod.getValue().toString()) == 0) {
            drawMethodValue = DRAW_METHOD.CIRCLES;
        } else if ("RECTANGLES".compareTo(drawMethod.getValue().toString()) == 0) {
            drawMethodValue = DRAW_METHOD.RECTANGLES;
        } else if ("RECTANGLES_NEW_YORK".compareTo(drawMethod.getValue().toString()) == 0) {
            drawMethodValue = DRAW_METHOD.RECTANGLES_NEW_YORK;
        }

        // If a map was already shown, I need to redraw it
        //
        if (map4CTCurrent != null) {

            // Clean the scene
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

            // // Update the string map representation field
            // // xxx DrawCurrentMap is called to many times and I cannot update the mapTextRepresentation here
            // //
            // SwingUtilities.invokeLater(new Runnable() {
            // public void run() {
            // mapTextRepresentationTextField.setText(map4CTCurrent.toString());
            // }
            // });

            // After "add" a container has to be validated
            //
            // xxx mapExplorerPanel.validate();
        } else {
            gScene.removeAll();
            gScene.refresh();
        }
    }

    // EVENTS EVENTS EVENTS
    //
    @FXML private void quitAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML private void logWhilePopulateAction(ActionEvent event) {
        mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
    }

    @FXML private void startElaborationAction(ActionEvent event) {

        // Read parameters set by user (User Interface)
        //
        mapsGenerator.slowdownMillisec = Integer.parseInt(slowdownMillisecTextField.getText());
        mapsGenerator.randomElaboration = randomElaboration.isSelected();
        mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
        mapsGenerator.processAll = processAll.isSelected();

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

    @FXML private void pauseElaborationAction(ActionEvent event) {

        // I want to stop the elaboration
        // Information on screen will be updated by the thread itself
        //
        mapsGenerator.stopRequested = true;
    }

    @FXML private void filterLessThanFourElaborationAction(ActionEvent event) {

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

    @FXML private void filterLessThanFiveElaborationAction(ActionEvent event) {

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

    @FXML private void filterLessThanFacesElaborationAction(ActionEvent event) {

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

    @FXML private void copyMapsToTodoElaborationAction(ActionEvent event) {

        mapsGenerator.todoList = new ArrayList<Map4CT>();
        mapsGenerator.copyMapsToTodo();
        if (mapsGenerator.todoList.size() != 0) {
            startElaborationButton.setDisable(false);
        }
        refreshInfo();
    }

    @FXML private void resetElaborationAction(ActionEvent event) {

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

    @FXML private void drawFirstMapAction(ActionEvent event) {

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

    @FXML private void drawPreviousMapAction(ActionEvent event) {

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

    @FXML private void drawRandomMapAction(ActionEvent event) {

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

    @FXML private void drawNextMapAction(ActionEvent event) {

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

    @FXML private void drawLastMapAction(ActionEvent event) {

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

    @FXML private void drawCurrentMapSlowMotionAction(ActionEvent event) {

        drawCurrentMapSlowMotion();
    }

    /**
     * Draw current map in slow motion mode
     */
    public synchronized void drawCurrentMapSlowMotion() {

        // Read the draw method
        //
        if ("CIRCLES".compareTo(drawMethod.getValue().toString()) == 0) {
            drawMethodValue = DRAW_METHOD.CIRCLES;
        } else if ("RECTANGLES".compareTo(drawMethod.getValue().toString()) == 0) {
            drawMethodValue = DRAW_METHOD.RECTANGLES;
        } else if ("RECTANGLES_NEW_YORK".compareTo(drawMethod.getValue().toString()) == 0) {
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

                    // After "add" a container has to be validated
                    //
                    // xxx mapExplorerPanel.validate();
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

                    // After "add" a container has to be validated
                    //
                    // xxx mapExplorerPanel.validate();
                }
            }
        } else {
            gScene.removeAll();
            gScene.refresh();
        }
    }

    @FXML private void createMapFromTextRepresentationAction(ActionEvent event) {

        // Update the map list and the todoList + set the current map
        //
        Map4CT newMap = mapsGenerator.createMapFromTextRepresentation(mapTextRepresentationTextField.getText(), -1);
        mapsGenerator.maps.add(newMap);
        mapsGenerator.todoList.add(newMap);
        map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
        map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

        // It behaves as the play (generation) action - At the end of the generation thread
        //
        startElaborationButton.setDisable((mapsGenerator.todoList.size() == 0));
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

    @FXML private void getTextRepresentationOfCurrentMapAction(ActionEvent event) {

        // "1b+, 2b+, 9b+, 8b-, 3b-, 5b-, 7b-, 4b-, 2e-, 3e-, 5e-, 6b-, 4e-, 8e-, 7e-, 9e+, 6e+, 1e+"
        //
        if (map4CTCurrent != null) {
            mapTextRepresentationTextField.setText(map4CTCurrent.toString());
        }
    }

    @FXML private void loadMapFromAPreviouslySavedImageAction(ActionEvent event) {

        // Read the metadata from a saved image
        // Choose the filename
        //
        File fileToRead = fileChooser.showOpenDialog(null);
        if (fileToRead != null) {

            try {

                // Read the image
                //
                ImageInputStream inputStream = ImageIO.createImageInputStream(fileToRead);
                ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();
                imageReader.setInput(inputStream, true);

                // Read the metadata and search for the "map-representation" previously save information
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
                Map4CT newMap = mapsGenerator.createMapFromTextRepresentation(mapRepresentation, -1);
                mapsGenerator.maps.add(newMap);
                mapsGenerator.todoList.add(newMap);
                map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
                map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

                // It behaves as the play (generation) action - At the end of the generation thread
                //
                startElaborationButton.setDisable((mapsGenerator.todoList.size() == 0));
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

    @FXML private void drawMethodAction(ActionEvent event) {

        drawCurrentMap();

        if (drawMethodValue == DRAW_METHOD.RECTANGLES) {
            taitButton.setDisable(false);
        } else if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
            taitButton.setDisable(false);
        } else if (drawMethodValue == DRAW_METHOD.CIRCLES) {
            taitButton.setDisable(true);
        }
    }

    @FXML private void graphLayoutAction(ActionEvent event) {

        drawCurrentGraph(true);
    }

    /**
     * Create the graph (update graph4CTCurrent) from the sequence of coordinates of the current map: 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
     */
    public void drawCurrentGraph(boolean validateTheGraphExplorerPanel) {

        // If a map has been selected
        //
        // NOTE
        // if (map4CTCurrent.numberOfFWithGivenCardinality(2) != 0) {
        // - JOptionPane.showConfirmDialog(this, "The current graph has multiple edges (faces with 2 edges)", "Error", JOptionPane.PLAIN_MESSAGE);
        // }
        //
        if (map4CTCurrent == null) {
            JOptionPane.showConfirmDialog(null, "Map is not currently set", "Error", JOptionPane.PLAIN_MESSAGE);
        } else {

            // Re-init and redraw the graph
            //
            initGraphExplorerForGraphic();
            graph4CTCurrent.drawGraph(map4CTCurrent);

            // Update or reset the scale according to the user flag (swing checkbox)
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
                graph4CTCurrent.drawSpiralChain(startingVertexTextField.getText());
            }

            // Validate the graph panel (show the graph)
            //
            if (validateTheGraphExplorerPanel) {
                // xxx graphExplorerPanel.validate();
            }
        }
    }

    /**
     * Init the GraphExplorer to visualize mxGraph (jgraph) graphics
     */
    public void initGraphExplorerForGraphic() {

        // Init (or re-init) all
        //
        graph4CTCurrent = new Graph4CT();
        graph4CTCurrent.init();
        if ("RECTANGULAR".compareTo(graphLayout.getValue().toString()) == 0) {
            graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.RECTANGULAR);
        } else if ("MX_HIERARCHICAL_LAYOUT".compareTo(graphLayout.getValue().toString()) == 0) {
            graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.MX_HIERARCHICAL_LAYOUT);
            // } else if (graphLayout.getSelectedIndex() == 2) {
            // graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.MX_CIRCLE_LAYOUT);
        }

        // Attach (or re-attach) it to the JPanel
        //
        // xxx graphExplorerPanel.removeAll();
        // xxx graphExplorerPanel.setSize(graphExplorerOriginalWidth, graphExplorerOriginalHeight);
        // xxx graphExplorerPanel.add(graph4CTCurrent.getComponent());
    }

    @FXML private void selectColorOneAction(ActionEvent event) {

        colorOne = chooseNewColor(colorOne);
        // xxx selectColorOneButton.setForeground(colorOne);
    }

    @FXML private void selectColorTwoAction(ActionEvent event) {

        colorTwo = chooseNewColor(colorTwo);
        // xxx selectColorTwoButton.setForeground(colorTwo);
    }

    @FXML private void selectColorThreeAction(ActionEvent event) {

        colorThree = chooseNewColor(colorThree);
        // xxx selectColorThreeButton.setForeground(colorThree);
    }

    @FXML private void selectColorFourAction(ActionEvent event) {

        colorFour = chooseNewColor(colorFour);
        // xxx selectColorFourButton.setForeground(colorFour);

        // Re-init graphic
        // It did not repaint (even using repaint()). I had to recreate everything. It maybe AWT and SWING mixed together
        //
        initMapExplorerForGraphic();
    }

    /**
     * Choose a new color
     * 
     * @param currentColor
     *            The current color
     * @return The new color
     */
    public Color chooseNewColor(Color currentColor) {
        ColorPicker colorPicker = new ColorPicker();
        Color newColor = colorPicker.getValue();
        if (newColor == null) {
            newColor = currentColor;
        }

        return newColor;
    }

    @FXML private void selectColorDefaultAction(ActionEvent event) {

        // Set default = RGBW
        //
        colorOne = Color.RED;
        colorTwo = Color.GREEN;
        colorThree = Color.BLUE;
        colorFour = Color.WHITE;

        selectColorOneButton.setTextFill(colorOne);
        selectColorTwoButton.setTextFill(colorTwo);
        selectColorThreeButton.setTextFill(colorThree);
        selectColorFourButton.setTextFill(colorFour);

        // Re-init graphic
        // It did not repaint (even using repaint()). I had to recreate everything. It maybe AWT and SWING mixed together
        //
        initMapExplorerForGraphic();
    }

    @FXML private void showFaceCardinalityAction(ActionEvent event) {

        drawCurrentMap();
    }

    @FXML private void taitAction(ActionEvent event) {

        // Read the original image from screen
        //
        WritableImage inputImageTemp = new WritableImage(gWindow.getCanvas().getWidth(), gWindow.getCanvas().getHeight());
        PixelReader inputImage = inputImageTemp.getPixelReader();
        // xxx Graphics2D graphics2D = inputImage.createGraphics();
        // xxx gWindow.getCanvas().paint(graphics2D);
        // xxx graphics2D.dispose();

        // Create the image where the 3-edge-coloring has to be painted
        //
        WritableImage outputImageTemp = new WritableImage((int) inputImageTemp.getWidth(), (int) inputImageTemp.getHeight());
        PixelWriter outputImage = outputImageTemp.getPixelWriter();

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
                        outputImage.setColor(iX - 1, iY, Color.RED);
                        outputImage.setColor(iX, iY, Color.RED);
                        outputImage.setColor(iX + 1, iY, Color.RED);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorTwo)) || ((previousColor == colorTwo) && (nextColor == colorOne)) || ((previousColor == colorThree) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorThree))) {
                        outputImage.setColor(iX - 1, iY, Color.GREEN);
                        outputImage.setColor(iX, iY, Color.GREEN);
                        outputImage.setColor(iX + 1, iY, Color.GREEN);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorTwo))) {
                        outputImage.setColor(iX - 1, iY, Color.BLUE);
                        outputImage.setColor(iX, iY, Color.BLUE);
                        outputImage.setColor(iX + 1, iY, Color.BLUE);
                    }

                    // Y (Vertical) scan - previous = ABOVE, next = BELOW
                    //
                    previousColor = inputImage.getColor(iX, iY - 1);
                    nextColor = inputImage.getColor(iX, iY + 1);

                    if (((previousColor == colorOne) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorTwo))) {
                        outputImage.setColor(iX, iY - 1, Color.RED);
                        outputImage.setColor(iX, iY, Color.RED);
                        outputImage.setColor(iX, iY + 1, Color.RED);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorTwo)) || ((previousColor == colorTwo) && (nextColor == colorOne)) || ((previousColor == colorThree) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorThree))) {
                        outputImage.setColor(iX, iY - 1, Color.GREEN);
                        outputImage.setColor(iX, iY, Color.GREEN);
                        outputImage.setColor(iX, iY + 1, Color.GREEN);
                    }
                    if (((previousColor == colorOne) && (nextColor == colorFour)) || ((previousColor == colorFour) && (nextColor == colorOne)) || ((previousColor == colorTwo) && (nextColor == colorThree)) || ((previousColor == colorThree) && (nextColor == colorTwo))) {
                        outputImage.setColor(iX, iY - 1, Color.BLUE);
                        outputImage.setColor(iX, iY, Color.BLUE);
                        outputImage.setColor(iX, iY + 1, Color.BLUE);
                    }
                } else {
                    outputImage.setColor(iX, iY, Color.WHITE);
                }
            }
        }

        // Open tait colored image
        //
        JFrame taitFrame = new JFrame();
        JLabel taitImagelabel = new JLabel();
        // xxx taitImagelabel.setIcon(new ImageIcon(outputImage));
        taitFrame.setContentPane(taitImagelabel);
        taitFrame.pack();
        taitFrame.setVisible(true);
    }

    @FXML private void spiralChainAction(ActionEvent event) {

        if (graph4CTCurrent != null) {
            graph4CTCurrent.drawSpiralChain(startingVertexTextField.getText());
        }
    }

    private final Runnable runnableHamiltonialCheck = new Runnable() {
        public void run() {
            hamiltonianCheck();
        }
    };

    @FXML private void stopHamiltonialCheckAction(ActionEvent event) {

        // Stop checking Hamilton-nicity
        //
        stopHamiltonialCheckRequested = true;
    }

    @FXML private void hamiltonialCheckAction(ActionEvent event) {

        // Stop cheching hamilton-icity
        //
        stopHamiltonialCheckRequested = true;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the flag to stop the work
        //
        stopHamiltonialCheckRequested = false;

        // Create the thread ... if not created
        // Execute the thread ... if not already running
        //
        // Note: A java thread cannot be reused ... even if it terminated correctly
        //
        if (findIsoThread == null) {
            findIsoThread = new Thread(runnableHamiltonialCheck);
            findIsoThread.start();
        } else if (findIsoThread.isAlive() == false) {
            findIsoThread = new Thread(runnableHamiltonialCheck);
            findIsoThread.start();
        }
    }

    public void hamiltonianCheck() {

        // Stop when the graph is not Hamiltonian (more than 1 spiral) or stop requested
        //
        // NOTE: It may be Hamiltonian with a different starting point
        //
        int numberOfSpiralChain = 1;

        // Loop
        //
        for (int iMap = 0; (iMap < (mapsGenerator.maps.size() - 1)) && (numberOfSpiralChain == 1) && (stopHamiltonialCheckRequested == false); iMap++) {

            // Move to map
            //
            map4CTCurrentIndex = iMap;
            map4CTCurrent = mapsGenerator.maps.get(iMap);

            // Draw the graph
            //
            drawCurrentGraph(false);

            // Draw the spiral
            //
            numberOfSpiralChain = graph4CTCurrent.drawSpiralChain("default");

            // Show the counter
            //
            if ((iMap % 100) == 0) {

                // Update the inner counter
                //
                // innerLoopTextField.setText("" + iMap + "/" + mapsGenerator.maps.size());
                System.out.println("Debug: " + iMap + "/" + mapsGenerator.maps.size());
            }
        }

        // Check if all graphs had Hamiltonian paths
        //
        if (numberOfSpiralChain == 1) {
            JOptionPane.showMessageDialog(null, "All graphs so far have an Hamiltonian path (using a default starting point)");
        }
    }

    @FXML private void clearSpiralChainAction(ActionEvent event) {

        if (map4CTCurrent != null) {
            drawCurrentGraph(true);
        }
    }

    private final Runnable runnableFindIso = new Runnable() {
        public void run() {
            findIsomorphicGraphs();
        }
    };

    /**
     * Find all isomorphic graphs
     */
    public void findIsomorphicGraphs() {

        // Loop
        //
        for (int iMapOuter = 0; (iMapOuter < (mapsGenerator.maps.size() - 1)) && (stopFindIsomorphicGraphsRequested == false); iMapOuter++) {
            Map4CT map4CTOuter = mapsGenerator.maps.get(iMapOuter);

            // Update the outer counter
            //
            // SwingUtilities.invokeLater(new Runnable() {
            // public void run() {
            // outerLoopTextField.setText("" + iMapOuter + "/" + mapsGenerator.maps.size());
            // }
            // }); xxx come passo dei parametri ad un Runnable?
            System.out.println("" + (iMapOuter + 1) + "/" + mapsGenerator.maps.size());

            // First graph
            //
            Graph4CT graph4CTOuter = new Graph4CT();
            graph4CTOuter.init();
            graph4CTOuter.drawGraph(map4CTOuter);
            UndirectedGraph<String, DefaultEdge> graphT4CTOuter = graph4CTOuter.getJGraphT();

            // Loop
            //
            for (int iMapInner = iMapOuter + 1; (iMapInner < mapsGenerator.maps.size()) && (stopFindIsomorphicGraphsRequested == false); iMapInner++) {
                Map4CT map4CTInner = mapsGenerator.maps.get(iMapInner);

                if ((iMapInner % 100) == 0) {

                    // Update the inner counter
                    //
                    // innerLoopTextField.setText("" + iMapInner + "/" + mapsGenerator.maps.size());
                    System.out.println("" + (iMapInner + 1) + "/" + mapsGenerator.maps.size());
                }

                // Maps have different number of vertices?
                //
                if (map4CTOuter.faces.size() == map4CTInner.faces.size()) {

                    // Second graph
                    //
                    Graph4CT graph4CTInner = new Graph4CT();
                    graph4CTInner.init();
                    graph4CTInner.drawGraph(map4CTInner);
                    UndirectedGraph<String, DefaultEdge> graphT4CTInner = graph4CTInner.getJGraphT();

                    // To speed up the process and to avoid a problem with JGraphT (exception of graphs with different number of edges), I used these checks
                    //
                    if (graph4CTOuter.getJGraphT().edgeSet().size() == graph4CTInner.getJGraphT().edgeSet().size()) {

                        // Iso?
                        //
                        try {
                            GraphIsomorphismInspector iso = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(graphT4CTOuter, graphT4CTInner, null, null);
                            if (iso.isIsomorphic() == true) {
                                System.out.println("Debug: iso found: " + iMapOuter + " " + iMapInner);
                            }
                        } catch (Exception exception) {

                            // In case of an exception, I want to analyze the graphs ... so I'm about to save them
                            //
                            JOptionPane.showMessageDialog(null, "Error in verifying the isomorphism. Graphs will be saved for analysis");
                            saveGraphToGraphML(graph4CTOuter);
                            saveGraphToGraphML(graph4CTInner);
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @FXML private void saveGraphToGraphMLAction(ActionEvent event) {

        if (graph4CTCurrent != null) {
            saveGraphToGraphML(graph4CTCurrent);
        }
    }

    public void saveGraphToGraphML(Graph4CT graphToSave) {
        try {
            // xxx String fileName = "save-graph-" + graphToSave.hashCode() + ".graphml";
            // xxx fileChooser.setSelectedFile(new File(fileName));
            // xxx if (fileChooser.showOpenDialog(graphExplorerPanel) == FileChooser.APPROVE_OPTION) {
            // xxx File fileToSave = fileChooser.getSelectedFile();
            // xxx FileWriter fileToSaveWriter = new FileWriter(fileToSave);
            // xxx GraphMLExporter graphMLExporter = new GraphMLExporter();
            // xxx graphMLExporter.export(fileToSaveWriter, graphToSave.getJGraphT());
            // xxx fileToSaveWriter.close();
            // xxx }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML private void stopFindIsomorphicGraphsAction(ActionEvent event) {

        // Stop finding isomorphism (request the stop to the thread)
        //
        stopFindIsomorphicGraphsRequested = true;
    }

    @FXML private void findIsomorphicGraphsAction(ActionEvent event) {

        // Stop finding isomorphism
        //
        stopFindIsomorphicGraphsRequested = true;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the flag to stop the work
        //
        stopFindIsomorphicGraphsRequested = false;

        // Create the thread ... if not created
        // Execute the thread ... if not already running
        //
        // Note: A java thread cannot be reused ... even if it terminated correctly
        //
        if (findIsoThread == null) {
            findIsoThread = new Thread(runnableFindIso);
            findIsoThread.start();
        } else if (findIsoThread.isAlive() == false) {
            findIsoThread = new Thread(runnableFindIso);
            findIsoThread.start();
        }
    }

    @FXML private void saveMapToImageAction(ActionEvent event) {

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

            // Choose the filename
            //
            fileToSave = fileChooser.showOpenDialog(theStage);
            if (fileToSave != null) {

                // Write the image to memory (BufferedImage)
                //
                BufferedImage bufferedImage = new BufferedImage(gWindow.getCanvas().getWidth(), gWindow.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                gWindow.getCanvas().paint(graphics2D);
                graphics2D.dispose();

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
                writer.write(new IIOImage(bufferedImage, null, metadata));
                imageOutputStream.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML private void saveGraphToImageAction(ActionEvent event) {

        if (graph4CTCurrent != null) {
            try {
                String fileName = "save-graph-" + graph4CTCurrent.hashCode() + ".png";
                fileChooser.setInitialDirectory(new File(fileName));
                // Choose the filename
                //
                File fileToSave = fileChooser.showOpenDialog(null);

                if (fileToSave != null) {

                    // Write the image to memory (BufferedImage)
                    //
                    // BufferedImage bufferedImage = new BufferedImage((int) graph4CTCurrent.getView().getGraphBounds().getWidth(), (int) graph4CTCurrent.getView().getGraphBounds().getHeight(), BufferedImage.TYPE_INT_RGB);
                    // xxx BufferedImage bufferedImage = new BufferedImage((int) graphExplorerPanel.getWidth(), (int) graphExplorerPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
                    // xxx Graphics2D graphics2D = bufferedImage.createGraphics();
                    // xxx graphExplorerPanel.paint(graphics2D);
                    // xxx graphics2D.dispose();

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
                    // xxx writer.write(new IIOImage(bufferedImage, null, metadata));
                    imageOutputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * TODO: For now is disables. Choose the directory xxx
     */
    @FXML private void saveAllGraphToGraphMLAction(ActionEvent event) {

        // Loop
        //
        for (int iMap = 0; iMap < mapsGenerator.maps.size(); iMap++) {

            // Move to map
            //
            map4CTCurrentIndex = iMap;
            map4CTCurrent = mapsGenerator.maps.get(iMap);

            // Draw the graph
            //
            drawCurrentGraph(false);

            // Save the graph and give it a name
            //
            String fileName = "D:\\Temp\\maps\\save-graph-" + iMap + ".graphml";
            File fileToSave = new File(fileName);
            FileWriter fileToSaveWriter;
            try {
                fileToSaveWriter = new FileWriter(fileToSave);
                GraphMLExporter graphMLExporter = new GraphMLExporter();
                graphMLExporter.export(fileToSaveWriter, graph4CTCurrent.getJGraphT());
                fileToSaveWriter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML private void refreshInfoAction(ActionEvent event) {

        refreshInfo();
    }

    @FXML private void colorItAction(ActionEvent event) {

        // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
        //
        stopColorAllRequested = true;
        try {
            Thread.sleep(120);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the stop auto color it request
        //
        stopColorAllRequested = false;

        // Create the thread ... if not created
        // If previously executed and not running, create a new thread
        //
        // Note: A java thread cannot be reused ... even if it terminated correctly
        //
        if (colorAllThread == null) {
            colorAllThread = new Thread(runnableColorIt);
            colorAllThread.start();
        } else if (colorAllThread.isAlive() == false) {
            colorAllThread = new Thread(runnableColorIt);
            colorAllThread.start();
        }
    }

    @FXML private void colorAllAction(ActionEvent event) {

        // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
        //
        stopColorAllRequested = true;
        try {
            Thread.sleep(120);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Reset the stop auto color it request ("X" button)
        //
        stopColorAllRequested = false;

        // Create the thread ... if not created
        // Execute the thread ... if not already running
        //
        // Note: A java thread cannot be reused ... even if it terminated correctly
        //
        if (colorAllThread == null) {
            colorAllThread = new Thread(runnableColorAll);
            colorAllThread.start();
        } else if (colorAllThread.isAlive() == false) {
            colorAllThread = new Thread(runnableColorAll);
            colorAllThread.start();
        }
    }

    @FXML private void stopColorAllAction(ActionEvent event) {

        // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
        //
        stopColorAllRequested = true;
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

            // Since this is a separated thread I cannot rely on the map4CTCurrent
            //
            Map4CT mapBeingColored = map4CTCurrent;

            // Prepare and reset palette for all faces and redraw it
            //
            for (int i = 0; i < mapBeingColored.faces.size(); i++) {
                if (mapBeingColored.faces.get(i).color != COLORS.UNCOLORED) { // Pinned color
                    mapsPalette.add(new ColorPalette(mapBeingColored.faces.get(i).color)); // Set a palette with one color (pinned)
                    facesWithPinnedColor.add(mapBeingColored.faces.get(i));
                } else {
                    mapsPalette.add(new ColorPalette(true)); // Set all four colors
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
            while (!endOfJob && !stopColorAllRequested) {

                // Reset colorFound and moveBackOneFace
                //
                colorFound = false;
                moveBackOneFace = false;

                // Try the four colors
                //
                while (!colorFound && !moveBackOneFace) {

                    // Pick a color and remove it the the reusable colors
                    //
                    if (mapsPalette.get(currentFaceIndex).palette.size() == 0) { // Not correctly colored and I tried all colors

                        // Move to the previous face
                        //
                        moveBackOneFace = true;
                        mapsPalette.get(currentFaceIndex).setToFull();
                        if (mapsPalette.get(currentFaceIndex).isPinned == false) {
                            faceToAnalyze.color = COLORS.UNCOLORED;
                        }
                        if (mapBeingColored.isFaceFacingTheOcean(currentFaceIndex + 1) == true) {
                            colorsFacingTheOcean.palette.pop(); // Throw away the last color inserted
                        }
                        currentFaceIndex--;
                    } else {

                        // Try a new color
                        //
                        faceToAnalyze.color = mapsPalette.get(currentFaceIndex).palette.pop();

                        // Update the list of colors already facing the ocean (if the face is facing the ocean)
                        //
                        if (mapBeingColored.isFaceFacingTheOcean(currentFaceIndex + 1) == true) {
                            if (colorsFacingTheOcean.palette.contains(faceToAnalyze.color) == false) {
                                colorsFacingTheOcean.palette.add(faceToAnalyze.color);
                            }
                        }

                        // Check if map is correctly four colored respect to previous neighbors
                        //
                        if (mapBeingColored.isFaceCorrectlyColoredRespectToPreviousNeighbors(faceToAnalyze) == true) {

                            // Check also if it is correctly colored considering the ocean (the ocean is not a face. Is treated as a special face)
                            //
                            if (colorsFacingTheOcean.palette.size() < 4) {

                                // Check also the coloring of pinned faces (hence: check coloring respect to pinned)
                                //
                                boolean correctColoredRespectToPinned = true;
                                for (int i = 0; (i < facesWithPinnedColor.size()) && (correctColoredRespectToPinned == true); i++) {

                                    if (currentFaceIndex < (facesWithPinnedColor.get(i).id - 1)) {
                                        if (mapBeingColored.isFaceCorrectlyColoredRespectToPreviousNeighbors(facesWithPinnedColor.get(i)) == false) {
                                            correctColoredRespectToPinned = false;
                                        }
                                    }
                                }

                                // If also the coloring of pinned faces is correct
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
                    // NOTE: I hope that changing the instruments too often won't be a problem
                    //
                    changeInstruments();

                    // Play sounds
                    // If midi note goes out of range 0-127, it it set to maximum (or it would not play any sound)
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
                        JOptionPane.showConfirmDialog(null, "Four coloring not possible with these pinned colors.", "Error", JOptionPane.PLAIN_MESSAGE);
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
            for (int map4CTCurrentIndexTemp = map4CTCurrentIndex; (map4CTCurrentIndexTemp < mapsGenerator.maps.size()) && (stopColorAllRequested == false); map4CTCurrentIndexTemp++) {
                map4CTCurrentIndex = map4CTCurrentIndexTemp;
                map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);
                colorIt();
            }
        }
    }

    /**
     * Play sounds: set or change the selected instruments
     */
    public void changeInstruments() {
        // xxx
        midiChannels[0].programChange(1);
        midiChannels[1].programChange(1);
        midiChannels[2].programChange(1);
        midiChannels[3].programChange(1);
    };

    @FXML private void drawFirstGraphAction(ActionEvent event) {

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

    @FXML private void drawPreviousGraphAction(ActionEvent event) {

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

    @FXML private void drawRandomGraphAction(ActionEvent event) {

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

    @FXML private void drawNextGraphAction(ActionEvent event) {

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

    @FXML private void drawLastGraphAction(ActionEvent event) {

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
}
