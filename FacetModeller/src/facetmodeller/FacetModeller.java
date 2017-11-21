package facetmodeller;

import facetmodeller.clicktasks.ClickTask;
import facetmodeller.commands.*;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.gui.Projector3D;
import facetmodeller.gui.SceneInfo;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.PLC;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import geometry.Matrix3D;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPoint3D;
import gui.JFrameExit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/** For building 2D or 3D models.
 * @author Peter Lelievre
 */
public final class FacetModeller extends JFrameExit {
    private static final long serialVersionUID = 1L;
    
    // ------------------ Properties -------------------
    
    // Objects that manage various components of the application:
    private FileIOManager fileIOManager = new FileIOManager(this); // manages reading and writing tasks
    private ModelManager modelManager; // the model components (plc, sections, groups)
    private ViewManager viewManager; // the GUI components
    private InteractionManager interactionManager; // manages user interaction with the GUI
    private Synthesizer synthesizer; // generates the temporary overlay objects
    private UndoVector undoVector = new UndoVector(32); // holds the undo information

    // Working objects:
    private Section calibrationSection = null; // the section to calibrate
    
    // ------------------ Main Method ------------------

    public static void main(String[] args) {
        // Launch the FacetModeller window:
        SwingUtilities.invokeLater(() -> {
            FacetModeller facetModeller = new FacetModeller();
        });
    }
    
    // ------------------ Constructor ------------------

    /** Creates the window and sets up the menu items. */
    public FacetModeller() {
        // Create the window with a title:
        super("FacetModeller","FacetModeller");
        // Check for a previous session information file and read it if it exists:
        fileIOManager.readPreviousSessionFile();
        // Display the about information and ask for number of dimensions:
        int ndim = InteractionDialogs.askNumberOfDimensions(aboutString());
        if (ndim<=0) { // user cancelled
            System.out.println("Goodbye.");
            System.exit(0);
            return;
        }
        // Make required objects (the order is important here):
        modelManager = new ModelManager(ndim);
        interactionManager = new InteractionManager(this,ndim); // must occur before the ViewManager is instantiated
        viewManager = new ViewManager(this);
        synthesizer = new Synthesizer(this);
        // Link the model SectionVector object to all section selectors:
        if (ndim==3) { setSectionVector(modelManager.getSections()); }
        // Link the model GroupVector object to all group selectors:
        setGroupVector(modelManager.getGroups());
        // Set the background colour to the default:
        setBackgroundColorDefault();
        // Resize the window to some percentage of the screen size and centre the window on the screen:
        resizeAndCentre();
        // Disable some menus on startup:
        checkItemsEnabled();
        // Initialize the display:
        redraw();
        // Make the window visible:
        setVisible(true);
        requestFocusInWindow();
    }
    private void resizeAndCentre() {
        // Resize the window to some percentage of the screen size:
        Dimension dim = getToolkit().getScreenSize();
        setSize(dim.width , (int)(0.75*dim.height) );
        // Centre the window on the screen:
        Dimension siz = this.getSize();
        setLocation((int)((dim.width  - siz.width )*0.5),
                (int)((dim.height - siz.height)*0.5));
    }
    
    public void setBackgroundColorDefault() {
        //Color col = getBackground();
        Color col = new Color(82,87,110);
        setBackgroundColor(col);
    }
    
    @Override
    protected void runBeforeExit() { fileIOManager.writePreviousSessionFile(); }
    
    // -------------------- Implemented methods from class JFrameExit --------------------
    
    private String versionNumberString() {
        return "3.0"; // you must be able to parse this as a double
    }
    @Override
    protected String versionString() {
        return versionNumberString(); // I could add more information here e.g. a date.
    }
    @Override
    protected String rulesString() {
        return "FOR INTERNAL USE WITHIN COLIN FARQUHARSON'S\n"
                + "RESEARCH GROUP (OR EXTERNALLY BY PERMISSION).\n"
                + "FOR ACADEMIC RESEARCH ONLY.";
    }
    @Override
    protected String authorString() {
        return "Author: Peter Lelievre";
    }
    @Override
    protected String contactString() {
        return "Contact: plelievre@mun.ca";
    }
    
    public int versionInt() {
        double d =  Double.parseDouble( versionNumberString() );
        return (int)Math.floor(d);
    }
    
