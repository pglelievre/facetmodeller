package facetmodeller.sections;

import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import fileio.FileUtils;
import geometry.Dir3D;
import geometry.MyPoint3D;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Holds information that all sections share.
 * @author Peter
 */
public class DefaultSectionInfo {
    
    // ------------------ Properties -------------------
    
    private MyPoint3D typed1 = null; // 1st typed calibration point (in real-space coordinates)
    private MyPoint3D typed2 = null; // 2nd typed calibration point (in real-space coordinates)

    private Color color = Color.WHITE;
    
    private final NodeVector nodes = new NodeVector(); // list of nodes associated with the section
    private final FacetVector facets = new FacetVector(); // list of facets associated with the section
    private final RegionVector regions = new RegionVector(); // list of region points associated with the section

    // ------------------ Constructors -------------------

    public DefaultSectionInfo() {} // required by the SessionLoader (should not be used elsewhere)
    
    public DefaultSectionInfo(Color col) {
        color = col;
    }
    
    // -------------------- Copy --------------------
    
//    public DefaultSectionInfo deepCopy() {
//        int r,g,b,a;
//        r = this.color.getRed();
//        g = this.color.getGreen();
//        b = this.color.getBlue();
//        a = this.color.getAlpha();
//        DefaultSectionInfo s = new DefaultSectionInfo( new Color(r,g,b,a) );
//        s.setTyped1( this.typed1.deepCopy() );
//        s.setTyped2( this.typed2.deepCopy() );
//        s.addNodes( this.nodes.deepCopy() );
//        s.addFacets( this.facets.deepCopy() );
//        s.addRegions( this.regions.deepCopy() );
//        return s;
//    }
//    public DefaultSectionInfo undoCopy() {
//        int r,g,b,a;
//        r = this.color.getRed();
//        g = this.color.getGreen();
//        b = this.color.getBlue();
//        a = this.color.getAlpha();
//        DefaultSectionInfo s = new DefaultSectionInfo( new Color(r,g,b,a) );
//        if ( this.typed1!=null ) { s.setTyped1( this.typed1.deepCopy() ); }
//        if ( this.typed2!=null ) { s.setTyped2( this.typed2.deepCopy() ); }
//        s.addNodes( this.nodes.shallowCopy() );
//        s.addFacets( this.facets.shallowCopy() );
//        s.addRegions( this.regions.shallowCopy() );
//        return s;
//    }
    
    // -------------------- Checkers --------------------

    public boolean isCalibrated() { // returns true if the section image has been calibrated
        return ( typed1!=null && typed2!=null );
    }
    
    // -------------------- Getters --------------------

    protected MyPoint3D getTyped1() { return typed1; }
    protected MyPoint3D getTyped2() { return typed2; }
    public Color getColor() { return color; }
    public NodeVector getNodes() { return nodes; }
    public FacetVector getFacets() { return facets; }
    public RegionVector getRegions() { return regions; }
    
    public Dir3D getDir3D() {
        // Check if the x, y or z coordinates for the two typed points are equal:
        Dir3D dir = null;
        if ( typed1.getX() == typed2.getX() ) {
            dir = Dir3D.X;
        }
        if ( typed1.getY() == typed2.getY() ) {
            if (dir!=null) { return null; } // can't figure it out
            dir = Dir3D.Y;
        }
        if ( typed1.getZ() == typed2.getZ() ) {
            if (dir!=null) { return null; } // can't figure it out
            dir = Dir3D.Z;
        }
        return dir;
    }

    // -------------------- Setters --------------------
    
    public void setTyped1(MyPoint3D p) { typed1 = p; }
    public void setTyped2(MyPoint3D p) { typed2 = p; }
    public void setColor(Color col) { color = col; }

    // -------------------- Private Methods --------------------

//    private void addNodes(NodeVector n) {
//        nodes.addAll(n);
//    }
//    private void addFacets(FacetVector f) {
//        facets.addAll(f);
//    }
//    private void addRegions(RegionVector r) {
//        regions.addAll(r);
//    }

