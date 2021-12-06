package facetmodeller.plc;

import fileio.SessionIO;
import facetmodeller.groups.Group;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Color;

/** A node attached to facets, a section and group.
 * @author Peter Lelievre
 */
public abstract class Node extends HasSection implements SessionIO {

    // -------------------- Properties -------------------
    
    public static final int NODE_ON_SECTION=1; // node coordinates are pixel coordinates for the related image
    public static final int NODE_OFF_SECTION=2; // node coordinates are spatial coordinates???

    private boolean boundaryMarker = false;
    private final FacetVector facets = new FacetVector(); // list of facets associated with the vertex

    // -------------------- Constructors -------------------

    public Node() { super(); } // required by the SessionLoader (should not be used elsewhere)

    public Node(Section s, Group g) {
        super(s,g);
    }

    // -------------------- Copy -------------------

    public abstract Node deepCopyPointAndGroup(); // copy only the point and group information, not facet or section membership.
    
    // -------------------- Checkers -------------------
    
    public abstract int getType(); // returns one of the section types above
    public abstract boolean isOff();
    
    // -------------------- Getters -------------------

    public abstract MyPoint2D getPoint2D();
    public abstract MyPoint3D getPoint3D();
    
    public boolean getBoundaryMarker() { return boundaryMarker; }
    public FacetVector getFacets() { return facets; }
    public Facet getFacet(int i) { return facets.get(i); }
    public Color getColor() { return getGroup().getNodeColor(); }
    
    // -------------------- Setters -------------------

    public void setBoundaryMarker(boolean bm) { boundaryMarker = bm; }
    public abstract void setPoint2D(MyPoint2D p);
    public abstract void setPoint3D(MyPoint3D p);
    
    // -------------------- Public Methods -------------------
    
    public void toggleBoundaryMarker() { boundaryMarker = !boundaryMarker; }

    /** Returns the node neighbours.
     * Will only work with a 3D model and triangular facets.
     * Returns null if any non-triangular facets are encountered.
     * @return 
     */
    public NodeVector getNodeNeighbours() {
        NodeVector neighbours = new NodeVector();
        // Loop over each facet that contains the node:
        for (int i=0 ; i<facets.size() ; i++) {
            Facet facet = facets.get(i);
            // Check for triangular facet:
            NodeVector facetNodes = facet.getNodes();
            int n = facetNodes.size();
            if (n!=3) { return null; } // non-triangular facet
            // Loop over each node in the ith facet:
            for (int j=0 ; j<n ; j++) {
                Node facetNode = facetNodes.get(i);
                // Check that the jth facet node isn't this node and isn't already in the list of neighbours:
                if ( !facetNode.equals(this) ) { // && !neighbours.contains(facetNode) )
                    // Add the jth facet node to the list of neighbours:
                    neighbours.add(facetNode); // node is not added if already in the list
                }
            }
        }
        // Return successfully:
        return neighbours;
    }

    // -------------------- Wrappers for FacetVector Methods -------------------

    public void addFacet(Facet f) {
        facets.add(f); // checks for duplicates
    }
    public void addFacets(FacetVector f) {
        facets.addAll(f); // checks for duplicates
    }
    public void removeFacet(Facet f) {
        facets.remove(f);
    }
    public void clearFacets() {
        facets.clear();
    }
    
}