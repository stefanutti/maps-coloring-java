/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.swixml;

import it.tac.ct.core.COLORS;
import it.tac.ct.core.ColorPalette;
import it.tac.ct.core.Edge;
import it.tac.ct.core.F;
import it.tac.ct.core.FCoordinate;
import it.tac.ct.core.GraphicalObjectCoordinate;
import it.tac.ct.core.Map4CT;
import it.tac.ct.core.MapsGenerator;
import it.tac.ct.core.Vertex;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;

import org.swixml.SwingEngine;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.sun.imageio.plugins.png.PNGMetadata;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Main program: It uses Swixml for GUI and the G graphical library for graphic
 *          </p>
 *          <p>
 *          MOVED TO SOURCEFORGE TRACKER:
 *          <li>Bug?: if method == F and maxNumber < 7 and automatic filter is active --> computation does not produce maps (only the basic one)
 *          <li>Bug?: Place somewhere else the variables: x, y, w, h and startRadius, stopRadius, ... of F?
 *          <li>Bug?: Review the indexes used for array and similar. Some begin from 0 and some from 1
 *          <li>Bug: When coloring the map, do not recreate it every time but change only the styles
 *          <li>Bug?: Verify if styles are created also when not needed (create 4 styles and use the same style for more objects)
 *          <li>Bug: If you push other buttons when automatic color algorithm is working (separated thread) coloring start acting strange
 *          <li>Add a coloring method based on the arbitrary choice of the three color for central face, ocean + another face adjoining the other two
 *          <li>Set a map manually (using the string that represents the "sequence of coordinates" as in logging on stdout)
 *          <li>Add a flag to not filter (remove) any map, not even if a not reach-able face has cardinality = 2
 *          <li>Add timing statistics
 *          <li>Color all maps (silent without visualization + progress bar)
 *          <li>Add a grid to easily understand each face which face it is (or mouse over)
 *          <li>Add a tool that from the picture of a graph or of a map (to upload), transform it into a simplified map
 *          <li>Make the user interface more compact and write an introduction (all before publishing it) - skins
 *          <li>Save and restore maps on disk (single and groups). Each map can be saved as a string that represents the "sequence of coordinates"
 *          <li>Save all images found (if list is small or +- 10 maps respect the current one)
 *          <li>Show the use of memory directly on the UI (heap)
 *          </p>
 *          <p>
 *          DONE (older than SourceForge):
 *          <li>Starting from a displayed map remove the last inserted face (not considering the Ocean) and create another view for compare coloring
 *          <li>Add a map searching tool: for example "find a map with 13 faces" or "find a map with with at least two F6 faces confining" or ... CANCELLED
 *          <li>Try to think to an highly distributed architecture using a grid of computers to distribute jobs and increase memory available CANCELLED
 *          <li>Transform a map that does'nt have an F5 ocean into a map with an F5 ocean (from plane back to sphere and hole an F5)
 *          <li>Permit zoom or color mode or find a way to have both at the same time (different buttons?) CANCELLED
 *          <li>When F mode is activated and while generating maps, filter "closed" maps that have reached F faces (only if >= 12)
 *          <li>Filter maps with less than F faces (considering the ocean)
 *          <li>Change the transparent slider to text and add an action to it (open a swixml2 BUG for sliders not handling actions)
 *          <li>CANCELLED: Use 64 bits JVM to use more memory. 64 bits JVM has many bugs
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
 *          <li>Create the object Ocean to color it, through mouse selection as any other face
 *          <li>Add print button
 *          <li>Circles mode
 *          <li>3D mode: navigable in all directions + all angles (like google earth). It will be done for the JMonkey version
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
    private GScene gScene = null;
    private GWindow gWindow = null;

    private enum DRAW_METHOD {
        CIRCLES, RECTANGLES, RECTANGLES_NEW_YORK
    };

    // Variables automatically initialized to form object (linked to swixml)
    //
    private final JFrame mainframe = null;

    // TAB: Main
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
    private final JPanel mapExplorer = null;
    private final JTextField mapTextRepresentation = null;
    private final JButton createMapFromTextRepresentation = null;
    private final JButton getTextRepresentationOfCurrentMap = null;
    private final JButton loadMapFromAPreviouslySavedImage = null;

    private final JComboBox drawMethod = null;
    private Enum<DRAW_METHOD> drawMethodValue = DRAW_METHOD.CIRCLES;
    private final JSlider transparency = null;
    private int transparencyValue = 255; // See swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54)
    private final JCheckBox showFaceCardinality = null;
    private final JButton selectColorOne = null;
    private final JButton selectColorTwo = null;
    private final JButton selectColorThree = null;
    private final JButton selectColorFour = null;
    private Color colorOne = null;
    private Color colorTwo = null;
    private Color colorThree = null;
    private Color colorFour = null;
    private Thread colorItThread = null;
    private Thread colorAllThread = null;
    private boolean stopColorRequested = false;
    private final JButton tait = null;
    private final JButton spiralChain = null;
    private JFileChooser fileChooser = null;

    private final JTextField mapsSize = null;
    private final JTextField currentMap = null;
    private final JTextField mapsRemoved = null;
    private final JTextField todoListSize = null;
    private final JTextField totalMemory = null;
    private final JTextField maxMemory = null;
    private final JTextField freeMemory = null;

    private final JCheckBox soundWhileColoring = null;
    private final JComboBox colorOneInstrument = null;
    private final JTextField colorOneBaseNote = null;
    private final JTextField colorOneBaseDuration = null;
    private final JTextField colorOneBaseVelocity = null;
    private final JComboBox colorTwoInstrument = null;
    private final JTextField colorTwoBaseNote = null;
    private final JTextField colorTwoBaseDuration = null;
    private final JTextField colorTwoBaseVelocity = null;
    private final JComboBox colorThreeInstrument = null;
    private final JTextField colorThreeBaseNote = null;
    private final JTextField colorThreeBaseDuration = null;
    private final JTextField colorThreeBaseVelocity = null;
    private final JComboBox colorFourInstrument = null;
    private final JTextField colorFourBaseNote = null;
    private final JTextField colorFourBaseDuration = null;
    private final JTextField colorFourBaseVelocity = null;
    private Soundbank soundbank = null;
    private Synthesizer synthesizer = null;
    private Instrument[] instruments = null;
    private MidiChannel[] midiChannels = null;

    // TAB: Graph theory
    //
    private final JPanel graphExplorer = null;

    // Variables to distinguish the various cases in graph creation (B-E, B-M, M-M, M-E)
    //
    private static enum TYPE_OF_VERTEX {
        NOT_DEFINED, BEGIN, MIDDLE, END
    };

    private mxGraph graph4CTCurrent = null; // yyy
    private mxGraphComponent graph4CTCurrentComponent = null;
    private mxStylesheet graph4CTCurrentStylesheet = null;
    mxIGraphLayout graph4CTCurrentLayout = null;

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
    private MapsGeneratorMain() throws Exception {

        // Initialize Swixml
        //
        SwingEngine<MapsGeneratorMain> engine = new SwingEngine<MapsGeneratorMain>(this);
        URL configFileURL = this.getClass().getClassLoader().getResource("config/4ct.xml");
        engine.render(configFileURL).setVisible(false); // Has to become visible at the end

        // Initialize the fileChooser to desktop
        //
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(fileSystemView.getRoots()[0]);

        // Initialize the sound system and load all instruments
        //
        URL soundbankURL = this.getClass().getClassLoader().getResource("config/soundbank-deluxe.gm");
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

        selectColorOne.setForeground(colorOne);
        selectColorTwo.setForeground(colorTwo);
        selectColorThree.setForeground(colorThree);
        selectColorFour.setForeground(colorFour);

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

        // Init the graph theory explorer
        //
        initGraphExplorerForGraphic();

        // Get visible
        //
        validate();
        setVisible(true);
    }

    public void setInstrumentsNames(JComboBox jComboBox, Instrument[] instruments) {
        for (int i = 0; i < instruments.length; i++) {
            jComboBox.addItem(instruments[i].getName());
        }
    }

    public void initMapExplorerForGraphic() {

        // G lib initialization (link window canvas to JPanel)
        //
        gWindow = new GWindow(colorFour);
        gScene = new GScene(gWindow);
        mapExplorer.removeAll();
        mapExplorer.add(gWindow.getCanvas());

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

    public void initGraphExplorerForGraphic() {

        // This is the graph theory model that has to be modified
        // The graph is attached to an adapter that permits to represent it on video
        // Added to the rendering JPanel
        //
        graph4CTCurrent = new mxGraph();
        graph4CTCurrentComponent = new mxGraphComponent(graph4CTCurrent);

        // Style
        //
        mxStylesheet graph4CTCurrentStylesheet = graph4CTCurrent.getStylesheet();

        Hashtable<String, Object> cellStyle = new Hashtable<String, Object>();
        cellStyle.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
        cellStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);
        cellStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        cellStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        cellStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        cellStyle.put(mxConstants.STYLE_NOLABEL, "0"); // 0 == visualize the label
        cellStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);

        Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
        edgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        // edgeStyle.put(mxConstants.EDGESTYLE_ORTHOGONAL, "1"); // better without this
        edgeStyle.put(mxConstants.STYLE_STARTARROW, "0");
        edgeStyle.put(mxConstants.STYLE_ENDARROW, "0");
        // edgeStyle.put(mxConstants.STYLE_EDITABLE, "0"); // does not work?
        // edgeStyle.put(mxConstants.STYLE_MOVABLE, "0"); // does not work?
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, "3");

        graph4CTCurrentStylesheet.putCellStyle("MyVertexStyle", cellStyle);
        graph4CTCurrentStylesheet.putCellStyle("MyEdgeStyle", edgeStyle);

        // Automatic layout
        //
        graph4CTCurrentLayout = new mxHierarchicalLayout(graph4CTCurrent);
        ((mxHierarchicalLayout) graph4CTCurrentLayout).setIntraCellSpacing(50);
        ((mxHierarchicalLayout) graph4CTCurrentLayout).setOrientation(SwingConstants.WEST);
        // ((mxHierarchicalLayout) graph4CTCurrentLayout).setParentBorder(100); // does not work?
        // ((mxHierarchicalLayout) graph4CTCurrentLayout).setUseBoundingBox(true); // does not work?

        // Not drag-gable
        //
        graph4CTCurrentComponent.setDragEnabled(false);

        // Attach it to the JPanel
        //
        graphExplorer.add(graph4CTCurrentComponent);
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
                createMapFromTextRepresentation.setEnabled(true);
                getTextRepresentationOfCurrentMap.setEnabled(true);
                loadMapFromAPreviouslySavedImage.setEnabled(true);
                refreshInfo();
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

                GStyle faceStyle = styleFromFace(map4CT.faces.get(iFace));
                rectangles[iFace].setStyle(faceStyle);
            }

            // Override plot data for F1
            //
            if (showFaceCardinality.isSelected()) {
                rectangles[0].setText(new GText("" + (map4CT.faces.size() + 1) + ": " + map4CT.faces.get(0).cardinality + " - " + map4CT.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders()));
            }
        }

        /**
         * draw just one rectangle of the map
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

            GStyle faceStyle = styleFromFace(map4CT.faces.get(face));
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
            GStyle faceStyle = styleFromFace(map4CT.faces.get(0));
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

                faceStyle = styleFromFace(map4CT.faces.get(iFace));
                rings[iFace].setStyle(faceStyle);
            }
        }

        /**
         * draw just one ring of the map
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
                GStyle faceStyle = styleFromFace(map4CT.faces.get(0));
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

                GStyle faceStyle = styleFromFace(map4CT.faces.get(face));
                rings[face].setStyle(faceStyle);
            }
        }

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
    public Action tabbedPaneSwitchAction = new AbstractAction() { // zzz
        public void actionPerformed(ActionEvent e) {
            System.out.println("Debug: xxxxxxx");
        }
    };

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

    public Action startElaborationAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Read parameters set by user (User Interface)
            //
            mapsGenerator.slowdownMillisec = Integer.parseInt(slowdownMillisec.getText());
            mapsGenerator.randomElaboration = randomElaboration.isSelected();
            mapsGenerator.logWhilePopulate = logWhilePopulate.isSelected();
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
            createMapFromTextRepresentation.setEnabled(false);
            getTextRepresentationOfCurrentMap.setEnabled(false);
            loadMapFromAPreviouslySavedImage.setEnabled(false);

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
            mapsGenerator.removeMapsWithLessThanFFaces(Integer.parseInt(maxNumber.getText()));
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
                startElaboration.setEnabled(true);
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
            startElaboration.setEnabled(true);
            pauseElaboration.setEnabled(false);
            resetElaboration.setEnabled(false);
            filterLessThanFourElaboration.setEnabled(false);
            filterLessThanFiveElaboration.setEnabled(false);
            filterLessThanFacesElaboration.setEnabled(false);
            copyMapsToTodoElaboration.setEnabled(false);
            createMapFromTextRepresentation.setEnabled(true);
            getTextRepresentationOfCurrentMap.setEnabled(true);
            loadMapFromAPreviouslySavedImage.setEnabled(true);

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
            Map4CT newMap = mapsGenerator.createMapFromTextRepresentation(mapTextRepresentation.getText(), -1);
            mapsGenerator.maps.add(newMap);
            mapsGenerator.todoList.add(newMap);
            map4CTCurrent = newMap;

            // It behaves as the play (generation) action - At the end of the generation thread
            //
            startElaboration.setEnabled(!(mapsGenerator.todoList.size() == 0));
            pauseElaboration.setEnabled(false);
            resetElaboration.setEnabled(true);
            filterLessThanFourElaboration.setEnabled(true);
            filterLessThanFiveElaboration.setEnabled(true);
            filterLessThanFacesElaboration.setEnabled(true);
            copyMapsToTodoElaboration.setEnabled(true);
            createMapFromTextRepresentation.setEnabled(true);
            getTextRepresentationOfCurrentMap.setEnabled(true);
            loadMapFromAPreviouslySavedImage.setEnabled(true);

            refreshInfo();
            drawCurrentMap();
        }
    };

    public Action getTextRepresentationOfCurrentMapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // "1b+, 2b+, 9b+, 8b-, 3b-, 5b-, 7b-, 4b-, 2e-, 3e-, 5e-, 6b-, 4e-, 8e-, 7e-, 9e+, 6e+, 1e+"
            //
            if (map4CTCurrent != null) {
                mapTextRepresentation.setText(map4CTCurrent.toString());
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
                    Map4CT newMap = mapsGenerator.createMapFromTextRepresentation(mapTextRepresentation.getText(), -1);
                    mapsGenerator.maps.add(newMap);
                    mapsGenerator.todoList.add(newMap);
                    map4CTCurrent = newMap;

                    // It behaves as the play (generation) action - At the end of the generation thread
                    //
                    startElaboration.setEnabled(!(mapsGenerator.todoList.size() == 0));
                    pauseElaboration.setEnabled(false);
                    resetElaboration.setEnabled(true);
                    filterLessThanFourElaboration.setEnabled(true);
                    filterLessThanFiveElaboration.setEnabled(true);
                    filterLessThanFacesElaboration.setEnabled(true);
                    copyMapsToTodoElaboration.setEnabled(true);
                    createMapFromTextRepresentation.setEnabled(true);
                    getTextRepresentationOfCurrentMap.setEnabled(true);
                    loadMapFromAPreviouslySavedImage.setEnabled(true);

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
                tait.setEnabled(true);
            } else if (drawMethodValue == DRAW_METHOD.RECTANGLES_NEW_YORK) {
                tait.setEnabled(true);
            } else if (drawMethodValue == DRAW_METHOD.CIRCLES) {
                tait.setEnabled(false);
            }
        }
    };

    // For a swixml2 bug (http://code.google.com/p/swixml2/issues/detail?id=54) I needed to change this and use setter and getter methods
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

    public Action selectColorOneAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorOne = chooseNewColor(colorOne);
            selectColorOne.setForeground(colorOne);
        }
    };

    public Action selectColorTwoAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorTwo = chooseNewColor(colorTwo);
            selectColorTwo.setForeground(colorTwo);
        }
    };

    public Action selectColorThreeAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorThree = chooseNewColor(colorThree);
            selectColorThree.setForeground(colorThree);
        }
    };

    public Action selectColorFourAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            colorFour = chooseNewColor(colorFour);
            selectColorFour.setForeground(colorFour);

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

            selectColorOne.setForeground(colorOne);
            selectColorTwo.setForeground(colorTwo);
            selectColorThree.setForeground(colorThree);
            selectColorFour.setForeground(colorFour);

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

    public Action spiralChainAction = new AbstractAction() { // xxx to finish
        public void actionPerformed(ActionEvent e) {

            int spiralChainNumber = 1;
            boolean allSpiralChainsFound = true;
            int numberOfAllUsedVerticesOfTheGraph = 0;

            while (allSpiralChainsFound == false) {
                if (spiralChainNumber == 1) {
                    // move to a random vertex on the external cycle. Default = the second vertex
                } else {
                    // move to the closest unused vertex to the last vertex of the last spiral chain
                }

                int spiralChainVertexNumber = 1;
                boolean spiralChainCompleted = true;

                while (spiralChainCompleted == false) {
                    // set the vertex as "used"
                    // set all incident edges as "used"

                    if ((spiralChainNumber == 1) && (spiralChainVertexNumber == 1)) {
                        // move to the second vertex of the external cycle clockwise
                        numberOfAllUsedVerticesOfTheGraph = numberOfAllUsedVerticesOfTheGraph + 1;
                    } else {
                        if (true) { // edge at left is not "used"
                            // move to the next vertex at the end of the left edge
                            numberOfAllUsedVerticesOfTheGraph = numberOfAllUsedVerticesOfTheGraph + 1;
                        } else if (true) { // edge at right is "used"
                            // move to the next vertex at the end of the right edge
                            numberOfAllUsedVerticesOfTheGraph = numberOfAllUsedVerticesOfTheGraph + 1;
                        } else {
                            spiralChainCompleted = true;
                        }
                    }
                }

                // V = (F - 2) * 2
                //
                if (numberOfAllUsedVerticesOfTheGraph == ((map4CTCurrent.faces.size() - 2) * 2)) {
                    allSpiralChainsFound = true;
                } else {
                    spiralChainNumber = spiralChainNumber + 1;
                }
            }
        }

        private int[] VERTEX_ARRAY = new int[3];

        private void initialize() {
            VERTEX_ARRAY[0] = 255;
            VERTEX_ARRAY[1] = 0;
            VERTEX_ARRAY[2] = 0;

            VERTEX_ARRAY[3] = 1;
            VERTEX_ARRAY[4] = 1;
            VERTEX_ARRAY[5] = 1;
        }

        private boolean onVertex(int[] imageWindow3x3) {
            if (Arrays.equals(imageWindow3x3, VERTEX_ARRAY))
                ;
            return false;
        }

        private void readImageWindow3x3(BufferedImage inputImage, int iX, int iY, int[] imageWindow3x3) {
            imageWindow3x3[0] = inputImage.getRGB(iX - 1, iY - 1);
            imageWindow3x3[1] = inputImage.getRGB(iX - 0, iY - 1);
            imageWindow3x3[2] = inputImage.getRGB(iX + 1, iY - 1);

            imageWindow3x3[3] = inputImage.getRGB(iX - 1, iY - 0);
            imageWindow3x3[4] = inputImage.getRGB(iX - 0, iY - 0);
            imageWindow3x3[5] = inputImage.getRGB(iX + 1, iY - 0);

            imageWindow3x3[6] = inputImage.getRGB(iX - 1, iY + 1);
            imageWindow3x3[7] = inputImage.getRGB(iX - 0, iY + 1);
            imageWindow3x3[8] = inputImage.getRGB(iX + 1, iY + 1);
        }

        private void printImageWindow3x3(int[] imageWindow3x3) {
            System.out.print("" + imageWindow3x3[0]);
            System.out.print("" + imageWindow3x3[1]);
            System.out.println("" + imageWindow3x3[2]);

            System.out.print("" + imageWindow3x3[3]);
            System.out.print("" + imageWindow3x3[4]);
            System.out.println("" + imageWindow3x3[5]);

            System.out.print("" + imageWindow3x3[6]);
            System.out.print("" + imageWindow3x3[7]);
            System.out.println("" + imageWindow3x3[8]);

            System.out.println("------------------------------------");
        }
    };

    public Action saveMapToImageAction = new AbstractAction() {
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

    public Action refreshInfoAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            refreshInfo();
        }
    };

    public Action colorItAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
            //
            stopColorRequested = true;
            try {
                Thread.sleep(120);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            // Reset the stop auto color it request ("X" button)
            //
            stopColorRequested = false;

            // Create the thread ... if not created
            // Execute the thread ... if not already running
            //
            // Note: A java thread cannot be reused ... even if it terminated correctly
            //
            if (colorItThread == null) {
                colorItThread = new Thread(runnableColorIt);
                colorItThread.start();
            } else if (colorItThread.isAlive() == false) {
                colorItThread = new Thread(runnableColorIt);
                colorItThread.start();
            }
        }
    };

    public Action colorAllAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
            //
            stopColorRequested = true;
            try {
                Thread.sleep(120);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            // Reset the stop auto color it request ("X" button)
            //
            stopColorRequested = false;

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
    };

    public Action stopColorAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {

            // Stop auto coloring if running. It waits few millesec to be sure the thread understands the request
            //
            stopColorRequested = true;
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
                drawCurrentGraph();
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
                drawCurrentGraph();
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
                drawCurrentGraph();
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
                drawCurrentGraph();
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
                drawCurrentGraph();
            }
        }
    };

    public void drawCurrentGraphTest() {

        // Start the transaction to modify the graph
        //
        graph4CTCurrent.getModel().beginUpdate();

        // Create a new Graph (xxx)
        //
        Vertex v1 = new Vertex();
        v1.name = "v1";
        Vertex v2 = new Vertex();
        v2.name = "v2";
        Vertex v3 = new Vertex();
        v3.name = "v3";
        Vertex v4 = new Vertex();
        v4.name = "v4";

        Edge e1 = new Edge();
        e1.name = v1.name + "/" + v2.name;
        Edge e2 = new Edge();
        e2.name = v2.name + "/" + v3.name;
        Edge e3 = new Edge();
        e3.name = v3.name + "/" + v4.name;
        Edge e4 = new Edge();
        e4.name = v4.name + "/" + v1.name;

        Object v1O = graph4CTCurrent.insertVertex(graph4CTCurrent.getDefaultParent(), v1.name, v1, 0, 0, 30, 30, "MyVertexStyle");
        Object v2O = graph4CTCurrent.insertVertex(graph4CTCurrent.getDefaultParent(), v2.name, v2, 0, 0, 30, 30, "MyVertexStyle");
        Object v3O = graph4CTCurrent.insertVertex(graph4CTCurrent.getDefaultParent(), v3.name, v3, 0, 0, 30, 30, "MyVertexStyle");
        Object v4O = graph4CTCurrent.insertVertex(graph4CTCurrent.getDefaultParent(), v4.name, v4, 0, 0, 30, 30, "MyVertexStyle");

        System.out.println("Debug: " + v1O + " ==? " + v1 + " - " + ((Vertex) ((mxCell) v1O).getValue()));

        graph4CTCurrent.insertEdge(graph4CTCurrent.getDefaultParent(), e1.name, e1, v1O, v2O, "MyEdgeStyle");
        graph4CTCurrent.insertEdge(graph4CTCurrent.getDefaultParent(), e2.name, e2, v2O, v3O, "MyEdgeStyle");
        graph4CTCurrent.insertEdge(graph4CTCurrent.getDefaultParent(), e3.name, e3, v3O, v4O, "MyEdgeStyle");
        graph4CTCurrent.insertEdge(graph4CTCurrent.getDefaultParent(), e4.name, e4, v4O, v1O, "MyEdgeStyle");

        // End the transaction to modify the graph
        //
        graph4CTCurrentLayout.execute(graph4CTCurrent.getDefaultParent());
        graph4CTCurrent.getModel().endUpdate();

        graphExplorer.validate();
    }

    // Create the graph (update graph4CTCurrent) from the sequence of coordinates of the current map (xxx)
    //
    // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
    //
    public void drawCurrentGraph() {

        // If a map has been selected
        //
        if (map4CTCurrent != null) {

            // Variables to distinguish the various cases (B-E, B-M, M-M, M-E)
            //
            Enum<TYPE_OF_VERTEX> typeOfPreviousVertex = TYPE_OF_VERTEX.NOT_DEFINED;
            Enum<TYPE_OF_VERTEX> typeOfCurrentVertex = TYPE_OF_VERTEX.NOT_DEFINED;

            // Maintain a map of vertices
            //
            Map<String, Vertex> verticesMap = new HashMap<String, Vertex>();

            // Maintain a list of hidden vertices
            //
            List<String> hiddenVertices = new ArrayList<String>();

            // Start the transaction to modify the graph
            //
            graph4CTCurrent.getModel().beginUpdate();

            // Clean up the graph
            // xxx Maybe I have to put verticesMap at the same level of the graph and clean it up here
            //
            graph4CTCurrent.removeCells(graph4CTCurrent.getChildCells(graph4CTCurrent.getDefaultParent(), true, true));

            // Create the graph, analyzing all submaps, rebuilding the original map step by step, face by face
            // NOTE: The first face is not indexed 0 but 1
            //
            for (int iFace = 2; iFace < (map4CTCurrent.sequenceOfCoordinates.sequence.size() / 2) + 1; iFace++) {

                // Create the sub map with iFace faces
                //
                Map4CT subMap = mapsGenerator.createMapFromTextRepresentation(map4CTCurrent.toString(), iFace);

                // These two variable will be usefull also to create edges and to represent left and right once finished
                //
                Vertex previousVertex = null;
                Vertex currentVertex = null;

                // Temp variable
                //
                FCoordinate fTempPreviousCoordinate = null;
                FCoordinate fTempCurrentCoordinate = null;

                // Skip to the BEGIN coordinate
                //
                int iCoordinate = 1;
                while (subMap.sequenceOfCoordinates.sequence.get(iCoordinate).fNumber != iFace) {
                    iCoordinate++;
                }
                typeOfPreviousVertex = TYPE_OF_VERTEX.NOT_DEFINED;
                typeOfCurrentVertex = TYPE_OF_VERTEX.BEGIN;
                fTempPreviousCoordinate = null;
                fTempCurrentCoordinate = subMap.sequenceOfCoordinates.sequence.get(iCoordinate);

                // Loop all coordinates up to the END
                // The "while" loop may be completely skipped, for example in the case of this coordinate's string: "... nb? ne? ..."
                //
                iCoordinate++;
                while (subMap.sequenceOfCoordinates.sequence.get(iCoordinate).fNumber != iFace) {

                    // If this coordinate was already hidden, it does not have to be considered
                    //
                    // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
                    //
                    // Example: for face number 6, the coordinates 10b+, 10e+, 7b+, 7e+, 4b-, 5b- may be vertices (if not hidden before)
                    //
                    if (hiddenVertices.contains(subMap.sequenceOfCoordinates.sequence.get(iCoordinate).toString()) == false) {

                        // Here, we are on a MIDDLE vertex
                        //
                        typeOfPreviousVertex = typeOfCurrentVertex;
                        typeOfCurrentVertex = TYPE_OF_VERTEX.MIDDLE;
                        fTempPreviousCoordinate = fTempCurrentCoordinate;
                        fTempCurrentCoordinate = subMap.sequenceOfCoordinates.sequence.get(iCoordinate);

                        // Add to hidden, so next time it will be skipped
                        //
                        hiddenVertices.add(fTempCurrentCoordinate.toString());

                        // Handle the case and create the vertices
                        //
                        if ((typeOfPreviousVertex == TYPE_OF_VERTEX.BEGIN) && (typeOfCurrentVertex == TYPE_OF_VERTEX.MIDDLE)) {
                            previousVertex = addVertexToGraph(graph4CTCurrent, map4CTCurrent, verticesMap, iFace, FCoordinate.TYPE.BEGIN);
                            currentVertex = verticesMap.get(searchFCoordinateInMap(map4CTCurrent, fTempCurrentCoordinate.fNumber, fTempCurrentCoordinate.type).toString());
                            addEdgeBottomLeft(graph4CTCurrent, previousVertex, currentVertex);
                        } else if ((typeOfPreviousVertex == TYPE_OF_VERTEX.MIDDLE) && (typeOfCurrentVertex == TYPE_OF_VERTEX.MIDDLE)) {
                            previousVertex = verticesMap.get(searchFCoordinateInMap(map4CTCurrent, fTempPreviousCoordinate.fNumber, fTempPreviousCoordinate.type).toString());
                            currentVertex = verticesMap.get(searchFCoordinateInMap(map4CTCurrent, fTempCurrentCoordinate.fNumber, fTempCurrentCoordinate.type).toString());
                            addEdgeRightLeft(graph4CTCurrent, previousVertex, currentVertex);
                        }
                    }

                    // Move to next coordinate
                    //
                    iCoordinate++;
                }

                // At this point of code, the coordinate is an END
                //
                typeOfPreviousVertex = typeOfCurrentVertex;
                typeOfCurrentVertex = TYPE_OF_VERTEX.END;
                fTempPreviousCoordinate = fTempCurrentCoordinate;
                fTempCurrentCoordinate = subMap.sequenceOfCoordinates.sequence.get(iCoordinate);

                // Handle the case and create the vertices
                //
                if ((typeOfPreviousVertex == TYPE_OF_VERTEX.BEGIN) && (typeOfCurrentVertex == TYPE_OF_VERTEX.END)) {
                    previousVertex = addVertexToGraph(graph4CTCurrent, map4CTCurrent, verticesMap, iFace, FCoordinate.TYPE.BEGIN);
                    currentVertex = addVertexToGraph(graph4CTCurrent, map4CTCurrent, verticesMap, iFace, FCoordinate.TYPE.END);
                    addEdgeBottomBottom(graph4CTCurrent, previousVertex, currentVertex);
                } else if ((typeOfPreviousVertex == TYPE_OF_VERTEX.MIDDLE) && (typeOfCurrentVertex == TYPE_OF_VERTEX.END)) {
                    previousVertex = verticesMap.get(searchFCoordinateInMap(map4CTCurrent, fTempPreviousCoordinate.fNumber, fTempPreviousCoordinate.type).toString());
                    currentVertex = addVertexToGraph(graph4CTCurrent, map4CTCurrent, verticesMap, iFace, FCoordinate.TYPE.END);
                    addEdgeRightBottom(graph4CTCurrent, previousVertex, currentVertex);
                }
            }

            // These two variable will be usefull also to create edges and to represent left and right once finished
            //
            Vertex previousVertex = null;
            Vertex currentVertex = null;

            // All missing vertices relationship (of coordinates still visible) will be defined here
            //
            // 1b+ and 1e+ are not considered "1b+ ... 1e+" (these are not vertices)
            // Search the first visible coordinate (besides 1b+). Starts from 1
            //
            int iCoordinate = 1;
            while (map4CTCurrent.sequenceOfCoordinates.sequence.get(iCoordinate).isVisible == false) {
                iCoordinate++;
            }
            previousVertex = null;
            currentVertex = verticesMap.get(map4CTCurrent.sequenceOfCoordinates.sequence.get(iCoordinate).toString());

            // Step ahead and loop until the end of all visible coordinates (excluding 1e+)
            //
            iCoordinate++;
            for (; iCoordinate < (map4CTCurrent.sequenceOfCoordinates.sequence.size() - 1); iCoordinate++) {
                if (map4CTCurrent.sequenceOfCoordinates.sequence.get(iCoordinate).isVisible) {
                    previousVertex = currentVertex;
                    currentVertex = verticesMap.get(map4CTCurrent.sequenceOfCoordinates.sequence.get(iCoordinate).toString());
                    addEdgeRightLeft(graph4CTCurrent, previousVertex, currentVertex);
                }
            }

            // Second coordinate and second-last coordinate are neighbour
            //
            previousVertex = verticesMap.get(map4CTCurrent.sequenceOfCoordinates.sequence.get(1).toString());
            currentVertex = verticesMap.get(map4CTCurrent.sequenceOfCoordinates.sequence.get(map4CTCurrent.sequenceOfCoordinates.sequence.size() - 2).toString());
            addEdgeLeftRight(graph4CTCurrent, previousVertex, currentVertex);

            // Debug
            //
            System.out.println(map4CTCurrent.toString());
            Iterator it = verticesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Vertex v = (Vertex) pairs.getValue();
                System.out.println("Debug: " + v.name + ", " + v.edgeAtLeft.name + ", " + v.edgeAtRight.name + ", " + v.edgeAtBottom.name);
            }

            // End the transaction to modify the graph
            //
            graph4CTCurrentLayout.execute(graph4CTCurrent.getDefaultParent());
            graph4CTCurrent.getModel().endUpdate();

            // Validate the graph
            //
            graphExplorer.validate();
        }
    }

    public Vertex addVertexToGraph(mxGraph graph, Map4CT map, Map<String, Vertex> verticesMapToUpdate, int iFace, Enum<FCoordinate.TYPE> fCoordinateType) {

        // Add another vertex
        //
        FCoordinate fRealCoordinate = searchFCoordinateInMap(map, iFace, fCoordinateType);
        Vertex vertexToAdd = new Vertex();
        vertexToAdd.name = fRealCoordinate.toString();
        if (fRealCoordinate.isVisible) {
            vertexToAdd.isOnTheExcternalCycle = true;
        }
        graph.insertVertex(graph4CTCurrent.getDefaultParent(), vertexToAdd.name, vertexToAdd, 0, 0, 30, 30, "MyVertexStyle");

        // Update the list of vertices
        //
        verticesMapToUpdate.put(vertexToAdd.name, vertexToAdd);

        // Return the vertex
        //
        return vertexToAdd;
    }

    private FCoordinate searchFCoordinateInMap(Map4CT map, int iFaceToSearch, Enum<FCoordinate.TYPE> typeOfCoordinateToSearch) {

        // The coordinate to return
        //
        FCoordinate fCoordinateToReturn = null;

        // Search the coordinates up to the begin of the face being analyzed
        //
        for (int i = 0; (i < map.sequenceOfCoordinates.sequence.size()) && (fCoordinateToReturn == null); i++) {
            FCoordinate fCoordinateTemp = map.sequenceOfCoordinates.sequence.get(i);
            if ((fCoordinateTemp.fNumber == iFaceToSearch) && (fCoordinateTemp.type == typeOfCoordinateToSearch)) {
                fCoordinateToReturn = fCoordinateTemp;
            }
        }

        // Return
        //
        return fCoordinateToReturn;
    }

    private void addEdgeBottomLeft(mxGraph graph, Vertex firstVertex, Vertex secondVertex) {
        Edge edgeToCreate = new Edge();
        edgeToCreate.name = firstVertex.name + "/" + secondVertex.name;
        mxCell firstVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(firstVertex.name);
        mxCell secondVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(secondVertex.name);
        graph.insertEdge(graph.getDefaultParent(), edgeToCreate.name, edgeToCreate, firstVertexRaw, secondVertexRaw, "MyEdgeStyle");
        firstVertex.edgeAtBottom = edgeToCreate;
        secondVertex.edgeAtLeft = edgeToCreate;
    }

    private void addEdgeRightLeft(mxGraph graph, Vertex firstVertex, Vertex secondVertex) {
        // Bug?:
        // Map: [1b+, 5b+, 15b+, 14b-, 15e+, 14e+, 5e+, 7b+, 11b+, 2b-, 7e-, 11e+, 8b+, 8e+, 3b+, 6b+, 3e-, 4b-, 6e+, 10b+, 12b+, 13b+, 13e+, 9b-, 4e-, 10e-, 12e+, 9e+, 2e+, 1e+]
        // edgeToCreate null pointer exception at: 8b+ 8e+
        // Is it a multiple edge
        //
        Edge edgeToCreate = new Edge();
        edgeToCreate.name = firstVertex.name + "/" + secondVertex.name;
        mxCell firstVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(firstVertex.name);
        mxCell secondVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(secondVertex.name);
        graph.insertEdge(graph.getDefaultParent(), edgeToCreate.name, edgeToCreate, firstVertexRaw, secondVertexRaw, "MyEdgeStyle");
        firstVertex.edgeAtRight = edgeToCreate;
        secondVertex.edgeAtLeft = edgeToCreate;
    }

    private void addEdgeLeftRight(mxGraph graph, Vertex firstVertex, Vertex secondVertex) {
        Edge edgeToCreate = new Edge();
        edgeToCreate.name = firstVertex.name + "/" + secondVertex.name;
        mxCell firstVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(firstVertex.name);
        mxCell secondVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(secondVertex.name);
        graph.insertEdge(graph.getDefaultParent(), edgeToCreate.name, edgeToCreate, firstVertexRaw, secondVertexRaw, "MyEdgeStyle");
        firstVertex.edgeAtLeft = edgeToCreate;
        secondVertex.edgeAtRight = edgeToCreate;
    }

    private void addEdgeBottomBottom(mxGraph graph, Vertex firstVertex, Vertex secondVertex) {
        Edge edgeToCreate = new Edge();
        edgeToCreate.name = firstVertex.name + "/" + secondVertex.name;
        mxCell firstVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(firstVertex.name);
        mxCell secondVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(secondVertex.name);
        graph.insertEdge(graph.getDefaultParent(), edgeToCreate.name, edgeToCreate, firstVertexRaw, secondVertexRaw, "MyEdgeStyle");
        firstVertex.edgeAtBottom = edgeToCreate;
        secondVertex.edgeAtBottom = edgeToCreate;
    }

    private void addEdgeRightBottom(mxGraph graph, Vertex firstVertex, Vertex secondVertex) {
        Edge edgeToCreate = new Edge();
        edgeToCreate.name = firstVertex.name + "/" + secondVertex.name;
        mxCell firstVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(firstVertex.name);
        mxCell secondVertexRaw = (mxCell) ((mxGraphModel) graph.getModel()).getCell(secondVertex.name);
        graph.insertEdge(graph.getDefaultParent(), edgeToCreate.name, edgeToCreate, firstVertexRaw, secondVertexRaw, "MyEdgeStyle");
        firstVertex.edgeAtRight = edgeToCreate;
        secondVertex.edgeAtBottom = edgeToCreate;
    }

    private int findThePreviousVisibleCoordinate(List<FCoordinate> sequence, int coordinateNumber) {

        // Loop while not found
        //
        boolean previousVisibleEdgeFound = false;
        while (previousVisibleEdgeFound == false) {
            coordinateNumber--;
            if (sequence.get(coordinateNumber).isVisible == true) {
                previousVisibleEdgeFound = true;
            }
        }
        return coordinateNumber;
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
            while (!endOfJob && !stopColorRequested) {

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
                            Integer note = Integer.parseInt(colorOneBaseNote.getText()) + currentFaceIndex;
                            if (note > 127) {
                                note = 127;
                            }
                            midiChannels[0].noteOn(note, Integer.parseInt(colorOneBaseVelocity.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorOneBaseDuration.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[0].noteOff(note);
                        } else if (faceToAnalyze.color == COLORS.TWO) {
                            Integer note = Integer.parseInt(colorTwoBaseNote.getText()) + currentFaceIndex;
                            if (note > 127) {
                                note = 127;
                            }
                            midiChannels[1].noteOn(note, Integer.parseInt(colorTwoBaseVelocity.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorTwoBaseDuration.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[1].noteOff(note);
                        } else if (faceToAnalyze.color == COLORS.THREE) {
                            Integer note = Integer.parseInt(colorThreeBaseNote.getText()) + currentFaceIndex;
                            midiChannels[2].noteOn(note, Integer.parseInt(colorThreeBaseVelocity.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorThreeBaseDuration.getText()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            midiChannels[2].noteOff(note);
                        } else if (faceToAnalyze.color == COLORS.FOUR) {
                            Integer note = Integer.parseInt(colorFourBaseNote.getText()) + currentFaceIndex;
                            midiChannels[3].noteOn(note, Integer.parseInt(colorFourBaseVelocity.getText()));
                            try {
                                Thread.sleep(Integer.parseInt(colorFourBaseDuration.getText()));
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
        if (mapsGenerator.maps.size() != 0) {

            // Start from current position to the end
            //
            for (; (map4CTCurrentIndex < mapsGenerator.maps.size()) && (stopColorRequested == false); map4CTCurrentIndex++) {
                map4CTCurrent = mapsGenerator.maps.get(map4CTCurrentIndex);
                colorIt();
            }
        }
    }

    /**
     * Refresh runtime info
     */
    public synchronized void refreshInfo() {
        currentMap.setText("" + (map4CTCurrentIndex + 1));
        mapsSize.setText("" + mapsGenerator.maps.size());
        mapsRemoved.setText("" + mapsGenerator.numberOfRemovedMaps);
        todoListSize.setText("" + mapsGenerator.todoList.size());
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
        totalMemory.setText("" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " Mb");
        maxMemory.setText("" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " Mb");
        freeMemory.setText("" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " Mb");
    }

    /**
     * Choose a color
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

            // After "add" a container has to be validated
            //
            mapExplorer.validate();
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
                    mapExplorer.validate();
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
                    mapExplorer.validate();
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
     * @param face
     * @return The new style (G library)
     */
    public GStyle styleFromFace(F face) {

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

                if (face.color == COLORS.UNCOLORED) {
                    colorToUse = new Color(colorOne.getRed(), colorOne.getGreen(), colorOne.getBlue(), transparency.getValue());
                    face.color = COLORS.ONE;
                } else if (face.color == COLORS.ONE) {
                    colorToUse = new Color(colorTwo.getRed(), colorTwo.getGreen(), colorTwo.getBlue(), transparency.getValue());
                    face.color = COLORS.TWO;
                } else if (face.color == COLORS.TWO) {
                    colorToUse = new Color(colorThree.getRed(), colorThree.getGreen(), colorThree.getBlue(), transparency.getValue());
                    face.color = COLORS.THREE;
                } else if (face.color == COLORS.THREE) {
                    colorToUse = new Color(colorFour.getRed(), colorFour.getGreen(), colorFour.getBlue(), transparency.getValue());
                    face.color = COLORS.FOUR;
                } else if (face.color == COLORS.FOUR) {
                    colorToUse = new Color(255, 255, 255, transparency.getValue());
                    face.color = COLORS.UNCOLORED;
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
