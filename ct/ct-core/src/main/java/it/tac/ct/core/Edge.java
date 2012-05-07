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

    /**
     * Serializable
     */
    private static final long serialVersionUID = 1L;

    // Name
    //
    public String name = "";

    // If the edge has already been visited/used
    //
    public boolean used = false;

    // Used for coloring maps
    //
    public COLORS color = COLORS.UNCOLORED; // Default for no color
}
