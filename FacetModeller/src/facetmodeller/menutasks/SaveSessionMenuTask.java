package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.SessionSaver;
import facetmodeller.filters.SessionFilter;
import fileio.FileUtils;
import java.io.File;

public final class SaveSessionMenuTask extends ControlledMenuTask {
    
    private final boolean saveAs;
    //private boolean saved=false; // set to true once a session has been saved successfully
    
    public SaveSessionMenuTask(FacetModeller con, boolean b) {
        super(con);
        saveAs = b;
    }

    @Override
    public String text() {
        if (saveAs) {
            return "Save session as";
        } else {
            return "Save session";
        }
    }

    @Override
    public String tip() {
        if (saveAs) {
            return "Save the current session to a specified file";
        } else {
            return "Save the current session, overwriting the existing file";
        }
    }

    @Override
    public String title() {
        if (saveAs) {
            return "Save Session As";
        } else {
            return "Save Session";
        }
    }

    @Override
    public boolean check() {
        return ( controller.hasSections() || controller.hasGroups() || controller.hasVOI() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the file name for saving if required:
        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.
        boolean saved=controller.getSaved();
        boolean ok = controller.chooseSaveSession(title(),(saveAs || !saved));
        if (!ok) { return; }
        // Give the file the .fms extension:
        File sessionFile = controller.getSessionFile();
        String root = FileUtils.getRoot(sessionFile);
        sessionFile = new File( root + "." + SessionFilter.FMS );
        controller.setSessionFile(sessionFile);
        // Check for file overwrite:
        if (sessionFile.exists()) {
            int response = Dialogs.confirm(controller,"Overwrite the existing file?",title());
            if (response != Dialogs.OK_OPTION) { return; }
        }
        // Reset the id's:
        controller.resetIDs();
        // Save ascii file:
        ok = SessionSaver.saveSessionAscii(controller,sessionFile);
        // Display message indicating success for failure:
        if (ok) {
            Dialogs.inform(controller,"Session saved successfully.",title());
        } else {
            Dialogs.error(controller,"Failed to save session.",title());
            return;
        }
        // Set the saved flag to true:
        controller.setSaved(true);
        // Set the window title bar to include the name of the session:
        controller.resetTitle();
    }
    
}
