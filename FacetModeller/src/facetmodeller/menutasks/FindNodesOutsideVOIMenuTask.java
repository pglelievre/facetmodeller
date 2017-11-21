package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeNodeGroupCommandVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import geometry.MyPoint3D;

/** Moves any nodes outside of the VOI into the current group.
 * @author Peter
 */
public final class FindNodesOutsideVOIMenuTask extends ControlledMenuTask {
    
    public FindNodesOutsideVOIMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find nodes outside VOI"; }

    @Override
    public String tip() { return "Moves any nodes outside of the VOI into the current group"; }

    @Override
    public String title() { return "Find Nodes Outside VOI"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return ( controller.hasNodes() && controller.hasVOI() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Find any nodes outside of the VOI:
        NodeVector nodes = new NodeVector();
        for (int i=0 ; i<controller.numberOfNodes() ; i++) {
            Node node = controller.getNode(i);
            MyPoint3D p = node.getPoint3D();
            if (p!=null) { // calibrated
                if (!controller.inOrOn(p)) {
                    nodes.add(node);
                }
            }
        }
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
