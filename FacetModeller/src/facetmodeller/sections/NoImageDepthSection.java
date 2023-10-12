package facetmodeller.sections;

import dialogs.Dialogs;
import facetmodeller.commands.CommandVector;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.NodeVector;
import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPoint3D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;

/** Vertical depth-section with no image.
 * @author Peter
 */
public class NoImageDepthSection extends DepthSection {

    // ------------------ Properties -------------------
    
    private boolean isTopo; // required to support obsolete TopoSection
    private File nodeFile, eleFile; // required to support version 2 session files
    private final NoImageSection noImageSection; // composition required because already extending DepthSection class
    
    // ------------------ Constructors -------------------

    public NoImageDepthSection() { // required by the SessionLoader (should not be used elsewhere)
        super();
        noImageSection = new NoImageSection();
    }
    
    public NoImageDepthSection(String n, Color col) {
        super(col);
        noImageSection = new NoImageSection(n);
    }
    
    public NoImageDepthSection(File nf, File ef) { // required to support version 2 session files
        super();
        noImageSection = new NoImageSection("TOPOGRAPHY");
        isTopo = true;
        nodeFile = nf;
        eleFile = ef;
        // (the files get read later)
    }
    
    public NoImageDepthSection(boolean ist) { // required to support obsolete TopoSection
        super();
        if (ist) {
            noImageSection = new NoImageSection("TOPOGRAPHY");
            isTopo = true;
            // (the .node and .ele files get read later)
        } else {
            noImageSection = new NoImageSection();
        }
    }
    
    public File getNodeFile() { return nodeFile; }
    public File getEleFile() { return eleFile; }
    
    // -------------------- Deep Copy --------------------
    
    @Override
    public NoImageDepthSection copySection() {
        // Create new object:
        NoImageDepthSection newSection = new NoImageDepthSection();
        // Deep copy some of the DefaultSectionInfo:
        this.copyTypedAndColorTo(newSection);
        // Deep copy the NoImageSection information:
        this.noImageSection.deepCopyTo(newSection.noImageSection);
        // Deep copy the rest of the properties for NoImageDepthSection objects:
        newSection.isTopo = this.isTopo;
        URI nodeURI=null;
        URI eleURI=null;
        if (nodeFile!=null) nodeURI = this.nodeFile.toURI();
        if (eleFile!=null) eleURI = this.eleFile.toURI();
        File nodeF=null;
        File eleF=null;
        if (nodeURI!=null) nodeF = new File(nodeURI);
        if (eleURI!=null) eleF = new File(eleURI);
        newSection.nodeFile = nodeF;
        newSection.eleFile = eleF;
        // Return the new object:
        return newSection;
    }
    
//    @Override
//    public NoImageDepthSection deepCopy() {
//        NoImageDepthSection s = new NoImageDepthSection();
//        s.deepCopyDefaultInfo();
//        s.setNoImageSection( this.noImageSection.deepCopy() );
//        return s;
//    }
//    @Override
//    public NoImageDepthSection undoCopy() {
//        NoImageDepthSection s = new NoImageDepthSection();
//        s.setUndoCopyDefaultInfo( this.undoCopyDefaultInfo() );
//        s.setNoImageSection( this.noImageSection.deepCopy() );
//        return s;
//    }

    // -------------------- Checkers --------------------

    @Override
    public int getType() { return Section.SECTION_NOIMAGE_DEPTH; }
    @Override
    public boolean hasImage() { return noImageSection.hasImage(); }
    @Override
    public boolean canCalibrate() { return true; }
    @Override
    public boolean canAddNodesOnSection() { return true; }
    @Override
    public boolean canNodesShift() { return true; }
    @Override
    public boolean canDeleteNodesRange() { return true; }
    @Override
    public boolean canChangeName() { return noImageSection.canChangeName(); }
    
    // -------------------- Getters --------------------

    @Override
    public MyPoint2D getClicked1() { return noImageSection.getClicked1(); }
    @Override
    public MyPoint2D getClicked2() { return noImageSection.getClicked2(); }
    @Override
    public MyPoint2DVector getCorners() { return noImageSection.getCorners(); }

