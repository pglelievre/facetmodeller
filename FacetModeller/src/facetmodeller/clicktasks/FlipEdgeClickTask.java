package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.FlipEdgeCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import geometry.MyPoint2D;

public final class FlipEdgeClickTask extends ControlledClickTask {
    
    public FlipEdgeClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_EDGE_FLIP; }

    @Override
    public String text() { return ClickTaskUtil.FLIP_EDGE_TEXT; }

    @Override
    public String tip() { return ClickTaskUtil.FLIP_EDGE_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.FLIP_EDGE_TITLE; }

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
        
        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node node2 = controller.getClosestNode();
        
        // Check if we need to start, continue or stop the edge flipping operation:
        Node currentNode = controller.getCurrentNode();
        if (currentNode==null) { // need to start fresh
            controller.setCurrentNode(node2);
        } else { // first node was already selected
            
            // Find the triangular facets containing the edge (triangular facets that contain the two selected nodes):
            FacetVector edgeFacets = new FacetVector(); // will hold the triangular facets containing the edge
            NodeVector nodes = new NodeVector(); // will hold the 4 nodes
            nodes.add(currentNode);
            nodes.add(node2);
            FacetVector facets1 = currentNode.getFacets(); // the facets for the first node
            for (int i1=0 ; i1<facets1.size() ; i1++ ) { // loop over each facet for the first node
                Facet f1 = facets1.get(i1);
                // Skip non-triangular facets:
                if ( f1.size() != 3 ) { continue; }
                // Check if the facet contains the second node:
                if ( f1.containsNode(node2) ) {
                    // Add the facet to the list:
                    edgeFacets.add(f1);
                }
            }
            
            // Nullify the temporary object before any dialogs can launch:
            controller.clearCurrentNode();
            
            // Check that the two nodes define an edge:
            if (edgeFacets.isEmpty()) {
                Dialogs.error(controller,"Those nodes don't define an edge.",title());
                return;
            }
            
            // Check for a boundary edge:
            if ( edgeFacets.size() == 1 ) {
                Dialogs.error(controller,"Boundary edges can not be flipped.",title());
                return;
            }
            
            // Check for a triple point:
            if ( edgeFacets.size() != 2 ) {
                Dialogs.error(controller,"Triple point edges can not be flipped.",title());
                return;
            }
            
            // TODO: check for a convex situation
            
            // Determine the two other nodes to use:
            nodes.addAll( edgeFacets.get(0).getNodes() ); // duplicates are not added in this call
            nodes.addAll( edgeFacets.get(1).getNodes() ); // duplicates are not added in this call
            
            // Check I did it correctly:
            if ( nodes.size() != 4 ) {
                Dialogs.error(controller,"That edge can not be flipped.",title());
                return;
            }
            
            // Figure out which group to use:
            Group group = edgeFacets.get(0).getGroup();
            if ( edgeFacets.get(1).getGroup() != group ) { // the two facets belong to different groups
                group = controller.getSelectedCurrentGroup();
            }
            
            // Define two new facets:
            Facet newFacet1 = new Facet(group);
            newFacet1.addNode(nodes.get(0));
            newFacet1.addNode(nodes.get(2));
            newFacet1.addNode(nodes.get(3));
            Facet newFacet2 = new Facet(group);
            newFacet2.addNode(nodes.get(1));
            newFacet2.addNode(nodes.get(2));
            newFacet2.addNode(nodes.get(3));
            FacetVector newFacets = new FacetVector();
            newFacets.add(newFacet1);
            newFacets.add(newFacet2);
            
            // Perform the edge flip (add the new facets, delete the existing facets):
            FlipEdgeCommand com = new FlipEdgeCommand(controller.getModelManager(),edgeFacets,newFacets); com.execute();
            controller.undoVectorAdd(com);
            
        }

        // Repaint:
        controller.redraw();
        
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest node to the cursor position:
        controller.calculateClosestNode(p);
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest node:
        controller.updateClosestBar(p);
    }
    
}