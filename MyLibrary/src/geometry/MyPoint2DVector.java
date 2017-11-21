package geometry;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/** A Vector of MyPoint2D objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class MyPoint2DVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private final ArrayList<MyPoint2D> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void MyPoint2DVector() {}

    // -------------------- Deep Copy -------------------

    /** Deep copies the object.
     * @return A new MyPoiny2DVector object copied from this one.
     */
    public MyPoint2DVector deepCopy(){

        // Create a new object:
        MyPoint2DVector out = new MyPoint2DVector();

        // Deep copy over the coordinate points:
        for ( int i=0 ; i<this.size() ; i++ ) {
            MyPoint2D p = this.get(i).deepCopy();
            out.add(p);
        }
        
        // Return the new object:
        return out;

    }

    // -------------------- Public Methods -------------------

    /** Clears the vector. */
    public void clear() {
        vector.clear();
    }

    /** Returns the size of the vector.
     * @return The size of the vector.
     */
    public int size() {
        return vector.size();
    }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public MyPoint2D get(int i) {
        return vector.get(i);
    }

    /** Adds an element to the end of the vector.
     * @param p The MyPoint2D object to add to the end of the vector.
     */
    public void add(MyPoint2D p) {
        vector.add(p);
    }

    /** Adds an element to the vector at a specific position.
     * Any existing elements are shifted as required.
     * @param i The position at which to add.
     * @param p The MyPoint2D object to add.
     */
    public void add(int i, MyPoint2D p) {
        vector.add(i,p);
    }

    /** Adds many elements to the end of the vector.
     * @param pp A MyPoint2DVector containing MyPoint2D objects to add to the end of the vector.
     */
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public void addAll(MyPoint2DVector pp) {
        vector.addAll(pp.vector);
    }

    /** Removes an element from the vector.
     * @param i The index of the element to remove.
     */
    public void remove(int i) {
        vector.remove(i);
    }

    /* Removes an element from the vector.
     * @param p The element to remove.
    public void remove(MyPoint2D p) {
        vector.remove(p);
    }
     */

    /** Adds the point coordinates in the vector to a general path object for plotting.
     * The first point is added using path.moveTo() and the subsequent points are added using path.lineTo().
     * The path is not closed automatically. Instead, use the path.close() method if desired.
     * @param path The path to add the point coordinates to.
     */
    public void addToPath(GeneralPath path) {

        // Add the first point:
        MyPoint2D p = get(0);
        path.moveTo((float)p.getX(),(float)p.getY());

        // Loop over the other points:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            path.lineTo((float)p.getX(),(float)p.getY());
        }

    }

    /** Calculates the average separation between the points in the vector.
     * @return The average separation.
     */
    public double averageSeparation() {
        return totalLength() / size(); // mean = sum/n
    }

    /** Calculates the total length between the points in the vector (sum of line segment lengths).
     * @return The total length.
     */
    public double totalLength() {
        double d = 0.0;
        for ( int i=1 ; i<size() ; i++ ) {
            MyPoint2D p1 = get(i-1);
            MyPoint2D p2 = get(i);
            d += MyPoint2D.distanceBetweenPoints(p1,p2); // sum
        }
        return d;
    }

    /** Calculates the range of the x coordinates in the vector.
     * @return The range of the x coordinates.
     */
    public double rangeX() {

        // Initialization:
        MyPoint2D p = get(0);
        double minX = p.getX();
        double maxX = p.getX();

        // Find min and max coordinates:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            minX = Math.min(minX,p.getX());
            maxX = Math.max(maxX,p.getX());
        }

        // Return range:
        return maxX-minX;

    }

    /** Calculates the range of the y coordinates in the vector.
     * @return The range of the y coordinates.
     */
    public double rangeY() {

        // Initialization:
        MyPoint2D p = get(0);
        double minY = p.getY();
        double maxY = p.getY();

        // Find min and max coordinates:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            minY = Math.min(minY,p.getY());
            maxY = Math.max(maxY,p.getY());
        }

        // Return range:
        return maxY-minY;

    }

    /** Determines the minimum value of the x coordinates in the vector.
     * @return The minimum value of the x coordinates.
     */
    public double minX() {

        // Initialization:
        MyPoint2D p = get(0);
        double minX = p.getX();

        // Find min coordinate:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            minX = Math.min(minX,p.getX());
        }

        // Return minimum value:
        return minX;

    }

    /** Determines the minimum value of the y coordinates in the vector.
     * @return The minimum value of the y coordinates.
     */
    public double minY() {

        // Initialization:
        MyPoint2D p = get(0);
        double minY = p.getY();

        // Find min coordinate:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            minY = Math.min(minY,p.getY());
        }

        // Return minimum value:
        return minY;

    }

    /** Determines the maximum value of the x coordinates in the vector.
     * @return The maximum value of the x coordinates.
     */
    public double maxX() {

        // Initialization:
        MyPoint2D p = get(0);
        double maxX = p.getX();

        // Find max coordinate:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            maxX = Math.max(maxX,p.getX());
        }

        // Return maximum value:
        return maxX;

    }

    /** Determines the maximum value of the y coordinates in the vector.
     * @return The maximum value of the y coordinates.
     */
    public double maxY() {

        // Initialization:
        MyPoint2D p = get(0);
        double maxY = p.getY();

        // Find max coordinate:
        for ( int i=1 ; i<size() ; i++ ) {
            p = get(i);
            maxY = Math.max(maxY,p.getY());
        }

        // Return maximum value:
        return maxY;

    }
    
