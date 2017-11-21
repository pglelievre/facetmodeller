package facetmodeller.filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for FacetModeller session files.
 * @author Peter Lelievre
 */
public class SessionFilter extends FileFilter {

    // -------------------- Properties -------------------

    public final static String FMS = "fms";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains the specified FacetModeller session extension.
     * @param f The file to check.
     * @return True if the file contains the specified FacetModeller session extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept .fms files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return extension.equals(FMS);
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return "FacetModeller session files"; }

}