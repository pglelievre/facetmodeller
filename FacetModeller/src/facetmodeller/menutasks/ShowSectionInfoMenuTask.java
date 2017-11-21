package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.sections.Section;

/** 
 * @author Peter
 */
public final class ShowSectionInfoMenuTask extends ControlledMenuTask {
    
    public ShowSectionInfoMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Section information"; }

    @Override
    public String tip() { return "Display information about the current section"; }

    @Override
    public String title() { return "Section Information"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        Section section = controller.getSelectedCurrentSection();
        String t;
        if (section==null) {
            if (controller.is3D()) {
                t = "There are no sections loaded.";
            } else {
                t = "There is no section loaded.";
            }
        } else {
            if (controller.is3D()) {
                int n = controller.numberOfSections();
                if (n==1) {
                    t = "There is one section.";
                } else {
                    t = "There are " + n + " sections.";
                }
                t += "\nThe current section image is ";
            } else {
                t = "The section image is ";
            }
            t = t + section.getWidth() + "-by-" + section.getHeight() + " pixels.";
        }
        Dialogs.inform(controller,t,title());
    }
    
}
