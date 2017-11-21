package facetmodeller.plc;

import facetmodeller.groups.Group;
import facetmodeller.sections.Section;
import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** A node off of a section, specified by a 3D point.
 * @author Peter
 */
public class NodeOffSection extends Node {

    // -------------------- Properties -------------------

    private MyPoint3D p3;

    // -------------------- Constructors -------------------

    public NodeOffSection() { super(); } // required by SessionLoader (should not be used elsewhere)
    
    public NodeOffSection(double x, double y, double z) { // required by when reading a topo file (should not be used elsewhere)
        super();
        p3 = new MyPoint3D(x,y,z);
    }

    public NodeOffSection(MyPoint3D p, Section s, Group g) {
        super(s,g);
        p3 = p;
    }

    // -------------------- Deep Copy -------------------

//    @Override
//    public Node deepCopy() {
//        Node newNode = new NodeOffSection(p3,getSection(),getGroup());
//        newNode.addFacets(getFacets());
//        return newNode;
//    }
    
    // -------------------- Checkers -------------------
    
    @Override
    public int getType() { return Node.NODE_OFF_SECTION; }
    @Override
    public boolean isOff() { return true; }
    
    // -------------------- Getters -------------------

    @Override
    public MyPoint2D getPoint2D() {
        Section s = getSection();
        if (!s.isCalibrated()) { return null; }
        return s.projectOnto(p3);
    }
    @Override
    public MyPoint3D getPoint3D() { return p3; }
    
    // -------------------- Setters -------------------

    @Override
    public void setPoint2D(MyPoint2D p) { }
    @Override
    public void setPoint3D(MyPoint3D p) { p3 = p; }

    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        String textLine = p3.toString() + "\n";
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read 3D coordinates:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading node 3D coordinates line."; }
        double x,y,z;
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+");
        if (ss.length<3) { return "Not enough values on node 3D coordinates line."; }
        try {
            x = Double.parseDouble(ss[0].trim()); // converts to Double
            y = Double.parseDouble(ss[1].trim());
            z = Double.parseDouble(ss[2].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing node 3D coordinates."; }
        p3 = new MyPoint3D(x,y,z);
        // Return successfully:
        return null;
    }
    
}
