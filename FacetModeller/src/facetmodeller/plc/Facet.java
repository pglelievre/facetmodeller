package facetmodeller.plc;

import facetmodeller.groups.Group;
import facetmodeller.sections.SectionVector;
import facetmodeller.sections.Section;
import geometry.MyPoint3D;
import java.awt.Color;

/** A polygonal facet of connected nodes.
 * A facet is connected to a group and possibly many sections.
 * @author Peter Lelievre
 */
public class Facet extends HasGroup {

    // -------------------- Properties -------------------

    private boolean boundaryMarker = false;
    private final NodeVector nodes = new NodeVector();
//    private MyPoint3D centroid = null; // centroid point
    private MyPoint3D normal = null; // normal vector
//    private final SectionVector sections = new SectionVector();

    // ------------------- Constructors ------------------

    public Facet() { super(); } // required by the SessionLoader (should not be used elsewhere)
    public Facet(Group g) { super(g); }

    // ------------------- Copy Methods ------------------

    // Returns a new Facet object with links to existing nodes.
    public Facet copy() {
        Facet facet = new Facet(getGroup());
        facet.addNodes(nodes);
        return facet;
    }
    
    // -------------------- Getters -------------------

    public boolean getBoundaryMarker() { return boundaryMarker; }
    public NodeVector getNodes() { return nodes; }
    public SectionVector getSections() {
//        return sections;
        return nodes.getSections();
    }
    public Node getNode(int i) { return nodes.get(i); }
//    public Section getSection(int i) { return sections.get(i); }

    public Color getColor() { return getGroup().getFacetColor(); }
    
    public MyPoint3D getCentroid() {
        // Recalculate centroid if required:
        //if ( centroid == null ) { calculateCentroid(); }
        //return centroid;
        // Always recalculate the centroid in case the node coordinates or their section calibration changes:
        return calculateCentroid();
    }
    
    public MyPoint3D getNormal() {
        // Recalculate normal if required:
        if ( normal == null ) { calculateNormal(); }
        return normal;
        //return calculateNormal();
    }
    
    // -------------------- Setters -------------------
    
    public void setBoundaryMarker(boolean bm) { boundaryMarker = bm; }
    
    // -------------------- Public Methods -------------------
    
    public void toggleBoundaryMarker() { boundaryMarker = !boundaryMarker; }

    public int size() {
        return nodes.size();
    }

    /** Returns true if the supplied node is in the facet.
     * @param n
     * @return  */
    public boolean containsNode(Node n) {
        if (n==null) { return false; }
        return nodes.contains(n);
    }

    /** Returns true if the supplied section is in the facet.
     * @param s
     * @return  */
    public boolean containsSection(Section s) {
        if (s==null) { return false; }
//        return sections.contains(s);
        return nodes.containsSection(s);
    }

    public void addNode(Node n) {
        // Check the node isn't already in the list:
        if (nodes.contains(n)) { return; }
        // Add the node to the list:
        nodes.add(n);
        // Nullify the centroid and normal so they will be recalculated (when required):
        //centroid = null;
        normal = null;
    }

    public void addNodes(NodeVector n) {
        nodes.addAll(n);
        // Nullify the centroid and normal so they will be recalculated (when required):
        //centroid = null;
        normal = null;
    }

//    public void addSection(Section s) {
//        // Check the section isn't already in the list:
//        if (!sections.contains(s)) { sections.add(s); }
//    }

    public void clear() {
        nodes.clear();
        //centroid = null;
        normal = null;
    }

    public void removeNode(Node n) {
        nodes.remove(n);
        // Nullify the centroid and normal so they will be recalculated (when required):
        //centroid = null;
        normal = null;
    }

    public void removeLastNode() {
        nodes.removeLast();
        // Nullify the centroid and normal so they will be recalculated (when required):
        //centroid = null;
        normal = null;
    }

//    public void removeSection(Section s) {
//        sections.remove(s);
//    }

