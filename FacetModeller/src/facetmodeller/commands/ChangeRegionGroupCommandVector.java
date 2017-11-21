package facetmodeller.commands;

import facetmodeller.groups.Group;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;

/** Command to perform several ChangeRegionGroup commands.
 * @author Peter
 */
public final class ChangeRegionGroupCommandVector extends CommandVector {
    
    public ChangeRegionGroupCommandVector(RegionVector regions, Group g) {
        super("Change Regions' Group");
        // Create all the individual change region group commands:
        for (int i=0 ; i<regions.size() ; i++ ) {
            Region n = regions.get(i);
            add( new ChangeRegionGroupCommand(n,g) );
        }
    }
    
}
