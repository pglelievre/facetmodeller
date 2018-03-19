package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveRegionCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Region;
import geometry.MyPoint2D;

public final class DeleteRegionClickTask extends ControlledClickTask {
    
    public DeleteRegionClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_DELETE_REGIONS; }

    @Override
    public String text() { return ClickTaskUtil.DELETE_REGION_TEXT; }

    @Override
    public String tip() { return ClickTaskUtil.DELETE_REGION_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.DELETE_REGION_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasRegions();
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Calculate the closest region to the clicked point:
        if (!controller.calculateClosestRegion(p)) { return; }
        Region region = controller.getClosestRegion(); // just incase the closestRegion object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        // Remove the region:
        RemoveRegionCommand com = new RemoveRegionCommand(controller.getModelManager(),region); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.clearClosestRegion(); // (or else the old closest region point will be painted)
        controller.redraw();
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest region point to the cursor position:
        controller.calculateClosestRegion(p);
        // Redraw the 2D panel:
        controller.redraw2D();
        // Use the cursor bar to show information for the closest region:
        controller.updateClosestBar(p);
    }
    
}
