package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.ChangeFacetGroupCommandVector;
import facetmodeller.plc.FacetVector;

/** Helps the user find particular facets by index.
 * @author Peter
 */
public final class FindFacetsIndexMenuTask extends ControlledMenuTask {
    
    public FindFacetsIndexMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find facets by index"; }

    @Override
    public String tip() { return "Moves facets with specified indices into the current group"; }

    @Override
    public String title() { return "Find Facets by Index"; }

    @Override
    public boolean check() {
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasFacets();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the facet index to find:
        String response = Dialogs.input(controller,"Enter the indices of the facets to find:",title());
        if (response==null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        FacetVector facetsFound = new FacetVector();
        int nfacets = controller.numberOfFacets();
        try {
            ModelManager model = controller.getModelManager();
            //for (int i=0 ; i<ss.length ; i++ ) {
            //    int ind = Integer.parseInt(ss[i].trim());
            for (String s : ss) {
                int ind = Integer.parseInt(s.trim());
                if ( ind<1 || ind>nfacets ) { throw new NumberFormatException(); }
                facetsFound.add( model.getFacet(ind-1) ); // -1 because Java starts numbering from 0 but poly files from 1
            }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter integer values on [1,"+nfacets+"]. Please try again.","Error");
            return;
        }
        // Move the facets to the current group:
        ChangeFacetGroupCommandVector com = new ChangeFacetGroupCommandVector(facetsFound,controller.getSelectedCurrentGroup(),title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
