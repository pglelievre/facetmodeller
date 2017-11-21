package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.filters.View3DFilter;
import fileio.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import javax.swing.JFileChooser;

/** Loads 3D view angles from an ascii file.
 * @author Peter
 */
public final class LoadView3DMenuTask extends ControlledMenuTask {
    
    public LoadView3DMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Load view"; }

    @Override
    public String tip() { return "Load 3D view angles from a file"; }

    @Override
    public String title() { return "Load View"; }

    @Override
    public boolean check() { return controller.is3D(); }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Ask for the file:
        JFileChooser chooser = new JFileChooser();
        View3DFilter filter = new View3DFilter();
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(controller.getOpenDirectory());
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        int response = chooser.showOpenDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();
        if (file==null) { return; }
        
        // Set the load directory to the chosen directory:
        File dir = chooser.getCurrentDirectory();
        controller.setOpenDirectory(dir);

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) {
            Dialogs.error(controller,"Failed to open the file.",title());
            return;
        }
        
        // Read the ascii file:
        String msg = controller.getProjector3D().getRotationMatrix().readSessionInformation(reader,false);

        // Close the file:
        FileUtils.close(reader);
        
        // Check for error:
        if (msg==null) {
            Dialogs.inform(controller,"File read successfully.",title());
        } else {
            Dialogs.error(controller, "Failed to read the file.\n" + msg.trim() ,title());
        }
        
        // Redraw the 3D view:
        controller.redraw3D();
        
    }
    
}
