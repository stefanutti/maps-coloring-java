/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.swixml.tests;

import java.net.URL;

import javax.swing.JFrame;

import org.swixml.SwingEngine;

public class SwixmlUITester extends JFrame {

    /**
	 * For serialization
	 */
	private static final long serialVersionUID = 1L;

	public SwixmlUITester() {
        try {
            SwingEngine<SwixmlUITester> engine = new SwingEngine<SwixmlUITester>(this);
            URL configFileURL = this.getClass().getClassLoader().getResource("config/4ct-test-snap.xml");
            engine.render(configFileURL).setVisible(true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SwixmlUITester();
    }
}
