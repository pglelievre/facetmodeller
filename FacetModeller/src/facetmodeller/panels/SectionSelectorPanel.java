package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.gui.SectionSelector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import fileio.SessionIO;
import gui.TextBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** The panel containing the two section selection objects.
 * @author Peter
 */
public final class SectionSelectorPanel extends JPanel implements SessionIO {
    private static final long serialVersionUID = 1L;

    private final SectionSelector currentSectionSelector, otherSectionSelector;
    private final JButton clearOtherButton;

    public SectionSelectorPanel(FacetModeller con) {
        
        // Create the section selector panels:
        currentSectionSelector = new SectionSelector(con,true);
        otherSectionSelector = new SectionSelector(con,false);
        
        // Create the clear other sections button:
        clearOtherButton = new JButton("Clear other sections");
        clearOtherButton.setToolTipText("Clears the Other Sections selected abive");
        clearOtherButton.setVerticalTextPosition(AbstractButton.CENTER);
        clearOtherButton.setHorizontalTextPosition(AbstractButton.CENTER);
        clearOtherButton.addActionListener(new ClearOtherButtonListener(con));
    
        // Create the parent scrollers and descriptions:
        JScrollPane currentSectionScroller = new JScrollPane(currentSectionSelector);
        JScrollPane otherSectionScroller = new JScrollPane(otherSectionSelector);
        TextBar currentSectionScrollerDesc = new TextBar();
        TextBar otherSectionScrollerDesc = new TextBar();
        currentSectionScrollerDesc.setText("Current Section");
        currentSectionScrollerDesc.setToolTipText("The section to display and add new nodes and regions to");
        otherSectionScrollerDesc.setText("Other Sections");
        String t = "The other sections to add node and facet overlays from;" + System.lineSeparator()
                + "the sections to display in the 3D viewer";
        otherSectionScrollerDesc.setToolTipText(t);

        // Make panels holding the scrollers and the descriptions:
        JPanel currentSectionPanel = new JPanel();
        currentSectionPanel.setLayout(new BorderLayout());
        currentSectionPanel.add(currentSectionScrollerDesc,BorderLayout.NORTH);
        currentSectionPanel.add(currentSectionScroller,BorderLayout.CENTER);
        JPanel otherSectionPanel = new JPanel();
        otherSectionPanel.setLayout(new BorderLayout());
        otherSectionPanel.add(otherSectionScrollerDesc,BorderLayout.NORTH);
        otherSectionPanel.add(otherSectionScroller,BorderLayout.CENTER);
        
        // Add those panels to a single panel:
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        panel.add(currentSectionPanel);
        panel.add(otherSectionPanel);
        
        // Add that panel and the clear other button to this panel:
        setLayout(new BorderLayout());
        add(panel,BorderLayout.CENTER);
        add(clearOtherButton,BorderLayout.SOUTH);
        
        // Set the preferred sizes of the scroller panels:
        Dimension dim = new Dimension(0,0); // a bit of a hack because the size gets determined by the other components
        currentSectionPanel.setPreferredSize(dim);
        otherSectionPanel.setPreferredSize(dim);
       
    }

    private class ClearOtherButtonListener implements ActionListener {
        private final FacetModeller controller;
        public ClearOtherButtonListener(FacetModeller con) {
            controller = con;
        }
        @Override
        public void actionPerformed(ActionEvent event) {
            clearOtherSectionSelection();
            controller.sectionSelectionChanged(false);
        }
    }
    
    public Section getSelectedCurrentSection() { return currentSectionSelector.getSelectedSection(); }
    public SectionVector getSelectedOtherSections() { return otherSectionSelector.getSelectedSections(); }
    public int getSelectedCurrentSectionIndex() { return currentSectionSelector.getSelectedIndex(); }
    public int[] getSelectedOtherSectionIndices() { return otherSectionSelector.getSelectedIndices(); }

    public void setSectionVector(SectionVector v) {
        // Both section selectors use the same vector:
        currentSectionSelector.setSectionVector(v);
        otherSectionSelector.setSectionVector(v);
    }
    
    public void setSelectedCurrentSection(Section s) { currentSectionSelector.setSelectedSection(s); }
    public void setSelectedCurrentSectionIndex(int s) { currentSectionSelector.setSelectedIndex(s); }
    public void setSelectedOtherSections(SectionVector s) { otherSectionSelector.setSelectedSections(s); }
    public void setSelectedOtherSectionIndices(int[] s) { otherSectionSelector.setSelectedIndices(s); }

    public void clearCurrentSectionSelection() { currentSectionSelector.clearSelection(); }
    public void clearOtherSectionSelection() { otherSectionSelector.clearSelection(); }

    public void update() {
        currentSectionSelector.update();
        otherSectionSelector.update();
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!currentSectionSelector.writeSessionInformation(writer)) { return false; }
        return otherSectionSelector.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = currentSectionSelector.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        return otherSectionSelector.readSessionInformation(reader,merge);
    }
    
}
