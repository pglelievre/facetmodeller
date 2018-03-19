package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveGroupCommand;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;

/** Removes the current group from the group lists.
 * @author Peter
 */
public final class DeleteGroupMenuTask extends ControlledMenuTask {
    
    public DeleteGroupMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete group"; }

    @Override
    public String tip() { return "Removes the current group from the group lists"; }

    @Override
    public String title() { return "Delete Group"; }

    @Override
    public boolean check() {
        if (!controller.hasGroups()) { return false; }
        return (controller.getSelectedCurrentGroup()!=null);
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask user for confirmation:
        int response = Dialogs.confirm(controller,"Are you sure you want to remove the current group?",title());
        if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        // Store the selections in the group selector objects for use later:
        int ind = controller.getSelectedCurrentGroupIndex();
        GroupVector selectedNodeGroups = controller.getSelectedNodeGroups();
        GroupVector selectedFacetGroups = controller.getSelectedFacetGroups();
        // Change those selections to remove the current group:
        Group currentGroup = controller.getSelectedCurrentGroup();
        selectedNodeGroups.remove(currentGroup);
        selectedFacetGroups.remove(currentGroup);
        // Remove the group and all associated objects (nodes,facets,regions):
        RemoveGroupCommand com = new RemoveGroupCommand(controller,currentGroup,true); com.execute();
        controller.undoVectorAdd(com);
        // Reset the selections:
        if (controller.numberOfGroups()==0) { // there are no longer any sections loaded
            controller.clearCurrentGroupSelection();
        } else {
            if (ind==0) { // the deleted group was the first
                controller.setSelectedCurrentGroupIndex(0);
            } else {
                controller.setSelectedCurrentGroupIndex(ind-1); // positions the current group to that before the one deleted
            }
        }
        controller.setSelectedNodeGroups(selectedNodeGroups);
        controller.setSelectedFacetGroups(selectedFacetGroups);
        // Tell the controller that the group selection has changed:
        controller.groupSelectionChanged(true);
    }
    
}
