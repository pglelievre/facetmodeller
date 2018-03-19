package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;

public final class ReverseGroupOrderMenuTask extends ControlledMenuTask {
    
    public ReverseGroupOrderMenuTask(FacetModeller con) {
        super(con);
    }
    
    @Override
    public String text() { return "Reverse group order"; }

    @Override
    public String tip() { return "Reverses the order of the groups in the group selection boxes."; }
    
    @Override
    public String title() { return "Reverse Group Order"; }
    
    @Override
    public boolean check() { return controller.hasGroups(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the selected group:
        Group selectedCurrentGroup = controller.getSelectedCurrentGroup();
        // Save the selected node and facet groups for later:
        GroupVector selectedNodeGroups = controller.getSelectedNodeGroups();
        GroupVector selectedFacetGroups = controller.getSelectedFacetGroups();
        // Reverse the ordering:
        controller.getModelManager().reverseGroupOrder();
        // Update the graphical selector objects:
        controller.updateGroupSelectors();
        controller.setSelectedCurrentGroup(selectedCurrentGroup);
        controller.setSelectedNodeGroups(selectedNodeGroups);
        controller.setSelectedFacetGroups(selectedFacetGroups);
        // Redraw 2D because group ordering may affect the order that the overlays are painted:
        controller.redraw2D();
    }
    
}
