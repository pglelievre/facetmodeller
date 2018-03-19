package facetmodeller.commands;

import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.groups.Group;

/** Command to remove a group.
 * @author Peter
 */
public final class RemoveGroupCommand extends ControlledCommandVector{
    
    private final Group group;
    
    public RemoveGroupCommand(FacetModeller con, Group g, boolean removeObjects) {
        super(con,"Remove Group");
        group = g;
        // Create the remove nodes/facets/regions commands:
        if (removeObjects) {
            ModelManager mod = con.getModelManager();
            add( new RemoveNodeCommandVector(mod,g.getNodes(),"") );
            add( new RemoveFacetCommandVector(mod,g.getFacets(),"") );
            add( new RemoveRegionCommandVector(mod,g.getRegions()) );
        }
    }
    
    @Override
    public void execute() {
        // Remove the nodes/facets/regions associated with the group:
        super.execute();
        // Remove the group from the group vector:
        controller.removeGroup(group);
        // Update the graphical selector objects:
        controller.updateGroupSelectors();
    }
    
    @Override
    public void undo() {
        new AddGroupCommand(controller,group,0).execute();
    }
    
}
