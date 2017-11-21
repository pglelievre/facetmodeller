package geometry;

/** A 3D point (or vector) with x,y,z stored as double precision.
 * @author Peter Lelievre
 */
public class MyPoint3D {

    // -------------------- Properties -------------------

    // Coordinates:
    private double x, y, z;
    
    // Set to true once x,y,z are set:
    //private boolean s=false;

    // -------------------- Constructors -------------------
    
    /** Construction from (x,y,z) triplet.
     * @param x The x coordinate value.
     * @param y The y coordinate value.
     * @param z The z coordinate value.
     */
    public MyPoint3D(double x, double y, double z) {
        setXYZ(x,y,z);
    }
    
    /** Construction from (i,j) doublet and location on missing direction.
     * @param i
     * @param j
     * @param dir
     * @param loc */
    public MyPoint3D(double i, double j, Dir3D dir, double loc) {
        switch (dir) {
            case X:
                setXYZ(loc,i,j);
                break;
            case Y:
                setXYZ(i,loc,j);
                break;
            case Z:
                setXYZ(i,j,loc);
                break;
        } // default can't happen with use of enum
    }

    // -------------------- Deep Copy -------------------

    /** Deep copies the object.
     * @return A new MyPoint3D object copied from this one.
     */
    public MyPoint3D deepCopy() {
        return new MyPoint3D( getX(), getY(), getZ() );
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


    /** Returns the z coordinate value of the point.
     * @return The z coordinate value of the point.
     */
    public double getZ() { return z; }

    // -------------------- Setters -------------------
    
    private void setXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        //s = true;
    }
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    // -------------------- Static Methods -------------------

    public static MyPoint3D zero() {
        return new MyPoint3D(0.0,0.0,0.0);
    }

    public static MyPoint3D maxValue() {
        double d = Double.MAX_VALUE;
        return new MyPoint3D(d,d,d);
    }
    
    /** Calculates the vector between two 3D points.
     * @param p1 The first point (tail of vector).
     * @param p2 The second point (head of vector).
     * @return The vector from the first point to the second.
     */
    public static MyPoint3D vectorBetweenPoints(MyPoint3D p1, MyPoint3D p2) {
        return MyPoint3D.minus(p2,p1);
    }

    /** Calculates the distance between two 3D points.
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The distance between the two points.
     */
    public static double distanceBetweenPoints(MyPoint3D p1, MyPoint3D p2) {
        return Math.sqrt(
                Math.pow( p1.getX() - p2.getX() ,2.0) +
                Math.pow( p1.getY() - p2.getY() ,2.0) +
                Math.pow( p1.getZ() - p2.getZ() ,2.0) );
    }

    /** Calculates the map distance between two 3D points.
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The map distance between the two points.
     */
    public static double distanceBetweenPointsXY(MyPoint3D p1, MyPoint3D p2) {
        return Math.sqrt(
                Math.pow( p1.getX() - p2.getX() ,2.0) +
                Math.pow( p1.getY() - p2.getY() ,2.0) );
    }

    /** Calculates the angle between two 3D vectors.
     * @param v1 The first 3D vector.
     * @param v2 The second 3D vector.
     * @return The angle in radians between the vectors on [0,PI].
     */
    public static double angleBetweenVectors(MyPoint3D v1, MyPoint3D v2) {
        double dp = v1.dot(v2); // dot product
        double m1 = v1.norm(); // vector magnitude
        double m2 = v2.norm(); // vector magnitude
        double ca = dp / (m1*m2); // cos(angle)
        ca = Math.min(ca, 1.0); // to avoid possible NaN result of Math.acos ...
        ca = Math.max(ca,-1.0); // ... ca must be on [-1,1]
        return Math.acos(ca); // angle on [0,pi]
    }

    /** Dot product of two vectors.
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The dot product.
     */
    public static double dot(MyPoint3D v1, MyPoint3D v2) {
        return v1.getX() * v2.getX()
             + v1.getY() * v2.getY()
             + v1.getZ() * v2.getZ();
    }

