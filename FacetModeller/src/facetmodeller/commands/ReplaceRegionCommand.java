package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;

/** Command to remove some regions and add another.
 * @author Peter
 */
public final class ReplaceRegionCommand extends ModelCommandVector {
    
    public ReplaceRegionCommand(ModelManager mod, RegionVector rv, Region r) {
        super(mod,"Add Region");
        add( new RemoveRegionCommandVector(mod,rv) );
        add( new AddRegionCommand(mod,r) );
    }
    
}
