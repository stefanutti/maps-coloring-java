package it.tac.ct.core;

/**
 * @author Mario Stefanutti
 * @version September 2010
 *          <p>
 *          It is used to store graphical coordinate
 *          </p>
 */
public class GraphicalObjectCoordinate {

    // Used only during the graphical representation of square maps.
    //
    // Normalized space: x -> [0..1], y -> [0..1]
    //
    public float x = 0.0f;
    public float y = 0.0f;
    public float w = 0.0f;
    public float h = 0.0f;

    // Used only during the graphical representation of circular maps.
    //
    // In this case, all sectors start at center.
    //
    public float startRadius = 0.0f;
    public float stopRadius = 0.0f;
    public float startAngle = 0.0f;
    public float stopAngle = 0.0f;

    /**
     * Default constructor
     */
    public GraphicalObjectCoordinate() {
    }
}
