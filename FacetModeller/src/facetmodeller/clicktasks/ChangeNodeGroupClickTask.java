package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeNodeGroupCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import geometry.MyPoint2D;

public final class ChangeNodeGroupClickTask extends ControlledClickTask {
    
    public ChangeNodeGroupClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_CHANGE_NODES_GROUP; }

    @Override
    public String tip() { return "Change a node's group"; }

    @Override
    public String title() { return ClickTaskUtil.CHANGE_NODE_GROUP_TITLE; }

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
        Node node = controller.getClosestNode(); // just incase the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        // Perform the change:
        ChangeNodeGroupCommand com = new ChangeNodeGroupCommand(node,controller.getSelectedCurrentGroup()); com.execute();
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
        // Perform the mouseClick processing:
        mouseClick(p);
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