    // -------------------- Methods called by the old read methods in the SessionLoader class (should not be used elsewhere) --------------------

    public void resetPLC(PLC plc, boolean merge) {
        if (merge && is3D()) {
            modelManager.addPLC(plc);
        } else {
            modelManager.setPLC(plc);
        }
    }
    
    public void resetSectionVector(SectionVector v, boolean merge) {
        int ndim = numberOfDimensions();
        if (merge && ndim==3) {
            modelManager.addSections(v);
        } else {
            modelManager.setSections(v);
        }
        if (ndim==3) { setSectionVector(modelManager.getSections()); }
    }
    
    public void resetGroupVector(GroupVector v, boolean merge) {
        if (merge) {
            modelManager.addGroups(v);
        } else {
            modelManager.setGroups(v);
        }
        setGroupVector(modelManager.getGroups());
    }

    // ------------------- Methods called by listeners -------------------

    public void mouseDrag(MyPoint2D p) { interactionManager.mouseDrag(p); }
    public void mouseClick(MyPoint2D p) { interactionManager.mouseClick(p); }
    public void mouseMove(MyPoint2D p) { interactionManager.mouseMove(p); }

    public void groupSelectionChanged(boolean doRedraw) {
        // Set the current facet to null just in case we are in the middle of creating a facet:
        synthesizer.clearCurrentFacet();
        // Check if we need to enable or disable any menu items:
        checkItemsEnabled();
        // Redraw only if there is at least one section:
        if ( hasSections() && doRedraw ) { redraw(); }
    }
   
    public void sectionSelectionChanged(boolean current) {
        // Check if it was the current or other section that changed:
        if (current) {
            // Check if the section has an image:
            Section currentSection = this.getSelectedCurrentSection();
            if (currentSection==null) { return; }
            if (currentSection.hasImage()) {
                BufferedImage image = currentSection.getImage();
                while (image==null) { // I'll also jump out of this if the user cancels a dialog
                    // Check if the file exists:
                    File file = currentSection.getImageFile();
                    if (file.exists()) {
                        InteractionDialogs.imageFileError(this);
                        return;
                    }
                    // Ask the user to locate the image file:
                    file = InteractionDialogs.imageFileRequest(this,file);
                    if (file==null) { return; }
                    // Set the file (this also reads the image from the file):
                    currentSection.setImageFile(file);
                    // Get the section image:
                    image = currentSection.getImage();
                }
            }
            // Update the name of the current section in the bar:
            updateSectionBar();
        }
        // Check if we need to enable or disable any menu items:
        checkItemsEnabled();
        // Redraw:
        redraw();
    }
    
    // -------------------- Methods associated with menu tasks that are too small to bother creating MenuTask classes for --------------------

    public void setShowConfirmationDialogs(boolean show) {
        interactionManager.setShowConfirmationDialogs(show);
        checkItemsEnabled();
    }
    
    public void defineVOI() {
        if ( modelManager.defineVOI(this) ) {
            // Enable or disable menu items:
            checkItemsEnabled();
            // Redraw:
            redraw();
        }
    }
    
    public void openSession(boolean prev) {
        fileIOManager.openSession(prev);
        // Clear the undo information:
        undoVector.clear();
        // Enable or disable menu items:
        checkItemsEnabled();
    }
    
    public boolean saveSession(String title, FileFilter filter, boolean saveAs) {
        return fileIOManager.saveSession(this,title,filter,saveAs);
    }
    
    public void clearOriginNode3D() {
        // Clear the origin:
        viewManager.clearOrigin3D();
        // Redraw the 3D view:
        redraw3D();
    }
    
    // ------------------- Methods required for click modes that provide dialogs before/between clicks: -------------------

    // These methods are used in both the StartCalibration and CalibrationTask classes:
    public boolean calibrationCheck() {
        if (!hasSections()) { return false; }
        //if (!hasGroups()) { return false; }
        Section currentSection = getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        //if (getSelectedCurrentGroup()==null) { return false; }
        //return ( hasNodes() && hasFacets() && currentSection.canCalibrate() );
        return currentSection.canCalibrate();
    }
    public void endCalibration() {
        // Clear the calibration section and click mode:
        calibrationSection = null;
        setClickMode(ClickModeManager.MODE_NULL);
        // Enable or disable menu items:
        checkItemsEnabled();
        // Repaint:
        redraw();
    }
    // Some associated methods:
    public Section getCalibrationSection() { return calibrationSection; }
    public void setCalibrationSection() { calibrationSection = getSelectedCurrentSection(); }
    public void startCalibration() { viewManager.startCalibration(); }
    
