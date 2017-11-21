package facetmodeller.commands;

import facetmodeller.groups.Group;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;

/** Command to perform several ChangeFacetGroup commands.
 * @author Peter
 */
public final class ChangeFacetGroupCommandVector extends CommandVector {
    
    public ChangeFacetGroupCommandVector(FacetVector facets, Group g, String t) {
        super(t);
        // Create all the individual change facet group commands:
        for (int i=0 ; i<facets.size() ; i++ ) {
            Facet f = facets.get(i);
            add( new ChangeFacetGroupCommand(f,g) );
        }
    }
    
}
