package facetmodeller;

import facetmodeller.clicktasks.*;
import facetmodeller.gui.ClickModeManager;
import geometry.MyPoint2D;

/** Manages events caused by the user interacting with the mouse on the 2D viewer panel.
 * @author Peter
 */
public final class MouseInteractionManager {
    
    private final FacetModeller controller;
    
    // Objects used to perform processing for clicks:
    private final ClickTask ignore, info, defineNode, deleteNode, moveNode, mergeNodes, duplicateNode,
            changeNodeGroup, changeNodeSection, changeNodeCoords, markNodeToggle, markNodeTrue, markNodeFalse,
            definePolyFacet, definePolyFacetTri, defineTriFacet, defineLineFacet,
            deleteFacet, changeFacetGroup, reverseFacet, flipEdge,  markFacetToggle, markFacetTrue, markFacetFalse,
            defineRegion, deleteRegion, propagateNormals,
            defineNodeOnEdge, defineNodeInFacet, originPoint2D, originNode3D, calibrate;
    
    public MouseInteractionManager(FacetModeller con) {
        controller = con;
        ignore = new IgnoreClickTask(con);
        info = new InfoClickTask(con);
        defineNode = new DefineNodeClickTask(con);
        deleteNode = new DeleteNodeClickTask(con);
        moveNode = new MoveNodeClickTask(con);
        mergeNodes = new MergeNodesClickTask(con);
        duplicateNode = new DuplicateNodeClickTask(con);
        markNodeToggle = new MarkNodeClickTask(con,0);
        markNodeTrue = new MarkNodeClickTask(con,1);
        markNodeFalse = new MarkNodeClickTask(con,-1);
        changeNodeGroup = new ChangeNodeGroupClickTask(con);
        changeNodeSection = new ChangeNodeSectionClickTask(con);
        changeNodeCoords = new ChangeNodeCoordsClickTask(con);
        definePolyFacet = new DefinePolyFacetClickTask(con,false);
        definePolyFacetTri = new DefinePolyFacetClickTask(con,true);
        defineTriFacet = new DefineTriFacetClickTask(con);
        defineLineFacet = new DefineLineFacetClickTask(con);
        deleteFacet = new DeleteFacetClickTask(con);
        changeFacetGroup = new ChangeFacetGroupClickTask(con);
        reverseFacet = new ReverseFacetClickTask(con);
        flipEdge = new FlipEdgeClickTask(con);
        markFacetToggle = new MarkFacetClickTask(con,0);
        markFacetTrue = new MarkFacetClickTask(con,1);
        markFacetFalse = new MarkFacetClickTask(con,-1);
        defineRegion = new DefineRegionClickTask(con);
        deleteRegion = new DeleteRegionClickTask(con);
        defineNodeOnEdge = new DefineNodeOnEdgeClickTask(con);
        defineNodeInFacet = new DefineNodeInFacetClickTask(con);
        originPoint2D = new OriginPoint2DClickTask(con);
        originNode3D = new OriginNode3DClickTask(con);
        calibrate = new CalibrateClickTask(con);
        propagateNormals = new PropagateNormalClickTask(con);
    }
    
    // Returns the ClickTask object corresponding to the supplied mode.
    public ClickTask getClickTask(int mode){
        // Check the mode:
        switch (mode) {
            case ClickModeManager.MODE_NULL:
                return ignore;
            case ClickModeManager.MODE_INFO:
                return info;
            case ClickModeManager.MODE_ORIGIN_2D:
                return originPoint2D;
            case ClickModeManager.MODE_CALIBRATE:
                return calibrate;
            case ClickModeManager.MODE_ORIGIN_NODE_3D:
                return originNode3D;
            case ClickModeManager.MODE_ADD_NODES:
                return defineNode;
            case ClickModeManager.MODE_DELETE_NODES:
                return deleteNode;
            case ClickModeManager.MODE_MOVE_NODES:
                return moveNode;
            case ClickModeManager.MODE_MERGE_NODES:
                return mergeNodes;
            case ClickModeManager.MODE_DUPLICATE_NODES:
                return duplicateNode;
            case ClickModeManager.MODE_CHANGE_NODES_GROUP:
                return changeNodeGroup;
            case ClickModeManager.MODE_CHANGE_NODES_SECTION:
                return changeNodeSection;
            case ClickModeManager.MODE_CHANGE_NODES_COORDS:
                return changeNodeCoords;
            case ClickModeManager.MODE_MARK_NODES_TOGGLE:
                return markNodeToggle;
            case ClickModeManager.MODE_MARK_NODES_TRUE:
                return markNodeTrue;
            case ClickModeManager.MODE_MARK_NODES_FALSE:
                return markNodeFalse;
            case ClickModeManager.MODE_CHANGE_FACETS_GROUP:
                return changeFacetGroup;
            case ClickModeManager.MODE_DEFINE_POLY_FACETS:
                return definePolyFacet;
            case ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI:
                return definePolyFacetTri;
            case ClickModeManager.MODE_DEFINE_TRI_FACETS:
                return defineTriFacet;
            case ClickModeManager.MODE_DEFINE_LINE_FACETS:
                return defineLineFacet;
            case ClickModeManager.MODE_DELETE_FACETS:
                return deleteFacet;
            case ClickModeManager.MODE_REVERSE_FACETS:
                return reverseFacet;
            case ClickModeManager.MODE_EDGE_FLIP:
                return flipEdge;
//            case ClickModeManager.MODE_SPLIT_TRI_FACETS:
            case ClickModeManager.MODE_MARK_FACETS_TOGGLE:
                return markFacetToggle;
            case ClickModeManager.MODE_MARK_FACETS_TRUE:
                return markFacetTrue;
            case ClickModeManager.MODE_MARK_FACETS_FALSE:
                return markFacetFalse;
            case ClickModeManager.MODE_ADD_NODES_ON_EDGES:
                return defineNodeOnEdge;
            case ClickModeManager.MODE_ADD_NODES_IN_FACETS:
                return defineNodeInFacet;
            case ClickModeManager.MODE_ADD_REGIONS:
                return defineRegion;
            case ClickModeManager.MODE_DELETE_REGIONS:
                return deleteRegion;
            case ClickModeManager.MODE_PROPAGATE_NORMALS:
                return propagateNormals;
            default:
                return null;
        }
    }

    public void mouseClick(MyPoint2D p) {
        // Get the ClickTask object corresponding to the current click mode:
        ClickTask task = getClickTask(controller.getClickMode());
        // Execute the task:
        if (task!=null) { task.mouseClick(p); }
    }
    
    public void mouseDrag(MyPoint2D p) {
        // Get the ClickTask object corresponding to the current click mode:
        ClickTask task = getClickTask( controller.getClickMode() );
        // Execute the task:
        if (task!=null) { task.mouseDrag(p); }
        // Update the cursor bar:
        controller.updateCursorBar(p);
    }

    public void mouseMove(MyPoint2D p) {
        // Get the ClickTask object corresponding to the current click mode:
        ClickTask task = getClickTask( controller.getClickMode() );
        // Execute the task:
        if (task!=null) { task.mouseMove(p); }
//            case ClickModeManager.MODE_SPLIT_TRI_FACETS:
//                // Calculate the closest facet centroid to the cursor position:
//                calculateClosestFacet(p);
//                // Redraw if the closestFacet is not null:
//                //if (closestFacet!=null) { redraw(); }
//                redraw();
//                // Update the cursor bar:
//                updateCursorBar(p);
//                break;
    }
    
}
