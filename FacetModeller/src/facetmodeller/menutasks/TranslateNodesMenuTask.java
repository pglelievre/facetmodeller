package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.CommandVector;
import geometry.MyPoint3D;

/** 
 * @author Peter
 */
public final class TranslateNodesMenuTask extends ControlledMenuTask {
    
    public TranslateNodesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Translate nodes"; }

    @Override
    public String tip() { return "Translates (moves) nodes spatially"; }

    @Override
    public String title() { return "Translate Nodes"; }

    @Override
    public boolean check() {
        return ( controller.hasNodes() && controller.is3D() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for confirmation:
//        if (controller.getShowConfirmationDialogs()) {
//            String message = "Any on-section nodes will be changed to off-section nodes. Do you want to continue?";
//            int response = Dialogs.yesno(controller,message,title());
//            if ( response != Dialogs.YES_OPTION ) { return; }
//        }
        // Ask which nodes to translate:
        int which = Dialogs.question(controller,"Which groups of nodes?",title(),"All","Current","Cancel","Current");
        if (which==Dialogs.CANCEL_OPTION) { return; }
        // Ask for the translation information:
        String response = Dialogs.input(controller,"Enter the translation (x,y,z):",title());
        // Check response:
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=3) {
            Dialogs.error(controller,"You must enter three numerical values. Please try again.","Error");
            return;
        }
        double x,y,z;
        try {
            x = Double.parseDouble(ss[0].trim());
            y = Double.parseDouble(ss[1].trim());
            z = Double.parseDouble(ss[2].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(controller,"You must enter a three numerical values. Please try again.","Error");
            return;
        }
        MyPoint3D p = new MyPoint3D(x,y,z);
        CommandVector commands;
        if (which==Dialogs.YES_OPTION) { // all groups
            commands = controller.translateNodes(p,null);
        } else { // selected groups
            commands = controller.translateNodes(p,controller.getSelectedCurrentGroups());
        }
        commands.setName(title());
        controller.undoVectorAdd(commands); // (the commands have already been executed)
        // Repaint:
        controller.redraw();
    }
    
}
