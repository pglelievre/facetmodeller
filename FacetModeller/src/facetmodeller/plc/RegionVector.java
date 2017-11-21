package facetmodeller.plc;

import java.util.ArrayList;

/** A Vector of Region objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class RegionVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private final ArrayList<Region> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void RegionVector() {}

    // ------------------- Copy ------------------

//    public RegionVector deepCopy() {
//        RegionVector regions = new RegionVector();
//        for ( int i=0 ; i<size() ; i++ ) {
//            Region r = get(i).deepCopy();
//            regions.add(r);
//        }
//        return regions;
//    }
//    public RegionVector shallowCopy() {
//        RegionVector regions = new RegionVector();
//        regions.addAll(this);
//        return regions;
//    }

    // -------------------- Public Methods -------------------

    /** Clears the vector. */
    public void clear() {
        vector.clear();
    }

    /** Returns the size of the vector.
     * @return The size of the vector.
     */
    public int size() {
        return vector.size();
    }
    
    /** Returns the number of true region points (not control points).
    * @return The number of region points.
     */
    public int numberOfRegionPoints() {
        int n = size() - numberOfControlPoints();
        return n;
    }
    
    /** Returns the number of control points (not true region points).
    * @return The number of control points.
     */
    public int numberOfControlPoints() {
        int n = 0;
        for (Region vector1 : vector) {
            if (vector1.getIsControl()) {
                n++;
            }
        }
        return n;
    }
    
    /** Returns true if the vector has no elements.
     * @return  */
    public boolean isEmpty() {
        return vector.isEmpty();
    }
    
    public int indexOf(Region r) { return vector.indexOf(r); }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public Region get(int i) {
        return vector.get(i);
    }

    /** Returns true if the supplied region is in the list.
     * @param r
     * @return  */
    public boolean contains(Region r) {
        return vector.contains(r);
    }

    /** Adds an element to the end of the vector if not already in the list.
     * @param r The Region object to add to the end of the vector.
     */
    public void add(Region r) {
        if (contains(r)) { return; }
        vector.add(r);
    }
    
    /** Combines region vectors.
     * @param v */
    public void addAll(RegionVector v) {
        //vector.addAll(v.vector);
        for (int i=0 ; i<v.size() ; i++ ) {
            add(v.get(i)); // passing through this method makes sure there are no duplicates
        }
    }

    /** Finds the Region object in the vector and removes it (if found).
     * @param r The Region object to remove.
     */
    public void remove(Region r) {
        vector.remove(r);
    }

    /** Resets the ID values from 0 to the size of the vector in the order listed. */
    public void resetIDs() {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setID(i);
        }
    }

}