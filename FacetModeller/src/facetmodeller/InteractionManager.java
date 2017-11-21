package facetmodeller;

import facetmodeller.clicktasks.ClickTask;
import facetmodeller.gui.InteractionOptions;
import fileio.SessionIO;
import geometry.MyPoint2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Manages user interactions with the GUI.
 * @author Peter
 */
public class InteractionManager implements SessionIO {
    
    private MouseInteractionManager mouseManager; // manages mouse interactions
    private InteractionOptions interactionOptions; // interaction options
    
    public InteractionManager(FacetModeller con, int ndim) {
        mouseManager = new MouseInteractionManager(con);
        interactionOptions = new InteractionOptions(con,ndim);
    }
    
    // Wrappers for the MouseInteractionManager class:
    public void mouseMove(MyPoint2D p) { mouseManager.mouseMove(p); }
    public void mouseDrag(MyPoint2D p) { mouseManager.mouseDrag(p); }
    public void mouseClick(MyPoint2D p) { mouseManager.mouseClick(p); }
    public ClickTask getClickTask(int mode) { return mouseManager.getClickTask(mode); }
    
    // Wrappers for the InteractionOptions class:
    public boolean getShowView3DPanel() { return interactionOptions.getShowView3DPanel(); }
    public boolean getShowToolPanel() { return interactionOptions.getShowToolPanel(); }
    public boolean getShowScroller() { return interactionOptions.getShowScroller(); }
    public double getPickingDistance() { return interactionOptions.getPickingDistance(); }
    public double getAutoFacetFactor() { return interactionOptions.getAutoFacetFactor(); }
    public boolean getShowConfirmationDialogs() { return interactionOptions.getShowConfirmationDialogs(); }
    public void setPickingDistance(double d) { interactionOptions.setPickingDistance(d); }
    public void setAutoFacetFactor(double d) { interactionOptions.setAutoFacetFactor(d); }
    public void setShowConfirmationDialogs(boolean show) { interactionOptions.setShowConfirmationDialogs(show); }
    public boolean toggleShowToolPanel() { return interactionOptions.toggleShowToolPanel(); }
    public boolean toggleShowView3DPanel() { return interactionOptions.toggleShowView3DPanel(); }
    public boolean toggleShowScroller() { return interactionOptions.toggleShowScroller(); }
    public void selectPickingRadius() { interactionOptions.selectPickingRadius(); }
    public void selectAutoFacetFactor() { interactionOptions.selectAutoFacetFactor(); }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        return interactionOptions.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        return interactionOptions.readSessionInformation(reader,merge);
    }
    
}
