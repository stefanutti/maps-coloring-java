package it.tac.ct.ui.swixml.tests;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public class JGraphXHelloWorld extends JFrame {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2707712944901661771L;

    public JGraphXHelloWorld() {
        super("Hello, World!");

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30);
            graph.insertEdge(parent, "Edge", "Edge", v1, v2);
            // changeGeometryOfAnEdge(graph); // Change geometry of an edge works here
        } finally {
            graph.getModel().endUpdate();
        }

        graph.getModel().beginUpdate();
        changeGeometryOfAnEdge(graph); // Change geometry of an edge DOES NOT work here (another beginUpdate, endUpdate)
        graph.getModel().endUpdate();

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public void changeGeometryOfAnEdge(mxGraph graph) {
        mxCell edgeToChange = (mxCell) ((mxGraphModel) (graph.getModel())).getCell("Edge");
        mxGeometry geometryOfEdge = ((mxGraphModel) (graph.getModel())).getGeometry(edgeToChange);
        geometryOfEdge = (mxGeometry) geometryOfEdge.clone();
        List<mxPoint> pointsOfTheEdge = geometryOfEdge.getPoints();
        if (pointsOfTheEdge == null) {
            pointsOfTheEdge = new ArrayList<mxPoint>();
        }
        pointsOfTheEdge.add(new mxPoint(100, 200));
        geometryOfEdge.setPoints(pointsOfTheEdge);
        ((mxGraphModel) (graph.getModel())).setGeometry(edgeToChange, geometryOfEdge);
    }

    public static void main(String[] args) {
        JGraphXHelloWorld frame = new JGraphXHelloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }
}
