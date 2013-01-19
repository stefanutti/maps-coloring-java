/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Represent a Face (F) of a regular map
 *          </p>
 */
public class F implements Cloneable, Serializable {

    // Number of Edges of the F (type of F in the Euler formula): F = F2 + F3 + F4 + F5 + ...
    //
    public short cardinality = 0;

    // Id of the F (from 1 to ...). -1 = NOT INITIALIZED
    //
    public short id = -1;

    // Used for coloring maps
    //
    public COLORS color = COLORS.UNCOLORED; // Default for no color selected

    // List of neighbors of the previous levels
    //
    public List<Integer> neighbors = new ArrayList<Integer>();

    /**
     * @param id
     *            The id of the F (from 1 to ...)
     * @param cardinality
     *            Number of Edges of the F (type of F in the Euler formula): F = F2 + F3 + F4 + F5 + ...
     */
    public F(short id, short cardinality) {
        super();
        this.id = id;
        this.cardinality = cardinality;
    }

    /**
     * @return The cloned F
     */
    @Override public F clone() throws CloneNotSupportedException {
        return (F) super.clone();
    }
}
