package geometry;

import java.awt.geom.AffineTransform;

/** A closed polygon.
 * Extension of the MyPoint2DVector class with additional methods.
 * @author Peter Lelievre
 */
public class MyPolygon extends MyPoint2DVector {

    // -------------------- Public Methods -------------------

    /** Calculates the cumulative length of the polygon.
     * @return The sum of the lengths of the polygon's segments.
     */
    public double length() {

        // Loop over each point:
        double len = 0.0;
        MyPoint2D p1;
        MyPoint2D p2;
        for ( int j=1 ; j<=size() ; j++ ) {
            p1 = get(j-1);
            if ( j==size() ) {
                // This closes the polygon:
                p2 = get(0);
            } else {
                p2 = get(j);
            }
            len += MyPoint2D.distanceBetweenPoints(p1,p2);
        }

        // Return the polygon length
        return len;

    }

    /** Calculates the centre of mass of the polygon.
     * http://en.wikipedia.org/wiki/Polygon
     * http://www.cs.princeton.edu/introcs/35purple/Polygon.java.html
     * @return The centre of mass of the polygon.
     */
    public MyPoint2D com() {

        // Loop over each point:
        double xc=0.0, yc=0.0, t;
        MyPoint2D p1;
        MyPoint2D p2;
        for ( int j=1 ; j<=size() ; j++ ) {
            p1 = get(j-1);
            if ( j==size() ) {
                // This closes the polygon:
                p2 = get(0);
            } else {
                p2 = get(j);
            }
            t = p1.getX()*p2.getY() - p1.getY()*p2.getX();
            xc += ( p1.getX() + p2.getX() )*t;
            yc += ( p1.getY() + p2.getY() )*t;
        }

        // Scale the values:
        t = 6.0*signedArea();
        xc /= t;
        yc /= t;

        // Construct the MyPoint2D output object:
        return new MyPoint2D(xc,yc);

    }

    /** Returns the area of the polygon.
     * http://en.wikipedia.org/wiki/Polygon
     * http://www.cs.princeton.edu/introcs/35purple/Polygon.java.html
     * @return The absolute value of the unsigned area of the polygon.
     */
    public double area() { return Math.abs(signedArea()); }

    /** Returns true if the outline is specified clockwise.
     * Assumes the polygon does not intersect itself.
     * http://en.wikipedia.org/wiki/Polygon
     * http://www.cs.princeton.edu/introcs/35purple/Polygon.java.html
     * @return True if the signed area of the polygon is negative, false otherwise.
     */
    public boolean isClockwise() {
         // If the signed area is negative then the outline is clockwise:
        return (signedArea() < 0.0);
    }

    /** Applies an AffineTransform to the MyPoint2D coordinates.
     * @param trans The transform to apply.
     */
    public void transform(AffineTransform trans) {
        MyPoint2D p;
        for ( int j=0 ; j<size() ; j++ ) {
            p = get(j);
            p.transform(trans);
        }
    }

    // -------------------- Private Methods -------------------

    /** Returns the signed area of the polygon.
     * This is used to determine if the polygon is clockwise or counterclockwise.
     */
    private double signedArea() {

        // Loop over each point:
        double a=0.0, t;
        MyPoint2D p1;
        MyPoint2D p2;
        for ( int j=1 ; j<=size() ; j++ ) {
            p1 = get(j-1);
            if ( j==size() ) {
                // This closes the polygon:
                p2 = get(0);
            } else {
                p2 = get(j);
            }
            t = p1.getX()*p2.getY() - p1.getY()*p2.getX();
            a += t;
        }

        // Scale the value and return:
        a = 0.5*a;
        return a;

    }

}
