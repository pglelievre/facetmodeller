package facetmodeller.commands;

import facetmodeller.plc.Node;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

/** Command to move a node.
 * @author Peter
 */
public final class MoveNodeCommand extends Command {
    
    private Node node; // the node moved
    private MyPoint2D oldP2; // old node location for on-section nodes
    private MyPoint3D oldP3; // old node location for off-section nodes
    private MyPoint2D newP2; // new node location for on-section nodes
    private MyPoint3D newP3; // new node location for off-section nodes

    // Provide oldP2=null to always use setPoint3D method, otherwise the method used depends on
    // whether the node is on- or off-section or you then need to provide the appropriate 2D or 3D point.
    public MoveNodeCommand(Node n, MyPoint2D p2, MyPoint3D p3) {
        super("Move Node");
        node = n;
        if (p2!=null) { newP2 = p2.deepCopy(); }
        if (p3!=null) { newP3 = p3.deepCopy(); }
    }
    
    @Override
    public void execute() {
        if (node==null) { return; }
        // Check for off-section node:
        if (node.isOff()) {
            // Store the current location:
            oldP3 = node.getPoint3D().deepCopy();
            // Change the node location:
            node.setPoint3D(newP3);
        } else {
            // Store the current location:
            oldP2 = node.getPoint2D().deepCopy();
            // Change the node location:
            if (newP2==null) {
                node.setPoint3D(newP3);
            } else {
                node.setPoint2D(newP2);
            }
        }
    }
    
    @Override
    public void undo() {
        if (node==null) { return; }
        if (node.isOff()) {
            node.setPoint3D(oldP3);
        } else {
            node.setPoint2D(oldP2);
        }
    }
    
}
