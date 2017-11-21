package filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for .ele files.
 * @author Peter Lelievre
 */
public class EleFilter extends FileFilter {

    // -------------------- Properties -------------------

    public final static String ELE = "ele";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains the .ele extension.
     * @param f The file to check.
     * @return True if the file contains the .ele extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept .ele files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return extension.equals(ELE);
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return ".ele files"; }

}