package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;

/** Command to perform several RemoveFacet commands.
 * @author Peter
 */
public final class RemoveFacetCommandVector extends ModelCommandVector {
    
    public RemoveFacetCommandVector(ModelManager mod, FacetVector facets, String t) {
        super(mod,t);
        // Create all the individual remove facet commands:
        for (int i=0 ; i<facets.size() ; i++ ) {
            Facet f = facets.get(i);
            add( new RemoveFacetCommand(mod,f) );
        }
    }
    
}
