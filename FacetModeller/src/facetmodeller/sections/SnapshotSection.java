package facetmodeller.sections;

import dialogs.Dialogs;
import facetmodeller.commands.CommandVector;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.Projector3D;
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

/** An immutable section with the view defined by a parallel projection snapshot of the 3D viewer.
 * @author Peter
 */
public final class SnapshotSection extends Section {

    // ------------------ Properties -------------------
    
    private final NoImageSection noImageSection;
    private Projector3D projector;
    
    // ------------------ Constructors -------------------

    public SnapshotSection() { // required by the SessionLoader (should not be used elsewhere)
        super();
        noImageSection = new NoImageSection();
        projector = new Projector3D();
    }
    
    public SnapshotSection(String n, Color col, Projector3D proj) {
        super(col);
        noImageSection = new NoImageSection(n); // width and height will remain equal
        setProjector(proj);
    }
    
    // -------------------- Deep Copy --------------------
    
//    @Override
//    public SnapshotSection deepCopy() {
//        SnapshotSection s = new SnapshotSection();
//        s.deepCopyDefaultInfo();
//        s.setNoImageSection( this.noImageSection.deepCopy() );
//        s.setProjector( this.projector.deepCopy() );
//        return s;
//    }
//    @Override
//    public SnapshotSection undoCopy() {
//        SnapshotSection s = new SnapshotSection();
//        s.setUndoCopyDefaultInfo( this.undoCopyDefaultInfo() );
//        s.setNoImageSection( this.noImageSection.deepCopy() );
//        s.setProjector( this.projector.deepCopy() );
//        return s;
//    }
    
    // -------------------- Checkers --------------------

    @Override
    public int getType() { return Section.SECTION_SNAPSHOT; }
    @Override
    public boolean hasImage() { return noImageSection.hasImage(); }
    @Override
    public boolean canCalibrate() { return false; }
    @Override
    public boolean isCalibrated() { return true; }
    @Override
    public boolean canAddNodesOnSection() { return false; }
    @Override
    public boolean canNodesShift() { return false; }
    @Override
    public boolean canDeleteNodesRange() { return false; }
    @Override
    public boolean canChangeName() { return noImageSection.canChangeName(); }
    
    // -------------------- Getters --------------------

    @Override
    public MyPoint2D getClicked1() { return noImageSection.getClicked1(); }
    @Override
    public MyPoint2D getClicked2() { return noImageSection.getClicked2(); }
    @Override
    public MyPoint2DVector getCorners() { return null; }

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
    
    public void setProjector(Projector3D proj) {
        // Deal with the projector object:
        projector = proj;
        projector.viewParallel(); // this is important because the imageToSpace transform assumes parallel projection
        int w = getWidth(); // image width
        int h = getHeight(); // image height
        double wd = w;
        double hd = h;
        double x = wd / 2.0; // half panel width
        double y = hd / 2.0; // half panel height
        projector.setImageOrigin( new MyPoint3D(x,y,0.0) ); // overwrite image origin from 3D viewer
        projector.setImageSizeScaling(wd); // scaled to fit width=height
        // Set the typed points:
        setTyped1( imageToSpace( new MyPoint2D(0.0,0.0) ) );
        setTyped2( imageToSpace( new MyPoint2D(wd,hd) ) );
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
        Dialogs.error(con,"Calibrating a snapshot section is not possible.","Calibrate");
        return false;
    }
    @Override
    public boolean continueCalibration(JFrame con) { return false; }
    @Override
    public boolean calibrate(JFrame con, MyPoint2D clickPoint) { return false; }
    @Override
    public boolean changeCalibrationCoordinates(JFrame con) {
        Dialogs.error(con,"Changing a snapshot section calibration is not possible.","Change Calibration Coordinates");
        return false;
    }
    @Override
    public void clearCalibration() { noImageSection.clearCalibration(); } // does nothing
    @Override
    public CommandVector snapToCalibration(double pickingRadius, GroupVector groups, boolean doH, boolean doV) { return null; } // not allowed
    @Override
    public NodeVector removeNodesRange(NodeVector nodesToRemove) { return null; } // not allowed
    
    @Override
    public MyPoint3D imageToSpace(MyPoint2D p2) {
        if (p2==null) { return null; }
        MyPoint3D p3 = new MyPoint3D(p2.getX(),p2.getY(),0.0);
        p3 = projector.imageToSpaceParallel(p3);
        return p3;
    }
    @Override
    public MyPoint3D imageCornerToSpace(MyPoint2D p2) { return imageToSpace(p2); }
    @Override
    public MyPoint2D projectOnto(MyPoint3D p3) {
        MyPoint3D p = projector.spaceToImage(p3); // p is a deep copy of p3
        return new MyPoint2D(p.getX(),p.getY());
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!super.writeSessionInformation(writer)) { return false; }
        if (!noImageSection.writeSessionInformation(writer)) { return false; }
        return projector.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = super.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        msg = noImageSection.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        msg = projector.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        return null;
    }
    
}
