package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddFacetCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Facet;
import facetmodeller.plc.Node;
import geometry.MyPoint2D;

public final class DefinePolyFacetClickTask extends ControlledClickTask {
    
    private final boolean autoStop;
    
    public DefinePolyFacetClickTask(FacetModeller con, boolean b) {
        super(con);
        autoStop = b;
    }
    
    @Override
    public int mode() {
        if (autoStop) {
            return ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI;
        } else {
            return ClickModeManager.MODE_DEFINE_POLY_FACETS;
        }
    }

    @Override
    public String text() {
        if (autoStop) {
            return ClickTaskUtil.DEFINE_POLY_FACET_TRI_TEXT;
        } else {
            return ClickTaskUtil.DEFINE_POLY_FACET_TEXT;
        }
    }

    @Override
    public String tip() {
        if (autoStop) {
            return ClickTaskUtil.DEFINE_POLY_FACET_TRI_TEXT;
        } else {
            return ClickTaskUtil.DEFINE_POLY_FACET_TEXT;
        }
    }

    @Override
    public String title() {
        if (autoStop) {
            return ClickTaskUtil.DEFINE_POLY_FACET_TRI_TITLE;
        } else {
            return ClickTaskUtil.DEFINE_POLY_FACET_TITLE;
        }
    }

    @Override
    public boolean check() {
        if ( autoStop && !controller.is3D() ) { return false; }
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
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
        
        // Check if we need to start, continue or stop the definition of a facet:
        Group group = controller.getSelectedCurrentGroup();
        Facet currentFacet = controller.getCurrentFacet();
        if (currentFacet==null) { // need to start new facet

            // Create a new facet object linked to the current group:
            currentFacet = new Facet(group);
            // Add the node to the current facet:
            currentFacet.addNode(node);
            // Save the facet for later:
            controller.setCurrentFacet(currentFacet);

        } else { // facet is already being defined

            // Check the group has not changed:
            if (!currentFacet.getGroup().equals(group)) {
                // Clear the facet:
                controller.clearCurrentFacet();
            } else {

                // Check if the new node is already in the facet:
                int inode = currentFacet.getNodes().indexOf(node);
                if ( inode == currentFacet.size()-1 ) { return; } // clicked the previous node
                if (inode>0) { // (the case of returning to the first node is dealt with after this if statement)
                    // Ask the user for confirmation:
                    int response = Dialogs.question(controller,"That node is already in the facet. How do you want to continue?",
                                                      title(),"Ignore click","Remove nodes","Stop defining","Ignore click");
                    if (response==Dialogs.YES_OPTION) { return; } // ignore the click
                    if (response==Dialogs.CANCEL_OPTION) { // stop defining the current facet
                        controller.clearCurrentFacet();
                        controller.checkItemsEnabled();
                        controller.redraw();
                        return;
                    }
                    // Remove all nodes from the facet back to and including the selected node:
                    while(currentFacet.containsNode(node)) {
                        currentFacet.removeLastNode();
                    }
                    // Check if the current facet is now empty:
                    if (currentFacet.size()==0) {
                        controller.clearCurrentFacet(); // process will restart
                    }
                    controller.redraw();
                    return;
                }
                
                // Check how to proceed:
                boolean done, add;
                if (controller.is3D()) {
                    // Check if the node is the first node in the current facet
                    // or if we can automatically stop:
                    if (autoStop) {
                        done = ( currentFacet.size()==2 );
                        add = true;
                    } else {
                        done = ( inode==0 ); // currentFacet.getNode(0).equals(node); // (stop once user circles back to first node)
                        add = !done; // (don't explicitly close the facet by duplicating the first node)
                        if ( done && currentFacet.size()==2 ) { return; } // not enough nodes in the facet
                    }
                } else {
                    // Check if there will now be 2 nodes for the facet in 2D:
                    done = true; //( currentFacet.size()==1 ); // (there is already one node and we only need two)
                    add = true;
                }

                // Add the node to the current facet (if necessary):
                if (add) {
                    // Check if the node is the same as any of the existing nodes:
                    if (currentFacet.containsNode(node)) { return; } // (assume this is a user mistake)
                    // Add the node to the current facet:
                    currentFacet.addNode(node);
                    // Don't need to save the facet for later - Java passes pointers!
                    // synthesizer.setCurrentFacet(currentFacet);
                }

                // Finish defining the facet (if necessary):
                if (done) {
                    // Add the facet:
                    AddFacetCommand com = new AddFacetCommand(controller.getModelManager(),currentFacet); com.execute();
                    controller.undoVectorAdd(com);
                    // Nullify the current facet pointer so that the next clicked node will start a new facet definition:
                    controller.clearCurrentFacet();
                }

            }

        }

        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
    }
    
    @Override
    public void mouseDrag(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest node to the cursor position and redraw if successful:
        if (controller.calculateClosestNode(p)) { controller.redraw(); }
        // Perform the mouseClick processing:
        mouseClick(p);
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
