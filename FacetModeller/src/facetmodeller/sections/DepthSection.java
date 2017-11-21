package facetmodeller.sections;

import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Color;

/** A horizontal depth section that is aligned with the x,y axes.
 * @author Peter
 */
public abstract class DepthSection extends Section {
    
    public DepthSection() { // required by the SessionLoader (should not be used elsewhere)
        super();
    }
    
    public DepthSection(Color col) {
        super(col);
    }
    
    /** Transforms from section image pixel coordinates to real-space measurements for a topography slice
     * (horizontal depth-slice, assumed to be in map view with North up, East right).
     * @param p2
     * @return  */
    @Override
    public MyPoint3D imageToSpace(MyPoint2D p2) {
        
        // Check input and calibration:
        if (p2==null) { return null; }
        if (!isCalibrated()) { return null; }
        
        // Calculate some quantities required for interpolation:
        MyPoint2D v2 = getClicked1().vectorToPoint(getClicked2());
        MyPoint3D v3 = getTyped1().vectorToPoint(getTyped2());
        
        // x and y vary linearly with horizontal and vertical pixel coordinates respectively:
        double mx = v3.getX() / v2.getX();
        double my = v3.getY() / v2.getY();
        double x = getTyped1().getX() + mx*( p2.getX() - getClicked1().getX() );
        double y = getTyped1().getY() + my*( p2.getY() - getClicked1().getY() );
        
        // z is constant across a depth slice:
        double z = getTyped1().getZ();
        
        // Return a new MyPoint3D object:
        return new MyPoint3D(x,y,z);
        
    }
    
    @Override
    public MyPoint3D imageCornerToSpace(MyPoint2D p2) { return imageToSpace(p2); }

    /** Projects a 3D point in space onto the section in pixel coordinates.
     * @param p3
     * @return  */
    @Override
    public MyPoint2D projectOnto(MyPoint3D p3) {
        
        // Check input and calibration:
        if (p3==null) { return null; }
        if (!isCalibrated()) { return null; }
        
        // Calculate some quantities required for interpolation:
        MyPoint2D v2 = getClicked1().vectorToPoint(getClicked2());
        MyPoint3D v3 = getTyped1().vectorToPoint(getTyped2());
        
        // x and y vary linearly with horizontal and vertical pixel coordinates respectively:
        double mi = v2.getX() / v3.getX();
        double mj = v2.getY() / v3.getY();
        double i = getClicked1().getX() + mi*( p3.getX() - getTyped1().getX());
        double j = getClicked1().getY() + mj*( p3.getY() - getTyped1().getY() );
        
        // Return a new MyPoint2D object:
        return new MyPoint2D(i,j);
        
    }
    
}
