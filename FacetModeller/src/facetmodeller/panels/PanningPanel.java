package facetmodeller.panels;

import geometry.MyPoint3D;
import gui.ImagePanel;

/** Extends ImagePanel to a panel that can be panned.
 *
 * @author Peter
 */
public class PanningPanel extends ImagePanel {
    private static final long serialVersionUID = 1L;
    
    // ------------------- Properties ------------------
    
    private MyPoint3D pt1 = null; // the first point clicked (mouse down event)
    private double panX = 0;
    private double panY = 0;
    
    // ------------------- Getters/Setters ------------------

    public MyPoint3D getPt1() { return pt1; }
    public double getPanX() { return panX; }
    public double getPanY() { return panY; }
    
    public void setPt1(MyPoint3D pt1) { this.pt1 = pt1; }
    public void setPanX(double px) { panX = px; }
    public void setPanY(double py) { panY = py; }
    public void clearPan() {
        panX = 0;
        panY = 0;
    }
    
    // ------------------- Methods ------------------
    
    /** Applies the mouse-drag pan.
     * @param pt2
     */
    public void pan(MyPoint3D pt2) {
        
        // Make sure that everything required has been defined:
        if (pt1==null) {
            return;
        } // should never happen
        
        // Calculate movement of mouse between the two points:
        double dx = pt2.getX() - pt1.getX();
        double dy = pt2.getY() - pt1.getY();
        
        // Check for no movement:
        if ( dx==0.0 && dy==0.0 ) { return; }
        
        // Adjust the pan information:
        panX += dx;
        panY += dy;
        
    }
    
}
