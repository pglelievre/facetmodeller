package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import java.io.File;
import javax.swing.JFileChooser;

/** Saves group definitions to an ascii file.
 * @author Peter
 */
public final class SaveGroupDefinitionsMenuTask extends ControlledMenuTask {
    
    public SaveGroupDefinitionsMenuTask(FacetModeller con) { super(con); }
    

    @Override
    public String text() { return "Save group definitions"; }

    @Override
    public String tip() { return "Saves (writes) group definitions to an ascii file"; }

    @Override
    public String title() { return "Save Group Definitions"; }

    @Override
    public boolean check() { return controller.hasGroups(); }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Create confirmation dialog if required:
        int response;
        if (controller.hasGroups()) {
            String prompt = "This will write the drawing colours for the nodes only. Do you wish to continue?";
            response = Dialogs.yesno(controller,prompt,title());
            // Check answer:
            if (response != Dialogs.YES_OPTION) { return; }
        }
        
        // Ask for the file name for saving if required:
        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        File dir = controller.getSaveDirectory();
        if (dir!=null) {
            chooser.setCurrentDirectory(dir);
        }
        response = chooser.showSaveDialog(controller);
        
        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();
        if (file==null) { return; }
        
        // Set the save directory to the chosen directory:
        dir = chooser.getCurrentDirectory();
        controller.setSaveDirectory(dir);
        
        // Check for file overwrite:
        if (file.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title());
            if (response != Dialogs.OK_OPTION) { return; }
        }
        
        // Write the group definitions to the file:
        boolean ok = controller.writeGroupDefinitions(file);

        // Display message of success or error:
        if (ok) {
            Dialogs.inform(controller,"Group definitions saved successfully.",title());
        } else {
            Dialogs.error(controller,"Failed to save the file.",title());
        }
        
    }
    
}
