package it.tac.ct.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mario Stefanutti
 *         <p>
 *         Handle loops (for (int i = 0; i < N; i++)) in rondom way
 *         </p>
 */
public class RandomLoopManager {

    // The loop values: (int i = 0; i < N; i++)
    //
    private List<Integer> loopValueList = null;

    // If you don't want to process all the values, set processAll = false This variable keeps track of the number of
    // elements to process
    //
    private int numberOfIterations = 0;

    /**
     * @param start
     *            Starting value
     * @param end
     *            End value
     * @param processAll
     *            If you don't want to process all the values, set processAll = false
     */
    public RandomLoopManager(int start, int end, boolean processAll) {
        loopValueList = new ArrayList<Integer>();

        for (int i = 0; i < (end - start); i++) {
            loopValueList.add(new Integer(start + i));
        }

        if (processAll) {
            numberOfIterations = loopValueList.size();
        } else {
            numberOfIterations = Math.round((long)(Math.random() * loopValueList.size()));
            if (numberOfIterations == 0) {
                numberOfIterations = 1;
            }
        }
    }

    /**
     * @param randomFlag
     *            Do you want the values in random order?
     * @return One of the values of the list (-1 when the list is empty or iterations are finished)
     */
    public int getValue(boolean randomFlag) {

        // The value to return
        //
        int value = 0;

        // Handle each situation
        //
        if (loopValueList.size() == 0) {
            value = -1;
        } else if (numberOfIterations == 0) {
            value = -1;
        } else if (randomFlag == false) {
            value = loopValueList.remove(0).intValue();
        } else {
            int index = Math.round((long)(Math.random() * loopValueList.size()));
            value = loopValueList.remove(index).intValue();
        }

        // Decrease the max number of iterations to do
        //
        numberOfIterations--;

        // Return the value
        //
        return value;
    }

    /**
     * Main (test).
     */
    public static void main() {
        RandomLoopManager randomLoopManager = new RandomLoopManager(0, 20, true);
        int value = 0;

        while ((value = randomLoopManager.getValue(false)) != -1) {
            System.out.println("Debug: ordered values = " + value);
        }

        randomLoopManager = new RandomLoopManager(0, 20, true);
        while ((value = randomLoopManager.getValue(true)) != -1) {
            System.out.println("Debug2: unordered values = " + value);
        }
    }
}
