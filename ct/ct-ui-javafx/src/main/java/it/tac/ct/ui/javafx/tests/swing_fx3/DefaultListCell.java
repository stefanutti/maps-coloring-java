/*
 * Baseado em ListView, custom cell factories (Jonathan Giles)
 * http://fxexperience.com/2012/05/listview-custom-cell-factories-and-context-menus/
 * @version 0.1 (24/11/2014)
 */
package it.tac.ct.ui.javafx.tests.swing_fx3;

import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * Um item padrão para uso no menu de contexto
 */
public class DefaultListCell<String> extends ListCell<String> {
    @Override public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (item instanceof Node) {
            setText(null); 
            Node currentNode = getGraphic();
            Node newNode = (Node) item;
            if (currentNode == null || ! currentNode.equals(newNode)) {
                setGraphic(newNode);
            }
        } else {
            setText(item == null ? "null" : item.toString());
            setGraphic(null);
        }
    }
}
