package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeFacetGroupCommandVector;
import facetmodeller.plc.FacetVector;

/** Finds any edges that are not connected to two facets and moves their facets into the current group.
 * @author Peter
 */
public final class FindHolesMenuTask extends ControlledMenuTask {
    
    public FindHolesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find holes"; }

    @Override
    public String tip() { return "Finds any edges that are not connected to two facets and moves their facets into the current group"; }

    @Override
    public String title() { return "Find Holes"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return ( controller.hasNodes() || controller.hasFacets() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Find them:
        FacetVector facets = controller.findHoles();
        if (facets==null) {
            Dialogs.inform(controller,"You can't currently search for holes when non-triangular facets exist.",title());
            return;
        }
        // Check facets were found:
        if (facets.size()==0) {
            Dialogs.inform(controller,"No holes found.",title());
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
        String s = facets.size() + " facets found around holes and moved to current group.";
        Dialogs.inform(controller,s,title());
    }
    
}