    /** Cross product of two vectors.
     * @param p1 The first vector.
     * @param p2 The second vector.
     * @return The cross product.
     */
    public static MyPoint3D cross(MyPoint3D p1, MyPoint3D p2) {
        double x = p1.getY()*p2.getZ() - p1.getZ()*p2.getY();
        double y = p1.getZ()*p2.getX() - p1.getX()*p2.getZ();
        double z = p1.getX()*p2.getY() - p1.getY()*p2.getX();
        return new MyPoint3D(x,y,z);
    }
    
    public static MyPoint3D min(MyPoint3D p1, MyPoint3D p2) {
        double x = Math.min( p1.getX(), p2.getX() );
        double y = Math.min( p1.getY(), p2.getY() );
        double z = Math.min( p1.getZ(), p2.getZ() );
        return new MyPoint3D(x,y,z);
    }
    
    public static MyPoint3D max(MyPoint3D p1, MyPoint3D p2) {
        double x = Math.max( p1.getX(), p2.getX() );
        double y = Math.max( p1.getY(), p2.getY() );
        double z = Math.max( p1.getZ(), p2.getZ() );
        return new MyPoint3D(x,y,z);
    }

    /** Vector addition.
     * @param p1
     * @param p2
     * @return
     */
    public static MyPoint3D plus(MyPoint3D p1, MyPoint3D p2) {
        double x = p1.getX() + p2.getX();
        double y = p1.getY() + p2.getY();
        double z = p1.getZ() + p2.getZ();
        return new MyPoint3D(x,y,z);
    }

    /** First vector minus second vector.
     * @param p1
     * @param p2
     * @return
     */
    public static MyPoint3D minus(MyPoint3D p1, MyPoint3D p2) {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        double z = p1.getZ() - p2.getZ();
        return new MyPoint3D(x,y,z);
    }

    /** First vector divided by second vector (element-wise).
     * @param p1
     * @param p2
     * @return
     */
    public static MyPoint3D divide(MyPoint3D p1, MyPoint3D p2) {
        double x = p1.getX() / p2.getX();
        double y = p1.getY() / p2.getY();
        double z = p1.getZ() / p2.getZ();
        return new MyPoint3D(x,y,z);
    }

    // -------------------- Public Methods -------------------

    /** Calculates the distance to a supplied 3D point.
     * @param p The supplied point.
     * @return The distance to the supplied point.
     */
    public double distanceToPoint(MyPoint3D p) {
        return distanceBetweenPoints(this,p);
    }

    /** Calculates the map distance to a supplied 3D point.
     * @param p The supplied point.
     * @return The map distance to the supplied point.
     */
    public double distanceToPointXY(MyPoint3D p) {
        return distanceBetweenPointsXY(this,p);
    }

    /** Calculates the vector to a supplied 3D point.
     * @param p The supplied point.
     * @return The vector to the supplied point.
     */
    public MyPoint3D vectorToPoint(MyPoint3D p) {
        return vectorBetweenPoints(this,p);
    }

    /** Calculates the angle between this vector and a supplied 3D vector.
     * @param p The supplied vector.
     * @return The angle.
     */
    public double angleToVector(MyPoint3D p) {
        return angleBetweenVectors(this,p);
    }

    /** Dot product with another vectors.
     * @param v The other vector.
     * @return The dot product.
     */
    public double dot(MyPoint3D v) {
        return dot(this,v);
    }

    /** Cross product with another vectors.
     * @param v The other vector.
     * @return The cross product.
     */
    public MyPoint3D cross(MyPoint3D v) {
        return cross(this,v);
    }

    /** Returns the magnitude of the vector.
     * @return  */
    public double norm() {
        return Math.sqrt( this.dot(this) );
    }

    /** Returns the magnitude of the vector as projected in map view (z value ignored).
     * @return  */
    public double normXY() {
        return Math.sqrt( x*x + y*y );
    }

    /** Normalizes by the magnitude of the vector to create a unit vector. */
    public void normalize() {
        divide(norm());
    }
    
