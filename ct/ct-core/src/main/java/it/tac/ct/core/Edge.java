/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.awt.Color;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Represent an Edge (E) of a graph
 *          </p>
 */
public class Edge {

    // The shape when drawing the rectangular map
    //
    public enum SHAPE_TYPE {
        STRAIGHT, L, MIRRORED_L, U, OCEAN
    }

    public SHAPE_TYPE shapeType = SHAPE_TYPE.STRAIGHT;

    // The vertices of the edge
    //
    public Vertex firstVertex = null;
    public Vertex secondVertex = null;

    // Name
    //
    public String name = "";

    // If the edge has already been visited/used
    //
    public boolean used = false;

    // Used for coloring graphs
    //
    public Color color = Color.lightGray; // Default for no color
    public static final Color USED_COLOR = Color.black;

    /**
     * Return the other vertex of an edge (or null if null is given)
     * 
     * @param The
     *            vertex of an edge we don't want
     * @return The other vertex
     */
    public Vertex getTheOtherVertex(Vertex vertex) {
        Vertex vertexToReturn = null;

        if (firstVertex == vertex) {
            vertexToReturn = secondVertex;
        } else if (secondVertex == vertex) {
            vertexToReturn = firstVertex;
        }

        return vertexToReturn;
    }

    // Support method
    //
    public String toString() {
        return name;
    }
}