    // These methods are used in both the StartOriginNode3D and OriginNode3DTask classes:
    public boolean originNode3DCheck() {
        if (!is3D()) { return false; }
        if (!hasSections()) { return false; }
        if (!hasGroups()) { return false; }
        if (getSelectedCurrentSection()==null) { return false; }
        if (getSelectedCurrentGroup()==null) { return false; }
        return hasNodes();
    }
    public void endOriginNode3D() {
        // Clear the click mode:
        setClickMode(ClickModeManager.MODE_NULL);
    }
    
    // ------------------- Wrappers -------------------
    // TODO: make sure wrapper methods are all used. I may want to comment them or make them private.
    
    // Wrappers for the managers that should be used sparingly:
    public ModelManager getModelManager() { return modelManager; } // TODO: check this is used sparingly
    public ViewManager getViewManager() { return viewManager; }
    public InteractionManager getInteractionManager() { return interactionManager; }
    
    // Wrappers for the ModelManager class that should be used sparingly: TODO: make sure they are
    public VOI getVOI() { return modelManager.getVOI(); }
    public PLC getPLC() { return modelManager.getPLC(); }
    public Node getNode(int i) { return modelManager.getNode(i); } // TODO: check this is used sparingly
    public boolean hasSections() { return modelManager.hasSections(); } // TODO: check this is used sparingly
    public boolean hasGroups() { return modelManager.hasGroups(); } // TODO: check this is used sparingly
    public Section getSection(int i) { return modelManager.getSection(i); } // TODO: check this is used sparingly
    public Group getGroup(int i) { return modelManager.getGroup(i); } // TODO: check this is used sparingly
    public void copyCalibration(double step) { modelManager.copyCalibration(getSelectedCurrentSection(),step); } // TODO: check this is used sparingly
    
    // Wrappers for the ModelManager class:
    public boolean hasVOI() { return modelManager.hasVOI(); }
    public MyPoint3D[][] getVOIEdges() { return modelManager.getVOIEdges(); }
    public void showPLCInfo() { modelManager.showPLCInfo(this); }
    public boolean hasNodes() { return modelManager.hasNodes(); }
    public boolean hasFacets() { return modelManager.hasFacets(); }
    public boolean hasRegions() { return modelManager.hasRegions(); }
    public int indexOfNode(Node n) { return modelManager.indexOfNode(n); }
    public int indexOfFacet(Facet f) { return modelManager.indexOfFacet(f); }
    public int indexOfRegion(Region r) { return modelManager.indexOfRegion(r); }
    public boolean plcIsEmpty() { return modelManager.plcIsEmpty(); }
    public boolean inOrOn(MyPoint3D p) { return modelManager.inOrOn(p); }
    public boolean is2D() { return modelManager.is2D(); }
    public boolean is3D() { return modelManager.is3D(); }
    public int numberOfDimensions() { return modelManager.numberOfDimensions(); }
    public int numberOfNodes() { return modelManager.numberOfNodes(); }
    public int numberOfFacets() { return modelManager.numberOfFacets(); }
    public int numberOfSections() { return modelManager.numberOfSections(); }
    public int numberOfGroups() { return modelManager.numberOfGroups(); }
    public boolean writeGroupDefinitions(File file) { return modelManager.writeGroupDefinitions(file); }
    public MyPoint3D[] getVOICorners() { return modelManager.getVOICorners(); }
    public NodeVector findUnusedNodes() { return modelManager.findUnusedNodes(); }
    public DuplicateNodeInfo findDuplicateNodes() { return modelManager.findDuplicateNodes(); }
    public FacetVector findBadFacets() { return modelManager.findBadFacets(numberOfDimensions()); }
    public FacetVector findHoles() { return modelManager.findHoles(); }
    public NodeVector removeNodesCalibrationRange() { return modelManager.removeNodesCalibrationRange(); }
    public void clearPLC() { modelManager.clearPLC(); }
    public void resetIDs() { modelManager.resetIDs(); }
    public void clearSections() { modelManager.clearSections(); }
    public void addSection(Section s) { modelManager.addSection(s); }
    public void removeSection(Section s) { modelManager.removeSection(s); }
    public void addGroup(Group g) { modelManager.addGroup(g); }
    public void addGroup(Group g, int i) { modelManager.addGroup(g,i); }
    public void addGroups(GroupVector gv) { modelManager.addGroups(gv); }
    public void removeGroup(Group g) { modelManager.removeGroup(g); }
    public void addSectionsFromFiles(File[] files, boolean iscross) {
        modelManager.addSectionsFromFiles(files,iscross);
    }
    public CommandVector snapToVOI(double snappingDistance, GroupVector groups, boolean doH, boolean doV) {
        return modelManager.snapToVOI(snappingDistance,groups,doH,doV);
    }
    public CommandVector snapToGrid(double m, GroupVector groups, boolean doH, boolean doV) {
        return modelManager.snapToGrid(m,groups,doH,doV);
    }
    public CommandVector snapToCalibration(double pickingRadius, GroupVector groups, boolean doH, boolean doV) {
        return modelManager.snapToCalibration(pickingRadius,groups,doH,doV);
    }
    public CommandVector translateNodes(MyPoint3D p) { return modelManager.translate(p); }
    public void scalePixels(double factor) { modelManager.scalePixels(factor); }
    
