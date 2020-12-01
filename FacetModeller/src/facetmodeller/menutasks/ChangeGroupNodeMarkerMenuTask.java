package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeNodeMarkerCommandVector;
import facetmodeller.groups.Group;
import facetmodeller.plc.NodeVector;

public final class ChangeGroupNodeMarkerMenuTask extends ControlledMenuTask {
    
    private final boolean bm;
    
    public ChangeGroupNodeMarkerMenuTask(FacetModeller con, boolean b) {
        super(con);
        bm = b;
    }

    @Override
    public String text() { return "Change group node boundary markers"; }

    @Override
    public String tip() { return "Change the boundary marker values for all nodes in the group"; }

    @Override
    public String title() { return "Change Group Node Boundary Markers"; }

    @Override
    public boolean check() {
        if ( !controller.hasGroups() ) { return false; }
        if ( controller.getSelectedCurrentGroup() == null ) { return false; }
        return ( controller.getSelectedCurrentGroup().numberOfNodes() != 0 );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the selected group:
        Group group = controller.getSelectedCurrentGroup();
        // Get the selected group's nodes:
        NodeVector nodes = group.getNodes();
        // Change the boundary markers:
        ChangeNodeMarkerCommandVector com = new ChangeNodeMarkerCommandVector(nodes,bm,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
