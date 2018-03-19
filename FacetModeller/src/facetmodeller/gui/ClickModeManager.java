package facetmodeller.gui;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.clicktasks.ClickTaskUtil;
import facetmodeller.groups.Group;
import javax.swing.JComboBox;

/** Manages the click mode and related information.
 * @author Peter
 */
public final class ClickModeManager {

    // Mouse click modes:
    // I'm not sure about this but probably best not to change the values here.
    // IF A NEW CLICK MODE IS ADDED THEN YOU HAVE TO ADD IT IN THE CONTROLLER AND IN METHOD mode2ind.
    public static final int MODE_NULL                   =  0;
    public static final int MODE_INFO                   =  2;
    public static final int MODE_ORIGIN_2D              =  4;
    public static final int MODE_CALIBRATE              = 10;
    public static final int MODE_ADD_NODES              = 20;
    public static final int MODE_ADD_NODES_ON_EDGES     = 21;
    public static final int MODE_ADD_NODES_IN_FACETS    = 22;
    public static final int MODE_DELETE_NODES           = 24;
    public static final int MODE_MOVE_NODES             = 26;
    public static final int MODE_MERGE_NODES            = 27;
    public static final int MODE_DUPLICATE_NODES        = 23;
    public static final int MODE_CHANGE_NODES_GROUP     = 28;
    public static final int MODE_CHANGE_NODES_SECTION   = 29;
    public static final int MODE_CHANGE_NODES_COORDS    = 25;
    public static final int MODE_DEFINE_POLY_FACETS     = 30;
    public static final int MODE_DEFINE_POLY_FACETS_TRI = 31;
    public static final int MODE_DEFINE_TRI_FACETS      = 32;
    public static final int MODE_DEFINE_LINE_FACETS     = 33;
    public static final int MODE_DELETE_FACETS          = 34;
    public static final int MODE_CHANGE_FACETS_GROUP    = 36;
    public static final int MODE_REVERSE_FACETS         = 37;
    public static final int MODE_EDGE_FLIP              = 38;
    //public static final int MODE_SPLIT_TRI_FACETS      = 39;
    public static final int MODE_ADD_REGIONS            = 40;
    public static final int MODE_DELETE_REGIONS         = 42;
    public static final int MODE_ORIGIN_NODE_3D         = 50;
    
    private final FacetModeller controller;
    private int mode = MODE_NULL;
    private final JComboBox<String> clickModeSelector;
    
    public ClickModeManager(FacetModeller con) {
        controller = con;
        // Create the click mode pull-down menu:
        // IF A NEW CLICK MODE IS ADDED THEN YOU HAVE TO ADD IT HERE AND IN METHOD mode2ind.
        // ALSO SEE ClickTaskUtil AND MouseInteractionManager.
        String[] clickModelStrings = new String[24];
        clickModelStrings[0] = ClickTaskUtil.IGNORE_TEXT;
        clickModelStrings[1] = ClickTaskUtil.INFO_TEXT;
        clickModelStrings[2] = ClickTaskUtil.ORIGIN_POINT_2D_TEXT;
        clickModelStrings[3] = ClickTaskUtil.ORIGIN_NODE_3D_TEXT;
        clickModelStrings[4] = ClickTaskUtil.CALIBRATE_TEXT;
        clickModelStrings[5] = ClickTaskUtil.DEFINE_NODE_TEXT;
        clickModelStrings[6] = ClickTaskUtil.DELETE_NODE_TEXT;
        clickModelStrings[7] = ClickTaskUtil.MOVE_NODE_TEXT;
        clickModelStrings[8] = ClickTaskUtil.MERGE_NODE_TEXT;
        clickModelStrings[9] = ClickTaskUtil.DUPLICATE_NODE_TEXT;
        clickModelStrings[10] = ClickTaskUtil.CHANGE_NODE_GROUP_TEXT;
        clickModelStrings[11] = ClickTaskUtil.CHANGE_NODE_SECTION_TEXT;
        clickModelStrings[12] = ClickTaskUtil.CHANGE_NODE_COORDS_TEXT;
        clickModelStrings[13] = ClickTaskUtil.DEFINE_POLY_FACET_TEXT;
        clickModelStrings[14] = ClickTaskUtil.DEFINE_TRI_FACET_TEXT;
        clickModelStrings[15] = ClickTaskUtil.DEFINE_LINE_FACET_TEXT;
        clickModelStrings[16] = ClickTaskUtil.DELETE_FACET_TEXT;
        clickModelStrings[17] = ClickTaskUtil.CHANGE_FACET_GROUP_TEXT;
        clickModelStrings[18] = ClickTaskUtil.REVERSE_FACET_TEXT;
        clickModelStrings[19] = ClickTaskUtil.FLIP_EDGE_TEXT;
        //clickModelStrings[20] = "Split triangular facet into 3";
        clickModelStrings[20] = ClickTaskUtil.DEFINE_NODE_ON_EDGE_TITLE;
        clickModelStrings[21] = ClickTaskUtil.DEFINE_NODE_IN_FACET_TITLE;
        clickModelStrings[22] = ClickTaskUtil.DEFINE_REGION_TEXT;
        clickModelStrings[23] = ClickTaskUtil.DELETE_REGION_TEXT;
        clickModeSelector = new JComboBox<>(clickModelStrings);
        clickModeSelector.setSelectedIndex(0); // null mode
        //clickModeSelector.addActionListener(actionListener);
        clickModeSelector.setEnabled(false);
    }
    
