package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeNodeGroupCommandVector;
import facetmodeller.plc.NodeVector;

/** Moves any nodes that are not found in facet definitions into the current group.
 * @author Peter
 */
public final class FindUnusedNodesMenuTask extends ControlledMenuTask {
    
    public FindUnusedNodesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find unused nodes"; }

    @Override
    public String tip() { return "Moves any nodes that are not found in facet definitions into the current group"; }

    @Override
    public String title() { return "Find Unused Nodes"; }

    @Override
    public boolean check() {
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return ( controller.hasNodes() && controller.hasFacets() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Find unused nodes:
        NodeVector nodes = controller.findUnusedNodes();
        if (nodes==null) { return; }
        // Check nodes were found:
        if (nodes.size()==0) {
            Dialogs.inform(controller,"No nodes found.",title());
            return;
        }
        // Change the group membership of those nodes:
        ChangeNodeGroupCommandVector com = new ChangeNodeGroupCommandVector(nodes,controller.getSelectedCurrentGroup(),title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        // Inform user:
        String s = nodes.size() + " nodes found and moved to current group.";
        Dialogs.inform(controller,s,title());
    }
    
}
