package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.FacetVector;

/** Command to perform an edge flip.
 * @author Peter
 */
public final class FlipEdgeCommand extends ModelCommandVector {
    
    public FlipEdgeCommand(ModelManager mod, FacetVector oldFacets, FacetVector newFacets) {
        super(mod,"Perform Edge Flip");
        add( new AddFacetCommandVector(mod,newFacets) );
        add( new RemoveFacetCommandVector(mod,oldFacets,"") );
    }
    
}
