package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

/** Command to perform several RemoveNode commands.
 * @author Peter
 */
public final class RemoveNodeCommandVector extends ModelCommandVector {
    
    public RemoveNodeCommandVector(ModelManager mod, NodeVector nodes, String t) {
        super(mod,t);
        // Create all the individual remove node commands:
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node n = nodes.get(i);
            add( new RemoveNodeCommand(mod,n) );
        }
    }
    
}
