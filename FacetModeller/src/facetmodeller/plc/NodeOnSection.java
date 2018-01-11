package facetmodeller.plc;

import facetmodeller.groups.Group;
import facetmodeller.sections.Section;
import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** A node on a section, specified by a 2D point.
 * @author Peter
 */
public class NodeOnSection extends Node {

    // -------------------- Properties -------------------

    private MyPoint2D p2;

    // -------------------- Constructors -------------------

    public NodeOnSection() { super(); } // required by SessionLoader (should not be used elsewhere)

    public NodeOnSection(double i, double j) { // required by the SessionLoader (should not be used elsewhere)
        super();
        p2 = new MyPoint2D(i,j);
    }

    public NodeOnSection(MyPoint2D p, Section s, Group g) {
        super(s,g);
        p2 = p;
    }

    // -------------------- Deep Copy -------------------

//    @Override
//    public Node deepCopy() {
//        Node newNode = new NodeOnSection(p2,getSection(),getGroup());
//        newNode.addFacets(getFacets());
//        return newNode;
//    }
    
    // -------------------- Checkers -------------------
    
    @Override
    public int getType() { return Node.NODE_ON_SECTION; }
    @Override
    public boolean isOff() { return false; }
    
    // -------------------- Getters -------------------

    @Override
    public MyPoint2D getPoint2D() { return p2; }
    @Override
    public MyPoint3D getPoint3D() {
        Section s = getSection();
        if (!s.isCalibrated()) { return null; }
        MyPoint3D p3 = s.imageToSpace(p2);
        // Hardwire rounding to closest integer if very close to that integer:
        double x1 = p3.getX();
        double y1 = p3.getY();
        double z1 = p3.getZ();
        double x2 = Math.round(x1);
        double y2 = Math.round(y1);
        double z2 = Math.round(z1);
        if ( Math.abs(x1-x2) < 1.0E-12 ) { p3.setX(x2); }
        if ( Math.abs(y1-y2) < 1.0E-12 ) { p3.setY(y2); }
        if ( Math.abs(z1-z2) < 1.0E-12 ) { p3.setZ(z2); }
        return p3;
    }
    
    // -------------------- Setters -------------------

    @Override
    public void setPoint2D(MyPoint2D p) { p2 = p; }
    @Override
    public void setPoint3D(MyPoint3D p) {
        // Make sure the section is calibrated:
        Section s = getSection();
        if (!s.isCalibrated()) { return; }
        // Project onto the section to get section image coordinates:
        MyPoint2D p2new = s.projectOnto(p);
        if (p2new==null) { return; }
        // Reset the 2D point:
        p2 = p2new;
    }

    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        String textLine = p2.toString();
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read 2D coordinates:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading node 2D coordinates line."; }
        double x,y;
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+");
        if (ss.length<2) { return "Not enough values on node 2D coordinates line."; }
        try {
            x = Double.parseDouble(ss[0].trim()); // converts to Double
            y = Double.parseDouble(ss[1].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing node 2D coordinates."; }
        p2 = new MyPoint2D(x,y);
        // Return successfully:
        return null;
    }
    
}
