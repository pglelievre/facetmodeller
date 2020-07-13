package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.Command;
import facetmodeller.commands.SetNodeBoundaryMarkerCommand;
import facetmodeller.commands.ToggleNodeBoundaryMarkerCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import geometry.MyPoint2D;

public final class MarkNodeClickTask extends ControlledClickTask {
    
    private int opt; // =0 if toggling, <0 if setting to false, >0 if setting to true
    
    public MarkNodeClickTask(FacetModeller con, int i) {
        super(con);
        opt = i;
    }
    
    @Override
    public int mode() {
        if (opt>0) {
            return ClickModeManager.MODE_MARK_NODES_TRUE;
        } else if (opt<0) {
            return ClickModeManager.MODE_MARK_NODES_FALSE;
        } else { // opt==0
            return ClickModeManager.MODE_MARK_NODES_TOGGLE;
        }
    }

    @Override
    public String text() {
        if (opt>0) {
            return ClickTaskUtil.MARK_NODE_TRUE_TEXT;
        } else if (opt<0) {
            return ClickTaskUtil.MARK_NODE_FALSE_TEXT;
        } else { // opt==0
            return ClickTaskUtil.MARK_NODE_TOGGLE_TEXT;
        }
    }

    @Override
    public String tip() {
        if (opt>0) {
            return "Set a node's boundary marker to true (1)";
        } else if (opt<0) {
            return "Set a node's boundary marker to false (0)";
        } else { // opt==0
            return "Toggle a node's boundary marker";
        }
    }

    @Override
    public String title() {
        if (opt>0) {
            return ClickTaskUtil.MARK_NODE_TRUE_TITLE;
        } else if (opt<0) {
            return ClickTaskUtil.MARK_NODE_FALSE_TITLE;
        } else { // opt==0
            return ClickTaskUtil.MARK_NODE_TOGGLE_TITLE;
        }
    }
    
    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node node = controller.getClosestNode(); // just in case the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        if (node==null) { return; }
        // Perform the change:
        Command com;
        if (opt>0) {
            com = new SetNodeBoundaryMarkerCommand(node,true);
        } else if (opt<0) {
            com = new SetNodeBoundaryMarkerCommand(node,false);
        } else { // opt==0
            com = new ToggleNodeBoundaryMarkerCommand(node);
        }
        com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.clearClosestNode(); // (or else the old closest node point will be painted)
        controller.redraw();
    }
    
    @Override
    public void mouseDrag(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest node to the cursor position and redraw if successful:
        if (controller.calculateClosestNode(p)) { controller.redraw(); }
        // Perform the mouseClick processing if not toggling:
        if (opt!=0) { mouseClick(p); }
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest node to the cursor position:
        controller.calculateClosestNode(p);
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest node:
        controller.updateClosestBar(p);
    }
    
}
