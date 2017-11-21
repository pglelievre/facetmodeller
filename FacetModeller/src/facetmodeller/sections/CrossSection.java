package facetmodeller.sections;

import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Color;

/** A vertical cross section.
 * @author Peter
 */
public abstract class CrossSection extends Section {
    
    public CrossSection() { // required by the SessionLoader (should not be used elsewhere)
        super();
    }
    
    public CrossSection(Color col) { // required by the SessionLoader (should not be used elsewhere)
        super(col);
    }
    
    /** Transform from section image pixel coordinates to real-space measurements for a vertical cross-section.
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
        
        // x and y vary linearly with horizontal pixel coordinate:
        double v2i = v2.getX();
        double mx = v3.getX() / v2i;
        double my = v3.getY() / v2i;
        double d2i = p2.getX() - getClicked1().getX();
        double x = getTyped1().getX() + mx*d2i;
        double y = getTyped1().getY() + my*d2i;
        
        // z varies linearly with vertical pixel coordinate:
        double v2j = v2.getY();
        double mz = v3.getZ() / v2j;
        double d2j = p2.getY() - getClicked1().getY();
        double z = getTyped1().getZ() + mz*d2j;
        
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
        
        // x and y need to be interpolated (projection of point onto line):
        MyPoint2D t1 = new MyPoint2D(getTyped1().getX(),getTyped1().getY()); // first typed point (in map view)
        MyPoint2D vt = new MyPoint2D(v3.getX(),v3.getY()); // vector between typed points (in map view)
        double l2 = v2.getX(); // distance between clicked points (in map view)
        double l3 = vt.norm(); // distance between typed points (in map view)
        double mi = l2/l3; // scaling converstion factor from spatial to pixel coordinates
        MyPoint2D vp = new MyPoint2D(p3.getX(),p3.getY());
        vp.minus(t1); // vector from first typed point to p3 (in map view)
        vt.normalize(); // unit vector along the section (in map view)
        double i = getClicked1().getX() + vt.dot(vp)*mi;
        
        // z varies linearly with vertical pixel coordinate:
        double mj = v2.getY() / v3.getZ();
        double j = getClicked1().getY() + mj*( p3.getZ() - getTyped1().getZ() );
        
        // Return a new MyPoint2D object:
        return new MyPoint2D(i,j);
        
    }
    
}
