package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.DuplicateNodeInfo;
import facetmodeller.commands.MergeDuplicateNodesCommand;

/** 
 * @author Peter
 */
public final class MergeDuplicateNodesMenuTask extends ControlledMenuTask {
    
    public MergeDuplicateNodesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete duplicate nodes via merging"; }

    @Override
    public String tip() { return "Deletes duplicate nodes via merging"; }

    @Override
    public String title() { return "Merge Duplicate Nodes"; }

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
               message = "1 node will be removed via merging.";
            } else {
               message = n + " nodes will be removed via merging.";
            }
            int resp = Dialogs.confirm(controller,message,title());
            if (resp!=Dialogs.OK_OPTION) { return; }
        }
        // Merge the marked nodes:
        MergeDuplicateNodesCommand com = new MergeDuplicateNodesCommand(controller.getModelManager(),dupInfo); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
