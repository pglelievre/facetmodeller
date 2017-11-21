package filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/* File type filter for image files.
 * Found online. Seems to work just fine.
 */
public class ImageFilter extends FileFilter {

    // -------------------- Properties -------------------
    
    public final static String JPEG = "jpeg";
    public final static String JPG  = "jpg";
    public final static String GIF  = "gif";
    public final static String TIFF = "tiff";
    public final static String TIF  = "tif";
    public final static String PNG  = "png";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains one of the specified image extensions.
     * @param f The file to check.
     * @return True if the file contains one of the specified image extensions, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept tif, gif, jpg, and png files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return ( extension.equals(TIFF) ||
                     extension.equals(TIF) ||
                     extension.equals(GIF) ||
                     extension.equals(JPEG) ||
                     extension.equals(JPG) ||
                     extension.equals(PNG)
                   );
        }

        return false;
        
    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return "Image files"; }
    
}