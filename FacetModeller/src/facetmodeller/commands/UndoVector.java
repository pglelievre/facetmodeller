package facetmodeller.commands;

import java.util.ArrayList;

/** For tracking commands and undoing them in the reverse order they were executed.
 * @author Peter
 */
public class UndoVector {

    // -------------------- Properties -------------------
    
    private int maxsize = 0; // maximum size of vector
    
    // Favour composition over inheritence!
    private final ArrayList<Command> vector = new ArrayList<>();

    // -------------------- Public Methods -------------------
    
    public UndoVector(int n) {
        maxsize = n;
    }
    
    public void add(Command c) {
        // Check if we are at the maximum size:
        if ( maxsize>0 && vector.size()==maxsize ) {
            // Remove the last element:
            vector.remove(maxsize-1);
        }
        // Add to the start of the vector:
        vector.add(0,c);
    }

    public Command get() {
        // Check for empty vector:
        if (vector.isEmpty()) { return null; }
        // Return the most recent item from the start of the vector:
        return vector.get(0);
    }

    public void remove() {
        // Check for empty vector:
        if (vector.isEmpty()) { return; }
        // Remove the most recent item from the start of the vector:
        vector.remove(0);
    }
    
    //public int size() { return vector.size(); }
    public boolean isEmpty() { return vector.isEmpty(); }
    
    public void clear() { vector.clear(); }
    
}
