package facetmodeller.commands;

import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

/** Command to change a node's coordinate (2D pixel coordinates for an on-section node, 3D spatial coordinates for an off-section node).
 * @author Peter
 */
public final class ChangeNodeCoordinateCommand extends Command {
    
    private final Node node; // the node changed
    private final MyPoint2D p2old; // the old 2D point
    private final MyPoint3D p3old; // the old 3D point
    private final MyPoint2D p2new; // the new 2D point
    private final MyPoint3D p3new; // the new 3D point
    
    public ChangeNodeCoordinateCommand(NodeOnSection n, MyPoint2D p2) {
        super("Change Node Coordinate");
        node = n;
        p2old = node.getPoint2D();
        p2new = p2;
        p3old = null;
        p3new = null;
    }
    
    public ChangeNodeCoordinateCommand(NodeOffSection n, MyPoint3D p3) {
        super("Change Node Coordinate");
        node = n;
        p3old = node.getPoint3D();
        p3new = p3;
        p2old = null;
        p2new = null;
    }
    
    @Override
    public void execute() {
        // Change the node coordinate to the new point:
        if (node.isOff()) {
            node.setPoint3D(p3new);
        } else {
            node.setPoint2D(p2new);
        }
    }
    
    @Override
    public void undo() {
        // Change the node coordinate to the old point:
        if (node.isOff()) {
            node.setPoint3D(p3old);
        } else {
            node.setPoint2D(p2old);
        }
    }
    
}
