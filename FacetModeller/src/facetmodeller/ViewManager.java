package facetmodeller;

import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.ClickModeToolBar;
import facetmodeller.gui.CursorTextBar;
import facetmodeller.gui.MenuBar;
import facetmodeller.gui.PaintingOptions;
import facetmodeller.gui.Projector3D;
import facetmodeller.gui.SectionTextBar;
import facetmodeller.panels.ToolPanel;
import facetmodeller.panels.ViewsPanel;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.RegionVector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import fileio.SessionIO;
import geometry.Matrix3D;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPoint3D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Manages the visible part of the FacetModeller application.
 * This would be like the View component of the MVC architecture
 * Basically I've just encapsulated all the main GUI objects in this class.
 * @author Peter
 */
public final class ViewManager implements SessionIO {
    
    private final FacetModeller controller;
    private MenuBar menuBar;
    private ClickModeToolBar toolBar; // Tool bar for common tasks
    private CursorTextBar cursorBar; // Display area for cursor position
    private SectionTextBar sectionBar; // section information
    private ToolPanel toolPanel; // left-most panel with all the radio buttons and selectors
    private ViewsPanel viewsPanel; // the 2D and 3D viewer panels and associated buttons
    private PaintingOptions paintingOptions; // painting options
    
    public ViewManager(FacetModeller con) {
        // Set the controller:
        controller = con;
        // Make and add the objects:
        makeObjects();
        addObjects();
    }
    
    private void makeObjects() {
        // Instantiate the painting options:
        paintingOptions = new PaintingOptions(controller);
        // Instantiate the menu:
        menuBar = new MenuBar(controller);
        // Create the tool bar and buttons for commonly used functionality:
        toolBar = new ClickModeToolBar(controller,controller.numberOfDimensions());
        // Create the cursor location text bar:
        cursorBar = new CursorTextBar(controller);
        cursorBar.setBorder(BorderFactory.createEtchedBorder());
        // Create the current section bar:
        sectionBar = new SectionTextBar();
        sectionBar.setBorder(BorderFactory.createEtchedBorder());
        // Make the tool panel:
        int ndim = controller.numberOfDimensions();
        toolPanel = new ToolPanel(controller,ndim);
        // Make the views panel:
        viewsPanel = new ViewsPanel(controller,ndim,controller.getShowScroller(),controller.getShowView3DPanel());
    }

