package filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for .node files.
 * @author Peter Lelievre
 */
public class NodeFilter extends FileFilter {

    // -------------------- Properties -------------------

    public final static String NODE = "node";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains the .node extension.
     * @param f The file to check.
     * @return True if the file contains the .node extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept .node files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return extension.equals(NODE);
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return ".node files"; }

}