package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

/** Command to perform several AddNode commands.
 * @author Peter
 */
public final class AddNodeCommandVector extends ModelCommandVector {
    
    public AddNodeCommandVector(ModelManager mod, NodeVector nodes, String t) {
        super(mod,t);
        // Create all the individual add node commands:
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node n = nodes.get(i);
            add( new AddNodeCommand(mod,n,"") );
        }
    }
    
}
