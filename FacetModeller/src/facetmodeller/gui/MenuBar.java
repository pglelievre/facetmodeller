package facetmodeller.gui;

import gui.MenuTaskMenuItem;
import tasks.MenuTask;
import facetmodeller.FacetModeller;
import facetmodeller.FileIOManager;
import facetmodeller.ModelManager;
import facetmodeller.clicktasks.ClickTask;
import facetmodeller.sections.Section;
import facetmodeller.menutasks.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/** The menu on the FacetModeller window.
 * @author Peter Lelievre.
 */
public final class MenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;

    // FacetModeller JFrame that the menu bar is associated with:
    private final FacetModeller controller;

    // Second level menu items:
    private JMenuItem miAbout, miExit,
            miOpenSession, miOpenPreviousSession,
            miExportPoly, miExportPolyGroup, miExportPolyDisplayed, 
            miExportPair, miExportPairGroup, miExportPairDisplayed,
            miExportNodes, miExportFacets, miExportRegions, miExportVTU, miExportAll,
            miChangeSectionColor, miPLCInfo,miDefineVOI,
            miCalibrationColor,miEdgeColor,miDefineFacetEdgeColor,miDefaultBackgroundColor,miSelectBackgroundColor,
            miPointWidth, miLineWidth, miShiftStep2D,miPanStep2D, miVerticalExaggeration,
            miPickingRadius, miAutoFacetFactor, miToggleToolPanel, miToggleView3DPanel1, miToggleView3DPanel2, miToggleScroller,
            miClearOrigin3D, miShowConfirmationDialogs, miHideConfirmationDialogs;
    private ClickTaskMenuItem miNullMode,miInfoMode,miSetOrigin2DMode,miAddNodes,miDeleteNodes,miMoveNodes,miMergeNodes,miChangeNodes,miChangeNodesSection,
            miDefinePolyFacets,miDefinePolyFacetsTri,miDefineTriFacets,miDefineLineFacets,miDeleteFacets,miChangeFacets,
            miReverseFacets,miEdgeFlip,miAddNodesInTriFacets,miAddNodesOnEdges,
            miAddRegions,miDeleteRegions;
    private MenuTaskMenuItem miUndo,miSaveSession,miSaveSessionAs,miLoadNodesAndFacets,
            miLoadCrossSectionImages,miLoadDepthSectionImages,miLoadGroups,miSaveGroups,
            miSectionInfo,miNewNoImageCrossSection,miNewNoImageDepthSection,miNewSnapshotSection,miResetSnapshotSection,
            miGroupUp1,miGroupUp2,miGroupTop,miGroupDown1,miGroupDown2,miGroupBottom,
            miSectionName, miGroupName,miGroupColor,miGroupNodeColor,miGroupFacetColor,miGroupRegionColor,
            miCopyCalibration, miNodesAtCalibration, miAddNodesVOI,
            miSnapToCalibration,miSnapToCalibrationVertical,miSnapToCalibrationHorizontal,
            miSnapToVOI,miSnapToVOIVertical,miSnapToVOIHorizontal,
            miSnapToGrid,miSnapToGridVertical,miSnapToGridHorizontal,miTranslate,miScalePixels,
            miClearPLC,miClearFacets,miFindBadFacets,miFindHoles,miFindUnusedNodes,
            miDeleteDuplicateNodes,miMergeDuplicateNodes,miFindNodesCalibration,miFindNodesVOI,
            miFindNodesIndex,miFindFacetsIndex,miSplitGroupVOI,miSplitGroupBoundary,
            miDeleteNodeGroup,miDeleteFacetGroup,miDeleteDisplayedNodes,miDeleteDisplayedFacets,miDeleteSection,miNewGroup,miDeleteGroup,miMergeGroups,
            miCalibrate,miNodeOrigin3D,miCalibrateTyped,miSaveView3D,miLoadView3D;
    private ArrayList<ClickTaskMenuItem> clickTaskMenuItems = new ArrayList<>();
    private ArrayList<MenuTaskMenuItem> menuTaskMenuItems = new ArrayList<>();

    /** Makes the menu bar.
     * @param con FacetModeller window (JFrame extension) to place the menu on.
     */
    public MenuBar(FacetModeller con) {
        super();
        this.controller = con;
        makeMenuItems();
        addMenuItems();
    }
    
    private void makeMenuItems() {
        
        MenuListener listener = new MenuListener();

        // Build the FacetModeller menu items:
        miAbout = makeMenuItem("About","Display information about FacetModeller",listener);
        miExit = makeMenuItem("Exit","Exit FacetModeller without saving",listener);
        miUndo = makeMenuTaskMenuItem(new UndoPreviousCommandMenuTask(controller),listener);

        // Build the file menu items:
        miLoadCrossSectionImages = makeMenuTaskMenuItem(new LoadSectionFilesMenuTask(controller,true),listener);
        miLoadDepthSectionImages = makeMenuTaskMenuItem(new LoadSectionFilesMenuTask(controller,false),listener);
        miLoadNodesAndFacets = makeMenuTaskMenuItem(new LoadNodesAndFacetsMenuTask(controller),listener);
        miOpenSession = makeMenuItem("Open session","Open a previously saved session",listener);
        miOpenPreviousSession = makeMenuItem("Open previous session","Open the last opened session",listener);
        miSaveSession = makeMenuTaskMenuItem(new SaveSessionMenuTask(controller,false),listener);
        miSaveSessionAs = makeMenuTaskMenuItem(new SaveSessionMenuTask(controller,true),listener);
        miExportPoly = makeMenuItem("Model to .poly file",listener);
        miExportPair = makeMenuItem("Model to .node/.ele files",listener);
        miExportVTU = makeMenuItem("Model to .vtu file",listener);
        miExportNodes = makeMenuItem("Nodes to .node file",listener);
        miExportPolyGroup = makeMenuItem("Selected group to .poly file",listener);
        miExportPairGroup = makeMenuItem("Selected group to .node/.ele files",listener);
        miExportPolyDisplayed = makeMenuItem("Displayed groups to .poly file",listener);
        miExportPairDisplayed = makeMenuItem("Displayed groups to .node/.ele files",listener);
        miExportFacets = makeMenuItem("Facets to .ele file",listener);
        miExportRegions = makeMenuItem("Regions to .node file",listener);
        miExportAll = makeMenuItem("All possible files",listener);

        // Build the click mode menu items:
        miNullMode = makeClickTaskMenuItem(ClickModeManager.MODE_NULL,listener);
        miSetOrigin2DMode = makeClickTaskMenuItem(ClickModeManager.MODE_ORIGIN_2D,listener);
        miInfoMode = makeClickTaskMenuItem(ClickModeManager.MODE_INFO,listener);
        miAddNodes = makeClickTaskMenuItem(ClickModeManager.MODE_ADD_NODES,listener);
        miDeleteNodes = makeClickTaskMenuItem(ClickModeManager.MODE_DELETE_NODES,listener);
        miMoveNodes = makeClickTaskMenuItem(ClickModeManager.MODE_MOVE_NODES,listener);
        miMergeNodes = makeClickTaskMenuItem(ClickModeManager.MODE_MERGE_NODES,listener);
        miChangeNodes = makeClickTaskMenuItem(ClickModeManager.MODE_CHANGE_NODES_GROUP,listener);
        miChangeNodesSection = makeClickTaskMenuItem(ClickModeManager.MODE_CHANGE_NODES_SECTION,listener);
        miAddRegions = makeClickTaskMenuItem(ClickModeManager.MODE_ADD_REGIONS,listener);
        miDeleteRegions = makeClickTaskMenuItem(ClickModeManager.MODE_DELETE_REGIONS,listener);
        miDefinePolyFacets = makeClickTaskMenuItem(ClickModeManager.MODE_DEFINE_POLY_FACETS,listener);
        miDefinePolyFacetsTri = makeClickTaskMenuItem(ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI,listener);
        miDefineTriFacets = makeClickTaskMenuItem(ClickModeManager.MODE_DEFINE_TRI_FACETS,listener);
        miDefineLineFacets = makeClickTaskMenuItem(ClickModeManager.MODE_DEFINE_LINE_FACETS,listener);
        miDeleteFacets = makeClickTaskMenuItem(ClickModeManager.MODE_DELETE_FACETS,listener);
        miChangeFacets = makeClickTaskMenuItem(ClickModeManager.MODE_CHANGE_FACETS_GROUP,listener);
        miReverseFacets = makeClickTaskMenuItem(ClickModeManager.MODE_REVERSE_FACETS,listener);
        miEdgeFlip = makeClickTaskMenuItem(ClickModeManager.MODE_EDGE_FLIP,listener);
        miAddNodesOnEdges = makeClickTaskMenuItem(ClickModeManager.MODE_ADD_NODES_ON_EDGES,listener);
        miAddNodesInTriFacets = makeClickTaskMenuItem(ClickModeManager.MODE_ADD_NODES_IN_FACETS,listener);
        
        // Build the sections menu items:
        miSectionInfo = makeMenuTaskMenuItem(new ShowSectionInfoMenuTask(controller),listener);
        miNewNoImageCrossSection = makeMenuTaskMenuItem(new NewNoImageSectionMenuTask(controller,true),listener);
        miNewNoImageDepthSection = makeMenuTaskMenuItem(new NewNoImageSectionMenuTask(controller,false),listener);
        miNewSnapshotSection = makeMenuTaskMenuItem(new NewSnapshotSectionMenuTask(controller),listener);
        miResetSnapshotSection = makeMenuTaskMenuItem(new ResetSnapshotSectionMenuTask(controller),listener);
        miSectionName = makeMenuTaskMenuItem(new ChangeSectionNameMenuTask(controller),listener);
        miCalibrate = makeMenuTaskMenuItem(new StartCalibrationMenuTask(controller),listener);
        miCalibrateTyped = makeMenuTaskMenuItem(new ChangeCalibrationCoordinatesMenuTask(controller),listener);
        miCopyCalibration = makeMenuTaskMenuItem(new CopyCalibrationMenuTask(controller),listener);
        miNodesAtCalibration = makeMenuTaskMenuItem(new NodesAtCalibrationMenuTask(controller),listener);
        miChangeSectionColor = makeMenuItem("Change section plotting color",listener);
        miDeleteSection = makeMenuTaskMenuItem(new DeleteSectionMenuTask(controller),listener);

        // Build the groups menu items:
        miLoadGroups = makeMenuTaskMenuItem(new LoadGroupDefinitionsMenuTask(controller),listener);
        miSaveGroups = makeMenuTaskMenuItem(new SaveGroupDefinitionsMenuTask(controller),listener);
        miNewGroup = makeMenuTaskMenuItem(new NewGroupMenuTask(controller),listener);
        miDeleteGroup = makeMenuTaskMenuItem(new DeleteGroupMenuTask(controller),listener);
        miMergeGroups = makeMenuTaskMenuItem(new MergeGroupsMenuTask(controller),listener);
        miGroupUp1 = makeMenuTaskMenuItem(new MoveGroupUp1MenuTask(controller),"Up 1",listener);
        miGroupUp2 = makeMenuTaskMenuItem(new MoveGroupUp2MenuTask(controller),"Up 2",listener);
        miGroupTop = makeMenuTaskMenuItem(new MoveGroupTopMenuTask(controller),"To top",listener);
        miGroupDown1 = makeMenuTaskMenuItem(new MoveGroupDown1MenuTask(controller),"Down 1",listener);
        miGroupDown2 = makeMenuTaskMenuItem(new MoveGroupDown2MenuTask(controller),"Down 2",listener);
        miGroupBottom = makeMenuTaskMenuItem(new MoveGroupBottomMenuTask(controller),"To bottom",listener);
        miGroupName = makeMenuTaskMenuItem(new ChangeGroupNameMenuTask(controller),"Name",listener);
        miGroupColor = makeMenuTaskMenuItem(new ChangeGroupColorMenuTask(controller),"Colour",listener);
        miGroupNodeColor = makeMenuTaskMenuItem(new ChangeGroupNodeColorMenuTask(controller),"Node colour",listener);
        miGroupFacetColor = makeMenuTaskMenuItem(new ChangeGroupFacetColorMenuTask(controller),"Facet colour",listener);
        miGroupRegionColor = makeMenuTaskMenuItem(new ChangeGroupRegionColorMenuTask(controller),"Region colour",listener);
        miSplitGroupVOI = makeMenuTaskMenuItem(new SplitGroupVOIMenuTask(controller),"By VOI",listener);
        miSplitGroupBoundary = makeMenuTaskMenuItem(new SplitGroupBoundaryMenuTask(controller),"Boundary nodes from facet definitions",listener);

        // Build the model menu items:
        miPLCInfo = makeMenuItem("Model information","Display information about the model",listener);
        miSnapToCalibration = makeMenuTaskMenuItem(new SnapToCalibrationMenuTask(controller,true,true),"calibration points",listener);
        miSnapToCalibrationHorizontal = makeMenuTaskMenuItem(new SnapToCalibrationMenuTask(controller,true,false),"calibration points horizontally",listener);
        miSnapToCalibrationVertical = makeMenuTaskMenuItem(new SnapToCalibrationMenuTask(controller,false,true),"calibration points vertically",listener);
        miSnapToVOI = makeMenuTaskMenuItem(new SnapToVOIMenuTask(controller,true,true),"VOI",listener);
        miSnapToVOIHorizontal = makeMenuTaskMenuItem(new SnapToVOIMenuTask(controller,true,false),"VOI horizontally",listener);
        miSnapToVOIVertical = makeMenuTaskMenuItem(new SnapToVOIMenuTask(controller,false,true),"VOI vertically",listener);
        miSnapToGrid = makeMenuTaskMenuItem(new SnapToGridMenuTask(controller,true,true),"grid",listener);
        miSnapToGridHorizontal = makeMenuTaskMenuItem(new SnapToGridMenuTask(controller,true,false),"grid horizontally",listener);
        miSnapToGridVertical = makeMenuTaskMenuItem(new SnapToGridMenuTask(controller,false,true),"grid vertically",listener);
        miTranslate = makeMenuTaskMenuItem(new TranslateNodesMenuTask(controller),listener);
        miScalePixels = makeMenuTaskMenuItem(new ScalePixelsMenuTask(controller),listener);
        miClearPLC = makeMenuTaskMenuItem(new ClearPLCMenuTask(controller),"All nodes, facets and regions",listener);
        miClearFacets = makeMenuTaskMenuItem(new ClearFacetsMenuTask(controller),"All facets",listener);
        miDefineVOI = makeMenuItem("Define volume of interest (VOI)",listener);
        miAddNodesVOI = makeMenuTaskMenuItem(new DefineNodesVOIMenuTask(controller),listener);
        miFindNodesIndex = makeMenuTaskMenuItem(new FindNodesIndexMenuTask(controller),"Nodes by index",listener);
        miFindFacetsIndex = makeMenuTaskMenuItem(new FindFacetsIndexMenuTask(controller),"Facets by index",listener);
        miFindNodesVOI = makeMenuTaskMenuItem(new FindNodesOutsideVOIMenuTask(controller),"Nodes outside of VOI",listener);
        miFindNodesCalibration = makeMenuTaskMenuItem(new FindNodesOutsideCalibrationMenuTask(controller),"Nodes outside calibration",listener);
        miFindBadFacets = makeMenuTaskMenuItem(new FindBadFacetsMenuTask(controller),"Bad facets",listener);
        miFindHoles = makeMenuTaskMenuItem(new FindHolesMenuTask(controller),"Holes",listener);
        miFindUnusedNodes = makeMenuTaskMenuItem(new FindUnusedNodesMenuTask(controller),"Unused nodes",listener);
        miDeleteNodeGroup = makeMenuTaskMenuItem(new DeleteNodeGroupMenuTask(controller),"Group of nodes",listener);
        miDeleteFacetGroup = makeMenuTaskMenuItem(new DeleteFacetGroupMenuTask(controller),"Group of facets",listener);
        miDeleteDisplayedNodes = makeMenuTaskMenuItem(new DeleteDisplayedNodesMenuTask(controller),"Displayed nodes",listener);
        miDeleteDisplayedFacets = makeMenuTaskMenuItem(new DeleteDisplayedFacetsMenuTask(controller),"Displayed facets",listener);
        miDeleteDuplicateNodes = makeMenuTaskMenuItem(new DeleteDuplicateNodesMenuTask(controller),"Duplicate nodes",listener);
        miMergeDuplicateNodes = makeMenuTaskMenuItem(new MergeDuplicateNodesMenuTask(controller),"Duplicate nodes via merging",listener);
        
        // Build the display menu items:
        miDefaultBackgroundColor = makeMenuItem("Default background color",listener);
        miSelectBackgroundColor = makeMenuItem("Change background color",listener);
        miCalibrationColor = makeMenuItem("Change calibration color",listener);
        miEdgeColor = makeMenuItem("Change facet edge color",listener);
        miDefineFacetEdgeColor = makeMenuItem("Change facet edge color when defining",listener);
        miPointWidth = makeMenuItem("Point size",listener);
        miLineWidth = makeMenuItem("Line width",listener);
        
        // Build the interaction menu items:
        miShiftStep2D = makeMenuItem("Shift step (2D viewer)",listener);
        miPanStep2D = makeMenuItem("Pan step (2D viewer)",listener);
        miPickingRadius = makeMenuItem("Picking/snapping distance","Change the distance used when picking and snapping nodes",listener);
        miAutoFacetFactor = makeMenuItem("Facet selection factor",listener);
        miShowConfirmationDialogs = makeMenuItem("Show all confirmation dialogs",listener);
        miHideConfirmationDialogs = makeMenuItem("Hide some confirmation dialogs",listener);
        
        // Build the 3D view menu items:
        miNodeOrigin3D = makeMenuTaskMenuItem(new StartOriginNode3DMenuTask(controller),listener);
        miClearOrigin3D = makeMenuItem("Clear 3D origin node",listener);
        miVerticalExaggeration = makeMenuItem("Vertical exaggeration","Change the vertical exaggeration in the 3D viewer",listener);
        miSaveView3D = makeMenuTaskMenuItem(new SaveView3DMenuTask(controller),listener);
        miLoadView3D = makeMenuTaskMenuItem(new LoadView3DMenuTask(controller),listener);
        
        // Build the window menu:
        miToggleToolPanel = makeMenuItem("Show/hide tool panel",listener);
        miToggleView3DPanel1 = makeMenuItem("Show/hide 3D view",listener);
        miToggleView3DPanel2 = makeMenuItem("Show/hide 3D view",listener);
        miToggleScroller = makeMenuItem("Show/hide scroll bars on 2D image panel",listener);

    }
    
    private MenuTaskMenuItem makeMenuTaskMenuItem(MenuTask task, ActionListener listener) {
        MenuTaskMenuItem mi = new MenuTaskMenuItem(task);
        mi.addActionListener(listener);
        menuTaskMenuItems.add(mi);
        return mi;
    }
    private MenuTaskMenuItem makeMenuTaskMenuItem(MenuTask task, String text, ActionListener listener) {
        MenuTaskMenuItem mi = new MenuTaskMenuItem(task,text);
        mi.addActionListener(listener);
        menuTaskMenuItems.add(mi);
        return mi;
    }
    
    private ClickTaskMenuItem makeClickTaskMenuItem(int mode, ActionListener listener) {
        ClickTask task = controller.getClickTask(mode);
        ClickTaskMenuItem mi = new ClickTaskMenuItem(task);
        mi.addActionListener(listener);
        clickTaskMenuItems.add(mi);
        return mi;
    }
    
    private JMenuItem makeMenuItem(String text, ActionListener al) {
        JMenuItem mi = new JMenuItem(text);
        mi.addActionListener(al);
        return mi;
    }
    
    private JMenuItem makeMenuItem(String text, String tip, ActionListener al) {
        JMenuItem mi = new JMenuItem(text);
        mi.setToolTipText(tip);
        mi.addActionListener(al);
        return mi;
    }
    
    private void addMenuItems() {
        
        int ndim = controller.numberOfDimensions();

        // Build the FacetModeller menu:
        JMenu mainMenu = new JMenu("FacetModeller");
        this.add(mainMenu);
        mainMenu.add(miAbout);
        mainMenu.add(miExit);
        mainMenu.add(miUndo);

        // Build the file menu:
        JMenu fileMenu = new JMenu("File");
        this.add(fileMenu);
        fileMenu.add(miLoadCrossSectionImages);
        fileMenu.add(miLoadDepthSectionImages);
        fileMenu.add(miLoadNodesAndFacets);
        fileMenu.add(miOpenSession);
        fileMenu.add(miOpenPreviousSession);
        fileMenu.add(miSaveSession);
        fileMenu.add(miSaveSessionAs);
        // Export submenu:
        JMenu exportMenu = new JMenu("Export");
        fileMenu.add(exportMenu);
        exportMenu.add(miExportPoly);
        exportMenu.add(miExportPair);
        exportMenu.add(miExportVTU);
        exportMenu.add(miExportNodes);
        exportMenu.add(miExportPolyGroup);
        exportMenu.add(miExportPairGroup);
        exportMenu.add(miExportPolyDisplayed);
        exportMenu.add(miExportPairDisplayed);
        exportMenu.add(miExportFacets);
        exportMenu.add(miExportRegions);
        exportMenu.add(miExportAll);

        // Build the mode menu:
        JMenu modeMenu = new JMenu("Click Mode");
        this.add(modeMenu);
        modeMenu.add(miNullMode);
        modeMenu.add(miSetOrigin2DMode);
        modeMenu.add(miInfoMode);
        modeMenu.add(miAddNodes);
        modeMenu.add(miDeleteNodes);
        modeMenu.add(miMoveNodes);
        modeMenu.add(miMergeNodes);
        modeMenu.add(miChangeNodes);
        modeMenu.add(miChangeNodesSection);
        modeMenu.add(miDefinePolyFacets);
        if (ndim==3) {
            modeMenu.add(miDefinePolyFacetsTri);
            modeMenu.add(miDefineTriFacets);
        }
        if (ndim==2) { modeMenu.add(miDefineLineFacets); }
        modeMenu.add(miDeleteFacets);
        modeMenu.add(miChangeFacets);
        modeMenu.add(miReverseFacets);
        if (ndim==3) {
            modeMenu.add(miEdgeFlip);
            modeMenu.add(miAddNodesOnEdges);
            modeMenu.add(miAddNodesInTriFacets);
        }
        modeMenu.add(miAddRegions);
        modeMenu.add(miDeleteRegions);

        // Build the sections menu:
        JMenu sectionsMenu = new JMenu("Sections");
        this.add(sectionsMenu);
        sectionsMenu.add(miSectionInfo);
        sectionsMenu.add(miNewNoImageCrossSection);
        sectionsMenu.add(miNewNoImageDepthSection);
        if (ndim==3) {
            sectionsMenu.add(miNewSnapshotSection);
            sectionsMenu.add(miResetSnapshotSection);
        }
        sectionsMenu.add(miSectionName);
        sectionsMenu.add(miCalibrate);
        sectionsMenu.add(miCalibrateTyped);
        if (ndim==3) { sectionsMenu.add(miCopyCalibration); }
        sectionsMenu.add(miNodesAtCalibration);
        sectionsMenu.add(miChangeSectionColor);
        if (ndim==3) { sectionsMenu.add(miDeleteSection); }

        // Build the groups menu:
        JMenu groupsMenu = new JMenu("Groups");
        this.add(groupsMenu);
        groupsMenu.add(miLoadGroups);
        groupsMenu.add(miSaveGroups);
        groupsMenu.add(miNewGroup);
        groupsMenu.add(miDeleteGroup);
        groupsMenu.add(miMergeGroups);
        // Move submenu:
        JMenu moveGroupMenu = new JMenu("Move current group");
        groupsMenu.add(moveGroupMenu);
        moveGroupMenu.add(miGroupTop);
        moveGroupMenu.add(miGroupUp2);
        moveGroupMenu.add(miGroupUp1);
        moveGroupMenu.add(miGroupDown1);
        moveGroupMenu.add(miGroupDown2);
        moveGroupMenu.add(miGroupBottom);
        // Change submenu:
        JMenu changeGroupMenu = new JMenu("Change current group");
        groupsMenu.add(changeGroupMenu);
        changeGroupMenu.add(miGroupName);
        changeGroupMenu.add(miGroupColor);
        changeGroupMenu.add(miGroupNodeColor);
        changeGroupMenu.add(miGroupFacetColor);
        changeGroupMenu.add(miGroupRegionColor);
        // Split submenu:
        JMenu splitGroupMenu = new JMenu("Split current group");
        groupsMenu.add(splitGroupMenu);
        splitGroupMenu.add(miSplitGroupVOI);
        splitGroupMenu.add(miSplitGroupBoundary);

        // Build the model menu:
        JMenu modelMenu = new JMenu("Model");
        this.add(modelMenu);
        modelMenu.add(miPLCInfo);
        // Snap submenu:
        JMenu snapMenu = new JMenu("Snap nodes to");
        modelMenu.add(snapMenu);
        snapMenu.add(miSnapToCalibration);
        snapMenu.add(miSnapToCalibrationVertical);
        snapMenu.add(miSnapToCalibrationHorizontal);
        if (ndim==3) {
            snapMenu.add(miSnapToVOI);
            snapMenu.add(miSnapToVOIVertical);
            snapMenu.add(miSnapToVOIHorizontal);
        }
        snapMenu.add(miSnapToGrid);
        snapMenu.add(miSnapToGridVertical);
        snapMenu.add(miSnapToGridHorizontal);
        if (ndim==3) {
            modelMenu.add(miTranslate);
        }
        modelMenu.add(miScalePixels);
        if (ndim==3) {
            modelMenu.add(miDefineVOI);
        }
        modelMenu.add(miAddNodesVOI);
        // Find submenu:
        JMenu findMenu = new JMenu("Find");
        modelMenu.add(findMenu);
        findMenu.add(miFindNodesIndex);
        findMenu.add(miFindFacetsIndex);
        if (ndim==3) { findMenu.add(miFindNodesVOI); }
        findMenu.add(miFindNodesCalibration);
        findMenu.add(miFindUnusedNodes);
        findMenu.add(miFindBadFacets);
        findMenu.add(miFindHoles);
        // Delete submenu:
        JMenu deleteMenu = new JMenu("Delete");
        modelMenu.add(deleteMenu);
        deleteMenu.add(miClearPLC);
        deleteMenu.add(miClearFacets);
        deleteMenu.add(miDeleteNodeGroup);
        deleteMenu.add(miDeleteFacetGroup);
        deleteMenu.add(miDeleteDisplayedNodes);
        deleteMenu.add(miDeleteDisplayedFacets);
        deleteMenu.add(miDeleteDuplicateNodes);
        deleteMenu.add(miMergeDuplicateNodes);
        
        // Build the display menu:
        JMenu displayMenu = new JMenu("Display");
        this.add(displayMenu);
        displayMenu.add(miDefaultBackgroundColor);
        displayMenu.add(miSelectBackgroundColor);
        displayMenu.add(miCalibrationColor);
        if (ndim==3) { displayMenu.add(miEdgeColor); }
        displayMenu.add(miDefineFacetEdgeColor);
        displayMenu.add(miPointWidth);
        displayMenu.add(miLineWidth);
        
        // Build the interaction menu:
        JMenu interactionMenu = new JMenu("Interaction");
        this.add(interactionMenu);
        if (ndim==3) {
            interactionMenu.add(miShiftStep2D);
            interactionMenu.add(miPanStep2D);
        }
        interactionMenu.add(miPickingRadius);
        interactionMenu.add(miAutoFacetFactor);
        interactionMenu.add(miShowConfirmationDialogs);
        interactionMenu.add(miHideConfirmationDialogs);
        
        // Build the 3D view menu:
        if ( ndim==3 ) {
            JMenu view3DMenu = new JMenu("3D View");
            this.add(view3DMenu);
            view3DMenu.add(miNodeOrigin3D);
            view3DMenu.add(miClearOrigin3D);
            view3DMenu.add(miToggleView3DPanel1);
            view3DMenu.add(miVerticalExaggeration);
            view3DMenu.add(miSaveView3D);
            view3DMenu.add(miLoadView3D);
        }
        
        // Build the window menu:
        JMenu windowMenu = new JMenu("Window");
        this.add(windowMenu);
        windowMenu.add(miToggleToolPanel);
        if (ndim==3) { windowMenu.add(miToggleView3DPanel2); }
        windowMenu.add(miToggleScroller);

    }

    /** Action listener for menu items. */
    private class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            // Deal with the MenuTaskMenuItems:
            if (src instanceof MenuTaskMenuItem) {
                MenuTaskMenuItem tmi = (MenuTaskMenuItem) src; // cast
                tmi.execute();
                return;
            }
            // Deal with the other menu items:
            if (src == miExit) { controller.exit(); }
            else if (src == miAbout) { controller.about(); }
            else if (src == miOpenSession) { controller.openSession(false); }
            else if (src == miOpenPreviousSession) { controller.openSession(true); }
            else if (src == miExportPoly) { controller.exportPoly(FileIOManager.EXPORT_ALL); }
            else if (src == miExportPair) { controller.exportPair(FileIOManager.EXPORT_ALL); }
            else if (src == miExportVTU) { controller.exportVTU(); }
            else if (src == miExportNodes) { controller.exportNodes(); }
            else if (src == miExportPolyGroup) { controller.exportPoly(FileIOManager.EXPORT_CURRENT); }
            else if (src == miExportPairGroup) { controller.exportPair(FileIOManager.EXPORT_CURRENT); }
            else if (src == miExportPolyDisplayed) { controller.exportPoly(FileIOManager.EXPORT_DISPLAYED); }
            else if (src == miExportPairDisplayed) { controller.exportPair(FileIOManager.EXPORT_DISPLAYED); }
            else if (src == miExportFacets) { controller.exportFacets(); }
            else if (src == miExportRegions) { controller.exportRegions(); }
            else if (src == miExportAll) { controller.exportAll(); }
            else if (src == miPLCInfo) { controller.showPLCInfo(); }
            else if (src == miClearOrigin3D) { controller.clearOriginNode3D(); }
            else if (src == miDefineVOI) { controller.defineVOI(); }
            else if (src == miNullMode) { controller.setClickMode(ClickModeManager.MODE_NULL); }
            else if (src == miSetOrigin2DMode) { controller.setClickMode(ClickModeManager.MODE_ORIGIN_2D); }
            else if (src == miInfoMode) { controller.setClickMode(ClickModeManager.MODE_INFO); }
            else if (src == miAddNodes) { controller.setClickMode(ClickModeManager.MODE_ADD_NODES); }
            else if (src == miDeleteNodes) { controller.setClickMode(ClickModeManager.MODE_DELETE_NODES); }
            else if (src == miMoveNodes) { controller.setClickMode(ClickModeManager.MODE_MOVE_NODES); }
            else if (src == miMergeNodes) { controller.setClickMode(ClickModeManager.MODE_MERGE_NODES); }
            else if (src == miChangeNodes) { controller.setClickMode(ClickModeManager.MODE_CHANGE_NODES_GROUP); }
            else if (src == miChangeNodesSection) { controller.setClickMode(ClickModeManager.MODE_CHANGE_NODES_SECTION); }
            else if (src == miAddRegions) { controller.setClickMode(ClickModeManager.MODE_ADD_REGIONS); }
            else if (src == miDeleteRegions) { controller.setClickMode(ClickModeManager.MODE_DELETE_REGIONS); }
            else if (src == miDefinePolyFacets) { controller.setClickMode(ClickModeManager.MODE_DEFINE_POLY_FACETS); }
            else if (src == miDefinePolyFacetsTri) { controller.setClickMode(ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI); }
            else if (src == miDefineTriFacets) { controller.setClickMode(ClickModeManager.MODE_DEFINE_TRI_FACETS); }
            else if (src == miDefineLineFacets) { controller.setClickMode(ClickModeManager.MODE_DEFINE_LINE_FACETS); }
            else if (src == miDeleteFacets) { controller.setClickMode(ClickModeManager.MODE_DELETE_FACETS); }
            else if (src == miChangeFacets) { controller.setClickMode(ClickModeManager.MODE_CHANGE_FACETS_GROUP); }
            else if (src == miReverseFacets) { controller.setClickMode(ClickModeManager.MODE_REVERSE_FACETS); }
            else if (src == miEdgeFlip) { controller.setClickMode(ClickModeManager.MODE_EDGE_FLIP); }
            else if (src == miAddNodesOnEdges) { controller.setClickMode(ClickModeManager.MODE_ADD_NODES_ON_EDGES); }
            else if (src == miAddNodesInTriFacets) { controller.setClickMode(ClickModeManager.MODE_ADD_NODES_IN_FACETS); }
            else if (src == miDefaultBackgroundColor) { controller.setBackgroundColorDefault(); }
            else if (src == miSelectBackgroundColor) { controller.selectBackgroundColor(); }
            else if (src == miChangeSectionColor) { controller.selectSectionColor(); }
            else if (src == miCalibrationColor) { controller.selectCalibrationColor(); }
            else if (src == miEdgeColor) { controller.selectEdgeColor(); }
            else if (src == miDefineFacetEdgeColor) { controller.selectDefineFacetEdgeColor(); }
            else if (src == miPointWidth) { controller.selectPointWidth(); }
            else if (src == miLineWidth) { controller.selectLineWidth(); }
            else if (src == miShiftStep2D) { controller.selectShiftStep2D(); }
            else if (src == miPanStep2D) { controller.selectPanStep2D(); }
            else if (src == miPickingRadius) { controller.selectPickingRadius(); }
            else if (src == miAutoFacetFactor) { controller.selectAutoFacetFactor(); }
            else if (src == miShowConfirmationDialogs) { controller.setShowConfirmationDialogs(true); }
            else if (src == miHideConfirmationDialogs) { controller.setShowConfirmationDialogs(false); }
            else if (src == miToggleToolPanel) { controller.toggleToolPanel(); }
            else if (src == miToggleView3DPanel1) { controller.toggleView3DPanel(); }
            else if (src == miToggleView3DPanel2) { controller.toggleView3DPanel(); }
            else if (src == miToggleScroller) { controller.toggleScroller(); }
            else if (src == miVerticalExaggeration) { controller.selectVerticalExaggeration(); }
        }
    }

    /** Enables or disables some menu items. */
    public void checkItemsEnabled() {
        
        // Deal with the MenuTaskMenuItems:
        for (int i=0 ; i<menuTaskMenuItems.size() ; i++) {
            menuTaskMenuItems.get(i).checkEnabled();
        }
        
        // Deal with the ClickTaskMenuItems:
        for (int i=0 ; i<clickTaskMenuItems.size() ; i++) {
            clickTaskMenuItems.get(i).checkEnabled();
        }
        
        // Get some information from the controller:
        int ndim = controller.numberOfDimensions();
        Section currentSection = controller.getSelectedCurrentSection();
        ModelManager model = controller.getModelManager();
        boolean showConfirmationDialogs = controller.getShowConfirmationDialogs();
        boolean allSectionsCalibrated = controller.areAllSectionsCalibrated();
        
        // Create some booleans based on the information above:
        boolean is3D = ( ndim == 3 );
        boolean hasCurrentSection = ( currentSection != null );
        boolean hasOriginNode3D = ( controller.getOriginNode3D() != null );
        boolean hasNodes = model.hasNodes();
        boolean hasFacets = model.hasFacets();
        boolean hasRegions = model.hasRegions();
        boolean hasPLC = ( hasNodes && hasFacets );
        boolean hasPLCandAllCalibrated = ( hasPLC && allSectionsCalibrated );
        boolean hasNodesandAllCalibrated = ( hasNodes && allSectionsCalibrated );
        
        // FacetModeller menu items:
        miAbout.setEnabled(true);
        miExit.setEnabled(true);
        
        // File menu items:
        miOpenSession.setEnabled(true);
        miOpenPreviousSession.setEnabled(true);
        miExportPoly.setEnabled(hasPLCandAllCalibrated);
        miExportPair.setEnabled(hasPLCandAllCalibrated);
        miExportVTU.setEnabled(hasPLCandAllCalibrated);
        miExportNodes.setEnabled(hasNodes && allSectionsCalibrated);
        miExportPolyGroup.setEnabled(hasNodesandAllCalibrated);
        miExportPairGroup.setEnabled(hasNodesandAllCalibrated);
        miExportPolyDisplayed.setEnabled(hasNodesandAllCalibrated);
        miExportPairDisplayed.setEnabled(hasNodesandAllCalibrated);
        miExportFacets.setEnabled(hasFacets && allSectionsCalibrated);
        miExportRegions.setEnabled(hasRegions && allSectionsCalibrated);
        miExportAll.setEnabled(hasPLCandAllCalibrated);

        // Sections menu items:
        miChangeSectionColor.setEnabled( hasCurrentSection );

        // Model menu items:
        miPLCInfo.setEnabled(true);
        miDefineVOI.setEnabled(is3D);
        
        // Display menu items:
        miDefaultBackgroundColor.setEnabled(true);
        miSelectBackgroundColor.setEnabled(true);
        miCalibrationColor.setEnabled(true);
        miEdgeColor.setEnabled(true);
        miDefineFacetEdgeColor.setEnabled(true);
        miPointWidth.setEnabled(true);
        miLineWidth.setEnabled(true);
        
        // Interaction menu items:
        miShiftStep2D.setEnabled(is3D);
        miPanStep2D.setEnabled(is3D);
        miPickingRadius.setEnabled(true);
        miAutoFacetFactor.setEnabled(true);
        miShowConfirmationDialogs.setEnabled(!showConfirmationDialogs);
        miHideConfirmationDialogs.setEnabled(showConfirmationDialogs);
        
        // 3D view menu items:
        miClearOrigin3D.setEnabled( hasOriginNode3D && is3D );
        miVerticalExaggeration.setEnabled(is3D);
        
        // Window menu items:
        miToggleToolPanel.setEnabled(true);
        miToggleView3DPanel1.setEnabled(is3D);
        miToggleView3DPanel2.setEnabled(is3D);
        miToggleScroller.setEnabled(true);

    }
    
    // Methods required for when the tasks associated with the menu items need to be fired from outside of this class:
    public void startCalibration() { miCalibrate.execute(); }

}
