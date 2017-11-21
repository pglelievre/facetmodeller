package facetmodeller.sections;

import facetmodeller.commands.CommandVector;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.Facet;
import facetmodeller.plc.NodeVector;
import geometry.Dir3D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.io.File;
import java.util.ArrayList;

/** A Vector of Section objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class SectionVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private final ArrayList<Section> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void SectionVector() {}

    // ------------------- Copy ------------------

//    public SectionVector deepCopy() {
//        SectionVector sections = new SectionVector();
//        for ( int i=0 ; i<size() ; i++ ) {
//            Section s = get(i).deepCopy();
//            sections.add(s);
//        }
//        return sections;
//    }
//    public SectionVector undoCopy() {
//        SectionVector sections = new SectionVector();
//        for ( int i=0 ; i<size() ; i++ ) {
//            Section s = get(i).undoCopy();
//            sections.add(s);
//        }
//        return sections;
//    }
//    public SectionVector shallowCopy() {
//        SectionVector sections = new SectionVector();
//        sections.addAll(this);
//        return sections;
//    }

    // -------------------- Public Methods -------------------

    /** Clears the vector. */
    public void clear() {
        vector.clear();
    }

//    /** Clears all plc information from the sections. */
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
    public Section get(int i) {
        if ( i<0 || i>=size() ) {
            return null;
        } else {
            return vector.get(i);
        }
    }

    /** Adds an element to the end of the vector.
     * @param s The Section object to add to the end of the vector.
     */
    public void add(Section s) {
        if (vector.contains(s)) { return; }
        vector.add(s);
    }
    
    /** Combines section vectors.
     * @param v */
    public void addAll(SectionVector v) {
        //vector.addAll(v.vector);
        for (int i=0 ; i<v.size() ; i++ ) {
            add(v.get(i)); // passing through this method makes sure there are no duplicates
        }
    }

    /** Adds an element to the start of the vector.
     * @param s The Section object to add to the start of the vector.
     */
    public void addFirst(Section s) {
        if (vector.contains(s)) { return; }
        vector.add(0,s);
    }

    /** Adds a facet to all the sections.
     * @param f */
    public void addFacet(Facet f) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).addFacet(f);
        }
    }

    public boolean contains(Section s) {
        return vector.contains(s);
    }

    /** Removes an element from the vector.
     * @param i The index of the element to remove.
     */
    public void remove(int i) {
        //if ( i<0 || i>=size() ) { return; }
        vector.remove(i);
    }

    /** Removes an element from the vector.
     * @param s */
    public void remove(Section s) {
        vector.remove(s);
    }

    /** Removes a facet from any sections that contain it.
     * @param f */
    public void removeFacet(Facet f) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).removeFacet(f);
        }
    }

    /** Resets the ID values from 0 to the size of the vector in the order listed. */
    public void resetIDs() {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setID(i);
        }
    }

    /** Returns a string array containing the section names.
     * @return  */
    public String[] nameList() {
        // Create new object to return:
        String[] names = new String[size()];
        // Loop over each section:
        for (int i=0 ; i<size() ; i++ ) {
            // Get the file name for the current section:
            names[i] = (i+1) + ": " + get(i).shortName();
        }
        // Return the new object:
        return names;
    }

    /** Adds new cross sections to the list and sets the section files to those in the supplied array of files.
     * Does NOT clear the section list.
     * @param files Array of files where section images exist.
     * @param iscross
     */
    public void addFromFiles(File[] files, boolean iscross) {
        for (File file : files) {
            // Create a new section linked to the ith file:
            Section s;
            if (iscross) {
                s = new ImageCrossSection(file); // It is important to create a new instance of the ImageCrossSection class here!
            } else {
                s = new ImageDepthSection(file);
            }
            // Add the new section object to the vector of sections:
            add(s);
        }
    }

    public int find(Section s) { return vector.indexOf(s); }

//    /** Puts the first/only topography section at the start of the list. */
//    public void topoFirst() {
//        // Loop over each section:
//        for (int i=0 ; i<size() ; i++ ) {
//            // Check for topography section:
//            if (get(i) instanceof TopoSection) {
//                // Put section at start:
//                Section section = vector.get(i); // makes sure section is not trashed when removed from vector in following statement
//                vector.remove(i); // removes section from vector
//                vector.add(0,section); // adds section at start
//                // Break out of for loop:
//                break;
//            }
//        }
//    }

//    /** Returns true if there is a topography section in the vector.
//     * @return  */
//    public boolean hasTopo() {
//        for (int i=0 ; i<size() ; i++ ) {
//            if (get(i) instanceof TopoSection) { return true; }
//        }
//        return false;
//    }
    
    public void scalePixels(double f) {
        for (int i=0 ; i<size() ; i++ ) {
            Section s = get(i);
            s.scalePixels(f);
        }
    }
    
    /** Copies the calibration information from the supplied section to all in the list below the supplied section.
     * @param section The section to copy calibration information from.
     * @param step */
    public void copyCalibration(Section section, double step) {
        // Get the number of sections:
        int n = vector.size();
        if (n==0) { return; }
        // Make sure the section has a natural direction:
        Dir3D dir = section.getDir3D();
        if (dir==null) { return; }
        // Get calibration information for the supplied section:
        MyPoint2D c1 = section.getClicked1();
        MyPoint2D c2 = section.getClicked2();
        MyPoint3D t1 = section.getTyped1().deepCopy();
        MyPoint3D t2 = section.getTyped2().deepCopy();
        // Loop over each section:
        double loc = dir.getCoord(t1);
        boolean copy = false; // set to true once we hit the supplied section
        for (int i=0 ; i<n ; i++ ) {
            Section s = get(i);
            // Check if the ith section is the supplied section:
            if (s.equals(section)) {
                // Set copy to true to indicate we are ready to start copying:
                copy = true;
                // Skip to next section:
                continue;
            }
            // Make sure we have found the supplied section:
            if (!copy) { continue; }
            // Skip sections that can't be calibrated:
            if (!s.canCalibrate()) { continue; }
            // Shift the typed points:
            loc += step;
            t1 = dir.setCoord(t1,loc);
            t2 = dir.setCoord(t2,loc);
            // Copy over the information:
            s.setClicked1(c1.deepCopy());
            s.setClicked2(c2.deepCopy());
            s.setTyped1(t1.deepCopy());
            s.setTyped2(t2.deepCopy());
        }
    }
    
    public CommandVector snapToCalibration(double pickingRadius, GroupVector groups, boolean doH, boolean doV) {
        CommandVector commands = new CommandVector("");
        // Loop over each section:
        for (int i=0 ; i<size() ; i++ ) {
            // Get the ith section:
            Section section = get(i);
            // Skip sections where snapping to calibration is not allowed:
            if (!section.canNodesShift()) { continue; }
            // Process the section:
            CommandVector coms = section.snapToCalibration(pickingRadius,groups,doH,doV);
            commands.addAll(coms);
        }
        return commands;
    }
    
    public NodeVector removeNodesRange() {
        // Loop over each section:
        NodeVector nodesToRemove = new NodeVector();
        for (int i=0 ; i<size() ; i++ ) {
            // Get the ith section:
            Section section = get(i);
            // Skip topography sections, or others where deleting nodes outside the calibration points is not allowed:
            if (!section.canDeleteNodesRange()) { continue; }
            // Process the section:
            nodesToRemove = section.removeNodesRange(nodesToRemove);
        }
        return nodesToRemove;
    }

}