package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.MoveNodeCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

public final class MoveNodeClickTask extends ControlledClickTask {
    
    public MoveNodeClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_MOVE_NODES; }

    @Override
    public String tip() { return "Move nodes in the plane of the current section"; }

    @Override
    public String title() { return ClickTaskUtil.MOVE_NODE_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        
        // Get the current section:
        Section currentSection = controller.getSelectedCurrentSection();
        
        // Check if a node has already been chosen for moving:
        if (!controller.checkCurrentNode()) { // the node to move has not yet been chosen
            
            // Calculate the closest node to the clicked point:
            if (!controller.calculateClosestNode(p)) { return; }
            Node node = controller.getClosestNode(); // just in case the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
            // If the node is on-section then make sure the node is on the current section:
            if ( !node.isOff() && !node.getSection().equals(currentSection) ) { return; }
            // Save the node for later:
            controller.setCurrentNode(node);

        } else { // the node to move has already been chosen
            
            // If the node is on-section then make sure the node is on the current section:
            Node currentNode = controller.getCurrentNode();
            if (currentNode==null) { // something went wrong so reset and return
                controller.clearCurrentNode();
                return;
            }
            boolean isOff = currentNode.isOff();
            if ( !isOff && !currentNode.getSection().equals(currentSection) ) {
                controller.clearCurrentNode();
                controller.redraw();
                return;
            }
            
            // Check for off-section node:
            MoveNodeCommand com;
            if (isOff) {
                // Move the off-section node laterally ...
                // Get the 3D coordinates of the point:
                MyPoint3D point3 = currentNode.getPoint3D().deepCopy();
                if (point3==null) { return; }
                // Project onto current section:
                MyPoint2D p2 = currentSection.projectOnto(point3); // image pixel coordinates
                if (p2==null) { return; }
                // Convert points from image pixel coordinates to spatial coordinates:
                MyPoint3D p0 = currentSection.imageToSpace(p); // clicked point
                if (p0==null) { return; }
                MyPoint3D p3 = currentSection.imageToSpace(p2); // projected node
                if (p3==null) { return; }
                // Calculate the 3D spatial vector between the two points on the image:
                // (the vector will lie in the plane of the section, e.g. laterally to the current 2D view)
                MyPoint3D v = MyPoint3D.minus(p0,p3);
                // Move the off-section node by that vector:
                point3.plus(v);
                com = new MoveNodeCommand(currentNode,null,point3);
            } else {
                // Move the on-section node to the click point:
                com = new MoveNodeCommand(currentNode,p,null);
            }
            com.execute();
            controller.undoVectorAdd(com);
            controller.clearCurrentNode(); // to reset the move node mode
            
        }

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
