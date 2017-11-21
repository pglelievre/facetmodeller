package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.ReverseFacetCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import geometry.MyPoint2D;

public final class ReverseFacetClickTask extends ControlledClickTask {
    
    public ReverseFacetClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_REVERSE_FACETS; }

    @Override
    public String tip() { return ClickTaskUtil.REVERSE_FACET_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.REVERSE_FACET_TITLE; }

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
        Facet facet = controller.getClosestFacet(); // just incase the closestFacet object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        // Reverse the node order of the facet:
        ReverseFacetCommand com = new ReverseFacetCommand(facet); com.execute();
        controller.undoVectorAdd(com);
        // Repaint:
        controller.redraw();
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
