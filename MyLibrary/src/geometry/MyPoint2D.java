package geometry;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/** A 2D point with x,y stored as double precision.
 * @author Peter Lelievre
 */
public class MyPoint2D {

    // -------------------- Properties -------------------

    // Coordinates:
    private double x, y;

    // -------------------- Constructors -------------------

    //public MyPoint2D() { }
    
    public MyPoint2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Construction from (x,y) pair.
     * @param x The x coordinate value.
     * @param y The y coordinate value.
     */
    public MyPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Construction from Point2D object.
     * @param p The Point2D object to take the x and y coordinates from.
     */
    public MyPoint2D(Point2D p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    /** Construction from MyPoint3D object.
     * @param p The MyPoint3D object to take the x and y coordinates from.
     */
    public MyPoint2D(MyPoint3D p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    // -------------------- Deep Copy -------------------

    /** Deep copies the object.
     * @return A new MyPoint2D object copied from this one.
     */
    public MyPoint2D deepCopy() {
        return new MyPoint2D( getX(), getY() );
    }

    // -------------------- Getters -------------------

    /** Returns the x coordinate value of the point.
     * @return The x coordinate value of the point.
     */
    public double getX() { return x; }

    /** Returns the y coordinate value of the point.
     * @return The y coordinate value of the point.
     */
    public double getY() { return y; }

    // -------------------- Setters -------------------
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // -------------------- Static Methods -------------------

    public static MyPoint2D zero() {
        return new MyPoint2D(0.0,0.0);
    }

    public static MyPoint2D maxValue() {
        double d = Double.MAX_VALUE;
        return new MyPoint2D(d,d);
    }

    /** Calculates the vector between two 2D points.
     * @param p1 The first point (tail of vector).
     * @param p2 The second point (head of vector).
     * @return The vector from the first point to the second.
     */
    public static MyPoint2D vectorBetweenPoints(MyPoint2D p1, MyPoint2D p2) {
        return MyPoint2D.minus(p2,p1);
    }

    /** Calculates the distance between two 2D points.
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The distance between the two points.
     */
    public static double distanceBetweenPoints(MyPoint2D p1, MyPoint2D p2) {
        return Math.sqrt(
                Math.pow( p1.getX() - p2.getX() ,2.0) +
                Math.pow( p1.getY() - p2.getY() ,2.0) );
    }

    /** Dot product of two vectors.
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The dot product.
     */
    public static double dot(MyPoint2D v1, MyPoint2D v2) {
        return v1.getX() * v2.getX()
             + v1.getY() * v2.getY();
    }

    /** Calculates the angle between three points ordered 1-2-3 with the angle at 2.
     * @param p0 The first point.
     * @param p1 The second point (at which the angle resides).
     * @param p2 The third point.
     * @return The angle in radians.
     */
    public static double angleBetweenThreePoints(MyPoint2D p0, MyPoint2D p1, MyPoint2D p2) {

        double a = distanceBetweenPoints(p0, p1);
        double b = distanceBetweenPoints(p1, p2);
        double c = distanceBetweenPoints(p2, p0); // this is the triangle side opposite the angle

        // Law of cosines: c^2 = a^2 + b^2 - 2a*b*cos(angle)
        double d = (a * a + b * b - c * c) / (2.0 * a * b); // = cos(angle)
        return Math.acos(d); // kept in radians, so have to convert later

    }
    
    public static MyPoint2D min(MyPoint2D p1, MyPoint2D p2) {
        double x = Math.min( p1.getX(), p2.getX() );
        double y = Math.min( p1.getY(), p2.getY() );
        return new MyPoint2D(x,y);
    }
    
    public static MyPoint2D max(MyPoint2D p1, MyPoint2D p2) {
        double x = Math.max( p1.getX(), p2.getX() );
        double y = Math.max( p1.getY(), p2.getY() );
        return new MyPoint2D(x,y);
    }

    /** Vector addition.
     * @param p1
     * @param p2
     * @return
     */
    public static MyPoint2D plus(MyPoint2D p1, MyPoint2D p2) {
        double x = p1.getX() + p2.getX();
        double y = p1.getY() + p2.getY();
        return new MyPoint2D(x,y);
    }

    /** First vector minus second vector.
     * @param p1
     * @param p2
     * @return
     */
    public static MyPoint2D minus(MyPoint2D p1, MyPoint2D p2) {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        return new MyPoint2D(x,y);
    }

    /** First vector divided by second vector (element-wise).
     * @param p1
     * @param p2
     * @return
     */
    public static MyPoint2D divide(MyPoint2D p1, MyPoint2D p2) {
        double x = p1.getX() / p2.getX();
        double y = p1.getY() / p2.getY();
        return new MyPoint2D(x,y);
    }

    // -------------------- Private Methods -------------------

    /** Casts the MyPoint2D object to a new Point2D.Double object.
     * @return A new Point2D object.
     */
    private Point2D.Double cast() {
        return new Point2D.Double(x,y);
    }

    // -------------------- Public Methods -------------------

    public Dimension toDimension() {
        return new Dimension((int)x,(int)y);
    }
    
    /** Calculates the distance to a supplied 2D point.
     * @param p The supplied point.
     * @return The distance to the supplied point.
     */
    public double distanceToPoint(MyPoint2D p) {
        return distanceBetweenPoints(this,p);
    }

    /** Calculates the vector to a supplied 2D point.
     * @param p The supplied point.
     * @return The vector to the supplied point.
     */
    public MyPoint2D vectorToPoint(MyPoint2D p) {
        return vectorBetweenPoints(this,p);
    }

    /** Dot product with another vectors.
     * @param v The other vector.
     * @return The dot product.
     */
    public double dot(MyPoint2D v) {
        return dot(this,v);
    }

    /** Returns the magnitude of the vector.
     * @return  */
    public double norm() {
        return Math.sqrt( this.dot(this) );
    }

    /** Returns the squared magnitude of the vector.
     * @return  */
    public double norm2() {
        return this.dot(this);
    }

    /** Normalizes by the magnitude of the vector to create a unit vector. */
    public void normalize() {
        divide(norm());
    }
    
    /** Element-wise Math.min operation.
     * @param p */
    public void min(MyPoint2D p) {
        x = Math.min(x,p.getX());
        y = Math.min(y,p.getY());
    }
    
    /** Element-wise Math.max operation.
     * @param p */
    public void max(MyPoint2D p) {
        x = Math.max(x,p.getX());
        y = Math.max(y,p.getY());
    }
    
    /** Returns the minimum dimension value.
     * @return  */
    public double min() {
        return Math.min(x,y);
    }
    
    /** Returns the maximum dimension value.
     * @return  */
    public double max() {
        return Math.max(x,y);
    }

    // -------------------- Transforms -------------------

    /** Applies an affine transform the the point.
     * @param trans The affine transform to apply.
     */
    public void transform(AffineTransform trans) {
        if (trans==null) { return; }
        Point2D p = new Point2D.Double();
        trans.transform(this.cast(),p);
        x = p.getX();
        y = p.getY();
    }
    
    /** Translates by the supplied point (addition).
     * @param p0 
     */
    public void plus(MyPoint2D p0) {
        x += p0.getX();
        y += p0.getY();
    }
    
    /** Translates by the supplied point (addition).
     * @param dx
     * @param dy
     */
    public void plus(double dx, double dy) {
        x += dx;
        y += dy;
    }
    
    /** Translate by negative of supplied point (subtraction).
     * @param p0
     */
    public void minus(MyPoint2D p0) {
        x -= p0.getX();
        y -= p0.getY();
    }
    
    /** Scales Cartesian coordinates (element-wise multiplication).
     * @param p
     */
    public void times(MyPoint2D p) {
        x *= p.getX();
        y *= p.getY();
    }
    
    /** Scales Cartesian coordinates (scalar multiplication).
     * @param sc
     */
    public void times(double sc) {
        x *= sc;
        y *= sc;
    }
    
    /** Scales Cartesian coordinates (scalar multiplication).
     * @param sx
     * @param sy
     */
    public void times(double sx, double sy) {
        x *= sx;
        y *= sy;
    }
    
    /** Scales Cartesian coordinates (multiplication).
     * @param sc
     */
    public void divide(double sc) {
        x /= sc;
        y /= sc;
    }
    
    /** Performs a - transform . */
    public void neg() {
        x = -x;
        y = -y;
    }
    
    /** Performs a y = -y transform . */
    public void negY() { y = -y; }

    // -------------------- File I/O -------------------

    /** Provides a formatted string containing the x and y values.
     * @return A string of the form "( x , y )".
     */
    public String print() {
        return String.format("(%+10.4f ,%+10.4f )",getX(),getY());
    }
    
    @Override
    public String toString() {
        return getX() + " " + getY();
    }

    public String toString(double tolzero) {
        double xt = getX();
        double yt = getY();
        if (Math.abs(xt)<=tolzero)  { xt = 0.0; }
        if (Math.abs(yt)<=tolzero)  { yt = 0.0; }
        return xt + " " + yt;
    }

}