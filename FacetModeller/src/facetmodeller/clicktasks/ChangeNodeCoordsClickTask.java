package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ChangeNodeCoordinateCommand;
import facetmodeller.commands.Command;
import facetmodeller.commands.MergeNodesCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
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
        // Check for on-section node:
        boolean do3D = true;
        if (!node.isOff()) {
            // Ask user how to continue:
            int response = Dialogs.question(controller,"What coordinates do you want to specify for this on-section node?",title(),"3D spatial","2D pixel","Cancel");
            if (response==Dialogs.CANCEL_OPTION) { return; }
            do3D = (response==Dialogs.YES_OPTION);
            if (do3D) {
                response = Dialogs.confirm(controller,"The node will be changed to a 3D off-section node.",title());
                if (response!=Dialogs.OK_OPTION) { return; }
            }
        }
        // Do whatever is required:
        Command com;
        if (do3D) { // it's an off-section node, or we're converting an no-section node to an off-section node, and we're changing the 3D spatial coordinates
            // Get the node's existing 3D coordinates:
            MyPoint3D p3 = node.getPoint3D();
            // Ask the user for the new node coordinates:
            String message = "You must enter three numeric values separated by spaces. Please try again.";
            String prompt = "Enter the new 3D spatial coordinates (x y z) for the node, separated by spaces:";
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
            // Create a new 3D point object:
            p3 = new MyPoint3D(x,y,z);
            if (node.isOff()) {
                // Change the 3D spatial coordinates of the off-section node:
                com = new ChangeNodeCoordinateCommand((NodeOffSection)node,p3); com.execute();
            } else {
                // Create a new off-section node object at the specified 3D spatial coordinates, attached to the same section and group as the old node:
                Node newNode = new NodeOffSection(p3,node.getSection(),node.getGroup());
                // Replace the old node with the new node:
                com = new MergeNodesCommand(controller.getModelManager(),node,newNode,title()); com.execute();
            }
        } else { // it's an on-section node and we're changing the 2D pixel coordinates
            // Get the node's existing 2D coordinates:
            MyPoint2D p2 = node.getPoint2D();
            // Ask the user for the new node coordinates:
            String message = "You must enter two numeric values separated by spaces. Please try again.";
            String prompt = "Enter the new 2D pixel coordinates (x y) for the node, separated by spaces:";
            String input = Dialogs.input(controller,prompt,title(),p2.toString());
            if (input==null) { return; } // user cancelled
            input = input.trim();
            String s[];
            s = input.split("[ ]+");
            if (s.length!=2) {
                Dialogs.error(controller,message,title());
                return;
            }
            double x,y;
            try {
                x = Double.parseDouble(s[0].trim());
                y = Double.parseDouble(s[1].trim());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Dialogs.error(controller,message,title());
                return;
            }
            // Create a new 2D point object:
            p2 = new MyPoint2D(x,y);
            // Change the 2D pixel coordinates of the on-section node:
            com = new ChangeNodeCoordinateCommand((NodeOnSection)node,p2); com.execute();
        }
        // Add the command to the undo information:
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
