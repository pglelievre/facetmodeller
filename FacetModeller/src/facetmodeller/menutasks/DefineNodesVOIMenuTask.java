package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommandVector;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeVector;
import geometry.MyPoint3D;

/** Defines new nodes on the corners of the VOI.
 * @author Peter
 */
public final class DefineNodesVOIMenuTask extends ControlledMenuTask {
    
    public DefineNodesVOIMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Add nodes on VOI corners"; }

    @Override
    public String tip() { return "Defines new nodes on the corners of the VOI"; }

    @Override
    public String title() { return "Add Nodes on VOI Corners"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        //Section currentSection = controller.getSelectedCurrentSection();
        //if (currentSection==null) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasVOI();
        //return currentSection.canAddNodesOnSection();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask the user for confirmation:
        int response = Dialogs.confirm(controller,"The nodes will be added as 3D off-section nodes.",title());
        if (response!=Dialogs.OK_OPTION) { return; }
        // Define 8 new off-section nodes:
        NodeVector nodes = new NodeVector();
        MyPoint3D[] p = controller.getVOICorners();
        for (int i=0 ; i<8 ; i++ ) {
            nodes.add( new NodeOffSection(p[i],controller.getSelectedCurrentSection(),controller.getSelectedCurrentGroup()) ); // new node is not added to the section or group yet
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
