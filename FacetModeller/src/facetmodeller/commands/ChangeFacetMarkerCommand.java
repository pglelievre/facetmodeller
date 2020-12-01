package facetmodeller.commands;

import facetmodeller.plc.Facet;

/** Command to change a facet's group membership.
 * @author Peter
 */
public final class ChangeFacetMarkerCommand extends Command {
    
    private final Facet facet; // the facet changed
    private final boolean oldMarker; // the old boundary marker value (existing facet boundary marker value on construction)
    private final boolean newMarker; // the new boundary marker value
    
    public ChangeFacetMarkerCommand(Facet f, boolean b) {
        super("Change Facet Boundary Marker");
        facet = f;
        oldMarker = facet.getBoundaryMarker();
        newMarker = b;
    }
    
    @Override
    public void execute() {
        // Change the boundary marker value:
        facet.setBoundaryMarker(newMarker);
    }
    
    @Override
    public void undo() {
        // Change the boundary marker value back to its old value:
        facet.setBoundaryMarker(oldMarker);
    }
    
}
