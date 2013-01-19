package it.tac.ct.ui.swixml.tests;

import edu.ucla.sspace.graph.Edge;
import edu.ucla.sspace.graph.Graph;
import edu.ucla.sspace.graph.SimpleEdge;
import edu.ucla.sspace.graph.SparseUndirectedGraph;
import edu.ucla.sspace.graph.isomorphism.IsomorphismTester;
import edu.ucla.sspace.graph.isomorphism.TypedVF2IsomorphismTester;

public final class SSpaceGraphIsomorphicTest {
    private SSpaceGraphIsomorphicTest() {
        Graph<Edge> g1 = new SparseUndirectedGraph();
        g1.add(new SimpleEdge("1".hashCode(), "2".hashCode()));
        g1.add(new SimpleEdge("3".hashCode(), "2".hashCode()));
        g1.add(new SimpleEdge("2".hashCode(), "4".hashCode()));
        g1.add(new SimpleEdge("5".hashCode(), "4".hashCode()));
        g1.add(new SimpleEdge("4".hashCode(), "6".hashCode()));
        g1.add(new SimpleEdge("7".hashCode(), "5".hashCode()));
        g1.add(new SimpleEdge("5".hashCode(), "8".hashCode()));
        g1.add(new SimpleEdge("9".hashCode(), "6".hashCode()));
        g1.add(new SimpleEdge("6".hashCode(), "10".hashCode()));
        g1.add(new SimpleEdge("11".hashCode(), "8".hashCode()));
        g1.add(new SimpleEdge("8".hashCode(), "9".hashCode()));
        g1.add(new SimpleEdge("9".hashCode(), "12".hashCode()));
        g1.add(new SimpleEdge("13".hashCode(), "3".hashCode()));
        g1.add(new SimpleEdge("3".hashCode(), "7".hashCode()));
        g1.add(new SimpleEdge("7".hashCode(), "11".hashCode()));
        g1.add(new SimpleEdge("11".hashCode(), "14".hashCode()));
        g1.add(new SimpleEdge("15".hashCode(), "13".hashCode()));
        g1.add(new SimpleEdge("13".hashCode(), "14".hashCode()));
        g1.add(new SimpleEdge("14".hashCode(), "12".hashCode()));
        g1.add(new SimpleEdge("12".hashCode(), "16".hashCode()));
        g1.add(new SimpleEdge("1".hashCode(), "15".hashCode()));
        g1.add(new SimpleEdge("15".hashCode(), "16".hashCode()));
        g1.add(new SimpleEdge("16".hashCode(), "10".hashCode()));
        g1.add(new SimpleEdge("1".hashCode(), "10".hashCode()));

        Graph<Edge> g2 = new SparseUndirectedGraph();
        g2.add(new SimpleEdge("1".hashCode(), "2".hashCode()));
        g2.add(new SimpleEdge("3".hashCode(), "4".hashCode()));
        g2.add(new SimpleEdge("5".hashCode(), "6".hashCode()));
        g2.add(new SimpleEdge("7".hashCode(), "8".hashCode()));
        g2.add(new SimpleEdge("9".hashCode(), "1".hashCode()));
        g2.add(new SimpleEdge("1".hashCode(), "3".hashCode()));
        g2.add(new SimpleEdge("3".hashCode(), "5".hashCode()));
        g2.add(new SimpleEdge("5".hashCode(), "10".hashCode()));
        g2.add(new SimpleEdge("11".hashCode(), "9".hashCode()));
        g2.add(new SimpleEdge("9".hashCode(), "10".hashCode()));
        g2.add(new SimpleEdge("10".hashCode(), "7".hashCode()));
        g2.add(new SimpleEdge("7".hashCode(), "12".hashCode()));
        g2.add(new SimpleEdge("13".hashCode(), "8".hashCode()));
        g2.add(new SimpleEdge("8".hashCode(), "6".hashCode()));
        g2.add(new SimpleEdge("6".hashCode(), "4".hashCode()));
        g2.add(new SimpleEdge("4".hashCode(), "14".hashCode()));
        g2.add(new SimpleEdge("15".hashCode(), "13".hashCode()));
        g2.add(new SimpleEdge("13".hashCode(), "14".hashCode()));
        g2.add(new SimpleEdge("14".hashCode(), "2".hashCode()));
        g2.add(new SimpleEdge("2".hashCode(), "16".hashCode()));
        g2.add(new SimpleEdge("11".hashCode(), "12".hashCode()));
        g2.add(new SimpleEdge("12".hashCode(), "15".hashCode()));
        g2.add(new SimpleEdge("15".hashCode(), "16".hashCode()));
        g2.add(new SimpleEdge("11".hashCode(), "16".hashCode()));

        IsomorphismTester isoTest = new TypedVF2IsomorphismTester();
        if (isoTest.areIsomorphic(g1, g2)) {
            System.out.println("Debug: the two graphs are isomorphic");
        } else {
            System.out.println("Debug: the two graphs are NOT isomorphic");
        }

    }

    public static void main(String[] args) {
        SSpaceGraphIsomorphicTest test = new SSpaceGraphIsomorphicTest();
    }
}
