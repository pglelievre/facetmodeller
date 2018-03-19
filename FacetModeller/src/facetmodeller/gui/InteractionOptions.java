package facetmodeller.gui;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import fileio.FileUtils;
import fileio.SessionIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Manages interaction options.
 * @author Peter
 */
public final class InteractionOptions implements SessionIO {
    
    public static final double DEFAULT_PICKING_RADIUS = 10.0;
    public static final double DEFAULT_AUTO_FACET_FACTOR = 1.5;
    
    private final FacetModeller controller;
    
    private double pickingDistance = DEFAULT_PICKING_RADIUS; /** Picking/snapping distance in spatial units,
    e.g. for deciding whether a clicked point is close enough to a node or facet centroid. */
    private double autoFacetFactor = DEFAULT_AUTO_FACET_FACTOR; /** Picking radius factor for line/triangular facet selection. */
    private boolean showConfirmationDialogs = true; /** Setting to false will hide some confirmation dialogs. */
    private boolean showToolPanel = true; /* Determines whether to show the tool panel. */
    private boolean showView3DPanel; /* Determines whether to show the 3D panel. */
    private boolean showScroller = false; /* Determines whether to show the scroller for the 2D panel. */
//    private boolean showPickingRadius = false; /* Determines whether to plot the picking radius. */

    public InteractionOptions(FacetModeller con, int nDimensions) {
        controller = con;
        showView3DPanel = (nDimensions==3);
    }
    
    public double getPickingDistance() { return pickingDistance; }
    public double getAutoFacetFactor() { return autoFacetFactor; }
    public boolean getShowConfirmationDialogs() { return showConfirmationDialogs; }
    public boolean getShowView3DPanel() { return showView3DPanel; }
    public boolean getShowToolPanel() { return showToolPanel; }
    public boolean getShowScroller() { return showScroller; }
    
    public void setPickingDistance(double d) { pickingDistance = d; }
    public void setAutoFacetFactor(double d) { autoFacetFactor = d; }
    public void setShowConfirmationDialogs(boolean show) { showConfirmationDialogs = show; }
    
    public boolean toggleShowToolPanel() {
        showToolPanel = !showToolPanel;
        return showToolPanel;
    }
    public boolean toggleShowView3DPanel() {
        showView3DPanel = !showView3DPanel;
        return showView3DPanel;
    }
    public boolean toggleShowScroller() {
        showScroller = !showScroller;
        return showScroller;
    }

    /** Allows the user to change the picking/snapping distance. */
    public void selectPickingRadius() {
        // Ask for the picking radius:
        String response = Dialogs.input(controller,"Enter the picking/snapping distance (spatial units):","Picking Distance",Double.toString(getPickingDistance()));
        // Check response:
        if (response == null) { return; }
        // Parse the picking radius out of the response:
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numeric value. Please try again.","Error");
            return;
        }
        double d;
        try {
            d = Double.parseDouble(ss[0].trim());
            if (d<=0.0) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive value. Please try again.","Error");
            return;
        }
        // Set the picking/snapping distance to that entered:
        setPickingDistance(d);
    }

    /** Allows the user to change the picking radius factor for line/triangular facet selection. */
    public void selectAutoFacetFactor() {
        String response = Dialogs.input(controller,"Enter the facet selection factor:","Facet Selection Factor",Double.toString(getAutoFacetFactor()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numeric value. Please try again.","Error");
            return;
        }
        double d;
        try {
            d = Double.parseDouble(ss[0].trim());
            if ( d<1.0 || d>2.0 ) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive real value on [1,2]. Please try again.","Error");
            return;
        }
        setAutoFacetFactor(d);
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write everything on a single line:
        String textLine =
                pickingDistance + " " +
                autoFacetFactor + " " +
                showConfirmationDialogs + " " +
                showToolPanel + " " +
                showView3DPanel + " " +
                showScroller;
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Write everything from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading interaction options line."; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<6) { return "Not enough values on interaction options line."; }
        try {
            pickingDistance         = Double.parseDouble(s[0]);
            autoFacetFactor         = Double.parseDouble(s[1]);
            showConfirmationDialogs = Boolean.parseBoolean(s[2]);
            showToolPanel           = Boolean.parseBoolean(s[3]);
            showView3DPanel         = Boolean.parseBoolean(s[4]);
            showScroller            = Boolean.parseBoolean(s[5]);
        } catch (NumberFormatException e) { return "Parsing interaction options."; }
        // Return successfully:
        return null;
    }

}