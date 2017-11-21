package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.sections.Section;

/** Changes the coordinates typed by the user for the calibration points.
 * @author Peter
 */
public final class ChangeCalibrationCoordinatesMenuTask extends ControlledMenuTask {
    
    public ChangeCalibrationCoordinatesMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Change calibration coordinates for current section"; }

    @Override
    public String tip() { return "Changes the coordinates typed by the user for the calibration points"; }

    @Override
    public String title() { return "Change Calibration Coordinates"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        return ( currentSection.canCalibrate() && currentSection.isCalibrated() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Tell the current section to do the work:
        Section currentSection = controller.getSelectedCurrentSection();
        if (!currentSection.changeCalibrationCoordinates(controller)) { return; }
        // Enable or disable menu items:
        controller.checkItemsEnabled(); // I don't think this is necessary but it can't hurt.
        // Repaint:
        controller.redraw();
    }
    
}
