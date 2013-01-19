/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.swixml.tests;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;

import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;

import org.swixml.SwingEngine;

public class SwixmlFlickering extends JFrame {

    private final JPanel mapExplorer = null;

    public SwixmlFlickering() {
        try {
            SwingEngine<SwixmlFlickering> engine = new SwingEngine<SwixmlFlickering>(this);
            URL configFileURL = this.getClass().getClassLoader().getResource("config/tests/4ct-test-flickering.xml");
            engine.render(configFileURL);

            // Create the graphic canvas
            GWindow window = new GWindow();

            // HERE IS THE WORKAROUND
            //
            RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
            // Create scene with default viewport and world extent settings
            // GScene scene = new GScene(window);

            // mapExplorer.add(window.getCanvas());

            // Create the graphics object and add to the scene
            // GObject helloWorld = new HelloWorld();
            // scene.add(helloWorld);

            // Get visible
            //
            validate();
            setVisible(true);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Defines the geometry and presentation for the sample graphic object.
     */
    public class HelloWorld extends GObject {
        private GSegment star_;

        public HelloWorld() {
            star_ = new GSegment();
            GStyle starStyle = new GStyle();
            starStyle.setForegroundColor(new Color(255, 0, 0));
            starStyle.setBackgroundColor(new Color(255, 255, 0));
            starStyle.setLineWidth(3);
            setStyle(starStyle);
            addSegment(star_);

            GText text = new GText("HelloWorld", GPosition.MIDDLE);
            GStyle textStyle = new GStyle();
            textStyle.setForegroundColor(new Color(100, 100, 150));
            textStyle.setBackgroundColor(null);
            textStyle.setFont(new Font("Dialog", Font.BOLD, 48));
            text.setStyle(textStyle);
            star_.setText(text);
        }

        /**
         * This method is called whenever the canvas needs to redraw this object. For efficiency, prepare as much of the graphic object up front (such as sub object, segment and style setup) and set geometry only in this method.
         */
        public void draw() {
            star_.setGeometry(Geometry.createStar(220, 220, 200, 80, 15));
        }
    }

    public static void main(String[] args) {
        new SwixmlFlickering();
    }
}
