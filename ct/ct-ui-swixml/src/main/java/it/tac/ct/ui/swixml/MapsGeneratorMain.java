package it.tac.ct.ui.swixml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.graphics.ZoomInteraction;

import org.swixml.SwingEngine;

import it.tac.ct.core.COLORS;
import it.tac.ct.core.ColorPalette;
import it.tac.ct.core.F;
import it.tac.ct.core.FCoordinate;
import it.tac.ct.core.GraphicalObjectCoordinate;
import it.tac.ct.core.Map4CT;
import it.tac.ct.core.MapsGenerator;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Main program: It uses Swixml for GUI and the G graphical library for graphic
 *          </p>
 *          <p>
 *          TODO:
 *          <li>Review the code after the migration to sourceforge (07/02/2011)
 *          <li>Make the user interface more compact and write an introduction (all before publishing it)
 *          <li>Transformations:
 *          <li>- Starting from a displayed map remove the last inserted face (not considering the Ocean) and create another view for compare coloring
 *          <li>- Transform a map that does'nt have an F5 ocean into a map with an F5 ocean (from plane back to sphere and hole an F5)
 *          <li>Extend the list to be stored also on disk when memory is a problem (see if ehcache is good)
 *          <li>Show the use of memory directly on the UI (heap)
 *          <li>Add a tool that from the picture of a graph or of a map (to upload), transform it into a simplified map
 *          <li>Try to think to an highly distributed architecture using a grid of computers to distribute jobs and increase memory available
 *          <li>Add a flag to not filter (remove) any map, not even if a not reach-able face has cardinality = 2
 *          <li>Add the coloring method based on the arbitrary choice the three color for central face, ocean + another face adjoining the other two
 *          <li>Add a map serching tool: for example "find a map with 13 faces" or "find a map with second face with at least two F6 faces" or ...
 *          <li>Set a map manually (using the string that represents the "sequence of coordinates")
 *          <li>Filter maps that have less than N faces
 *          <li>Save and restore maps on disk (single and groups). Each map can be saved as a string that represents the "sequence of coordinates"
 *          <li>Add timing statistics
 *          <li>Remove from F the variables: x, y, w, h and startRadius, stopRadius, ...
 *          <li>Create the object Ocean to color it, through mouse selection as any other face
 *          <li>Clean the code: Review the indexes used for array and similar. Some begin from 0 and some from 1
 *          <li>Clean the code: Verify is styles are created also when not needed (create 4 styles and use the same style for more objects)
 *          <li>Clean the code: When coloring the map, do not recreate it every time but change only the styles
 *          <li>Clean the code: refactor all
 *          <li>Color all maps (silent without visualization + progress bar)
 *          <li>Add a grid to easily understand each face which face it is (or mouse over)
 *          <li>Create a pause button (separated thread) for the automatic color algorithm with the possibility to manually modify the map
 *          <li>Permit zoom or color mode or find a way to have both at the same time (different buttons?)
 *          <li>Save all images found (if list is small or +- 10 maps respect the current one)
 *          <li>3D mode: navigable in all directions + all angles (like google earth)
 *          <li>Bug?: if method == F and maxNumber < 7 and automatic filter is set --> computation does not produce maps (only the basic one)
 *          </p>
 *          <p>
 *          DONE:
 *          <li>When F mode is activated and while generating maps, filter "closed" maps that have reached F faces (only if >= 12)
 *          <li>Filter maps with less than F faces (considering the ocean)
 *          <li>Change the transparent slider to text and add an action to it (open a swixml2 BUG for sliders not handling actions)
 *          <li>Use 64bits JVM to use more memory
 *          <li>If the list of maps gets empty (after a filtering) also clean the screen with the visualized map
 *          <li>LinkedList have been changed to ArrayList to free memory (CPU is sacrificed)
 *          <li>Clean the code: Adjust the automatic coloring algorithm and then find some enhancement + some others + some others
 *          <li>Clean the code: Simplify hasUnreachableFWithCardinalityLessThanFive
 *          <li>Clean the code: Fix the fNumberAtIndex and fNumberAtIndexForColors problem
 *          <li>Set automatic method: compute, filter, copy, compute, filter, copy, etc. NOTE: FIXED_MAPS_LEN already daes it
 *          <li>VERIFIED (it works correctly): Filter less than 4 does not longer work: it remove all maps, no matter what
 *          <li>Automatic "color it" algorithm button
 *          <li>CANCELLED: For the NewYork mode version show a skyline and the statue of liberty
 *          <li>Permit to color the map from a four color palette
 *          <li>text/no-text for numbers
 *          <li>Add buttons to move and visualize maps in order: start, previous, random, next, last
 *          <li>Add print button
 *          <li>Circles mode
 *          <li>Remove maps that have also ocean < 5
 *          <li>Set todoList = maps
 *          <li>Use a LinkedList instead a Map (to free memory)
 *          <li>Build using Ant or Maven (consider also the configuration file)
 *          <li>Add more graphical controls: X-Ray, colors, etc.
 *          <li>Use JGoodies FormLayout
 *          <li>Find a graphic library for swing --> G: http://geosoft.no/graphics/
 *          </p>
 */
