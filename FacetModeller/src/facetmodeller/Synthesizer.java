package facetmodeller;

import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPoint3D;
import java.util.ArrayList;

/** Manages tasks required to synthesize new model items based on user interaction with the model views.
 * @author Peter
 */
public final class Synthesizer {
    
    public static final int UNKNOWN_IS_CLOSEST = 0;
    public static final int NODE_IS_CLOSEST = 1;
    public static final int FACET_IS_CLOSEST = 2;
    public static final int REGION_IS_CLOSEST = 3;
    
    private final FacetModeller controller;
    
    private boolean lockInfo = false; // used in info click mode
    private Node candidateNode = null; // The candidate node, e.g. being created on a facet.
    private Node currentNode = null; // The current node being moved or otherwise worked with.
    private Facet currentFacet = null; // The current facet being defined or otherwise worked with.
    private Node closestNode = null; // The closest node to the cursor.
    private MyPoint2D closestNodePoint = null; // The closest painted node point to the cursor.
    private Facet closestFacet = null; // The facet with the closest centroid to the cursor.
    private MyPoint2D closestFacetCentroid = null; // The closest painted facet centroid to the cursor.
    private Region closestRegion = null; // The closest region point to the cursor.
    private MyPoint2D closestRegionPoint = null; // The closest painted region point to the cursor.

    public Synthesizer(FacetModeller con) { controller = con; }
    
    public boolean isLocked() { return lockInfo; }
    public Node getCandidateNode() { return candidateNode; }
    public Node getCurrentNode() { return currentNode; }
    public Facet getCurrentFacet() { return currentFacet; }
    public Node getClosestNode() { return closestNode; }
    public MyPoint2D getClosestNodePoint() { return closestNodePoint; }
    public Facet getClosestFacet() { return closestFacet; }
    public Region getClosestRegion() { return closestRegion; }
    public MyPoint2D getClosestRegionPoint() { return closestRegionPoint; }
    
    public void unlock() { lockInfo = false; }
    public void toggleLock() { lockInfo = !lockInfo; }
    public void setCandidateNode(Node node) { candidateNode = node; }
    public void setCurrentNode(Node node) { currentNode = node; }
    public void setCurrentFacet(Facet facet) { currentFacet = facet; }
    
    private void clearCandidateNode() { candidateNode=null; }
    public void clearCurrentNode() { currentNode=null; }
    public void clearCurrentFacet() { currentFacet=null; }
    public void clearClosestNode() { closestNode=null; closestNodePoint=null; }
    public void clearClosestFacet() { closestFacet=null; closestFacetCentroid=null; }
    public void clearClosestRegion() { closestRegion=null; closestRegionPoint=null; }
    public void clearCurrent() {
        clearCurrentNode();
        clearCurrentFacet();
    }
    public void clearClosest() {
        clearClosestNode();
        clearClosestFacet();
        clearClosestRegion();
    }
    public void clearAll() {
        clearCandidateNode();
        clearClosest();
        clearCurrent();
    }
    
    public boolean checkCurrentNode() { return (currentNode!=null); }
    public boolean checkCurrentFacet() { return (currentFacet!=null); }
//    public boolean checkClosestNode() {
//        return ( closestNode!=null && closestNodePoint!=null );
//    }
    
    public boolean calculateClosest(MyPoint2D p) {
        boolean b1 = calculateClosestNode(p);
        boolean b2 = calculateClosestFacet(p);
        boolean b3 = calculateClosestRegion(p);
        return ( b1 || b2 || b3 ); // returns true if there is any new object e.g. to paint
    }
    
    /** Determines the closest node to the input point (e.g. the cursor position)
     * and sets the closestNode object to that node.
     * @param p
     * @return  */
    public boolean calculateClosestNode(MyPoint2D p) {

        // Set the closestNode object to null:
        closestNode = null;
        closestNodePoint = null;

        // Get the current section:
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }

        // Check nodes have been painted:
        NodeVector nodes = controller.getPaintedNodes();
        if (nodes.isEmpty()) { return false; }

        // Find the closest painted node point to the point p:
        MyPoint2DVector points = controller.getPaintedNodePoints();
        int ic = points.findClosest(p);
        if (ic<0) { return false; }
        MyPoint2D point = points.get(ic);
        
        // Convert points from image pixel coordinates to spatial coordinates:
        MyPoint3D p3 = currentSection.imageToSpace(p);
        if (p3==null) { return false; }
        MyPoint3D point3 = currentSection.imageToSpace(point);
        if (point3==null) { return false; }

        // Check the distance is small enough:
        Double d = point3.distanceToPoint(p3);
        if (d>controller.getPickingDistance()) { return false; }

        // Get the closest painted node:
        closestNode = nodes.get(ic);
        closestNodePoint = points.get(ic);
        
