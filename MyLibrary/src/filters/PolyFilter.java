package filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for .poly files.
 * @author Peter Lelievre
 */
public class PolyFilter extends FileFilter {

    // -------------------- Properties -------------------

    public final static String POLY = "poly";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains the .poly extension.
     * @param f The file to check.
     * @return True if the file contains the .poly extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept .poly files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return extension.equals(POLY);
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return ".poly files"; }

}