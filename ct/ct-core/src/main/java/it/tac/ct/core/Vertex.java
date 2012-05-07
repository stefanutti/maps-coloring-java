/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Represent a Vertex (V) of a graph
 *          </p>
 */
public class Vertex {

    // For the shape of the Vertex
    //
    public static int WIDTH = 30;
    public static int HEIGHT = 30;

    // Name
    //
    public String name = "";

    // If the vertex has already been visited/used
    //
    public boolean used = false;

    // If the vertex has already been visited/used
    //
    public boolean isOnTheExcternalCycle = false;

    // edgeAtLeft, edgeAtRight and edgeAtBottom will be used to understand left and right
    // Each vertex has three vertices. If a come from one of its edges, which is the left-most edge
    //
    // if I came from edgeAtLeft --> edgeAtRight is the left-most edge
    // if I came from edgeAtRight --> edgeAtBottom is the left-most edge
    // if I came from edgeAtBottom --> edgeAtLeft is the left-most edge
    //
    public Edge edgeAtLeft = null;
    public Edge edgeAtRight = null;
    public Edge edgeAtBottom = null;

    // It may be useful to position a vertex on the video (or any cartesian system)
    //
    public int xCoordinate = -1;
    public int yCoordinate = -1;

    // Used for coloring maps
    //
    public COLORS color = COLORS.UNCOLORED; // Default for no color

    public String toString() {
        return name;
    }
}