    // Wrappers for the ViewManager and ModelManager classes:
    public Section getSelectedCurrentSection() {
        if (!hasSections()) { return null; }
        if (is3D()) {
            return viewManager.getSelectedCurrentSection();
        } else {
            return modelManager.getSection(0); // there is only one section for 2D models
        }
    }
    public int getSelectedCurrentSectionIndex() {
        if (!hasSections()) { return -1; }
        if (is3D()) {
            return viewManager.getSelectedCurrentSectionIndex();
        } else {
            return 0; // there is only one section for 2D models
        }
    }
    public SceneInfo getSceneInfo3D() { return modelManager.getSceneInfo3D(viewManager.getOrigin3D()); }
    
    // Wrappers for the ViewManager class:
    public void resetTitle() { viewManager.resetTitle(); }
    public void toggleToolPanel() { viewManager.toggleToolPanel(); }
    public void toggleView3DPanel() { viewManager.toggleView3DPanel(); }
    public void toggleScroller() { viewManager.toggleScroller(); }
    public void redraw() { viewManager.redraw(); }
    public void redraw2D() { viewManager.redraw2D(); }
    public void redraw3D() { viewManager.redraw3D(); }
    private void updateSectionBar() {
        viewManager.updateSectionBar(numberOfDimensions(),numberOfSections(),
                getSelectedCurrentSectionIndex(),getSelectedCurrentSection());
    }
    public void updateCursorBar(MyPoint2D p) { viewManager.updateCursorBar(p); }
    public void updateMinAngleBar() { viewManager.updateMinAngleBar(); }
    public void updateClosestBar(MyPoint2D p) { viewManager.updateClosestBar(p); }
    public void checkItemsEnabled() { viewManager.checkItemsEnabled(); }
    public void markToolBarButtonBackground(int mode) { viewManager.markToolBarButtonBackground(mode); }
    public int getClickMode() { return viewManager.getClickMode(); }
    public void setClickMode(int mode) { viewManager.setClickMode(mode); }
    public boolean getShowImage() { return viewManager.getShowImage(); }
    public boolean getShowOutlines() { return viewManager.getShowOutlines(); }
    public boolean getShowAll() { return viewManager.getShowAll(); }
    public boolean getShowVOI() { return viewManager.getShowVOI(); }
    public boolean getShowFaces() { return viewManager.getShowFaces(); }
    public boolean getShowRegions() { return viewManager.getShowRegions(); }
    public boolean getNodeColorBySection() { return viewManager.getNodeColorBySection(); }
    public SectionVector getSelectedOtherSections() { return viewManager.getSelectedOtherSections(); }
    public int[] getSelectedOtherSectionIndices() { return viewManager.getSelectedOtherSectionIndices(); }
    public void setSectionVector(SectionVector v) { viewManager.setSectionVector(v); }
    public void setSelectedCurrentSectionIndex(int s) { viewManager.setSelectedCurrentSectionIndex(s); }
    public void setSelectedOtherSectionIndices(int[] s) { viewManager.setSelectedOtherSectionIndices(s); }
    public void setSelectedCurrentSection(Section s) { viewManager.setSelectedCurrentSection(s); }
    public void setSelectedOtherSections(SectionVector s) { viewManager.setSelectedOtherSections(s); }
    public void clearCurrentSectionSelection() { viewManager.clearCurrentSectionSelection(); }
    public void clearOtherSectionSelection() { viewManager.clearOtherSectionSelection(); }
    public void updateSectionSelectors() { viewManager.updateSectionSelectors(); }
    public Group getSelectedCurrentGroup() { return viewManager.getSelectedCurrentGroup(); }
    public GroupVector getSelectedCurrentGroups() { return viewManager.getSelectedCurrentGroups(); }
    public GroupVector getSelectedNodeGroups() { return viewManager.getSelectedNodeGroups(); }
    public GroupVector getSelectedFacetGroups() { return viewManager.getSelectedFacetGroups(); }
    public int getSelectedCurrentGroupIndex() { return viewManager.getSelectedCurrentGroupIndex(); }
    public int[] getSelectedNodeGroupIndices() { return viewManager.getSelectedNodeGroupIndices(); }
    public int[] getSelectedFacetGroupIndices() { return viewManager.getSelectedFacetGroupIndices(); }
    public boolean isSelectedNodeGroup(Group g) { return viewManager.isSelectedNodeGroup(g); }
    public boolean isSelectedFacetGroup(Group g) { return viewManager.isSelectedFacetGroup(g); }
    public boolean isSelectedFacetGroupIndex(int i) { return viewManager.isSelectedFacetGroupIndex(i); }
    public void setGroupVector(GroupVector v) { viewManager.setGroupVector(v); }
    public void setSelectedCurrentGroup(Group s) { viewManager.setSelectedCurrentGroup(s);  }
    public void setSelectedCurrentGroupIndex(int s) { viewManager.setSelectedCurrentGroupIndex(s);  }
    public void setSelectedNodeGroups(GroupVector s) { viewManager.setSelectedNodeGroups(s); }
    public void setSelectedNodeGroupIndex(int s) { viewManager.setSelectedNodeGroupIndex(s); }
    public void setSelectedNodeGroupIndices(int[] s) { viewManager.setSelectedNodeGroupIndices(s); }
    public void setSelectedFacetGroups(GroupVector s) { viewManager.setSelectedFacetGroups(s); }
    public void setSelectedFacetGroupIndices(int[] s) { viewManager.setSelectedFacetGroupIndices(s); }
    public void clearCurrentGroupSelection() { viewManager.clearCurrentGroupSelection(); }
    public void clearFacetGroupSelection() { viewManager.clearFacetGroupSelection(); }
    public void clearGroupSelections() { viewManager.clearGroupSelections(); }
    public void updateGroupSelectors() { viewManager.updateGroupSelectors(); }
    public void addToNodeGroupSelection(Group g) { viewManager.addToNodeGroupSelection(g); }
    public void addToFacetGroupSelection(Group g) { viewManager.addToFacetGroupSelection(g); }
    public int getShiftingX() { return viewManager.getShiftingX(); }
    public int getShiftingY() { return viewManager.getShiftingY(); }
    public int getPanning2DX() { return viewManager.getPanning2DX(); }
    public int getPanning2DY() { return viewManager.getPanning2DY(); }
    public int getShiftStep2D() { return viewManager.getShiftStep2D(); }
    public int getPanStep2D() { return viewManager.getPanStep2D(); }
    public void setShiftStep2D(int i) { viewManager.setShiftStep2D(i); }
    public void setPanStep2D(int i) { viewManager.setPanStep2D(i); }
    public void selectShiftStep2D() { viewManager.selectShiftStep2D(); }
    public void selectPanStep2D() { viewManager.selectPanStep2D(); }
    public void clearPan2D() { viewManager.clearPan2D(); }
    public void resetShiftButtonText() { viewManager.resetShiftButtonText(); }
    public Color getBackgroundColor() { return viewManager.getBackgroundColor(); }
    public void setBackgroundColor(Color col) {
        viewManager.setBackgroundColor(col);
        if (is3D()) { viewManager.setBackgroundColor(col); }
    }
    public void setOrigin2D(MyPoint2D p) { viewManager.setOrigin2D(p); }
    public NodeVector getPaintedNodes() { return viewManager.getPaintedNodes(); }
    public MyPoint2DVector getPaintedNodePoints() { return viewManager.getPaintedNodePoints(); }
    public FacetVector getPaintedFacets() { return viewManager.getPaintedFacets(); }
    public MyPoint2DVector getPaintedFacetCentroids() { return viewManager.getPaintedFacetCentroids(); }
    public RegionVector getPaintedRegions() { return viewManager.getPaintedRegions(); }
    public MyPoint2DVector getPaintedRegionPoints() { return viewManager.getPaintedRegionPoints(); }
    public void resetScroller() { viewManager.resetScroller(); }
    public void zoomReset2D() { viewManager.zoomReset2D(); }
    public boolean getMouseInside2D() { return viewManager.getMouseInside2D(); }
    public Projector3D getProjector3D() { return viewManager.getProjector3D(); }
    public Matrix3D getRotationMatrix3D() { return viewManager.getRotationMatrix3D(); }
    public void setRotationMatrix3D(Matrix3D m) { viewManager.setRotationMatrix3D(m); }
    public Color getCalibrationColor() { return viewManager.getCalibrationColor(); }
    public Color getEdgeColor() { return viewManager.getEdgeColor(); }
    public Color getDefineFacetEdgeColor() { return viewManager.getDefineFacetEdgeColor(); }
    public int getPointWidth() { return viewManager.getPointWidth(); }
    public int getLineWidth() { return viewManager.getLineWidth(); }
    public Node getOriginNode3D() { return viewManager.getOriginNode3D(); }
    public void setCalibrationColor(Color c) { viewManager.setCalibrationColor(c); }
    public void setEdgeColor(Color c) { viewManager.setEdgeColor(c); }
    public void setDefineFacetEdgeColor(Color c) { viewManager.setDefineFacetEdgeColor(c); }
    public void setPointWidth(int i) { viewManager.setPointWidth(i); }
    public void setLineWidth(int i) { viewManager.setLineWidth(i); }
    public void setOrigin3D(Node node) { viewManager.setOrigin3D(node); }
    public void selectCalibrationColor() { viewManager.selectCalibrationColor(); }
    public void selectEdgeColor() { viewManager.selectEdgeColor(); }
    public void selectDefineFacetEdgeColor() { viewManager.selectDefineFacetEdgeColor(); }
    public void selectPointWidth() { viewManager.selectPointWidth(); }
    public void selectLineWidth() { viewManager.selectLineWidth(); }
    public void selectVerticalExaggeration() { viewManager.selectVerticalExaggeration(); }
    public void selectBackgroundColor() { viewManager.selectBackgroundColor(); }
    public void selectSectionColor() { viewManager.selectSectionColor(); }
    
