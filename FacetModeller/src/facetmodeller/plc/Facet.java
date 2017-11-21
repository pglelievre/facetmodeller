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

    private final NodeVector nodes = new NodeVector();
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

    public NodeVector getNodes() { return nodes; }
    public SectionVector getSections() {
//        return sections;
        return nodes.getSections();
    }
    public Node getNode(int i) { return nodes.get(i); }
//    public Section getSection(int i) { return sections.get(i); }

    public Color getColor() { return getGroup().getFacetColor(); }

    // -------------------- Public Methods -------------------

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
        if (!nodes.contains(n)) { nodes.add(n); }
    }

    public void addNodes(NodeVector n) {
        nodes.addAll(n);
    }

//    public void addSection(Section s) {
//        // Check the section isn't already in the list:
//        if (!sections.contains(s)) { sections.add(s); }
//    }

    public void clear() {
        nodes.clear();
    }

    public void removeNode(Node n) {
        nodes.remove(n);
    }

    public void removeLastNode() {
        nodes.removeLast();
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
    
    /** Calculates a vector normal to the facet.
     * The vector is normalized.
     * @return 
     */
    public MyPoint3D normal() {
        // Check number of nodes:
        int n = size();
        if (n<3) { return null; } // not supported for 2D facets
        MyPoint3D p0 = getNode(0).getPoint3D();
        MyPoint3D p1 = getNode(1).getPoint3D();
        MyPoint3D v1 = p0.vectorToPoint(p1);
        // Loop until we find three non-collinear nodes:
        for (int i=2 ; i<n ; i++) {
            MyPoint3D p2 = getNode(i).getPoint3D();
            MyPoint3D v2 = p0.vectorToPoint(p2);
            MyPoint3D nv = v1.cross(v2);
            if (nv.norm()!=0.0) { // not collinear
                nv.normalize();
                return nv;
            }
        }
        return null;
    }
    
    /** Reverses the node order in the facet. */
    public void reverse() {
        nodes.reverse();
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
    
}
