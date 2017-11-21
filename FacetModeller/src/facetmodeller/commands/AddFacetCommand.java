package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Facet;

/** Command to add a facet.
 * @author Peter
 */
public final class AddFacetCommand extends ModelCommand {
    
    private Facet facet; // the facet added
    
    public AddFacetCommand(ModelManager mod, Facet f) {
        super(mod,"Add Facet");
        facet = f;
    }
    
    @Override
    public void execute() {
        if (facet==null) { return; }
        // Add the facet to the PLC:
        model.addFacet(facet);
        // Add the facet to the sections that the facet is in:
        facet.getSections().addFacet(facet);
        // Add the facet to the group that the facet is in:
        facet.getGroup().addFacet(facet);
        // Add the facet to each node in the facet:
        facet.getNodes().addFacet(facet);
    }
    
    @Override
    public void undo() {
        // Remove the facet:
        new RemoveFacetCommand(model,facet).execute();
    }
    
}
