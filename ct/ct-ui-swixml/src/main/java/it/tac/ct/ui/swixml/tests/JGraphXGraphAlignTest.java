package it.tac.ct.ui.swixml.tests;

import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class JGraphXGraphAlignTest {

    JFrame frame;

    mxGraph mxgraph;
    mxGraphComponent graphComponent;
    mxGraphLayout layout;

    HashMap<Integer, Object> vertexMap;

    public JGraphXGraphAlignTest(Integer root) {

        // create a frame to put the graph on
        frame = new JFrame("Branching graph");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mxgraph = new mxGraph();
        this.setStyles();
        vertexMap = new HashMap<Integer, Object>();

        // Build a tree layout
        layout = new mxCompactTreeLayout(mxgraph, false);
        ((mxCompactTreeLayout) layout).setLevelDistance(25);
        ((mxCompactTreeLayout) layout).setNodeDistance(50);

        graphComponent = new mxGraphComponent(mxgraph);
        frame.getContentPane().add(graphComponent);
        frame.setVisible(true);

        // Add the root of the tree
        this.addVertex(root);
    }

    private Object addVertex(Integer id) {
        Object vertex = null;
        Object mxDefaultParent = mxgraph.getDefaultParent();
        mxgraph.getModel().beginUpdate();
        try {
            vertex = mxgraph.insertVertex(mxDefaultParent, null, id, 100.0, 100.0, 50, 50, "VERTEX"); // gradientColor=#FF3333;
            vertexMap.put(id, vertex);
        } finally {
            mxgraph.getModel().endUpdate();
        }
        return vertex;
    }

    public void createBranch(Integer parent, Integer child1, Integer child2) {
        Object v1 = this.addVertex(child1);
        Object v2 = this.addVertex(child2);

        Object mxDefaultParent = mxgraph.getDefaultParent();
        mxgraph.getModel().beginUpdate();
        try {
            mxgraph.insertEdge(mxDefaultParent, null, "", vertexMap.get(parent), v1);
            mxgraph.insertEdge(mxDefaultParent, null, "", vertexMap.get(parent), v2);

        } finally {
            mxgraph.getModel().endUpdate();
        }

        layout.execute(mxDefaultParent);
        // this.reallignGraph();
    }

    /*
     * This should re-align the graph such that there are no white borders between the graph and the frame. In addition, there should be a zoom-out if the graph becomes too big.
     */
    public void realignGraph() {
        // Remove white borders
        mxRectangle bounds = graphComponent.getGraph().getGraphBounds();
        mxgraph.getView().setTranslate(new mxPoint(-bounds.getX(), -bounds.getY()));

        // Rescale the graph
        int w = graphComponent.getWidth();
        int h = graphComponent.getHeight();
        double s = Math.min(w / bounds.getWidth(), h / bounds.getHeight());
        s = Math.max(0.6, s);
        mxgraph.getView().setScale(s);

        bounds = graphComponent.getGraph().getGraphBounds();
        frame.setSize(Math.min(1000, (int) bounds.getWidth()), Math.min(1000, (int) bounds.getHeight()));
    }

    // Some style definitions
    private void setStyles() {
        mxStylesheet stylesheet = mxgraph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_OPACITY, 100);
        stylesheet.putCellStyle("VERTEX", style);
    }

    // Build some graphs
    public static void main(String[] args) {
        // Graph 1
        int root = 0;
        JGraphXGraphAlignTest viz = new JGraphXGraphAlignTest(root);
        for (int i = 1; i < 23; i += 2) {
            viz.createBranch(root, i, i + 1);
            root++;
        }
        viz.realignGraph();

        // Graph2
        JGraphXGraphAlignTest viz2 = new JGraphXGraphAlignTest(0);
        viz2.createBranch(0, 14, 1);
        viz2.createBranch(1, 13, 2);
        for (int i = 2; i < 13; i += 2) {
            viz2.createBranch(i, i + 2, i + 1);
        }
        viz2.realignGraph();
    }

}