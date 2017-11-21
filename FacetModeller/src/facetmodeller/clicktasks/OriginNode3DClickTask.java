package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import facetmodeller.sections.SnapshotSection;
import geometry.MyPoint2D;

public final class OriginNode3DClickTask extends ControlledClickTask {
    
    public OriginNode3DClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_ORIGIN_NODE_3D; }

    @Override
    public String tip() { return ClickTaskUtil.ORIGIN_NODE_3D_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.ORIGIN_NODE_3D_TITLE; }

    @Override
    public boolean check() { return controller.originNode3DCheck(); }

    @Override
    public void mouseClick(MyPoint2D p) {

        // Check for the required information:
        if ( !check() || p==null ) {
            controller.endOriginNode3D();
            return;
        }

        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node node = controller.getClosestNode(); // just in case the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
//        MyPoint2D point = closestNodePoint;
        
//        // Convert points from image pixel coordinates to spatial coordinates:
//        MyPoint3D p3 = currentSection.imageToSpace(p);
//        MyPoint3D point3 = currentSection.imageToSpace(point);

//        // Check the distance is small enough: // already done in calculateClosestNode
//        Double d = point3.distanceToPoint(p3);
//        if (d>pickingDistance) { return; }

        // Set the origin:
        controller.setOrigin3D(node);

        // Redraw the 3D view:
        controller.redraw3D();
        
        // Reset the 2D view:
//        if (currentSection instanceof SnapshotSection) {
//            // Reset snapshot section:
//            resetSnapshotSection();
//        } else {
//            // Set the viewing centre:
//            imagePanel.setOrigin(p);
//        }
//        // Redraw the 2D view:
//        drawCurrentSection();

        if (!(controller.getSelectedCurrentSection() instanceof SnapshotSection)) {
            // Set the viewing centre:
            controller.setOrigin2D(p);
            // Redraw the 2D view:
            controller.redraw2D();
        }
        
        // Finish by clearing the click mode:
        controller.endOriginNode3D();
        
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
