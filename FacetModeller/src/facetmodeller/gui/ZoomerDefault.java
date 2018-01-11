package facetmodeller.gui;

import fileio.FileUtils;
import fileio.SessionIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Zoom information.
 * @author Peter Lelievre
 */
public final class ZoomerDefault implements SessionIO { // TODO: implement Zoomer once Java allows implementing multiple interfaces

    // ------------------- Properties ------------------

    // zoom scaling = ZOOM_Factor^zoom
    private final int zoomInit;
    private final int zoomMin;
    private final int zoomMax;
    private final double zoomFactor;
    private int zoom;

    // ------------------ Constructor ------------------

    public ZoomerDefault() {
        super();
        zoomInit = 0;
        zoomMin = 0;
        zoomMax = 0;
        zoomFactor = 0;
        zoom = zoomInit;
    }

    public ZoomerDefault(int init, int min, int max, double factor) {
        super();
        zoomInit = init;
        zoomMin = min;
        zoomMax = max;
        zoomFactor = factor;
        zoom = zoomInit;
    }
    
    // -------------------- Getters --------------------
    
    public int getZoom() { return zoom; }
    
    public double getScaling() {
        return Math.pow(zoomFactor,zoom);
    }
    
    // -------------------- Implemented Zoomer Methods --------------------
    
//    @Override
    public void zoomIn() {
        if (zoom==zoomMax) { return; }
        zoom = Math.min(zoom+1,zoomMax);
    }
    
//    @Override
    public void zoomOut() {
        if (zoom==zoomMin) { return; }
        zoom = Math.max(zoom-1,zoomMin);
    }
    
//    @Override
    public void zoomReset() {
        if (zoom==zoomInit) { return; }
        zoom = zoomInit;
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write the zoom integer:
        String textLine = Integer.toString( zoom );
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read the zoom integer:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Zoom integer line"; }
        try {
            zoom = Integer.parseInt(textLine.trim());
        } catch (NumberFormatException e) { return "Parsing zoom integer line"; }
        // Check for zoom out of range:
        if ( zoom < zoomMin ) { zoom = zoomMin; }
        if ( zoom > zoomMax ) { zoom = zoomMax; }
        // Return successfully:
        return null;
    }
    
}
