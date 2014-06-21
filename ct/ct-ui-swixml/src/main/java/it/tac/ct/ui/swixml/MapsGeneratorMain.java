/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.swixml;

import it.tac.ct.core.COLORS;
import it.tac.ct.core.ColorPalette;
import it.tac.ct.core.F;
import it.tac.ct.core.FCoordinate;
import it.tac.ct.core.Graph4CT;
import it.tac.ct.core.GraphicalObjectCoordinate;
import it.tac.ct.core.Map4CT;
import it.tac.ct.core.MapsGenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.filechooser.FileSystemView;

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
import org.swixml.SwingEngine;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.png.PNGMetadata;

// TODO: Re-factor the code. It is getting too large to maintain!
// TODO: Change "G" to JavaFX
// TODO: Change swixml2 to JavaFX
// TODO: Remove unused libraries: JUNG, ...
// TODO: Spiral chains with sleep(), save to svg, manual vertex selection for spiral start vertex
// TODO: saveAllGraphs ... choose the directory where to save all graphs
// TODO: Load soundbanks on the fly
// TODO: Debug (print) info on demand (button)

/**
 * @author Mario Stefanutti
 * @version September 2007
 */
@SuppressWarnings("serial")
public class MapsGeneratorMain extends JFrame implements GInteraction {

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

    // Variables automatically initialized to form object (linked to swixml)
    //
    private final JFrame mainframe = null;

    // TAB: Main
    //
    private final JTextField slowdownMillisecTextField = null;
    private final JCheckBox logWhilePopulate = null;
    private final JCheckBox randomElaboration = null;
    private final JCheckBox processAll = null;
    private final JComboBox maxMethod = null;
    private final JCheckBox removeIsoWhilePopulate = null;
    private final JTextField maxNumberTextField = null;
    private final JButton startElaborationButton = null;
    private final JButton pauseElaborationButton = null;
    private final JButton filterLessThanFourElaborationButton = null;
    private final JButton filterLessThanFiveElaborationButton = null;
    private final JButton filterLessThanFacesElaborationButton = null;
    private final JButton copyMapsToTodoElaborationButton = null;
    private final JButton resetElaborationButton = null;
    private final JPanel mapExplorerPanel = null;
    private final JTextField mapTextRepresentationTextField = null;
    private final JButton createMapFromTextRepresentationButton = null;
    private final JButton getTextRepresentationOfCurrentMapButton = null;
    private final JButton loadMapFromAPreviouslySavedImageButton = null;
    private final JComboBox drawMethod = null;
    private final JSlider transparency = null;
    private final JCheckBox showFaceCardinality = null;
    private final JButton selectColorOneButton = null;
    private final JButton selectColorTwoButton = null;
    private final JButton selectColorThreeButton = null;
    private final JButton selectColorFourButton = null;
    private final JButton taitButton = null;
    private final JTextField currentMapTextField = null;
    private final JTextField mapsSizeTextField = null;
    private final JTextField mapsRemovedTextField = null;
    private final JTextField todoListSizeTextField = null;
    private final JTextField currentMapTextField2 = null;
    private final JTextField mapsSizeTextField2 = null;
    private final JTextField mapsRemovedTextField2 = null;
    private final JTextField todoListSizeTextField2 = null;
    private final JTextField totalMemoryTextField = null;
    private final JTextField maxMemoryTextField = null;
    private final JTextField freeMemoryTextField = null;
    private final JCheckBox soundWhileColoring = null;
    private final JComboBox colorOneInstrument = null;
    private final JTextField colorOneBaseNoteTextField = null;
    private final JTextField colorOneBaseDurationTextField = null;
    private final JTextField colorOneBaseVelocityTextField = null;
    private final JComboBox colorTwoInstrument = null;
    private final JTextField colorTwoBaseNoteTextField = null;
    private final JTextField colorTwoBaseDurationTextField = null;
    private final JTextField colorTwoBaseVelocityTextField = null;
    private final JComboBox colorThreeInstrument = null;
    private final JTextField colorThreeBaseNoteTextField = null;
    private final JTextField colorThreeBaseDurationTextField = null;
    private final JTextField colorThreeBaseVelocityTextField = null;
    private final JComboBox colorFourInstrument = null;
    private final JTextField colorFourBaseNoteTextField = null;
    private final JTextField colorFourBaseDurationTextField = null;
    private final JTextField colorFourBaseVelocityTextField = null;

