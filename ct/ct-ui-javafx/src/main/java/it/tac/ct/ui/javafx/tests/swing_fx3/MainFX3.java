/*
 * UnB - Universidade de Brasília
 * CIC - Departamento de Ciência da Computação
 * IA - Introdução a Inteligência Artificial
 * 
 * @author zidenis
 * @version 0.1 (24/11/2014)
*/
package it.tac.ct.ui.javafx.tests.swing_fx3;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Uma aplicação JavaFX para coloração de um mapas
 * onde há a restrição de que as áreas adjacentes não podem possuir uma mesma cor
 * @author Denis Albuquerque (11/0114388)
 * @version 0.1 (24/11/2014)
 */
public class MainFX3 extends Application {
    
    private final MapColoring mapColoring;
    private final List<String> mapas;
    
    // Compoenentes da Interface Gráfica
    private Stage primaryStage;
    private AnchorPane rootLayout;
    
    /**
     * Inicialização do programa
     * @param args argumentos do programa
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public MainFX3() {
        mapColoring = new MapColoring();
        mapas = mapColoring.getMaps();
    }
    
    /**
     * Inicializa a Interface Gráfica do Usuário JavaFX
     * @param primaryStage JavaFX stage
     * @throws MalformedURLException 
     */
    @Override
    public void start(Stage primaryStage) throws MalformedURLException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Map Coloring");
        // Carrega os objetos da GUI descritos em documento XML
        System.out.println("Debugxxx: " + MainFX3.class.getResource("."));
        // FXMLLoader loader = new FXMLLoader(MainFX3.class.getResource(MainFX3.class.getResource(".") + "MainFX3.fxml"));
        FXMLLoader loader = new FXMLLoader(new URL(MainFX3.class.getResource(".") + "MainFX3.fxml"));
        try {
            rootLayout = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainFX3.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        // Carrega GUI Controller
        GUIController controller = loader.getController();
        controller.setMain(this);
        primaryStage.show();
    }

    /**
     * Obtém a lista de nomes dos mapas contidos na Base de Conhecimento
     * @return lista de nomes dos mapas
     */
    public List<String> getMapas() {
        return mapas;
    }

    /**
     * Obtém uma lista de áreas de um mapa
     * @param mapa nome do mapa
     * @return lista de nomes das áreas do mapa
     */
    public List<String> getAreas(String mapa) {
        return mapColoring.getAreasMap(mapa);
    }
    
    /**
     * Obtém uma lista de áreas adjacentes à uma área de um mapa
     * @param mapa nome do mapa
     * @param area nome da área
     * @return lista de nomes das áreas adjacentes à área informada
     */
    public List<String> getAdjacencias(String mapa, String area) {
        return mapColoring.getAreasAdjacentes(mapa, area);
    }
    
    /**
     * Obtém a lista de cores disponíveis para coloração do mapa
     * @return lista de cores
     */
    public List<String> getCores() {
        return mapColoring.getColors();
    }
    
    /**
     * Constrói um grafo colorido de um mapa
     * @param mapa nome do mapa a ser colorido
     * @return Compomente com um grafo colorido
     */
    public mxGraphComponent buildMapGraph(String mapa) {
        return this.buildMapGraph(mapa, null, null);
    }
    
    /**
     * Constrói um grafo colorido de um mapa,
     * considerando uma restrição de que uma área deve possui uma cor determinada.
     * Utiliza biblioteca jgraphx
     * @param mapa nome do mapa a ser colorido
     * @param area area cuja cor deverá ser restrita
     * @param cor cor definida como restrição da área
     * @return Compomente com um grafo colorido
     */
    public mxGraphComponent buildMapGraph(String mapa, String area, String cor) {
        // Tamanho dos nós do mapa
        final int NODE_HEIGHT = 40;
        final int NODE_WIDTH = 60;
        List<Pair> colorMap;
        if (area == null && cor == null) {
            colorMap = mapColoring.colorMap(mapa);
        }
        else {
            colorMap = mapColoring.colorMap(mapa, area, cor);
        }
        Map<String, mxCell> graphMap = new HashMap<>();
        Hashtable<String, Object> style = new Hashtable<>();
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setDragEnabled(false);
        graphComponent.setEventsEnabled(false);
        Object parent = graph.getDefaultParent();
        // Estilo dos elementos do grafo
        //mxIGraphLayout layout = new mxCircleLayout(graph);
        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        //mxOrganicLayout layout = new mxOrganicLayout(graph, new Rectangle(10, 10));
        
        
        mxStylesheet stylesheet = graph.getStylesheet();
        style.put(mxConstants.STYLE_STROKEWIDTH, 2);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTSIZE, 16);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_HEXAGON);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_HEXAGON);
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
        stylesheet.setDefaultVertexStyle(style);
        graph.setStylesheet(stylesheet);
        // Adiciona nós do grafo
        for (Pair node : colorMap) {
            String nodeId = node.getFirst().toString();
            graphMap.put(nodeId, (mxCell) graph.insertVertex(parent, nodeId, nodeId, 0, 0, NODE_WIDTH, NODE_HEIGHT, "defaultVertex;fillColor=" + node.getSecond().toString()));
        }
        // Adiciona arcos do grafo
        for (String nodeId : graphMap.keySet()) {
            for (String adj : mapColoring.getAreasAdjacentes(mapa, nodeId)) {
                graph.insertEdge(parent, null, null, graphMap.get(nodeId), graphMap.get(adj), "strokeColor=black;strokeWidth=2;endArrow=none");
            }
        }
        layout.execute(graph.getDefaultParent());
        graph.getModel().endUpdate();
// Cria imagem PNG com o grafo
//        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.lightGray, true, null);
//        try {
//            ImageIO.write(image, "PNG", new File("mapGraph.png"));
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        return graphComponent;
    }
}
