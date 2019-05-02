package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

/** Command to replace a node with another and delete the former.
 * If the new node is not in the PLC then it is added to the PLC.
 * @author Peter
 */
public final class MergeNodesCommand extends ModelCommand {
    
    private Node node1; // the node removed
    private Node node2; // the node that replaces the one removed
    private FacetVector oldFacets1; // a deep copy of the facets belonging to node1 before merging
    private AddNodeCommand com = null;
    
    public MergeNodesCommand(ModelManager mod, Node n1, Node n2, String t) {
        super(mod,t);
        node1 = n1;
        node2 = n2;
    }
    
    @Override
    public void execute() {
        // Check if we have to add the new node to the PLC:
        if (!model.containsNode(node2)) {
            com = new AddNodeCommand(model,node2,""); com.execute();
        }
        // Make a deep copy of the facets that contain the first node:
        FacetVector facets1 = node1.getFacets();
        oldFacets1 = facets1.deepCopy(); // contains new Facet objects that link to existing nodes
        // Replace node1 with node2 in any facet definitions for the node being removed:
        for (int i=0 ; i<facets1.size() ; i++ ) { // loop over every facet that contains the first node
            Facet f = facets1.get(i);
            NodeVector nodes = f.getNodes(); // the nodes in the current facet
            while ( nodes.replace(node1,node2) >= 0 ) {} // tries to replace the FIRST instance of node1 in the list until node1 is no longer found
            // If the facet connects a pair of nodes along an edge then delete one of the node references (zero-length edge):
            nodes.removeZeroEdges();
            // Make sure that node2 is linked to the facet:
            //f.addNode(node2); // unnecessary
            node2.addFacet(f);
        }
        // Remove the first node from the PLC:
        model.removeNode(node1);
        // Remove the first node from the section that holds it:
        node1.getSection().removeNode(node1);
        // Remove the first node from the group that holds it:
        node1.getGroup().removeNode(node1);
    }
    
    @Override
    public void undo() {
        // Add the old node back in:
        new AddNodeCommand(model,node1,"").execute();
        // Replace the existing facets with the old versions:
        FacetVector facets1 = node1.getFacets();
        for (int i=0 ; i<facets1.size() ; i++ ) {
            Facet f = facets1.get(i);
            f.clear(); // clears all nodes
            f.addNodes( oldFacets1.get(i).getNodes() ); // adds old node references back into the facet
        }
        // Check if we have to remove the new node from the PLC:
        if (com!=null) {
            // First remove the node from all of it's facets so they aren't deleted in the undo below:
            node2.clearFacets();
            // Now undo addition of the new node:
            com.undo();
        }
        // Nullify any working objects:
        node1 = null;
        node2 = null;
        oldFacets1 = null;
        com = null;
    }
    
}