    @Override
    public int getWidth() { return noImageSection.getWidth(); }
    @Override
    public int getHeight() { return noImageSection.getHeight(); }
    @Override
    public BufferedImage getImage() { return noImageSection.getImage(); }
    @Override
    public File getImageFile() { return noImageSection.getImageFile(); }
    @Override
    public void setImageFile(File f) { noImageSection.setImageFile(f); }

    // -------------------- Setters --------------------
    
    @Override
    public void setClicked1(MyPoint2D p) { noImageSection.setClicked1(p); }
    @Override
    public void setClicked2(MyPoint2D p) { noImageSection.setClicked2(p); }
    
    @Override
    public void setName(String s) { noImageSection.setName(s); }
    
    @Override
    public void setTyped2(MyPoint3D p) {
        super.setTyped2(p);
        // Change the height based on the calibration points:
        setHeightBasedOnCalibration();
    }
    
    private void setHeightBasedOnCalibration() {
        // Set the height to maintain the desired aspect ratio:
        MyPoint3D v3 = getTyped1().vectorToPoint(getTyped2()); // vector between diagonal corners in 3D
        double w = Math.abs(v3.getX()); // spatial width
        double h = Math.abs(v3.getY()); // spatial height
        if (w==0.0) {
            noImageSection.setHeight(getWidth());
            return;
        }
        double r = h / w; // ratio between spatial height and width
        h = getWidth() * r;
        noImageSection.setHeight((int)h);
    }

    // -------------------- Private Methods --------------------
    
//    private void setNoImageSection(NoImageSection s) {
//        noImageSection = s;
//    }

    // -------------------- Public Methods --------------------

    @Override
    public String shortName() { return noImageSection.shortName(); }
    @Override
    public String longName() { return noImageSection.longName(); }
    
//    @Override
//    public Node newNode(MyPoint2D p, Group g) {
//        return new NodeOnSection(p,this,g);
//    }
//    @Override
//    public void addNode(Node n) {
//        // Make sure it is the correct type of node:
//        if (!(n instanceof NodeOnSection)) { return; }
//        // Add the node:
//        getNodes().add(n);
//    }
    
    @Override
    public void scalePixels(double f) {
        noImageSection.scalePixels(f);
    }
    
    @Override
    public boolean startCalibration(JFrame con) {
        // Get the information from the user:
        changeCalibrationCoordinates(con);
        // Always return false (no further information is required from the user):
        return false;
    }
    
    @Override
    public boolean continueCalibration(JFrame con) { return false; } // everything is done in the startCalibration method!
    
    @Override
    public boolean calibrate(JFrame con, MyPoint2D clickPoint) { return false; } // everything is done in the startCalibration method!
    
