package facetmodeller.commands;

import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

/** Command to perform several ChangeNodeGroup commands.
 * @author Peter
 */
public final class ChangeNodeMarkerCommandVector extends CommandVector {
    
    public ChangeNodeMarkerCommandVector(NodeVector nodes, boolean b, String t) {
        super(t);
        // Create all the individual change node marker commands:
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node n = nodes.get(i);
            add( new ChangeNodeMarkerCommand(n,b) );
        }
    }
    
}
