package facetmodeller.gui;

import gui.MenuTaskButton;
import facetmodeller.FacetModeller;
import facetmodeller.clicktasks.ClickTask;
import tasks.MenuTask;
import facetmodeller.menutasks.ResetSnapshotSectionMenuTask;
import facetmodeller.menutasks.SaveSessionMenuTask;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/** The tool bar for the click modes and a couple other things.
 * Icons obtained from https://www.iconfinder.com and then edited.
 * @author Peter
 */
public final class ClickModeToolBar extends JToolBar {
    private static final long serialVersionUID = 1L;

    // FacetModeller JFrame that the menu bar is associated with:
    private final FacetModeller controller;
    
    // The MenuTaskButtons:
    private final MenuTaskButton buttonResetSnapshot;
    
    // The ClickTaskButtons:
    private final ClickTaskButton buttonModeInfo, buttonModeSetOrigin2D, buttonModeOriginNode3D, //buttonModeCalibrate,
        buttonModeAddNodes, buttonModeAddNodesInTriFacets, buttonModeDeleteNodes, buttonModeMoveNodes, buttonModeMergeNodes, buttonModeChangeNodes, buttonModeAddNodesOnEdges,
        buttonModeDefinePolyFacets, buttonModeDefinePolyFacetsTri, buttonModeDefineTriFacets, buttonModeDefineLineFacets, buttonModeDeleteFacets, buttonModeChangeFacets,
        buttonModeReverseFacets, buttonModeEdgeFlip;
        
    private final ArrayList<MenuTaskButton> taskButtons = new ArrayList<>();
    private final ArrayList<ClickTaskButton> clickTaskButtons = new ArrayList<>();

    /** Makes the tool bar.
     * @param con FacetModeller window (JFrame extension) to place the menu on.
     * @param ndim Number of dimensions.
     */
    public ClickModeToolBar(FacetModeller con, int ndim) {
        
        super();
        controller = con;
        setFloatable(false);
        
        // Make and add all the buttons:
        MyActionListener listener = new MyActionListener();
        makeMenuTaskButton(new SaveSessionMenuTask(controller,false),"saveSession","SS",listener);
        buttonResetSnapshot = makeMenuTaskButton(new ResetSnapshotSectionMenuTask(controller),"resetSnapshot","RS",listener);
        makeClickTaskButton(ClickModeManager.MODE_NULL,"ignore","X",listener);
        buttonModeInfo = makeClickTaskButton(ClickModeManager.MODE_INFO,"info","I",listener);
        buttonModeSetOrigin2D = makeClickTaskButton(ClickModeManager.MODE_ORIGIN_2D,"setOrigin2D","2D",listener);
        buttonModeOriginNode3D = makeClickTaskButton(ClickModeManager.MODE_ORIGIN_NODE_3D,"originNode3D","3D",listener);
        buttonModeAddNodes = makeClickTaskButton(ClickModeManager.MODE_ADD_NODES,"addNodes","AN",listener);
        buttonModeDeleteNodes = makeClickTaskButton(ClickModeManager.MODE_DELETE_NODES,"deleteNodes","DN",listener);
        buttonModeMoveNodes = makeClickTaskButton(ClickModeManager.MODE_MOVE_NODES,"moveNodes","MN",listener);
        buttonModeMergeNodes = makeClickTaskButton(ClickModeManager.MODE_MERGE_NODES,"mergeNodes","RN",listener);
        buttonModeChangeNodes = makeClickTaskButton(ClickModeManager.MODE_CHANGE_NODES_GROUP,"changeNodes","NG",listener);
        buttonModeDefinePolyFacets = makeClickTaskButton(ClickModeManager.MODE_DEFINE_POLY_FACETS,"definePolyFacets","PF",listener);
        buttonModeDefinePolyFacetsTri = makeClickTaskButton(ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI,"definePolyFacetsTri","PT",listener);
        buttonModeDefineTriFacets = makeClickTaskButton(ClickModeManager.MODE_DEFINE_TRI_FACETS,"defineTriFacets","TF",listener);
        buttonModeDefineLineFacets = makeClickTaskButton(ClickModeManager.MODE_DEFINE_LINE_FACETS,"defineLineFacets","EF",listener);
        buttonModeDeleteFacets = makeClickTaskButton(ClickModeManager.MODE_DELETE_FACETS,"deleteFacets","DF",listener);
        buttonModeChangeFacets = makeClickTaskButton(ClickModeManager.MODE_CHANGE_FACETS_GROUP,"changeFacets","FG",listener);
        buttonModeReverseFacets = makeClickTaskButton(ClickModeManager.MODE_REVERSE_FACETS,"reverseFacets","RF",listener);
        buttonModeEdgeFlip = makeClickTaskButton(ClickModeManager.MODE_EDGE_FLIP,"edgeFlip","EF",listener);
        buttonModeAddNodesOnEdges = makeClickTaskButton(ClickModeManager.MODE_ADD_NODES_ON_EDGES,"addNodesOnEdges","NE",listener); 
        buttonModeAddNodesInTriFacets = makeClickTaskButton(ClickModeManager.MODE_ADD_NODES_IN_FACETS,"addNodesInTriFacets","NT",listener);

        // Remove some of the buttons depending on the number of dimensions:
        if (ndim==3) {
            remove(buttonModeDefineLineFacets);
        }
        if (ndim==2) {
            remove(buttonResetSnapshot);
            remove(buttonModeOriginNode3D);
            remove(buttonModeDefinePolyFacetsTri);
            remove(buttonModeDefineTriFacets);
            remove(buttonModeEdgeFlip);
            remove(buttonModeAddNodesInTriFacets);
        }
        
    }
    
