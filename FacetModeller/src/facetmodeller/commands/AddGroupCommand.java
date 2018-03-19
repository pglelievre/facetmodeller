package facetmodeller.commands;

import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.groups.Group;

/** Command to add a group.
 * @author Peter
 */
public final class AddGroupCommand extends ControlledCommandVector {
    
    private final Group group;
    private final int index;
    
    public AddGroupCommand(FacetModeller con, Group g, int i) {
        super(con,"Add Group");
        group = g;
        index = i;
        // Create the add nodes/facets/regions commands:
        ModelManager mod = con.getModelManager();
        add( new AddNodeCommandVector(mod,g.getNodes(),"") );
        add( new AddFacetCommandVector(mod,g.getFacets()) );
        add( new AddRegionCommandVector(mod,g.getRegions()) );
    }
    
    @Override
    public void execute() {
        // Remove the nodes/facets/regions associated with the group:
        super.execute();
        // Add the group to the group vector:
        if (index<=0) {
            controller.addGroup(group);
        } else {
            controller.addGroup(group,index);
        }
        // Update the graphical selector objects:
        controller.updateGroupSelectors();
    }
    
    @Override
    public void undo() {
        new RemoveGroupCommand(controller,group,true).execute();
    }
    
}
