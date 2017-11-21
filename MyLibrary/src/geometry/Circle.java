package geometry;

import java.util.Arrays;

/** A circle defined by a central point and a radius.
 * @author Peter Lelievre
 */
public class Circle {
    
    // -------------------- Properties -------------------

    private double radius = -1.0; /** The radius (if less than zero then the circle has not been or could not be defined). */
    private MyPoint2D centre = null; /** The centre coordinates (if null then the circle has not been or could not be defined). */

    // -------------------- Constructor -------------------

    /** Constructs a circle from a centre coordinate and radius.
     * @param p0
     * @param r
     */
    public Circle( MyPoint2D p0, double r ) {
        radius = r;
        centre = p0;
    }

    /** Constructs a circle from a centre coordinates and radius.
     * @param x
     * @param y
     * @param r
     */
    public Circle( double x, double y, double r ) {
        radius = r;
        centre = new MyPoint2D(x,y);
    }
    
    /** Constructs a circle from three points.
     * The points can not be co-linear. If they are then the radius will be set to a non-positive value.
     * Use the getRadius method to check for a non-positive radius after construction.
     * Alternately, the getCentre method will return null if the points were co-linear.
     * @param p0 A first point on the circle.
     * @param p1 A second point on the circle.
     * @param p2 A third point on the circle.
     */
    public Circle( MyPoint2D p0, MyPoint2D p1, MyPoint2D p2 ) {

        /* The points must not be collinear; if the largest distance between
         * a pair of points is equal to the sum of the smaller distances
         * between the other pairs of points then the points are collinear: */
        double[] dp = new double[3]; // will hold distances between pairs of points
        dp[0] = MyPoint2D.distanceBetweenPoints(p0,p1);
        dp[1] = MyPoint2D.distanceBetweenPoints(p1,p2);
        dp[2] = MyPoint2D.distanceBetweenPoints(p2,p0);
        Arrays.sort(dp); // sorted in ascending numerical order

        // Test for colinearity within a 0.1% relative tolerance:
        if ( Math.abs(dp[2]-dp[1]-dp[0]) / dp[2] <= 0.001 ) { return; }

        // Calculate the radius and centre of the circle defined by the three coordinates:
        double p0x = p0.getX();
        double p0y = p0.getY();
        double p1x = p1.getX();
        double p1y = p1.getY();
        double p2x = p2.getX();
        double p2y = p2.getY();
        double a = MyPoint2D.distanceBetweenPoints(p0,p1);
        double b = MyPoint2D.distanceBetweenPoints(p1,p2);
        double c = MyPoint2D.distanceBetweenPoints(p2,p0);

        // Circumradius:
        // http://mathworld.wolfram.com/Circumradius.html
        radius = (a*b*c) / Math.sqrt( (a+b+c)*(b+c-a)*(c+a-b)*(a+b-c) );

        // Circumcentre:
        // http://en.wikipedia.org/wiki/Circumcircle#Cartesian_coordinates
        double d = 2.0*( p0x*(p1y-p2y) + p1x*(p2y-p0y) + p2x*(p0y-p1y) );
        double xc = ( (p0x*p0x+p0y*p0y)*(p1y-p2y) + (p1x*p1x+p1y*p1y)*(p2y-p0y) + (p2x*p2x+p2y*p2y)*(p0y-p1y) ) / d;
        double yc = ( (p0x*p0x+p0y*p0y)*(p2x-p1x) + (p1x*p1x+p1y*p1y)*(p0x-p2x) + (p2x*p2x+p2y*p2y)*(p1x-p0x) ) / d;
        centre = new MyPoint2D(xc,yc);

    }

    // -------------------- Getters -------------------

    /** Returns the radius of the circle.
     * The radius will be non-positive if the constructor was called with three co-linear points.
     * @return The radius of the circle.
     */
    public double getRadius() { return radius; }

    /** Returns the coordinates of the centre of the circle.
     * The return value will be null if the constructor was called with three co-linear points.
     * @return The coordinates of the centre of the circle.
     */
    public MyPoint2D getCentre() { return centre; }

    // -------------------- Public methods -------------------

    /** Checks if a point is inside a circle or on its boundary.
     * @param p
     * @return  */
    public boolean inside(MyPoint2D p) {
        double r = centre.distanceToPoint(p);
        return ( r <= radius );
    }
    
    /** Interpolates the z position at an internal point assuming the circle
     * represents the divider of a sphere.
     * This method does not check if the supplied point is internal
     * and will calculate the square-root of a negative value if external.
     * @param p
     * @return  */
    public double interpolate(MyPoint2D p) {
        MyPoint2D v = centre.vectorToPoint(p);
        double r2 = radius*radius;
        double n2 = v.norm2(); // = (x-x0)^2 + (y-y0)^2
        return Math.sqrt(r2-n2); // (x-x0)^2 + (y-y0)^2 + (z-z0)^2 = n2 + (z-z0)^2 = r2
    }
    
    /** If the supplied point is inside the circle then the result is the same point.
     *  If it is outside the circle then it is projected onto the circle (towards the centre).
     * @param p
     * @return 
     */
    public MyPoint2D project(MyPoint2D p) {
        // Check if we are inside:
        if (inside(p)) { return p; }
        // Project:
        double r = radius / centre.distanceToPoint(p);
        double x = p.getX()*r; // similar triangles approach
        double y = p.getY()*r;
        return new MyPoint2D(x,y);
    }
    
}
