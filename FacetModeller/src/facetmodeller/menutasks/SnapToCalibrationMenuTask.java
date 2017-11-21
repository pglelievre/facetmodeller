package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.CommandVector;
import facetmodeller.sections.Section;

/** 
 * @author Peter
 */
public final class SnapToCalibrationMenuTask extends ControlledMenuTask {
    
    private boolean doH, doV;
    
    public SnapToCalibrationMenuTask(FacetModeller con,boolean bH, boolean bV) {
        super(con);
        doH = bH;
        doV = bV;
    }
    
    @Override
    public String text() {
        if (doH && doV) {
            return "Snap nodes to calibration points";
        } else if (doH) {
            return "Snap nodes to calibration points horizontally";
        } else if (doV) {
            return "Snap nodes to calibration points vertically";
        } else {
            return ""; // shouldn't happen
        }
    }

    @Override
    public String tip() { return "Any nodes within the picking/snapping distance of the calibration points are moved to those locations."; }

    @Override
    public String title() { return "Snap to Calibration"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if ( currentSection == null ) { return false; }
        return ( controller.hasNodes() && currentSection.canNodesShift() && currentSection.isCalibrated() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask which node groups to apply the snapping to:
        int which = Dialogs.question(controller,"Which groups of nodes?",title(),"Current","All","Cancel","Current");
        if (which==Dialogs.CANCEL_OPTION) { return; }
        // Perform the snapping:
        CommandVector commands;
        double d = controller.getPickingDistance();
        if (which==Dialogs.YES_OPTION) { // current group
           commands = controller.snapToCalibration(d,controller.getSelectedCurrentGroups(),doH,doV);
        } else { // all groups
           commands = controller.snapToCalibration(d,null,doH,doV);
        }
        commands.setName(title());
        controller.undoVectorAdd(commands); // (the commands have already been executed)
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
