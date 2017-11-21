package geometry;

/** Provides fitting and interpolation methods for a non-vertical triangle in 3D space.
 * @author Peter
 */
public class ZBary extends Bary2D {
    
    private final double z0,z1,z2;
    
    public ZBary(MyPoint3D p0, MyPoint3D p1, MyPoint3D p2) {
        super(new MyPoint2D(p0),new MyPoint2D(p1),new MyPoint2D(p2));
        // Store the z values of the vertices for interpolation later:
        z0 = p0.getZ();
        z1 = p1.getZ();
        z2 = p2.getZ();
    }
    
//    public double interpolate(MyPoint3D p) {
//        // Calculate:
//        calculate(new MyPoint2D(p));
//        // Interpolate:
//        return interpolate(z0,z1,z2);
//    }

    public double interpolate() {
        return interpolate(z0,z1,z2);
    }
    
}