package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.MergeNodesCommand;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import geometry.MyPoint2D;

// Merges two nodes. The second node clicked replaces the first node clicked (the first node is deleted).
public final class MergeNodesClickTask extends ControlledClickTask {
    
    public MergeNodesClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_MERGE_NODES; }

    @Override
    public String tip() { return "Merge one node into another"; }

    @Override
    public String title() { return ClickTaskUtil.MERGE_NODE_TITLE; }

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
        
        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node clickedNode = controller.getClosestNode(); // just in case the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        Node currentNode = controller.getCurrentNode();
        
        // Check if the first node has already been chosen:
        if (currentNode==null) { // the first node has not yet been chosen
            
            // Save the clicked node for later:
            controller.setCurrentNode(clickedNode);

        } else { // the first node has already been chosen
            
            // New pointers to keep things straight:
            Node node1 = currentNode;
            Node node2 = clickedNode;
            //if ( node1==null || node2==null ) { // node1 can never be null here
            if ( node2==null ) { // something went wrong so reset and redraw
                controller.clearCurrentNode();
                controller.redraw();
                return;
            }
            
            // Check if the same node was clicked twice:
            boolean sameClickedTwice = ( node1==node2 );
            
            // Ask the user for confirmation:
            if (controller.getShowConfirmationDialogs()) {
                String prompt;
                if (sameClickedTwice) {
                    prompt = "Are you sure you want to replace that node with its closest neighbour?";
                } else {
                    prompt = "Are you sure you want to replace the first node you clicked with the second?";
                }
                int response = Dialogs.confirm(controller,prompt,title());
                if (response!=Dialogs.OK_OPTION) {
                    controller.clearCurrentNode();
                    controller.redraw();
                    return;
                }
            }
            
            // Find the closest node:
            ModelManager model = controller.getModelManager();
            if (sameClickedTwice) {
                node2 = model.findClosestNode(node1);
                if ( node2==null ) { // something went wrong so reset and redraw
                    controller.clearCurrentNode();
                    controller.redraw();
                    return;
                }
            }
            
            // Replace the first node with the second:
            MergeNodesCommand com = new MergeNodesCommand(model,node1,node2,title()); com.execute();
            controller.undoVectorAdd(com);
            
            // Enable or disable menu items:
            controller.checkItemsEnabled();
            
            // Reset:
            controller.clearCurrentNode();
            controller.clearClosestNode(); // (or else the old closest node point will be painted)
            
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
