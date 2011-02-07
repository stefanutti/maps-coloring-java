package it.tac.ct.ui.swixml;

import javax.swing.JFrame;

import org.swixml.SwingEngine;

public class SwixmlUITester extends JFrame {

    public SwixmlUITester() {
        try {
            SwingEngine<SwixmlUITester> engine = new SwingEngine<SwixmlUITester>(this);
            engine.render("config/test.xml").setVisible(true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SwixmlUITester();
    }
}
