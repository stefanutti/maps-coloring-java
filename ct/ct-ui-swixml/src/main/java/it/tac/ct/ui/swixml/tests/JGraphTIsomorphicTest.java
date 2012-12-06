package it.tac.ct.ui.swixml.tests;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public final class JGraphTIsomorphicTest {
    private JGraphTIsomorphicTest() {

        // Bug: In this case with number of egdes different, it goes in exception instead of returning false
        //
        // UndirectedGraph<String, DefaultEdge> graph01 = createGraph01();
        // UndirectedGraph<String, DefaultEdge> graph02 = createGraph02();
        //
        // GraphIsomorphismInspector iso1_2 = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(graph01, graph02, null, null);
        // boolean isoResult1_2 = iso1_2.isIsomorphic();
        // if (isoResult1_2) {
        // System.out.println("Graphs are isomorphic.");
        // } else {
        // System.out.println("Graphs are NOT isomorphic.");
        // }

        // Bug: Infinite loop?
        //
        UndirectedGraph<String, DefaultEdge> graph04 = createGraph04();
        UndirectedGraph<String, DefaultEdge> graph05 = createGraph05();

        GraphIsomorphismInspector iso3_4 = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(graph04, graph05, null, null);
        boolean isoResult3_4 = iso3_4.isIsomorphic();
        if (isoResult3_4) {
            System.out.println("Graphs are isomorphic.");
        } else {
            System.out.println("Graphs are NOT isomorphic.");
        }
    }

    public static void main(String[] args) {
        JGraphTIsomorphicTest test = new JGraphTIsomorphicTest();
    }

    private UndirectedGraph<String, DefaultEdge> createGraph01() {
        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);

        g.addEdge(v1, v2);
        g.addEdge(v3, v1);
        g.addEdge(v1, v4);
        g.addEdge(v5, v3);
        g.addEdge(v3, v6);
        g.addEdge(v5, v6);
        g.addEdge(v6, v4);
        g.addEdge(v4, v2);
        g.addEdge(v5, v2);

        return g;
    }

    private UndirectedGraph<String, DefaultEdge> createGraph02() {
        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);

        g.addEdge(v1, v2);
        g.addEdge(v3, v4);
        g.addEdge(v5, v6);
        g.addEdge(v3, v5);
        g.addEdge(v6, v4);
        g.addEdge(v4, v1);
        g.addEdge(v3, v2);

        return g;
    }

    private UndirectedGraph<String, DefaultEdge> createGraph02a() {
        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);

        g.addEdge(v1, v2);
        g.addEdge(v3, v4);
        g.addEdge(v5, v6);
        g.addEdge(v3, v5);
        g.addEdge(v6, v4);
        g.addEdge(v4, v1);
        g.addEdge(v3, v2);

        return g;
    }

    private UndirectedGraph<String, DefaultEdge> createGraph04() {
        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";
        String v7 = "7";
        String v8 = "8";
        String v9 = "9";
        String v10 = "10";
        String v11 = "11";
        String v12 = "12";
        String v13 = "13";
        String v14 = "14";
        String v15 = "15";
        String v16 = "16";

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addVertex(v8);
        g.addVertex(v9);
        g.addVertex(v10);
        g.addVertex(v11);
        g.addVertex(v12);
        g.addVertex(v13);
        g.addVertex(v14);
        g.addVertex(v15);
        g.addVertex(v16);

        g.addEdge(v1, v2);
        g.addEdge(v3, v2);
        g.addEdge(v2, v4);
        g.addEdge(v5, v4);
        g.addEdge(v4, v6);
        g.addEdge(v7, v5);
        g.addEdge(v5, v8);
        g.addEdge(v9, v6);
        g.addEdge(v6, v10);
        g.addEdge(v11, v8);
        g.addEdge(v8, v9);
        g.addEdge(v9, v12);
        g.addEdge(v13, v3);
        g.addEdge(v3, v7);
        g.addEdge(v7, v11);
        g.addEdge(v11, v14);
        g.addEdge(v15, v13);
        g.addEdge(v13, v14);
        g.addEdge(v14, v12);
        g.addEdge(v12, v16);
        g.addEdge(v1, v15);
        g.addEdge(v15, v16);
        g.addEdge(v16, v10);
        g.addEdge(v1, v10);

        return g;
    }

    private UndirectedGraph<String, DefaultEdge> createGraph05() {
        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";
        String v7 = "7";
        String v8 = "8";
        String v9 = "9";
        String v10 = "10";
        String v11 = "11";
        String v12 = "12";
        String v13 = "13";
        String v14 = "14";
        String v15 = "15";
        String v16 = "16";

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addVertex(v8);
        g.addVertex(v9);
        g.addVertex(v10);
        g.addVertex(v11);
        g.addVertex(v12);
        g.addVertex(v13);
        g.addVertex(v14);
        g.addVertex(v15);
        g.addVertex(v16);

        g.addEdge(v1, v2);
        g.addEdge(v3, v4);
        g.addEdge(v5, v6);
        g.addEdge(v7, v8);
        g.addEdge(v9, v1);
        g.addEdge(v1, v3);
        g.addEdge(v3, v5);
        g.addEdge(v5, v10);
        g.addEdge(v11, v9);
        g.addEdge(v9, v10);
        g.addEdge(v10, v7);
        g.addEdge(v7, v12);
        g.addEdge(v13, v8);
        g.addEdge(v8, v6);
        g.addEdge(v6, v4);
        g.addEdge(v4, v14);
        g.addEdge(v15, v13);
        g.addEdge(v13, v14);
        g.addEdge(v14, v2);
        g.addEdge(v2, v16);
        g.addEdge(v11, v12);
        g.addEdge(v12, v15);
        g.addEdge(v15, v16);
        g.addEdge(v11, v16);

        return g;
    }
}