    private ClickTaskButton makeClickTaskButton(int mode, String file, String alt, ActionListener listener) {
        ClickTask task = controller.getClickTask(mode);
        ClickTaskButton button;
        java.net.URL imgURL = getClass().getResource("/facetmodeller/icons/" + file + ".png");
        if (imgURL==null) {
            button = new ClickTaskButton(task,alt);
        } else {
            final ImageIcon icon = new ImageIcon(imgURL);
            button = new ClickTaskButton(task,icon);
        }
        button.addActionListener(listener);
        add(button);
        clickTaskButtons.add(button);
        return button;
    }
    
    private MenuTaskButton makeMenuTaskButton(MenuTask task, String file, String alt, ActionListener listener) {
        MenuTaskButton button;
        java.net.URL imgURL = getClass().getResource("/facetmodeller/icons/" + file + ".png");
        if (imgURL==null) {
            button = new MenuTaskButton(task,alt);
        } else {
            final ImageIcon icon = new ImageIcon(imgURL);
            button = new MenuTaskButton(task,icon);
        }
        button.setToolTipText(task.text());
        button.addActionListener(listener);
        add(button);
        taskButtons.add(button);
        return button;
    }

    /** Action listener for the buttons. */
    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            // Deal with the MenuTaskButtons:
            if (src instanceof MenuTaskButton) {
                MenuTaskButton button = (MenuTaskButton) src; // cast
                button.execute();
                return;
            }
            // Deal with the ClickTaskButtons:
            if (src instanceof ClickTaskButton) {
                ClickTaskButton button = (ClickTaskButton) src; // cast
                controller.setClickMode(button.getMode());
                //return;
            }
        }
    }

    /** Enables or disables some menu items. */
    public void checkItemsEnabled() {
        // Deal with the TaskButtons:
        for (int i=0 ; i<taskButtons.size() ; i++) {
            taskButtons.get(i).checkEnabled();
        }
        // Deal with the ClickTaskButtons:
        for (int i=0 ; i<clickTaskButtons.size() ; i++) {
            clickTaskButtons.get(i).checkEnabled();
        }
    }
    
    public void markToolBarButtonBackground(int mode) {
        // Change all button backgrounds back to default:
        for (int i=0 ; i<taskButtons.size() ; i++) {
            resetButton(taskButtons.get(i));
        }
        for (int i=0 ; i<clickTaskButtons.size() ; i++) {
            resetButton(clickTaskButtons.get(i));
        }
        // Figure out which button corresponds to the current click mode:
        JButton button;
        switch (mode) {
            case ClickModeManager.MODE_INFO:
                button = buttonModeInfo;
                break;
            case ClickModeManager.MODE_ORIGIN_2D:
                button = buttonModeSetOrigin2D;
                break;
            case ClickModeManager.MODE_ORIGIN_NODE_3D:
                button = buttonModeOriginNode3D;
                break;
            case ClickModeManager.MODE_ADD_NODES:
                button = buttonModeAddNodes;
                break;
            case ClickModeManager.MODE_ADD_NODES_ON_EDGES:
                button = buttonModeAddNodesOnEdges;
                break;
            case ClickModeManager.MODE_ADD_NODES_IN_FACETS:
                button = buttonModeAddNodesInTriFacets;
                break;
            case ClickModeManager.MODE_DELETE_NODES:
                button = buttonModeDeleteNodes;
                break;
            case ClickModeManager.MODE_MOVE_NODES:
                button = buttonModeMoveNodes;
                break;
            case ClickModeManager.MODE_MERGE_NODES:
                button = buttonModeMergeNodes;
                break;
            case ClickModeManager.MODE_CHANGE_NODES_GROUP:
                button = buttonModeChangeNodes;
                break;
            case ClickModeManager.MODE_DEFINE_POLY_FACETS:
                button = buttonModeDefinePolyFacets;
                break;
            case ClickModeManager.MODE_DEFINE_POLY_FACETS_TRI:
                button = buttonModeDefinePolyFacetsTri;
                break;
            case ClickModeManager.MODE_DEFINE_TRI_FACETS:
                button = buttonModeDefineTriFacets;
                break;
            case ClickModeManager.MODE_DEFINE_LINE_FACETS:
                button = buttonModeDefineLineFacets;
                break;
            case ClickModeManager.MODE_DELETE_FACETS:
                button = buttonModeDeleteFacets;
                break;
            case ClickModeManager.MODE_CHANGE_FACETS_GROUP:
                button = buttonModeChangeFacets;
                break;
            case ClickModeManager.MODE_REVERSE_FACETS:
                button = buttonModeReverseFacets;
                break;
            case ClickModeManager.MODE_EDGE_FLIP:
                button = buttonModeEdgeFlip;
                break;
            default:
                return;
        }
        // Change the background colour of the correct button:
        markButton(button);
    }
    private void resetButton(JButton button) {
        button.setBackground(null);
        button.setOpaque(false);
        button.setBorderPainted(true);
    }
    private void markButton(JButton button) {
        // Change the background colour of the button:
        button.setBackground(Color.green);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }
    
}
