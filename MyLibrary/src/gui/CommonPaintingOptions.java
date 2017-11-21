package gui;

import dialogs.Dialogs;
import fileio.FileUtils;
import fileio.SessionIO;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import paint.Paintable;

/** Manages the painting options.
 * @author Peter
 */
public class CommonPaintingOptions implements SessionIO {
    
    public static final Color DEFAULT_CALIBRATION_COLOR = Color.CYAN;
    public static final int DEFAULT_POINT_WIDTH = Paintable.DEFAULT_POINT_WIDTH;
    public static final int DEFAULT_LINE_WIDTH = Paintable.DEFAULT_LINE_WIDTH;
    
    private JFrame controller;
    private boolean success = false;
    
    private Color calibrationColor = DEFAULT_CALIBRATION_COLOR; // The painting colour for calibration information.
    private int pointWidth = DEFAULT_POINT_WIDTH; // Point size for overlays.
    private int lineWidth = DEFAULT_LINE_WIDTH; // Line width for overlays.
    
    /**
     * @param con Used for positioning dialogs.
     */
    public CommonPaintingOptions(JFrame con) { controller = con; }
    
    public boolean getSuccess() { return success; }
    
    public Color getCalibrationColor() { return calibrationColor; }
    public int getPointWidth() { return pointWidth; }
    public int getLineWidth() { return lineWidth; }
    
    public void setCalibrationColor(Color col) { calibrationColor = col; }
    public void setPointWidth(int i) { pointWidth = i; }
    public void setLineWidth(int i) { lineWidth = i; }
    
    /** Allows the user to select a colour for the calibration measurements. */
    public void selectCalibrationColor() {
        success = false;
        // Ask for the colour:
        Color col = JColorChooser.showDialog(controller,"Select the calibration color",getCalibrationColor());
        // Check response:
        if (col == null) { return; }
        // Set calibration colour to that specified:
        setCalibrationColor(col);
        // Return successfully:
        success = true;
    }

    /** Allows the user to change the point size for 2D and 3D overlays. */
    public void selectPointWidth() {
        success = false;
        // Ask for the point size:
        String response = Dialogs.input(controller,"Enter the point width for overlays (screen pixel units):","Point Width",Integer.toString(getPointWidth()));
        // Check response:
        if (response == null) { return; }
        // Parse the point size out of the response:
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single integer value. Please try again.","Error");
            return;
        }
        int i;
        try {
            i = Integer.parseInt(ss[0].trim());
            if ( i<1 || i>50 ) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter an integer value on [1,50]. Please try again.","Error");
            return;
        }
        // Set the point size to that entered:
        setPointWidth(i);
        success = true;
    }

    /** Allows the user to change the line width for 2D overlays. */
    public void selectLineWidth() {
        success = false;
        String response = Dialogs.input(controller,"Enter the line width for overlays (screen pixel units):","Line Width",Integer.toString(getLineWidth()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single integer value. Please try again.","Error");
            return;
        }
        int i;
        try {
            i = Integer.parseInt(ss[0].trim());
            if ( i<1 || i>10 ) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter an integer value on [1,10]. Please try again.","Error");
            return;
        }
        setLineWidth(i);
        success = true;
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write calibration colour:
        String textLine = Integer.toString(calibrationColor.getRGB()) + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write point width:
        textLine = Integer.toString(pointWidth) + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write line width:
        textLine = Integer.toString(lineWidth) + "\n";
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read calibration colour:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading calibration colour line."; }
        try {
            calibrationColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing calibration colour."; }
        // Read point width:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading point width line."; }
        try {
            pointWidth = Integer.parseInt(textLine.trim());
        } catch (NumberFormatException e) { return "Parsing point width."; }
        // Read line width:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading line width line."; }
        try {
            lineWidth = Integer.parseInt(textLine.trim());
        } catch (NumberFormatException e) { return "Parsing line width."; }
        // Return successfully:
        return null;
    }
    
}
