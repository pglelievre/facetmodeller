package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.Command;

/** 
 * @author Peter
 */
public final class UndoPreviousCommandMenuTask extends ControlledMenuTask {
    
    public UndoPreviousCommandMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Undo"; }

    @Override
    public String tip() { return "Reverses the most recent command"; }

    @Override
    public String title() { return "Undo"; }

    @Override
    public boolean check() { return !controller.getUndoIsEmpty(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the previous command:
        Command command = controller.undoVectorGet();
        // Check we can undo:
        if (command==null) {
            Dialogs.inform(controller,"There is nothing to undo.",title());
            return;
        }
        // Ask for confirmation:
        String prompt = "This will undo the most recent \"" + command.getName() + "\" command.";
        int response = Dialogs.continueCancel(controller,prompt,title());
        if (response!=Dialogs.OK_OPTION) { return; }
        // Remove the previous command from the undo information vector:
        controller.undoVectorRemove();
        // Undo the command:
        command.undo();
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
