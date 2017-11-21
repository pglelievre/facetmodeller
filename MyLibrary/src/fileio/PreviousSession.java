package fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/** Manages file reading and writing tasks.
 * @author Peter
 */
public class PreviousSession extends OpenAndSave {
    
    // ------------------ Properties ------------------
    
    private final File previousSessionFile;
    
    // ------------------ Constructor ------------------
    
    public PreviousSession(File file) {
        previousSessionFile = file;
    }
    
    // -------------------- Public methods --------------------
    
    public void readPreviousSessionFile() {
        // Check file exists:
        if (!previousSessionFile.exists()) { return; }
        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(previousSessionFile);
        if (reader==null) { return; }
        // Read and set the open directory:
        File f;
        String t = FileUtils.readLine(reader);
        if (t==null) { return; }
        if (!t.equals("null")) {
            try {
                URI uri = new URI(t);
                f = new File(uri);
            } catch (URISyntaxException e) {
                FileUtils.close(reader);
                return;
            }
            if (f.exists()) {
                setOpenDirectory(f);
            }
        }
        // Read and set the save directory:
        t = FileUtils.readLine(reader);
        if (t==null) { return; }
        if (!t.equals("null")) {
            try {
                URI uri = new URI(t);
                f = new File(uri);
            } catch (URISyntaxException e) {
                FileUtils.close(reader);
                return;
            }
            if (f.exists()) {
                setSaveDirectory(f);
            }
        }
        // Read and set the previous jms file:
        t = FileUtils.readLine(reader);
        if (t==null) { return; }
        if (!t.equals("null")) {
            try {
                URI uri = new URI(t);
                f = new File(uri);
            } catch (URISyntaxException e) {
                FileUtils.close(reader);
                return;
            }
            if (f.exists()) {
                setSessionFile(f);
            }
        }
        // Close the file:
        FileUtils.close(reader);
    }
    
    public void writePreviousSessionFile() {
        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(previousSessionFile);
        if (writer==null) { return; }
        String t1,t2,t3;
        // Get the three directories/files to write:
        File f1 = getOpenDirectory();
        File f2 = getSaveDirectory();
        File f3 = getSessionFile();
        // Set the three lines to write:
        if (f1==null) {
            t1 = "null\n";
        } else {
            t1 = f1.toURI().toString() + "\n";
        }
        if (f2==null) {
            t2 = "null\n";
        } else {
            t2 = f2.toURI().toString() + "\n";
        }
        if (f3==null) {
            t3 = "null\n";
        } else {
            t3 = f3.toURI().toString() + "\n";
        }
        // Write those lines:
        if (FileUtils.writeLine(writer,t1)) {
            if (FileUtils.writeLine(writer,t2)) {
                FileUtils.writeLine(writer,t3);
            }
        }
        // Close the file:
        FileUtils.close(writer);
    }
    
}
