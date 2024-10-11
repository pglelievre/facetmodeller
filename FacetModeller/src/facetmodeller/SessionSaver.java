package facetmodeller;

import fileio.FileUtils;
import java.io.BufferedWriter;
import java.io.File;

/** Static methods for saving the FacetModeller session.
 * Every time the version is changed a new save method should be added to this class.
 * The names of the older static methods should be suffixed with their version numbers.
 * @author Peter Lelievre
 */
public class SessionSaver {

    /** Saves the existing session to an ascii file.
     * All ID's should be reset before calling this method.
     * @param controller
     * @param file
     * @return 
     */
    public static boolean saveSessionAscii(FacetModeller controller, File file) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return false; }

        // Write the program name and version string:
        String textLine = "# FacetModeller version " + controller.versionString();
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write the floored version number:
        textLine = Integer.toString(controller.versionInt() );
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write the model information:
        if (!controller.getModelManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Comment start of viewing options:
        if (!FileUtils.writeLine(writer,"# VIEWING OPTIONS")) { FileUtils.close(writer); return false; }

        // Write view manager options:
        if (!controller.getViewManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Comment start of interaction options:
        if (!FileUtils.writeLine(writer,"# INTERACTION OPTIONS")) { FileUtils.close(writer); return false; }

        // Write interaction manager options:
        if (!controller.getInteractionManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Comment start of file i/o options:
        if (!FileUtils.writeLine(writer,"# FILE I/O OPTIONS")) { FileUtils.close(writer); return false; }

        // Write file i/o manager options:
        if (!controller.getFileIOManager().writeSessionInformation(writer)) { FileUtils.close(writer); return false; }

        // Close the file:
        FileUtils.close(writer);

        // Return successfully:
        return true;

    }

}
