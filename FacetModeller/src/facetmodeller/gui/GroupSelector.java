package facetmodeller.gui;

import facetmodeller.FacetModeller;
import facetmodeller.JListSelector;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import java.util.Arrays;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/** A JList selector with a vector of groups.
 * @author Peter Lelievre
 */
public class GroupSelector extends JListSelector {
    private static final long serialVersionUID = 1L;

    // ------------------ Properties -------------------

    private GroupVector vector = null; // Set to null so I can have two GroupSelector objects sharing the same GroupVector object.

    // ------------------ Constructor ------------------

    public GroupSelector(FacetModeller con, boolean single, boolean doRedraw) {
        super();

        // Set the selection mode to multiple:
        if (single) {
            this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        // Add a listener for whenever the selection changes:
        this.addListSelectionListener(new SelectionListener(con,doRedraw));

    }

    // -------------------- Getters --------------------

    public int numberSelected() { return getSelectedIndices().length; }

    /** Getter for the current group object (first if multiple are selected) or null if no groups exist.
     * @return  */
    public Group getSelectedGroup() {
        // Check for no groups loaded:
        if (vector.isEmpty()) {
            return null;
        } else {
            int i = getSelectedIndex();
            if (i<0) { return null; }
            return vector.get(i);
        }
    }
    
    /** Getter for the selected group objects.
     * @return  */
    public GroupVector getSelectedGroups() {
        GroupVector groups = new GroupVector();
        // Check for no groups loaded:
        if (vector.isEmpty()) {
            return groups;
        } else {
            int[] selected = getSelectedIndices();
            if (selected==null) { return groups; }
            int n = selected.length;
            if (n==0) { return groups; }
            for (int i=0 ; i<n ; i++) {
                Group g = vector.get(selected[i]);
                groups.add(g);
            }
            return groups;
        }
    }

    // -------------------- Setters --------------------

    /** Setter for the current group object.
     * @param g The group object to set as the current group (must be one of the groups in the selection list). */
    public void setSelectedGroup(Group g) {
        // First check the supplied group is actually in the selection list:
        int f = vector.indexOf(g);
        if (f<0) { return; } // the supplied group is not in the selection list
        // Now set the selected index:
        this.setSelectedIndex(f);
    }
    
    public void setSelectedGroups(GroupVector groups) {
        // First check the supplied groups are are actually in the selection list:
        int[] f = new int[groups.size()];
        int n = 0; // counts the number in the selection list
        for (int i=0 ; i<groups.size() ; i++) { // loop over each supplied group
            f[i] = vector.indexOf(groups.get(i));
            if (f[i]>=0) { n++; } // the supplied group is in the selection list
        }
        // Now create the selection array:
        int[] selected = new int[n];
        n = 0;
        for (int i=0 ; i<groups.size() ; i++) {
            if (f[i]>=0) {
                selected[n] = f[i];
                n++;
            }
        }
        this.setSelectedIndices(selected);
    }

    /** Setter for the list of groups.
     * @param s */
    public void setGroupVector(GroupVector s) {
        // Set the vector object:
        vector = s;
        // Update the clickable list:
        update();
        // Check the selection mode:
        if (getSelectionMode()==ListSelectionModel.SINGLE_SELECTION) {
            // Set the first group to the first group:
            super.setSelectedIndex(0);
        } else {
            // Set the selection to all groups:
            super.setSelectionInterval(0,vector.size()-1);
        }
    }

    // -------------------- Public Methods --------------------

    public void update() { setListData(vector.nameList()); }
    
    public boolean isSelected(Group g) {
        int i = vector.indexOf(g);
        if (i<0) { return false; }
        return isSelectedIndex(i);
    }
    
    public void addToSelection(Group g) {
        int i = vector.indexOf(g);
        if (i<0) { return; }
        int[] ii = getSelectedIndices();
        int n = ii.length;
        int[] ii2 = new int[n+1];
        System.arraycopy(ii,0,ii2,0,n);
        ii2[n] = i;
        Arrays.sort(ii2);
        this.setSelectedIndices(ii2);
    }

    // -------------------- Monitors --------------------

    /** Listens for list selection changes. */
    private class SelectionListener implements ListSelectionListener {
        private final FacetModeller controller;
        private boolean doRedraw=false;
        public SelectionListener(FacetModeller con, boolean rd) {
            controller = con;
            doRedraw = rd;
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            controller.groupSelectionChanged(doRedraw);
        }
    }
    
}
