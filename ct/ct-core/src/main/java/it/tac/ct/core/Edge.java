/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

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

    public Enum<SHAPE_TYPE> shapeType = SHAPE_TYPE.STRAIGHT;

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

    // Used for coloring maps
    //
    public COLORS color = COLORS.UNCOLORED; // Default for no color

    // Support method
    //
    public String toString() {
        return name;
    }
}
