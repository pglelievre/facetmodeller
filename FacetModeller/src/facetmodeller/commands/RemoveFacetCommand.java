package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Facet;

/** Command to remove a node.
 * @author Peter
 */
public final class RemoveFacetCommand extends ModelCommand {
    
    private Facet facet; // the facet removed
    
    public RemoveFacetCommand(ModelManager mod, Facet f) {
        super(mod,"Remove Facet");
        facet = f;
    }
    
    @Override
    public void execute() {
        // Remove the facet from the PLC:
        model.removeFacet(facet);
        // Remove the facet from the sections that hold it:
        facet.getSections().removeFacet(facet);
        // Remove the facet from the group that holds it:
        facet.getGroup().removeFacet(facet);
        // Remove the facet from its nodes:
        facet.getNodes().removeFacet(facet);
    }
    
    @Override
    public void undo() {
        // Add the facet:
        new AddFacetCommand(model,facet).execute();
    }
    
}
