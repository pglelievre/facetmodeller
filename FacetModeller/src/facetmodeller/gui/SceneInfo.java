package facetmodeller.gui;

import geometry.MyPoint3D;

/** Holds information about a scene to paint.
 * @author Peter
 */
public class SceneInfo {

    // -------------------- Properties -------------------
    
    private MyPoint3D origin, dimensions;
    private double scaling=0.0;
    
    // -------------------- Getters -------------------
    
    public MyPoint3D getOrigin() { return origin; }
    public MyPoint3D getDimensions() { return dimensions; }
    public double getScaling() { return scaling; }
    
    // -------------------- Setters -------------------
    
    public void setOrigin(MyPoint3D p) { origin=p; }
    public void setDimensions(MyPoint3D d) { dimensions=d; }
    public void setScaling(double s) { scaling=s; }
    
}
