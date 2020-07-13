package facetmodeller.commands;

import facetmodeller.plc.Facet;

/** Command to toggle a facet's boundary marker.
 * @author Peter
 */
public final class ToggleFacetBoundaryMarkerCommand extends Command {
    
    private final Facet facet; // the facet changed
    
    public ToggleFacetBoundaryMarkerCommand(Facet f) {
        super("Toggle Facet Boundary Marker");
        facet = f;
    }
    
    @Override
    public void execute() {
        // Toggle the facet boundary marker:
        facet.toggleBoundaryMarker();
    }
    
    @Override
    public void undo() {
        // Toggle the facet boundary marker:
        facet.toggleBoundaryMarker();
    }
    
}
