package facetmodeller.plc;

import fileio.SessionIO;
import facetmodeller.groups.Group;
import facetmodeller.sections.Section;
import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** A region defined by a 2D point attached to a section and group.
 * A region can be a true region (part of a true PLC) or a control point for moving nodes in a surface inversion.
 * @author Peter Lelievre
 */
public class Region extends HasSection implements SessionIO {

    // -------------------- Properties -------------------

    private MyPoint2D p2;
    private boolean isControl = false; // true if a control point, false if a region point
//    private double attribute=0.0, volume=0.0;
//    private boolean hasAttribute=false, hasVolume=false;

    // -------------------- Constructors -------------------

    public Region() { super(); } // required by SessionLoader (should not be used elsewhere)
    
    public Region(boolean isCon, double i, double j) { // required by the SessionLoader (should not be used elsewhere)
        super();
        p2 = new MyPoint2D(i,j);
        isControl = isCon;
    }

    public Region(boolean isCon, MyPoint2D p, Section s, Group g) {
        super(s,g);
        p2 = p;
        isControl = isCon;
    }

    // -------------------- Deep Copy -------------------

//    public Region deepCopy() {
//        return new Region( this.isControl, this.p2.deepCopy(), getSection().deepCopy(), getGroup().deepCopy() );
//    }

    // -------------------- Getters -------------------

    public boolean getIsControl() { return isControl; }
    public Color getColor() { return getGroup().getRegionColor(); }

    public MyPoint2D getPoint2D() { return p2; }
    public MyPoint3D getPoint3D() {
        Section s = getSection();
        if (!s.isCalibrated()) { return null; }
        return s.imageToSpace(p2);
    }
    
//    public boolean hasAttribute() { return hasAttribute; }
//    public double getAttribute() { return attribute; }
//    public boolean hasVolume() { return hasVolume; }
//    public double getVolume() { return volume; }
    
    // -------------------- Setters -------------------
    
//    public void setAttribute(double a) {
//        attribute = a;
//        hasAttribute = true;
//    }
//    public void setVolume(double v) {
//        volume = v;
//        hasVolume = true;
//    }
    
    // -------------------- Public Methods -------------------
    
//    public String attributeText() {
//        if (hasAttribute) {
//            if (hasVolume) {
//                return attribute + " " + volume;
//            } else {
//                return Double.toString(attribute);
//            }
//        } else {
//            return " ";
//        }
//    }

    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write isControl information:
        String textLine = Boolean.toString(isControl);
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write point coordinates, attribute and volume:
        textLine = p2.toStringSpaces();
//        textLine = p2.toString() + " " + hasAttribute + " " + attribute + " " + hasVolume + " " + volume;
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read isControl:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading isControl line."; }
        textLine = textLine.trim();
        try {
            isControl = Boolean.parseBoolean(textLine); // converts to Boolean
        } catch (NumberFormatException e) { return "Parsing isControl."; }
        // Read 2D coordinates:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading region 2D coordinates line."; }
        double x,y;
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+",3);
//        String[] ss = textLine.split("[ ]+",7);
        if (ss.length<2) { return "Not enough values on region 2D coordinates line."; }
        try {
            x = Double.parseDouble(ss[0].trim()); // converts to Double
            y = Double.parseDouble(ss[1].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing region 2D coordinates."; }
        p2 = new MyPoint2D(x,y);
//        // Attempt to read additional information (ignore error because this is new functionality):
//        try {
//            hasAttribute = Boolean.parseBoolean(ss[2].trim());
//            attribute    = Double.parseDouble(  ss[3].trim());
//            hasVolume    = Boolean.parseBoolean(ss[4].trim());
//            volume       = Double.parseDouble(  ss[5].trim());
//        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//            hasAttribute = false;
//            attribute = 0.0;
//            hasVolume = false;
//            volume = 0.0;
//        }
        // Return successfully:
        return null;
    }
    
}