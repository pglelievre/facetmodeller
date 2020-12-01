package facetmodeller.commands;

import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;

/** Command to perform several ChangeFacetGroup commands.
 * @author Peter
 */
public final class ChangeFacetMarkerCommandVector extends CommandVector {
    
    public ChangeFacetMarkerCommandVector(FacetVector facets, boolean b, String t) {
        super(t);
        // Create all the individual change facet marker commands:
        for (int i=0 ; i<facets.size() ; i++ ) {
            Facet f = facets.get(i);
            add( new ChangeFacetMarkerCommand(f,b) );
        }
    }
    
}
