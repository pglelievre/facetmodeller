package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Node;

/** Command to add a node.
 * @author Peter
 */
public final class AddNodeCommand extends ModelCommand {
    
    private Node node; // the node added
    
    public AddNodeCommand(ModelManager mod, Node n, String t) {
        super(mod,t);
        node = n; // the added node should be fully defined on construction (attached to section and group)
    }
    
    @Override
    public void execute() {
        if (node==null) { return; }
        // Add the node to the PLC:
        model.addNode(node);
        // Add the node to the section that the node is in:
        node.getSection().addNode(node);
        // Add the node to the group that the node is in:
        node.getGroup().addNode(node);
    }
    
    @Override
    public void undo() {
        // Remove the node:
        new RemoveNodeCommand(model,node).execute();
    }
    
}