        // Return successfully:
        return true;

    }

    /** Determines the closest facet centroid to the input point (e.g. the cursor position)
     * and sets the closestFacet object to that facet and the closestCentroid to the corresponding centroid point.
     * @param p
     * @return  */
    public boolean calculateClosestFacet(MyPoint2D p) {

        // Set the closestFacet object to null:
        closestFacet = null;
        closestFacetCentroid = null;

        // Get the current section:
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }

        // Check facet centroids have been painted:
        FacetVector facets = controller.getPaintedFacets();
        if (facets.isEmpty()) { return false; }

        // Find the closest painted facet centroid to the point p:
        MyPoint2DVector points = controller.getPaintedFacetCentroids();
        int ic = points.findClosest(p);
        if (ic<0) { return false; }
        MyPoint2D point = points.get(ic);
        
        // Convert points from image pixel coordinates to spatial coordinates:
        MyPoint3D p3 = currentSection.imageToSpace(p);
        if (p3==null) { return false; }
        MyPoint3D point3 = currentSection.imageToSpace(point);
        if (point3==null) { return false; }

        // Check the distance is small enough:
        Double d = point3.distanceToPoint(p3);
        if (d>controller.getPickingDistance()) { return false; }

        // Get the closest painted facet:
        closestFacet = facets.get(ic);
        closestFacetCentroid = points.get(ic);
        
        // Return successfully:
        return true;

    }

    /** Determines the closest region point to the input point (e.g. the cursor position)
     * and sets the closestRegion object to that region.
     * @param p
     * @return  */
    public boolean calculateClosestRegion(MyPoint2D p) {

        // Set the closestRegion object to null:
        closestRegion = null;
        closestRegionPoint = null;

        // Get the current section:
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }

        // Check regions exist for the current section:
        //RegionVector regions = getCurrentSection().getRegions(); // the regions for the current section
        //if (regions.size()==0) { return; }

        // Check regions have been painted:
        RegionVector regions = controller.getPaintedRegions();
        if (regions.isEmpty()) { return false; }

        // Find the closest painted region point to the point p:
        MyPoint2DVector points = controller.getPaintedRegionPoints();
        int ic = points.findClosest(p);
        if (ic<0) { return false; }
        MyPoint2D point = points.get(ic);
        
        // Convert points from image pixel coordinates to spatial coordinates:
        MyPoint3D p3 = currentSection.imageToSpace(p);
        if (p3==null) { return false; }
        MyPoint3D point3 = currentSection.imageToSpace(point);
        if (point3==null) { return false; }

        // Check the distance is small enough:
        Double d = point3.distanceToPoint(p3);
        if (d>controller.getPickingDistance()) { return false; }

        // Get the closest painted region:
        closestRegion = regions.get(ic);
        closestRegionPoint = points.get(ic);
        
        // Return successfully:
        return true;

    }

    /** Calculates a triangular facet from the three closest painted nodes.
     * @param p
     * @return  */
    public Facet calculateTriFacet(MyPoint2D p) {

        // Set the currentFacet object to null:
        currentFacet = null;

        // Check for no sections or groups:
        if (!controller.hasSections()) { return null; }
        if (!controller.hasGroups()) { return null; }

        // Check at least three nodes have been painted:
        NodeVector nodes = controller.getPaintedNodes();
        if (nodes.size()<3) { return null; }

        // Find the three closest painted node points to the selected point p:
        MyPoint2DVector points = controller.getPaintedNodePoints();
        ArrayList<Integer> ibest = points.findClosest(p,3);
        if (ibest==null) { return null; }
        
        // Calculate a length scale based on the points found above:
        double d = 0;
        for (int i=0 ; i<3 ; i++ ) {
            int ii = ibest.get(i);
            MyPoint2D pi = points.get(ii);
            double di = p.distanceToPoint(pi);
            d = Math.max(d,di);
        }
        d *= controller.getAutoFacetFactor();
        
        // Find all points within that length scale distance:
        ibest = points.findClose(p,d);
        
        // Loop over each possible facet (each possible combination of 3 points found above):
        int n = ibest.size();
        int[] inode = new int[3];
        double dbest = Double.MAX_VALUE;
        for (int i0=0    ; i0<n-2 ; i0++ ) {
        for (int i1=i0+1 ; i1<n-1 ; i1++ ) {
        for (int i2=i1+1 ; i2<n   ; i2++ ) {
            // Calculate the centroid of the current possible facet:
            MyPoint2DVector pv = new MyPoint2DVector();
            int ii0 = ibest.get(i0);
            int ii1 = ibest.get(i1);
            int ii2 = ibest.get(i2);
            pv.add(points.get(ii0));
            pv.add(points.get(ii1));
            pv.add(points.get(ii2));
            MyPoint2D centroid = pv.centroid();
            // Calculate the distance to that centroid:
            d = p.distanceToPoint(centroid);
            // Record the closest centroid:
            if ( d < dbest ) {
                inode[0] = ii0;
                inode[1] = ii1;
                inode[2] = ii2;
                dbest = d;
            }
        } // for i2
        } // for i1
        } // for i0
        
        // Create a new facet object:
        currentFacet = new Facet(); // NOT LINKED TO A GROUP YET!
        
        // Loop over each of the best three points:
        for (int i=0 ; i<3 ; i++ ) {
            // Get the ith closest painted node:
            Node node = nodes.get(inode[i]);
//            // Get the section for the ith node so it can be added to the facet:
//            Section section = node.getSection();
            // Add the node to the current facet:
            currentFacet.addNode(node);
//            // Add the relevant section to the current facet: // no longer necessary
//            currentFacet.addSection(section);
        }
        
        // Return the facet:
        return currentFacet;

    }

    /** Calculates a linear edge element facet from the two closest painted nodes.
     * @param p
     * @return  */
    public Facet calculateLineFacet(MyPoint2D p) {

        // Set the currentFacet object to null:
        currentFacet = null;

        // Check for no sections or groups:
        if (!controller.hasSections()) { return null; }
        if (!controller.hasGroups()) { return null; }

        // Check at least two nodes have been painted:
        NodeVector nodes = controller.getPaintedNodes();
        if (nodes.size()<2) { return null; }

        // Find the two closest painted node points to the selected point p:
        MyPoint2DVector points = controller.getPaintedNodePoints();
        ArrayList<Integer> ibest = points.findClosest(p,2);
        if (ibest==null) { return null; }
        
        // Calculate a length scale based on the points found above:
        double d = 0;
        for (int i=0 ; i<2 ; i++ ) {
            int ii = ibest.get(i);
            MyPoint2D pi = points.get(ii);
            double di = p.distanceToPoint(pi);
            d = Math.max(d,di);
        }
        d *= controller.getAutoFacetFactor();
        
        // Find all points within that length scale distance:
        ibest = points.findClose(p,d);
        
        // Loop over each possible facet (each possible combination of 2 points found above):
        int n = ibest.size();
        int[] inode = new int[2];
        double dbest = Double.MAX_VALUE;
        for (int i0=0    ; i0<n-1 ; i0++ ) {
        for (int i1=i0+1 ; i1<n   ; i1++ ) {
            // Calculate the centroid of the current possible facet:
            MyPoint2DVector pv = new MyPoint2DVector();
            int ii0 = ibest.get(i0);
            int ii1 = ibest.get(i1);
            pv.add(points.get(ii0));
            pv.add(points.get(ii1));
            MyPoint2D centroid = pv.centroid();
            // Calculate the distance to that centroid:
            d = p.distanceToPoint(centroid);
            // Record the closest centroid:
            if ( d < dbest ) {
                inode[0] = ii0;
                inode[1] = ii1;
                dbest = d;
            }
        } // for i1
        } // for i0
        
        // Create a new facet object:
        currentFacet = new Facet(); // NOT LINKED TO A GROUP YET!

        // Loop over each of the best two points:
        for (int i=0 ; i<2 ; i++ ) {
            // Get the ith closest painted node:
            Node node = nodes.get(inode[i]);
//            // Get the section for the ith node so it can be added to the facet:
//            Section section = node.getSection();
            // Add the node to the current facet:
            currentFacet.addNode(node);
//            // Add the relevant section to the current facet: // no longer necessary
//            currentFacet.addSection(section);
        }
        
        // Return the facet:
        return currentFacet;
        
    }
    
    public int whichIsClosest(MyPoint2D p) {
        
        int whichIsIt = UNKNOWN_IS_CLOSEST;
        
        // Get the distance to the closest node:
        double d=Double.MAX_VALUE;
        if (closestNodePoint!=null) {
            d = closestNodePoint.distanceToPoint(p);
            whichIsIt = NODE_IS_CLOSEST;
        }
        
        // Get the distance to the closest facet:
        if (closestFacetCentroid!=null) {
            double df = closestFacetCentroid.distanceToPoint(p);
            if (whichIsIt==0) {
                whichIsIt = FACET_IS_CLOSEST;
            } else {
                // Check if closer than the node:
                if (df<d) {
                    whichIsIt = FACET_IS_CLOSEST;
                    d = df;
                }
            }
        }
        
        // Get the distance to the closest region:
        if (closestRegionPoint!=null) {
            double dr = closestRegionPoint.distanceToPoint(p);
            if (whichIsIt==0) {
                whichIsIt = REGION_IS_CLOSEST;
            } else {
                // Check if closer than the node and/or facet:
                if (dr<d) {
                    whichIsIt = REGION_IS_CLOSEST;
                    //d = dr;
                }
            }
        }
        
        // Return the integer:
        return whichIsIt;
        
    }
    
}
