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

	/**
	 * @param isPreconfigured
	 *            preconfigured with 4 colors?
	 */
	public ColorPalette(boolean isPreconfigured) {
		if (isPreconfigured) {
			resetToFull();
		} else {
			resetToEmpty();
		}
	}

	/**
	 * Reset the palette of colors
	 */
	public void resetToFull() {
		palette.removeAllElements();
		palette.push(COLORS.FOUR);
		palette.push(COLORS.THREE);
		palette.push(COLORS.TWO);
		palette.push(COLORS.ONE);
	}

	/**
	 * Reset the palette of colors
	 */
	public void resetToEmpty() {
		palette.removeAllElements();
	}
}
