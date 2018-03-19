package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.sections.Section;
import facetmodeller.sections.SnapshotSection;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

public final class DefineNodeOnEdgeClickTask extends ControlledClickTask {
    
    public DefineNodeOnEdgeClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_ADD_NODES_ON_EDGES; }

    @Override
    public String text() { return ClickTaskUtil.DEFINE_NODE_ON_EDGE_TEXT; }

    @Override
    public String tip() { return ClickTaskUtil.DEFINE_NODE_ON_EDGE_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.DEFINE_NODE_ON_EDGE_TITLE; }

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
        
        // Get the current section and current group:
        Section currentSection = controller.getSelectedCurrentSection();
        Group currentGroup = controller.getSelectedCurrentGroup();
        
        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node node2 = controller.getClosestNode();
        
        // Check if we need to start, continue or stop the procedure:
        Node currentNode = controller.getCurrentNode();
        if (currentNode==null) { // need to start fresh
            controller.setCurrentNode(node2);
        } else { // first node was already selected
            
            // Nullify the temporary object before any dialogs can launch:
            Node node1 = currentNode;
            controller.clearCurrentNode();
            
            // Check if the two nodes define an edge:
            boolean ok = false;
            FacetVector facets1 = node1.getFacets(); // the facets for the first node
            for (int i1=0 ; i1<facets1.size() ; i1++ ) { // loop over each facet for the first node
                Facet f1 = facets1.get(i1);
                // Skip non-triangular facets:
                if ( f1.size() != 3 ) { continue; }
                // Check if the facet contains the second node:
                if ( f1.containsNode(node2) ) {
                    ok = true;
                    break;
                }
            }
            
            // Check that the two nodes define an edge:
            if (!ok) {
                Dialogs.error(controller,"Those nodes don't define an edge on a triangular facet.",title());
                return;
            }
            
            // Check for a SnapshotSection:
            boolean isSnapshot = (currentSection instanceof SnapshotSection);
            
            // Check if the nodes are both on-section:
            boolean onSection = ( !node1.isOff() && !node2.isOff() );

            // Check if the nodes are both in the same section:
            Section section = node1.getSection();
            boolean sameSection = ( section == node2.getSection() );
            if (!sameSection) { section = currentSection; }
        
            // Check that nodes can be added to the section:
            if (section==null) { return; } // shouldn't happen but avoids a compiler warning
            if ( !isSnapshot && !section.canAddNodesOnSection() ) {
                Dialogs.error(controller,"Nodes can not be added to the section.",title());
                return;
            }

            // Check if the nodes are both in the same group:
            Group group = node1.getGroup();
            if ( group != node2.getGroup() ) { group = currentGroup; }
            
            // Create a new node at the edge midpoint and in the appropriate section and group:
            Node newNode;
            if (!isSnapshot && onSection && sameSection) { // create an on-section node on the same section
                // Calculate the 2D coordinates for the new node:
                MyPoint2D pc = node1.getPoint2D().deepCopy();
                pc.plus( node2.getPoint2D() );
                pc.divide(2.0);
                // Create a new on-section node:
                newNode = new NodeOnSection(pc,section,group);
            } else { // create an off-section node
                // Warn user:
                if (!isSnapshot && controller.getShowConfirmationDialogs()) {
                    String message = "WARNING! That node must be added as an off-section node. Do you want to continue?";
                    int response = Dialogs.yesno(controller,message,title());
                    if ( response != Dialogs.YES_OPTION ) {
                        controller.clearCurrentFacet();
                        return;
                    }
                }
                // Calculate the 3D coordinates for the new node:
                MyPoint3D pc = node1.getPoint3D().deepCopy();
                pc.plus( node2.getPoint3D() );
                pc.divide(2.0);
                // Create a new off-section node:
                newNode = new NodeOffSection(pc,section,group);
            }
            
            // Add the node to the PLC:
            AddNodeCommand com = new AddNodeCommand(controller.getModelManager(),newNode,title()); com.execute();
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
