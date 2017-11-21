package facetmodeller.sections;

import fileio.SessionIO;
import facetmodeller.commands.CommandVector;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.HasID;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
//import fileio.FileUtils;
import geometry.Dir3D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import javax.swing.JFrame;

/** A section with nodes and facets.
 * There is a lot commented out in here - that stuff was moved to DefaultSection.
 * @author Peter Lelievre
 */
public abstract class Section extends HasID implements SessionIO {
    
    // ------------------ Properties -------------------
    
    public static final int SECTION_IMAGE_CROSS=1;
    public static final int SECTION_IMAGE_DEPTH=6;
    public static final int SECTION_NOIMAGE_CROSS=2;
    public static final int SECTION_NOIMAGE_DEPTH=5;
    public static final int SECTION_SNAPSHOT=4;
    
    private DefaultSectionInfo defaultInfo = new DefaultSectionInfo(); // favour composition over inheritence, and in this case it's the only way to do it

    // ------------------ Constructors -------------------

    public Section() {} // required by the SessionLoader (should not be used elsewhere)
    
    public Section(Color col) {
        defaultInfo.setColor(col);
    }
    
    // -------------------- Copy --------------------
    
    // -------------------- Checkers --------------------

    public abstract int getType(); // returns one of the section types above; should only be used in SessionSaver
    public abstract boolean hasImage(); // if this returns false then a rectangle is drawn instead of the image; should only be used in SectionImagePanel
    public abstract boolean canCalibrate(); // can the section be calibrated?
    public boolean isCalibrated() {  // returns true if the section image has been calibrated
        return defaultInfo.isCalibrated();
    }
    public abstract boolean canAddNodesOnSection(); // can new on-section nodes be added to the section
    public abstract boolean canNodesShift(); // can the nodes get shifted to calibration points?
    public abstract boolean canDeleteNodesRange(); // can the nodes get deleted outside the calibration points?
    public abstract boolean canChangeName(); // can change the section name
    
    // -------------------- Getters --------------------

    protected MyPoint3D getTyped1() {
        return defaultInfo.getTyped1();
    }
    protected MyPoint3D getTyped2() {
        return defaultInfo.getTyped2();
    }
    public abstract MyPoint2D getClicked1();
    public abstract MyPoint2D getClicked2();

    public Color getColor() {
        return defaultInfo.getColor();
    }
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract BufferedImage getImage();
    public abstract File getImageFile();
    public abstract void setImageFile(File f);

    public NodeVector getNodes() {
        return defaultInfo.getNodes();
    }
    public FacetVector getFacets() {
        return defaultInfo.getFacets();
    }
    public RegionVector getRegions() {
        return defaultInfo.getRegions();
    }
    
    public Dir3D getDir3D() {
        return defaultInfo.getDir3D();
    }

    // -------------------- Setters --------------------
    
    public void setTyped1(MyPoint3D p) {
        defaultInfo.setTyped1(p);
    }
    public void setTyped2(MyPoint3D p) {
        defaultInfo.setTyped2(p);
    }
    public abstract void setClicked1(MyPoint2D p);
    public abstract void setClicked2(MyPoint2D p);
    
    public void setColor(Color col) {
        defaultInfo.setColor(col);
    }
    public abstract void setName(String s);
    
    public void setUndoCopyDefaultInfo( DefaultSectionInfo d ) {
        defaultInfo = d;
    }

    // -------------------- Public Methods --------------------

    /** Returns the short name of the section (the file name minus path and extension).
     * @return  */
    public abstract String shortName();

    /** Returns the long name of the section (the file name minus extension).
     * @return  */
    public abstract String longName();

    public abstract MyPoint3D imageToSpace(MyPoint2D p2);
    public abstract MyPoint3D imageCornerToSpace(MyPoint2D p2);
    /** Projects a 3D point in space onto the section in pixel coordinates.
     * @param p3 The 3D point in spatial coordinates.
     * @return The 2D point in section pixel coordinates. */
    public abstract MyPoint2D projectOnto(MyPoint3D p3);
    
    public int numberOfNodes() { return defaultInfo.numberOfNodes(); }
    public int numberOfFacets() { return defaultInfo.numberOfFacets(); }
    public int numberOfRegions() { return defaultInfo.numberOfRegions(); }
    
    public void addNode(Node n) {
        defaultInfo.addNode(n);
    }
    public void removeNode(Node n) {
        defaultInfo.removeNode(n);
    }
    
    public void addFacet(Facet f) {
        defaultInfo.addFacet(f);
    }
    
    public void addRegion(Region r) {
        defaultInfo.addRegion(r);
    }
    public void removeFacet(Facet f) {
        defaultInfo.removeFacet(f);
    }
    public void removeRegion(Region r) {
        defaultInfo.removeRegion(r);
    }
    
    public abstract void scalePixels(double f);

    /** Sets up the calibration (e.g. asking any required questions).
     * @param con The controller FacetModeller window.
     * @return true if the calibration needs to continue by having the user click a point.
     */
    public abstract boolean startCalibration(JFrame con);
    /** Continues the calibration (e.g. asking any required questions).
     * @param con The controller FacetModeller window.
     * @return true if the calibration needs to continue by having the user click a point.
     */
    public abstract boolean continueCalibration(JFrame con);
    /** Performs a single step of the calibration, given an input point clicked by the user.
     * @param con The controller FacetModeller window.
     * @param clickPoint The point clicked by the user (pixel coordinates)
     * @return true if the calibration needs to continue by having the user click a point.
     */
    public abstract boolean calibrate(JFrame con, MyPoint2D clickPoint);
    /** Allows the user to change the typed calibration coordinates.
     * @param con The controller FacetModeller window.
     * @return true if the user supplies appropriate information and we should redraw. */
    public abstract boolean changeCalibrationCoordinates(JFrame con);
    /** Clears any calibration information. */
    public abstract void clearCalibration();
    
    /** Shifts any nodes close to the calibration points onto those calibration points.
     * @param pickingRadius
     * @param groups Only nodes in these groups should be moved.
     * @param doH Set to true to shift horizontally
     * @param doV Set to true to shift vertically
     * @return Commands that were executed to change the node positions.
     */
    public CommandVector snapToCalibration(double pickingRadius, GroupVector groups, boolean doH, boolean doV) {
        // Get the clicked calibration points in spatial coordinates:
        MyPoint3D p1 = imageToSpace(getClicked1());
        MyPoint3D p2 = imageToSpace(getClicked2());
        if (p1==null || p2==null) { return null; } // not calibrated
        // Perform the snapping:
        return defaultInfo.getNodes().snapToPoints(p1,p2,pickingRadius,groups,doH,doH);
    }
    
    /** Adds any nodes out of range of the calibration points to the input node vector.
     * @param nodesToRemove
     * @return The input vector plus any additional nodes to remove. */
    public abstract NodeVector removeNodesRange(NodeVector nodesToRemove);

    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        return defaultInfo.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        return defaultInfo.readSessionInformation(reader,merge);
    }

}