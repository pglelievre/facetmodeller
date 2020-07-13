package facetmodeller.commands;

import facetmodeller.plc.Node;

/** Command to change a node's boundary marker.
 * @author Peter
 */
public final class SetNodeBoundaryMarkerCommand extends Command {
    
    private final Node node; // the node changed
    private final boolean oldB; // the original boundary marker value
    private final boolean newB; // the new boundary marker value
    
    public SetNodeBoundaryMarkerCommand(Node n, boolean b) {
        super("Set Node Boundary Marker");
        node = n;
        oldB = node.getBoundaryMarker();
        newB = b;
    }
    
    @Override
    public void execute() {
        // Change the node boundary marker to that specified:
        node.setBoundaryMarker(newB);
    }
    
    @Override
    public void undo() {
        // Reset the node boundary marker to the original value:
        node.setBoundaryMarker(oldB);
    }
    
}
