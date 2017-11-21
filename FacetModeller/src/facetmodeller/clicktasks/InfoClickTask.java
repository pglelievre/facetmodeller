package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;
import geometry.MyPoint2D;

public final class InfoClickTask extends ControlledClickTask {
    
    public InfoClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_INFO; }

    @Override
    public String tip() { return ClickTaskUtil.INFO_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.INFO_TITLE; }

    @Override
    public boolean check() { return !controller.plcIsEmpty(); }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Lock or unlock the closest node/facet/region information:
        controller.toggleLock();
        // Calculate the closest node, facet and region to the cursor position and
        // redraw if any of those objects are not null:
        if ( controller.calculateClosest(p) ) { controller.redraw(); }
        // Use the cursor bar to show information for the closest node/facet/region:
        controller.updateClosestBar(p);
    }
    
    @Override
    public void mouseDrag(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Check if the closest node/facet/region information is locked:
        if (!controller.isLocked()) {
            // Set some working objects to null:
            controller.clearClosestTemporaryOverlays();
        }
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Check if the closest node/facet/region information is locked:
        if (controller.isLocked()) {
            return;
        } else {
            // Set some working objects to null:
            controller.clearClosestTemporaryOverlays();
        }
        // Calculate the closest node/facet/region to the cursor position:
        controller.calculateClosest(p);
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest node/facet/region:
        controller.updateClosestBar(p);
    }
    
}
