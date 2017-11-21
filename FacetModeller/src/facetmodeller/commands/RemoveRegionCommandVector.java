package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;

/** Command to perform several RemoveRegion commands.
 * @author Peter
 */
public final class RemoveRegionCommandVector extends ModelCommandVector {
    
    public RemoveRegionCommandVector(ModelManager mod, RegionVector regions) {
        super(mod,"Remove Regions");
        // Create all the individual remove region commands:
        for (int i=0 ; i<regions.size() ; i++ ) {
            Region r = regions.get(i);
            add( new RemoveRegionCommand(mod,r) );
        }
    }
    
}
