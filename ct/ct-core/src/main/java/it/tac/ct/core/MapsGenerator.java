/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.util.ArrayList;
import java.util.Random;

import edu.ucla.sspace.graph.Graph;
import edu.ucla.sspace.graph.isomorphism.IsomorphismTester;
import edu.ucla.sspace.graph.isomorphism.TypedVF2IsomorphismTester;

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
    public int slowdownMillisec = 0; // May be needed to slow down the process (what for? I don't know)
    public boolean logWhilePopulate = false;
    public boolean randomElaboration = false;
    public boolean processAll = true;
    public MAX_METHOD maxMethod = MAX_METHOD.MAPS;
    public int maxNumber = 0;

    // Maps removed during elaboration. These would have been removed anyway running removeMapsWithCardinalityLessThanFour after elaboration
    //
    public int numberOfRemovedMaps = 0;

    // Used to see the progress in finding duplicates
    //
    public int isoOuterLoop = 0;
    public int isoInnerLoop = 0;
    public int isoRemoved = 0;

    // If this method will be used from a thread, it can be stopped setting this flag
    //
    public boolean stopRequested = false;
    public boolean stopRemovingIsoRequested = false;

    // Main lists
    //
    public ArrayList<Map4CT> maps = new ArrayList<Map4CT>();
    public ArrayList<Map4CT> todoList = new ArrayList<Map4CT>();

    /**
     * Defaut constructor. A map is initialized with two F and the ocean surrounding the two Fs
     */
    public MapsGenerator() {

        // Prepare the basic map, out of which all the other maps will be prepared
        //
        // For simplicity (not to handle limit cases), the basic map is made of 3 faces considering the ocean (the third Face)
        // - key = 1.1, cardinality = 2 for all faces. Sequence (at shore) = "1b+, 2b+, 2e+, 1e+"
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

            // Acquire Job. It removes the map from the todoList
            //
            Map4CT currentMap = getTodo();

            // Reset colors: I don't remember why I did this. It was inside getTodo()??? Maybe because the next loop it will clone a cleaned map!
            //
            for (int i = 0; i < currentMap.faces.size(); i++) {
                currentMap.faces.get(i).color = COLORS.UNCOLORED;
            }

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
                //
                // Note: I thought here there was a bug, but this time was a good bug
                // - With this code, every map will end with: "... 2e+ 1e+"
                // - End insertion point of every new F should be after the last edge: after 2e+
                // - But since given a graph I can always choose the first 3 neighbours to represent the ocean and the first two faces,
                // - I can stop before the last edge
                // - It would be better to have the face number two at the beginning of the string "1b+ 2b+ ...", but I'm satisfied with this configuration
                // - I'm happy with this unexpected behaviour (bug?) ... and I don't want to kill this lucky one!
                //
                // I can also always force the colors of the first two faces and of the ocean!
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
                    // The missing faces are sufficient to generate all F5 and greater? Each face can touch at maximum two existing edges and modify their cardinality:
                    //
                    // number of F2 * 3 (missing edges to reach five) > number of faces missing to the end * 2 (+1 +1 at maximum for, touched faces)
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
                        numberOfRemovedMaps++;

                        // Log
                        //
                        if (logWhilePopulate == true) {
                            System.out.print("REMOVED-" + removeCase + ": " + numberOfRemovedMaps + ": ");
                            Map4CT.printDetailedMap(newMap);
                        }
                    } else {

                        // Check if the new map is isomorphic to an already map in the todoList (that is also a created map)
                        // TODO: Do I have to check both todoList and maps? Answer: I think I should, but to go faster I can postpone the check of isomorphic maps at the end of the generation process (todoList empty)
                        //
                        if (checkIsomorphic(todoList, newMap) == true) {
                            isoRemoved++;
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
                                    // fNumberToAdd < (12 - 1) means that the map has less than 12 faces (considering the ocean), and in this case I don't need to check hasFWithCardinalityLessThanFiveConsideringTheOcean()
                                    //
                                    if (fNumberToAdd < (12 - 1)) {
                                        maps.add(newMap);
                                    } else if (newMap.hasFWithCardinalityLessThanFiveConsideringTheOcean() == false) {
                                        maps.add(newMap);
                                    } else {
                                        // System.out.println("Debug: unexpected condition? I don remember why I did not use an else condition here");
                                        // System.out.println("Debug: fNumberToAdd = " + fNumberToAdd + ", maxNumber = " + maxNumber);
                                        // Map4CT.printDetailedMap(newMap);

                                        // NOTE:
                                        // - else condition is when the new map has the number of faces we want (MAX_METHOD.F) and the map hasFWithCardinalityLessThanFiveConsideringTheOcean()
                                        // - The previous else-if condition is == false
                                        //
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
        // From the end because it is easier to remove elements: shifting will not affect indices
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
     * Remove maps marked to be removed Depending on the state of the elaborations, maps can be in the maps list and in the todo list
     */
    public void removeMapsMarkedToBeRemoved() {

        // I need to cleanup the maps lists
        // Loops on all maps (from the end)
        // From the end because it is easier to remove elements: shifting of elements in the list will not affect the loop indices
        //
        for (int iMaps = maps.size() - 1; iMaps >= 0; iMaps--) {

            // Check if the map is to be removed
            //
            if (maps.get(iMaps).removeLater) {
                maps.remove(iMaps);
            }
        }

        // I need to cleanup the todoList
        //
        for (int iTodo = todoList.size() - 1; iTodo >= 0; iTodo--) {

            // Check if the map is to be removed
            //
            if (todoList.get(iTodo).removeLater) {
                todoList.remove(iTodo);
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

        // Get from the todoList a map
        //
        if (randomElaboration) {
            mapToProcess = todoList.remove(new Random().nextInt(todoList.size()));
        } else {
            mapToProcess = todoList.remove(0);
        }

        // Return the map to process
        //
        return mapToProcess;
    }

    /**
     * Create a new map from the text representation (for debugging)
     */
    public Map4CT createMapFromTextRepresentationTest(String mapTextRepresentation, int facesToCreate) {

        Map4CT newMap = new Map4CT();

        // Example: "Potential criminals" (B)
        //
        newMap.insertF(3, 2, 2);
        newMap.insertF(4, 4, 1);
        newMap.insertF(5, 3, 3);
        newMap.insertF(6, 4, 2);
        newMap.insertF(7, 5, 2);
        newMap.insertF(8, 5, 2);
        newMap.insertF(9, 3, 4);
        newMap.insertF(10, 5, 2);
        newMap.insertF(11, 3, 4);
        newMap.insertF(12, 3, 2);
        newMap.insertF(13, 5, 2);
        newMap.insertF(14, 5, 2);
        newMap.insertF(15, 4, 3);
        newMap.insertF(16, 5, 2);
        newMap.insertF(17, 6, 3);
        newMap.insertF(18, 6, 2);
        newMap.insertF(19, 4, 4);
        newMap.insertF(20, 4, 3);
        newMap.insertF(21, 4, 2);
        newMap.insertF(22, 4, 2);
        newMap.insertF(23, 3, 3);
        newMap.insertF(24, 2, 3);
        newMap.insertF(25, 2, 2);
        newMap.insertF(26, 3, 4);
        newMap.insertF(27, 3, 4);

        // Tutte's map
        //
        // newMap.insertF(3, 1, 2);
        // newMap.insertF(4, 1, 2);
        // newMap.insertF(5, 3, 2);
        // newMap.insertF(6, 2, 3);
        // newMap.insertF(7, 2, 2);
        // newMap.insertF(8, 1, 3);
        // newMap.insertF(9, 2, 1);
        // newMap.insertF(10, 3, 2);
        // newMap.insertF(11, 3, 2);
        // newMap.insertF(12, 4, 3);
        // newMap.insertF(13, 4, 2);
        // newMap.insertF(14, 5, 3);
        // newMap.insertF(15, 3, 3);
        // newMap.insertF(16, 4, 4);
        // newMap.insertF(17, 5, 6);
        // newMap.insertF(18, 5, 2);
        // newMap.insertF(19, 6, 2);
        // newMap.insertF(20, 5, 3);
        // newMap.insertF(21, 6, 2);
        // newMap.insertF(22, 5, 3);
        // newMap.insertF(23, 7, 3);
        // newMap.insertF(24, 5, 4);

        // Update the map list and the todoList
        //
        maps.add(newMap);
        todoList.add(newMap);

        System.out.println("DEBUG: " + newMap.sequenceOfCoordinates.sequence);
        System.out.println("DEBUG: " + mapTextRepresentation);

        // Return the new map
        //
        return newMap;
    }

    /**
     * Create a new map from its text representation, stopping at n faces (facesToCreate) not considering the ocean
     * 
     * @param mapTextRepresentation
     *            Example: 1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+
     * @param facesToCreate
     *            Values permitted go from 2 to the number of faces of the map (max is: tokensOfTheMapTextRepresentation.length / 2;) not considering the ocean
     */
    public static Map4CT createMapFromTextRepresentation(String mapTextRepresentation, int facesToCreate) {

        // Create an empty (almost) map (default is 2 faces + the ocean = 3 faces)
        //
        Map4CT newMap = new Map4CT();

        // Analyze the input string to understand how many faces I need to create
        //
        String[] tokensOfTheMapTextRepresentation = mapTextRepresentation.split(", ");
        int numberOfFaces = facesToCreate;
        if (facesToCreate == -1) {
            numberOfFaces = tokensOfTheMapTextRepresentation.length / 2;
        } else if ((facesToCreate < 2) || (facesToCreate > (tokensOfTheMapTextRepresentation.length / 2))) {
            throw new IndexOutOfBoundsException();
        } else {
            numberOfFaces = facesToCreate;
        }

        // Loops all faces. The first two faces are created by the constructor() of Map4CT
        //
        for (int faceNumber = 3; faceNumber <= numberOfFaces; faceNumber++) {

            // Compute insertPoint and numberOfEToTouch
            //
            int tokenNumber = 1;
            int beginInsertPoint = 1;
            int numberOfEToTouch = 1;

            String[] currentTokensOfTheMapTextRepresentation = newMap.sequenceOfCoordinates.sequence.toString().substring(1).split(", ");

            // Loop all tokens to search insertPoint, filtering out faces not yet to insert and not visible
            //
            boolean beginInsertPointFound = false;
            while (beginInsertPointFound == false) {
                String extractNumber = tokensOfTheMapTextRepresentation[tokenNumber].substring(0, tokensOfTheMapTextRepresentation[tokenNumber].length() - 2);
                int faceAnalyzed = Integer.parseInt(extractNumber, 10);
                String extractVisibilityBeginOrEnd = tokensOfTheMapTextRepresentation[tokenNumber].substring(tokensOfTheMapTextRepresentation[tokenNumber].length() - 2, tokensOfTheMapTextRepresentation[tokenNumber].length() - 1);
                String extractVisibilityPlusOrMinus = null;
                for (int i = 0; i < currentTokensOfTheMapTextRepresentation.length; i++) {
                    if (currentTokensOfTheMapTextRepresentation[i].startsWith("" + faceAnalyzed + extractVisibilityBeginOrEnd)) {
                        extractVisibilityPlusOrMinus = currentTokensOfTheMapTextRepresentation[i].substring(currentTokensOfTheMapTextRepresentation[i].length() - 1, currentTokensOfTheMapTextRepresentation[i].length());
                    }
                }
                if ((faceAnalyzed < faceNumber) && (extractVisibilityPlusOrMinus.compareTo("+") == 0)) {
                    beginInsertPoint++;
                } else if (faceAnalyzed == faceNumber) {
                    beginInsertPointFound = true;
                }
                tokenNumber++;
            }

            // Continue the loop with a different check, to search numberOfEToTouch, filtering out faces not yet to insert
            //
            boolean numberOfEToTouchComputed = false;
            while (numberOfEToTouchComputed == false) {
                String extractNumber = tokensOfTheMapTextRepresentation[tokenNumber].substring(0, tokensOfTheMapTextRepresentation[tokenNumber].length() - 2);
                int faceAnalyzed = Integer.parseInt(extractNumber, 10);
                String extractVisibilityBeginOrEnd = tokensOfTheMapTextRepresentation[tokenNumber].substring(tokensOfTheMapTextRepresentation[tokenNumber].length() - 2, tokensOfTheMapTextRepresentation[tokenNumber].length() - 1);
                String extractVisibilityPlusOrMinus = null;
                for (int i = 0; i < currentTokensOfTheMapTextRepresentation.length; i++) {
                    if (currentTokensOfTheMapTextRepresentation[i].startsWith("" + faceAnalyzed + extractVisibilityBeginOrEnd)) {
                        extractVisibilityPlusOrMinus = currentTokensOfTheMapTextRepresentation[i].substring(currentTokensOfTheMapTextRepresentation[i].length() - 1, currentTokensOfTheMapTextRepresentation[i].length());
                    }
                }
                if ((faceAnalyzed < faceNumber) && (extractVisibilityPlusOrMinus.compareTo("+") == 0)) {
                    numberOfEToTouch++;
                } else if (faceAnalyzed == faceNumber) {
                    numberOfEToTouchComputed = true;
                }
                tokenNumber++;
            }

            // Insert the face in the new map
            //
            newMap.insertF(faceNumber, beginInsertPoint, numberOfEToTouch);
        }

        // Return the new map
        //
        return newMap;
    }

    /**
     * Check if the new map is isomorphic to an existing map
     */
    public boolean checkIsomorphic(ArrayList<Map4CT> theListOfExistingMaps, Map4CT theNewMap) {

        // Iso tester
        //
        IsomorphismTester isoTester = new TypedVF2IsomorphismTester();

        // Stop if you found an isomorphic map
        //
        boolean isoFound = false;

        // From the map to check I need to create a graph (evviva SSpace)
        //
        Graph4CT theNew4CTGraph = new Graph4CT();
        theNew4CTGraph.init();
        theNew4CTGraph.drawGraph(theNewMap);
        Graph<edu.ucla.sspace.graph.Edge> theNewGraph = theNew4CTGraph.getSSpaceGraph4CT();

        // Loop
        //
        for (int iMap = 0; (iMap < theListOfExistingMaps.size()) && (isoFound == false); iMap++) {
            Map4CT map4CTToCheck = theListOfExistingMaps.get(iMap);

            // Have maps different number of vertices? In this case they cannot be isomorphic!
            //
            if (map4CTToCheck.faces.size() == theNewMap.faces.size()) {

                // Second graph
                //
                Graph4CT existing4CTGraph = new Graph4CT();
                existing4CTGraph.init();
                existing4CTGraph.drawGraph(map4CTToCheck);
                Graph<edu.ucla.sspace.graph.Edge> existingGraph = existing4CTGraph.getSSpaceGraph4CT();

                // Iso?
                //
                if (isoTester.areIsomorphic(theNewGraph, existingGraph)) {

                    // Update the iso removed counter
                    //
                    isoFound = true;
                }
            }
        }

        // Did I find an iso?
        //
        return isoFound;
    }

    /**
     * Remove isomorphic maps NOTE: I decided to elaborate separately the maps and the todoList maps. This is because depending on various factors and status of elaboration the two lists may have few or many shared maps TODO: I should implement a cache (limited in size) of already created and reusable graphs
     */
    public void removeIsomorphicMaps(ArrayList<Map4CT> maps, int startingMap) {

        // Iso tester
        //
        IsomorphismTester isoTester = new TypedVF2IsomorphismTester();

        // Reset counters
        //
        isoOuterLoop = startingMap;
        isoInnerLoop = isoOuterLoop + 1;
        isoRemoved = 0;

        // If, during a cycle, no maps were isomorphic, I don't need to clean the list
        //
        boolean atLeastOneMapHasBeenRemoved = false;

        // Loop: I can stop one graph before the end
        //
        for (int iMapOuter = startingMap; (iMapOuter < (maps.size() - 1)) && (stopRemovingIsoRequested == false); iMapOuter++) {
            Map4CT map4CTOuter = maps.get(iMapOuter);

            // Update the outer counter
            //
            isoOuterLoop = iMapOuter;

            // Reset the flag
            //
            atLeastOneMapHasBeenRemoved = false;

            // First graph
            //
            Graph4CT graph4CTOuter = new Graph4CT();
            graph4CTOuter.init();
            graph4CTOuter.drawGraph(map4CTOuter);
            Graph<edu.ucla.sspace.graph.Edge> sSpaceGraph4CTOuter = graph4CTOuter.getSSpaceGraph4CT();

            // Loop
            //
            for (int iMapInner = iMapOuter + 1; (iMapInner < maps.size()) && (stopRemovingIsoRequested == false); iMapInner++) {
                Map4CT map4CTInner = maps.get(iMapInner);

                // Update the inner counter
                //
                isoInnerLoop = iMapInner;

                // Have maps different number of vertices? In this case they cannot be isomorphic!
                //
                if (map4CTOuter.faces.size() == map4CTInner.faces.size()) {

                    // Second graph
                    //
                    Graph4CT graph4CTInner = new Graph4CT();
                    graph4CTInner.init();
                    graph4CTInner.drawGraph(map4CTInner);
                    Graph<edu.ucla.sspace.graph.Edge> sSpaceGraph4CTInner = graph4CTInner.getSSpaceGraph4CT();

                    // Iso?
                    //
                    if (isoTester.areIsomorphic(sSpaceGraph4CTOuter, sSpaceGraph4CTInner)) {

                        // Update the iso removed counter
                        //
                        isoRemoved++;
                        map4CTInner.removeLater = true;
                        atLeastOneMapHasBeenRemoved = true;
                    }
                }
            }

            // Remove all items marked to be removed (maps and todo list)
            //
            if (atLeastOneMapHasBeenRemoved) {
                removeMapsMarkedToBeRemoved();
            }
        }
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

    /**
     * Main program
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        MapsGenerator mapsGenerator = new MapsGenerator();

        String mapString = "1b+, 11b+, 11e+, 8b+, 2b-, 9b+, 8e-, 3b-, 9e+, 6b+, 10b+, 10e+, 7b+, 7e+, 4b-, 5b-, 6e+, 5e+, 4e+, 3e+, 2e+, 1e+";
        Map4CT map = mapsGenerator.createMapFromTextRepresentation(mapString, 2);
        System.out.println("Debug: map = " + map.toString());

        map = mapsGenerator.createMapFromTextRepresentation(mapString, 3);
        System.out.println("Debug: map = " + map.toString());
    }
}