    // Wrappers for the InteractionManager class:
    public ClickTask getClickTask(int mode) { return interactionManager.getClickTask(mode); }
    public boolean getShowView3DPanel() { return interactionManager.getShowView3DPanel(); }
    public boolean getShowToolPanel() { return interactionManager.getShowToolPanel(); }
    public boolean getShowScroller() { return interactionManager.getShowScroller(); }
    public double getPickingDistance() { return interactionManager.getPickingDistance(); }
    public double getAutoFacetFactor() { return interactionManager.getAutoFacetFactor(); }
    public boolean getShowConfirmationDialogs() { return interactionManager.getShowConfirmationDialogs(); }
    public void setPickingDistance(double d) { interactionManager.setPickingDistance(d); }
    public void setAutoFacetFactor(double d) { interactionManager.setAutoFacetFactor(d); }
    public boolean toggleShowToolPanel() { return interactionManager.toggleShowToolPanel(); }
    public boolean toggleShowView3DPanel() { return interactionManager.toggleShowView3DPanel(); }
    public boolean toggleShowScroller() { return interactionManager.toggleShowScroller(); }
    public void selectPickingRadius() { interactionManager.selectPickingRadius(); }
    public void selectAutoFacetFactor() { interactionManager.selectAutoFacetFactor(); }

