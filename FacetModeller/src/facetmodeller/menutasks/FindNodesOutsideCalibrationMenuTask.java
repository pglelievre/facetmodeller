package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeNodeGroupCommandVector;
import facetmodeller.plc.NodeVector;

/** For all sections, moves any nodes outside of the calibration points into the current group.
 * @author Peter
 */
public final class FindNodesOutsideCalibrationMenuTask extends ControlledMenuTask {
    
    public FindNodesOutsideCalibrationMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find nodes outside calibration"; }

    @Override
    public String tip() { return "For all sections, moves any nodes outside of the calibration points into the current group"; }

    @Override
    public String title() { return "Find Nodes Outside Calibration"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Find the nodes:
        NodeVector nodes = controller.removeNodesCalibrationRange();
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
