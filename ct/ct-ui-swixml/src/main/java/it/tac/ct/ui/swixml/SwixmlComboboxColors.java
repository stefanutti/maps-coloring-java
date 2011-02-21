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
public class SwixmlComboboxColors extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public SwixmlComboboxColors() {
        super(new Object[] { "ONE", "TWO", "THREE", "FOUR" });
    }
};
