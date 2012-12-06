/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.awt.Color;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Represent a Vertex (V) of a graph
 *          </p>
 */
public class Vertex {

    // Variables to distinguish the various cases in graph creation (B-E, B-M, M-M, M-E)
    //
    public enum TYPE_OF_VERTEX {
        NOT_DEFINED, BEGIN, MIDDLE, END
    };

    // For the shape of the Vertex
    //
    public static int DIAMETER = 10;

    // Name
    //
    public String name = "";

    // The face that created this vertex
    //
    public int faceNumber = -1;

    // The face that created this vertex
    //
    public int spiralChainNumber = -1;
    public int spiralChainSequenceNumber = -1;

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

    // Used for coloring spiral chains
    //
    public Color color = Color.black; // Default for no color
    public static final Color USED_COLOR = Color.black;
    public static final Color FIRST_USED_FILL_COLOR = Color.white;
    public static final Color USED_FILL_COLOR = Color.black;

    /**
     * Which edge is at right? I don't care about used edges
     * 
     * @param comingFrom
     *            The edge I am coming from
     * @return The edge at left
     */
    public Edge getTheEdgeAtRight(Edge comingFrom) {

        // The edge to return
        //
        Edge edgeToReturn = null;

        // From which direction am I coming?
        //
        if (comingFrom == edgeAtLeft) {
            edgeToReturn = edgeAtBottom;
        } else if (comingFrom == edgeAtRight) {
            edgeToReturn = edgeAtLeft;
        } else if (comingFrom == edgeAtBottom) {
            edgeToReturn = edgeAtRight;
        }

        // Return the edge
        //
        return edgeToReturn;
    }

    /**
     * Which edge is at left? I DO care about used edges
     * 
     * @param comingFrom
     *            The edge I am coming from
     * @return The edge at left or null if all edges have been previously used
     */
    public Edge getTheEdgeToTurnLeftOrRightIfLeftIsUsed(Edge comingFrom) {

        // The edge to return
        //
        Edge edgeToReturn = null;

        // If all edges have been used, returns null
        //
        if (edgeAtLeft.used && edgeAtRight.used && edgeAtBottom.used) {
            edgeToReturn = null;
        } else {

            // From which direction am I coming? Is the edge at left used?
            //
            if (comingFrom == edgeAtLeft) {
                edgeToReturn = edgeAtRight.used ? edgeAtBottom : edgeAtRight;
            } else if (comingFrom == edgeAtRight) {
                edgeToReturn = edgeAtBottom.used ? edgeAtLeft : edgeAtBottom;
            } else if (comingFrom == edgeAtBottom) {
                edgeToReturn = edgeAtLeft.used ? edgeAtRight : edgeAtLeft;
            }
        }

        // Return the edge
        //
        return edgeToReturn;
    }

    // Support method
    //
    public String toString() {
        return name;
    }
}
