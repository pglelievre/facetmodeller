package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;

public final class CalibrateClickTask extends ControlledClickTask {
    
    public CalibrateClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_CALIBRATE; }

    @Override
    public String tip() { return ClickTaskUtil.CALIBRATE_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.CALIBRATE_TITLE; }

    @Override
    public boolean check() { return controller.calibrationCheck(); }

    @Override
    public void mouseClick(MyPoint2D p) {

        // Check for the required information:
        if ( !check() || p==null ) {
            controller.endCalibration();
            return;
        }
        
        // Get the current section and check against the section being calibrated:
        Section currentSection = controller.getSelectedCurrentSection();
        Section calibrationSection = controller.getCalibrationSection();
        if (!currentSection.equals(calibrationSection)) {
            controller.endCalibration();
            return;
        }
        // Make sure the current section can be calibrated:
        if (!calibrationSection.canCalibrate()) {
            controller.endCalibration();
            return;
        }
        // Perform the next step in the calibration:
        boolean ok = calibrationSection.calibrate(controller,p);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        // Display any instructions to continue the calibration:
        if (!ok) {
            controller.endCalibration();
            return;
        }
        if (!calibrationSection.continueCalibration(controller)) {
            controller.endCalibration();
            //return;
        }
    }
    
}
