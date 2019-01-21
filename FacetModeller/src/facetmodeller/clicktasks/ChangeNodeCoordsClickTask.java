package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.MergeNodesCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

public final class ChangeNodeCoordsClickTask extends ControlledClickTask {
    
    public ChangeNodeCoordsClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_CHANGE_NODES_COORDS; }

    @Override
    public String text() { return ClickTaskUtil.CHANGE_NODE_COORDS_TEXT; }

    @Override
    public String tip() { return "Change a node's coordinates via editing in a text input dialog"; }

    @Override
    public String title() { return ClickTaskUtil.CHANGE_NODE_COORDS_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        //if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node node = controller.getClosestNode(); // just in case the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        if (node==null) { return; }
        // Nullify temporary objects:
        controller.clearClosestNode(); // (or else the old closest node point will be painted)
        // Ask user for confirmation, if required, and get 3D node coordinates:
        if (!node.isOff()) {
            int response = Dialogs.confirm(controller,"The node will be changed to a 3D off-section node.",title());
            if (response!=Dialogs.OK_OPTION) { return; }
        }
        // Get the existing node coordinates:
        MyPoint3D p3 = node.getPoint3D();
        // Ask the user for the new node coordinates:
        String message = "You must enter three numeric values separated by spaces. Please try again.";
        String prompt = "Enter the new 3D coordinates (x y z) for the node, separated by spaces:";
        String input = Dialogs.input(controller,prompt,title(),p3.toString());
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
        // Create a new off-section node object at the specified coordinates, attached to the same section and group as the old node:
        p3 = new MyPoint3D(x,y,z);
        Node newNode = new NodeOffSection(p3,node.getSection(),node.getGroup());
        // Replace the old node with the new node:
        MergeNodesCommand com = new MergeNodesCommand(controller.getModelManager(),node,newNode,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest node to the cursor position:
        controller.calculateClosestNode(p);
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest node:
        controller.updateClosestBar(p);
    }
    
}
