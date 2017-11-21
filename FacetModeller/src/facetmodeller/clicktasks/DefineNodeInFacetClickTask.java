package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.sections.Section;
import facetmodeller.sections.SnapshotSection;
import geometry.Bary2D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

public final class DefineNodeInFacetClickTask extends ControlledClickTask {
    
    public DefineNodeInFacetClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_ADD_NODES_IN_FACETS; }

    @Override
    public String tip() { return ClickTaskUtil.DEFINE_NODE_IN_FACET_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.DEFINE_NODE_IN_FACET_TITLE; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return ( controller.hasNodes() && controller.hasFacets() );
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        
        // Check if we need to start, continue or stop the operation:
        if (!controller.checkCurrentFacet()) { // need to start fresh
            
            // Calculate the closest facet to the clicked point:
            if (!controller.calculateClosestFacet(p)) { return; }
            Facet closestFacet = controller.getClosestFacet();
            
            // Check if the facet is triangular:
            if ( closestFacet.size() == 3 ) { // store it
                controller.setCurrentFacet(closestFacet);
            } else { // clear it
                controller.clearCurrentFacet();
                Dialogs.error(controller,"The facet must be a triangle.",title());
                return;
            }
            
        } else { // facet was already selected
            
            // Calculate the candidate node on the plane of the facet:
            Node newNode = calculateCandidateNode(p,true);
            
            // Check for null newNode:
            if (newNode==null) { return; }
            
            // Add the node to the PLC:
            AddNodeCommand com = new AddNodeCommand(controller.getModelManager(),newNode,title()); com.execute();
            controller.undoVectorAdd(com);
            
            // Clear temporary overlays:
            controller.clearAllTemporaryOverlays();
            
        }

        // Redraw:
        controller.redraw();
        
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Check if we need to start, continue or stop the operation:
        if (!controller.checkCurrentFacet()) { // need to start fresh
            // Clear temporary overlays:
            controller.clearClosestTemporaryOverlays();
            // Calculate the closest facet to the cursor position:
            controller.calculateClosestFacet(p);
            // Redraw:
            controller.redraw();
            // Use the cursor bar to show information for the closest facet:
            controller.updateClosestBar(p);
        } else {
            // Calculate the candidate node on the plane of the facet:
            Node newNode = calculateCandidateNode(p,false);
            // Set the temporary candidate node overlay:
            controller.setCandidateNode(newNode);
            // Redraw:
            controller.redraw();
        }
    }

    public Node calculateCandidateNode(MyPoint2D p, boolean verbose) {

        // Get the current section and current group:
        Section currentSection = controller.getSelectedCurrentSection();
        Group currentGroup = controller.getSelectedCurrentGroup();

        // Get the three nodes for the facet:
        Facet currentFacet = controller.getCurrentFacet();
        Node n0 = currentFacet.getNode(0);
        Node n1 = currentFacet.getNode(1);
        Node n2 = currentFacet.getNode(2);

        // Get various information about the nodes:
        Section s0 = n0.getSection();
        Section s1 = n1.getSection();
        Section s2 = n2.getSection();
        Boolean e0 = s0.equals(currentSection);
        Boolean e1 = s1.equals(currentSection);
        Boolean e2 = s2.equals(currentSection);

        // Get the 3D coordinates of the nodes:
        MyPoint3D p0 = n0.getPoint3D();
        MyPoint3D p1 = n1.getPoint3D();
        MyPoint3D p2 = n2.getPoint3D();

        // Project onto current section:
        MyPoint2D q0 = currentSection.projectOnto(p0); // image pixel coordinates
        MyPoint2D q1 = currentSection.projectOnto(p1); // image pixel coordinates
        MyPoint2D q2 = currentSection.projectOnto(p2); // image pixel coordinates

        // Shift projected points if required so that all points are as plotted:
        // TODO: perhaps there is an easier way to extract information from what is saved by the 2D viewing panel.
        double sx = controller.getShiftingX();
        double sy = controller.getShiftingY();
        if (!e0) { q0.plus(sx,sy); }
        if (!e1) { q1.plus(sx,sy); }
        if (!e2) { q2.plus(sx,sy); }

        // Calculate the baricentric coordinates in the 2D projection with shifted points:
        Bary2D bary = new Bary2D(q0,q1,q2);
        bary.calculate(p);

        // Check if the pixel is inside the projected triangle:
        if (!bary.inOrOn(0.1)) { // 10% tolerance
            if (verbose) {
                controller.clearCurrentFacet();
                Dialogs.error(controller,"The node must be added inside the facet.",title());
            }
            return null;
        }

        // Check for a SnapshotSection:
        boolean isSnapshot = (currentSection instanceof SnapshotSection);

        // Check if the nodes are all on-section:
        boolean onSection = true;
        for (int i=0 ; i<3 ; i++ ) {
            if (currentFacet.getNode(i).isOff()) {
                onSection = false;
                break;
            }
        }

        // Check if the nodes are all in the same section:
        boolean sameSection;
        Section section;
        if ( currentFacet.getSections().size() == 1 ) { // all in the same section so add to that section
            sameSection = true;
            section = currentFacet.getSections().get(0);
        } else { // not all in the same section so add to the currentSection
            sameSection = false;
            section = currentSection;
        }

        // Check that nodes can be added to the section:
        if ( !isSnapshot && !section.canAddNodesOnSection() ) {
            if (verbose) {
                controller.clearCurrentFacet();
                Dialogs.error(controller,"Nodes can not be added to the section.",title());
            }
            return null;
        }

        // Check if the nodes are all in the same group:
        boolean sameGroup = true;
        Group group = currentFacet.getNode(0).getGroup();
        for (int i=1 ; i<3 ; i++ ) {
            if ( currentFacet.getNode(i).getGroup() != group ) {
                sameGroup = false;
                break;
            }
        }
        if (!sameGroup) { group = currentGroup; } // not all in the same group so add to the currentGroup

        // Create a new node at the cursor position and in the appropriate section and group:
        Node newNode;
        if (!isSnapshot && onSection && sameSection) { // create an on-section node on the same section
            // Add new 2D point to the section:
            MyPoint2D newPoint = p.deepCopy();
            newNode = new NodeOnSection(newPoint,section,group);
        } else { // create an off-section node
            // Warn user:
            if (!isSnapshot) {
                if (verbose) {
                    String message = "WARNING! That node must be added as an off-section node. Do you want to continue?";
                    int response = Dialogs.yesno(controller,message,title());
                    if ( response != Dialogs.YES_OPTION ) {
                        controller.clearCurrentFacet();
                        return null;
                    }
                }
            }
            // Interpolate the new 3D point using the unshifted points:
            // (the barycentric coordinates from above are used
            //  but the interpolation uses unshifted points;
            //  this effectively removes the shifting effect)
            double x = bary.interpolate(p0.getX(),p1.getX(),p2.getX());
            double y = bary.interpolate(p0.getY(),p1.getY(),p2.getY());
            double z = bary.interpolate(p0.getZ(),p1.getZ(),p2.getZ());
            MyPoint3D newPoint = new MyPoint3D(x,y,z);
            // Create a new off-section node:
            newNode = new NodeOffSection(newPoint,section,group);
        }
        return newNode;

    }
    
}
