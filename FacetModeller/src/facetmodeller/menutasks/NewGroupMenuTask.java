package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddGroupCommand;
import facetmodeller.groups.Group;

/** Adds a new group (user is asked for some information).
 * @author Peter
 */
public final class NewGroupMenuTask extends ControlledMenuTask {
    
    public NewGroupMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "New group"; }

    @Override
    public String tip() { return "Adds a new group (user is asked for some information)"; }

    @Override
    public String title() { return "New Group"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the name of the group:
        String name = Dialogs.input(controller,"Enter a name for the group:",title());
        if (name==null) { return; } // user cancelled
        // Get the currently selected node and facet groups so we can maintain the selections:
        int[] nodeGroupIndices = controller.getSelectedNodeGroupIndices();
        int[] facetGroupIndices = controller.getSelectedFacetGroupIndices();
        // Create a new group object:
        Group group = new Group(name);
        // Add the new group object:
        AddGroupCommand com = new AddGroupCommand(controller,group,0); com.execute();
        controller.undoVectorAdd(com);
        // Update the selections in the group selector objects:
        int n = controller.numberOfGroups() - 1;
        controller.setSelectedCurrentGroupIndex(n); // sets the selected group to the new (last) group
        controller.setSelectedNodeGroupIndices(nodeGroupIndices);
        controller.setSelectedFacetGroupIndices(facetGroupIndices);
        // Change the painting color:
        new ChangeGroupColorMenuTask(controller).execute();
    }
    
}
