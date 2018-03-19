package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Region;

/** Command to add a region.
 * @author Peter
 */
public final class AddRegionCommand extends ModelCommand {
    
    private final Region region; // the region added
    
    public AddRegionCommand(ModelManager mod, Region r) {
        super(mod,"Add Region");
        region = r; // the added region should be fully defined on construction (attached to section and group)
    }
    
    @Override
    public void execute() {
        if (region==null) { return; }
        // Add the region to the PLC:
        model.addRegion(region);
        // Add the region to the section that the region is in:
        region.getSection().addRegion(region);
        // Add the region to the group that the region is in:
        region.getGroup().addRegion(region);
    }
    
    @Override
    public void undo() {
        // Remove the region:
        new RemoveRegionCommand(model,region).execute();
    }
    
}
