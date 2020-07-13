package facetmodeller.commands;

import facetmodeller.plc.Node;

/** Command to toggle a node's boundary marker.
 * @author Peter
 */
public final class ToggleNodeBoundaryMarkerCommand extends Command {
    
    private final Node node; // the node changed
    
    public ToggleNodeBoundaryMarkerCommand(Node n) {
        super("Toggle Node Boundary Marker");
        node = n;
    }
    
    @Override
    public void execute() {
        // Toggle the node boundary marker:
        node.toggleBoundaryMarker();
    }
    
    @Override
    public void undo() {
        // Toggle the node boundary marker:
        node.toggleBoundaryMarker();
    }
    
}
