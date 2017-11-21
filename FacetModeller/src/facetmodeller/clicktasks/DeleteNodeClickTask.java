package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveNodeCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import geometry.MyPoint2D;

public final class DeleteNodeClickTask extends ControlledClickTask {
    
    public DeleteNodeClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_DELETE_NODES; }

    @Override
    public String tip() { return "Delete painted nodes"; }

    @Override
    public String title() { return ClickTaskUtil.DELETE_NODE_TITLE; }

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
        // Ask the user for confirmation:
        if (controller.getShowConfirmationDialogs()) {
            int response = Dialogs.confirm(controller,"Are you sure you want to delete that node?",title());
            if (response!=Dialogs.OK_OPTION) { return; }
        }
        // Remove the node:
        RemoveNodeCommand com = new RemoveNodeCommand(controller.getModelManager(),node); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Redraw:
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