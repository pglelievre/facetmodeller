package geometry;

import java.util.ArrayList;

/** A Vector of MyPoint3D objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class MyPoint3DVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private final ArrayList<MyPoint3D> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void MyPoint3DVector() {}

    // -------------------- Public Methods -------------------

    /** Clears the vector. */
    public void clear() {
        vector.clear();
    }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public MyPoint3D get(int i) {
        return vector.get(i);
    }

    /** Adds an element to the end of the vector.
     * @param p The MyPoint2D object to add to the end of the vector.
     */
    public void add(MyPoint3D p) {
        vector.add(p);
    }
    
}
