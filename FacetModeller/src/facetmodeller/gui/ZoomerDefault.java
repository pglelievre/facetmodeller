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
    private int zoomInit;
    private int zoomMin;
    private int zoomMax;
    private double zoomFactor;
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
        setFactor(factor);
    }
    
    // -------------------- Setters --------------------

    public void setFactor(double factor) {
        zoomFactor = factor;
        zoom = zoomInit;
    }
    
    // -------------------- Getters --------------------

    public double getFactor() { return zoomFactor; }
    
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
        String textLine = zoom + " " + zoomInit + " " + zoomMin + " " + zoomMax + " " + zoomFactor;
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read everything from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading zoomer parameters line."; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<1) { return "Not enough values on zoomer parameters line."; }
        // Read the parameters integer:
        try {
            zoom = Integer.parseInt(s[0]);
            if (s.length>1) { zoomInit = Integer.parseInt(s[1]); }
            if (s.length>2) { zoomMin = Integer.parseInt(s[2]); }
            if (s.length>3) { zoomMax = Integer.parseInt(s[3]); }
            if (s.length>4) { zoomFactor = Double.parseDouble(s[4]); }
        } catch (NumberFormatException e) { return "Parsing zoomer parameters line"; }
        // Check for zoom out of range:
        if ( zoom < zoomMin ) { zoom = zoomMin; }
        if ( zoom > zoomMax ) { zoom = zoomMax; }
        // Return successfully:
        return null;
    }
    
}