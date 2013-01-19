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
 * @version September 2007
 *          <p>
 *          The regular map
 *          </p>
 */
public class Map4CT implements Cloneable, Serializable {

    // The Faces of the map (ordered as inserted)
    //
    public List<F> faces = new ArrayList<F>();

    // The sequence of all coordinates (for each F): 1b+,3b+,2b-,2e-,3e+,1e+ (as seen from the shore)
    //
    // b = begin e = end - = not visible
    //
    public SequenceOfCoordinates sequenceOfCoordinates = new SequenceOfCoordinates();

    // Mark this map to be removed (used when removing lots of maps to avoid shifting of list elements every time an item is removed)
    //
    public boolean removeLater = false;

    /**
     * Standard constructor
     */
    public Map4CT() {

        // The initial Map = 2 F + the ocean, ready to add other F
        //
        faces.add(new F((short) 1, (short) 2));
        faces.add(new F((short) 2, (short) 2));
        sequenceOfCoordinates.insertF((short) 1, 0, 1);
        sequenceOfCoordinates.insertF((short) 2, 1, 2);

        // The second face as a previous neighbor that is the first face of index equal to 0
        //
        faces.get(1).neighbors.add(new Integer(0));
    }

    /**
     * @param fNumberToAdd
     *            The number of F to be inserted (from 1 to ...)
     * @param beginInsertPoint
     *            The insert point of the new F (related to visible edges only) starting from 1
     * @param numberOfEToTouch
     *            Number of edges to touch
     */
    public void insertF(int fNumberToAdd, int beginInsertPoint, int numberOfEToTouch) {

        // To calculate the correct endInsertPoint, I need to skip the Fs that are no more visible
        //
        int numberOfETouched = 0;
        int endInsertPoint = -1;

        // At this point the face has not been added yet, so the neighbors of the face will be set at the end of the method
        //
        List<Integer> tmpNeighbors = new ArrayList<Integer>();

        // Considering that some edges may be invisible at this level, I have to compute the real index respect to the sequence of coordinates
        //
        int realBeginInsertPoint = 1;
        int touchedFaces = 1;
        while (touchedFaces < beginInsertPoint) {
            if (sequenceOfCoordinates.sequence.get(realBeginInsertPoint).isVisible == true) {
                touchedFaces++;
            }
            realBeginInsertPoint++;
        }

        // Initial position of the endInsertPoint
        //
        endInsertPoint = realBeginInsertPoint;

        // Only one edge to touch
        //
        if (numberOfEToTouch == 1) {

            // Add the neighbor
            //
            tmpNeighbors.add(new Integer(sequenceOfCoordinates.fNumberAtIndex(endInsertPoint, 0) - 1));
            endInsertPoint++;
        } else {
            while (numberOfETouched < numberOfEToTouch) {
                if (sequenceOfCoordinates.sequence.get(endInsertPoint).isVisible == true) {
                    numberOfETouched++;
                }

                // Add the neighbor and move to the next insertion point
                //
                tmpNeighbors.add(new Integer(sequenceOfCoordinates.fNumberAtIndex(endInsertPoint, 0) - 1));
                endInsertPoint++;
            }
        }

        // Add the new F to the Map
        // Cardinality is directly updated as if the ocean is already in place (+1 respect to the Fs touched)
        //
        sequenceOfCoordinates.insertF(fNumberToAdd, realBeginInsertPoint, endInsertPoint);
        faces.add(new F((short) fNumberToAdd, (short) (numberOfEToTouch + 1)));

        // I need to update the cardinality of the Fs that have been touched at the borders by the new F
        //
        faces.get(sequenceOfCoordinates.fNumberAtIndex(realBeginInsertPoint, -1) - 1).cardinality++;
        faces.get(sequenceOfCoordinates.fNumberAtIndex(endInsertPoint, -1) - 1).cardinality++;

        // Set the neighbors
        //
        faces.get(fNumberToAdd - 1).neighbors = tmpNeighbors;
    }

    /**
     * @param map
     *            The map to print
     */
    public static void printDetailedMap(Map4CT map) {

        // 4F2 + 3F3 + 2F4 + 1F5 + 0F6 - 1F7 - 2F8 - 3F9 - ... = 12
        //
        int euleroResult = 0;

        // Print the header
        //
        System.out.print((map.faces.size() + 1) + "F *** ");

        // Print the cardinality of all Fs (in order. The ocean is a special F and )
        //
        for (int i = 0; i < map.faces.size(); i++) {
            System.out.print(map.faces.get(i).cardinality + " ");
            euleroResult = euleroResult + (6 - map.faces.get(i).cardinality);
        }

        // The last one is the ocean
        //
        System.out.print("(" + map.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders() + ")");

        // Test if the map has been programmatically built in the correct way (4F2 + 3F3 + 2F4 + 1F5 + 0F6 - 1F7 - 2F8 - ... = 12)
        //
        euleroResult = euleroResult + (6 - map.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders());

        // Eulero
        //
        System.out.print(" *** E" + euleroResult);

        // Print the sequence of the coordinates of each F
        //
        System.out.print(" *** " + map.sequenceOfCoordinates.sequence.toString());

        // Close the print line
        //
        System.out.println();
    }

