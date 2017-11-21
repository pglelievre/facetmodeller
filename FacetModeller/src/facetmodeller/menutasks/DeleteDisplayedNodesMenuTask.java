package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveNodeCommandVector;
import facetmodeller.plc.NodeVector;

/** Deletes all the nodes currently displayed (painted).
 * @author Peter
 */
public final class DeleteDisplayedNodesMenuTask extends ControlledMenuTask {
    
    public DeleteDisplayedNodesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete displayed nodes"; }

    @Override
    public String tip() { return "Deletes all the nodes currently displayed"; }

    @Override
    public String title() { return "Delete Displayed Nodes"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Check for no nodes painted:
        NodeVector nodes = controller.getPaintedNodes();
        if (nodes.isEmpty()) { return; }
        // Ask user for confirmation:
        int response = Dialogs.confirm(controller,"Are you sure you want to remove all the currently displayed nodes?",title());
        if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        // Delete the displayed nodes:
        RemoveNodeCommandVector com = new RemoveNodeCommandVector(controller.getModelManager(),nodes,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
