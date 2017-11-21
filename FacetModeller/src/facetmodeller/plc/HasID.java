package facetmodeller.plc;

/** An object with an ID value.
 * @author Peter Lelievre
 */
public class HasID {

    // -------------------- Properties -------------------

    private int id = 0; // each node can have an id value attached to it

    // -------------------- Constructors -------------------

    public HasID() {}

    // -------------------- Getters -------------------

    public int getID() { return id; }

    // -------------------- Setters -------------------

    public void setID(int i) { id = i; }

}