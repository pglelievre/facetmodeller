package facetmodeller.commands;

import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;

/** Command to split a group of nodes and facets.
 * @author Peter
 */
public final class SplitGroupCommand extends ControlledCommandVector {
    
    public SplitGroupCommand(FacetModeller con, Group group, String name, Group newGroup, NodeVector nodes, FacetVector facets, int index, String t) {
        super(con,t);
        // Set up the sub commands ...
        // Change the group name:
        if (name!=null) {
            add( new ChangeGroupNameCommand(group,name) );
        }
        // Move the nodes and facets into the new group:
        add( new ChangeNodeGroupCommandVector(nodes,newGroup,"") );
        if (facets!=null) {
            add( new ChangeFacetGroupCommandVector(facets,newGroup,"") );
        }
        // Add the group:
        add( new AddGroupCommand(con,group,index) );
    }
    
}
