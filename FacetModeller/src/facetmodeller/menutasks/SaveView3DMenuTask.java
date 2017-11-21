package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.filters.View3DFilter;
import fileio.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import javax.swing.JFileChooser;

/** Saves 3D view angles to an ascii file.
 * @author Peter
 */
public final class SaveView3DMenuTask extends ControlledMenuTask {
    
    public SaveView3DMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Save view"; }

    @Override
    public String tip() { return "Save the current 3D view angles to a file"; }

    @Override
    public String title() { return "Save View"; }

    @Override
    public boolean check() { return controller.is3D(); }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Ask for the file name for saving if required:
        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.
        JFileChooser chooser = new JFileChooser();
        View3DFilter filter = new View3DFilter();
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        File dir = controller.getSaveDirectory();
        if (dir!=null) {
            chooser.setCurrentDirectory(dir);
        }
        int response = chooser.showSaveDialog(controller);
        
        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();
        if (file==null) { return; }
        
        // Set the save directory to the chosen directory:
        dir = chooser.getCurrentDirectory();
        controller.setSaveDirectory(dir);
        
        // Give the file the .fmv extension:
        String root = FileUtils.getRoot(file);
        file = new File( root + "." + View3DFilter.FMV );
        
        // Check for file overwrite:
        if (file.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title());
            if (response != Dialogs.OK_OPTION) { return; }
        }

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) {
            Dialogs.error(controller,"Failed to save the file.",title());
            return;
        }
        
        // Write the ascii file:
        boolean ok = controller.getProjector3D().getRotationMatrix().writeSessionInformation(writer);

        // Close the file:
        FileUtils.close(writer);

        // Display message if error occurred:
        if (!ok) {
            Dialogs.error(controller,"Failed to save the file.",title());
        }
        
    }
    
}
