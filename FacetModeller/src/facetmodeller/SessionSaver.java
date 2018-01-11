package facetmodeller;

import fileio.FileUtils;
import java.io.BufferedWriter;
import java.io.File;

/** Static methods for saving the FacetModeller session.
 * Everytime the version is changed a new save method should be added to this class.
 * The names of the older static methods should be suffixed with their version numbers.
 * @author Peter Lelievre
 */
public class SessionSaver {

    /** Saves the existing session to an ascii file.
     * All ID's should be reset before calling this method.
     * @param con
     * @param file
     * @return 
     */
    public static boolean saveSessionAscii(FacetModeller con, File file) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return false; }

        // Write the program name and version string:
        String textLine = "# FacetModeller version " + con.versionString();
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write the floored version number:
        textLine = Integer.toString( con.versionInt() );
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write the model information:
        if (!con.getModelManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Comment start of viewing options:
        if (!FileUtils.writeLine(writer,"# VIEWING OPTIONS")) { FileUtils.close(writer); return false; }

        // Write view manager options:
        if (!con.getViewManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Comment start of interaction options:
        if (!FileUtils.writeLine(writer,"# INTERACTION OPTIONS")) { FileUtils.close(writer); return false; }

        // Write interaction manager options:
        if (!con.getInteractionManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Close the file:
        FileUtils.close(writer);

        // Return true:
        return true;

    }

}
