package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import fileio.SessionIO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** The tool panel (selectors, radio buttons etc).
 * @author Peter
 */
public final class ToolPanel extends JPanel implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    private FacetModeller controller;
    private RadioButtonsPanel radioButtonsPanel;
    private SectionSelectorPanel sectionSelectorPanel;
    private GroupSelectorPanel groupSelectorPanel;
    private ShiftOrPanPanel shiftOrPanPanel;
    private ClickModeManager clickModeManager;
    
    public ToolPanel(FacetModeller con, int ndim) {
        
        // Set the controller:
        controller = con;
        
        // Instantiate the clickModeManager:
        clickModeManager = new ClickModeManager(con);
        
        // Make whatever JPanels are required based on the number of dimensions:
        radioButtonsPanel = new RadioButtonsPanel(con,ndim);
        groupSelectorPanel = new GroupSelectorPanel(con);
        shiftOrPanPanel = new ShiftOrPanPanel(con,ndim);
        JPanel selectorPanel = new JPanel();
        if (ndim==3) {
            sectionSelectorPanel = new SectionSelectorPanel(controller);
            // Add the radioButtonsPanel, sectionSelectorPanel and shiftOrPanPanel to a single panel:
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(radioButtonsPanel,BorderLayout.NORTH);
            panel.add(sectionSelectorPanel,BorderLayout.CENTER);
            panel.add(shiftOrPanPanel,BorderLayout.SOUTH);
            // Combine that panel with the groupSelectorPanel:
            selectorPanel.setLayout(new GridLayout(1,2));
            selectorPanel.add(panel);
            selectorPanel.add(groupSelectorPanel);
        } else {
            // Add the radioButtonsPanel, groupSelectorPanel and shiftOrPanPanel to a single panel:
            selectorPanel.setLayout(new BorderLayout());
            //selectorPanel.add(showImageButton,BorderLayout.NORTH);
            selectorPanel.add(radioButtonsPanel,BorderLayout.NORTH); // a single showImageButton
            selectorPanel.add(groupSelectorPanel,BorderLayout.CENTER);
            selectorPanel.add(shiftOrPanPanel,BorderLayout.SOUTH);
            // (for the 2D GUI there are no section selectors)
        }
        
        // Add the section top panel and selector panel to this panel:
        setLayout(new BorderLayout());
        add(selectorPanel,BorderLayout.CENTER);
        add(clickModeManager.getClickModelSelector(),BorderLayout.SOUTH);
        setBorder(BorderFactory.createEtchedBorder());
        
    }

    // Wrappers for the RadioButtonsPanel class:
    public boolean getShowImage() { return radioButtonsPanel.getShowImage(); }
    public boolean getShowOutlines() { return radioButtonsPanel.getShowOutlines(); }
    public boolean getShowAll() { return radioButtonsPanel.getShowAll(); }
    public boolean getShowVOI() { return radioButtonsPanel.getShowVOI(); }
    public boolean getShowFaces() { return radioButtonsPanel.getShowFaces(); }
    public boolean getShowRegions() { return radioButtonsPanel.getShowRegions(); }
    public boolean getNodeColorBySection() { return radioButtonsPanel.getNodeColorBySection(); }
    
    // Wrappers for the SectionSelectorPanel class:
    public Section getSelectedCurrentSection() {
        if (sectionSelectorPanel==null) { return null; }
        return sectionSelectorPanel.getSelectedCurrentSection();
    }
    public SectionVector getSelectedOtherSections() {
        if (sectionSelectorPanel==null) { return null; }
        return sectionSelectorPanel.getSelectedOtherSections();
    }
    public int getSelectedCurrentSectionIndex() {
        if (sectionSelectorPanel==null) { return -1; }
        return sectionSelectorPanel.getSelectedCurrentSectionIndex();
    }
    public int[] getSelectedOtherSectionIndices() {
        if (sectionSelectorPanel==null) { return null; }
        return sectionSelectorPanel.getSelectedOtherSectionIndices();
    }
    public void setSectionVector(SectionVector v) {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.setSectionVector(v);
    }
    public void setSelectedCurrentSectionIndex(int s) {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.setSelectedCurrentSectionIndex(s);
    }
    public void setSelectedOtherSectionIndices(int[] s) {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.setSelectedOtherSectionIndices(s);
    }
    public void setSelectedCurrentSection(Section s) {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.setSelectedCurrentSection(s);
    }
    public void setSelectedOtherSections(SectionVector s) {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.setSelectedOtherSections(s);
    }
    public void clearCurrentSectionSelection() {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.clearCurrentSectionSelection();
    }
    public void clearOtherSectionSelection() {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.clearOtherSectionSelection();
    }
    public void updateSectionSelectors() {
        if (sectionSelectorPanel==null) { return; }
        sectionSelectorPanel.update();
    }
    
    // Wrappers for the GroupSelectorPanel class:
    public Group getSelectedCurrentGroup() { return groupSelectorPanel.getSelectedCurrentGroup(); }
    public GroupVector getSelectedCurrentGroups() { return groupSelectorPanel.getSelectedCurrentGroups(); }
    public GroupVector getSelectedNodeGroups() { return groupSelectorPanel.getSelectedNodeGroups(); }
    public GroupVector getSelectedFacetGroups() { return groupSelectorPanel.getSelectedFacetGroups(); }
    public int getSelectedCurrentGroupIndex() { return groupSelectorPanel.getSelectedCurrentGroupIndex(); }
    public int[] getSelectedNodeGroupIndices() { return groupSelectorPanel.getSelectedNodeGroupIndices(); }
    public int[] getSelectedFacetGroupIndices() { return groupSelectorPanel.getSelectedFacetGroupIndices(); }
    public boolean isSelectedNodeGroup(Group g) { return groupSelectorPanel.isSelectedNodeGroup(g); }
    public boolean isSelectedFacetGroup(Group g) { return groupSelectorPanel.isSelectedFacetGroup(g); }
    public boolean isSelectedFacetGroupIndex(int i) { return groupSelectorPanel.isSelectedFacetGroupIndex(i); }
    public void setGroupVector(GroupVector v) { groupSelectorPanel.setGroupVector(v); }
    public void setSelectedCurrentGroup(Group s) { groupSelectorPanel.setSelectedCurrentGroup(s);  }
    public void setSelectedCurrentGroupIndex(int s) { groupSelectorPanel.setSelectedCurrentGroupIndex(s);  }
    public void setSelectedNodeGroups(GroupVector s) { groupSelectorPanel.setSelectedNodeGroups(s); }
    public void setSelectedNodeGroupIndex(int s) { groupSelectorPanel.setSelectedNodeGroupIndex(s); }
    public void setSelectedNodeGroupIndices(int[] s) { groupSelectorPanel.setSelectedNodeGroupIndices(s); }
    public void setSelectedFacetGroups(GroupVector s) { groupSelectorPanel.setSelectedFacetGroups(s); }
    public void setSelectedFacetGroupIndices(int[] s) { groupSelectorPanel.setSelectedFacetGroupIndices(s); }
    public void clearCurrentGroupSelection() { groupSelectorPanel.clearCurrentGroupSelection(); }
    public void clearFacetGroupSelection() { groupSelectorPanel.clearFacetGroupSelection(); }
    public void clearGroupSelections() { groupSelectorPanel.clearSelections(); }
    public void updateGroupSelectors() { groupSelectorPanel.update(); }
    public void addToNodeGroupSelection(Group g) { groupSelectorPanel.addToNodeGroupSelection(g); }
    public void addToFacetGroupSelection(Group g) { groupSelectorPanel.addToFacetGroupSelection(g); }
    
    // Wrappers for the ShiftOrPanPanel class:
    public int getShiftingX() { return shiftOrPanPanel.getShiftingX(); }
    public int getShiftingY() { return shiftOrPanPanel.getShiftingY(); }
    public int getPanning2DX() { return shiftOrPanPanel.getPanning2DX(); }
    public int getPanning2DY() { return shiftOrPanPanel.getPanning2DY(); }
    public int getShiftStep2D() { return shiftOrPanPanel.getShiftStep2D(); }
    public int getPanStep2D() { return shiftOrPanPanel.getPanStep2D(); }
    public void setShiftStep2D(int i) { shiftOrPanPanel.setShiftStep2D(i); }
    public void setPanStep2D(int i) { shiftOrPanPanel.setPanStep2D(i); }
    public void selectShiftStep2D() { shiftOrPanPanel.selectShiftStep2D(); }
    public void selectPanStep2D() { shiftOrPanPanel.selectPanStep2D(); }
    public void clearPan2D() { shiftOrPanPanel.clearPan2D(); }
    public void resetShiftButtonText() { shiftOrPanPanel.resetShiftButtonText(); }
    
    // Wrappers for the ClickModeManager class:
    public int getClickMode() { return clickModeManager.getClickMode(); }
    public void setClickMode(int mode) { clickModeManager.setClickMode(mode); }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!radioButtonsPanel.writeSessionInformation(writer)) { return false; }
        if ( sectionSelectorPanel!=null && !sectionSelectorPanel.writeSessionInformation(writer) ) { return false; }
        if (!groupSelectorPanel.writeSessionInformation(writer)) { return false; }
        return shiftOrPanPanel.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg;
        msg = radioButtonsPanel.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        if ( sectionSelectorPanel!=null ) {
            msg = sectionSelectorPanel.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        }
        msg = groupSelectorPanel.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        return shiftOrPanPanel.readSessionInformation(reader,merge);
    }
    
}
