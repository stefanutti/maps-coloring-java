/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          It is used to make it easier to compute the graphical coordinates of the map
 *          </p>
 */
public class FCoordinate implements Cloneable {

    // Possible TYPEs of coordinates
    //
    public static enum TYPE {
        BEGIN, END
    };

    /**
     * BEGIN or END?
     */
    public Enum<TYPE> type = TYPE.BEGIN;

    /**
     * The id of the F (from 1 to ...)
     */
    public int fNumber = -1;

    /**
     * Is the F at this coordinate still visible? Can the border (BEGIN or END) access to the ocean?
     */
    public boolean isVisible = true;

    /**
     * Default constructor
     */
    public FCoordinate() {
    }

    /**
     * @param type
     *            BEGIN or END?
     * @param fNumber
     *            The id of the F (from 1 to ...)
     * @param isVisible
     *            Is the F at this coordinate still visible? Can the border (BEGIN or END) access to the ocean?
     */
    public FCoordinate(Enum<TYPE> type, int fNumber, boolean isVisible) {
        this.type = type;
        this.fNumber = fNumber;
        this.isVisible = isVisible;
    }

    /**
     * @return The string representation of a coordinate (for debugging and output)
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(fNumber);
        if (type == TYPE.BEGIN)
            sb.append("b");
        else if (type == TYPE.END)
            sb.append("e");
        else
            sb.append("m");
        if (isVisible)
            sb.append("+");
        else
            sb.append("-");

        return sb.toString();
    }

    /**
     * @return The cloned FCoordinate
     */
    @Override
    public FCoordinate clone() throws CloneNotSupportedException {
        return (FCoordinate)super.clone();
    }
}
