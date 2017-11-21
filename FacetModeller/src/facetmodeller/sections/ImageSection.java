package facetmodeller.sections;

import dialogs.Dialogs;
import fileio.SessionIO;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import gui.HasImage;
import java.awt.image.*;
import java.io.*;
import javax.swing.JFrame;

/** A cross section with associated image on which to draw nodes and connect facets.
 * @author Peter Lelievre
 */
@SuppressWarnings("PublicInnerClass")
public class ImageSection implements SessionIO {

    // ------------------ Properties -------------------
    
    private MyPoint2D clicked1 = null; // 1st clicked calibration point (in image panel coordinates)
    private MyPoint2D clicked2 = null; // 2nd clicked calibration point (in image panel coordinates)

    //private final HasImage hasImage; // the image and associated file
    private HasImage hasImage; // the image and associated file
    
    // ------------------ Constructor -------------------
    
    public ImageSection() { // required by the SessionLoader (should not be used elsewhere)
        hasImage = new HasImage();
    }
    
    public ImageSection(File f) {
        hasImage = new HasImage(f);
    }
    
    // -------------------- Copy --------------------
    
    public ImageSection deepCopy() {
        ImageSection s = new ImageSection();
        s.setClicked1( this.clicked1.deepCopy() );
        s.setClicked2( this.clicked2.deepCopy() );
        s.setHasImage( this.hasImage.deepCopy() );
        return s;
    }

    // -------------------- Checkers --------------------

    public boolean hasImage() { return true; }
    public boolean canCalibrate() { return (getImage()!=null); } // can't calibrate if image could not be loaded from file
    public boolean isCalibrated() {
        return ( clicked1!=null && clicked2!=null );
    }
    public boolean canChangeName() { return false; }
    
    // -------------------- Getters --------------------

    public MyPoint2D getClicked1() { return clicked1; }
    public MyPoint2D getClicked2() { return clicked2; }

    public int getWidth() { return hasImage.getWidth(); }
    public int getHeight() { return hasImage.getHeight(); }
    public BufferedImage getImage() { return hasImage.getImage(); }
    public File getImageFile() { return hasImage.getFile(); }
    public void setImageFile(File f) { hasImage = new HasImage(f); } // hasImage.setImageFile(f); }
    
    // -------------------- Setters --------------------
    
    public void setClicked1(MyPoint2D p) { clicked1 = p; }
    public void setClicked2(MyPoint2D p) { clicked2 = p; }
    
    public void setName(String s) {};
    
    // -------------------- Private Methods --------------------
    
    private void setHasImage(HasImage h) {
        hasImage = h;
    }

    // -------------------- Public Methods --------------------
    
    public String shortName() { return hasImage.getName(); }
    public String longName() { return hasImage.fileString(); }
    
    public void scalePixels(double f) {
        if (clicked1!=null) { clicked1.times(f); }
        if (clicked2!=null) { clicked2.times(f); }
    }

    public boolean startCalibration(JFrame con) {
        // Provide initial instructions:
        Dialogs.inform(con,"Click on the first calibration point.","Calibrate");
        // Indicate that we now need to wait for user input:
        return true;
    }

    public boolean continueCalibration(JFrame con) {
        // Provide second instructions:
        Dialogs.inform(con,"Click on the second calibration point.","Calibrate");
        // Indicate that we now need to wait for user input:
        return true;
    }
    