    private Color colorOne = null;
    private Color colorTwo = null;
    private Color colorThree = null;
    private Color colorFour = null;
    private Thread colorItThread = null;
    private Thread colorAllThread = null;
    private boolean stopColoringRequested = false;
    private int transparencyValue = 255; // See swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54)
    private Soundbank soundbank = null;
    private Synthesizer synthesizer = null;
    private Instrument[] instruments = null;
    private MidiChannel[] midiChannels = null;
    private JFileChooser fileChooser = null;
    private JFileChooser directoryChooser = null;

    // TAB: Graph theory
    //
    private final JPanel graphExplorerPanel = null;
    private final JCheckBox fitToWindow = null;
    private final JComboBox graphLayout = null;
    private final JTextField startingVertexTextField = null;
    private final JCheckBox autoSpiral = null;
    private final JTextField isoOuterLoopTextField = null;
    private final JTextField isoInnerLoopTextField = null;
    private final JTextField isoRemovedTextField = null;

    private final JComboBox secondColorOfKempeSwitch = null;

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
        @Override
        public void draw() {

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

            // |......|
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
                startElaborationButton.setEnabled(!(mapsGenerator.todoList.size() == 0));
                pauseElaborationButton.setEnabled(false);
                resetElaborationButton.setEnabled(true);
                filterLessThanFourElaborationButton.setEnabled(true);
                filterLessThanFiveElaborationButton.setEnabled(true);
                filterLessThanFacesElaborationButton.setEnabled(true);
                copyMapsToTodoElaborationButton.setEnabled(true);
                createMapFromTextRepresentationButton.setEnabled(true);
                getTextRepresentationOfCurrentMapButton.setEnabled(true);
                loadMapFromAPreviouslySavedImageButton.setEnabled(true);
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

    /**
     * Main program
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        new MapsGeneratorMain();
    }

    /**
     * Constructor
     */
    private MapsGeneratorMain() throws Exception {

        // Initialize Swixml
        //
        SwingEngine<MapsGeneratorMain> engine = new SwingEngine<MapsGeneratorMain>(this);
        URL configFileURL = this.getClass().getClassLoader().getResource("config/4ct.xml");
        engine.render(configFileURL).setVisible(false); // Has to become visible at the end, not now

        // Initialize the fileChooser to desktop
        //
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(fileSystemView.getRoots()[0]);
        directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setCurrentDirectory(fileSystemView.getRoots()[0]);

        // Initialize the sound system and load all instruments
        // Check both if Java 1.5, 1.6, 1.7 and if it is running within a jar
        //
        // .sf2 are not supported for 1.5 e 1.6
        // .gm are no longer supported for 1.7
        //
        String soundbankName = null;
        String javaVersion = System.getProperty("java.version");
        System.out.println("Debug: java.version = " + javaVersion);
        if (javaVersion.startsWith("1.5")) {
            soundbankName = "config/soundbank-deluxe.gm";
        } else if (javaVersion.startsWith("1.6")) {
            soundbankName = "config/soundbank-deluxe.gm";
        } else if (javaVersion.startsWith("1.7")) {
            soundbankName = "config/soundbank-vintage_dreams_waves.sf2";
        } else {
            soundbankName = "config/soundbank-deluxe.gm";
        }
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

        // Set instruments for the combos (I wasn't able to do it through JComboBox's constructor, because soundbanks cannot be closed and then re-opened
        //
        setInstrumentsNames(colorOneInstrument, instruments);
        setInstrumentsNames(colorTwoInstrument, instruments);
        setInstrumentsNames(colorThreeInstrument, instruments);
        setInstrumentsNames(colorFourInstrument, instruments);

        // Initialize colors = Some colors I liked ... instead of using RGBW
        //
        colorOne = new Color(255, 99, 71);
        colorTwo = new Color(50, 205, 50);
        colorThree = new Color(238, 238, 0);
        colorFour = new Color(176, 196, 222);

        selectColorOneButton.setForeground(colorOne);
        selectColorTwoButton.setForeground(colorTwo);
        selectColorThreeButton.setForeground(colorThree);
        selectColorFourButton.setForeground(colorFour);

        // StartUp refresh manager (Runtime info, memory, etc.) - poller
        //
        new Thread(refreshManager).start();

        // Get visible
        //
        initMapExplorerForGraphic();

        // Bug fixed: flickering
        //
        // When GCanvas from geosoft gets mixed with swing code, it causes flickering of objects
        // I found this solution (re-enabling double buffering) and I hape it does not have counter effects. I didn't find any
        //
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        // Get visible
        //
        validate();
        setVisible(true);

        // Useful to workaround a swing problem I can't and I don't want to solve
        //
        graphExplorerOriginalWidth = graphExplorerPanel.getWidth();
        graphExplorerOriginalHeight = graphExplorerPanel.getHeight();

        // When the JFrame will be resized, I'll update also these two variables (used for the jGraphX objects)
        // FIXME: It does not work well when I resize to smaller dimension. Should be solved in another way
        //
        mainframe.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent componentEvent) {
                graphExplorerOriginalWidth = graphExplorerPanel.getWidth();
                graphExplorerOriginalHeight = graphExplorerPanel.getHeight();
            }

            public void componentHidden(ComponentEvent componentEvent) {
            }

            public void componentMoved(ComponentEvent componentEvent) {
            }

            public void componentShown(ComponentEvent componentEvent) {
            }
        });
    }

    /**
     * Set the instruments name taken from an internet bank
     * 
     * @param jComboBox
     * @param instruments
     */
    public void setInstrumentsNames(JComboBox jComboBox, Instrument[] instruments) {
        for (int i = 0; i < instruments.length; i++) {
            jComboBox.addItem(instruments[i].getName());
        }
    }

    /**
     * Init the MapExplorer to visualize G graphics
     */
    public void initMapExplorerForGraphic() {

        // G lib initialization (link window canvas to JPanel)
        //
        gWindow = new GWindow(colorFour); // The background is the ocean, that is always colored with the fourth color ... or the 4ct would be wrong!
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

        // Set interaction
        //
        // window.startInteraction(new ZoomInteraction(scene)); Zoom or mouse selection for coloring. Is it possible to have both?
        gWindow.startInteraction(this);
    }

    /**
     * Init the GraphExplorer to visualize mxGraph (jgraph) graphics
     */
    public void initGraphExplorerForGraphic() {

        // Init (or re-init) all
        //
        graph4CTCurrent = new Graph4CT();
        graph4CTCurrent.init();
        graph4CTCurrent.setDimension(graphExplorerOriginalHeight, graphExplorerOriginalWidth);
        if (graphLayout.getSelectedIndex() == 0) {
            graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.RECTANGULAR);
        } else if (graphLayout.getSelectedIndex() == 1) {
            graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.MX_HIERARCHICAL_LAYOUT);
            // } else if (graphLayout.getSelectedIndex() == 2) {
            // graph4CTCurrent.setGraphLayouts(Graph4CT.GRAPH_LAYOUT.MX_CIRCLE_LAYOUT);
        }

        // Attach (or re-attach) it to the JPanel
        //
        graphExplorerPanel.removeAll();
        graphExplorerPanel.setSize(graphExplorerOriginalWidth, graphExplorerOriginalHeight);
        graphExplorerPanel.add(graph4CTCurrent.getComponent());
    }

    // EVENTS EVENTS EVENTS ...
    //
    public Action quitAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            System.exit(NORMAL);
        }
    };

    public Action logWhilePopulateAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
        }
    };

    public Action removeIsoWhilePopulateAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            mapsGenerator.removeIsoWhilePopulate = removeIsoWhilePopulate.isSelected();
        }
    };

    public Action startElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Read parameters set by user (User Interface)
            //
            mapsGenerator.slowdownMillisec = Integer.parseInt(slowdownMillisecTextField.getText());
            mapsGenerator.randomElaboration = randomElaboration.isSelected();
            mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
            mapsGenerator.processAll = processAll.isSelected();
            mapsGenerator.removeIsoWhilePopulate = removeIsoWhilePopulate.isSelected();

            if (maxMethod.getSelectedIndex() == 0) {
                mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.F;
            } else if (maxMethod.getSelectedIndex() == 1) {
                mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.MAPS;
            } else if (maxMethod.getSelectedIndex() == 2) {
                mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.FIXED_MAPS_LEN;
            }
            mapsGenerator.maxNumber = Integer.parseInt(maxNumberTextField.getText());

            // Set the buttons
            //
            startElaborationButton.setEnabled(false);
            pauseElaborationButton.setEnabled(true);
            resetElaborationButton.setEnabled(false);
            filterLessThanFourElaborationButton.setEnabled(false);
            filterLessThanFiveElaborationButton.setEnabled(false);
            filterLessThanFacesElaborationButton.setEnabled(false);
            copyMapsToTodoElaborationButton.setEnabled(false);
            createMapFromTextRepresentationButton.setEnabled(false);
            getTextRepresentationOfCurrentMapButton.setEnabled(false);
            loadMapFromAPreviouslySavedImageButton.setEnabled(false);

            // Execute the thread
            //
            new Thread(runnableGenerate).start();
        }
    };

    public Action pauseElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // I want to stop the elaboration
            // Information on screen will be updated by the thread itself
            //
            mapsGenerator.stopRequested = true;
        }
    };

    public Action filterLessThanFourElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
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
    };

    public Action filterLessThanFiveElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
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
    };

    public Action filterLessThanFacesElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
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
    };

    public Action copyMapsToTodoElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            mapsGenerator.todoList = new ArrayList<Map4CT>();
            mapsGenerator.copyMapsToTodo();
            if (mapsGenerator.todoList.size() != 0) {
                startElaborationButton.setEnabled(true);
            }
            refreshInfo();
        }
    };

    public Action resetElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Creates a brend new generator
            //
            mapsGenerator = new MapsGenerator();
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;

            // Reset buttons
            //
            startElaborationButton.setEnabled(true);
            pauseElaborationButton.setEnabled(false);
            resetElaborationButton.setEnabled(false);
            filterLessThanFourElaborationButton.setEnabled(false);
            filterLessThanFiveElaborationButton.setEnabled(false);
            filterLessThanFacesElaborationButton.setEnabled(false);
            copyMapsToTodoElaborationButton.setEnabled(false);
            createMapFromTextRepresentationButton.setEnabled(true);
            getTextRepresentationOfCurrentMapButton.setEnabled(true);
            loadMapFromAPreviouslySavedImageButton.setEnabled(true);

            // Refresh info and redraw (reset in this case) graph
            //
            refreshInfo();
            drawCurrentMap();
        }
    };

    public Action drawFirstMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawPreviousMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawRandomMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawNextMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawLastMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawCurrentMapSlowMotionAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            drawCurrentMapSlowMotion();
        }
    };

    public Action createMapFromTextRepresentationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Update the map list and the todoList + set the current map
            //
            Map4CT newMap = MapsGenerator.createMapFromTextRepresentation(mapTextRepresentationTextField.getText(), -1);
            mapsGenerator.maps.add(newMap);
            mapsGenerator.todoList.add(newMap);
            map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
            map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

            // It behaves as the play (generation) action - At the end of the generation thread
            //
            startElaborationButton.setEnabled(!(mapsGenerator.todoList.size() == 0));
            pauseElaborationButton.setEnabled(false);
            resetElaborationButton.setEnabled(true);
            filterLessThanFourElaborationButton.setEnabled(true);
            filterLessThanFiveElaborationButton.setEnabled(true);
            filterLessThanFacesElaborationButton.setEnabled(true);
            copyMapsToTodoElaborationButton.setEnabled(true);
            createMapFromTextRepresentationButton.setEnabled(true);
            getTextRepresentationOfCurrentMapButton.setEnabled(true);
            loadMapFromAPreviouslySavedImageButton.setEnabled(true);

            refreshInfo();
            drawCurrentMap();
        }
    };

    public Action getTextRepresentationOfCurrentMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // "1b+, 2b+, 9b+, 8b-, 3b-, 5b-, 7b-, 4b-, 2e-, 3e-, 5e-, 6b-, 4e-, 8e-, 7e-, 9e+, 6e+, 1e+"
            //
            if (map4CTCurrent != null) {
                mapTextRepresentationTextField.setText(map4CTCurrent.toString());
            }
        }
    };

    public Action loadMapFromAPreviouslySavedImageAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Read the metadata from a saved image
            //
            if (fileChooser.showOpenDialog(gWindow.getCanvas()) == JFileChooser.APPROVE_OPTION) {

                // Choose the filename
                //
                File fileToRead = fileChooser.getSelectedFile();
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
                    Map4CT newMap = MapsGenerator.createMapFromTextRepresentation(mapRepresentation, -1);
                    mapsGenerator.maps.add(newMap);
                    mapsGenerator.todoList.add(newMap);
                    map4CTCurrentIndex = mapsGenerator.maps.size() - 1;
                    map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);

                    // It behaves as the play (generation) action - At the end of the generation thread
                    //
                    startElaborationButton.setEnabled(!(mapsGenerator.todoList.size() == 0));
                    pauseElaborationButton.setEnabled(false);
                    resetElaborationButton.setEnabled(true);
                    filterLessThanFourElaborationButton.setEnabled(true);
                    filterLessThanFiveElaborationButton.setEnabled(true);
                    filterLessThanFacesElaborationButton.setEnabled(true);
                    copyMapsToTodoElaborationButton.setEnabled(true);
                    createMapFromTextRepresentationButton.setEnabled(true);
                    getTextRepresentationOfCurrentMapButton.setEnabled(true);
                    loadMapFromAPreviouslySavedImageButton.setEnabled(true);

                    refreshInfo();
                    drawCurrentMap();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    };

    public Action drawMethodAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            drawCurrentMap();

            if (drawMethodValue == DRAW_METHOD.RECTANGLES) {
                taitButton.setEnabled(true);
            } else if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
                taitButton.setEnabled(true);
            } else if (drawMethodValue == DRAW_METHOD.CIRCLES) {
                taitButton.setEnabled(false);
            }
        }
    };

    public Action graphLayoutAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            drawCurrentGraph(true);
        }
    };

    /**
     * Set the Transparency value
     * 
     * @param value
     */
    public final void setTransparencyValue(int value) {

        // Read the transparency to use
        // FIXME: For a swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54) I needed to change this and use setter and getter methods
        //
        drawCurrentMap();
        transparencyValue = value;
    }

    public final int getTransparencyValue() {
        return transparencyValue;
    }

    public Action selectColorOneAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorOne = chooseNewColor(colorOne);
            selectColorOneButton.setForeground(colorOne);
        }
    };

    public Action selectColorTwoAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorTwo = chooseNewColor(colorTwo);
            selectColorTwoButton.setForeground(colorTwo);
        }
    };

    public Action selectColorThreeAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorThree = chooseNewColor(colorThree);
            selectColorThreeButton.setForeground(colorThree);
        }
    };

    public Action selectColorFourAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorFour = chooseNewColor(colorFour);
            selectColorFourButton.setForeground(colorFour);

            // Re-init graphic
            // It did not repaint (even using repaint()). I had to recreate everything. It maybe AWT and SWING mixed together
            //
            initMapExplorerForGraphic();
        }
    };

    public Action selectColorDefaultAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Set default = RGBW
            //
            colorOne = Color.RED;
            colorTwo = Color.GREEN;
            colorThree = Color.BLUE;
            colorFour = Color.WHITE;

            selectColorOneButton.setForeground(colorOne);
            selectColorTwoButton.setForeground(colorTwo);
            selectColorThreeButton.setForeground(colorThree);
            selectColorFourButton.setForeground(colorFour);

            // Re-init graphic
            // It did not repaint (even using repaint()). I had to recreate everything. It maybe AWT and SWING mixed together
            //
            initMapExplorerForGraphic();
        }
    };

    public Action showFaceCardinalityAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            drawCurrentMap();
        }
    };

    public Action taitAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Read the original image from screen
            //
            BufferedImage inputImage = new BufferedImage(gWindow.getCanvas().getWidth(), gWindow.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = inputImage.createGraphics();
            gWindow.getCanvas().paint(graphics2D);
            graphics2D.dispose();

            // Create the image where the 3-edge-coloring has to be painted
            //
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Scan the image
            //
            int previousColor = 0;
            int currentColor = 0;
            int nextColor = 0;
            for (int iY = 1; iY < inputImage.getHeight() - 1; iY++) {
                for (int iX = 1; iX < inputImage.getWidth() - 1; iX++) {
                    currentColor = inputImage.getRGB(iX, iY);

                    if (currentColor == Color.BLACK.getRGB()) {

                        // Y (Vertical) scan - previous = LEFT, next = RIGHT
                        //
                        previousColor = inputImage.getRGB(iX - 1, iY);
                        nextColor = inputImage.getRGB(iX + 1, iY);

                        if (((previousColor == colorOne.getRGB()) && (nextColor == colorThree.getRGB())) || ((previousColor == colorThree.getRGB()) && (nextColor == colorOne.getRGB())) || ((previousColor == colorTwo.getRGB()) && (nextColor == colorFour.getRGB())) || ((previousColor == colorFour.getRGB()) && (nextColor == colorTwo.getRGB()))) {
                            outputImage.setRGB(iX - 1, iY, Color.RED.getRGB());
                            outputImage.setRGB(iX, iY, Color.RED.getRGB());
                            outputImage.setRGB(iX + 1, iY, Color.RED.getRGB());
                        }
                        if (((previousColor == colorOne.getRGB()) && (nextColor == colorTwo.getRGB())) || ((previousColor == colorTwo.getRGB()) && (nextColor == colorOne.getRGB())) || ((previousColor == colorThree.getRGB()) && (nextColor == colorFour.getRGB())) || ((previousColor == colorFour.getRGB()) && (nextColor == colorThree.getRGB()))) {
                            outputImage.setRGB(iX - 1, iY, Color.GREEN.getRGB());
                            outputImage.setRGB(iX, iY, Color.GREEN.getRGB());
                            outputImage.setRGB(iX + 1, iY, Color.GREEN.getRGB());
                        }
                        if (((previousColor == colorOne.getRGB()) && (nextColor == colorFour.getRGB())) || ((previousColor == colorFour.getRGB()) && (nextColor == colorOne.getRGB())) || ((previousColor == colorTwo.getRGB()) && (nextColor == colorThree.getRGB())) || ((previousColor == colorThree.getRGB()) && (nextColor == colorTwo.getRGB()))) {
                            outputImage.setRGB(iX - 1, iY, Color.BLUE.getRGB());
                            outputImage.setRGB(iX, iY, Color.BLUE.getRGB());
                            outputImage.setRGB(iX + 1, iY, Color.BLUE.getRGB());
                        }

                        // Y (Vertical) scan - previous = ABOVE, next = BELOW
                        //
                        previousColor = inputImage.getRGB(iX, iY - 1);
                        nextColor = inputImage.getRGB(iX, iY + 1);

                        if (((previousColor == colorOne.getRGB()) && (nextColor == colorThree.getRGB())) || ((previousColor == colorThree.getRGB()) && (nextColor == colorOne.getRGB())) || ((previousColor == colorTwo.getRGB()) && (nextColor == colorFour.getRGB())) || ((previousColor == colorFour.getRGB()) && (nextColor == colorTwo.getRGB()))) {
                            outputImage.setRGB(iX, iY - 1, Color.RED.getRGB());
                            outputImage.setRGB(iX, iY, Color.RED.getRGB());
                            outputImage.setRGB(iX, iY + 1, Color.RED.getRGB());
                        }
                        if (((previousColor == colorOne.getRGB()) && (nextColor == colorTwo.getRGB())) || ((previousColor == colorTwo.getRGB()) && (nextColor == colorOne.getRGB())) || ((previousColor == colorThree.getRGB()) && (nextColor == colorFour.getRGB())) || ((previousColor == colorFour.getRGB()) && (nextColor == colorThree.getRGB()))) {
                            outputImage.setRGB(iX, iY - 1, Color.GREEN.getRGB());
                            outputImage.setRGB(iX, iY, Color.GREEN.getRGB());
                            outputImage.setRGB(iX, iY + 1, Color.GREEN.getRGB());
                        }
                        if (((previousColor == colorOne.getRGB()) && (nextColor == colorFour.getRGB())) || ((previousColor == colorFour.getRGB()) && (nextColor == colorOne.getRGB())) || ((previousColor == colorTwo.getRGB()) && (nextColor == colorThree.getRGB())) || ((previousColor == colorThree.getRGB()) && (nextColor == colorTwo.getRGB()))) {
                            outputImage.setRGB(iX, iY - 1, Color.BLUE.getRGB());
                            outputImage.setRGB(iX, iY, Color.BLUE.getRGB());
                            outputImage.setRGB(iX, iY + 1, Color.BLUE.getRGB());
                        }
                    } else {
                        outputImage.setRGB(iX, iY, Color.WHITE.getRGB());
                    }
                }
            }

            // Open tait colored image
            //
            JFrame taitFrame = new JFrame();
            JLabel taitImagelabel = new JLabel();
            taitImagelabel.setIcon(new ImageIcon(outputImage));
            taitFrame.setContentPane(taitImagelabel);
            taitFrame.pack();
            taitFrame.setVisible(true);
        }
    };

    public Action spiralChainAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            if (graph4CTCurrent != null) {
                graph4CTCurrent.drawSpiralChain(startingVertexTextField.getText());
            }
        }
    };

    public Action clearSpiralChainAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            if (map4CTCurrent != null) {
                drawCurrentGraph(true);
            }
        }
    };

    public Action stopRemovingIsoAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop removing isomorphic maps (request the stop to the thread)
            //
            mapsGenerator.stopRemovingIsoRequested = true;
        }
    };

    public Action removeIsoMapsAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
            // Note: A java thread cannot be reused ... even if it terminated correctly
            //
            if (removeIsoThread == null) {
                removeIsoThread = new Thread(runnableRemoveIsoMaps);
                removeIsoThread.start();
            } else if (removeIsoThread.isAlive() == false) {
                removeIsoThread = new Thread(runnableRemoveIsoMaps);
                removeIsoThread.start();
            }
        }
    };

    public Action removeIsoTodoListAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
            // Note: A java thread cannot be reused ... even if it terminated correctly
            //
            if (removeIsoThread == null) {
                removeIsoThread = new Thread(runnableRemoveIsoTodoList);
                removeIsoThread.start();
            } else if (removeIsoThread.isAlive() == false) {
                removeIsoThread = new Thread(runnableRemoveIsoTodoList);
                removeIsoThread.start();
            }
        }
    };

    public Action saveMapToImageAction = new AbstractAction() {
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
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
                fileChooser.setSelectedFile(new File(fileName));
                if (fileChooser.showOpenDialog(gWindow.getCanvas()) == JFileChooser.APPROVE_OPTION) {

                    // Choose the filename
                    //
                    fileToSave = fileChooser.getSelectedFile();

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
    };

    public Action saveGraphToImageAction = new AbstractAction() {
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {

            if (graph4CTCurrent != null) {
                try {
                    String fileName = "save-graph-" + graph4CTCurrent.hashCode() + ".png";
                    fileChooser.setSelectedFile(new File(fileName));
                    if (fileChooser.showOpenDialog(graphExplorerPanel) == JFileChooser.APPROVE_OPTION) {

                        // Choose the filename
                        //
                        File fileToSave = fileChooser.getSelectedFile();

                        // Write the image to memory (BufferedImage)
                        //
                        // BufferedImage bufferedImage = new BufferedImage((int) graph4CTCurrent.getView().getGraphBounds().getWidth(), (int) graph4CTCurrent.getView().getGraphBounds().getHeight(), BufferedImage.TYPE_INT_RGB);
                        BufferedImage bufferedImage = new BufferedImage((int) graphExplorerPanel.getWidth(), (int) graphExplorerPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics2D = bufferedImage.createGraphics();
                        graphExplorerPanel.paint(graphics2D);
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
        }
    };

    public Action saveAllMapsAndTodoListAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            try {
                String fileName = "save-all-maps.serialized";
                fileChooser.setSelectedFile(new File(fileName));
                if (fileChooser.showOpenDialog(graphExplorerPanel) == JFileChooser.APPROVE_OPTION) {

                    // Choose the filename
                    //
                    File fileToSave = fileChooser.getSelectedFile();
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
    };

    public Action loadAllMapsAction = new AbstractAction() {
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {

            // Select the file to load
            //
            if (fileChooser.showOpenDialog(gWindow.getCanvas()) == JFileChooser.APPROVE_OPTION) {

                // Get the selected filename
                //
                File fileToRead = fileChooser.getSelectedFile();
                try {

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
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                // It behaves as the play (generation) action - At the end of the generation thread
                //
                filterLessThanFourElaborationButton.setEnabled(true);
                filterLessThanFiveElaborationButton.setEnabled(true);
                filterLessThanFacesElaborationButton.setEnabled(true);
                copyMapsToTodoElaborationButton.setEnabled(true);

                refreshInfo();
                drawCurrentMap();
            }
        }
    };

    public Action loadAllTodoListAction = new AbstractAction() {
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {

            // Select the file to load
            //
            if (fileChooser.showOpenDialog(gWindow.getCanvas()) == JFileChooser.APPROVE_OPTION) {

                // Get the selected filename
                //
                File fileToRead = fileChooser.getSelectedFile();
                try {

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
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                // It behaves as the play (generation) action - At the end of the generation thread
                //
                startElaborationButton.setEnabled(!(mapsGenerator.todoList.size() == 0));
                pauseElaborationButton.setEnabled(false);
                resetElaborationButton.setEnabled(true);

                refreshInfo();
                drawCurrentMap();
            }
        }
    };

    public Action saveGraphToGraphMLAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            if (graph4CTCurrent != null) {
                saveGraphToGraphML(graph4CTCurrent);
            }
        }
    };

    public void saveGraphToGraphML(Graph4CT graphToSave) {
        try {
            String fileName = "save-graph-" + graphToSave.hashCode() + ".graphml";
            fileChooser.setSelectedFile(new File(fileName));
            if (fileChooser.showOpenDialog(graphExplorerPanel) == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
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
    public Action saveAllGraphToGraphMLAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // If the map list is not empty
            //
            if (mapsGenerator.maps.size() != 0) {

                // Select a directory
                //
                if (directoryChooser.showOpenDialog(graphExplorerPanel) == JFileChooser.APPROVE_OPTION) {
                    String selectedDirectory = directoryChooser.getSelectedFile().getAbsolutePath();

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
                        String fileName = selectedDirectory + "/save-graph-" + (map4CTCurrent.faces.size() + 1) + "-" + map4CTCurrent.hashCode() + ".graphml";
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
    };

    public Action refreshInfoAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            refreshInfo();
        }
    };

    public Action colorItAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
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
    };

    public Action colorAllAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
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
            // Note: A java thread cannot be reused ... even if it terminated correctly
            //
            if (colorItThread == null) {
                colorItThread = new Thread(runnableColorAll);
                colorItThread.start();
            } else if (colorItThread.isAlive() == false) {
                colorItThread = new Thread(runnableColorAll);
                colorItThread.start();
            }
        }
    };

    public Action stopColorAllAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
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
    };

    public Action drawFirstGraphAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawPreviousGraphAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawRandomGraphAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawNextGraphAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action drawLastGraphAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

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
    };

    public Action secondColorOfKempeSwitchAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Read the second color of the chain
            //
            if (secondColorOfKempeSwitch.getSelectedIndex() == 0) {
                graph4CTCurrent.setSecondColorOfKempeSwitch(Color.red);
            } else if (secondColorOfKempeSwitch.getSelectedIndex() == 1) {
                graph4CTCurrent.setSecondColorOfKempeSwitch(Color.green);
            } else {
                graph4CTCurrent.setSecondColorOfKempeSwitch(Color.blue);
            }
        }
    };


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
            JOptionPane.showConfirmDialog(this, "Map is not currently set", "Error", JOptionPane.PLAIN_MESSAGE);
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
                graphExplorerPanel.validate();
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
                        JOptionPane.showConfirmDialog(this, "Four coloring not possible with these pinned colors.", "Error", JOptionPane.PLAIN_MESSAGE);
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
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size. Any attempt will result in an OutOfMemoryException
        // Get amount of free memory within the heap in bytes. This size will increase after garbage collection and decrease as new objects are created.
        //
        totalMemoryTextField.setText("" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " Mb");
        maxMemoryTextField.setText("" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " Mb");
        freeMemoryTextField.setText("" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " Mb");
    }

    /**
     * Choose a new color
     * 
     * @param currentColor
     *            The current color
     * @return The new color
     */
    public Color chooseNewColor(Color currentColor) {
        Color newColor = JColorChooser.showDialog(null, "Change color", currentColor);
        if (newColor == null) {
            newColor = currentColor;
        }

        return newColor;
    }

    /**
     * Draw current map (or reset graph if map4CTCurrent is null)
     */
    public synchronized void drawCurrentMap() {

        // Bug fixed: flickering
        //
        // When GCanvas from geosoft gets mixed with swing code, it causes flickering of objects
        // I found this solution (re-enabling double buffering) and I hape it does not have counter effects. I didn't find any
        //
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        // Read the draw method
        //
        if (drawMethod.getSelectedIndex() == 0) {
            drawMethodValue = DRAW_METHOD.CIRCLES;
        } else if (drawMethod.getSelectedIndex() == 1) {
            drawMethodValue = DRAW_METHOD.RECTANGLES;
        } else {
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

            // After "add" a container has to be validated
            //
            mapExplorerPanel.validate();
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
        if (drawMethod.getSelectedIndex() == 0) {
            drawMethodValue = DRAW_METHOD.CIRCLES;
        } else if (drawMethod.getSelectedIndex() == 1) {
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

                    // After "add" a container has to be validated
                    //
                    mapExplorerPanel.validate();
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
                    mapExplorerPanel.validate();
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
        midiChannels[0].programChange(colorOneInstrument.getSelectedIndex());
        midiChannels[1].programChange(colorTwoInstrument.getSelectedIndex());
        midiChannels[2].programChange(colorThreeInstrument.getSelectedIndex());
        midiChannels[3].programChange(colorFourInstrument.getSelectedIndex());
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
            colorToUse = new Color(255, 255, 255, transparency.getValue());
        } else if (face.color == COLORS.ONE) {
            colorToUse = new Color(colorOne.getRed(), colorOne.getGreen(), colorOne.getBlue(), transparency.getValue());
        } else if (face.color == COLORS.TWO) {
            colorToUse = new Color(colorTwo.getRed(), colorTwo.getGreen(), colorTwo.getBlue(), transparency.getValue());
        } else if (face.color == COLORS.THREE) {
            colorToUse = new Color(colorThree.getRed(), colorThree.getGreen(), colorThree.getBlue(), transparency.getValue());
        } else if (face.color == COLORS.FOUR) {
            colorToUse = new Color(colorFour.getRed(), colorFour.getGreen(), colorFour.getBlue(), transparency.getValue());
        }

        // Set the style of this object
        //
        faceStyle.setForegroundColor(Color.black);
        faceStyle.setBackgroundColor(colorToUse);
        faceStyle.setLineWidth(LINE_WIDTH);

        // Return the style
        //
        return faceStyle;
    }

    /**
     * Refresh runtime iso info
     */
    public synchronized void refreshIsoInfo() {
        isoOuterLoopTextField.setText("" + mapsGenerator.isoOuterLoop + 1);
        isoInnerLoopTextField.setText("" + mapsGenerator.isoInnerLoop + 1);
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
                // If a color has already be used by the neighbours ... don't use it
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

                // If a color has already be used by the neighbours ... don't use it
                // if I tryed all possibilities ... reset
                //
                if (colorFound == false) {
                    face.color = originalColor;
                }

                // Set the style of this object
                //
                GStyle style = new GStyle();
                style.setForegroundColor(Color.black);
                style.setBackgroundColor(colorToUse);
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
}
