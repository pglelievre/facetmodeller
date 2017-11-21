package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.GroupSelector;
import fileio.SessionIO;
import gui.TextBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** The panel containing the three group selection objects.
 * @author Peter
 */
public final class GroupSelectorPanel extends JPanel implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    private GroupSelector currentGroupSelector, nodeGroupSelector, facetGroupSelector;

    public GroupSelectorPanel(FacetModeller con) {
        
        // Create the group selector panels:
        currentGroupSelector = new GroupSelector(con,true,false); // single selection, don't redraw
        nodeGroupSelector    = new GroupSelector(con,false,true); // multiple selection, redraw
        facetGroupSelector   = new GroupSelector(con,false,true); // multiple selection, redraw
    
        // Create the parent scrollers and descriptions:
        JScrollPane groupScroller = new JScrollPane(currentGroupSelector);
        JScrollPane nodePaintScroller = new JScrollPane(nodeGroupSelector);
        JScrollPane facetPaintScroller = new JScrollPane(facetGroupSelector);
        TextBar groupScrollerDesc = new TextBar();
        TextBar nodePaintScrollerDesc = new TextBar();
        TextBar facetPaintScrollerDesc = new TextBar();
        groupScrollerDesc.setText("Current Group");
        groupScrollerDesc.setToolTipText("The group to add new nodes, facets or regions to");
        nodePaintScrollerDesc.setText("Node Groups");
        nodePaintScrollerDesc.setToolTipText("The node groups to display");
        facetPaintScrollerDesc.setText("Facet Groups");
        facetPaintScrollerDesc.setToolTipText("The facet groups to display");

        // Make panels holding the scrollers and the descriptions:
        JPanel currentGroupPanel = new JPanel();
        currentGroupPanel.setLayout(new BorderLayout());
        currentGroupPanel.add(groupScrollerDesc,BorderLayout.NORTH);
        currentGroupPanel.add(groupScroller,BorderLayout.CENTER);
        JPanel nodeGroupPanel = new JPanel();
        nodeGroupPanel.setLayout(new BorderLayout());
        nodeGroupPanel.add(nodePaintScrollerDesc,BorderLayout.NORTH);
        nodeGroupPanel.add(nodePaintScroller,BorderLayout.CENTER);
        JPanel facetGroupPanel = new JPanel();
        facetGroupPanel.setLayout(new BorderLayout());
        facetGroupPanel.add(facetPaintScrollerDesc,BorderLayout.NORTH);
        facetGroupPanel.add(facetPaintScroller,BorderLayout.CENTER);
        
        // Add those panels to this panel:
        setLayout(new GridLayout(3,1));
        add(currentGroupPanel);
        add(nodeGroupPanel);
        add(facetGroupPanel);
        
        // Set the preferred sizes of the scroller panels:
        Dimension dim = new Dimension(0,0); // a bit of a hack because the size gets determined by the other components
        currentGroupPanel.setPreferredSize(dim);
        nodeGroupPanel.setPreferredSize(dim);
        facetGroupPanel.setPreferredSize(dim);
        
    }
    
    public Group getSelectedCurrentGroup() { return currentGroupSelector.getSelectedGroup(); }
    public GroupVector getSelectedCurrentGroups() { return currentGroupSelector.getSelectedGroups(); }
    public GroupVector getSelectedNodeGroups() { return nodeGroupSelector.getSelectedGroups(); }
    public GroupVector getSelectedFacetGroups() { return facetGroupSelector.getSelectedGroups(); }
    public int getSelectedCurrentGroupIndex() { return currentGroupSelector.getSelectedIndex(); }
    public int[] getSelectedNodeGroupIndices() { return nodeGroupSelector.getSelectedIndices(); }
    public int[] getSelectedFacetGroupIndices() { return facetGroupSelector.getSelectedIndices(); }
    
    public boolean isSelectedCurrentGroup(Group g) { return currentGroupSelector.isSelected(g); }
    public boolean isSelectedNodeGroup(Group g) { return nodeGroupSelector.isSelected(g); }
    public boolean isSelectedFacetGroup(Group g) { return facetGroupSelector.isSelected(g); }
    public boolean isSelectedFacetGroupIndex(int i) { return facetGroupSelector.isSelectedIndex(i); }
    
    public void setGroupVector(GroupVector v) {
        // All group selectors use the same vector:
        currentGroupSelector.setGroupVector(v);
        nodeGroupSelector.setGroupVector(v);
        facetGroupSelector.setGroupVector(v);
    }

    public void setSelectedCurrentGroup(Group s) { currentGroupSelector.setSelectedGroup(s);  }
    public void setSelectedCurrentGroupIndex(int s) { currentGroupSelector.setSelectedIndex(s);  }
    public void setSelectedNodeGroups(GroupVector s) { nodeGroupSelector.setSelectedGroups(s); }
    public void setSelectedNodeGroupIndex(int s) { nodeGroupSelector.setSelectedIndex(s); }
    public void setSelectedNodeGroupIndices(int[] s) { nodeGroupSelector.setSelectedIndices(s); }
    public void setSelectedFacetGroups(GroupVector s) { facetGroupSelector.setSelectedGroups(s); }
    public void setSelectedFacetGroupIndices(int[] s) { facetGroupSelector.setSelectedIndices(s); }
    
    public void clearCurrentGroupSelection() { currentGroupSelector.clearSelection(); }
    public void clearNodeGroupSelection() { nodeGroupSelector.clearSelection(); }
    public void clearFacetGroupSelection() { facetGroupSelector.clearSelection(); }
    public void clearSelections() {
        currentGroupSelector.clearSelection();
        clearNodeGroupSelection();
        clearFacetGroupSelection();
    }
    
    public void addToNodeGroupSelection(Group g) { nodeGroupSelector.addToSelection(g); }
    public void addToFacetGroupSelection(Group g) { facetGroupSelector.addToSelection(g); }
    
    public void update() {
        currentGroupSelector.update();
        nodeGroupSelector.update();
        facetGroupSelector.update();
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!currentGroupSelector.writeSessionInformation(writer)) { return false; }
        if (!nodeGroupSelector.writeSessionInformation(writer)) { return false; }
        return facetGroupSelector.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg;
        msg = currentGroupSelector.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        msg = nodeGroupSelector.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        return facetGroupSelector.readSessionInformation(reader,merge);
    }
    
}
