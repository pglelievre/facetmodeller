package filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for some sort of file(s), specified on construction.
 * @author Peter Lelievre
 */
public class SpecFilter extends FileFilter {

    // -------------------- Properties -------------------

    private String[] options;
    private String description;
    
    // -------------------- Constructor -------------------

    public SpecFilter(String[] ss, String d) {
        int n = ss.length;
        options = new String[n];
        System.arraycopy(ss,0,options,0,n); // deep copy
        description = d;
    }
    
    // -------------------- Public Methods -------------------

    /** Checks if a file contains any of the allowed extensions.
     * @param f The file to check.
     * @return True if the file contains one of the allowed extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }
        
        // Check if the file extension is in the list of acceptable extensions:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            for (String option : options) {
                if (extension.equals(option)) {
                    return true;
                }
            }
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return description; }

}