    /** Calculates the minimum 3D vertex angle in the facet in radians.
     * @return  */
    public double minAngle() {

        // Check the facet has more than two nodes:
        if (size()<=2) { return 0; }

        // Loop over each node (each vertex) in the facet:
        double a = Math.PI; // will store the minimum angle in radians
        int n = size() - 1; // last index of the nodes vector for the facet
        for (int i=0 ; i<size() ; i++ ) {
            
            // Get the 3D point for the current node and its neighbours:
            MyPoint3D pi = getNode(i).getPoint3D();
            MyPoint3D p1,p2;
            if (i==0) {
                p1 = getNode(n).getPoint3D(); // last node in the facet
            } else {
                p1 = getNode(i-1).getPoint3D(); // previous neighbour
            }
            if (i==n) {
                p2 = getNode(0).getPoint3D(); // first node in the facet
            } else {
                p2 = getNode(i+1).getPoint3D(); // next neighbour
            }

            // Calculate the spatial vectors between the current node and its neighbours:
            MyPoint3D v1 = pi.vectorToPoint(p1);
            MyPoint3D v2 = pi.vectorToPoint(p2);

            // Calculate the angle between those two vectors and compare to current minimum angle:
            a = Math.min( a , v1.angleToVector(v2) ); // angle is on [0,PI]

        }

        // Return the minimum angle:
        return a;

    }
    
    /** Reverses the node order in the facet. */
    public void reverse() {
        nodes.reverse();
        // Flip the normal vector:
        if (normal!=null) { normal.neg(); }
    }

//    /** Determines if a supplied point is inside the facet. */
//    public boolean inFacet(MyPoint2D p) {
//        // http://www.blackpawn.com/texts/pointinpoly/default.html
//        if (size()==0) { return false; }
//        if ()
//        MyPoint2d
//        return ( sameSide(p,a, b,c) and sameSide(p,b, a,c) and sameSide(p,c, a,b) );
//    }
//
//    private boolean sameSide(double px, double py, double qx, double qy,
//            double ax, double ay, double bx, double by) {
//        double ux,uy,vx,vy,wx,wy,cp,cq;
//        ux = bx - ax;
//        uy = by - ay;
//        vx = px - ax;
//        vy = py - ay;
//        wx = qx - ax;
//        wy = qy - ay;
//        cp = ux*vy - uy*vx;
//        cq = ux*wy - uy*wx;
//        return ( cp*cq >= 0 );
//    }

//    /** Calculates the distance from the facet centroid to a 2D point.
//     * @param p The 2D point.
//     * @return The distance between the two points.
//     */
//    public double distanceToPoint(MyPoint2D p) {
//        MyPoint2D c = centroid(nodes);
//        return Math.sqrt(
//                Math.pow( c.getX() - p.getX() ,2.0) +
//                Math.pow( c.getY() - p.getY() ,2.0) );
//    }

    /** Sorts the nodes based on their ID values. */
    public void sortNodesByIDs() {
        // Sort the nodes by their ID value:
        nodes.sortByIDs();
        // Nullify the centroid and normal so they will be recalculated (when required):
        //centroid = null;
        normal = null;
    }

    /* Returns true if the vector intersects the supplied facet.
     * @param p The supplied facet.
    public boolean intersects(Facet f) {
        return intersects(this,f);
    }
     */

    /* Returns true if the two supplied vectors intersect.
     * @param p The supplied facet.
    public boolean intersects(Facet f1, Facet f2) {
        // Get the node locations for the facets:
        NodeVector n1 = f1.getNodes();
        NodeVector n2 = f2.getNodes();
        for (int i=0 ; i<)
    }
     */

    // -------------------- Static Public Methods -------------------
    
    public static NodeVector sharedNodes(Facet f1,Facet f2) {
        // Initialize return object:
        NodeVector nint = new NodeVector();
        // Loop over each node in f1:
        for ( int i=0 ; i<f1.size() ; i++ ) {
            // Get the ith node in f1:
            Node n = f1.getNode(i);
            // Check if that node is also in f2:
            if (f2.containsNode(n)) {
                // Add that node to the list:
                nint.add(n);
            }
        }
        // Return the result:
        return nint;
    }
    
