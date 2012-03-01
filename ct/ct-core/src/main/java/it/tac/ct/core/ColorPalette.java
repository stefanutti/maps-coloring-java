/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.core;

import java.util.Stack;

/**
 * @author Mario Stefanutti
 * @version September 2007
 *          <p>
 *          Represent a palette of colors to use
 *          </p>
 */
public class ColorPalette {

    public Stack<COLORS> palette = new Stack<COLORS>();

    public boolean isPinned = false;
    public COLORS savedPinnedColor = COLORS.UNCOLORED; // Default for no color pinned

    /**
     * @param isFull
     *            preconfigured with 4 colors (or with the pinned color)?
     */
    public ColorPalette(boolean isFull) {
        if (isFull) {
            setToFull();
        } else {
            setToEmpty();
        }
    }

    /**
     * @param pinnedColor
     *            If a color has been already given (forced color)
     */
    public ColorPalette(COLORS pinnedColor) {
        savedPinnedColor = pinnedColor;
        isPinned = true;
        setToFull();
    }

    /**
     * Reset the palette of colors to all four colors
     */
    public void setToFull() {
        palette.removeAllElements();
        if (isPinned) {
            palette.push(savedPinnedColor);
        } else {
            palette.push(COLORS.FOUR);
            palette.push(COLORS.THREE);
            palette.push(COLORS.TWO);
            palette.push(COLORS.ONE);
        }
    }

    /**
     * Reset the palette of colors to no colors
     */
    public void setToEmpty() {
        palette.removeAllElements();
    }
}
