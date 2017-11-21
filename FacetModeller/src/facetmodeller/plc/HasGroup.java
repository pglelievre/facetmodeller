package facetmodeller.plc;

import facetmodeller.groups.Group;

/** An object with an ID value and attached to a group.
 * @author Peter Lelievre
 */
public class HasGroup extends HasID {

    // -------------------- Properties -------------------

    private Group group = null; // each node, facet and region is linked to a particular group

    // -------------------- Constructors -------------------

    public HasGroup() { super(); }

    public HasGroup(Group g) {
        super();
        group = g;
    }

    // -------------------- Getters -------------------

    public Group getGroup() { return group; }

    // -------------------- Setters -------------------

    public void setGroup(Group g) { group = g; }

}