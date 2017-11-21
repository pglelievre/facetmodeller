package facetmodeller.commands;

import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;

/** Command to merge two groups.
 * @author Peter
 */
public final class MergeGroupsCommand extends ControlledCommandVector {
    
    public MergeGroupsCommand(FacetModeller con, Group groupMerged, Group groupKept) {
        super(con,"Merge Groups");
        // Set up the required commands ...
        // Change the group membership for any nodes associated with the ith group:
        add( new ChangeNodeGroupCommandVector( groupMerged.getNodes(), groupKept, "") );
        // Change the group membership for any facets associated with the ith group:
        add( new ChangeFacetGroupCommandVector( groupMerged.getFacets(), groupKept, "") );
        // Change the group membership for any regions associated with the ith group:
        add( new ChangeRegionGroupCommandVector( groupMerged.getRegions(), groupKept) );
        // Remove the merged group:
        add( new RemoveGroupCommand(con,groupMerged) );
    }
    
}
