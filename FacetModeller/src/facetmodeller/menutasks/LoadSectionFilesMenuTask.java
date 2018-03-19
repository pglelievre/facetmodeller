package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import filters.ImageFilter;
import java.io.File;
import javax.swing.JFileChooser;

/** Asks for the list of section image files and loads them.
 * @author Peter
 */
public final class LoadSectionFilesMenuTask extends ControlledMenuTask {
    
    private final boolean iscross;
    
    public LoadSectionFilesMenuTask(FacetModeller con, boolean b) {
        super(con);
        iscross = b;
    }

    @Override
    public String text() {
        String t = "Load";
        if (iscross) {
            t += " cross";
        } else {
            t += " depth";
        }
        t += " section";
        if (controller.is3D()) {
            t += " files";
        } else {
            t += " file"; // only a single section for 2D
        }
        return t;
    }

    @Override
    public String tip() {
        String t = "Load the image";
        if (controller.is3D()) {
            t += " files";
        } else {
            t += " file"; // only a single section for 2D
        }
        t += " containing the";
        if (iscross) {
            t += " cross";
        } else {
            t += " depth";
        }
        t += " section";
        if (controller.is3D()) { t += "s"; }
        t += " to digitize";
        return t;
    }

    @Override
    public String title() {
        String t = "Load";
        if (iscross) {
            t += " Cross";
        } else {
            t += " Depth";
        }
        t += " Section";
        if (controller.is3D()) {
            t += " Files";
        } else {
            t += " File"; // only a single section for 2D
        }
        return t;
    }

    @Override
    public boolean check() { return true; };

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Create confirmation dialog if required:
        int response;
        int numberOfSections = controller.numberOfSections(); // size before adding
        boolean overwrite = true; // overwrite sections already in the list or just add new ones?
        boolean is3D = controller.is3D();
        if (numberOfSections!=0) { // sections exist
            if (is3D) {
                String prompt = "There are already sections. How do you want to continue?";
                response = Dialogs.question(controller,prompt,title(),"Overwrite","Insert","Cancel");
                // Check answer:
                switch (response) {
                    case Dialogs.YES_OPTION:
                        overwrite = true;
                        break;
                    case Dialogs.NO_OPTION:
                        overwrite = false;
                        break;
                    default:
                        return; // user cancelled
                }
            } else {
                String prompt = "There is already a section loaded. Do you want to replace it?";
                response = Dialogs.confirm(controller,prompt,title());
                // Check answer:
                if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
                overwrite = true;
            }
        }
        if (overwrite) { numberOfSections=0; } // important for logic below involving this variable
        
        // Ask for the list of files:
        JFileChooser chooser = new JFileChooser();
        ImageFilter imageFilter = new ImageFilter();
        chooser.setCurrentDirectory(controller.getOpenDirectory());
        chooser.addChoosableFileFilter(imageFilter);
        chooser.setFileFilter(imageFilter);
        //chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(is3D);
        response = chooser.showOpenDialog(controller);

        // Check response and get the list of files:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File[] files;
        if (is3D) {
            files = chooser.getSelectedFiles();
        } else {
            files = new File[1];
            files[0] = chooser.getSelectedFile();
        }

        // Check response:
        if (files==null) { return; }
        
        // Check if any files are large:
        for (File file : files) {
            if (file.length() >= 1000000) { // 1Mb
                response = Dialogs.continueCancel(controller,"Large files detected: this may slow the GUI.",title());
                if (response != Dialogs.OK_OPTION ) { return; }
            }
        }

        // Set the load directory to the chosen directory:
        File dir = chooser.getCurrentDirectory();
        controller.setOpenDirectory(dir);
        
        // If the save directory is not yet set then set it that chosen:
        if (controller.getSaveDirectory()==null) {
            controller.setSaveDirectory(dir);
        }

        // Clear the section list and plc if required (if overwriting sections):
        if (overwrite) {
            controller.clearSections();
            controller.clearPLC();
        }

        // Insert new sections into the section list:
        controller.addSectionsFromFiles(files,iscross); // adds sections to the vector

        // Update the clickable lists:
        controller.updateSectionSelectors();
        controller.setSelectedCurrentSectionIndex(numberOfSections); // the first new section becomes the current section
        controller.clearOtherSectionSelection(); // no other section selected

        // Check that at least one section was loaded:
        numberOfSections = controller.numberOfSections() - numberOfSections; // = the number of sections loaded successfully
        if (numberOfSections==0) {

            // Display unsuccessful load:
            Dialogs.error(controller,"No sections loaded successfully.",title());

        } else {
        
            // Enable or disable menu items:
            controller.checkItemsEnabled();
            // Repaint:
            controller.redraw();

            // Display successful load:
            if (numberOfSections==files.length) {
                Dialogs.inform(controller,"All files loaded successfully.",title());
            } else {
                String t = numberOfSections + " of " + files.length + " files loaded successfully.";
                Dialogs.warning(controller,t,title());
            }

        }

    }
    
}
