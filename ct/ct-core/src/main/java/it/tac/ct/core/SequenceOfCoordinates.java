/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mario Stefanutti
 *         <p>
 *         It is used to construct the sequence of coordinates (Begin and End), as in the following example: 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
 *         </p>
 */
public class SequenceOfCoordinates implements Cloneable, Serializable {

	// For serialization
    //
	private static final long serialVersionUID = 4054291256638132187L;

	// The structure that represents the sequence of coordinates
    //
    public List<FCoordinate> sequence = new ArrayList<FCoordinate>();

    /**
     * @param fNumber
     *            The number of F to be inserted
     * @param beginInsertPoint
     *            The begin point of the new F
     * @param endInsertPoint
     *            The end point of the new F
     */
    public void insertF(int fNumber, int beginInsertPoint, int endInsertPoint) {

        // Add begin + end
        //
        sequence.add(beginInsertPoint, new FCoordinate(FCoordinate.TYPE.BEGIN, fNumber, true));
        sequence.add(endInsertPoint, new FCoordinate(FCoordinate.TYPE.END, fNumber, true));

        // Set to "no more visible" the coordinates between begin and end
        //
        for (int i = beginInsertPoint + 1; i < endInsertPoint; i++) {
            sequence.get(i).isVisible = false;
        }
    }

    /**
     * Which face is at the insert point?
     * 
     * @param index
     *            The index where to search (from 1 to (size of the sequence - 1))
     * @param offset
     *            0 = face at index -1 = face at index as if the last face wasn't inserted
     * @return The F number (from 1 to ...) at the given coordinate
     */
    public int fNumberAtIndex(int index, int offset) {

        // Some variables
        //
        int fNumberFound = -1;
        boolean isFFound = false;
        boolean skipCurrentCoordinate = false;

        // Back searching all the face numbers
        // 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
        //
        for (int iF = (sequence.size() / 2) + offset; (iF > 0) && (isFFound == false); iF--) {

            // Back searching, from the insert point back in the sequence
            //
            for (int iSequence = index - 1; (iSequence >= 0) && (skipCurrentCoordinate == false) && (isFFound == false); iSequence--) {

                // If found
                //
                if (sequence.get(iSequence).fNumber == iF) {

                    // If this coordinate is an End coordinate, I can skip this F
                    //
                    if (sequence.get(iSequence).type == FCoordinate.TYPE.END) {
                        skipCurrentCoordinate = true;
                    } else {
                        fNumberFound = iF;
                        isFFound = true;
                    }
                }
            }

            // Reset the skip flag
            //
            skipCurrentCoordinate = false;
        }

        // Return
        //
        return fNumberFound;
    }

    /**
     * @return The number of visible Edges (that have a direct access to the ocean)
     */
    public int numberOfVisibleEdgesAtBorders() {

        int numberOfVisibleEdgesAtBorders = 0;

        // Loop the list of coordinates, skipping the Begin and End of the first F (Face that is always visible)
        //
        for (int iCoordinate = 1; iCoordinate < (sequence.size() - 1); iCoordinate++) {
            if (sequence.get(iCoordinate).isVisible == true) {
                numberOfVisibleEdgesAtBorders++;
            }
        }

        // Return
        //
        return (numberOfVisibleEdgesAtBorders);
    }

    /**
     * @return The cloned SequenceOfCoordinates
     */
    @Override public SequenceOfCoordinates clone() throws CloneNotSupportedException {
        SequenceOfCoordinates clonedSequenceOfCoordinates = new SequenceOfCoordinates();

        Iterator<FCoordinate> iter = this.sequence.iterator();
        while (iter.hasNext()) {
            FCoordinate clonedFCoordinate = iter.next().clone();
            clonedSequenceOfCoordinates.sequence.add(clonedFCoordinate);
        }

        return clonedSequenceOfCoordinates;
    }
}
