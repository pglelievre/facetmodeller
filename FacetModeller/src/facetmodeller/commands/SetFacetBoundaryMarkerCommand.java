package facetmodeller.commands;

import facetmodeller.plc.Facet;

/** Command to change a facet's boundary marker.
 * @author Peter
 */
public final class SetFacetBoundaryMarkerCommand extends Command {
    
    private final Facet facet; // the facet changed
    private final boolean oldB; // the original boundary marker value
    private final boolean newB; // the new boundary marker value
    
    public SetFacetBoundaryMarkerCommand(Facet f, boolean b) {
        super("Set Facet Boundary Marker");
        facet = f;
        oldB = facet.getBoundaryMarker();
        newB = b;
    }
    
    @Override
    public void execute() {
        // Change the facet boundary marker to that specified:
        facet.setBoundaryMarker(newB);
    }
    
    @Override
    public void undo() {
        // Reset the facet boundary marker to the original value:
        facet.setBoundaryMarker(oldB);
    }
    
}
