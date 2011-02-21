/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author Mario Stefanutti
 *         <p>
 *         The map generator
 *         </p>
 */
public class MapsGenerator {

    public enum MAX_METHOD {
        MAPS, F, FIXED_MAPS_LEN
    };

    // Control parameters
    //
    public int slowdownMillisec = 0; // May be needed to slow down the process (what for, I don't know)
    public boolean logWhilePopulate = true;
    public boolean randomElaboration = false;
    public boolean processAll = true;
    public Enum<MAX_METHOD> maxMethod = MAX_METHOD.MAPS;
    public int maxNumber = 0;

    // Maps removed during elaboration. These would have been removed anyway running
    // removeMapsWithCardinalityLessThanFour after elaboration
    //
    public int removed = 0;

    // If this method will be used from a thread, it can be stopped setting this flag
    //
    public boolean stopRequested = false;

    // Main lists
    //
    public ArrayList<Map4CT> maps = new ArrayList<Map4CT>();
    public ArrayList<Map4CT> todoList = new ArrayList<Map4CT>();

    /**
     * Defaut constructor. A map is initialized with two F and the ocean surrounding the two Fs
     */
    public MapsGenerator() {

        // Prepare the basic map, out of which all the other will be prepared
        //
        // For simplicity (not to handle limit cases), the basic map is made of 3 faces considering the ocean (the third Face)
        // - key = 1.1, cardinality = 2 for all faces. Sequence (at border) = "1b+, 2b+, 2e+, 1e+"
        //
        Map4CT map = new Map4CT();

        // Add this map to the things to do.
        // This map is not added to the calculated maps because I'm looking for maps without F2 and F3 (and also F4)
        //
        maps.add(map);
        todoList.add(map);
    }