    private int mode2ind(int mode) {
        // Convert click mode integer to clickModeSelector index:
        // IF A NEW CLICK MODE IS ADDED THEN YOU HAVE TO ADD IT HERE AND IN THE CONSTRUCTOR
        switch (mode) {
            case MODE_NULL:                   return 0;
            case MODE_INFO:                   return 1;
            case MODE_ORIGIN_2D:              return 2;
            case MODE_ORIGIN_NODE_3D:         return 3;
            case MODE_CALIBRATE:              return 4;
            case MODE_ADD_NODES:              return 5;
            case MODE_DELETE_NODES:           return 6;
            case MODE_MOVE_NODES:             return 7;
            case MODE_MERGE_NODES:            return 8;
            case MODE_DUPLICATE_NODES:        return 9;
            case MODE_CHANGE_NODES_GROUP:     return 10;
            case MODE_CHANGE_NODES_SECTION:   return 11;
            case MODE_CHANGE_NODES_COORDS:    return 12;
            case MODE_DEFINE_POLY_FACETS:     return 13; // NOTE THE SAME RETURN VALUE AS DIRECTLY BELOW
            case MODE_DEFINE_POLY_FACETS_TRI: return 13; // NOTE THE SAME RETURN VALUE AS DIRECTLY ABOVE
            case MODE_DEFINE_TRI_FACETS:      return 14;
            case MODE_DEFINE_LINE_FACETS:     return 15;
            case MODE_DELETE_FACETS:          return 16;
            case MODE_CHANGE_FACETS_GROUP:    return 17;
            case MODE_REVERSE_FACETS:         return 18;
            case MODE_EDGE_FLIP:              return 19;
            //case MODE_SPLIT_TRI_FACETS:    return 20;
            case MODE_ADD_NODES_ON_EDGES:     return 20;
            case MODE_ADD_NODES_IN_FACETS:    return 21;
            case MODE_ADD_REGIONS:            return 22;
            case MODE_DELETE_REGIONS:         return 23;
            default: return MODE_NULL;
                
        }
    }
    
    public JComboBox<String> getClickModelSelector() { return clickModeSelector; }
    
    public int getClickMode() { return mode; }
    
    public void setClickMode(int m) {

        // Check if the mode is changing:
        if (mode==m) { return; }

        // Clear all temporary items that may be drawn:
        controller.clearAllTemporaryOverlays();
        controller.unlock();

        // If the user changes the mode in the middle of calibrating then clear the calibration:
        if (mode==ClickModeManager.MODE_CALIBRATE && !controller.getSelectedCurrentSection().isCalibrated() ) {
            controller.getSelectedCurrentSection().clearCalibration();
        }
        
        // If adding new nodes or facets then make sure that the current group is selected for painting:
        Group g = controller.getSelectedCurrentGroup();
        if ( m==ClickModeManager.MODE_ADD_NODES
                || m==ClickModeManager.MODE_CHANGE_NODES_GROUP
                || m==ClickModeManager.MODE_ADD_NODES_IN_FACETS
                || m==ClickModeManager.MODE_ADD_NODES_ON_EDGES) {
            if (!controller.isSelectedNodeGroup(g)) {
                int response = Dialogs.question(controller,"Would like the current node group to be selected for drawing?","Warning");
                if (response==Dialogs.CANCEL_OPTION) { return; }
                if (response==Dialogs.YES_OPTION) {
                    controller.addToNodeGroupSelection(g);
                }
            }
        } else if ( m==ClickModeManager.MODE_DEFINE_POLY_FACETS
                || m==ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI
                || m==ClickModeManager.MODE_DEFINE_TRI_FACETS
                || m==ClickModeManager.MODE_DEFINE_LINE_FACETS
                || m==ClickModeManager.MODE_CHANGE_FACETS_GROUP
                || m==ClickModeManager.MODE_EDGE_FLIP ) { // || m==MODE_SPLIT_TRI_FACETS ) {
            if (!controller.isSelectedFacetGroup(g)) {
                int response = Dialogs.question(controller,"Would like the current facet group to be selected for drawing?","Warning");
                if (response==Dialogs.CANCEL_OPTION) { return; }
                if (response==Dialogs.YES_OPTION) {
                    controller.addToFacetGroupSelection(g);
                }
            }
            
        }
        
        // Set the mode:
        mode = m;
        
        // Change the selected item in the combo box:
        clickModeSelector.setSelectedIndex(mode2ind(mode));
        
        // Set the background colour of the toolbar mode button that is now active:
        controller.markToolBarButtonBackground(m);
        
        // Enable or disable menu items:
        controller.checkItemsEnabled();

        // Redraw the current section:
        controller.redraw();

    }
    
}
