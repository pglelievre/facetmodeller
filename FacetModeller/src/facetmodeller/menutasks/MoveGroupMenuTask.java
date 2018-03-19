package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;

public abstract class MoveGroupMenuTask extends ControlledMenuTask {
    
    private final int move;
    
    public MoveGroupMenuTask(FacetModeller con, int i) {
        super(con);
        move = i;
    }

    @Override
    public boolean check() { return controller.hasGroups(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the selected group:
        Group group = controller.getSelectedCurrentGroup();
        if (group==null) {
            Dialogs.error(controller,"Please select a group first.",title());
        }
        // Save the selected node and facet groups for later:
        GroupVector selectedNodeGroups = controller.getSelectedNodeGroups();
        GroupVector selectedFacetGroups = controller.getSelectedFacetGroups();
        // Move the group in the model:
        int ind2 = controller.getModelManager().moveGroup(group,move);
        if (ind2<0) { return; }
        // Update the graphical selector objects:
        controller.updateGroupSelectors();
        controller.setSelectedCurrentGroupIndex(ind2);
        controller.setSelectedNodeGroups(selectedNodeGroups);
        controller.setSelectedFacetGroups(selectedFacetGroups);
        // Redraw 2D because group ordering may affect the order that the overlays are painted:
        controller.redraw2D();
    }
    
}