    // Wrappers for the Synthesizer class:
    public boolean isLocked() { return synthesizer.isLocked(); }
    public void unlock() { synthesizer.unlock(); }
    public void toggleLock() { synthesizer.toggleLock(); }
    public Node getCandidateNode() { return synthesizer.getCandidateNode(); }
    public Node getCurrentNode() { return synthesizer.getCurrentNode(); }
    public Facet getCurrentFacet() { return synthesizer.getCurrentFacet(); }
    public Node getClosestNode() { return synthesizer.getClosestNode(); }
    public MyPoint2D getClosestNodePoint() { return synthesizer.getClosestNodePoint(); }
    public Facet getClosestFacet() { return synthesizer.getClosestFacet(); }
    public Region getClosestRegion() { return synthesizer.getClosestRegion(); }
    public MyPoint2D getClosestRegionPoint() { return synthesizer.getClosestRegionPoint(); }
    public void setCandidateNode(Node node) { synthesizer.setCandidateNode(node); }
    public void setCurrentNode(Node node) { synthesizer.setCurrentNode(node); }
    public void setCurrentFacet(Facet facet) { synthesizer.setCurrentFacet(facet); }
    public void clearCurrentNode() { synthesizer.clearCurrentNode(); }
    public void clearCurrentFacet() { synthesizer.clearCurrentFacet(); }
    public void clearClosestNode() { synthesizer.clearClosestNode(); }
    public void clearClosestFacet() { synthesizer.clearClosestFacet(); }
    public void clearClosestRegion() { synthesizer.clearClosestRegion(); }
    //private void clearCurrentTemporaryOverlays() { synthesizer.clearCurrent(); }
    public void clearClosestTemporaryOverlays() { synthesizer.clearClosest(); }
    public void clearAllTemporaryOverlays() { synthesizer.clearAll(); }
    public boolean checkCurrentNode() { return synthesizer.checkCurrentNode(); }
    public boolean checkCurrentFacet() { return synthesizer.checkCurrentFacet(); }
    public boolean calculateClosest(MyPoint2D p) { return synthesizer.calculateClosest(p); }
    public boolean calculateClosestNode(MyPoint2D p) { return synthesizer.calculateClosestNode(p); }
    public boolean calculateClosestFacet(MyPoint2D p) { return synthesizer.calculateClosestFacet(p); }
    public boolean calculateClosestRegion(MyPoint2D p) { return synthesizer.calculateClosestRegion(p); }
    public Facet calculateTriFacet(MyPoint2D p) { return synthesizer.calculateTriFacet(p); }
    public Facet calculateLineFacet(MyPoint2D p) { return synthesizer.calculateLineFacet(p); }
    public int whichTemporaryOverlayIsClosest(MyPoint2D p) { return synthesizer.whichIsClosest(p); }
    
