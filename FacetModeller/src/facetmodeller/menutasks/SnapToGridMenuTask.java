package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.CommandVector;
import facetmodeller.sections.Section;

/** 
 * @author Peter
 */
public final class SnapToGridMenuTask extends ControlledMenuTask {
    
    private final boolean doH, doV;
    
    public SnapToGridMenuTask(FacetModeller con,boolean bH, boolean bV) {
        super(con);
        doH = bH;
        doV = bV;
    }
    
    @Override
    public String text() {
        if (doH && doV) {
            return "Snap nodes to grid";
        } else if (doH) {
            return "Snap nodes to grid horizontally";
        } else if (doV) {
            return "Snap nodes to grid vertically";
        } else {
            return ""; // shouldn't happen
        }
    }

    @Override
    public String tip() { return "Any nodes within the picking/snapping distance from a grid are moved to the grid points."; }

    @Override
    public String title() { return "Snap to Grid"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if ( currentSection == null ) { return false; }
        return ( controller.hasNodes() && currentSection.canNodesShift() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the snapping distance:
        String response = Dialogs.input(controller,"Enter the grid separation (spatial units):",title());
        // Check response:
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numeric value. Please try again.","Error");
            return;
        }
        double d;
        try {
            d = Double.parseDouble(ss[0].trim());
            if (d<=0.0) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive value. Please try again.","Error");
            return;
        }
        // Ask which node groups to apply the snapping to:
        int which = Dialogs.question(controller,"Which groups of nodes?",title(),"All","Current","Cancel","All");
        if (which==Dialogs.CANCEL_OPTION) { return; }
        // Perform the snapping:
        CommandVector commands;
        if (which==Dialogs.YES_OPTION) { // all groups
            commands = controller.snapToGrid(d,null,doH,doV);
        } else { // selected groups
            commands = controller.snapToGrid(d,controller.getSelectedCurrentGroups(),doH,doV);
        }
        commands.setName(title());
        controller.undoVectorAdd(commands); // (the commands have already been executed)
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
