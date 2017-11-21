package facetmodeller;

import fileio.FileUtils;
import fileio.SessionIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JList;

/** A JList selector that implements SessionIO.
 * @author Peter
 */
public class JListSelector extends JList<String> implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write the number of selected groups and the selected indices on a single line:
        int[] selection = getSelectedIndices();
        int n = selection.length;
        String textLine = Integer.toString(n);
        for ( int i=0 ; i<n ; i++ ) {
            textLine += " " + Integer.toString(selection[i]);
        }
        textLine += "\n";
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read the number of selected groups and group ID's from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading selected groups line."; }
        // If merging then don't change the selection:
        if (merge) { return null; }
        // Split and parse the selection line:
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<1) { return "No values on selected groups line."; }
        int[] selection;
        int n;
        try {
            n = Integer.parseInt(s[0]);
            if (n==0) {
                clearSelection();
                return null;
            }
            if ( s.length < n+1 ) { return "Not enough values on selected groups line."; }
            selection = new int[n];
            for ( int i=0 ; i<n ; i++ ) {
                selection[i] = Integer.parseInt(s[i+1]);
            }
        } catch (NumberFormatException e) { return "Parsing selected groups."; }
        // Set the selection:
        setSelectedIndices(selection);
        // Return successfully:
        return null;
    }
    
}
