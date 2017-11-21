package fileio;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/** Use this in applications that need to keep track of open/save directories and names.
 * @author Peter
 */
public class OpenAndSave {
    
    // ------------------ Properties ------------------
    
    // Working directories:
    private File openDirectory = null; // the directory to open files from
    private File saveDirectory = null; // the directory to save files to
    private File sessionFile = null; // the file to save a session to
    
    // ------------------ Constructor ------------------
    
    public OpenAndSave() {}
    
    // ------------------ Getters ------------------
    
    public File getOpenDirectory() { return openDirectory; }
    public File getSaveDirectory() { return saveDirectory; }
    public File getSessionFile() { return sessionFile; }
    
    // ------------------ Setters ------------------
    
    public void setOpenDirectory(File f) {
        openDirectory = f;
        // If the saveDirectory is still null then set it to the openDirectory:
        if (getSaveDirectory()==null) { saveDirectory = f; }
    }
    public void setSaveDirectory(File f) {
        saveDirectory = f;
        // If the openDirectory is still null then set it to the saveDirectory:
        if (openDirectory==null) { openDirectory = f; }
    }
    public void setSessionFile(File f) {
        sessionFile = f;
    }
    
    // -------------------- Public methods --------------------

    public boolean openSession(JFrame frame, String title, FileFilter filter) {
        // Ask for the name of the saved session file:
        JFileChooser chooser = new JFileChooser();
        if (sessionFile==null) {
            chooser.setCurrentDirectory(getOpenDirectory());
        } else {
            chooser.setSelectedFile(sessionFile);
        }
        if (filter!=null) {
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
        }
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        int response = chooser.showOpenDialog(frame);
        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return false; }
        sessionFile = chooser.getSelectedFile();
        // Check response:
        if (getSessionFile()==null) { return false; }
        // Set the openDirectory to the chosen directory:
        File f = chooser.getCurrentDirectory();
        setOpenDirectory(f);
        // If the saveDirectory has not yet been set then set it to the openDirectory:
        if (saveDirectory==null) { setSaveDirectory(f); }
        // Return successfully:
        return true;
    }

    public boolean saveSession(JFrame frame, String title, FileFilter filter, boolean saveAs) {
        // Ask for the file name for saving if required:
        if ( !saveAs && getSessionFile()!=null ) { return true; }
        JFileChooser chooser = new JFileChooser();
        if (sessionFile==null) {
            chooser.setCurrentDirectory(getSaveDirectory());
        } else {
            chooser.setSelectedFile(sessionFile);
        }
        if (filter!=null) {
            // The filter doesn't seem to be working, but it is actually doing what it should
            // according to Mac look-and-feel guidelines, which I think are pretty silly here.
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
        }
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            chooser.setSelectedFile(file);
        }
        int response = chooser.showSaveDialog(frame);
        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return false; }
        sessionFile = chooser.getSelectedFile();
        // Check response:
        if (getSessionFile()==null) { return false; }
        // Set the save directory to the chosen directory:
        File f = chooser.getCurrentDirectory();
        setSaveDirectory(f);
        // If the openDirectory has not yet been set then set it to the saveDirectory:
        if (openDirectory==null) { setOpenDirectory(f); }
        // Return successfully:
        return true;
    }
    
}
