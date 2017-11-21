package facetmodeller.plc;

import facetmodeller.groups.Group;
import facetmodeller.sections.Section;

/** An object with an ID value and attached to a section and group.
 * @author Peter Lelievre
 */
public class HasSection extends HasGroup {

    // -------------------- Properties -------------------

    private Section section = null; // each node and region is linked to a particular section, each facet can be attached to multiple sections

    // -------------------- Constructors -------------------

    public HasSection() { super(); }

    public HasSection(Section s, Group g) {
        super(g);
        section = s;
    }

    // -------------------- Getters -------------------

    public Section getSection() { return section; }

    // -------------------- Setters -------------------

    public void setSection(Section s) { section = s; }

}