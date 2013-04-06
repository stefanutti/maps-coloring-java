/*
 * Copyright 2011 by Mario Stefanutti, released under GPLv3.
 */
package it.tac.ct.ui.swixml;

import javax.swing.DefaultComboBoxModel;

/**
 * @author Mario Stefanutti
 *         <p>
 *         Utility class for SwixML combobox
 *         </p>
 */
public class SwixmlComboboxMaxMethod extends DefaultComboBoxModel {

    /**
	 * For serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public SwixmlComboboxMaxMethod() {
        super(new Object[] { "F", "FIXED_MAPS_LEN", "MAPS" });
    }
};