    /** Element-wise Math.min operation.
     * @param p */
    public void min(MyPoint3D p) {
        x = Math.min(x,p.getX());
        y = Math.min(y,p.getY());
        z = Math.min(z,p.getZ());
    }
    
    /** Element-wise Math.max operation.
     * @param p */
    public void max(MyPoint3D p) {
        x = Math.max(x,p.getX());
        y = Math.max(y,p.getY());
        z = Math.max(z,p.getZ());
    }
    
    /** Returns the minimum dimension value.
     * @return  */
    public double min() {
        return Math.min( Math.min(x,y) , z );
    }
    
    /** Returns the maximum dimension value.
     * @return  */
    public double max() {
        return Math.max( Math.max(x,y) , z );
    }

    // Projects onto a 2D plane with normal in a particular direction.
    public MyPoint2D getPoint2D(Dir3D dir) {
        switch (dir) {
            case X:
                return new MyPoint2D(getY(),getZ());
            case Y:
                return new MyPoint2D(getX(),getZ());
            case Z:
                return new MyPoint2D(getX(),getY());
            default:
                return null;
        }
    }

    // -------------------- Transforms -------------------
    
    /** Translates by the supplied point (addition).
     * @param p0 
     */
    public void plus(MyPoint3D p0) {
        x += p0.getX();
        y += p0.getY();
        z += p0.getZ();
    }
    
    /** Translates by the supplied point (addition).
     * @param dx
     * @param dy
     * @param dz
     */
    public void plus(double dx, double dy, double dz) {
        x += dx;
        y += dy;
        z += dz;
    }
    
    /** Translate by negative of supplied point (subtraction).
     * @param p0
     */
    public void minus(MyPoint3D p0) {
        x -= p0.getX();
        y -= p0.getY();
        z -= p0.getZ();
    }
    
    /** Scales Cartesian coordinates (element-wise multiplication).
     * @param p
     */
    public void times(MyPoint3D p) {
        x *= p.getX();
        y *= p.getY();
        z *= p.getZ();
    }
    
    /** Scales Cartesian coordinates (scalar multiplication).
     * @param sc
     */
    public void times(double sc) {
        x *= sc;
        y *= sc;
        z *= sc;
    }
    
    /** Scales x and y Cartesian coordinates (scalar multiplication).
     * @param sc
     */
    public void timesXY(double sc) {
        x *= sc;
        y *= sc;
    }
    
    /** Scales Cartesian coordinates (multiplication).
     * @param sc
     */
    public void divide(double sc) {
        x /= sc;
        y /= sc;
        z /= sc;
    }
    
    /** Multiplies by a rotation matrix.
     * @param mat */
    public void rotate(Matrix3D mat) {
         MyPoint3D p = mat.times(this);
         x = p.getX();
         y = p.getY();
         z = p.getZ();
    }
    
    /** Performs a z-flip transform (x and y are swapped and z becomes -z). */
    public void flipZ() {
        double t = x;
        x = y;
        y = t;
        z = -z;
    }
    
    /** Performs a - transform . */
    public void neg() {
        x = -x;
        y = -y;
        z = -z;
    }
    
    /** Performs a y = -y transform . */
    public void negY() { y = -y; }

    // -------------------- File I/O -------------------

    /** Provides a formatted string containing the x and y values.
     * @return A string of the form "( x , y , z )".
     */
    public String print() {
        return String.format("(%+10.4f ,%+10.4f ,%+10.4f )",getX(),getY(),getZ());
    }

    @Override
    public String toString() {
        return getX() + " " + getY() + " " + getZ();
    }

    public String toString(double tolzero) {
        double xt = getX();
        double yt = getY();
        double zt = getZ();
        if (Math.abs(xt)<=tolzero)  { xt = 0.0; }
        if (Math.abs(yt)<=tolzero)  { yt = 0.0; }
        if (Math.abs(zt)<=tolzero)  { zt = 0.0; }
        return xt + " " + yt + " " + zt;
    }
    
    public String toStringCSV() {
        return (float)getX() + ", " + (float)getY() + ", " + (float)getZ();
    }

}