    public CalibrateReturn calibrate(JFrame con, MyPoint2D clickPoint, MyPoint3D typed1, MyPoint3D typed2) {
        
        // Define some dialog text:
        final String title = "Calibrate";
        final String prompt1 = "Enter the coordinates of the ";
        final String prompt2 = " calibration point:";
        final String message = "Enter three numeric values.";
        
        // Determine which calibration point to work with and get the coordinates for that point:
        MyPoint3D p;
        String prompt;
        if (getClicked1()==null) { // neither have been set
            // We will set the first calibration point:
            p = typed1;
            prompt = "first";
        } else if (getClicked2()==null) { // only the first has been set
            // We will set the second calibration point:
            p = typed2;
            prompt = "second";
        } else { // both have been set
            // We will set the first calibration point:
            p = typed1;
            prompt = "first";
        }

        // Ask for the spatial coordinates of the clicked point:
        prompt = prompt1 + prompt + prompt2;
        String input;
        if (p==null) {
            input = Dialogs.input(con,prompt,title);
        } else {
            input = Dialogs.input(con,prompt,title,p.toString());
        }
        if (input==null) { return null; } // user cancelled
        input = input.trim();
        String[] inputs = input.split("[ ]+");
        if (inputs.length!=3) {
            Dialogs.error(con,"You must enter 3 values.",title);
            return null;
        }
        
        // Parse the inputs to doubles:
        double x,y,z;
        try {
            x = Double.parseDouble(inputs[0].trim());
            y = Double.parseDouble(inputs[1].trim());
            z = Double.parseDouble(inputs[2].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(con,message,title);
            return null;
        }

        // Determine which calibration point to work with and return the appropriate value:
        MyPoint3D newTyped = new MyPoint3D(x,y,z);
        if (getClicked1()==null) { // neither have been set
            // Set the first calibration point:
            setClicked1(clickPoint);
            //setTyped1(newTyped);
            return new CalibrateReturn(true,newTyped,null);
        } else if (getClicked2()==null) { // only the first has been set
            // Check the second calibration point is okay:
            if (clickPoint.getX()==getClicked1().getX()) {
               Dialogs.error(con,"The points lie on the same vertical line!",title);
               //clearCalibration();
               return null;
            }
            if (clickPoint.getY()==getClicked1().getY()) {
               Dialogs.error(con,"The points lie on the same horizontal line!",title);
               //clearCalibration();
               return null;
            }
            // Set the second calibration point:
            setClicked2(clickPoint);
            //setTyped2(newTyped);
            return new CalibrateReturn(false,null,newTyped); // don't need to continue because both typed points have now been entered
        } else { // both have been set
            // Set the first and clear the second:
            setClicked1(clickPoint);
            //setTyped1(newTyped);
            setClicked2(null);
            // (keep the typed2 point for later use)
            return new CalibrateReturn(true,newTyped,null);
        }
        
    }
    @SuppressWarnings("PublicField")
    public static class CalibrateReturn {
        public boolean ok = false;
        public MyPoint3D p1 = null;
        public MyPoint3D p2 = null;
        public CalibrateReturn(boolean ok, MyPoint3D p1, MyPoint3D p2) {
            this.ok = ok;
            this.p1 = p1;
            this.p2 = p2;
        }
    }
    
    public CalibrateReturn changeCalibrationCoordinates(JFrame con, MyPoint3D typed1, MyPoint3D typed2) {
        
        // Define some dialog text:
        final String title = "Calibrate";
        final String prompt1 = "Enter the coordinates of the ";
        String[] prompts = new String[2];
        prompts[0] = "first";
        prompts[1] = "second";
        final String prompt2 = " calibration point:";
        final String message = "Enter three numeric values.";
        MyPoint3D[] newTyped = new MyPoint3D[2];
        
        // Loop over each calibration point:
        for (int i=0 ; i<2 ; i++) {
            
            String input, prompt;
            String[] inputs;
            double x,y,z;
            MyPoint3D p;
            if (i==0) {
                p = typed1;
            } else {
                p = typed2;
            }
            
            // Ask for the spatial coordinates of the current clicked point:
            prompt = prompt1 + prompts[i] + prompt2;
            if (p==null) {
                input = Dialogs.input(con,prompt,title);
            } else {
                input = Dialogs.input(con,prompt,title,p.toString());
            }
            if (input==null) { return null; } // user cancelled
            input = input.trim();
            inputs = input.split("[ ]+");
            if (inputs.length!=3) {
                Dialogs.error(con,"You must enter 3 values.",title);
                return null;
            }
            
            // Parse the inputs to doubles and set the current typed point:
            try {
                x = Double.parseDouble(inputs[0].trim());
                y = Double.parseDouble(inputs[1].trim());
                z = Double.parseDouble(inputs[2].trim());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Dialogs.error(con,message,title);
                return null;
            }
            newTyped[i] = new MyPoint3D(x,y,z);
            
        } // for i
        
        // Return successfully with both altered typed points:
        return new CalibrateReturn(true,newTyped[0],newTyped[1]);
        
    }
    
    public void clearCalibration() {
        clicked1 = null;
        clicked2 = null;
    }
    
    public NodeVector removeNodesRange(NodeVector nodes, NodeVector nodesToRemove) {
        // Get the clicked calibration points for the ith section:
        MyPoint2D p1 = getClicked1();
        MyPoint2D p2 = getClicked2();
        double x1 = Math.min(p1.getX(),p2.getX());
        double x2 = Math.max(p1.getX(),p2.getX());
        double y1 = Math.min(p1.getY(),p2.getY());
        double y2 = Math.max(p1.getY(),p2.getY());
        // Loop over each node for the ith section:
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
        
        // Check the section is calibrated:
        String textLine;
        if (isCalibrated()) {
            // Write clicked points:
            textLine = clicked1.toString() + "\n";
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            textLine = clicked2.toString() + "\n";
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        } else {
            // Write "null" twice:
            textLine = "null\nnull\n";
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        }
        
        // Write the image file information:
        return hasImage.writeSessionInformation(writer);
        
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        
        // Read the clicked points:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading first clicked calibration point line."; }
        textLine = textLine.trim();
        if (!textLine.startsWith("null")) {
            String[] ss = textLine.split("[ ]+");
            if (ss.length<2) { return "Not enough values on first clicked calibration points line."; }
            double x,y;
            try {
                x = Double.parseDouble(ss[0].trim()); // converts to Double
                y = Double.parseDouble(ss[1].trim()); // converts to Double
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing first clicked calibration point."; }
            clicked1 = new MyPoint2D(x,y);
        }
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading second clicked calibration point line."; }
        textLine = textLine.trim();
        if (!textLine.startsWith("null")) {
            String[] ss = textLine.split("[ ]+");
            if (ss.length<2) { return "Not enough values on second clicked calibration points line."; }
            double x,y;
            try {
                x = Double.parseDouble(ss[0].trim()); // converts to Double
                y = Double.parseDouble(ss[1].trim()); // converts to Double
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing second clicked calibration point."; }
            clicked2 = new MyPoint2D(x,y);
        }
        
        // Read the image file information:
        return hasImage.readSessionInformation(reader,merge);
        
    }

}