    @Override
    public boolean changeCalibrationCoordinates(JFrame con) {
        
        // Define some dialog text:
        final String title = "Calibrate";
        final String prompt1 = "Enter the coordinates of the ";
        final String prompt2 = " corner of the image:";
        final String message = "Enter three numeric values.";
        
        // Loop until calibrated:
        while(true) {
        
        MyPoint3D p1 = getTyped1(); // x1,y2,z1 // corresponds to top left pixel (0,0)
        MyPoint3D p2 = getTyped2(); // x2,y1,z2 // corresponds to bottom right pixel (height,width)
        if ( p1!=null && p2!=null ) {
            double x1 = p1.getX();
            double x2 = p2.getX();
            double y1 = p2.getY(); // yes, these are supposed to be swapped!
            double y2 = p1.getY(); // yes, these are supposed to be swapped!
            double z1 = p1.getZ();
            double z2 = p2.getZ();
            p1 = new MyPoint3D(x1,y1,z1);
            p2 = new MyPoint3D(x2,y2,z2);
        }
        
        // Ask for the spatial coordinates of the first calibation point:
        String prompt = prompt1 + "bottom left (minimum coordinate values)" + prompt2;
        String input;
        if (p1==null) {
            input = Dialogs.input(con,prompt,title);
        } else {
            input = Dialogs.input(con,prompt,title,p1.toStringSpaces());
        }
        if (input==null) { return false; } // user cancelled
        input = input.trim();
        String[] inputs = input.split("[ ]+");
        if (inputs.length!=3) {
            Dialogs.error(con,"You must enter 3 values. Please try again.",title);
            return false;
        }
        
        // Parse the inputs to doubles and set the first typed point:
        double x1,y1,z1;
        try {
            x1 = Double.parseDouble(inputs[0].trim());
            y1 = Double.parseDouble(inputs[1].trim());
            z1 = Double.parseDouble(inputs[2].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(con,message,title);
            return false;
        }
        
        // Ask for the spatial coordinates of the second calibation point:
        prompt = prompt1 + "top right (maximum coordinate values)" + prompt2;
        if (p2==null) {
            input = Dialogs.input(con,prompt,title);
        } else {
            input = Dialogs.input(con,prompt,title,p2.toStringSpaces());
        }
        if (input==null) { return false; } // user cancelled
        input = input.trim();
        inputs = input.split("[ ]+");
        if (inputs.length!=3) {
            Dialogs.error(con,"You must enter 3 values. Please try again.",title);
            return false;
        }
        
        // Parse the inputs to doubles and set the second typed point:
        double x2,y2,z2;
        try {
            x2 = Double.parseDouble(inputs[0].trim());
            y2 = Double.parseDouble(inputs[1].trim());
            z2 = Double.parseDouble(inputs[2].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(con,message,title);
            return false;
        }
        
        // Check the inputs are appropriate:
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        if ( dx<=0.0 || dy<=0.0 || dz!=0.0 ) {
            // Clear calibration to indicate to FacetModeller to remove this section:
            clearCalibration();
            Dialogs.error(con,"Those calibration points are not good enough. Please try again.",title);
        } else {
           // Set the typed points:
           setTyped1( new MyPoint3D(x1,y2,z1) ); // corresponds to top left pixel (0,0)
           setTyped2( new MyPoint3D(x2,y1,z2) ); // corresponds to bottom right pixel (height,width)
           // Break from while loop:
           break;
        }
        
        } // end of while(true)
        
        // Return true to indicate successful recalibration:
        return true;
        
    }

    @Override
    public void clearCalibration() { noImageSection.clearCalibration(); }
    
    @Override
    public CommandVector snapToCalibration(double pickingRadius, GroupVector groups, boolean doH, boolean doV) {
        if (!doH) { return null; } // can't shift vertically on a depth section
        // Perform the snapping:
        return super.snapToCalibration(pickingRadius,groups,doH,doH);
    }
    
    @Override
    public NodeVector removeNodesRange(NodeVector nodesToRemove) {
        return noImageSection.removeNodesRange(getNodes(),nodesToRemove);
    }

    // -------------------- SessionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!super.writeSessionInformation(writer)) { return false; }
        return noImageSection.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = super.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        msg = noImageSection.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        // The following is to support older session files that used the obsolete TopoSection:
        if (!isTopo) { return null; }
        // Read the node file name:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading .node file for obsolete TopoSection."; }
        textLine = textLine.trim();
        if (textLine.startsWith("null")) {
            nodeFile = null;
        } else {
            URI uri;
            try {
                uri = new URI(textLine);
            } catch (URISyntaxException e) { return "Converting .node file string for obsolete TopoSection to URI."; }
            try {
                nodeFile = new File(uri); // image file or .node file
            } catch (IllegalArgumentException e) { return "Converting .node file URI for obsolete TopoSection to File."; }
        }
        // Read the ele file name:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading .ele file for obsolete TopoSection."; }
        textLine = textLine.trim();
        if (textLine.startsWith("null")) {
            eleFile = null;
        } else {
            try {
                URI uri = new URI(textLine);
                eleFile = new File(uri); // image file or .node file
            } catch (URISyntaxException e) { return "Converting .ele file string for obsolete TopoSection to URI."; }
        }
        // Skip the min/max/range information:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading min/max/range information for obsolete TopoSection."; }
        return null;
    }
    
}
