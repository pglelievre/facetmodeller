package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.sections.Section;

/** 
 * @author Peter
 */
public final class StartCalibrationMenuTask extends ControlledMenuTask {
    
    public StartCalibrationMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Calibrate current section"; }

    @Override
    public String tip() { return "Begins the calibration proceedure for the selected current section"; }

    @Override
    public String title() { return "Calibrate current section"; }

    @Override
    public boolean check() { return controller.calibrationCheck(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) {
            controller.endCalibration();
            return;
        }
        // Save the current section so we can make sure the calibrate method stops if the current section changes:
        controller.setCalibrationSection();
        // Start the calibration:
        Section calibrationSection = controller.getCalibrationSection();
        if (!calibrationSection.startCalibration(controller)) { // this may do all the calibration or may provide an error message
            controller.endCalibration();
            return;
        }
        // Set the click mode for calibration so that the calibrate method will be called when the 2D viewing window is clicked:
        controller.setClickMode(ClickModeManager.MODE_CALIBRATE);
    }
    
}
