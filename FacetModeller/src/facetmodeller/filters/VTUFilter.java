package facetmodeller.filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for .vtu files.
 * @author Peter Lelievre
 */
public class VTUFilter extends FileFilter {

    // -------------------- Properties -------------------

    public final static String VTU = "vtu";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains the .vtu extension.
     * @param f The file to check.
     * @return True if the file contains the .vtu extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept .vtu files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return extension.equals(VTU);
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return ".vtu files"; }

}