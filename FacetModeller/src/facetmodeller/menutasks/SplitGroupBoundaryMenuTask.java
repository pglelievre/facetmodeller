package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.SplitGroupCommand;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;
import java.awt.Color;

/** Creates a new group of nodes around the boundary of the current group of facets.
 * @author Peter
 */
public final class SplitGroupBoundaryMenuTask extends ControlledMenuTask {
    
    public SplitGroupBoundaryMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Split boundary nodes using facet definitions"; }

    @Override
    public String tip() { return "Creates a new group of nodes around the boundary of the current group of facets."; }

    @Override
    public String title() { return "Split Current Group's Boundary Nodes Using Facet Definitions"; }
    //public String title() { return "New Node Boundary Group From Facet Definitions"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasGroups()) { return false; }
        Group currentGroup = controller.getSelectedCurrentGroup();
        if (currentGroup==null) { return false; }
        return currentGroup.hasFacets();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the facets for the current group:
        Group currentGroup = controller.getSelectedCurrentGroup();
        FacetVector facets = currentGroup.getFacets();
        
        // Find the boundary nodes:
        NodeVector boundaryNodes = facets.findBoundaryNodes();
        
        // Check for an error:
        if (boundaryNodes==null) {
            Dialogs.error(controller,"All facets must be triangular.",title());
            return;
        }
        if (boundaryNodes.size()==0) {
            Dialogs.inform(controller,"No boundary nodes were found.",title());
            return;
        }
        
        // Define a new group:
        String name = currentGroup.getName() + "_NodeBoundary";
        Group newGroup = new Group(name);
        newGroup.setNodeColor(Color.WHITE);
        
        // Get the index of the current group object:
        int ind = controller.getSelectedCurrentGroupIndex();
        
        // Get the currently selected node and facet groups, so we can maintain the selections,
        // and adjust them as required:
        GroupVector selectedNodeGroups = controller.getSelectedNodeGroups();
        GroupVector selectedFacetGroups = controller.getSelectedFacetGroups();
        
        // Split the group, adding the new group object to the list of groups just after the current group:
        SplitGroupCommand com = new SplitGroupCommand(controller,currentGroup,null,newGroup,boundaryNodes,null,ind+1,title());
        com.execute();
        controller.undoVectorAdd(com);
        
        // Add the new group to the group selections:
        selectedNodeGroups.add(newGroup);
        controller.setSelectedCurrentGroupIndex(ind);
        controller.setSelectedNodeGroups(selectedNodeGroups);
        controller.setSelectedFacetGroups(selectedFacetGroups);

        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
    }
    
}
