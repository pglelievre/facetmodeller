package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.sections.Section;
import geometry.Dir3D;

/** Copies the calibration information from the current section to all the others below it in the list of sections.
 * @author Peter
 */
public final class CopyCalibrationMenuTask extends ControlledMenuTask {
    
    public CopyCalibrationMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Copy calibration"; }

    @Override
    public String tip() { return "Copies the calibration information from the current section to the other sections"; }

    @Override
    public String title() { return "Copy Calibration"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        return currentSection.canCalibrate();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Make sure the current section has a natural direction:
        Section currentSection = controller.getSelectedCurrentSection();
        Dir3D dir = currentSection.getDir3D();
        if (dir==null) {
            Dialogs.error(controller,"The current section is not alligned with the Cartesian axes.",title());
            return;
        }
        
        // Ask the user for the step:
        String prompt = "Enter the step in the " + dir.toChar() + " direction:";
        String s = Dialogs.input(controller,prompt,title());
        if (s==null) { return; } // user cancelled
        s = s.trim();
        String[] ss = s.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"Please enter a single numeric value.","Error");
            return;
        }
        double step;
        try {
           step = Double.parseDouble(ss[0].trim());
        } catch ( NumberFormatException e ) {
            Dialogs.error(controller,"Please enter a valid numeric.",title());
            return;
        }
        
        // Do it:
        controller.copyCalibration(step);
        
    }
    
}