@SuppressWarnings("serial")
public class MapsGeneratorMain extends JFrame implements GInteraction {

    // The class that generates maps
    //
    private MapsGenerator mapsGenerator = new MapsGenerator();

    // These two variables permit navigation through maps
    //
    private Map4CT map4CTCurrent = null;
    private int map4CTCurrentIndex = -1;

    // Graphic objects to draw maps
    //
    private GScene scene = null;
    private GWindow window = null;

    private enum DRAW_METHOD {
        CIRCLES, RECTANGLES, RECTANGLES_NEW_YORK
    };

    // Variables automatically initialized to form object (swixml)
    //
    private final JTextField slowdownMillisec = null;
    private final JCheckBox logWhilePopulate = null;
    private final JCheckBox randomElaboration = null;
    private final JCheckBox processAll = null;
    private final JComboBox maxMethod = null;
    private final JTextField maxNumber = null;
    private final JButton startElaboration = null;
    private final JButton pauseElaboration = null;
    private final JButton filterLessThanFourElaboration = null;
    private final JButton filterLessThanFiveElaboration = null;
    private final JButton filterLessThanFacesElaboration = null;
    private final JButton copyMapsToTodoElaboration = null;
    private final JButton resetElaboration = null;
    private final JTextField mapsSize = null;
    private final JTextField mapsRemoved = null;
    private final JTextField todoListSize = null;
    private final JComboBox drawMethod = null;
    private Enum<DRAW_METHOD> drawMethodValue = DRAW_METHOD.CIRCLES;
    private final JComboBox color = null;
    private final JSlider transparency = null;
    private int transparencyValue = 200; // See swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54)
    private final JPanel mapExplorer = null;
    private final JCheckBox showFaceCardinality = null;

