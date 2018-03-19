package facetmodeller.commands;

import facetmodeller.ModelManager;
import facetmodeller.plc.Region;

/** Command to remove a region.
 * @author Peter
 */
public final class RemoveRegionCommand extends ModelCommand {
    
    private final Region region; // the region removed
    
    public RemoveRegionCommand(ModelManager mod, Region r) {
        super(mod,"Remove Region");
        region = r;
    }
    
    @Override
    public void execute() {
        if (region==null) { return; }
        // Remove the region from the PLC:
        model.removeRegion(region);
        // Remove the region from the section that holds it:
        region.getSection().removeRegion(region);
        // Remove the region from the group that holds it:
        region.getGroup().removeRegion(region);
    }
    
    @Override
    public void undo() {
        // Add the region:
        new AddRegionCommand(model,region).execute();
    }
    
}
