package facetmodeller.sections;

import facetmodeller.plc.NodeVector;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import javax.swing.JFrame;

/** Vertical cross-section with an image.
 * @author Peter
 */
public class ImageCrossSection extends CrossSection {

    // ------------------ Properties -------------------
    
    private final ImageSection imageSection;

    // ------------------ Constructor -------------------

    public ImageCrossSection() { // required by the SessionLoader (should not be used elsewhere)
        super();
        imageSection = new ImageSection();
    }

    public ImageCrossSection(File f) {
        super();
        imageSection = new ImageSection(f);
    }
    
    // -------------------- Deep Copy --------------------
    
    @Override
    public ImageCrossSection copySection() {
        // Create new object:
        ImageCrossSection newSection = new ImageCrossSection();
        // Deep copy some of the DefaultSectionInfo:
        this.copyTypedAndColorTo(newSection);
        // Deep copy the ImageSection information:
        this.imageSection.deepCopyTo(newSection.imageSection);
        // Return the new object:
        return newSection;
    }
    
//    @Override
//    public ImageCrossSection deepCopy() {
//        ImageCrossSection s = new ImageCrossSection();
//        s.deepCopyDefaultInfo();
//        s.setImageSection( this.imageSection.deepCopy() );
//        return s;
//    }
//    @Override
//    public ImageCrossSection undoCopy() {
//        ImageCrossSection s = new ImageCrossSection();
//        s.setUndoCopyDefaultInfo( this.undoCopyDefaultInfo() );
//        s.setImageSection( this.imageSection.deepCopy() );
//        return s;
//    }

    // -------------------- Checkers --------------------

    @Override
    public int getType() { return Section.SECTION_IMAGE_CROSS; }
    @Override
    public boolean hasImage() { return imageSection.hasImage(); }
    @Override
    public boolean canCalibrate() { return imageSection.canCalibrate(); } // can't calibrate if image could not be loaded from file
    @Override
    public boolean isCalibrated() {
        return ( super.isCalibrated() && imageSection.isCalibrated() );
    }
    @Override
    public boolean canAddNodesOnSection() { return true; }
    @Override
    public boolean canNodesShift() { return true; }
    @Override
    public boolean canDeleteNodesRange() { return true; }
    @Override
    public boolean canChangeName() { return imageSection.canChangeName(); }
    
    // -------------------- Getters --------------------

    @Override
    public MyPoint2D getClicked1() { return imageSection.getClicked1(); }
    @Override
    public MyPoint2D getClicked2() { return imageSection.getClicked2(); }
    @Override
    public MyPoint2DVector getCorners() { return imageSection.getCorners(); }

    @Override
    public int getWidth() { return imageSection.getWidth(); }
    @Override
    public int getHeight() { return imageSection.getHeight(); }
    @Override
    public BufferedImage getImage() { return imageSection.getImage(); }
    @Override
    public File getImageFile() { return imageSection.getImageFile(); }
    @Override
    public void setImageFile(File f) { imageSection.setImageFile(f); }
    
    // -------------------- Setters --------------------
    
    @Override
    public void setClicked1(MyPoint2D p) { imageSection.setClicked1(p); }
    @Override
    public void setClicked2(MyPoint2D p) { imageSection.setClicked2(p); }
    
    @Override
    public void setName(String s) { imageSection.setName(s); }

    // -------------------- Private Methods --------------------
    
//    private void setImageSection(ImageSection s) {
//        imageSection = s;
//    }

    // -------------------- Public Methods --------------------

    @Override
    public String shortName() { return imageSection.shortName(); }
    @Override
    public String longName() { return imageSection.longName(); }
    
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
        imageSection.scalePixels(f);
    }
    
    @Override
    public boolean startCalibration(JFrame con) { return imageSection.startCalibration(con); }
    
    @Override
    public boolean continueCalibration(JFrame con) { return imageSection.continueCalibration(con); }
    
    @Override
    public boolean calibrate(JFrame con, MyPoint2D clickPoint) {
        ImageSection.CalibrateReturn out = imageSection.calibrate(con,clickPoint,getTyped1(),getTyped2());
        if (out==null) { return false; }
        if (out.p1!=null) { setTyped1(out.p1); }
        if (out.p2!=null) { setTyped2(out.p2); }
        return out.ok;
    }
    
    @Override
    public boolean changeCalibrationCoordinates(JFrame con) {
        ImageSection.CalibrateReturn out = imageSection.changeCalibrationCoordinates(con,getTyped1(),getTyped2());
        if (out==null) { return false; }
        if (out.p1!=null) { setTyped1(out.p1); }
        if (out.p2!=null) { setTyped2(out.p2); }
        return out.ok;
    }

    @Override
    public void clearCalibration() { imageSection.clearCalibration(); }
    
    @Override
    public NodeVector removeNodesRange(NodeVector nodesToRemove) {
        return imageSection.removeNodesRange(getNodes(),nodesToRemove);
    }

    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!super.writeSessionInformation(writer)) { return false; }
        return imageSection.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = super.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        return imageSection.readSessionInformation(reader,merge);
    }
    
}
