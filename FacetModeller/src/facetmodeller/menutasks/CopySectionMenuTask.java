package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.AddNodeCommand;
import facetmodeller.plc.NodeVector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import facetmodeller.sections.SnapshotSection;

/** Copies the current section along with any nodes on it.
 * @author Peter
 */
public final class CopySectionMenuTask extends ControlledMenuTask {
    
    public CopySectionMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Copy section"; }

    @Override
    public String tip() { return "Copies the current section along with any nodes on it"; }

    @Override
    public String title() { return "Copy Section"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        return !(currentSection instanceof SnapshotSection);
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Store the selections in the group selector objects for use later:
        int ind = controller.getSelectedCurrentSectionIndex();
        SectionVector selectedOtherSections = controller.getSelectedOtherSections();
        // Deep copy the basic section information:
        Section currentSection = controller.getSelectedCurrentSection();
        Section newSection = currentSection.copySection();
        if (newSection==null) { return; } // this should only happen for a SnapshotSection (redundant check)
        // Deep copy the on-section nodes but don't link to any facets or sections:
        NodeVector newNodes = currentSection.getNodes().deepCopyNodesOnPointAndGroup();
        // Link the nodes to the new section:
        newNodes.setSection(newSection);
        // Add the nodes to the plc using the AddNodeCommand:
        ModelManager modelManager = controller.getModelManager();
        for (int i=0 ; i<newNodes.size() ; i++ ) {
            AddNodeCommand addNodeCommand = new AddNodeCommand(modelManager,newNodes.get(i),title());
            addNodeCommand.execute();
        }
        // Add the new section to the section vector:
        controller.addSection(newSection);
        // Update the clickable lists:
        controller.updateSectionSelectors();
        // Reset the selections:
        controller.setSelectedCurrentSectionIndex(ind+1); // positions the current section to the new section
        if (selectedOtherSections!=null) {
            selectedOtherSections.add(newSection);
            controller.setSelectedOtherSections(selectedOtherSections);
        }
        // Tell the controller that the section selection has changed:
        controller.sectionSelectionChanged(true);
    }
    
}