    // Some graphical properties
    //
    public static final int LINE_WIDTH = 1;

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
    private MapsGeneratorMain() {
        try {

            SwingEngine<MapsGeneratorMain> engine = new SwingEngine<MapsGeneratorMain>(this);
            URL configFileURL = this.getClass().getClassLoader().getResource("config/4ct.xml");
            engine.render(configFileURL).setVisible(true);

            // G lib initialization (link window canvas to JPanel)
            //
            window = new GWindow();
            scene = new GScene(window);
            mapExplorer.add(window.getCanvas(), BorderLayout.CENTER);

            // Use a normalized world extent (adding a safety border)
            //
            // @param x0 X coordinate of world extent origin.
            // @param y0 Y coordinate of world extent origin.
            // @param width Width of world extent.
            // @param height Height of world extent.
            //
            scene.setWorldExtent(-0.1, -0.1, 1.2, 1.2);

            // Set interaction
            //
            window.startInteraction(new ZoomInteraction(scene));
            // window.startInteraction(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
                startElaboration.setEnabled(!(mapsGenerator.todoList.size() == 0));
                pauseElaboration.setEnabled(false);
                resetElaboration.setEnabled(true);
                filterLessThanFourElaboration.setEnabled(true);
                filterLessThanFiveElaboration.setEnabled(true);
                filterLessThanFacesElaboration.setEnabled(true);
                copyMapsToTodoElaboration.setEnabled(true);
                refreshRuntimeInfo();
            }
        }
    };

    /**
     * The G map to draw
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

            // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+,
            // 2e+, 1e+
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
                    g.x = (float)((i / normalizationXFactor) + (1.0d / (2.0d * normalizationXFactor)));
                    g.y = (float)((map4CT.sequenceOfCoordinates.sequence.get(i).fNumber - 1.0d) / normalizationYFactor);
                    g.w = (float)i; // Temporary variable
                    g.h = (float)(1.0d - g.y);

                    if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
                        g.y = 1 - g.y;
                        g.h = -1 * g.h;
                    }
                } else {
                    g.w = (float)((i - g.w) * (1.0d / normalizationXFactor));
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

                GStyle faceStyle = styleFromFace(map4CT.faces.get(iFace));
                rectangles[iFace].setStyle(faceStyle);
            }

            // Override plot data for F1
            //
            if (showFaceCardinality.isSelected()) {
                rectangles[0].setText(new GText("" + (map4CT.faces.size() + 1) + ": " + map4CT.faces.get(0).cardinality + " - " + map4CT.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders()));
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
            // 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+,
            // 2e+, 1e+
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
                    g.startAngle = (float)((i / normalizationAngleFactor) * 2 * Math.PI);
                } else {
                    g.stopAngle = (float)((i / normalizationAngleFactor) * 2 * Math.PI);
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
            GStyle faceStyle = styleFromFace(map4CT.faces.get(0));
            rings[0].setStyle(faceStyle);

            // Plot the other rings all starting from the center (0.5, 0.5)
            //
            for (int iFace = 1; iFace < map4CT.faces.size(); iFace++) {
                g = graphicalObjectCoordinates.get(iFace);
                g.startRadius = (float)(iFace * spaceBetweenCircles);
                g.stopRadius = MAX_RADIUS;
                rings[iFace].setGeometryXy(createRing(0.5, 0.5, g.startRadius, g.stopRadius, g.startAngle, g.stopAngle));
                if (showFaceCardinality.isSelected()) {
                    rings[iFace].setText(new GText("" + map4CT.faces.get(iFace).cardinality));
                }

                faceStyle = styleFromFace(map4CT.faces.get(iFace));
                rings[iFace].setStyle(faceStyle);
            }
        }

        public double[] createRing(double xCenter, double yCenter, double startRadius, double stopRadius, double startAngle, double stopAngle) {

            // A point for each degree
            //
            double arcStep = Math.PI / 180.0;
            int internalPointsPerArc = (int)((stopAngle - startAngle) / arcStep);

            int pointX = 0; // Convenient variable to fill the double[]

            // |......||
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

    // EVENTS EVENTS EVENTS ...
    //
    public Action startElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Read parameters set by user (User Interface)
            //
            mapsGenerator.slowdownMillisec = Integer.parseInt(slowdownMillisec.getText());
            mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
            mapsGenerator.randomElaboration = randomElaboration.isSelected();
            mapsGenerator.processAll = processAll.isSelected();

            if (maxMethod.getSelectedIndex() == 0) {
                mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.FIXED_MAPS_LEN;
            } else if (maxMethod.getSelectedIndex() == 1) {
                mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.MAPS;
            } else if (maxMethod.getSelectedIndex() == 2) {
                mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.F;
            }
            mapsGenerator.maxNumber = Integer.parseInt(maxNumber.getText());

            // Set the buttons
            //
            startElaboration.setEnabled(false);
            pauseElaboration.setEnabled(true);
            resetElaboration.setEnabled(false);
            filterLessThanFourElaboration.setEnabled(false);
            filterLessThanFiveElaboration.setEnabled(false);
            filterLessThanFacesElaboration.setEnabled(false);
            copyMapsToTodoElaboration.setEnabled(false);

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
            refreshRuntimeInfo();

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
            refreshRuntimeInfo();

            // Reset graph if necessary
            //
            if (mapsGenerator.maps.size() == 0) {
                map4CTCurrent = null;
                map4CTCurrentIndex = -1;
                drawCurrentMap();
            }
        }
    };

    public Action filterLessThanMaxNumberElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            mapsGenerator.removeMapsWithLessThanFFaces(Integer.parseInt(maxNumber.getText()));
            refreshRuntimeInfo();

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
                startElaboration.setEnabled(true);
            }
            refreshRuntimeInfo();
        }
    };

    public Action resetElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            mapsGenerator = new MapsGenerator();
            startElaboration.setEnabled(true);
            pauseElaboration.setEnabled(false);
            resetElaboration.setEnabled(false);
            filterLessThanFourElaboration.setEnabled(false);
            filterLessThanFiveElaboration.setEnabled(false);
            filterLessThanFacesElaboration.setEnabled(false);
            copyMapsToTodoElaboration.setEnabled(false);
            refreshRuntimeInfo();

            // Reset graph if necessary
            //
            if (mapsGenerator.maps.size() == 0) {
                map4CTCurrent = null;
                map4CTCurrentIndex = -1;
                drawCurrentMap();
            }
        }
    };

    public Action refreshRuntimeInfoAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            refreshRuntimeInfo();
        }
    };

    public Action drawMethodAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            drawCurrentMap();
        }
    };

    // For a swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54) I need to change this and use setter and getter methods
    //
    public final void setTransparencyValue(int value) {

        // Read the transparency to use
        //
        // System.out.println("DEBUG: This event is not called. It is a swixml bug. Open a ticket");
        //
        drawCurrentMap();
        transparencyValue = value;
    }

    public final int getTransparencyValue() {
        return transparencyValue;
    }

    public Action showFaceCardinalityAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
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

    public Action saveMapToGifAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            String drawMethodName = "unknown";
            if (drawMethodValue == DRAW_METHOD.RECTANGLES) {
                drawMethodName = "rectangles";
            } else if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
                drawMethodName = "rectangles_new_york";
            } else if (drawMethodValue == DRAW_METHOD.CIRCLES) {
                drawMethodName = "circles";
            }

            try {
                BufferedImage bufferedImage = new BufferedImage(window.getCanvas().getWidth(), window.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                window.getCanvas().paint(graphics2D);
                graphics2D.dispose();
                if (map4CTCurrent != null) {
                    ImageIO.write(bufferedImage, "png", new File("save-" + drawMethodName + "-" + map4CTCurrent.hashCode() + ".png"));
                } else {
                    ImageIO.write(bufferedImage, "png", new File("save-" + drawMethodName + "-" + "000" + ".png"));
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    };

    /**
     * Utility class to run the generate() method of the MapsGenerator
     */
    private final Runnable runnableColorIt = new Runnable() {
        public void run() {

            // Local variables
            //
            boolean endOfJob = false;
            boolean colorFound = false;
            boolean moveBackOneFace = false;
            int currentFaceIndex = 0; // 0 is the first face = central face
            F faceToAnalyze = null;
            ColorPalette colorsFacingTheOcean = new ColorPalette(false); // When it reaches 4 before to add the ocean, the algorithm can break sooner
            List<ColorPalette> mapsPalette = new ArrayList<ColorPalette>();

            // If a map is set
            //
            if (map4CTCurrent != null) {

                // Prepare and reset the palette for all faces and redraw it
                //
                for (int i = 0; i < map4CTCurrent.faces.size(); i++) {
                    mapsPalette.add(new ColorPalette(true));
                    map4CTCurrent.faces.get(i).color = COLORS.WHITE;
                }

                // Draw the map
                //
                drawCurrentMap();

                // Start from the center
                //
                faceToAnalyze = map4CTCurrent.faces.get(0);

                // While not end of job (loop all faces)
                //
                while (!endOfJob) {

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
                            mapsPalette.get(currentFaceIndex).resetToFull();
                            faceToAnalyze.color = COLORS.WHITE;
                            if (map4CTCurrent.isFaceFacingTheOcean(currentFaceIndex + 1) == true) {
                                colorsFacingTheOcean.palette.pop(); // Throw away the last color inserted
                            }
                            currentFaceIndex--;
                        } else {

                            // Try a new color
                            //
                            faceToAnalyze.color = mapsPalette.get(currentFaceIndex).palette.pop();

                            // Update the list of colors already facing the ocean (if the face is facing the ocean)
                            //
                            if (map4CTCurrent.isFaceFacingTheOcean(currentFaceIndex + 1) == true) {
                                if (colorsFacingTheOcean.palette.contains(faceToAnalyze.color) == false) {
                                    colorsFacingTheOcean.palette.add(faceToAnalyze.color);
                                }
                            }

                            // Check if map is correctly four colored respect to neighbors
                            //
                            if (map4CTCurrent.isFaceCorrectlyColoredRespectToPreviousNeighbors(faceToAnalyze) == true) {

                                // Check also if it is correctly colored considering the ocean (the ocean is not a face. Is treated as a special face)
                                //
                                if (colorsFacingTheOcean.palette.size() < 4) {

                                    // Move to the next face
                                    //
                                    colorFound = true;
                                    currentFaceIndex++;
                                }
                            }
                        }
                    }

                    // Check if end of job
                    //
                    if (currentFaceIndex == map4CTCurrent.faces.size()) {
                        endOfJob = true;
                    } else {
                        faceToAnalyze = map4CTCurrent.faces.get(currentFaceIndex);
                    }

                    // Draw the map
                    //
                    drawCurrentMap();
                }
            }
        }
    };

    public Action colorItAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Execute the thread
            //
            new Thread(runnableColorIt).start();
        }
    };

    /**
     * Refresh runtime info
     */
    public void refreshRuntimeInfo() {
        mapsSize.setText("" + mapsGenerator.maps.size());
        mapsRemoved.setText("" + mapsGenerator.removed);
        todoListSize.setText("" + mapsGenerator.todoList.size());
    }

    /**
     * Draw current map
     */
    public void drawCurrentMap() {

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
            scene.removeAll();

            // Circle mode or rectangular mode
            //
            if (drawMethodValue != DRAW_METHOD.CIRCLES) {
                GMap4CTRectangles gMap4CTRectangles = new GMap4CTRectangles(map4CTCurrent);
                scene.add(gMap4CTRectangles);
                gMap4CTRectangles.draw();
            } else {
                GMap4CTCircles gMap4CTCirlces = new GMap4CTCircles(map4CTCurrent);
                scene.add(gMap4CTCirlces);
                gMap4CTCirlces.draw();

            }

            // Draw & Refresh
            //
            scene.refresh();

            // After "add" a container has to be validated
            //
            mapExplorer.validate();
        } else {
            scene.removeAll();
            scene.refresh();
        }
    }

    /**
     * @param face
     * @return The new style
     */
    public GStyle styleFromFace(F face) {

        // Local variables
        //
        Color colorToUse = null;
        GStyle faceStyle = new GStyle();

        // Read the color to use
        //
        if (face.color == COLORS.WHITE) {
            colorToUse = new Color(255, 255, 255, transparency.getValue());
        } else if (face.color == COLORS.RED) {
            colorToUse = new Color(255, 0, 0, transparency.getValue());
        } else if (face.color == COLORS.GREEN) {
            colorToUse = new Color(0, 255, 0, transparency.getValue());
        } else if (face.color == COLORS.BLUE) {
            colorToUse = new Color(0, 0, 255, transparency.getValue());
        } else if (face.color == COLORS.GRAY) {
            colorToUse = new Color(128, 128, 128, transparency.getValue());
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
     * Mouse events
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
                F face = (F)interactionSegment.getUserData();

                if (color.getSelectedIndex() == 0) {
                    colorToUse = new Color(255, 0, 0, transparency.getValue());
                    face.color = COLORS.RED;
                } else if (color.getSelectedIndex() == 1) {
                    colorToUse = new Color(0, 255, 0, transparency.getValue());
                    face.color = COLORS.GREEN;
                } else if (color.getSelectedIndex() == 2) {
                    colorToUse = new Color(0, 0, 255, transparency.getValue());
                    face.color = COLORS.BLUE;
                } else if (color.getSelectedIndex() == 3) {
                    colorToUse = new Color(128, 128, 128, transparency.getValue());
                    face.color = COLORS.GRAY;
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
}
