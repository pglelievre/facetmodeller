package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveNodeCommandVector;
import facetmodeller.plc.NodeVector;

/** Deletes all the nodes in the currently selected group.
 * @author Peter
 */
public final class DeleteNodeGroupMenuTask extends ControlledMenuTask {
    
    public DeleteNodeGroupMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete group of nodes"; }

    @Override
    public String tip() { return "Deletes all the nodes in the currently selected group"; }

    @Override
    public String title() { return "Delete Group of Nodes"; }

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
        int response = Dialogs.confirm(controller,"Are you sure you want to remove all the nodes for the selected group?",title());
        if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        // Delete the group of nodes:
        NodeVector nodes = controller.getSelectedCurrentGroup().getNodes();
        RemoveNodeCommandVector com = new RemoveNodeCommandVector(controller.getModelManager(),nodes,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
