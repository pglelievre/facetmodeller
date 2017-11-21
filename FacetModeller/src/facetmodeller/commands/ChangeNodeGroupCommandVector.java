package facetmodeller.commands;

import facetmodeller.groups.Group;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

/** Command to perform several ChangeNodeGroup commands.
 * @author Peter
 */
public final class ChangeNodeGroupCommandVector extends CommandVector {
    
    public ChangeNodeGroupCommandVector(NodeVector nodes, Group g, String t) {
        super(t);
        // Create all the individual change node group commands:
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node n = nodes.get(i);
            add( new ChangeNodeGroupCommand(n,g) );
        }
    }
    
}
