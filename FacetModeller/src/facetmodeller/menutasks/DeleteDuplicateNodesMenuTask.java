package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.DuplicateNodeInfo;
import facetmodeller.commands.RemoveNodeCommandVector;

/** 
 * @author Peter
 */
public final class DeleteDuplicateNodesMenuTask extends ControlledMenuTask {
    
    public DeleteDuplicateNodesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete duplicate nodes"; }

    @Override
    public String tip() { return "Deletes duplicate nodes"; }

    @Override
    public String title() { return "Delete Duplicate Nodes"; }

    @Override
    public boolean check() { return controller.hasNodes(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Find the duplicates:
        DuplicateNodeInfo dupInfo = controller.findDuplicateNodes();
        int n = dupInfo.size();
        // Check number of nodes to remove:
        if (n==0) {
            // Inform user:
            Dialogs.inform(controller,"There are no duplicate nodes.",title());
            return;
        } else {
            // Get user confirmation:
            String message;
            if (n==1) {
               message = "1 node will be removed.";
            } else {
               message = n + " nodes will be removed.";
            }
            int resp = Dialogs.confirm(controller,message,title());
            if (resp!=Dialogs.OK_OPTION) { return; }
        }
        // Remove the marked nodes:
        RemoveNodeCommandVector com = new RemoveNodeCommandVector(controller.getModelManager(),dupInfo.getNodesToRemove(),title()); com.execute();
        dupInfo.clear(); // removes any reference to the removed nodes so they can be removed from memory by the garbage collector
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
