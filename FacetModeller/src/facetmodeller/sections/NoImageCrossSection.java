package facetmodeller.sections;

import dialogs.Dialogs;
import facetmodeller.plc.NodeVector;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPoint3D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import javax.swing.JFrame;

/** Vertical cross-section with no image.
 * @author Peter
 */
public class NoImageCrossSection extends CrossSection {

    // ------------------ Properties -------------------
    
    private final NoImageSection noImageSection; // composition required because already extending CrossSection class
    
    // ------------------ Constructors -------------------

    public NoImageCrossSection() { // required by the SessionLoader (should not be used elsewhere)
        super();
        noImageSection = new NoImageSection();
    }
    
    public NoImageCrossSection(String n, Color col) {
        super(col);
        noImageSection = new NoImageSection(n);
    }
    
    // -------------------- Deep Copy --------------------
    
    @Override
    public NoImageCrossSection copySection() {
        // Create new object:
        NoImageCrossSection newSection = new NoImageCrossSection();
        // Deep copy some of the DefaultSectionInfo:
        this.copyTypedAndColorTo(newSection);
        // Deep copy the NoImageSection information:
        this.noImageSection.deepCopyTo(newSection.noImageSection);
        // Return the new object:
        return newSection;
    }
    
//    @Override
//    public NoImageCrossSection deepCopy() {
//        NoImageCrossSection s = new NoImageCrossSection();
//        s.deepCopyDefaultInfo();
//        s.setNoImageSection( this.noImageSection.deepCopy() );
//        return s;
//    }
//    @Override
//    public NoImageCrossSection undoCopy() {
//        NoImageCrossSection s = new NoImageCrossSection();
//        s.setUndoCopyDefaultInfo( this.undoCopyDefaultInfo() );
//        s.setNoImageSection( this.noImageSection.deepCopy() );
//        return s;
//    }

    // -------------------- Checkers --------------------

    @Override
    public int getType() { return Section.SECTION_NOIMAGE_CROSS; }
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
    public MyPoint2DVector getCorners() { return noImageSection.getCorners(); }
    
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
        MyPoint2D v2 = new MyPoint2D(v3.getX(),v3.getY()); // vector between diagonal corners in map view
        double w = v2.norm(); // spatial width
        double h = Math.abs(v3.getZ()); // spatial height
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
//        final int n=3;
//        final String[] descriptions = new String[n];
//        descriptions[0] = "Easting";
//        descriptions[1] = "Northing";
//        descriptions[2] = "Elevation";
        
        // Loop until calibrated:
        while(true) {
        
        // Ask for the spatial coordinates of the first calibation point:
        String prompt = prompt1 + "top left" + prompt2;
        MyPoint3D p = getTyped1();
//        String[] defaults = null;
//        if (p!=null) {
//            defaults = new String[n];
//            defaults[0] = Double.toString(p.getX());
//            defaults[1] = Double.toString(p.getY());
//            defaults[2] = Double.toString(p.getZ());
//        }
//        GridInputDialog dialog = new GridInputDialog(con,prompt,title,descriptions,defaults,null);
//        String[] inputs = dialog.getInputs();
//        if (inputs==null) { return false; } // user cancelled
        String input;
        if (p==null) {
            input = Dialogs.input(con,prompt,title);
        } else {
            input = Dialogs.input(con,prompt,title,p.toStringSpaces());
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
        prompt = prompt1 + "bottom right" + prompt2;
        p = getTyped2();
//        defaults = null;
//        if (p!=null) {
//            defaults = new String[n];
//            defaults[0] = Double.toString(p.getX());
//            defaults[1] = Double.toString(p.getY());
//            defaults[2] = Double.toString(p.getZ());
//        }
//        dialog = new GridInputDialog(con,prompt,title,descriptions,defaults,null);
//        inputs = dialog.getInputs();
//        if (inputs==null) { return false; } // user cancelled
        if (p==null) {
            input = Dialogs.input(con,prompt,title);
        } else {
            input = Dialogs.input(con,prompt,title,p.toStringSpaces());
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
        double dx = Math.abs(x2-x1);
        double dy = Math.abs(y2-y1);
        double dz = Math.abs(z2-z1);
        if ( ( dx==0.0 && dy==0.0 ) || ( dz==0.0 ) ) {
            // Clear calibration to indicate to FacetModeller to remove this section:
            clearCalibration();
            Dialogs.error(con,"Those calibration points are not good enough. Please try again.",title);
        } else {
           // Set the typed points:
           setTyped1( new MyPoint3D(x1,y1,z1) );
           setTyped2( new MyPoint3D(x2,y2,z2) );
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
        return noImageSection.readSessionInformation(reader,merge);
    }
    
}
