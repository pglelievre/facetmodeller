package facetmodeller.commands;

import facetmodeller.groups.Group;
import facetmodeller.plc.Region;

/** Command to change a region's group membership.
 * @author Peter
 */
public final class ChangeRegionGroupCommand extends Command {
    
    private Region region; // the region changed
    private Group oldGroup; // the old group (existing region group on construction)
    private Group newGroup; // the new group
    
    public ChangeRegionGroupCommand(Region r, Group g) {
        super("Change Region Group");
        region = r;
        oldGroup = region.getGroup();
        newGroup = g;
    }
    
    @Override
    public void execute() {
        // Remove the region from its existing group:
        //region.getGroup().removeRegion(region);
        oldGroup.removeRegion(region);
        // Change the group membership of the region:
        region.setGroup(newGroup);
        // Add the region to its new group:
        //region.getGroup().addRegion(region);
        newGroup.addRegion(region);
    }
    
    @Override
    public void undo() {
        // Remove the region from its new group:
        newGroup.removeRegion(region);
        // Change the group membership of the region back to its old group:
        region.setGroup(oldGroup);
        // Add the region to its old group:
        oldGroup.addRegion(region);
    }
    
}
