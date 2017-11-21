package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddRegionCommand;
import facetmodeller.commands.Command;
import facetmodeller.commands.ReplaceRegionCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import geometry.MyPoint2D;

public final class DefineRegionClickTask extends ControlledClickTask {
    
    public DefineRegionClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_ADD_REGIONS; }

    @Override
    public String tip() { return "Add new region points on the current section"; }

    @Override
    public String title() { return ClickTaskUtil.DEFINE_REGION_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        return (controller.getSelectedCurrentGroup()!=null);
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Get the current group:
        Group group = controller.getSelectedCurrentGroup();
        // Check if regions already exist in the current group and ask user what to do:
        boolean deleteRegions = false;
        if (group.numberOfRegions()!=0) {
            String message = "Do you want to delete the current region(s) in the group?";
            int response = Dialogs.question(controller,message,title());
            if (response==Dialogs.CANCEL_OPTION) { return; }
            if (response==Dialogs.YES_OPTION) {
                deleteRegions = true;
            }
        }
        // Ask if this is a regular region point or a control point:
        int response = Dialogs.question(controller,"What type of point is this?",title(),"Region","Control","Cancel");
        boolean isCon;
        switch (response) {
            case Dialogs.YES_OPTION:
                // region point
                isCon = false;
                break;
            case Dialogs.NO_OPTION:
                // control point
                isCon = true;
                break;
            default:
                // user cancelled
                return;
        }
        // Create a new region object linked to the current section and current group:
        Region newRegion = new Region(isCon,p,controller.getSelectedCurrentSection(),group);
        // Add the region, deleting the current regions if requested:
        Command com;
        if (deleteRegions) {
            RegionVector regions = group.getRegions();
            com = new ReplaceRegionCommand(controller.getModelManager(),regions,newRegion);
        } else {
            com = new AddRegionCommand(controller.getModelManager(),newRegion);
        }
        com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
