package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.AddFacetCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import geometry.MyPoint2D;

public final class DefineTriFacetClickTask extends ControlledClickTask {
    
    public DefineTriFacetClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_DEFINE_TRI_FACETS; }

    @Override
    public String text() { return ClickTaskUtil.DEFINE_TRI_FACET_TEXT; }

    @Override
    public String tip() { return ClickTaskUtil.DEFINE_TRI_FACET_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.DEFINE_TRI_FACET_TITLE; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
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
        // Calculate the candidate facet:
        Facet currentFacet = controller.calculateTriFacet(p);
        // Check for null facet:
        if (currentFacet==null) { return; }
        // Link the facet to the current group:
        currentFacet.setGroup(controller.getSelectedCurrentGroup());
        // Add the facet:
        AddFacetCommand com = new AddFacetCommand(controller.getModelManager(),currentFacet); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
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
        // Calculate a candidate triangular facet:
        Facet currentFacet = controller.calculateTriFacet(p);
        // Check for null facet:
        if (currentFacet!=null) {
            // Enable or disable menu items:
            controller.checkItemsEnabled();
            // Redraw:
            controller.redraw();
        }
        // Use the cursor bar to show the minimum angle of the candidate triangular facet:
        controller.updateMinAngleBar();
    }

}
