package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeFacetMarkerCommandVector;
import facetmodeller.groups.Group;
import facetmodeller.plc.FacetVector;

public final class ChangeGroupFacetMarkerMenuTask extends ControlledMenuTask {
    
    private final boolean bm;
    
    public ChangeGroupFacetMarkerMenuTask(FacetModeller con, boolean b) {
        super(con);
        bm = b;
    }

    @Override
    public String text() { return "Change group facet boundary markers"; }

    @Override
    public String tip() { return "Change the boundary marker values for all facets in the group"; }

    @Override
    public String title() { return "Change Group Facet Boundary Markers"; }

    @Override
    public boolean check() {
        if ( !controller.hasGroups() ) { return false; }
        if ( controller.getSelectedCurrentGroup() == null ) { return false; }
        return ( controller.getSelectedCurrentGroup().numberOfFacets() != 0 );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the selected group:
        Group group = controller.getSelectedCurrentGroup();
        // Get the selected group's facets:
        FacetVector facets = group.getFacets();
        // Change the boundary markers:
        ChangeFacetMarkerCommandVector com = new ChangeFacetMarkerCommandVector(facets,bm,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