//    public MyPoint2D findClosestPoint(MyPoint2D p) {
//        int i = findClosest(p);
//        if (i<0) {
//            return null;
//        } else {
//            return get(i);
//        }
//    }

    /** Finds the point in the vector closest to an input point.
     * @param p The input point.
     * @return The index of the point that is closest to the input point. */
    public int findClosest(MyPoint2D p) {
        // Call the other version of this method:
        ArrayList<Integer> ibest = findClosest(p,1);
        // Return the index to the closest point:
        return ibest.get(0);
    }

    /** Finds n points in the vector closest to an input point. The result is not sorted.
     * @param p The input point.
     * @param n The number of points requested.
     * @return The indices of the points closest to the input point. */
    public ArrayList<Integer> findClosest(MyPoint2D p, int n) {

        // Check for consistency:
        if (n>size()) { return null; }

        // Initialize the output object:
        ArrayList<Integer> ibest = new ArrayList<>(); // the indices of the best (closest) coordinates
        ArrayList<Double> dbest = new ArrayList<>();
        
        // Initialize the ibest and dbest arrays
        for (int i=0 ; i<n ; i++ ) {
            MyPoint2D pi = get(i);
            double d = p.distanceToPoint(pi);
            ibest.add(i);
            dbest.add(d);
        }

        // Check for trivial case:
        if (n==size()) { return ibest; }

        // Loop over the other points:
        for ( int i=n ; i<size() ; i++ ) { // already looked at first n points

            // Calculate distance to current point:
            MyPoint2D pi = get(i);
            double d = p.distanceToPoint(pi);

            // Find the maximum value in the dbest array:
            int imax = findMax(dbest);

            // Check if we should replace the maximum value:
            if ( d < dbest.get(imax) ) {
                ibest.set(imax,i);
                dbest.set(imax,d);
            }
        }

        // Return the information:
        return ibest;

    }

    /** Finds all points in the vector that are within a supplied distance to an input point. The result is not sorted.
     * @param p The input point.
     * @param r The distance.
     * @return The indices of the points closest to the input point. */
    public ArrayList<Integer> findClose(MyPoint2D p, double r) {

        // Initialize the output object:
        ArrayList<Integer> ibest = new ArrayList<>(); // the indices of the best (closest) coordinates
        //ArrayList<Double> dbest = new ArrayList<Double>();

        // Loop over the points:
        for ( int i=0 ; i<size() ; i++ ) {
            
            // Calculate distance to current point:
            MyPoint2D pi = get(i);
            double d = p.distanceToPoint(pi);

            // Check if the point is within the supplied radial distance:
            if ( d <= r ) {
                // Add the point to the record of close points:
                ibest.add(i);
                //dbest.add(d);
            }
        }

        // Return the information:
        return ibest;

    }

    /** Calculates the centre of mass of a polygonal connection of points.
     * http://en.wikipedia.org/wiki/Polygon
     * http://www.cs.princeton.edu/introcs/35purple/Polygon.java.html
     * @return The centre of mass of the polygon.
     */
    public MyPoint2D centroid() {

        MyPoint2D p1;
        MyPoint2D p2;
        double xc,yc;

        // Check for a single point or two points:
        if (size()==1) { return get(0).deepCopy(); }
        if (size()==2) {
            p1 = get(0);
            p2 = get(1);
            xc = ( p1.getX() + p2.getX() ) / 2.0d;
            yc = ( p1.getY() + p2.getY() ) / 2.0d;
            return new MyPoint2D(xc,yc);
        }

        // Check for zero area:
        Double a = 6.0*signedArea();
        if (a==0.0) {
            p1 = get(0).deepCopy();
            p2 = get(0).deepCopy();
            for ( int j=1 ; j<size() ; j++ ) {
                MyPoint2D pj;
                pj = get(j);
                p1.min(pj);
                p2.max(pj);
            }
            xc = ( p1.getX() + p2.getX() )*0.5; // average of min and max x values
            yc = ( p1.getY() + p2.getY() )*0.5;
            return new MyPoint2D(xc,yc);
        }

        // Loop over each point:
        xc = 0.0;
        yc = 0.0;
        for ( int j=1 ; j<=size() ; j++ ) {
            p1 = get(j-1);
            if ( j==size() ) {
                // This closes the polygon:
                p2 = get(0);
            } else {
                p2 = get(j);
            }
            double t = p1.getX()*p2.getY() - p1.getY()*p2.getX();
            xc += ( p1.getX() + p2.getX() )*t;
            yc += ( p1.getY() + p2.getY() )*t;
        }

        // Scale the values:
        xc /= a;
        yc /= a;

        // Construct the MyPoint2D output object:
        return new MyPoint2D(xc,yc);

    }

    // -------------------- Private Methods -------------------

    private static int findMax(ArrayList<Double> d) {
        int imax = 0; // index of maximum value
        double dmax = d.get(0); // maximum value
        for (int i=1 ; i<d.size() ; i++ ) {
            double di = d.get(i);
            if (di>dmax) {
                imax = i;
                dmax = di;
            }
        }
        return imax;
    }

    /** Returns the signed area of a polygonal connection of points. */
    private double signedArea() {

        // Loop over each point:
        double t, a=0.0;
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