    // -------------------- Private Methods -------------------
    
    /** Calculates the centroid of the facet.
     */
    private MyPoint3D calculateCentroid() {
        // TODO: develop a more robust approach for general planar polygons
        // Check number of nodes:
        int n = size();
        if (n<1) { return null; }
        // The following is an approximation that just finds the average coordinate values.
        // This will hold for a 2D edge or 3D triangle.
        MyPoint3D centroid = MyPoint3D.zero(); // initialization to zero before summation
        for (int i=0 ; i<n ; i++) { // loop over each node
            MyPoint3D p = getNode(i).getPoint3D(); // 3D coordinates of current node
            if (p==null) { return null; }
            centroid.plus(p); // sum of node coordinates
        }
        centroid.divide(n); // average of node coordinates
        return centroid;
    }
    
    /** Calculates a vector normal to the facet.
     * The vector is normalized.
     */
    //private MyPoint3D calculateNormal() {
    private void calculateNormal() {
        // Check number of nodes:
        int n = size();
        if (n<3) {
            normal = null; // normal angle not supported for 2D facets
            return;
        }
        // The normal vector calculation works best if we calculate a plane using vectors that are as close to 90 degrees as possible.
        // Set tolerance on angle between the two vectors:
        double angleTol = Math.PI/18.0; // HARDWIRE: increase to speed up, decrease to improve normal calculation
        double angleBest = Math.PI; // just needs to be initialized to some large value greater than pi/2
        int[] ibest = null;
        boolean ok = false; // set to true once we've found an appropriate set of nodes below
        // Loop over each node:
        for (int i0=0 ; i0<(n-2) ; i0++ ) {
            // Set initial node point:
            MyPoint3D p0 = getNode(i0).getPoint3D();
            // Loop over each node again:
            for (int i1=i0+1 ; i1<(n-1) ; i1++ ) {
                // Set next node point:
                MyPoint3D p1 = getNode(i1).getPoint3D();
                // Calculate vector between first pair of points:
                MyPoint3D v1 = p0.vectorToPoint(p1);
                // Loop over each node a last time:
                for (int i2=i1+1 ; i2<n ; i2++ ) {
                    // Set final node point:
                    MyPoint3D p2 = getNode(i2).getPoint3D();
                    // Calculate vector between second pair of points:
                    MyPoint3D v2 = p0.vectorToPoint(p2);
                    // Check angle between vectors is appropriate:
                    double angle = v1.angleToVector(v2); // angle in radians on [0,pi] (0 to 180 degrees)
                    angle = Math.abs( Math.PI/2.0 - angle ); // angle away from 90 degrees
                    if ( angle <= angleTol ) { // angle between the vectors is close to pi/2 (90 degrees)
                        ibest = new int[]{i0,i1,i2};
                        ok = true;
                        break; // from inner for loop
                    }
                    // Keep track of the best angle so far:
                    if ( angle < angleBest ) {
                        angleBest = angle;
                        ibest = new int[]{i0,i1,i2};
                    }
                } // inner for loop
                // Check if we should exit from middle for loop:
                if (ok) { break; }
            } // middle for loop
            // Check if we should exit from outer for loop:
            if (ok) { break; }
        } // outer for loop
        // Calculate the normal to the plane using the two vectors:
        MyPoint3D p0 = getNode(ibest[0]).getPoint3D();
        MyPoint3D p1 = getNode(ibest[1]).getPoint3D();
        MyPoint3D p2 = getNode(ibest[2]).getPoint3D();
        MyPoint3D v1 = p0.vectorToPoint(p1);
        MyPoint3D v2 = p0.vectorToPoint(p2);
        normal = v1.cross(v2); // vector normal to the plane
        if (normal.norm()==0.0) { // collinear
            normal = null;
        } else {
            normal.normalize();
        }
    }
    
}
