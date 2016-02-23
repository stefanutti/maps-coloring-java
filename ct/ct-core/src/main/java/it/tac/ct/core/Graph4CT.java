/*
 * Copyright 2012 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingConstants;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import edu.ucla.sspace.graph.Graph;
import edu.ucla.sspace.graph.SimpleEdge;
import edu.ucla.sspace.graph.SparseUndirectedGraph;

/**
 * @author Mario Stefanutti
 *         <p>
 *         The graph generator (use jgraph and jgrapht)
 *         </p>
 */
public class Graph4CT {

    // The selected graph layout
    //
    public enum GRAPH_LAYOUT {
        RECTANGULAR, MX_HIERARCHICAL_LAYOUT, MX_CIRCLE_LAYOUT
    };

    private GRAPH_LAYOUT graphLayout = GRAPH_LAYOUT.RECTANGULAR;

    // JGraphX draws graphs on a virtual panel with these dimensions
    //
    private int graphHeight = 1000;
    private int graphWidth = 1000;
    private static final int GRAPH_MARGIN = 70;

    // Graph, JGraphX and JGraphT objects
    //
    public mxGraph jGraph4CT = null;
    private UndirectedGraph<String, DefaultEdge> jGraphT4CT = null;
    private Graph<edu.ucla.sspace.graph.Edge> sSpaceGraph4CT = null;
    private mxGraphComponent jGraph4CTComponent = null;
    private mxIGraphLayout jGraph4CTLayout = null;

    private Map4CT map4CT = null;
    private Map<String, Vertex> verticesMap = null;
    private Map<String, Edge> edgesMap = null;

    private boolean isDashed = true;

    private static Color secondColorOfKempeSwitch = Color.red; // Red should be the default also for the UI

    /**
     * Defaut constructor
     */
    public Graph4CT() {
    }

    /**
     * To permit to attach the virtual panel (the JGraphX panel) to a real panel (JPanel for example)
     * 
     * @return
     */
    public mxGraphComponent getComponent() {
        return jGraph4CTComponent;
    }

    public UndirectedGraph<String, DefaultEdge> getJGraphT() {
        return jGraphT4CT;
    }

    public Graph<edu.ucla.sspace.graph.Edge> getSSpaceGraph4CT() {
        return sSpaceGraph4CT;
    }

    public Color getSecondColorOfKempeSwitch() {
        return secondColorOfKempeSwitch;
    }

    public void setSecondColorOfKempeSwitch(Color secondColorOfKempeSwitchToSet) {
        secondColorOfKempeSwitch = secondColorOfKempeSwitchToSet;
    }

    /**
     * Set the size of the graph on the graphical component
     * 
     * @param height
     * @param width
     */
    public void setDimension(int height, int width) {
        graphHeight = height;
        graphWidth = width;
    }

