/*
 * Baseado em ListView, custom cell factories (Jonathan Giles)
 * http://fxexperience.com/2012/05/listview-custom-cell-factories-and-context-menus/
 * @version 0.1 (24/11/2014)
 */
package it.tac.ct.ui.javafx.tests.swing_fx3;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
 
/**
 * Classe para construção de Menu de Contexto em GUI JavaFX
 * @param <T> tipo do objeto contido na lista
 */
public class ContextMenuListCell<T> extends ListCell<T> {
     
    public static <T> Callback<ListView<T>,ListCell<T>> forListView(ContextMenu contextMenu) {
        return forListView(contextMenu, null);
    }
      
    public static <T> Callback<ListView<T>,ListCell<T>> forListView(final ContextMenu contextMenu, final Callback<ListView<T>,ListCell<T>> cellFactory) {
        return new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> listView) {
                ListCell<T> cell = cellFactory == null ? new DefaultListCell<>() : cellFactory.call(listView);
                cell.setContextMenu(contextMenu);
                return cell;
            }
        };
    }
     
    public ContextMenuListCell(ContextMenu contextMenu) {
        setContextMenu(contextMenu);
    }
}