    // -------------------- Public Methods --------------------

    public int numberOfNodes() { return nodes.size(); }
    public int numberOfFacets() { return facets.size(); }
    public int numberOfRegions() { return regions.size(); }

    public void addNode(Node n) {
        if (!nodes.contains(n)) { nodes.add(n); }
    }

    public void removeNode(Node n) { nodes.remove(n); }

    public void addFacet(Facet f) {
        // Check if the section already contains the facet:
        if (facets.contains(f)) { return; }
        // Add the facet to the section:
        facets.add(f);
        // Check if there were already facets in the section:
        if (facets.size()<=1) { return; }
        // Get the first facet:
        Facet f0 = facets.get(0);
        // Get the normal vectors for the facets:
        MyPoint3D v0 = f0.getNormal();
        if (v0==null) { return; }
        MyPoint3D v = f.getNormal();
        if (v==null) { return; }
        // Check if those two normal vectors are in the same direction:
        double d = v.dot(v0);
        if (d>=0.0) { return; }
        // Change the ordering of the facet:
        f.reverse();
    }
    
    public void addRegion(Region r) {
        if (!regions.contains(r)) { regions.add(r); }
    }
    public void removeFacet(Facet f) { facets.remove(f); }
    public void removeRegion(Region r) { regions.remove(r); }
//    public void clearFacets() { facets.clear(); }
//    public void clearPLC() {
//        nodes.clear();
//        facets.clear();
//        regions.clear();
//    }
    
    // -------------------- SectionIO Methods --------------------
    
    public boolean writeSessionInformation(BufferedWriter writer) {
    
        // Write special line for use in readSessionInformation:
        String textLine = "COLOREXISTS";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // Check the section is calibrated:
        if (isCalibrated()) {
            // Write the typed points:
            textLine = typed1.toString();
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            textLine = typed2.toString();
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        } else {
            // Write "null" twice:
            textLine = "null";
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        }
        
        // Write the section color:
        Color col = getColor();
        textLine = Integer.toString(col.getRGB());
        return FileUtils.writeLine(writer,textLine);
        
    }
    
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        
        // Check for special line for use in readSessionInformation:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading COLOREXISTS line."; }
        textLine = textLine.trim();
        boolean hasColor = textLine.equals("COLOREXISTS");
        
        // Read the typed points:
        if (hasColor) {
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading first typed calibration points line."; }
            textLine = textLine.trim();
        }
        double x,y,z;
        String[] ss;
        if (!textLine.startsWith("null")) {
            ss = textLine.split("[ ]+");
            if (ss.length<3) { return "Not enough values on first typed calibration points line."; }
            try {
                x = Double.parseDouble(ss[0].trim()); // converts to Double
                y = Double.parseDouble(ss[1].trim()); // converts to Double
                z = Double.parseDouble(ss[2].trim()); // converts to Double
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing first typed calibration point."; }
            typed1 = new MyPoint3D(x,y,z);
        }
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading second typed calibration point line."; }
        textLine = textLine.trim();
        if (!textLine.startsWith("null")) {
            ss = textLine.split("[ ]+");
            if (ss.length<3) { return "Not enough values on second typed calibration points line."; }
            try {
                x = Double.parseDouble(ss[0].trim()); // converts to Double
                y = Double.parseDouble(ss[1].trim()); // converts to Double
                z = Double.parseDouble(ss[2].trim()); // converts to Double
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing second typed calibration point."; }
            typed2 = new MyPoint3D(x,y,z);
        }
        
        // Read the section color:
        if (!hasColor) { return null; } // return successfully
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading section colour line."; }
        try {
            Color col = new Color(Integer.parseInt(textLine.trim()));
            setColor(col); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing section colour."; }
        
        // Return successfully:
        return null;
        
    }
    
}
