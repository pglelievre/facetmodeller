package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeFacetGroupCommandVector;
import facetmodeller.plc.FacetVector;

/** Moves any "bad" facets into the current group:
 * - duplicate node indices
 * - fewer than nDimensions unique nodes
 * - duplicate facets
 * - linear polygonal (n>3) facets
 * - non-planar facets
 * - intersecting facets NOT implemented yet
 * @author Peter
 */
public final class FindBadFacetsMenuTask extends ControlledMenuTask {
    
    public FindBadFacetsMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find bad facets"; }

    @Override
    public String tip() { return "Moves any bad facets into the current group: facets with duplicate nodes, duplicate facets, etc."; }

    @Override
    public String title() { return "Find Bad Facets"; }

    @Override
    public boolean check() {
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return ( controller.hasNodes() && controller.hasFacets() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Find them:
        FacetVector facets = controller.findBadFacets();
        if (facets==null) { return; }
        // Check facets were found:
        if (facets.size()==0) {
            Dialogs.inform(controller,"No facets found.",title());
            return;
        }
        // Change the group membership of those facets:
        ChangeFacetGroupCommandVector com = new ChangeFacetGroupCommandVector(facets,controller.getSelectedCurrentGroup(),title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        // Inform user:
        String s = facets.size() + " facets found and moved to current group.";
        Dialogs.inform(controller,s,title());
    }
    
}
