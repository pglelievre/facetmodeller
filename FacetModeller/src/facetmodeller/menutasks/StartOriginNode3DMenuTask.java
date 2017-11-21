package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;

/** 
 * @author Peter
 */
public final class StartOriginNode3DMenuTask extends ControlledMenuTask {
    
    public StartOriginNode3DMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Select 3D origin node"; }

    @Override
    public String tip() { return "Select the node that should be the origin for the 3D viewer"; }

    @Override
    public String title() { return "Select 3D Origin Node"; }

    @Override
    public boolean check() { return controller.originNode3DCheck(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) {
            controller.endOriginNode3D();
            return;
        }
        // Provide instructions:
        Dialogs.inform(controller,"Click on the node that should be the origin of the 3D viewer.",title());
        // Set the click mode:
        controller.setClickMode(ClickModeManager.MODE_ORIGIN_NODE_3D);
    }
    
}
