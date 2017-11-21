package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;

/** Command to perform several AddRegion commands.
 * @author Peter
 */
public final class AddRegionCommandVector extends ModelCommandVector {
    
    public AddRegionCommandVector(ModelManager mod, RegionVector regions) {
        super(mod,"Add Regions");
        // Create all the individual add region commands:
        for (int i=0 ; i<regions.size() ; i++ ) {
            Region r = regions.get(i);
            add( new AddRegionCommand(mod,r) );
        }
    }
    
}