    // Adds the panels.
    private void addObjects() {
        // Put on the menu:
        controller.setJMenuBar(menuBar);
        // Get the content pane:
        Container contentPane = controller.getContentPane();
        contentPane.removeAll();
        // Add the current section and cursor location bars to a single panel:
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(1,2));
        statusPanel.add(cursorBar);
        statusPanel.add(sectionBar);
        // Add the toolBar and statusPanel to a single panel:
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        topPanel.add(toolBar);
        topPanel.add(statusPanel);
        // Add the top panel and view panel to the main panel:
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel,BorderLayout.NORTH);
        mainPanel.add(viewsPanel,BorderLayout.CENTER);
        // Add the tool panel and main panel to the window:
        contentPane.setLayout(new BorderLayout());
        if (controller.getShowToolPanel()) { contentPane.add(toolPanel,BorderLayout.WEST); }
        contentPane.add(mainPanel,BorderLayout.CENTER);
    }
    
    public void resetTitle() {
        // Set the window title bar to include the name of the session:
        controller.setTitle( "FacetModeller  ----  " + controller.getSessionFile().getName() );
    }
    
    /** Toggles the tool panel (show or hide). */
    public void toggleToolPanel() {
        Container contentPane = controller.getContentPane();
        boolean show = controller.toggleShowToolPanel(); // toggles and returns toggled value
        if (show) {
            // Attach the toolPanel to the window:
            contentPane.add(toolPanel,BorderLayout.WEST);
        } else {
            // Detach the toolPanel from the window:
            contentPane.remove(toolPanel);
        }
        contentPane.validate();
    }

    /** Toggles the 3D view panel (show or hide). */
    public void toggleView3DPanel() {
        if (controller.is2D()) { return; }
        boolean show = controller.toggleShowView3DPanel(); // toggles and returns toggled value
        viewsPanel.showOrHideView3DPanel(show);
        controller.getContentPane().validate();
    }
    
    /** Toggles the scrollers on the 2D image panel (show or hide). */
    public void toggleScroller() {
        boolean show = controller.toggleShowScroller(); // toggles and returns toggled value
        viewsPanel.showOrHideScroller(show);
        controller.getContentPane().validate();
        updateCursorBar(null);
    }
    
    // Methods for redrawing:
    public void redraw() {
        redraw2D();
        redraw3D();
    }
    public void redraw2D() {
        viewsPanel.redraw2D();
        updateCursorBar(null); // clears the cursor location bar
    }
    public void redraw3D() {
        if ( controller.is3D() && controller.getShowView3DPanel() ) { viewsPanel.redraw3D(); }
    }
    
    // Wrappers for the ExtendedCursorTextBar and SectionBar classes:
    public void updateSectionBar(int ndim, int numberOfSections, int currentSectionIndex, Section currentSection) {
        sectionBar.updateText(ndim,numberOfSections,currentSectionIndex,currentSection);
    }
    public void updateCursorBar(MyPoint2D p2) { cursorBar.updateCursor(p2); }
    public void updateMinAngleBar() { cursorBar.updateMinAngle(); }
    public void updateClosestBar(MyPoint2D p) { cursorBar.updateClosest(p); }

    // Wrappers for and MenuBar and ClickModeToolBar classes:
    public void startCalibration() { menuBar.startCalibration(); }
    public void checkItemsEnabled() {
        menuBar.checkItemsEnabled();
        toolBar.checkItemsEnabled();
    }
    public void markToolBarButtonBackground(int mode) { toolBar.markToolBarButtonBackground(mode); }
    
    // Wrappers for the ToolPanel class:
    public int getClickMode() { return toolPanel.getClickMode(); }
    public void setClickMode(int mode) { toolPanel.setClickMode(mode); }
    public boolean getShowImage() { return toolPanel.getShowImage(); }
    public boolean getShowOutlines() { return toolPanel.getShowOutlines(); }
    public boolean getShowAll() { return toolPanel.getShowAll(); }
    public boolean getShowVOI() { return toolPanel.getShowVOI(); }
    public boolean getShowFaces() { return toolPanel.getShowFaces(); }
    public boolean getShowNormals() { return toolPanel.getShowNormals(); }
    public boolean getShowRegions() { return toolPanel.getShowRegions(); }
    public boolean getNodeColorBySection() { return toolPanel.getNodeColorBySection(); }
    public SectionVector getSelectedOtherSections() { return toolPanel.getSelectedOtherSections(); }
    public Section getSelectedCurrentSection() { return toolPanel.getSelectedCurrentSection(); }
    public int getSelectedCurrentSectionIndex() { return toolPanel.getSelectedCurrentSectionIndex(); }
    public int[] getSelectedOtherSectionIndices() { return toolPanel.getSelectedOtherSectionIndices(); }
    public void setSectionVector(SectionVector v) { toolPanel.setSectionVector(v); }
    public void setSelectedCurrentSectionIndex(int s) { toolPanel.setSelectedCurrentSectionIndex(s); }
    public void setSelectedOtherSectionIndices(int[] s) { toolPanel.setSelectedOtherSectionIndices(s); }
    public void setSelectedCurrentSection(Section s) { toolPanel.setSelectedCurrentSection(s); }
    public void setSelectedOtherSections(SectionVector s) { toolPanel.setSelectedOtherSections(s); }
    public void clearCurrentSectionSelection() { toolPanel.clearCurrentSectionSelection(); }
    public void clearOtherSectionSelection() { toolPanel.clearOtherSectionSelection(); }
    public void updateSectionSelectors() { toolPanel.updateSectionSelectors(); }
    public Group getSelectedCurrentGroup() { return toolPanel.getSelectedCurrentGroup(); }
    public GroupVector getSelectedCurrentGroups() { return toolPanel.getSelectedCurrentGroups(); }
    public GroupVector getSelectedNodeGroups() { return toolPanel.getSelectedNodeGroups(); }
    public GroupVector getSelectedFacetGroups() { return toolPanel.getSelectedFacetGroups(); }
    public int getSelectedCurrentGroupIndex() { return toolPanel.getSelectedCurrentGroupIndex(); }
    public int[] getSelectedNodeGroupIndices() { return toolPanel.getSelectedNodeGroupIndices(); }
    public int[] getSelectedFacetGroupIndices() { return toolPanel.getSelectedFacetGroupIndices(); }
    public boolean isSelectedNodeGroup(Group g) { return toolPanel.isSelectedNodeGroup(g); }
    public boolean isSelectedFacetGroup(Group g) { return toolPanel.isSelectedFacetGroup(g); }
    public boolean isSelectedFacetGroupIndex(int i) { return toolPanel.isSelectedFacetGroupIndex(i); }
    public void setGroupVector(GroupVector v) { toolPanel.setGroupVector(v); }
    public void setSelectedCurrentGroup(Group s) { toolPanel.setSelectedCurrentGroup(s);  }
    public void setSelectedCurrentGroupIndex(int s) { toolPanel.setSelectedCurrentGroupIndex(s);  }
    public void setSelectedNodeGroups(GroupVector s) { toolPanel.setSelectedNodeGroups(s); }
    public void setSelectedNodeGroupIndex(int s) { toolPanel.setSelectedNodeGroupIndex(s); }
    public void setSelectedNodeGroupIndices(int[] s) { toolPanel.setSelectedNodeGroupIndices(s); }
    public void setSelectedFacetGroups(GroupVector s) { toolPanel.setSelectedFacetGroups(s); }
    public void setSelectedFacetGroupIndices(int[] s) { toolPanel.setSelectedFacetGroupIndices(s); }
    public void clearCurrentGroupSelection() { toolPanel.clearCurrentGroupSelection(); }
    public void clearFacetGroupSelection() { toolPanel.clearFacetGroupSelection(); }
    public void clearGroupSelections() { toolPanel.clearGroupSelections(); }
    public void updateGroupSelectors() { toolPanel.updateGroupSelectors(); }
    public void addToNodeGroupSelection(Group g) { toolPanel.addToNodeGroupSelection(g); }
    public void addToFacetGroupSelection(Group g) { toolPanel.addToFacetGroupSelection(g); }
    public int getShiftingX() { return toolPanel.getShiftingX(); }
    public int getShiftingY() { return toolPanel.getShiftingY(); }
    public int getPanning2DX() { return toolPanel.getPanning2DX(); }
    public int getPanning2DY() { return toolPanel.getPanning2DY(); }
    public int getShiftStep2D() { return toolPanel.getShiftStep2D(); }
    public int getPanStep2D() { return toolPanel.getPanStep2D(); }
    public void setShiftStep2D(int i) { toolPanel.setShiftStep2D(i); }
    public void setPanStep2D(int i) { toolPanel.setPanStep2D(i); }
    public void selectShiftStep2D() { toolPanel.selectShiftStep2D(); }
    public void selectPanStep2D() { toolPanel.selectPanStep2D(); }
    public void clearPan2D() { toolPanel.clearPan2D(); }
    public void resetShiftButtonText() { toolPanel.resetShiftButtonText(); }
    
    // Wrappers for the ViewsPanel class:
    public Color getBackgroundColor() { return viewsPanel.getBackgroundColor(); }
    public void setBackgroundColor(Color col) {
        viewsPanel.setBackgroundColor(col);
        if (controller.is3D()) { viewsPanel.setBackgroundColor(col); }
    }
    public void setOrigin2D(MyPoint2D p) { viewsPanel.setOrigin2D(p); }
    public NodeVector getPaintedNodes() { return viewsPanel.getPaintedNodes(); }
    public MyPoint2DVector getPaintedNodePoints() { return viewsPanel.getPaintedNodePoints(); }
    public FacetVector getPaintedFacets() { return viewsPanel.getPaintedFacets(); }
    public MyPoint2DVector getPaintedFacetCentroids() { return viewsPanel.getPaintedFacetCentroids(); }
    public RegionVector getPaintedRegions() { return viewsPanel.getPaintedRegions(); }
    public MyPoint2DVector getPaintedRegionPoints() { return viewsPanel.getPaintedRegionPoints(); }
    public void resetScroller() { viewsPanel.resetScroller(); }
    public void zoomReset2D() { viewsPanel.zoomReset2D(); }
    public boolean getMouseInside2D() { return viewsPanel.getMouseInside2D(); }
    public Projector3D getProjector3D() { return viewsPanel.getProjector3D(); }
    public Matrix3D getRotationMatrix3D() { return viewsPanel.getRotationMatrix3D(); }
    public void setRotationMatrix3D(Matrix3D m) { viewsPanel.setRotationMatrix3D(m); }
    
    // Wrappers for the PaintingOptions class:
    public Color getCalibrationColor() { return paintingOptions.getCalibrationColor(); }
    public Color getEdgeColor() { return paintingOptions.getEdgeColor(); }
    public Color getDefineFacetEdgeColor() { return paintingOptions.getDefineFacetEdgeColor(); }
    public int getPointWidth() { return paintingOptions.getPointWidth(); }
    public int getLineWidth() { return paintingOptions.getLineWidth(); }
    public double getTransparency() { return paintingOptions.getTransparency(); }
    public double getNormalLength() { return paintingOptions.getNormalLength(); }
    public Node getOriginNode3D() { return paintingOptions.getOriginNode3D(); }
    public void setCalibrationColor(Color c) { paintingOptions.setCalibrationColor(c); }
    public void setEdgeColor(Color c) { paintingOptions.setEdgeColor(c); }
    public void setDefineFacetEdgeColor(Color c) { paintingOptions.setDefineFacetEdgeColor(c); }
    public void setPointWidth(int i) { paintingOptions.setPointWidth(i); }
    public void setLineWidth(int i) { paintingOptions.setLineWidth(i); }
    public void setOrigin3D(Node node) { paintingOptions.setOrigin3D(node); }
    public void selectCalibrationColor() { paintingOptions.selectCalibrationColor(); }
    public void selectEdgeColor() { paintingOptions.selectEdgeColor(); }
    public void selectDefineFacetEdgeColor() { paintingOptions.selectDefineFacetEdgeColor(); }
    public void selectPointWidth() { paintingOptions.selectPointWidth(); }
    public void selectLineWidth() { paintingOptions.selectLineWidth(); }
    public void selectTransparency() { paintingOptions.selectTransparency(); }
    public void selectNormalLength() { paintingOptions.selectNormalLength(); }
    public void selectVerticalExaggeration() { paintingOptions.selectVerticalExaggeration(); }
    public void selectBackgroundColor() { paintingOptions.selectBackgroundColor(); }
    public void selectSectionColor() { paintingOptions.selectSectionColor(); }
    public MyPoint3D getOrigin3D() { return paintingOptions.getOrigin3D(); }
    public void clearOrigin3D() { paintingOptions.clearOrigin3D(); }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!toolPanel.writeSessionInformation(writer)) { return false; }
        if (!viewsPanel.writeSessionInformation(writer)) { return false; }
        return paintingOptions.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg;
        msg = toolPanel.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        msg = viewsPanel.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        return paintingOptions.readSessionInformation(reader,merge);
    }
    
}
