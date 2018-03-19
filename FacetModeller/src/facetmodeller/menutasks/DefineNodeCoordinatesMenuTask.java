package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommand;
import facetmodeller.groups.Group;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.sections.Section;
import geometry.MyPoint3D;

/** Defines a single new node at user-specified coordinates.
 * @author Peter
 */
public final class DefineNodeCoordinatesMenuTask extends ControlledMenuTask {
    
    public DefineNodeCoordinatesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Add node at specified coordinates"; }

    @Override
    public String tip() { return "Defines a new 3D off-section node at specified coordinates"; }

    @Override
    public String title() { return "Add Node at Specified Coordinates"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return currentSection.canAddNodesOnSection();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the current section and group:
        Section currentSection = controller.getSelectedCurrentSection();
        Group currentGroup = controller.getSelectedCurrentGroup();
        // Ask the user for the coordinates:
        String message = "You must enter three numeric values separated by spaces. Please try again.";
        String prompt = "Enter the 3D coordinates (x y z) for the new node, separated by spaces:";
        String input = Dialogs.input(controller,prompt,title());
        if (input==null) { return; } // user cancelled
        input = input.trim();
        String s[];
        s = input.split("[ ]+");
        if (s.length!=3) {
            Dialogs.error(controller,message,title());
            return;
        }
        double x,y,z;
        try {
            x = Double.parseDouble(s[0].trim());
            y = Double.parseDouble(s[1].trim());
            z = Double.parseDouble(s[2].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(controller,message,title());
            return;
        }
        // Define a new off-section node:
        MyPoint3D p = new MyPoint3D(x,y,z);
        Node node = new NodeOffSection(p,currentSection,currentGroup); // not added to the section or group yet
        // Add the node:
        AddNodeCommand com = new AddNodeCommand(controller.getModelManager(),node,title()); com.execute(); // adds the node to its section and group
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
