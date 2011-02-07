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

    /**
     * @param isPreconfigured
     *            preconfigured with 4 colors?
     */
    public ColorPalette(boolean isPreconfigured) {
        if (isPreconfigured) {
            resetToFull();
        }
        else {
            resetToEmpty();
        }
    }

    /**
     * Reset the palette of colors
     */
    public void resetToFull() {
        palette.removeAllElements();
        palette.push(COLORS.GRAY);
        palette.push(COLORS.BLUE);
        palette.push(COLORS.GREEN);
        palette.push(COLORS.RED);
    }

    /**
     * Reset the palette of colors
     */
    public void resetToEmpty() {
        palette.removeAllElements();
    }
}
