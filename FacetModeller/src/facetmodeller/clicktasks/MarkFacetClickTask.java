package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.Command;
import facetmodeller.commands.SetFacetBoundaryMarkerCommand;
import facetmodeller.commands.ToggleFacetBoundaryMarkerCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import geometry.MyPoint2D;

public final class MarkFacetClickTask extends ControlledClickTask {
    
    private int opt; // =0 if toggling, <0 if setting to false, >0 if setting to true
    
    public MarkFacetClickTask(FacetModeller con, int i) {
        super(con);
        opt = i;
    }
    
    @Override
    public int mode() {
        if (opt>0) {
            return ClickModeManager.MODE_MARK_FACETS_TRUE;
        } else if (opt<0) {
            return ClickModeManager.MODE_MARK_FACETS_FALSE;
        } else { // opt==0
            return ClickModeManager.MODE_MARK_FACETS_TOGGLE;
        }
    }

    @Override
    public String text() {
        if (opt>0) {
            return ClickTaskUtil.MARK_FACET_TRUE_TEXT;
        } else if (opt<0) {
            return ClickTaskUtil.MARK_FACET_FALSE_TEXT;
        } else { // opt==0
            return ClickTaskUtil.MARK_FACET_TOGGLE_TEXT;
        }
    }

    @Override
    public String tip() {
        if (opt>0) {
            return "Set a facet's boundary marker to true (1)";
        } else if (opt<0) {
            return "Set a facet's boundary marker to false (0)";
        } else { // opt==0
            return "Toggle a facet's boundary marker";
        }
    }

    @Override
    public String title() {
        if (opt>0) {
            return ClickTaskUtil.MARK_FACET_TRUE_TITLE;
        } else if (opt<0) {
            return ClickTaskUtil.MARK_FACET_FALSE_TITLE;
        } else { // opt==0
            return ClickTaskUtil.MARK_FACET_TOGGLE_TITLE;
        }
    }
    
    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasFacets();
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Calculate the closest facet to the clicked point:
        if (!controller.calculateClosestFacet(p)) { return; }
        Facet facet = controller.getClosestFacet(); // just in case the closestFacet object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        if (facet==null) { return; }
        // Perform the change:
        Command com;
        if (opt>0) {
            com = new SetFacetBoundaryMarkerCommand(facet,true);
        } else if (opt<0) {
            com = new SetFacetBoundaryMarkerCommand(facet,false);
        } else { // opt==0
            com = new ToggleFacetBoundaryMarkerCommand(facet);
        }
        com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.clearClosestFacet(); // (or else the old closest facet point will be painted)
        controller.redraw();
    }
    
    @Override
    public void mouseDrag(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest facet to the cursor position and redraw if successful:
        if (controller.calculateClosestFacet(p)) { controller.redraw(); }
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
        // Calculate the closest facet to the cursor position:
        controller.calculateClosestFacet(p);
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest facet:
        controller.updateClosestBar(p);
    }
    
}
