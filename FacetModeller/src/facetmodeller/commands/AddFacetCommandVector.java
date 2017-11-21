package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;

/** Command to perform several AddFacet commands.
 * @author Peter
 */
public final class AddFacetCommandVector extends ModelCommandVector {
    
    public AddFacetCommandVector(ModelManager mod, FacetVector facets) {
        super(mod,"Add Facets");
        // Create all the individual add facet commands:
        for (int i=0 ; i<facets.size() ; i++ ) {
            Facet n = facets.get(i);
            add( new AddFacetCommand(mod,n) );
        }
    }
    
}