    /**
     * Init (or re-init) all to store the graph and to visualize mxGraph (jgraph) graphics
     */
    public void init() {

        // This is the graph theory model that has to be modified
        // The graph is attached to an adapter that permits to represent it on video
        // Added to the rendering JPanel
        //
        jGraph4CT = new mxGraph();
        jGraphT4CT = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        sSpaceGraph4CT = new SparseUndirectedGraph();
        jGraph4CTComponent = new mxGraphComponent(jGraph4CT);

        map4CT = null;
        verticesMap = null;
        edgesMap = null;

        // Set styles
        //
        setGraphStyles();

        // Init the automatic graph layouts
        //
        setGraphLayouts(graphLayout);

        // Set some Edge properties
        //
        jGraph4CT.setCellsSelectable(false);
        jGraph4CT.setCellsMovable(false);
        jGraph4CT.setCellsEditable(false);
        jGraph4CT.setCellsBendable(false);
        jGraph4CT.setCellsDisconnectable(false);
        jGraph4CT.setCellsResizable(false);
        jGraph4CT.setKeepEdgesInBackground(true);
        jGraph4CT.setEdgeLabelsMovable(false);
        jGraph4CT.setVertexLabelsMovable(false);
        jGraph4CT.setAllowDanglingEdges(false); // Anyway ... I will not create them!
        jGraph4CTComponent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        // Listener to edge detection: mouse has been clicked on an edge
        //
        jGraph4CTComponent.getGraphControl().addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                Object cell = jGraph4CTComponent.getCellAt(e.getX(), e.getY());
                if ((cell != null) && ((((mxCell) cell).getValue()) instanceof Edge)) {

                    // The Object is an Edge
                    //
                    Edge edge = (Edge) ((mxCell) cell).getValue();

                    // Color the edge
                    // the Edge you are on determine the first value
                    //
                    Color secondColor = null;
                    if (e.getWheelRotation() > 0) {
                        if (edge.color == Color.red) {
                            secondColor = Color.blue;
                        }
                        if (edge.color == Color.green) {
                            secondColor = Color.red;
                        }
                        if (edge.color == Color.blue) {
                            secondColor = Color.green;
                        }
                        
                        showKempeChain(edge, secondColor);
                    } else if (e.getWheelRotation() < 0) {
                        if (edge.color == Color.red) {
                            secondColor = Color.green;
                        }
                        if (edge.color == Color.green) {
                            secondColor = Color.blue;
                        }
                        if (edge.color == Color.blue) {
                            secondColor = Color.red;
                        }

                        showKempeChain(edge, secondColor);
                    } else {
                        // Not a full click (0 has returned). Rotate with more passion!
                    }
                }
            }
        });

        jGraph4CTComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object cell = jGraph4CTComponent.getCellAt(e.getX(), e.getY());
                if ((cell != null) && ((((mxCell) cell).getValue()) instanceof Edge)) {

                    // The Object is an Edge
                    //
                    Edge edge = (Edge) ((mxCell) cell).getValue();

                    // Reset the color
                    //
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        edge.color = Color.lightGray;

                        jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(edge.color), new Object[] { (mxCell) cell });
                        jGraph4CT.clearSelection();
                        jGraph4CTComponent.refresh();
                    }

                    // Kempe's color switch
                    //
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        kempeSwitchColors(edge);
                    }

                    // Color the edge
                    //
                    if (e.getButton() == MouseEvent.BUTTON1) {

                        // Rotate colors and change it
                        // If a color has already be used by the neighbours ... don't use it
                        //
                        setAValidColor(edge);

                        jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(edge.color), new Object[] { (mxCell) cell });
                        jGraph4CT.clearSelection();
                        jGraph4CTComponent.refresh();
                    }
                }
            }
        });

        // Graph not drag-gable (also to avoid a NotSerializable Exception during drag & drop)
        //
        jGraph4CTComponent.setDragEnabled(false);
    }

    private Edge findTheEdgeWithTheNextColorOfTheChain(Edge currentEdge, Color nextColorToSwitch) {
        Edge edgeToReturn = null;

        if (currentEdge.firstVertex.edgeAtBottom.color == nextColorToSwitch) {
            edgeToReturn = currentEdge.firstVertex.edgeAtBottom;
        } else if (currentEdge.firstVertex.edgeAtLeft.color == nextColorToSwitch) {
            edgeToReturn = currentEdge.firstVertex.edgeAtLeft;
        } else if (currentEdge.firstVertex.edgeAtRight.color == nextColorToSwitch) {
            edgeToReturn = currentEdge.firstVertex.edgeAtRight;
        } else if (currentEdge.secondVertex.edgeAtBottom.color == nextColorToSwitch) {
            edgeToReturn = currentEdge.secondVertex.edgeAtBottom;
        } else if (currentEdge.secondVertex.edgeAtLeft.color == nextColorToSwitch) {
            edgeToReturn = currentEdge.secondVertex.edgeAtLeft;
        } else if (currentEdge.secondVertex.edgeAtRight.color == nextColorToSwitch) {
            edgeToReturn = currentEdge.secondVertex.edgeAtRight;
        }

        return edgeToReturn;
    }

    private Edge findTheOtherEdgeWithTheNextColorOfTheChain(Edge currentEdge, Color nextColorToSwitch, Edge edgeNotToSelect) {
        Edge edgeToReturn = null;

        if ((currentEdge.firstVertex.edgeAtBottom.color == nextColorToSwitch) && (currentEdge.firstVertex.edgeAtBottom != edgeNotToSelect)) {
            edgeToReturn = currentEdge.firstVertex.edgeAtBottom;
        } else if ((currentEdge.firstVertex.edgeAtLeft.color == nextColorToSwitch) && (currentEdge.firstVertex.edgeAtLeft != edgeNotToSelect)) {
            edgeToReturn = currentEdge.firstVertex.edgeAtLeft;
        } else if ((currentEdge.firstVertex.edgeAtRight.color == nextColorToSwitch) && (currentEdge.firstVertex.edgeAtRight != edgeNotToSelect)) {
            edgeToReturn = currentEdge.firstVertex.edgeAtRight;
        } else if ((currentEdge.secondVertex.edgeAtBottom.color == nextColorToSwitch) && (currentEdge.secondVertex.edgeAtBottom != edgeNotToSelect)) {
            edgeToReturn = currentEdge.secondVertex.edgeAtBottom;
        } else if ((currentEdge.secondVertex.edgeAtLeft.color == nextColorToSwitch) && (currentEdge.secondVertex.edgeAtLeft != edgeNotToSelect)) {
            edgeToReturn = currentEdge.secondVertex.edgeAtLeft;
        } else if ((currentEdge.secondVertex.edgeAtRight.color == nextColorToSwitch) && (currentEdge.secondVertex.edgeAtRight != edgeNotToSelect)) {
            edgeToReturn = currentEdge.secondVertex.edgeAtRight;
        }

        return edgeToReturn;
    }

    private boolean isTheColorInTheNeighood(Edge edge, Color colorToCheck) {
        boolean colorFound = false;

        if ((edge.firstVertex.edgeAtBottom.color == colorToCheck) || (edge.firstVertex.edgeAtLeft.color == colorToCheck) || (edge.firstVertex.edgeAtRight.color == colorToCheck) || (edge.secondVertex.edgeAtBottom.color == colorToCheck) || (edge.secondVertex.edgeAtLeft.color == colorToCheck) || (edge.secondVertex.edgeAtRight.color == colorToCheck)) {
            colorFound = true;
        }

        return colorFound;
    }

    /**
     * Init the styles for mxGraph
     */
    public void setGraphStyles() {
        mxStylesheet jGraph4CTStylesheet = jGraph4CT.getStylesheet();

        Hashtable<String, Object> vertexStyle = new Hashtable<String, Object>();
        vertexStyle.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
        vertexStyle.put(mxConstants.STYLE_STROKEWIDTH, "2");
        // vertexStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(50, 50, 50)));
        vertexStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.lightGray));
        vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        vertexStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        vertexStyle.put(mxConstants.STYLE_NOLABEL, "1"); // 0 == visualize the label
        vertexStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);

        Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
        edgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        edgeStyle.put(mxConstants.STYLE_STARTARROW, "0");
        edgeStyle.put(mxConstants.STYLE_ENDARROW, "0");
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, "2");
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.lightGray));
        // edgeStyle.put(mxConstants.EDGESTYLE_ORTHOGONAL, "1"); // better without this
        // edgeStyle.put(mxConstants.STYLE_EDITABLE, "0"); // does not work!?
        // edgeStyle.put(mxConstants.STYLE_MOVABLE, "0"); // does not work!?

        jGraph4CTStylesheet.putCellStyle("MyVertexStyle", vertexStyle);
        jGraph4CTStylesheet.putCellStyle("MyEdgeStyle", edgeStyle);
    }

    /**
     * @return the graph layout
     */
    public GRAPH_LAYOUT getGraphLayouts() {
        return graphLayout;
    }

    /**
     * Init the layouts for the mxGraph
     * 
     * @param layout
     *            The layout number (check the external class) to set
     */
    public void setGraphLayouts(GRAPH_LAYOUT layout) {

        // Save the current layout
        //
        graphLayout = layout;

        // Set the layout parameters
        //
        // "rectangular               "
        // "mxCircleLayout            " ?OK - Nice. Useful? OK when fit == false, NOK with errors when fit.
        // "mxHierarchicalLayout      " +OK - Very nice
        //
        // "mxCompactTreeLayout       " NOK - I don't understand it! Ugly, not useful
        // "mxEdgeLabelLayout         " NOK - All Vertices at origin. Ugly, not useful
        // "mxFastOrganicLayout       " ?OK - Seems random positioning. Useful?
        // "mxOrganicLayout           " NOK - Similar to mxFastOrganicLayout but worse than that!
        // "mxOrthogonalLayout        " NOK - All Vertices at origin. Ugly, not useful
        // "mxParallelEdgeLayout      " NOK - All Vertices at origin. Ugly, not useful
        // "mxPartitionLayout         " NOK - All Vertices at origin. Ugly, not useful
        // "mxStackLayout             " NOK - All Vertices on the same line. Not useful
        //
        if (layout == GRAPH_LAYOUT.RECTANGULAR) {
            jGraph4CTLayout = new mxHierarchicalLayout(jGraph4CT);
        }
        if (layout == GRAPH_LAYOUT.MX_CIRCLE_LAYOUT) {
            jGraph4CTLayout = new mxCircleLayout(jGraph4CT);
        } else if (layout == GRAPH_LAYOUT.MX_HIERARCHICAL_LAYOUT) {
            jGraph4CTLayout = new mxHierarchicalLayout(jGraph4CT);
            ((mxHierarchicalLayout) jGraph4CTLayout).setIntraCellSpacing(100);
            ((mxHierarchicalLayout) jGraph4CTLayout).setFineTuning(true);
            ((mxHierarchicalLayout) jGraph4CTLayout).setOrientation(SwingConstants.WEST);
            ((mxHierarchicalLayout) jGraph4CTLayout).setLayoutFromSinks(false);
            ((mxHierarchicalLayout) jGraph4CTLayout).setFixRoots(false);
        }
    }

    /**
     * Compute the spiral chain
     * 
     * @param startingVertexName
     *            "default" or vertex name (Es. "4b+")
     * @return The number of spiral chains found
     */
    public int drawSpiralChain(String startingVertexName, boolean autoColor) {

        // Start @ing
        // 1b+, 28b+, 36b+, 36e+, 26b-, 14b-, 23b-, 25b-, 24b-, 16b-, 22b-, 18b-, 20b-, 21b-, 17b-, 15b-, 13b-, 4b-, 12b-, 6b-, 14e-, 13e-, 16e-, 15e-, 12e-, 17e-, 11b-, 8b-, 10b-, 7b-, 6e-, 3b-, 4e-, 5b-, 7e-, 8e-, 9b-, 5e-, 3e-, 2b-, 9e-, 10e-, 11e-, 18e-, 19b-, 20e-, 22e-, 23e-, 21e-, 26e-, 28e+, 37b+, 37e+, 32b+, 31b-, 27b-, 32e+, 31e+, 29b+, 29e+, 34b+, 33b-, 25e-, 24e-, 19e-, 27e-, 34e+, 33e+, 30b+, 35b+, 35e+, 30e+, 2e+, 1e+
        //
        int spiralChainNumber = 1;
        int spiralChainVertexSequenceNumber = 1;
        boolean allSpiralChainsFound = false;
        int numberOfAllUsedVerticesOfTheGraph = 0;
        Vertex currentVertex = null;
        Edge currentEdge = null;
        Vertex previousVertex = null;
        Edge previousEdge = null;

        // Check if a previous run, has visited all vertices
        //
        allSpiralChainsFound = true;
        for (Vertex vertex : verticesMap.values()) {
            if (vertex.used == false) {
                allSpiralChainsFound = false;
            }
        }

        // Lets start
        //
        while (allSpiralChainsFound == false) {

            // Set the sequence number for one spiral chain
            //
            spiralChainVertexSequenceNumber = 1;

            // Set the starting vertex. We are at the beginning of spiral
            //
            if (spiralChainNumber == 1) {

                // Move to the external vertex as required by user
                // In case of default, the coordinate after "1b+" is always visible
                // 1b+, 2b+, 3b+, 2e-, 3e+, 1e+
                //
                if (startingVertexName.compareTo("default") == 0) {
                    currentVertex = verticesMap.get(map4CT.sequenceOfCoordinates.sequence.get(1).toString());
                    currentEdge = currentVertex.edgeAtLeft;
                } else {
                    currentVertex = verticesMap.get(startingVertexName);
                    if (currentVertex == null) {
                        currentVertex = verticesMap.get(map4CT.sequenceOfCoordinates.sequence.get(1).toString());
                    }
                    currentEdge = currentVertex.edgeAtLeft;
                }

                // Set the vertex and two incident edges (but not the one I'm moving to) as "used"
                //
                currentVertex.used = true;
                currentVertex.edgeAtLeft.used = true;
                currentVertex.spiralChainNumber = spiralChainNumber; // 1
                currentVertex.spiralChainSequenceNumber = spiralChainVertexSequenceNumber; // 1

                // Increment the number of vertices selected
                //
                numberOfAllUsedVerticesOfTheGraph++;

                // Color the spiral
                //
                setColorOfUsedFirstVertex(currentVertex);
            } else {

                // Move to the closest unused vertex to the last vertex of the last spiral chain
                // NOTE: Why is it mathematically important to move to the closest? Would a random unused vertex be the same?
                //
                Vertex lastVertexOfPreviousSpiral = currentVertex;
                Vertex closestVertex = null;
                double shortestDistance = -1;
                FloydWarshallShortestPaths<String, DefaultEdge> floydWarshallShortestPaths = new FloydWarshallShortestPaths<String, DefaultEdge>(jGraphT4CT);

                currentVertex = null;
                currentEdge = null;

                // Analyze all unused vertices and gets the shortestr distance() respect to the lastVertexOfPreviousSpiral
                //
                Iterator<Entry<String, Vertex>> iter = verticesMap.entrySet().iterator();
                while (iter.hasNext() && (currentVertex == null)) {
                    Vertex vertexTemp = iter.next().getValue();
                    if (vertexTemp.used == false) {
                        double distanceTemp = floydWarshallShortestPaths.shortestDistance(vertexTemp.name, lastVertexOfPreviousSpiral.name);
                        if ((shortestDistance == -1) || (distanceTemp < shortestDistance)) {
                            shortestDistance = distanceTemp;
                            closestVertex = vertexTemp;
                        }
                    }
                }
                currentVertex = closestVertex;

                // Decide which edge is the current Edge
                //
                // If all three edges take to vertices the are all used, it means this is an isolated vertex
                //
                if (currentVertex.edgeAtLeft.getTheOtherVertex(currentVertex).used) {
                    currentVertex.edgeAtLeft.used = true;
                    currentEdge = currentVertex.edgeAtLeft;
                }
                if (currentVertex.edgeAtRight.getTheOtherVertex(currentVertex).used) {
                    currentVertex.edgeAtRight.used = true;
                    currentEdge = currentVertex.edgeAtRight;
                }
                if (currentVertex.edgeAtBottom.getTheOtherVertex(currentVertex).used) {
                    currentVertex.edgeAtBottom.used = true;
                    currentEdge = currentVertex.edgeAtBottom;
                }

                // Set the vertex and two incident edges (but not the one I'm moving to) as "used"
                //
                currentVertex.used = true;
                currentEdge.used = true;
                currentVertex.spiralChainNumber = spiralChainNumber;
                currentVertex.spiralChainSequenceNumber = spiralChainVertexSequenceNumber; // 1

                // Increment the number of vertices selected
                //
                numberOfAllUsedVerticesOfTheGraph++;

                // Color the spiral
                //
                setColorOfUsedFirstVertex(currentVertex);
            }

            // Reset counters for the new spiral
            //
            boolean spiralChainCompleted = false;

            // Begin the spiral
            //
            while (spiralChainCompleted == false) {

                // Save the previous positions
                //
                previousVertex = currentVertex;
                previousEdge = currentEdge;

                // Move to the next edge and vertex
                //
                currentEdge = previousVertex.getTheEdgeToTurnLeftOrRightIfLeftIsUsed(previousEdge);

                // Set as used the edge at right (respect to the coming from edge)
                //
                previousVertex.getTheEdgeAtRight(previousEdge).used = true;

                // If all edges have been used, the spiral is finished
                //
                if (currentEdge == null) {
                    spiralChainCompleted = true;
                } else {

                    // Move to the next vertex
                    //
                    currentVertex = currentEdge.getTheOtherVertex(previousVertex);

                    // Set used vertices and incident edges
                    //
                    currentVertex.used = true;
                    currentEdge.used = true;

                    // Increment the number of total used vertices and the spiralChainSequenceNumber
                    //
                    numberOfAllUsedVerticesOfTheGraph = numberOfAllUsedVerticesOfTheGraph + 1;
                    spiralChainVertexSequenceNumber++;

                    // Counters for spiral chains
                    //
                    currentVertex.spiralChainNumber = spiralChainNumber;
                    currentVertex.spiralChainSequenceNumber = spiralChainVertexSequenceNumber;

                    // Color the spiral
                    //
                    setColorOfUsedVertex(currentVertex);
                    setColorOfUsedEdge(currentEdge, autoColor);
                }
            }

            // V = (F - 2) * 2
            //
            if (numberOfAllUsedVerticesOfTheGraph == (map4CT.sequenceOfCoordinates.sequence.size() - 2)) {
                allSpiralChainsFound = true;
            } else {
                spiralChainNumber = spiralChainNumber + 1;
            }
        }

        // Return
        //
        return spiralChainNumber;
    }

    /**
     * Set the color of the first vertex. It may be colored differently
     * 
     * @param currentVertex
     */
    public void setColorOfUsedFirstVertex(Vertex currentVertex) {
        currentVertex.color = Vertex.FIRST_USED_FILL_COLOR;
        mxCell vertexToColor = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(currentVertex.toString());
        jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Vertex.USED_COLOR), new Object[] { vertexToColor });
        jGraph4CT.setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Vertex.FIRST_USED_FILL_COLOR), new Object[] { vertexToColor });
        jGraph4CTComponent.refresh();
    }

    /**
     * Set the color of the vertex
     * 
     * @param currentVertex
     */
    public void setColorOfUsedVertex(Vertex currentVertex) {
        currentVertex.color = Vertex.USED_COLOR;
        mxCell vertexToColor = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(currentVertex.toString());
        jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Vertex.USED_COLOR), new Object[] { vertexToColor });
        jGraph4CT.setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Vertex.USED_FILL_COLOR), new Object[] { vertexToColor });
        jGraph4CTComponent.refresh();
    }

    /**
     * Set the color of the edge
     * 
     * @param currentEdge
     */
    public void setColorOfUsedEdge(Edge currentEdge, boolean autoColor) {

        if (autoColor) {

            // Rotate colors and change it
            // If a color has already be used by the neighbours ... don't use it
            //
            setAValidColor(currentEdge);
        } else {
            currentEdge.color = Edge.USED_COLOR;
        }

        mxCell edgeToColor = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(currentEdge.toString());
        jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(currentEdge.color), new Object[] { edgeToColor });
        jGraph4CT.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "4", new Object[] { edgeToColor });
        jGraph4CTComponent.refresh();
    }

    private void setAValidColor(Edge edge) {

        // Rotate colors and change it
        // If a color has already be used by the neighbours ... don't use it
        //
        boolean isJobFinished = false;
        Color originalColor = edge.color;

        // Start the search
        //
        for (int i = 0; (i < 3) && (isJobFinished == false); i++) {
            if (edge.color == Color.lightGray) {
                isJobFinished = !isTheColorInTheNeighood(edge, Color.red);
                edge.color = Color.red;
            } else if (edge.color == Edge.USED_COLOR) {
                isJobFinished = !isTheColorInTheNeighood(edge, Color.red);
                edge.color = Color.red;
            } else if (edge.color == Color.red) {
                isJobFinished = !isTheColorInTheNeighood(edge, Color.green);
                edge.color = Color.green;
            } else if (edge.color == Color.green) {
                isJobFinished = !isTheColorInTheNeighood(edge, Color.blue);
                edge.color = Color.blue;
            } else if (edge.color == Color.blue) {
                isJobFinished = !isTheColorInTheNeighood(edge, Color.lightGray);
                edge.color = Color.lightGray;
            }
        }

        // If a color has already be used by the neighbours ... don't use it
        // if I tryed all possibilities ... reset
        //
        if (isJobFinished == false) {
            edge.color = originalColor;
        }
    }

    /**
     * Create the graph (update jGraph4CT) from the sequence of coordinates of the current map: 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
     */
    public void drawGraphTest() {

        // Start the transaction to modify the graph
        //
        jGraph4CT.getModel().beginUpdate();

        // Create a new Graph
        //
        Vertex v1 = new Vertex();
        v1.name = "v1";
        Vertex v2 = new Vertex();
        v2.name = "v2";

        Edge e1 = new Edge();
        e1.name = v1.name + "/" + v2.name;

        Object v1O = jGraph4CT.insertVertex(jGraph4CT.getDefaultParent(), v1.name, v1, 100, 100, Vertex.DIAMETER, Vertex.DIAMETER, "MyVertexStyle");
        Object v2O = jGraph4CT.insertVertex(jGraph4CT.getDefaultParent(), v2.name, v2, 140, 140, Vertex.DIAMETER, Vertex.DIAMETER, "MyVertexStyle");

        jGraph4CT.insertEdge(jGraph4CT.getDefaultParent(), e1.name, e1, v1O, v2O, "MyEdgeStyle");

        // jGraph4CT.getModel().endUpdate();
        //
        // jGraph4CT.getModel().beginUpdate();

        mxCell vertexToChange = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(v1.name);
        mxGeometry geometryOfVertex = vertexToChange.getGeometry();

        mxCell edgeToChange = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell("v1/v2");
        mxGeometry geometryOfEdge = edgeToChange.getGeometry();
        List<mxPoint> pointsOfTheEdge = geometryOfEdge.getPoints();
        if (pointsOfTheEdge == null) {
            pointsOfTheEdge = new ArrayList<mxPoint>();
        }
        pointsOfTheEdge.add(new mxPoint(100 + (geometryOfVertex.getWidth() / 2), 140 + (geometryOfVertex.getHeight() / 2)));
        // pointsOfTheEdge.add(new mxPoint(500, 400));
        geometryOfEdge.setPoints(pointsOfTheEdge);
        edgeToChange.setGeometry(geometryOfEdge);

        jGraph4CT.getModel().endUpdate();
    }

    /**
     * Draw the graph to the model
     * 
     * @param map4CTToDraw
     */
    public void drawGraph(Map4CT map4CTToDraw) {

        // Set the map to elaborate
        //
        map4CT = map4CTToDraw;

        // Temp variable
        //
        FCoordinate fTempPreviousCoordinate = null;
        FCoordinate fTempCurrentCoordinate = null;
        FCoordinate fTempCoordinate = null;

        // Variables to distinguish the various cases (B-E, B-M, M-M, M-E)
        //
        Vertex.TYPE_OF_VERTEX typeOfPreviousVertex = Vertex.TYPE_OF_VERTEX.NOT_DEFINED;
        Vertex.TYPE_OF_VERTEX typeOfCurrentVertex = Vertex.TYPE_OF_VERTEX.NOT_DEFINED;

        // Maintain a list of hidden vertices (temp). Used to compute edge's endpoints
        //
        List<String> hiddenVertices = new ArrayList<String>();

        // Maintain a map of vertices and edges
        //
        verticesMap = new HashMap<String, Vertex>();
        edgesMap = new HashMap<String, Edge>();

        // Start the transaction to modify the graph
        //
        jGraph4CT.getModel().beginUpdate();

        // Create the graph, analyzing all submaps, rebuilding the original map step by step, face by face
        // NOTE: The first face is not 0 but 1 (don't remember why I named it starting from 1)
        //
        for (int iFace = 2; iFace < (map4CT.sequenceOfCoordinates.sequence.size() / 2) + 1; iFace++) {

            // Create the sub map with iFace faces
            //
            Map4CT subMap = MapsGenerator.createMapFromTextRepresentation(map4CT.toString(), iFace);

            // These two variable will be useful also to create edges and to represent left and right once finished
            //
            Vertex previousVertex = null;
            Vertex currentVertex = null;

            // Skip to the BEGIN coordinate and set the location characteristics
            //
            int iCoordinate = 1;
            while (subMap.sequenceOfCoordinates.sequence.get(iCoordinate).fNumber != iFace) {
                iCoordinate++;
            }
            typeOfPreviousVertex = Vertex.TYPE_OF_VERTEX.NOT_DEFINED;
            typeOfCurrentVertex = Vertex.TYPE_OF_VERTEX.BEGIN;
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
                    typeOfCurrentVertex = Vertex.TYPE_OF_VERTEX.MIDDLE;
                    fTempPreviousCoordinate = fTempCurrentCoordinate;
                    fTempCurrentCoordinate = subMap.sequenceOfCoordinates.sequence.get(iCoordinate);

                    // Add to hidden, so next time it will be skipped
                    //
                    hiddenVertices.add(fTempCurrentCoordinate.toString());

                    // Handle the case and create the vertices
                    //
                    if ((typeOfPreviousVertex == Vertex.TYPE_OF_VERTEX.BEGIN) && (typeOfCurrentVertex == Vertex.TYPE_OF_VERTEX.MIDDLE)) {
                        previousVertex = addVertexToGraph(verticesMap, iFace, FCoordinate.TYPE.BEGIN);
                        fTempCoordinate = searchFCoordinateInMap(fTempCurrentCoordinate.fNumber, fTempCurrentCoordinate.type);
                        currentVertex = verticesMap.get(fTempCoordinate.toString());
                        setVertexCoordinate(currentVertex, iFace, fTempCoordinate);
                        addEdgeBottomLeft(previousVertex, currentVertex, Edge.SHAPE_TYPE.L);
                    } else if ((typeOfPreviousVertex == Vertex.TYPE_OF_VERTEX.MIDDLE) && (typeOfCurrentVertex == Vertex.TYPE_OF_VERTEX.MIDDLE)) {
                        previousVertex = verticesMap.get(searchFCoordinateInMap(fTempPreviousCoordinate.fNumber, fTempPreviousCoordinate.type).toString());
                        fTempCoordinate = searchFCoordinateInMap(fTempCurrentCoordinate.fNumber, fTempCurrentCoordinate.type);
                        currentVertex = verticesMap.get(fTempCoordinate.toString());
                        setVertexCoordinate(currentVertex, iFace, fTempCoordinate);
                        addEdgeRightLeft(previousVertex, currentVertex, Edge.SHAPE_TYPE.STRAIGHT);
                    }
                }

                // Move to next coordinate
                //
                iCoordinate++;
            }

            // At this point of code, the coordinate is an END
            //
            typeOfPreviousVertex = typeOfCurrentVertex;
            typeOfCurrentVertex = Vertex.TYPE_OF_VERTEX.END;
            fTempPreviousCoordinate = fTempCurrentCoordinate;
            fTempCurrentCoordinate = subMap.sequenceOfCoordinates.sequence.get(iCoordinate);

            // Handle the case and create the vertices
            //
            if ((typeOfPreviousVertex == Vertex.TYPE_OF_VERTEX.BEGIN) && (typeOfCurrentVertex == Vertex.TYPE_OF_VERTEX.END)) {
                previousVertex = addVertexToGraph(verticesMap, iFace, FCoordinate.TYPE.BEGIN);
                currentVertex = addVertexToGraph(verticesMap, iFace, FCoordinate.TYPE.END);
                addEdgeBottomBottom(previousVertex, currentVertex, Edge.SHAPE_TYPE.U);
            } else if ((typeOfPreviousVertex == Vertex.TYPE_OF_VERTEX.MIDDLE) && (typeOfCurrentVertex == Vertex.TYPE_OF_VERTEX.END)) {
                previousVertex = verticesMap.get(searchFCoordinateInMap(fTempPreviousCoordinate.fNumber, fTempPreviousCoordinate.type).toString());
                currentVertex = addVertexToGraph(verticesMap, iFace, FCoordinate.TYPE.END);
                addEdgeRightBottom(previousVertex, currentVertex, Edge.SHAPE_TYPE.MIRRORED_L);
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
        while (map4CT.sequenceOfCoordinates.sequence.get(iCoordinate).isVisible == false) {
            iCoordinate++;
        }
        previousVertex = null;
        fTempCoordinate = map4CT.sequenceOfCoordinates.sequence.get(iCoordinate);
        currentVertex = verticesMap.get(fTempCoordinate.toString());
        setVertexCoordinate(currentVertex, map4CT.faces.size() + 1, fTempCoordinate);

        // Step ahead and loop until the end of all visible coordinates (excluding 1e+)
        //
        iCoordinate++;
        for (; iCoordinate < (map4CT.sequenceOfCoordinates.sequence.size() - 1); iCoordinate++) {
            if (map4CT.sequenceOfCoordinates.sequence.get(iCoordinate).isVisible) {
                previousVertex = currentVertex;
                fTempCoordinate = map4CT.sequenceOfCoordinates.sequence.get(iCoordinate);
                currentVertex = verticesMap.get(fTempCoordinate.toString());
                setVertexCoordinate(currentVertex, map4CT.faces.size() + 1, fTempCoordinate);
                addEdgeRightLeft(previousVertex, currentVertex, Edge.SHAPE_TYPE.STRAIGHT);
            }
        }

        // Second coordinate and second-last coordinate are neighbour
        //
        previousVertex = verticesMap.get(map4CT.sequenceOfCoordinates.sequence.get(1).toString());
        currentVertex = verticesMap.get(map4CT.sequenceOfCoordinates.sequence.get(map4CT.sequenceOfCoordinates.sequence.size() - 2).toString());
        addEdgeLeftRight(previousVertex, currentVertex, Edge.SHAPE_TYPE.OCEAN);

        // End the transaction (modify the graph)
        //
        jGraph4CT.getModel().endUpdate();

        // Apply a layout:
        //
        // Rectangular layout: I will set the position of vertices and create the shape of the edges
        // For the other layouts: Execute the out-of-the-box layout (set by the user)
        //
        if (graphLayout == GRAPH_LAYOUT.RECTANGULAR) {

            // Reset
            //
            jGraph4CT.getModel().beginUpdate();
            jGraph4CT.getView().setScale(1);
            jGraph4CT.getModel().endUpdate();

            // Scale
            //
            jGraph4CT.getModel().beginUpdate();
            manualLayoutToRectangular();
            jGraph4CT.getModel().endUpdate();
        } else {
            jGraph4CT.getModel().beginUpdate();
            jGraph4CTLayout.execute(jGraph4CT.getDefaultParent());
            jGraph4CT.getModel().endUpdate();
        }
    }

    /**
     * Add a new Vertex to the graph
     * 
     * @param verticesMapToUpdate
     * @param iFace
     * @param fCoordinateType
     * @return the new vertex
     */
    public Vertex addVertexToGraph(Map<String, Vertex> verticesMapToUpdate, int iFace, FCoordinate.TYPE fCoordinateType) {

        // Add another vertex
        //
        FCoordinate fRealCoordinate = searchFCoordinateInMap(iFace, fCoordinateType);
        Vertex vertexToAdd = new Vertex();
        vertexToAdd.name = fRealCoordinate.toString();
        vertexToAdd.faceNumber = iFace;
        if (fRealCoordinate.isVisible) {
            vertexToAdd.isOnTheExcternalCycle = true;
        }
        jGraph4CT.insertVertex(jGraph4CT.getDefaultParent(), vertexToAdd.name, vertexToAdd, 0, 0, Vertex.DIAMETER, Vertex.DIAMETER, "MyVertexStyle");
        jGraphT4CT.addVertex(vertexToAdd.name);

        // Update the list of vertices
        //
        verticesMapToUpdate.put(vertexToAdd.name, vertexToAdd);

        // Return the vertex
        //
        return vertexToAdd;
    }

    /**
     * Search a coordinate (begin or end) in the map's coordinate list
     * 
     * @param iFaceToSearch
     *            Face to search
     * @param typeOfCoordinateToSearch
     *            Type or coordinate (Begin or End)
     * @return The coordinate
     */
    public FCoordinate searchFCoordinateInMap(int iFaceToSearch, FCoordinate.TYPE typeOfCoordinateToSearch) {

        // The coordinate to return
        //
        FCoordinate fCoordinateToReturn = null;

        // Search the coordinates up to the begin of the face being analyzed
        //
        for (int i = 0; (i < map4CT.sequenceOfCoordinates.sequence.size()) && (fCoordinateToReturn == null); i++) {
            FCoordinate fCoordinateTemp = map4CT.sequenceOfCoordinates.sequence.get(i);
            if ((fCoordinateTemp.fNumber == iFaceToSearch) && (fCoordinateTemp.type == typeOfCoordinateToSearch)) {
                fCoordinateToReturn = fCoordinateTemp;
            }
        }

        // Return
        // my
        return fCoordinateToReturn;
    }

    /**
     * Scale the graph to fit the window.
     * 
     * @param height
     * @param width
     */
    public void scaleToFitTheWindow() {

        // Begin update
        //
        jGraph4CT.getModel().beginUpdate();

        // Get the real size
        //
        mxRectangle currentDimentionOfTheGraph = jGraph4CT.getView().getGraphBounds();

        // If the graph is larger than tall, it has to fit into the window height. If it is taller than larger, ...
        // If it is a square, it has to fit min(height, width)
        //
        double maxValueOfGraph = -1;
        double maxValueOfWindow = -1;
        if (currentDimentionOfTheGraph.getHeight() > currentDimentionOfTheGraph.getWidth()) {
            maxValueOfGraph = currentDimentionOfTheGraph.getHeight();
            maxValueOfWindow = graphHeight;
        } else if (currentDimentionOfTheGraph.getHeight() < currentDimentionOfTheGraph.getWidth()) {
            maxValueOfGraph = currentDimentionOfTheGraph.getWidth();
            maxValueOfWindow = graphWidth;
        } else {
            maxValueOfGraph = currentDimentionOfTheGraph.getHeight();
        }

        // Compute and set the new scale (addin a little margin)
        //
        double scale = (maxValueOfWindow / maxValueOfGraph) - 0.01;
        jGraph4CT.getView().setScale(scale);

        // Update
        //
        jGraph4CT.getModel().endUpdate();
    }

    public void scaleToDefault() {
        jGraph4CT.getModel().beginUpdate();
        jGraph4CT.getView().setScale(1);
        jGraph4CT.getModel().endUpdate();

    }

    /**
     * Create a new Edge and set the orientation respect to vertices (used to compute left and right)
     * 
     * @param firstVertex
     * @param secondVertex
     * @param shapeTypeToSet
     */
    public void addEdgeBottomLeft(Vertex firstVertex, Vertex secondVertex, Edge.SHAPE_TYPE shapeTypeToSet) {
        Edge edgeToCreate = addEdgeGeneric(firstVertex, secondVertex, shapeTypeToSet);
        firstVertex.edgeAtBottom = edgeToCreate;
        secondVertex.edgeAtLeft = edgeToCreate;
    }

    /**
     * Create a new Edge and set the orientation respect to vertices (used to compute left and right)
     * 
     * @param firstVertex
     * @param secondVertex
     * @param shapeTypeToSet
     */
    public void addEdgeRightLeft(Vertex firstVertex, Vertex secondVertex, Edge.SHAPE_TYPE shapeTypeToSet) {
        Edge edgeToCreate = addEdgeGeneric(firstVertex, secondVertex, shapeTypeToSet);
        firstVertex.edgeAtRight = edgeToCreate;
        secondVertex.edgeAtLeft = edgeToCreate;
    }

    /**
     * Create a new Edge and set the orientation respect to vertices (used to compute left and right)
     * 
     * @param firstVertex
     * @param secondVertex
     * @param shapeTypeToSet
     */
    public void addEdgeLeftRight(Vertex firstVertex, Vertex secondVertex, Edge.SHAPE_TYPE shapeTypeToSet) {
        Edge edgeToCreate = addEdgeGeneric(firstVertex, secondVertex, shapeTypeToSet);
        firstVertex.edgeAtLeft = edgeToCreate;
        secondVertex.edgeAtRight = edgeToCreate;
    }

    /**
     * Create a new Edge and set the orientation respect to vertices (used to compute left and right)
     * 
     * @param firstVertex
     * @param secondVertex
     * @param shapeTypeToSet
     */
    public void addEdgeBottomBottom(Vertex firstVertex, Vertex secondVertex, Edge.SHAPE_TYPE shapeTypeToSet) {
        Edge edgeToCreate = addEdgeGeneric(firstVertex, secondVertex, shapeTypeToSet);
        firstVertex.edgeAtBottom = edgeToCreate;
        secondVertex.edgeAtBottom = edgeToCreate;
    }

    /**
     * Create a new Edge and set the orientation respect to vertices (used to compute left and right)
     * 
     * @param firstVertex
     * @param secondVertex
     * @param shapeTypeToSet
     */
    public void addEdgeRightBottom(Vertex firstVertex, Vertex secondVertex, Edge.SHAPE_TYPE shapeTypeToSet) {
        Edge edgeToCreate = addEdgeGeneric(firstVertex, secondVertex, shapeTypeToSet);
        firstVertex.edgeAtRight = edgeToCreate;
        secondVertex.edgeAtBottom = edgeToCreate;
    }

    /**
     * Create a new Edge
     * 
     * @param firstVertex
     * @param secondVertex
     * @param shapeTypeToSet
     */
    public Edge addEdgeGeneric(Vertex firstVertex, Vertex secondVertex, Edge.SHAPE_TYPE shapeTypeToSet) {
        Edge edgeToCreate = new Edge();
        edgeToCreate.name = firstVertex.name + "/" + secondVertex.name + shapeTypeToSet;
        edgeToCreate.shapeType = shapeTypeToSet;
        edgeToCreate.firstVertex = firstVertex;
        edgeToCreate.secondVertex = secondVertex;
        edgesMap.put(edgeToCreate.name, edgeToCreate);
        mxCell firstVertexRaw = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(firstVertex.name);
        mxCell secondVertexRaw = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(secondVertex.name);
        jGraph4CT.insertEdge(jGraph4CT.getDefaultParent(), edgeToCreate.name, edgeToCreate, firstVertexRaw, secondVertexRaw, "MyEdgeStyle");
        jGraphT4CT.addEdge(firstVertex.name, secondVertex.name);
        sSpaceGraph4CT.add(new SimpleEdge(firstVertex.name.hashCode(), secondVertex.name.hashCode()));
        
        return edgeToCreate;
    }

    /**
     * Create a rectangular graph.
     */
    public void manualLayoutToRectangular() {

        // Add points to the geometry (have to be the same that have been set in the setVertexCoordinates method)
        //
        double maxY = graphHeight - (GRAPH_MARGIN * 2);
        double stepY = maxY / map4CT.faces.size();
        mxCell cellToChange = null;
        mxGeometry geometryToChange = null;

        // Iter all vertices, get the cells representing the vertices and set the location
        //
        for (Vertex vertex : verticesMap.values()) {
            cellToChange = (mxCell) ((mxGraphModel) jGraph4CT.getModel()).getCell(vertex.toString());
            ((mxGraphLayout) jGraph4CTLayout).setVertexLocation(cellToChange, vertex.xCoordinate, vertex.yCoordinate);
        }

        // Iter all edges, get the cells representing the edges and modify the edge points
        //
        for (Edge edge : edgesMap.values()) {

            // Gets the points representing the Edge
            //
            cellToChange = (mxCell) ((mxGraphModel) (jGraph4CT.getModel())).getCell(edge.name);
            geometryToChange = jGraph4CT.getModel().getGeometry(cellToChange);
            geometryToChange = (mxGeometry) geometryToChange.clone();
            List<mxPoint> pointsOfTheEdge = geometryToChange.getPoints();
            if (pointsOfTheEdge == null) {
                pointsOfTheEdge = new ArrayList<mxPoint>();
            }

            // Add points to the shape of the Edge depending on its type
            // Don't know why yAdjust is set this way. I computed it this by trials: *2, /2, *3.14, ... ;-)
            //
            int xAdjust = Vertex.DIAMETER / 2;
            int yAdjust = Vertex.DIAMETER;

            if (edge.shapeType == Edge.SHAPE_TYPE.L) {
                pointsOfTheEdge.add(new mxPoint(edge.firstVertex.xCoordinate + xAdjust, edge.secondVertex.yCoordinate + yAdjust));
            } else if (edge.shapeType == Edge.SHAPE_TYPE.MIRRORED_L) {
                pointsOfTheEdge.add(new mxPoint(edge.secondVertex.xCoordinate + xAdjust, edge.firstVertex.yCoordinate + yAdjust));
            } else if (edge.shapeType == Edge.SHAPE_TYPE.U) {
                int yTempCoordinate = (int) (GRAPH_MARGIN + maxY - ((edge.firstVertex.faceNumber - 1) * stepY));
                pointsOfTheEdge.add(new mxPoint(edge.firstVertex.xCoordinate + xAdjust, yTempCoordinate + yAdjust));
                pointsOfTheEdge.add(new mxPoint(edge.secondVertex.xCoordinate + xAdjust, yTempCoordinate + yAdjust));
            } else if (edge.shapeType == Edge.SHAPE_TYPE.OCEAN) {
                pointsOfTheEdge.add(new mxPoint(edge.firstVertex.xCoordinate - (GRAPH_MARGIN / 2) + xAdjust, edge.firstVertex.yCoordinate + yAdjust));
                pointsOfTheEdge.add(new mxPoint(edge.firstVertex.xCoordinate - (GRAPH_MARGIN / 2) + xAdjust, edge.firstVertex.yCoordinate + maxY + yAdjust));
                pointsOfTheEdge.add(new mxPoint(edge.secondVertex.xCoordinate + (GRAPH_MARGIN / 2) + xAdjust, edge.secondVertex.yCoordinate + maxY + yAdjust));
                pointsOfTheEdge.add(new mxPoint(edge.secondVertex.xCoordinate + (GRAPH_MARGIN / 2) + xAdjust, edge.secondVertex.yCoordinate + yAdjust));
            }

            // Set the geometry back to the edge
            //
            geometryToChange.setPoints(pointsOfTheEdge);
            jGraph4CT.getModel().setGeometry(cellToChange, geometryToChange);
        }
    }

    /**
     * Set the position of a vertex.
     * 
     * @param vertex
     * @param vertexAtFaceNumber
     * @param fCoordinateToSearch
     */
    public void setVertexCoordinate(Vertex vertex, int vertexAtFaceNumber, FCoordinate fCoordinateToSearch) {

        // Margin for all borders
        //
        double maxX = graphWidth - (GRAPH_MARGIN * 2);
        double maxY = graphHeight - (GRAPH_MARGIN * 2);
        double stepX = maxX / (map4CT.sequenceOfCoordinates.sequence.size() - 2 - 1); // 4 vertices divide the maxX in 3 segments. 1b+, 1e+ are also not considered
        double stepY = maxY / map4CT.faces.size();

        // Search the position of the coordinate (I can start from 1)
        //
        int iPositionOfCoordinate = 1;
        while (map4CT.sequenceOfCoordinates.sequence.get(iPositionOfCoordinate).toString().compareTo(fCoordinateToSearch.toString()) != 0) {
            iPositionOfCoordinate++;
        }

        // Set the coordinates
        //
        vertex.xCoordinate = (int) (GRAPH_MARGIN + ((iPositionOfCoordinate - 1) * stepX));
        vertex.yCoordinate = (int) (GRAPH_MARGIN + maxY - ((vertexAtFaceNumber - 1) * stepY));
    }

    // TODO: New method to switch colors. First select a chain (dashed lines), then switch
    //    
    private void kempeSwitchColorsNewTemp(Edge edge) {

        // First I need to check if the dashed edges are a chain?
        //
        
        jGraph4CT.clearSelection();
        jGraph4CTComponent.refresh();
    }

    private void kempeSwitchColors(Edge edge) {

        // The color of the chain are
        // one = the color of the edge selected with the mouse
        // two = the property set by the used (UI)
        //
        // To check:
        // - If the chain is a loop
        // - if the selected edge.color is equal to second color or not colored
        //
        boolean chainSwitched = false;
        boolean isTheChainALoop = false;

        Color currentColorToSwitch = edge.color;
        Color nextColorToSwitch = secondColorOfKempeSwitch;

        Edge currentEdge = edge;

        // Variables to check if you started switching color from the middle of a chain
        //
        Edge nextEdgeAtTheOtherDirection = null;
        Color nextColorForTheOtherDirectionToUseLater = edge.color;

        // If the selected edge has no color, switch cannot start
        // If the selected edge has the same color of the second color that has been set, switch cannot start
        //
        if ((edge.color == Color.lightGray) || (edge.color == nextColorToSwitch)) {
            chainSwitched = true;
        } else {

            // Check if you started switching color from the middle of a chain
            //
            Edge nextEdgeAtOneDirectionTmp = null;
            if ((nextEdgeAtOneDirectionTmp = findTheEdgeWithTheNextColorOfTheChain(currentEdge, nextColorToSwitch)) != null) {

                // In case is null, it means that you started switching form one end (not in the middle)
                //
                nextEdgeAtTheOtherDirection = findTheOtherEdgeWithTheNextColorOfTheChain(edge, nextColorToSwitch, nextEdgeAtOneDirectionTmp);
            } else {
                // This is in the case the chain was only made of one edge (not a chain)
                //
                chainSwitched = true;
            }
        }

        // Continue until you find one end of the chain (chech also if it is a loop)
        //
        while (!chainSwitched) {
            Edge nextEdge = findTheEdgeWithTheNextColorOfTheChain(currentEdge, nextColorToSwitch);

            // Chain (or loop) is finished == I didn't find another color to switch
            //
            if (nextEdge == null) {

                chainSwitched = true;
                currentEdge.color = nextColorToSwitch;

                Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(currentEdge.color), new Object[] { (mxCell) cellToModify });

                // This is in case of loops
                // In this case I did't find the next edge, because in loops at the end you have
                //
                if (currentEdge == nextEdgeAtTheOtherDirection) {
                    isTheChainALoop = true;
                }
            } else {

                // Change color and move to the next edge
                //
                currentEdge.color = nextColorToSwitch;

                Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(currentEdge.color), new Object[] { (mxCell) cellToModify });

                currentEdge = nextEdge;
                nextColorToSwitch = currentColorToSwitch;
                currentColorToSwitch = currentEdge.color;
            }
        }

        // If I had to continue the other side
        //
        if ((nextEdgeAtTheOtherDirection != null) && (isTheChainALoop != true)) {

            // Reset the flag and continue the other side
            //
            chainSwitched = false;
            currentEdge = nextEdgeAtTheOtherDirection;
            nextColorToSwitch = nextColorForTheOtherDirectionToUseLater;
            currentColorToSwitch = currentEdge.color;

            // Up to the end of the chain
            //
            while (!chainSwitched) {
                Edge nextEdge = findTheEdgeWithTheNextColorOfTheChain(currentEdge, nextColorToSwitch);
                if (nextEdge == null) {

                    // Chain if finished, I didn't find another color to switch
                    //
                    chainSwitched = true;
                    currentEdge.color = nextColorToSwitch;

                    Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                    jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(currentEdge.color), new Object[] { (mxCell) cellToModify });
                } else {

                    // Change color and move to the next edge
                    //
                    currentEdge.color = nextColorToSwitch;

                    Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                    jGraph4CT.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(currentEdge.color), new Object[] { (mxCell) cellToModify });

                    currentEdge = nextEdge;
                    nextColorToSwitch = currentColorToSwitch;
                    currentColorToSwitch = currentEdge.color;
                }
            }
        }

        jGraph4CT.clearSelection();
        jGraph4CTComponent.refresh();
    }

    private void showKempeChain(Edge edge, Color nextColorToHighlight) {

        // The color of the chain are
        // one = the color of the edge selected with the mouse
        // two = the property set by the used (UI)
        //
        // To check:
        // - If the chain is a loop
        // - if the selected edge.color is equal to second color or not colored
        //
        boolean chainHighlighted = false;
        boolean isTheChainALoop = false;

        Edge currentEdge = edge;

        // For every visit to this function alternate dash and remove, dash and remove, dash and remove, ...
        //
        String dashedStyle = null;
        if (isDashed) {
            isDashed = false;
            dashedStyle = "0";
        } else {
            isDashed = true;
            dashedStyle = "1";
        }
        
        // Variables to check if you started highlighting colors from the middle of a chain
        //
        Edge nextEdgeAtTheOtherDirection = null;
        Color nextColorForTheOtherDirectionToUseLater = edge.color;

        Edge notThisEdgeGoTheOtherDirection = null;
        Edge firstEdge = edge;

        // If the selected edge has no color, highlight cannot start
        // If the selected edge has the same color of the second color that has been set, highlight cannot start
        //
        if ((edge.color == Color.lightGray) || (edge.color == nextColorToHighlight)) {
            chainHighlighted = true;
        } else {

            // Check if you started highlighting color from the middle of a chain
            //
            Edge nextEdgeAtOneDirectionTmp = null;
            if ((nextEdgeAtOneDirectionTmp = findTheEdgeWithTheNextColorOfTheChain(currentEdge, nextColorToHighlight)) != null) {

                // In case is null, it means that you started highlighting form one end (not in the middle)
                //
                nextEdgeAtTheOtherDirection = findTheOtherEdgeWithTheNextColorOfTheChain(edge, nextColorToHighlight, nextEdgeAtOneDirectionTmp);
                notThisEdgeGoTheOtherDirection = nextEdgeAtTheOtherDirection;
            } else {

                // This is in the case the chain was only made of one edge (not a chain)
                //
                chainHighlighted = true;
            }
        }

        // Continue until you find one end of the chain (check also if it is a loop)
        //
        while (!chainHighlighted) {
            Edge nextEdge = findTheOtherEdgeWithTheNextColorOfTheChain(currentEdge, nextColorToHighlight, notThisEdgeGoTheOtherDirection);

            // This is one end of a chain (or loop) == I didn't find another color to highlight
            //
            if (nextEdge == null) {

                chainHighlighted = true;

                Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                jGraph4CT.setCellStyles(mxConstants.STYLE_DASHED, dashedStyle, new Object[] { (mxCell) cellToModify });
            } else {

                // Highlight color and move to the next edge
                //
                Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                jGraph4CT.setCellStyles(mxConstants.STYLE_DASHED, dashedStyle, new Object[] { (mxCell) cellToModify });

                // This is in case of loops
                // In this case I did't find the next edge, because in loops at the end you have
                //
                if (currentEdge == nextEdgeAtTheOtherDirection) {
                    isTheChainALoop = true;
                    chainHighlighted = true;
                }

                nextColorToHighlight = currentEdge.color;
                notThisEdgeGoTheOtherDirection = currentEdge;
                currentEdge = nextEdge;
            }
        }

        // If I had to continue the other side
        //
        if ((nextEdgeAtTheOtherDirection != null) && (isTheChainALoop != true)) {

            // Reset the flag and continue the other side
            //
            chainHighlighted = false;
            currentEdge = nextEdgeAtTheOtherDirection;
            nextColorToHighlight = nextColorForTheOtherDirectionToUseLater;
            notThisEdgeGoTheOtherDirection = firstEdge;

            // Up to the end of the chain
            //
            while (!chainHighlighted) {
                Edge nextEdge = findTheOtherEdgeWithTheNextColorOfTheChain(currentEdge, nextColorToHighlight, notThisEdgeGoTheOtherDirection);
                if (nextEdge == null) {

                    // Chain if finished, I didn't find another color to Highlight
                    //
                    chainHighlighted = true;

                    Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                    jGraph4CT.setCellStyles(mxConstants.STYLE_DASHED, dashedStyle, new Object[] { (mxCell) cellToModify });
                } else {

                    // Highlight color and move to the next edge
                    //
                    Object cellToModify = getTheCellOfAGivenEdge(currentEdge);
                    jGraph4CT.setCellStyles(mxConstants.STYLE_DASHED, dashedStyle, new Object[] { (mxCell) cellToModify });

                    nextColorToHighlight = currentEdge.color;
                    notThisEdgeGoTheOtherDirection = currentEdge;
                    currentEdge = nextEdge;

                }
            }
        }

        jGraph4CT.clearSelection();
        jGraph4CTComponent.refresh();
    }

    private Object getTheCellOfAGivenEdge(Edge currentEdge) {

        Object cellToReturn = null;
        boolean cellFound = false;

        // Select all (I don't know to do it differently)
        //
        Object[] objs = jGraph4CTComponent.getCells(new Rectangle(0, 0, 10000, 10000));

        // Check all objects
        //
        for (int i = 0; (i < objs.length) && (cellFound == false); i++) {
            if ((((mxCell) objs[i]).getValue()) instanceof Edge) {

                // The Object is an Edge
                //
                Edge edge = (Edge) ((mxCell) objs[i]).getValue();

                // Found? Is the current edge
                //
                if (edge == currentEdge) {
                    cellToReturn = objs[i];
                    cellFound = true;
                }
            }
        }

        return cellToReturn;
    }
}
