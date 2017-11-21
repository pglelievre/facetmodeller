package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.MergeGroupsCommand;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;

/** Merges user-selected groups into the current group.
 * @author Peter
 */
public final class MergeGroupsMenuTask extends ControlledMenuTask {
    
    public MergeGroupsMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Merge groups"; }

    @Override
    public String tip() { return "Merges user-selected groups into the current group"; }

    @Override
    public String title() { return "Merge Groups"; }

    @Override
    public boolean check() {
        if (controller.numberOfGroups()<2) { return false; }
        return (controller.getSelectedCurrentGroup()!=null);
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Ask user to select the single group to merge with the current group:
        int n = controller.numberOfGroups();
        String[] nameList = new String[n];
        for (int i=0 ; i<n ; i++ ) {
            nameList[i] = controller.getGroup(i).getName();
        }
        String prompt = "Select the group to merge into the current group:";
        int groupIndex = Dialogs.selection(controller,prompt,title(),nameList,0);
        
        // Check the response:
        if (groupIndex<0) { return; } // user cancelled
        
        // Store the selected group information for later:
        GroupVector selectedNodeGroups = controller.getSelectedNodeGroups();
        GroupVector selectedFacetGroups = controller.getSelectedFacetGroups();

        // Get the selected group:
        Group group = controller.getGroup(groupIndex);

        // Skip the current group:
        Group currentGroup = controller.getSelectedCurrentGroup();
        if (group==currentGroup) { return; }

        // Merge the two groups:
        MergeGroupsCommand com = new MergeGroupsCommand(controller,group,currentGroup); com.execute(); // calls groupVectorRemove and updateGroupSelectors
        controller.undoVectorAdd(com);

        // Remove the ith group from any selections:
        selectedNodeGroups.remove(group);
        selectedFacetGroups.remove(group);
            
        // Set the selections in the the group selector objects:
        controller.setSelectedCurrentGroup(currentGroup);
        controller.setSelectedNodeGroups(selectedNodeGroups);
        controller.setSelectedFacetGroups(selectedFacetGroups);
        
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
    }
    
}
