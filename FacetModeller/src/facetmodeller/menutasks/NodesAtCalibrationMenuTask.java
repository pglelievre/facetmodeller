package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommandVector;
import facetmodeller.groups.Group;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.plc.NodeVector;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;

/** Makes 4 new nodes on the current section as guided by the calibration points.
 * @author Peter
 */
public final class NodesAtCalibrationMenuTask extends ControlledMenuTask {
    
    public NodesAtCalibrationMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Make nodes around calibration rectangle"; }

    @Override
    public String tip() { return "Adds four new nodes at the corners of the calibration rectangle on the current section."; }

    @Override
    public String title() { return "Make Nodes Around Calibration Rectangle"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if ( currentSection == null ) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return ( currentSection.canAddNodesOnSection() && currentSection.isCalibrated() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the current section and group:
        Section currentSection = controller.getSelectedCurrentSection();
        Group currentGroup = controller.getSelectedCurrentGroup();
        
        // Add 4 new nodes to the section, 
        MyPoint2D p1 = currentSection.getClicked1();
        MyPoint2D p2 = currentSection.getClicked2();
        MyPoint2D[] p = new MyPoint2D[4];
        p[0] = p1.deepCopy();
        p[1] = p2.deepCopy();
        p[2] = new MyPoint2D(p1.getX(),p2.getY());
        p[3] = new MyPoint2D(p2.getX(),p1.getY());
        NodeVector nodes = new NodeVector();
        for (int i=0 ; i<4 ; i++ ) {
            Node n = new NodeOnSection(p[i],currentSection,currentGroup); // new node is not added to the section or group yet
            nodes.add(n);
        }
        AddNodeCommandVector com = new AddNodeCommandVector(controller.getModelManager(),nodes,title()); com.execute(); // adds the node to the section and group
        controller.undoVectorAdd(com);
        
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Redraw:
        controller.redraw();
        
    }
    
}
