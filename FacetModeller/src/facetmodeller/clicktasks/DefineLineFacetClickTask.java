package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.AddFacetCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import geometry.MyPoint2D;

public final class DefineLineFacetClickTask extends ControlledClickTask {
    
    public DefineLineFacetClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_DEFINE_LINE_FACETS; }

    @Override
    public String tip() { return ClickTaskUtil.DEFINE_LINE_FACET_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.DEFINE_LINE_FACET_TITLE; }

    @Override
    public boolean check() {
        if (!controller.is2D()) { return false; }
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
        Facet currentFacet = controller.calculateLineFacet(p);
        // Check for null facet:
        if (currentFacet==null) { return; }
//        // Check that at least one node is in the current section, otherwise don't allow facet to be created:
//        if (!currentFacet.containsSection(controller.getSelectedCurrentSection())) { return; }
        // Link the facet to the current group:
        currentFacet.setGroup(controller.getSelectedCurrentGroup());
        // Add the facet:
        AddFacetCommand com = new AddFacetCommand(controller.getModelManager(),currentFacet); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Redraw:
        controller.redraw();
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the candidate linear edge facet:
        Facet currentFacet = controller.calculateLineFacet(p);
        // Check for null facet:
        if (currentFacet!=null) {
            // Enable or disable menu items:
            controller.checkItemsEnabled();
            // Redraw:
            controller.redraw();
        }
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest node/facet/region:
        controller.updateClosestBar(p);
    }

    
}
