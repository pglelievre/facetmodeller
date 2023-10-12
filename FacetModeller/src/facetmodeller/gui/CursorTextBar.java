package facetmodeller.gui;

import facetmodeller.FacetModeller;
import facetmodeller.Synthesizer;
import facetmodeller.plc.Facet;
import facetmodeller.plc.Node;
import facetmodeller.plc.Region;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import gui.TextBar;
//import java.awt.Dimension;

/** The TextBar object that provides information about the cursor location and other information.
 * @author Peter
 */
public final class CursorTextBar extends TextBar {
    private static final long serialVersionUID = 1L;
    
    private final FacetModeller controller;
    
    public CursorTextBar(FacetModeller con) {
        controller = con;
        //this.setPreferredSize(new Dimension(100,50));
    }
    
    /** Updates the cursor bar by writing the current cursor position.
     * @param p2 */
    public void updateCursor(MyPoint2D p2) {
        // Clear the cursor bar:
        setText(" ");
        // Get the current section:
        Section section = controller.getSelectedCurrentSection();
        // Check for no point or no sections or no current section:
        if (p2==null || section==null) { return; }
        // Check if the current section has been calibrated:
        String s;
        if ( section.isCalibrated() ) {
            MyPoint3D p3 = section.imageToSpace(p2);
            if (p3==null) {
                s = "Uncalibrated = " + p2.toStringParentheses();
            } else {
                s = "Calibrated = " + p3.toStringParentheses();
                // Add the along-profile coordinates if required:
                //if (ndim==2) {
                //    s += " ; r = " + p3.normXY();
                //}
            }
        } else {
            s = "Uncalibrated = " + p2.toStringParentheses();
        }
        // Display the text:
        setText(s);
    }

    /** Updates the cursor bar by writing the minimum angle in a candidate facet . */
    public void updateMinAngle() {
        // Clear the cursor bar:
        setText(" ");
        // Check for no candidate facet:
        Facet currentFacet = controller.getCurrentFacet();
        if (currentFacet==null) { return; }
        // Calculate the minimum angle in the currentFacet:
        Double a = currentFacet.minAngle(); // angle in radians
        String s = "Minimum vertex angle = " + Math.toDegrees(a); // angle in degrees
        // Display the text:
        setText(s);
    }

    /** Updates the cursor bar by writing information about the closest node or facet.
     * @param p */
    public void updateClosest(MyPoint2D p) {

        // Clear the cursor bar:
        setText(" ");

        // Check for no sections loaded:
        if (!controller.hasSections()) { return; }
        Section section = controller.getSelectedCurrentSection();

        // Check for no point or no current section:
        if (p==null || section==null) { return; }
        
        // Figure out which is closest:
        String s;
        int whichIsIt = controller.whichTemporaryOverlayIsClosest(p);
        switch (whichIsIt) {
            case Synthesizer.NODE_IS_CLOSEST:
                {
                    Node closestNode = controller.getClosestNode();
                    int ind = controller.indexOfNode(closestNode) + 1;
                    if (ind<=0) { return; }
                    String groupName = closestNode.getGroup().getName();
                    String sectionName = closestNode.getSection().shortName();
                    String soff;
                    if (closestNode.isOff()) {
                        soff = "off ";
                    } else {
                        soff = "on ";
                    }
                    String sbm;
                    if (closestNode.getBoundaryMarker()) {
                        sbm = "bm=1; ";
                    } else {
                        sbm = "bm=0; ";
                    }
                    s = "node #" + ind + "; " + sbm + groupName + "; " + soff + sectionName + "; " + closestNode.getFacets().size() + " facets; "
                            + " (" + closestNode.getPoint3D().toStringCSV() + ")";
                            //+ System.lineSeparator() + "coordinates = (" + closestNode.getPoint3D().toStringCSV() + ")";
                    controller.clearClosestFacet();
                    controller.clearClosestRegion();
                    break;
                }
            case Synthesizer.FACET_IS_CLOSEST:
                {
                    Facet closestFacet = controller.getClosestFacet();
                    int ind = controller.indexOfFacet(closestFacet) + 1;
                    if (ind<=0) { return; }
                    String groupName = closestFacet.getGroup().getName();
                    int n = closestFacet.size();
                    String sbm;
                    if (closestFacet.getBoundaryMarker()) {
                        sbm = "bm=1; ";
                    } else {
                        sbm = "bm=0; ";
                    }
                    if (n>3) {
                        s = "facet #" + ind + "; " + sbm + groupName + "; " + n + " nodes";
                        //s = "facet #" + ind + "; " + name + "; " + System.lineSeparator() + n + " nodes";
                    } else {
                        s = "facet #" + ind + "; " + sbm + groupName + "; " + "nodes =";
                        //s = "facet #" + ind + "; " + name + ";" + System.lineSeparator() + "nodes =";
                        for (int i=0 ; i<n ; i++ ) {
                            Node node = closestFacet.getNode(i);
                            ind = controller.indexOfNode(node) + 1;
                            s += " " + ind;
                        }
                    }
                    controller.clearClosestNode();
                    controller.clearClosestRegion();
                    break;
                }
            case Synthesizer.REGION_IS_CLOSEST:
                {
                    Region closestRegion = controller.getClosestRegion();
                    int ind = controller.indexOfRegion(closestRegion) + 1;
                    if (ind<=0) { return; }
                    String groupName = closestRegion.getGroup().getName();
                    if (closestRegion.getIsControl()) {
                        s = "region #" + ind + " (control point)" + "; " + groupName;
                    } else {
                        s = "region #" + ind + " (region point)" + "; " + groupName;
                    }       s = s + "; (" + closestRegion.getPoint3D().toStringCSV() + ")";
                    controller.clearClosestNode();
                    controller.clearClosestFacet();
                    break;
                }
            default:
                return;
        }

        // Display the text:
        setText(s);

    }
    
}
