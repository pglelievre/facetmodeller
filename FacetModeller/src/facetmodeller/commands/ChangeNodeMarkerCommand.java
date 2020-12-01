package facetmodeller.commands;

import facetmodeller.plc.Node;

/** Command to change a node's group membership.
 * @author Peter
 */
public final class ChangeNodeMarkerCommand extends Command {
    
    private final Node node; // the node changed
    private final boolean oldMarker; // the old boundary marker value (existing node boundary marker value on construction)
    private final boolean newMarker; // the new boundary marker value
    
    public ChangeNodeMarkerCommand(Node n, boolean b) {
        super("Change Node Boundary Marker");
        node = n;
        oldMarker = node.getBoundaryMarker();
        newMarker = b;
    }
    
    @Override
    public void execute() {
        // Change the boundary marker value:
        node.setBoundaryMarker(newMarker);
    }
    
    @Override
    public void undo() {
        // Change the boundary marker value back to its old value:
        node.setBoundaryMarker(oldMarker);
    }
    
}
