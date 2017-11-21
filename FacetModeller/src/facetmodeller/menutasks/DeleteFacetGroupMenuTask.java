package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveFacetCommandVector;
import facetmodeller.plc.FacetVector;

/** Deletes all the facets in the currently selected group.
 * @author Peter
 */
public final class DeleteFacetGroupMenuTask extends ControlledMenuTask {
    
    public DeleteFacetGroupMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete group of facets"; }

    @Override
    public String tip() { return "Deletes all the facets in the currently selected group"; }

    @Override
    public String title() { return "Delete Group of Facets"; }

    @Override
    public boolean check() {
        if (!controller.hasGroups()) { return false; }
        return (controller.getSelectedCurrentGroup()!=null);
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask user for confirmation:
        int response = Dialogs.confirm(controller,"Are you sure you want to remove all the facets for the selected group?",title());
        if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        // Delete the group of facets:
        FacetVector facets = controller.getSelectedCurrentGroup().getFacets();
        RemoveFacetCommandVector com = new RemoveFacetCommandVector(controller.getModelManager(),facets,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
