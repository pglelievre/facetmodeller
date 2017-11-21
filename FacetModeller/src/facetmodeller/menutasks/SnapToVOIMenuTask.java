package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.CommandVector;

/** 
 * @author Peter
 */
public final class SnapToVOIMenuTask extends ControlledMenuTask {
    
    private boolean doH, doV;
    
    public SnapToVOIMenuTask(FacetModeller con,boolean bH, boolean bV) {
        super(con);
        doH = bH;
        doV = bV;
    }
    
    @Override
    public String text() {
        if (doH && doV) {
            return "Snap nodes to VOI";
        } else if (doH) {
            return "Snap nodes to VOI horizontally";
        } else if (doV) {
            return "Snap nodes to VOI vertically";
        } else {
            return ""; // shouldn't happen
        }
    }

    @Override
    public String tip() { return "Any nodes within the picking/snapping distance of the VOI boundary are moved onto the VOI boundary."; }

    @Override
    public String title() { return "Snap to VOI"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask which node groups to apply the snapping to:
        int which = Dialogs.question(controller,"Which groups of nodes?",title(),"Current","Displayed","Cancel","Current");
        if (which==Dialogs.CANCEL_OPTION) { return; }
        // Perform the snapping:
        CommandVector commands;
        double d = controller.getPickingDistance();
        if (which==Dialogs.YES_OPTION) { // current group
            commands = controller.snapToVOI(d,controller.getSelectedCurrentGroups(),doH,doV);
        } else { // displayed groups
            commands = controller.snapToVOI(d,controller.getSelectedNodeGroups(),doH,doV);
            //commands = model.snapToVOI(pickingDistance,null,doH,doV); // all groups
        }
        commands.setName(title());
        controller.undoVectorAdd(commands); // (the commands have already been executed)
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
