package fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** A collection of static methods for file I/O tasks.
 * @author Peter Lelievre
 */
@SuppressWarnings("PublicInnerClass")
public class FileUtils {

    /** The class definition for objects returned by some methods in class FileUtils.
     * See the individual methods for how the boolean, string and integer fields are used.
     * The n field is currently used to provide the number of non-empty lines read.
     */
    @SuppressWarnings("PublicField")
    public static class ReturnClassOKString {
        public boolean ok = true;
        public String s = null;
        public int i = 0;
        public int n = 0;
    }

    /** Provides the extension of a file path.
     * The extension returned does not include the dot character.
     * @param f The input file.
     * @return The extension of the file (does not include the dot character).
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /** Provides everything but the extension of a file path.
     * The root returned does not include the dot character.
     * @param f The input file.
     * @return Everything but the extension of the file (does not include the dot character).
     */
    public static String getRoot(File f) {
        String out;
        String s = f.getAbsolutePath();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            out = s.substring(0,i);
        } else {
            out = s;
        }
        return out;
    }

    /** Provides everything but the path and extension of a file.
     * The name returned does not include the dot character.
     * @param f The input file.
     * @return Everything but the path and extension of the file (does not include the dot character).
     */
    public static String getName(File f) {
        String out;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            out = s.substring(0,i);
        } else {
            out = s;
        }
        return out;
    }

    /** Closes a file, ignoring any exceptions thrown.
     * Wraps the b.close() method in a try-catch block.
     * @param b The BufferedReader object to use.
     */
    public static void close(BufferedReader b) {
        try { b.close(); } catch (IOException e) {}
    }

    /** Closes a file, ignoring any exceptions thrown.
     * Wraps the b.close() method in a try-catch block.
     * @param b The BufferedWriter object to use.
     */
    public static void close(BufferedWriter b) {
        try { b.close(); } catch (IOException e) {}
    }

    /** Opens a file for reading.
     * @param f The file to open.
     * @return A BufferedReader object to use for reading the file (null if error occurs).
     */
    public static BufferedReader openForReading(File f) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(f.getAbsoluteFile()));
        } catch (FileNotFoundException e) {
            return null;
        }
        return reader;
    }
    
    /** Opens a file for writing.
     * @param f The file to open.
     * @return A BufferedWriter object to use for writing the file (null if error occurs).
     */
    public static BufferedWriter openForWriting(File f) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(f.getAbsoluteFile()));
        } catch (IOException e) {
            return null;
        }
        return writer;
    }

    /** Reads a line from a file, closing the file if an error occurs.
     * @param reader The BufferedReader to use.
     * @return The line read from the file (null if error occurs).
     */
    public static String readLine(BufferedReader reader) {
        String textLine;
        try {
            textLine = reader.readLine(); // reads the line
        } catch (IOException e) {
            try { reader.close(); } catch (IOException ee) {}
            return null;
        }
        return textLine;
    }

    /** Writes a line to a file, closing the file if an error occurs.
     * @param writer The BufferedWriter to use.
     * @param textLine The line to write.
     * @return Returns false if an error occurs.
     */
    public static boolean writeLine(BufferedWriter writer, String textLine) {
        try {
            writer.write(textLine);
        } catch (IOException e) {
            try { writer.close(); } catch (IOException ee) {}
            return false;
        }
        return true;
    }

    /** Reads an open file until it finds a line containing the string provided.
     * That line is returned in the ReturnClassOKString object.
     * Closes the file if an error occurs.
     * @param reader The BufferedReader to use.
     * @param s1 The string to search for.
     * @return The .ok field is set to false if an error occurs; the line is placed in the .s field.
     */
    public static ReturnClassOKString findLine(BufferedReader reader, String s1) {

        ReturnClassOKString out = new ReturnClassOKString();
        String textLine = null;

        // Loop until we find the line or we reach the end of the file or an error occurs:
        out.n = 0;
        do {
            
            // Read line from file:
            textLine = readLine(reader);

            // Check for error:
            if (textLine==null) {
                try { reader.close(); } catch (IOException ee) {}
                out.ok = false;
                return out;
            }
            
            // Increment line counter if not empty line:
            textLine = textLine.trim();
            if (!textLine.isEmpty()) {
                out.n += 1;
            }

        } while (!textLine.contains(s1));

        // Fill and return the output object:
        out.ok = true;
        out.s = textLine;
        return out;

    }

    /** Reads an open file until it finds a line containing one of the strings provided.
     * That line is returned in the ReturnClassOKString object.
     * Closes the file if an error occurs.
     * @param reader The BufferedReader to use.
     * @param ss The strings to search for.
     * @return The .ok field is set to false if an error occurs; the line is placed in the .s field; the .i field indicates the string found.
     */
    public static ReturnClassOKString findLine(BufferedReader reader, String[] ss) {

        ReturnClassOKString out = new ReturnClassOKString();
        String textLine = null;

        // Loop until we find the line or we reach the end of the file or an error occurs:
        out.n = 0;
        out.i = -1; // will be set >=0 once one of the strings has been found
        do {

            // Read line from file:
            textLine = readLine(reader);

            // Check for error:
            if (textLine==null) {
                try { reader.close(); } catch (IOException ee) {}
                out.ok = false;
                out.i = -1;
                return out;
            }
            
            // Increment line counter if not empty line:
            textLine = textLine.trim();
            if (textLine.isEmpty()) {
                continue; // skip to next iteration if it was an empty line
            } else {
                out.n += 1;
            }
            
            // Check if the line contains any of the strings:
            for (int i=0 ; i<ss.length ; i++){
                if (textLine.contains(ss[i])) {
                    out.i = i;
                    break;
                }
            }

        } while (out.i<0);

        // Fill and return the output object:
        out.ok = true;
        out.s = textLine;
        return out;

    }

    /** Reads a contiguous set of lines containing the string provided.
     * Those lines are returned in the ReturnClassOKString object, joined by the carriage return character.
     * Closes the file if an error occurs.
     * @param reader The BufferedReader to use.
     * @param s1 The string to search for.
     * @param nmax The maximum number of lines to read (set to 0 to have no max number).
     * @return The .ok field is set to false if an error occurs; the lines are placed in the .s field; the .n field indicates the number of non-empty lines read.
     */
    public static ReturnClassOKString findLines(BufferedReader reader, String s1, int nmax) {

        ReturnClassOKString out = new ReturnClassOKString();
        String textLine;

        // Loop until we find a line without the search string or we reach the end of the file or an error occurs:
        out.n = 0;
        out.s = null; // will hold all the lines found
        while(true) {

            // Mark line to reset to later:
            try {
                reader.mark(1024);
            } catch (IOException ex) {
                out.ok = false;
                return out;
            }

            // Read line from file:
            textLine = readLine(reader);

            // Check for error:
            if (textLine==null) {
                try { reader.close(); } catch (IOException ee) {}
                out.ok = false;
                return out;
            }

            // Increment line counter:
            out.n += 1; // Don't need to check for empty line because I quit as soon as a line doesn't contain the search string (assumed to be non-empty).

            // Check if we have read enough lines:
            if (nmax>0) {
                if (out.n>nmax) { // I read one too many so that the resetting to the last mark is appropriate
                    break;
                }
            }

            // Check if the line contains the search string:
            if (textLine.contains(s1)) {
                // Add to the output string:
                if (out.s==null) {
                    out.s = textLine;
                } else {
                    out.s = out.s + "\n" + textLine;
                }
            } else {
                break; // I break as soon as a line doesn't contain the search string (includes an empty line).
                // Therefore, I am reading one too many so that the resetting to the last mark is appropriate.
            }

        }

        // Reset to last mark:
        try {
            reader.reset();
        } catch (IOException ex) {
            out.ok = false;
            return out;
        }
        
        // Fill and return the output object:
        out.ok = true;
        out.n -= 1; // I read one line too many.
        return out;

    }

    /** Reads a contiguous set of lines continued by a particular character at the END of the line.
     * Those lines are concatenated, the continuation character removed, and returned in the ReturnClassOKString object.
     * For example, this method can be used to read continuation lines in Fortran 90/95 code continued with the '&' character.
     * Closes the file if an error occurs.
     * @param reader The BufferedReader to use.
     * @param c The continuation character to use.
     * @return The .ok field is set to false if an error occurs; the lines are placed in the .s field; the .n field indicates the number of non-empty lines read.
     */
    public static ReturnClassOKString findContinuedLines(BufferedReader reader, String c) {

        ReturnClassOKString out = new ReturnClassOKString();
        String textLine;

        // Loop until we find a non-continuation line or blank line or we reach the end of the file or an error occurs:
        out.n = 0;
        out.s = null; // will hold the non-continued version of the continued lines
        do {

            // Read line from file:
            textLine = readLine(reader);

            // Check for error:
            if (textLine==null) {
                try { reader.close(); } catch (IOException ee) {}
                out.ok = false;
                return out;
            }
            
            // Increment line counter if not empty line:
            textLine = textLine.trim();
            if (textLine.isEmpty()) {
                break; // I break from the do loop when an empty line is found.
                // This means there is an empty line after a continued line.
                // Technically this should be a coding error, but any decent
                // Fortran compiler should catch that so I'll assume this never happens.
            } else {
                out.n += 1;
            }

            // Add to the output string:
            String t = textLine;
            t = t.replaceAll("&"," ");
            t = t.trim();
            if (out.s==null) {
                out.s = t;
            } else {
                out.s += t;
            }

        } while (textLine.endsWith(c));

        // Fill and return the output object:
        out.ok = true;
        return out;

    }

    /** Reads a contiguous set of commented or uncommented (non-empty) lines.
     * Those lines are returned in the ReturnClassOKString object, joined by the carriage return character.
     * Closes the file if an error occurs.
     * @param reader The BufferedReader to use.
     * @param c The comment character to use.
     * @param nmax The maximum number of lines to read (set to 0 to have no max number).
     * @param commented Set to true if you want a set of commented lines, false for uncommented lines.
     * @return The .ok field is set to false if an error occurs; the lines are placed in the .s field; the .n field indicates the number of non-empty lines read.
     */
    public static ReturnClassOKString readCommentedLines(BufferedReader reader, String c, int nmax, boolean commented) {

        ReturnClassOKString out = new ReturnClassOKString();
        String textLine;

        // Loop until we find the wrong line or we reach the end of the file or an error occurs:
        out.n = 0; // counter for lines read
        out.s = null; // will contain the set of commented/uncommented lines
        while(true) {

            // Mark line to reset to later:
            try {
                reader.mark(1024);
            } catch (IOException ex) {
                out.ok = false;
                return out;
            }

            // Read line from file:
            textLine = readLine(reader);

            // Check for error:
            if (textLine==null) {
                try { reader.close(); } catch (IOException ee) {}
                out.ok = false;
                return out;
            }

            // Increment line counter:
            out.n += 1; // Don't need to check for empty line because I quit below if an empty line is found.

            // Check if we have read enough lines:
            if (nmax>0) {
                if (out.n>nmax) { // I read one too many so that the resetting to the last mark is appropriate
                    break;
                }
            }

            // Determine how to continue for current line:
            textLine = textLine.trim();
            if (commented) { // Reading a contiguous set of commented lines.
                // Check for leading comment character:
                if (textLine.startsWith(c)) {
                    // Check for empty comment:
                    //String t = textLine;
                    //t = t.replaceAll(c," ");
                    //t = t.trim();
                    //if (t.isEmpty()) { out.n = out.n - 1; }
                    // Add to the output string:
                    if (out.s==null) {
                        out.s = textLine;
                    } else {
                        out.s = out.s + "\n" + textLine;
                    }
                } else {
                   break; // I break as soon as a line isn't commented (includes an empty line).
                   // Therefore, I am reading one too many so that the resetting to the last mark is appropriate.
                }
            } else { // Reading a contiguous set of uncommented non-empty lines.
                // Check for leading comment character or blank line:
                if ( textLine.startsWith(c) || textLine.isEmpty() ) {
                   break; // I break as soon as a line is commented or is empty.
                   // Therefore, I am reading one too many so that the resetting to the last mark is appropriate.
                } else {
                    // Add to the output string:
                    if (out.s==null) {
                        out.s = textLine;
                    } else {
                        out.s = out.s + "\n" + textLine;
                    }
                }
            }

        }

        // Reset to last mark:
        try {
            reader.reset();
        } catch (IOException ex) {
            out.ok = false;
            return out;
        }

        // Fill and return the output object:
        out.ok = true;
        out.n -= 1; // I read one line too many;
        return out;

    }

}