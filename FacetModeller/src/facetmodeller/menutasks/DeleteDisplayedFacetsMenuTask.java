package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveFacetCommandVector;
import facetmodeller.plc.FacetVector;

/** Deletes all the facets currently displayed (painted).
 * @author Peter
 */
public final class DeleteDisplayedFacetsMenuTask extends ControlledMenuTask {
    
    public DeleteDisplayedFacetsMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete displayed facets"; }

    @Override
    public String tip() { return "Deletes all the facets currently displayed"; }

    @Override
    public String title() { return "Delete Displayed Facets"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        return controller.hasFacets();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Check for no facets painted:
        FacetVector facets = controller.getPaintedFacets();
        if (facets.isEmpty()) { return; }
        // Ask user for confirmation:
        int response = Dialogs.confirm(controller,"Are you sure you want to remove all the currently displayed facets?",title());
        if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        // Delete the displayed facets:
        RemoveFacetCommandVector com = new RemoveFacetCommandVector(controller.getModelManager(),facets,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
