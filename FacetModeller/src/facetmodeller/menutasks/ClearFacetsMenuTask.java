package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.RemoveFacetCommandVector;

/** 
 * @author Peter
 */
public final class ClearFacetsMenuTask extends ControlledMenuTask {
    
    public ClearFacetsMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Clear facets"; }

    @Override
    public String tip() { return "Clear the model facets (remove all facets from the PLC)"; }

    @Override
    public String title() { return "Clear Facets"; }

    @Override
    public boolean check() { return controller.hasFacets(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get user confirmation:
        int response = Dialogs.confirm(controller,"Are you sure?",title());
        if (response!=Dialogs.OK_OPTION) { return; }
        // Clear the facets:
        ModelManager model = controller.getModelManager();
        RemoveFacetCommandVector com = new RemoveFacetCommandVector(model,model.getFacets(),""); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
