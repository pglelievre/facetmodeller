package fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Interface that defines methods for writing and reading information to and from
 * saved FacetModeller session files.
 *
 * @author Peter
 */
public interface SessionIO {
    
    /** Writes information to ASCII file.
     * Should be completely consistent with the readSessionInformation method.
     * @param writer
     * @return False if a writing error occurs.
     */
    public boolean writeSessionInformation(BufferedWriter writer);
    
    /** Reads information from ASCII file.
     * Any properties set in the standard constructor should be dealt with in this method.
     * @param reader
     * @param merge True if information read from the file should be merged into the object instead of overwriting.
     * @return An error message indicating what was happening when an error occurred while reading the file.
     */
    public String readSessionInformation(BufferedReader reader, boolean merge);
    
}
