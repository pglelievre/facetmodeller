package facetmodeller.commands;

import facetmodeller.groups.Group;
import facetmodeller.plc.Node;

/** Command to change a node's group membership.
 * @author Peter
 */
public final class ChangeNodeGroupCommand extends Command {
    
    private final Node node; // the node changed
    private final Group oldGroup; // the old group (existing node group on construction)
    private final Group newGroup; // the new group
    
    public ChangeNodeGroupCommand(Node n, Group g) {
        super("Change Node Group");
        node = n;
        oldGroup = node.getGroup();
        newGroup = g;
    }
    
    @Override
    public void execute() {
        // Remove the node from its existing group:
        //node.getGroup().removeNode(node);
        oldGroup.removeNode(node);
        // Change the group membership of the node:
        node.setGroup(newGroup);
        // Add the node to its new group:
        //node.getGroup().addNode(node);
        newGroup.addNode(node);
    }
    
    @Override
    public void undo() {
        // Remove the node from its new group:
        newGroup.removeNode(node);
        // Change the group membership of the node back to its old group:
        node.setGroup(oldGroup);
        // Add the node to its old group:
        oldGroup.addNode(node);
    }
    
}
