package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.groups.GroupVector;
import java.io.File;
import javax.swing.JFileChooser;

/** Loads group definitions from an ascii file.
 * @author Peter
 */
public final class LoadGroupDefinitionsMenuTask extends ControlledMenuTask {
    
    public LoadGroupDefinitionsMenuTask(FacetModeller con) { super(con); }
    

    @Override
    public String text() { return "Load group definitions"; }

    @Override
    public String tip() { return "Loads (reads) group definitions from an ascii file"; }

    @Override
    public String title() { return "Load Group Definitions"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Create confirmation dialog if required:
        int response;
        if (controller.hasGroups()) {
            String prompt = "This will add the groups to those that currently exist. Do you wish to continue?";
            response = Dialogs.yesno(controller,prompt,title());
            // Check answer:
            if (response != Dialogs.YES_OPTION) { return; }
        }
        
        // Ask for the file:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(controller.getOpenDirectory());
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        response = chooser.showOpenDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();
        if (file==null) { return; }
        
        // Set the load directory to the chosen directory:
        File loadDirectory = chooser.getCurrentDirectory();
        controller.setOpenDirectory(loadDirectory);

        // Load the group definitions from the file:
        GroupVector newGroups = new GroupVector();
        String err = newGroups.readFile(file);
        
        // Check for error reading the file:
        if (err!=null) {
            // Display error message:
            Dialogs.error(controller,err,title());
            return;
        }
        
        // Get the currently selected node and facet groups so we can maintain the selections:
        int currentGroupIndex = Math.max(controller.getSelectedCurrentGroupIndex() , 0 );
        int[] nodeGroupIndices = controller.getSelectedNodeGroupIndices();
        int[] facetGroupIndices = controller.getSelectedFacetGroupIndices();
        
        // Add the new groups to the list of groups:
        controller.addGroups(newGroups);
        
        // Update the graphical selector objects:
        controller.updateGroupSelectors();
        controller.setSelectedCurrentGroupIndex(currentGroupIndex);
        controller.setSelectedNodeGroupIndices(nodeGroupIndices);
        controller.setSelectedFacetGroupIndices(facetGroupIndices);
        
        // Display successful load:
        Dialogs.inform(controller,"Group definitions loaded successfully.",title());
        
    }
    
}
