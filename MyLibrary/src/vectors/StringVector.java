package vectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** A Vector of String objects.
 * All of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class StringVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private ArrayList<String> list = new ArrayList<>();

    // ------------------- Constructors ------------------

    public StringVector() {}
    
    public StringVector(String[] array) {
        list = new ArrayList<>(Arrays.asList(array));
    }

    // ------------------- Deep Copy ------------------
    
    public StringVector deepCopy() {
        StringVector s = new StringVector();
        for (int i=0 ; i<size() ; i++ ) {
            s.add(get(i));
        }
        return s;
    }

    // -------------------- Getters -------------------
    
    public boolean isEmpty() { return list.isEmpty(); }
    
    public int size() { return list.size(); }

    public String get(int i) { return list.get(i); }

    // -------------------- Setters -------------------

    public void set(int i,String s) { list.set(i,s); }

    // -------------------- Public Wrappers -------------------

    public void clear() { list.clear(); }

    public void add(String s) { list.add(s); }

    public void add(int i, String s) { list.add(i,s); }
    
    public void addAll(String[] array) { list.addAll(Arrays.asList(array)); }

    public void remove(int i) { list.remove(i); }
    
    /** Checks to see if the vector contains the input string object.
     * This is essentially a string comparison for every object in the list.
     * @param s Input string object.
     * @return True if the vector contains the input object, false otherwise.
     */
    public boolean contains(String s) {
        // The list.contains method uses the String.equals method, which
        // returns true only if the argument is a String that represents
        // the same sequence of characters.
        return list.contains(s);
    }

    public int indexOf(String s) { return list.indexOf(s); }

    /** Sorts the vector components in increasing order. */
    public void sort() { Collections.sort(list); }

    // -------------------- Public Methods -------------------

    /** Adds many elements to the end of the vector but only if they are not already in the vector.
     * @param s The array of strings to add to the end of the vector.
     */
    public void addUnique(String[] s) {
        for (String item : s) {
            if (!list.contains(item)) {
                list.add(item);
            }
        }
    }

    /** Trims all strings in the vector. */
    public void trim() {
        for ( int i=0 ; i<size() ; i++ ) {
            String s = list.get(i);
            s = s.trim();
            list.set(i,s);
        }
    }
    
    public String[] toArray() {
        //return (String[]) list.toArray(); // CRASHES
        if (isEmpty()) { return null; }
        String[] s = new String[size()];
        for ( int i=0 ; i<size() ; i++ ) {
            s[i] = list.get(i);
        }
        return s;
    }
    
    public void copy(int i) {
        String s = get(i); // I'm pretty sure this is a deep copy.
        add(i+1,s);
    }
    
}