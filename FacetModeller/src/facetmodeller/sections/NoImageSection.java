package facetmodeller.sections;

import fileio.SessionIO;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import fileio.FileUtils;
import geometry.MyPoint2D;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

/** A cross section without image on which to draw nodes and connect facets.
 * @author Peter Lelievre
 */
public class NoImageSection implements SessionIO {

    // ------------------ Properties -------------------

    private String name = " "; // user-defined name for the section
    private final int WIDTH = 100; // hardwired width of painted image
    private int height = WIDTH; // the height of the painted image can change based on the calibration
    
    // ------------------ Constructors -------------------
    
    public NoImageSection() {} // required by the SessionLoader (should not be used elsewhere)
    
    public NoImageSection(String n) {
        name = n;
    }
    
    // -------------------- Copy --------------------
    
    public NoImageSection deepCopy() {
        NoImageSection s = new NoImageSection(name);
        s.setHeight( this.height );
        return s;
    }

    // -------------------- Checkers --------------------

    public boolean hasImage() { return false; }
    public boolean canChangeName() { return true; }
    
    // -------------------- Getters --------------------

    public MyPoint2D getClicked1() { return new MyPoint2D(0,0); }
    public MyPoint2D getClicked2() { return new MyPoint2D(WIDTH,height); }
    
    public int getHeight() { return height; }
    public int getWidth() { return WIDTH; }
    public BufferedImage getImage() { return null; } // new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); }
    public File getImageFile() { return null; }
    public void setImageFile(File f) {}

    // -------------------- Setters --------------------
    
    public void setClicked1(MyPoint2D p) {}
    public void setClicked2(MyPoint2D p) {}
    
    public void setName(String s) { name = s; }
    
    protected void setHeight(int h) { height=h; }

    // -------------------- Public Methods --------------------

    public String shortName() { return name; }
    public String longName() { return name + " (no image)"; }

    // -------------------- Public Methods shared by NoImageCrossSection and NoImageDepthSection --------------------
    
    public void scalePixels(double f) {}
    public void clearCalibration() {}
    
    public NodeVector removeNodesRange(NodeVector nodes, NodeVector nodesToRemove) {
        // Get the clicked calibration points for the ith section:
        double w = getWidth();
        double h = getHeight();
        double x1 = Math.min(0.0,w);
        double x2 = Math.max(0.0,w);
        double y1 = Math.min(0.0,h);
        double y2 = Math.max(0.0,h);
        // Loop over each node for the section:
        for (int j=0 ; j<nodes.size() ; j++ ) {
            Node node = nodes.get(j);
            // Check if the node is inside the coordinate range:
            MyPoint2D p = node.getPoint2D();
            double x = p.getX();
            double y = p.getY();
            if ( x<x1 || x>x2 || y<y1 || y>y2 ) { // out of range
                // Add the node to the list to remove:
                nodesToRemove.add(node);
            }
        }
        return nodesToRemove;
    }

    // -------------------- SessionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        
        // Write the section name:
        String textLine = name.trim();
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // Write the image height:
        textLine = Integer.toString(height);
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // I used to write an image colour here and I still need to write a line so previous session files can still be read:
        return FileUtils.writeLine(writer,"DUMMYLINE");
        
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        
        // Read the section name:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading section name."; }
        name = textLine.trim();
        
        // Read the image height:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading image height line."; }
        textLine = textLine.trim();
        try {
            height = Integer.parseInt(textLine);
        } catch (NumberFormatException e) { return "Parsing image height."; }
        
        // I used to read an image colour here and I still need to read a line so previous session files can still be read:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading extra line."; }
        
        // Return successfully:
        return null;
        
    }

}