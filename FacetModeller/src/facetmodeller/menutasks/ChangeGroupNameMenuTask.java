package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;

public final class ChangeGroupNameMenuTask extends ControlledMenuTask {
    
    public ChangeGroupNameMenuTask(FacetModeller con) { super(con); }

    @Override
    public String text() { return "Change group name"; }

    @Override
    public String tip() { return "Change the name of the currently selected group"; }

    @Override
    public String title() { return "Change Group Name"; }

    @Override
    public boolean check() {
        if ( !controller.hasGroups() ) { return false; }
        return ( controller.getSelectedCurrentGroup() != null );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the selected group:
        Group group = controller.getSelectedCurrentGroup();
        // Ask for a new name:
        String newName = Dialogs.input(controller,"Enter the new name for the current group:",title(),group.getName());
        // Check response:
        if (newName == null) { return; }
        // Set the name to that specified:
        group.setName(newName);
        // Store the selections in the selector objects:
        int cgs = controller.getSelectedCurrentGroupIndex();
        int[] ngs = controller.getSelectedNodeGroupIndices();
        int[] fgs = controller.getSelectedFacetGroupIndices();
        // Update the graphical selector objects:
        controller.updateGroupSelectors();
        // Reset the selections to whatever they were:
        controller.setSelectedCurrentGroupIndex(cgs);
        controller.setSelectedNodeGroupIndices(ngs);
        controller.setSelectedFacetGroupIndices(fgs);
    }
    
}