    // Wrappers for the FacetModellerIO class:
    public File getSessionFile() { return fileIOManager.getSessionFile(); }
    public void setSessionFile(File f) { fileIOManager.setSessionFile(f); }
    public File getOpenDirectory() { return fileIOManager.getOpenDirectory(); }
    public File getSaveDirectory() { return fileIOManager.getSaveDirectory(); }
    public void setOpenDirectory(File f) { fileIOManager.setOpenDirectory(f); }
    public void setSaveDirectory(File f) { fileIOManager.setSaveDirectory(f); }
    public void exportPoly(int whatToExport) { fileIOManager.exportPoly(whatToExport); }
    public void exportPair(int whatToExport) { fileIOManager.exportPair(whatToExport); }
    public void exportVTU() { fileIOManager.exportVTU(); }
    public void exportNodes() { fileIOManager.exportNodes(); }
    public void exportFacets() { fileIOManager.exportFacets(); }
    public void exportRegions() { fileIOManager.exportRegions(); }
    public void exportAll() { fileIOManager.exportAll(); }
    
    // Wrappers for the UndoVector class:
    public boolean getUndoIsEmpty() { return undoVector.isEmpty(); }
    public Command undoVectorGet() { return undoVector.get(); }
    public void undoVectorAdd(Command com) { undoVector.add(com); }
    public void undoVectorRemove() { undoVector.remove(); }

}
