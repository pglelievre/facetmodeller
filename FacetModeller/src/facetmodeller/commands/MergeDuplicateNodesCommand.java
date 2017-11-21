package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Node;

/** Command to perform several MergeNode commands.
 * @author Peter
 */
public final class MergeDuplicateNodesCommand extends ModelCommandVector {
    
    public MergeDuplicateNodesCommand(ModelManager mod, DuplicateNodeInfo dupInfo) {
        super(mod,"Merge Duplicate Nodes");
        // Create all the individual merge node commands:
        for (int i=0 ; i<dupInfo.size() ; i++ ) {
            Node node1 = dupInfo.getNodeToRemove(i);
            Node node2 = dupInfo.getNodeToKeep(i);
            add( new MergeNodesCommand(mod,node1,node2,"") );
        }
    }
    
}
