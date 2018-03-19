package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommandVector;
import facetmodeller.groups.Group;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.plc.NodeVector;
import facetmodeller.sections.Section;
import geometry.MyPoint2DVector;

/** Defines new nodes on the corners of the current section.
 * @author Peter
 */
public final class DefineNodesSectionMenuTask extends ControlledMenuTask {
    
    public DefineNodesSectionMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Add nodes on section corners"; }

    @Override
    public String tip() { return "Defines new nodes on the corners of the current section"; }

    @Override
    public String title() { return "Add Nodes on Current Section Corners"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return currentSection.canAddNodesOnSection();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Define 8 new on-section nodes:
        Section currentSection = controller.getSelectedCurrentSection();
        Group currentGroup = controller.getSelectedCurrentGroup();
        if (currentSection==null) { return; }
        if (currentGroup==null) { return; }
        NodeVector nodes = new NodeVector();
        MyPoint2DVector p = currentSection.getCorners();
        if (p==null) { return; }
        int n = p.size();
        if (n!=4) { return; }
        for (int i=0 ; i<n ; i++ ) {
            nodes.add( new NodeOnSection(p.get(i),currentSection,currentGroup) ); // new node is not added to the section or group yet
        }
        // Add the nodes:
        AddNodeCommandVector com = new AddNodeCommandVector(controller.getModelManager(),nodes,""); com.execute(); // adds each node to its section and group
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
