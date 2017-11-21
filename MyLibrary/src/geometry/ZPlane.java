package geometry;

/** Provides fitting and interpolation methods for a non-vertical plane in 3D space.
 * @author Peter
 */
public class ZPlane {
    
    private double a=0.0,b=0.0,c=0.0;
    private boolean ok=false;
    
    public ZPlane(MyPoint3D p0, MyPoint3D vn) {
        // Check normal is not horizontal:
        double zn = vn.getZ();
        if (zn==0.0) { return; }
        // The equation of the plane is
        //    xn*(x-x0) + yn*(y-y0) + zn*(z-z0) = 0
        // (see http://en.wikipedia.org/wiki/Plane_(geometry)#Point-normal_form_and_general_form_of_the_equation_of_a_plane)
        // so I interpolate using
        //    z = -( xn*(x-x0) + yn*(y-y0) )/zn + z0
        //    z = -x*xn/zn - y*yn/zn + x0*xn/zn + y0*yn/zn + z0
        // or
        //    z = a*x + b*y + c
        // with
        //    a = -xn/zn
        //    b = -yn/zn
        //    c = x0*xn/zn + y0*yn/zn + z0
        //      = -x0*a - y0*b + z0
        double xn = vn.getX();
        double yn = vn.getY();
        double x0 = p0.getX();
        double y0 = p0.getY();
        double z0 = p0.getZ();
        a = xn/zn;
        b = yn/zn;
        c = x0*a + y0*b + z0;
        a = -a;
        b = -b;
        ok = true;
    }
    
    public boolean check() { return ok; }
    
    public double interpolate(double x,double y) {
        return ( a*x + b*y + c );
    }
    
}
