package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import geometry.MyPoint2D;

/** A task connected to a controller that requires a MyPoint2D object to execute.
 * @author Peter
 */
public abstract class ControlledClickTask implements ClickTask {
    
    @SuppressWarnings("ProtectedField")
    protected FacetModeller controller;
    
    public ControlledClickTask(FacetModeller con) {
        super();
        controller = con;
    }
    
    // Implement default drag and move execution methods:
    @Override
    public void mouseDrag(MyPoint2D p) {
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Update the cursor bar:
        controller.updateCursorBar(p);
    }
    @Override
    public void mouseMove(MyPoint2D p) {
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Update the cursor bar:
        controller.updateCursorBar(p);
    }
    
}
