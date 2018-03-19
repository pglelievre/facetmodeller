package facetmodeller.commands;

import facetmodeller.groups.Group;
import facetmodeller.plc.Facet;

/** Command to change a facet's group membership.
 * @author Peter
 */
public final class ChangeFacetGroupCommand extends Command {
    
    private final Facet facet; // the facet changed
    private final Group oldGroup; // the old group (existing facet group on construction)
    private final Group newGroup; // the new group
    
    public ChangeFacetGroupCommand(Facet f, Group g) {
        super("Change Facet Group");
        facet = f;
        oldGroup = facet.getGroup();
        newGroup = g;
    }
    
    @Override
    public void execute() {
        // Remove the facet from its existing group:
        //facet.getGroup().removeFacet(facet);
        oldGroup.removeFacet(facet);
        // Change the group membership of the facet:
        facet.setGroup(newGroup);
        // Add the facet to its new group:
        //facet.getGroup().addFacet(facet);
        newGroup.addFacet(facet);
    }
    
    @Override
    public void undo() {
        // Remove the facet from its new group:
        newGroup.removeFacet(facet);
        // Change the group membership of the facet back to its old group:
        facet.setGroup(oldGroup);
        // Add the facet to its old group:
        oldGroup.addFacet(facet);
    }
    
}
