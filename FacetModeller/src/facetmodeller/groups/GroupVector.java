package facetmodeller.groups;

import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;
import fileio.FileUtils;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/** A Vector of Group objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class GroupVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private final ArrayList<Group> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void GroupVector() {}

    // ------------------- Copy ------------------

//    public GroupVector deepCopy() {
//        GroupVector groups = new GroupVector();
//        for ( int i=0 ; i<size() ; i++ ) {
//            Group g = get(i).deepCopy();
//            groups.add(g);
//        }
//        return groups;
//    }
//    public GroupVector undoCopy() {
//        GroupVector groups = new GroupVector();
//        for ( int i=0 ; i<size() ; i++ ) {
//            Group g = get(i).undoCopy();
//            groups.add(g);
//        }
//        return groups;
//    }
//    public GroupVector shallowCopy() {
//        GroupVector groups = new GroupVector();
//        groups.addAll(this);
//        return groups;
//    }

    // -------------------- Public Methods -------------------

    /** Clears the vector. */
    public void clear() {
        vector.clear();
    }

//    /** Clears all plc information from the groups. */
//    public void clearPLC() {
//        for (int i=0 ; i<size() ; i++ ) {
//            get(i).clearPLC();
//        }
//    }
//    public void clearFacets() {
//        for (int i=0 ; i<size() ; i++ ) {
//            get(i).clearFacets();
//        }
//    }

    /** Returns the size of the vector.
     * @return The size of the vector.
     */
    public int size() {
        return vector.size();
    }
    
    /** Returns true if the vector has no elements.
     * @return  */
    public boolean isEmpty() {
        return vector.isEmpty();
    }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public Group get(int i) {
        //if ( i<0 || i>=size() ) {
        //    return null;
        //} else {
            return vector.get(i);
        //}
    }
    
    /** Returns all nodes in all groups.
     * @return 
     */
    public NodeVector getNodes() {
        NodeVector allNodes = new NodeVector();
        for (int i=0 ; i<size() ; i++ ) {
            NodeVector nodes = get(i).getNodes();
            allNodes.addAll(nodes);
        }
        return allNodes;
    }
    
    /** Returns all facets in all groups.
     * @return 
     */
    public FacetVector getFacets() {
        FacetVector allFacets = new FacetVector();
        for (int i=0 ; i<size() ; i++ ) {
            FacetVector facets = get(i).getFacets();
            allFacets.addAll(facets);
        }
        return allFacets;
    }

    /** Returns true if the supplied group is in the list.
     * @param g
     * @return  */
    public boolean contains(Group g) {
        return vector.contains(g);
    }

    /** Returns the index of the group in the list (or -1 if not in the list).
     * @param g
     * @return  */
    public int indexOf(Group g) {
        return vector.indexOf(g);
    }

    /** Adds an element to the end of the vector.
     * @param g
     */
    public void add(Group g) {
        if (vector.contains(g)) { return; }
        vector.add(g);
    }

    /** Adds an element at the specified index.
     * @param g
     * @param i The index.
     */
    public void add(Group g, int i) {
        if (vector.contains(g)) { return; }
        vector.add(i,g);
    }
    
    /** Adds the objects in the supplied group vector.
     * @param v */
    public void addAll(GroupVector v) {
        //if (v==null) { return; }
        //vector.addAll(v.vector);
        for (int i=0 ; i<v.size() ; i++ ) {
            add(v.get(i)); // passing through this method makes sure there are no duplicates
        }
    }

    /** Removes an element from the vector.
     * @param i The index of the element to remove.
     */
    public void remove(int i) {
        if ( i<0 || i>=size() ) { return; }
        vector.remove(i);
    }

    /** Removes an element from the vector.
     * @param g */
    public void remove(Group g) {
        vector.remove(g);
    }

    /** Reverses the order of the elements in the vector. */
    public void reverseOrder() {
        Collections.reverse(vector);
    }

    /** Resets the ID values from 0 to the size of the vector in the order listed. */
    public void resetIDs() {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setID(i);
        }
    }

    /** Returns a string array containing the group names.
     * @return  */
    public String[] nameList() {

        // Create new object to return:
        String[] names = new String[size()];

        // Loop over each group:
        for (int i=0 ; i<size() ; i++ ) {

            // Get the name for the current group:
            names[i] = (i+1) + ": " + get(i).getName();

        }

        // Return the new object:
        return names;

    }

    /** Reads group definitions from a file into the group vector.
     * The first line of the file should specify the number of measurement definitions.
     * Each of the following lines contains a group name followed by a colour (3 RGB values).
     * @param file The file to read.
     * @return A message describing a file reading error (null if read successfully).
     */
    public String readFile(File file) {

        String textLine; // string for holding a line in the file
        StringTokenizer tokenizer;

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return "Could not find the specified file."; }

        // Read the number of group definitions:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Failed to read the file"; }
        tokenizer = new StringTokenizer(textLine);
        textLine = tokenizer.nextToken(); // extracts the first token
        int nLines;
        try {
            nLines = Integer.parseInt(textLine.trim()); // converts to integer
        } catch (NumberFormatException e) {
            try { reader.close(); } catch (IOException ee) {}
            return "The number of groups defined must be specified on the first line of the file.";
        }

        // Loop over nLines lines in the file:
        for (int i=0 ; i<nLines ; i++ ) {

            // Read the current line:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Failed to read the file. Make sure there are enough lines in the file."; }
            
            // Extract the parameters (name and colour) for the current measurement:
            textLine = textLine.trim();
            String[] ss = textLine.split("[ ]+",5);
            String name = ss[0]; // group name
            int r,g,b;
            try {
                r = Integer.parseInt(ss[1]);
                g = Integer.parseInt(ss[2]);
                b = Integer.parseInt(ss[3]);
            } catch (NumberFormatException e) {
                try { reader.close(); } catch (IOException ee) {}
                return "Failed to read RGB information from a line.";
            }
            // Create the group:
            Group group = new Group(name);
            Color col;
            try {
                col = new Color(r,g,b);
            } catch (IllegalArgumentException e) {
                try { reader.close(); } catch (IOException ee) {}
                return "Failed to generate colour from RGB information: must be on [0,255].";
            }
            group.setColor(col);
            // Add the group to the list:
            this.add(group);
        }

        // Close the file:
        try { reader.close(); } catch (IOException e) {}

        // Return succesfully:
        return null;

    }

    /** Writes group definitions to a file.
     * @param file The file to write.
     * @return True if the file is written correctly.
     */
    public boolean writeFile(File file) {

        String textLine; // string for holding a line in the file

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return false; }

        // Write the number of group definitions:
        textLine = Integer.toString(size());
        boolean ok = FileUtils.writeLine(writer,textLine);
        if (!ok) { FileUtils.close(writer); return false; }
        
        // Loop over each group:
        for (int i=0 ; i<size() ; i++ ) {
            // Write the information for the current group:
            Group group = get(i);
            Color col = group.getNodeColor();
            textLine = group.getName() + " " + col.getRed() + " " + col.getGreen() + " " + col.getBlue();
            ok = FileUtils.writeLine(writer,textLine);
            if (!ok) { FileUtils.close(writer); return false; }
        }

        // Close the file:
        FileUtils.close(writer);

        // Return succesfully:
        return true;

    }

}