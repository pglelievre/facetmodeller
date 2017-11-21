package geometry;

/** Barycentric coordinates for triangle in 2D space.
 * @author Peter
 */
public class Bary2D {
    
    private final MyPoint2D p0;
    private final double x1,x2,y1,y2,det;
    private double b0,b1,b2;
    
    public Bary2D(MyPoint2D p0, MyPoint2D p1, MyPoint2D p2) {
        // Store the first point:
        this.p0 = p0.deepCopy();
        // Calculate vectors between vertices:
        MyPoint2D v1 = p0.vectorToPoint(p1);
        MyPoint2D v2 = p0.vectorToPoint(p2);
        // Calculate the determinant:
        x1 = v1.getX();
        y1 = v1.getY();
        x2 = v2.getX();
        y2 = v2.getY();
        det = x1*y2 - x2*y1;
    }
    
    public boolean check() { return ( det!=0.0 ); }
    
    public void calculate(MyPoint2D p) {
        // Calculate vector from p0 to p:
        MyPoint2D vp = p0.vectorToPoint(p);
        // Calculate the barycentric coordinates:
        double xp = vp.getX();
        double yp = vp.getY();
        b1 = ( y2*xp - x2*yp )/det;
        b2 = ( x1*yp - y1*xp )/det;
        b0 = 1.0 - b1 - b2;
    }
    
//    public MyPoint3D getCoords() {
//        return new MyPoint3D(b0,b1,b2);
//    }
    
    public double interpolate(double z0, double z1, double z2) {
        return ( b0*z0 + b1*z1 + b2*z2 );
    }
    
    public boolean inside() {
       // Check if the point is strictly within the triangle:
       return ( b0>0.0 && b0<1.0 && b1>0.0 && b1<1.0 && b2>0.0 && b2<1.0 );
    }
    
    public boolean inOrOn() {
       // Check if the point is inside or on the edge of the triangle:
       return ( b0>=0 && b0<=1.0 && b1>=0 && b1<=1.0 && b2>=0 && b2<=1.0 );
    }
    
    public boolean inOrOn(double tol) {
       // Check if the point is more-or-less within the triangle:
       double t1 = -tol;
       double t2 = 1.0 + tol;
       return ( b0>=t1 && b0<=t2 && b1>=t1 && b1<=t2 && b2>=t1 && b2<=t2 );
    }
    
}