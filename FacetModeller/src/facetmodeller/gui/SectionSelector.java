package facetmodeller.gui;

import facetmodeller.FacetModeller;
import facetmodeller.JListSelector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/** A JList selector with a list of sections.
 * @author Peter Lelievre
 */
public class SectionSelector extends JListSelector {
    private static final long serialVersionUID = 1L;

    // ------------------ Properties -------------------

    private SectionVector vector = null; // Set to null so I can have two SectionSelector objects sharing the same SectionVector object.
    
    // ------------------ Constructor ------------------

    public SectionSelector(FacetModeller con, boolean currentFlag) {
        super();

        // Set the selection mode to single:
        if (currentFlag) {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        // Add a listener for whenever the selection changes:
        addListSelectionListener(new SelectionListener(con,currentFlag));

    }

    // -------------------- Getters --------------------

    /** Getter for the first selected Section object or null if no sections exist.
     * @return  */
    public Section getSelectedSection() {
        // Check for no sections loaded:
        if (vector.isEmpty()) { return null; }
        int ind = getSelectedIndex();
        if (ind<0) { return null; }
        return vector.get(ind);
    }

    /** Getter for the selected Section objects or null if no sections exist.
     * @return  */
    public SectionVector getSelectedSections() {
        // Check for no sections loaded:
        if (vector.isEmpty()) { return null; }
        SectionVector sections = new SectionVector();
        int[] ind = getSelectedIndices();
        if (ind==null) { return null; }
        for (int i=0 ; i<ind.length ; i++) {
            Section s = vector.get(ind[i]);
            sections.add(s);
        }
        return sections;
    }

    // -------------------- Setters --------------------

    /** Setter for the list of sections.
     * @param s */
    public void setSectionVector(SectionVector s) {
        // Set the vector object:
        vector = s;
        // Update the clickable list:
        update();
        // Set first item as the selected item:
        setSelectedIndex(0);
    }

    /** Sets the selection to the section provided, or no selection if that section doesn't exist.
     * @param s */
    public void setSelectedSection(Section s) {
        // Clear selection and check for null section:
        clearSelection();
        if (s==null) { return; }
        // Find the section in the vector:
        int ind = vector.find(s); // = -1 if not found
        // Check if not found:
        if (ind<0) {
            clearSelection();
        } else {
            setSelectedIndex(ind);
        }
    }

    /** Sets the selection to the sections provided, or no selection if that section doesn't exist.
     * @param ss */
    public void setSelectedSections(SectionVector ss) {
        // Clear selection and check for null section vector:
        clearSelection();
        if (ss==null) { return; }
        // First make sure multi-select is on:
        if (getSelectionMode()!=ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
            // Set selection to first item is the vector:
            setSelectedSection(ss.get(0));
            return;
        }
        // Loop over each supplied section:
        ArrayList<Integer> indList = new ArrayList<>();
        for (int i=0 ; i<ss.size() ; i++ ) {
            // Find the index of the ith section in the vector:
            Section s = ss.get(i);
            int ind = -1;
            if (s!=null) { ind = vector.find(s); } // = -1 if not found
            // If found, add the index to the list of indices:
            if (ind>=0) { indList.add(ind); }
        }
        // Check for no sections found:
        if (indList.isEmpty()) {
            clearSelection();
        } else {
            // Convert ArrayList to Array:
            int[] selection = new int[indList.size()];
            for (int i=0; i<selection.length; i++) {
                selection[i] = indList.get(i);
            }
            // Set selected indices:
            setSelectedIndices(selection);
        }
    }

    // -------------------- Public Methods --------------------
    
    public int numberOfSections() { return vector.size(); }

    public void update() { setListData(vector.nameList()); }

    // -------------------- Monitors --------------------

    /** Listens for list selection changes. */
    private class SelectionListener implements ListSelectionListener {
        private final FacetModeller controller;
        private boolean currentFlag=false;
        public SelectionListener(FacetModeller con, boolean cf) {
            controller = con;
            currentFlag = cf;
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            controller.sectionSelectionChanged(currentFlag);
        }
    }
    
}