    /**
     * @return Returns if the map contains an unreachable ... read the method name please!
     */
    public boolean hasUnreachableFWithCardinalityLessThanFive() {

        // Some variables
        //
        boolean hasUnreachableFWithCardinalityLessThanFive = false;
        List<Integer> reachableTempList = new ArrayList<Integer>();

        // Check and memorize all the Fs that can directly reach the ocean
        //
        for (int iCoordinate = 1; iCoordinate < sequenceOfCoordinates.sequence.size(); iCoordinate++) {
            reachableTempList.add(new Integer(sequenceOfCoordinates.fNumberAtIndex(iCoordinate, 0)));
        }

        // For all faces NOT in the list of faces facing the ocean, check the cardinality
        //
        for (int iFaceNumber = 0; (iFaceNumber < faces.size()) && (hasUnreachableFWithCardinalityLessThanFive == false); iFaceNumber++) {
            if (reachableTempList.contains(new Integer(iFaceNumber + 1)) == false) {
                if (faces.get(iFaceNumber).cardinality < 5) {
                    hasUnreachableFWithCardinalityLessThanFive = true;
                }
            }
        }

        // Return
        //
        return hasUnreachableFWithCardinalityLessThanFive;
    }

    /**
     * @return Returns if the map contains a ... read the method name please!
     */
    public boolean hasFWithCardinalityLessThanFiveConsideringTheOcean() {

        // Some variables
        //
        boolean hasFWithCardinalityLessThanFive = false;

        // Check and memorize all the Fs that can directly reach the ocean
        //
        for (int iFaces = 0; (iFaces < faces.size()) && (hasFWithCardinalityLessThanFive == false); iFaces++) {
            if (faces.get(iFaces).cardinality < 5) {
                hasFWithCardinalityLessThanFive = true;
            }
        }

        // Consider also (if necessary) the Ocean cardinality
        //
        if (hasFWithCardinalityLessThanFive == false) {
            if (sequenceOfCoordinates.numberOfVisibleEdgesAtBorders() < 5) {
                hasFWithCardinalityLessThanFive = true;
            }
        }

        // Return
        //
        return hasFWithCardinalityLessThanFive;
    }

    /**
     * @param faceToAnalyze
     *            The face to analyze
     * @return true if the face is correctly colored respect to previous faces
     */
    public boolean isFaceCorrectlyColoredRespectToPreviousNeighbors(F faceToAnalyze) {

        boolean isCorrectlyColored = true;

        for (int i = 0; ((i < faceToAnalyze.neighbors.size()) && (isCorrectlyColored == true)); i++) {
            if (faceToAnalyze.color == faces.get(faceToAnalyze.neighbors.get(i)).color) {
                isCorrectlyColored = false;
            }
        }

        return isCorrectlyColored;
    }

    /**
     * @param faceNumber
     * @return true if the face has an access to the ocean
     */
    public boolean isFaceFacingTheOcean(int faceNumber) {

        boolean isFaceFacingTheOcean = false;

        // Loop the list of coordinates, skipping the Begin and End of the first F (Face that is always visible)
        //
        for (int iCoordinate = 0; (iCoordinate < sequenceOfCoordinates.sequence.size()) && (isFaceFacingTheOcean == false); iCoordinate++) {
            if (sequenceOfCoordinates.fNumberAtIndex(iCoordinate, 0) == faceNumber) {
                isFaceFacingTheOcean = true;
            }
        }

        return isFaceFacingTheOcean;
    }

    public void resetColors() {

        for (int i = 0; i < faces.size(); i++) {
            faces.get(i).color = COLORS.UNCOLORED;
        }
    }

    /**
     * @param cardinality
     * @return Returns the number of F with a given cardinality
     */
    public int numberOfFWithGivenCardinality(int cardinality) {

        // Some variables
        //
        int numberOfFWithGivenCardinality = 0;

        // Count the Fs with a given cardinality
        //
        for (int iF = 0; iF < faces.size(); iF++) {
            if (faces.get(iF).cardinality == cardinality) {
                numberOfFWithGivenCardinality++;
            }
        }

        // Return
        //
        return numberOfFWithGivenCardinality;
    }

    /**
     * @param cardinality
     * @return Returns the number of F with a given cardinality equal or less than
     */
    public int numberOfFWithGivenCardinalityEqualOrLessThan(int cardinality) {

        // Some variables
        //
        int numberOfFWithGivenCardinality = 0;

        // Count the Fs with a given cardinality
        //
        for (int iF = 0; iF < faces.size(); iF++) {
            if (faces.get(iF).cardinality <= cardinality) {
                numberOfFWithGivenCardinality++;
            }
        }

        // Return
        //
        return numberOfFWithGivenCardinality;
    }

    /**
     * @return The deep cloned map
     */
    @Override public Map4CT clone() throws CloneNotSupportedException {

        // The cloned map to return
        //
        Map4CT clonedMap = new Map4CT();

        clonedMap.sequenceOfCoordinates = this.sequenceOfCoordinates.clone();

        clonedMap.faces = new ArrayList<F>();
        Iterator<F> iter = this.faces.iterator();
        while (iter.hasNext()) {
            clonedMap.faces.add(iter.next().clone());
        }

        return clonedMap;
    }

    @Override public String toString() {
        return sequenceOfCoordinates.sequence.toString().substring(1, sequenceOfCoordinates.sequence.toString().length() - 1);
    }
}