    /**
     * Generate the maps
     */
    public void generate() throws Exception {

        // If you just started generate, it means you don't want to stop it
        // This flag is used when you execute this method in a multi thread
        //
        stopRequested = false;

        // Begin the main loop
        //
        while ((todoList.size() != 0) && (stopRequested == false)) {

            // Acquire Job
            //
            Map4CT currentMap = getTodo();

            // The F (id) being added
            //
            int fNumberToAdd = currentMap.faces.size() + 1;

            // Number of Edges visible at this level (level = border = shore = facing the ocean at this level)
            //
            int numberOfVisibleEdgesAtBorders = currentMap.sequenceOfCoordinates.numberOfVisibleEdgesAtBorders();

            // Loops on the possible insertion points (related to the disposition of edges at border)
            //
            RandomLoopManager insertPointLoopManager = new RandomLoopManager(1, numberOfVisibleEdgesAtBorders + 1, processAll);
            int insertPoint = -1;
            while ((insertPoint = insertPointLoopManager.getValue(randomElaboration)) != -1) {

                // Loops on the possible end points (related to the disposition of edges at border)
                // for (int numberOfEToTouch = 1; numberOfEToTouch <= (numberOfVisibleEdgesAtBorders - insertPoint + 2);
                // numberOfEToTouch++) {
                //
                RandomLoopManager numberOfEToTouchLoopManager = new RandomLoopManager(1, numberOfVisibleEdgesAtBorders - insertPoint + 2, processAll);
                int numberOfEToTouch = -1;
                while ((numberOfEToTouch = numberOfEToTouchLoopManager.getValue(randomElaboration)) != -1) {

                    // Clone the old map, to build the new one
                    //
                    Map4CT newMap = currentMap.clone();

                    // Add the new F to the new map
                    //
                    newMap.insertF(fNumberToAdd, insertPoint, numberOfEToTouch);

                    // Check if an unreachable Fs have cardinality less than 5. I can get rid of this maps and save memory
                    // +
                    // The missing faces are capable to generate all F5 and greater? Each face can touch at maximum two existing faces and modify their cardinality:
                    //
                    // number of F2 * 3 (missing edges to reach five) > number of faces missing to the end * 2 (+1 +1 at maximum for touched faces)
                    // number of F3 * 2 > number of faces missing to the end * 2
                    // number of F4 * 1 > number of faces missing to the end * 2
                    //
                    int removeCase = 0;
                    int facesToTheEnd = maxNumber - (fNumberToAdd + 1);

                    if (newMap.hasUnreachableFWithCardinalityLessThanFive() == true) {
                        removeCase = 1;
                    } else if (maxMethod == MAX_METHOD.F) {
                        if (((newMap.numberOfFWithGivenCardinality(2) * 3) + (newMap.numberOfFWithGivenCardinality(3) * 2) + (newMap.numberOfFWithGivenCardinality(4) * 1)) > (facesToTheEnd * 2)) {
                            removeCase = 2;
                        }
                    }
                    // If needs to be removed
                    //
                    if (removeCase > 0) {

                        // Increase the number of removed maps (optimization for memory and speed)
                        //
                        removed++;

                        // Log
                        //
                        if (logWhilePopulate == true) {
                            System.out.print("REMOVED-" + removeCase + ": " + removed + ": ");
                            Map4CT.printDetailedMap(newMap);
                        }
                    } else {

                        // I have to update the todoList
                        // Have I finished? It depends on the generation method used
                        //
                        if (maxMethod == MAX_METHOD.MAPS) {

                            // Add the new map into the list of all generated maps
                            //
                            maps.add(newMap);

                            // If I've not generated the number of maps I wanted
                            //
                            if (maps.size() < maxNumber) {

                                // There is work to do
                                //
                                todoList.add(newMap);
                            } else {

                                // Ok. I've done my job. I can get rid of the todoList
                                //
                                todoList.clear();
                            }
                        } else if (maxMethod == MAX_METHOD.F) {

                            // If the brand new map can be also used to generate other maps (with more Faces), I still need to work
                            //
                            if (fNumberToAdd < (maxNumber - 1)) {

                                // Add the new map into the list of all generated maps
                                //
                                maps.add(newMap);
                                todoList.add(newMap);
                            } else {

                                // Add the new map into the list of all generated maps (only if survives the filtering)
                                // fNumberToAdd < (12 - 1) means that the map has less than 12 faces (considering the ocean)
                                //
                                if (fNumberToAdd < (12 - 1)) {
                                    maps.add(newMap);
                                } else if (newMap.hasFWithCardinalityLessThanFiveConsideringTheOcean() == false) {
                                    maps.add(newMap);
                                }
                            }
                        } else if (maxMethod == MAX_METHOD.FIXED_MAPS_LEN) {

                            // Add the new map into the list of all generated maps
                            // If this generation method is used, I need to work night and day
                            //
                            maps.add(newMap);
                            todoList.add(newMap);

                            // Trim the maps list (removing random items)
                            //
                            int mapsToRemove = maps.size() - maxNumber;
                            for (int iRemove = 0; iRemove < mapsToRemove; iRemove++) {
                                maps.remove(new Random().nextInt(maps.size()));
                            }

                            // Keep also the todo list small
                            //
                            int todosToRemove = todoList.size() - maxNumber;
                            for (int iRemove = 0; iRemove < todosToRemove; iRemove++) {
                                todoList.remove(new Random().nextInt(todoList.size()));
                            }
                        }

                        // Log
                        //
                        if (logWhilePopulate == true) {
                            System.out.print("Debug: Map4CT = size = " + maps.size() + ", todo: " + todoList.size() + ": ");
                            Map4CT.printDetailedMap(newMap);
                        }

                        // Sleep?
                        //
                        if (slowdownMillisec != 0) {
                            Thread.sleep(slowdownMillisec);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove maps that have Fs with cardinality less than 4
     */
    public void removeMapsWithCardinalityLessThanFour() {

        // Local variables
        //
        boolean isRemoved = false;
        int numberOfMaps = -1;
        int numberOfF = -1;

        // Loops on all maps (from the end)
        //
        numberOfMaps = maps.size();
        for (int iMaps = numberOfMaps - 1; iMaps >= 0; iMaps--) {

            // Check Fs with cardinality less than 5
            //
            numberOfF = maps.get(iMaps).faces.size();
            isRemoved = false;

            for (int i = 0; (i < numberOfF) && (isRemoved == false); i++) {
                if (maps.get(iMaps).faces.get(i).cardinality < 4) {
                    maps.remove(iMaps);
                    isRemoved = true;
                } else if (maps.get(iMaps).sequenceOfCoordinates.numberOfVisibleEdgesAtBorders() < 4) {
                    maps.remove(iMaps);
                    isRemoved = true;
                }
            }
        }
    }

    /**
     * Remove maps that have Fs with cardinality less than 5
     */
    public void removeMapsWithCardinalityLessThanFive() {

        // Local variables
        //
        boolean isRemoved = false;
        int numberOfMaps = -1;
        int numberOfF = -1;

        // Loops on all maps (from the end)
        //
        numberOfMaps = maps.size();
        for (int iMaps = numberOfMaps - 1; iMaps >= 0; iMaps--) {

            // Check Fs with cardinality less than 5
            //
            numberOfF = maps.get(iMaps).faces.size();
            isRemoved = false;

            for (int i = 0; (i < numberOfF) && (isRemoved == false); i++) {
                if (maps.get(iMaps).faces.get(i).cardinality < 5) {
                    maps.remove(iMaps);
                    isRemoved = true;
                } else if (maps.get(iMaps).sequenceOfCoordinates.numberOfVisibleEdgesAtBorders() < 5) {
                    maps.remove(iMaps);
                    isRemoved = true;
                }
            }
        }
    }

    /**
     * Remove maps that have less than F faces (considering also the ocean)
     */
    public void removeMapsWithLessThanFFaces(int numberOfF) {

        // Local variables
        //
        int numberOfMaps = maps.size();

        // Loops on all maps (from the end)
        //
        for (int iMaps = numberOfMaps - 1; iMaps >= 0; iMaps--) {
            if ((maps.get(iMaps).faces.size() + 1) < numberOfF) {
                maps.remove(iMaps);
            }
        }
    }

    /**
     * Print info for all the generated maps
     */
    public void printAllMaps() {

        // Loops all the maps
        //
        for (int iMaps = 0; iMaps < maps.size(); iMaps++) {
            Map4CT.printDetailedMap(maps.get(iMaps));
        }
    }

    /**
     * @return The Map4CT to process
     */
    public Map4CT getTodo() {

        // Local variables
        //
        Map4CT mapToProcess = null;

        // Get from the totoList a map
        //
        if (randomElaboration) {
            mapToProcess = todoList.remove(new Random().nextInt(todoList.size()));
        } else {
            mapToProcess = todoList.remove(0);
        }

        for (int i = 0; i < mapToProcess.faces.size(); i++) {
            mapToProcess.faces.get(i).color = COLORS.UNCOLORED;
        }

        // Return the map to process
        //
        return mapToProcess;
    }

    /**
     * Create a new map from the text representation
     * XXX to be finished
     * 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
     */
    public void createMapFromTextRepresentation(String mapTextRepresentation) {

    	// Create an empty (almost) map (default is 2 faces + the ocean = 3 faces)
        //
        Map4CT newMap = new Map4CT();

        // Analyze the input string to understand how many faces I need to create
        //
        String[] tokensOfTheMapTextRepresentation = mapTextRepresentation.split(", ");
        int numberOfFaces = tokensOfTheMapTextRepresentation.length / 2;

        // Loops all faces
        //
        for (int faceNumber = 3; faceNumber <= numberOfFaces; faceNumber++) {
        	
        	// Compute insertPoint and numberOfEToTouch
        	//
        	int tokenNumber = 0;
        	int beginInsertPoint = 0;
        	int numberOfEToTouch = 1;
        	boolean beginFound = false;
        	boolean endFound = false;

        	// Loop all tokens to search insertPoint, filtering out faces not yet to insert
        	//
        	for (tokenNumber = 0; (tokenNumber < tokensOfTheMapTextRepresentation.length) && (beginFound == false); tokenNumber++) {

        		String preParsing = tokensOfTheMapTextRepresentation[tokenNumber].substring(0,tokensOfTheMapTextRepresentation[tokenNumber].length() - 2);
            	int faceAnalyzed = Integer.parseInt(preParsing,10);
        		if (faceAnalyzed < faceNumber) {
        			for (int iSequence = 0; iSequence < newMap.sequenceOfCoordinates.sequence.size(); iSequence++)
        			{
        				// if (newMap.sequenceOfCoordinates.sequence.get(iSequence).isVisible
        			}
        			beginInsertPoint++;
        		}
        		if (tokensOfTheMapTextRepresentation[tokenNumber].startsWith("" + faceNumber + "b")) {
        			beginFound = true;
        		}
        	}

        	// Continue the loop with a different check, to search numberOfEToTouch, filtering out faces not yet to insert
        	//
        	for (; (tokenNumber < tokensOfTheMapTextRepresentation.length) && (endFound == false); tokenNumber++) {

            	String preParsing = tokensOfTheMapTextRepresentation[tokenNumber].substring(0,tokensOfTheMapTextRepresentation[tokenNumber].length() - 2);
            	int faceAnalyzed = Integer.parseInt(preParsing,10);
        		if (faceAnalyzed < faceNumber) {
        			if ((!tokensOfTheMapTextRepresentation[tokenNumber].startsWith("" + faceAnalyzed + "b-")) && (!tokensOfTheMapTextRepresentation[tokenNumber].startsWith("" + faceAnalyzed + "e-"))) {
            			numberOfEToTouch++;
        			}
        			else if ((faceAnalyzed == (faceNumber -1)) && (tokensOfTheMapTextRepresentation[tokenNumber].startsWith("" + faceAnalyzed + "b-") || tokensOfTheMapTextRepresentation[tokenNumber].startsWith("" + faceAnalyzed + "e-"))) {
            			numberOfEToTouch++;
        			}
        		}
        		if (tokensOfTheMapTextRepresentation[tokenNumber].startsWith("" + faceNumber + "e")) {
        			endFound = true;
        		}
        	}

    		System.out.println("DEBUG: faceNumber = " + faceNumber + " beginInsertPoint = " + beginInsertPoint + " numberOfEToTouch = " + numberOfEToTouch);

        	// Insert the face in the new map
        	//
            newMap.insertF(faceNumber, beginInsertPoint, numberOfEToTouch);
        }

        // Update the map list and the todoList
        //
        maps.add(newMap);
        todoList.add(newMap);
    }

    /**
     * Copy the generated maps to the todoList
     */
    public void copyMapsToTodo() {

        // Loops all the maps
        //
        for (int iMaps = 0; iMaps < maps.size(); iMaps++) {
            todoList.add(maps.get(iMaps));
        }
    }
}
