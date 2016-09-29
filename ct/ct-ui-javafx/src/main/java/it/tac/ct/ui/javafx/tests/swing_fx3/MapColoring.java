/*
 * UnB - Universidade de BrasÃ­lia
 * CIC - Departamento de CiÃªncia da ComputaÃ§Ã£o
 * IA - IntroduÃ§Ã£o a InteligÃªncia Artificial
 * 
 * @author zidenis
 * @version 0.1 (24/11/2014)
*/
package it.tac.ct.ui.javafx.tests.swing_fx3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ColoraÃ§Ã£o de mapas com restriÃ§Ã£o de que as Ã¡reas adjacentes nÃ£o podem possuir uma mesma cor.
 * utiliza biblioteca tuProlog que implementa um motor de inferÃªncia prolog
 * @author Denis Albuquerque (11/0114388)
 * @version 0.1 (24/11/2014)
 */
public class MapColoring {
    public MapColoring() {
    }
    
    /**
     * ObtÃ©m a lista de cores disponÃ­veis para coloraÃ§Ã£o do mapa.
     * @return lista de cores
     */
    public List<String> getColors() {
        List<String> listColors = new ArrayList<>();
        return listColors;
    }
    
    /**
     * ObtÃ©m a lista de nomes dos mapas contidos na Base de Conhecimento
     * @return lista de nomes dos mapas
     */
    public List<String> getMaps() {
        List<String> listMaps = new ArrayList<String>();
        listMaps.add("a");
        listMaps.add("b");
        return listMaps;
    }
    
    /**
     * ObtÃ©m uma lista de Ã¡reas de um mapa
     * @param map nome do mapa
     * @return lista de nomes das Ã¡reas do mapa
     */
    public List<String> getAreasMap(String map) {
        List<String> listAreas = new ArrayList<String>();
        listAreas.add("a");
        listAreas.add("b");
        return listAreas;
    }
    
    /**
     * ObtÃ©m uma lista de Ã¡reas adjacentes Ã  uma Ã¡rea de um mapa
     * @param map nome do mapa
     * @param area nome da Ã¡rea
     * @return lista de nomes das Ã¡reas adjacentes Ã  Ã¡rea informada
     */
    public List<String> getAreasAdjacentes(String map, String area) {
        List<String> listAreas = new ArrayList<String>();
        listAreas.add("a");
        listAreas.add("b");
        return listAreas;
    }
    
    /**
     * Obtem uma soluÃ§Ã£o de coloraÃ§Ã£o de mapa
     * @param map nome do mapa a ser colorido
     * @return lista de pares (Nome da Ã�rea, Cor da Ã�rea)
     */
    public List<Pair> colorMap(String map) {
        return this.colorMap(map, null, null);
    }
    
    /**
     * Obtem uma soluÃ§Ã£o de coloraÃ§Ã£o de mapa
     * considerando uma restriÃ§Ã£o de que uma Ã¡rea deve possui uma cor determinada.
     * @param map nome do mapa a ser colorido
     * @param area area cuja cor deverÃ¡ ser restrita
     * @param cor cor definida como restriÃ§Ã£o da Ã¡rea
     * @return lista de pares (Nome da Ã�rea, Cor da Ã�rea)
     */
    public List<Pair> colorMap(String map, String area, String cor) {
        List<Pair> listAreas = new ArrayList<>();

        return listAreas;
    }
}