package it.tac.ct.ui.zk;

import java.util.ArrayList;

import it.tac.ct.core.Map4CT;
import it.tac.ct.core.MapsGenerator;

import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Slider;

public class UIMainController extends GenericForwardComposer {

    private static final long serialVersionUID = 1L;

    // UI Objects
    //
    private Checkbox randomElaborationCheckbox;
    private Checkbox processAllTodoMapsCheckbox;
    private Combobox maxMethodCombobox;
    private Intbox maxNumberIntbox;
    private Combobox drawMethodCheckbox;
    private Slider transparencySlider;
    private Checkbox showFaceCardinalityCheckbox;
    private Button startElaborationButton;
    private Button pauseElaborationButton;
    private Button filterLessThanFourElaborationButton;
    private Button filterLessThanFiveElaborationButton;
    private Button filterLessThanFFacesElaborationButton;
    private Button copyMapsToTodoElaborationButton;
    private Button resetElaborationButton;
    // private Button goStartButton;
    // private Button goPreviousButton;
    // private Button goRandomButton;
    // private Button goNextButton;
    // private Button goEndButton;
    private Radiogroup selectedColorRadiogroup;
    // private Button autoColorTheMapButton;
    private Intbox slowdownMillisecIntbox;
    private Checkbox debugEnabledCheckbox;
    // private Button exportCurrentMapToGifButton;
    // private Button exportAllMapsToGifButton;
    private Button refreshInfoButton;
    private Intbox numberOfMapsCreatedIntbox;
    private Intbox numberOfMapsRemovedIntbox;
    private Intbox numberOfTodoMapsIntbox;

    // The class that generates maps
    //
    private MapsGenerator mapsGenerator = new MapsGenerator();

    // These two variables permit navigation through maps
    //
    private Map4CT map4CTCurrent = null;
    private int map4CTCurrentIndex = -1;

    /**
     * Utility class to run the generate() method of the MapsGenerator
     */
    private final Runnable runnableGenerate = new Runnable() {
        public void run() {
            try {
                mapsGenerator.generate();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                startElaborationButton.setDisabled(mapsGenerator.todoList.size() == 0);
                pauseElaborationButton.setDisabled(true);
                resetElaborationButton.setDisabled(false);
                filterLessThanFourElaborationButton.setDisabled(false);
                filterLessThanFiveElaborationButton.setDisabled(false);
                filterLessThanFFacesElaborationButton.setDisabled(false);
                copyMapsToTodoElaborationButton.setDisabled(false);
                refreshRuntimeInfo();
            }
        }
    };

    /**
     * EVENT
     */
    public void onClick$startElaborationButton() {

        // Read parameters set by user (User Interface)
        //
        mapsGenerator.slowdownMillisec = slowdownMillisecIntbox.getValue();
        mapsGenerator.logWhilePopulate = debugEnabledCheckbox.isChecked();
        mapsGenerator.randomElaboration = randomElaborationCheckbox.isChecked();
        mapsGenerator.processAll = processAllTodoMapsCheckbox.isChecked();

        if (maxMethodCombobox.getValue().equals("FIXED_MAPS_LEN")) {
            mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.FIXED_MAPS_LEN;
        } else if (maxMethodCombobox.getValue().equals("FIXED_MAPS_LEN")) {
            mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.MAPS;
        } else if (maxMethodCombobox.getValue().equals("FIXED_MAPS_LEN")) {
            mapsGenerator.maxMethod = MapsGenerator.MAX_METHOD.F;
        }
        mapsGenerator.maxNumber = maxNumberIntbox.getValue();

        // Set the buttons
        //
        startElaborationButton.setDisabled(true);
        pauseElaborationButton.setDisabled(false);
        resetElaborationButton.setDisabled(true);
        filterLessThanFourElaborationButton.setDisabled(true);
        filterLessThanFiveElaborationButton.setDisabled(true);
        filterLessThanFFacesElaborationButton.setDisabled(true);
        copyMapsToTodoElaborationButton.setDisabled(true);

        try {
            mapsGenerator.generate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Execute the thread
        //
        // new Thread(runnableGenerate).start();
    }

    /**
     * EVENT
     */
    public void onClick$pauseElaborationButton() {

        // I want to stop the elaboration
        // Information on screen will be updated by the thread itself when it stops
        //
        mapsGenerator.stopRequested = true;
    }

    /**
     * EVENT
     */
    public void onClick$filterLessThanFourElaborationButton() {

        mapsGenerator.removeMapsWithCardinalityLessThanFour();
        refreshRuntimeInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    /**
     * EVENT
     */
    public void onClick$filterLessThanFiveElaborationButton() {

        mapsGenerator.removeMapsWithCardinalityLessThanFive();
        refreshRuntimeInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    /**
     * EVENT
     */
    public void onClick$filterLessThanFFacesButton() {

        mapsGenerator.removeMapsWithLessThanFFaces(maxNumberIntbox.getValue());
        refreshRuntimeInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    /**
     * EVENT
     */
    public void onClick$copyMapsToTodoElaborationButton() {

        mapsGenerator.todoList = new ArrayList<Map4CT>();
        mapsGenerator.copyMapsToTodo();
        if (mapsGenerator.todoList.size() != 0) {
            startElaborationButton.setDisabled(false);
        }
        refreshRuntimeInfo();
    }

    /**
     * EVENT
     */
    public void onClick$resetElaborationButton() {

        mapsGenerator = new MapsGenerator();
        startElaborationButton.setDisabled(false);
        pauseElaborationButton.setDisabled(true);
        resetElaborationButton.setDisabled(true);
        filterLessThanFourElaborationButton.setDisabled(true);
        filterLessThanFiveElaborationButton.setDisabled(true);
        filterLessThanFFacesElaborationButton.setDisabled(true);
        copyMapsToTodoElaborationButton.setDisabled(true);
        refreshRuntimeInfo();

        // Reset graph if necessary
        //
        if (mapsGenerator.maps.size() == 0) {
            map4CTCurrent = null;
            map4CTCurrentIndex = -1;
            drawCurrentMap();
        }
    }

    /**
     * EVENT
     */
    public void onClick$refreshInfoButton() {
        refreshRuntimeInfo();
    }

    /**
     * Refresh runtime info
     */
    public void refreshRuntimeInfo() {
        numberOfMapsCreatedIntbox.setText("" + mapsGenerator.maps.size());
        numberOfMapsRemovedIntbox.setText("" + mapsGenerator.removed);
        numberOfTodoMapsIntbox.setText("" + mapsGenerator.todoList.size());
    }

    private void drawCurrentMap() {
    }
}
