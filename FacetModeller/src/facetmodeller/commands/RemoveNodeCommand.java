package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Node;

/** Command to remove a node.
 * @author Peter
 */
public final class RemoveNodeCommand extends ModelCommand {
    
    private final Node node; // the node removed
    private RemoveFacetCommandVector com; // required for undoing the removal of the facets
    
    public RemoveNodeCommand(ModelManager mod, Node n) {
        super(mod,"Remove Node");
        node = n;
    }
    
    @Override
    public void execute() {
        // Remove any facets associated with the node:
        com = new RemoveFacetCommandVector(model,node.getFacets(),""); com.execute();
        // Remove the node from the PLC:
        model.removeNode(node);
        // Remove the node from the section that holds it:
        node.getSection().removeNode(node);
        // Remove the node from the group that holds it:
        node.getGroup().removeNode(node);
        // (Don't need to remove the node from the facets that contain it
        //  because those facets were removed in the first step.)
        // (Actually, that is done in the call to removeFacet above.)
    }
    
    @Override
    public void undo() {
        // Add the node:
        new AddNodeCommand(model,node,"").execute();
        // Undo the remove facets operation:
        com.undo();
    }
    
}
