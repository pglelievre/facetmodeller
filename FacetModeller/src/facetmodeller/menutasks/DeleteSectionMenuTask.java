package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.RemoveSectionCommand;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;

/** Removes the current section from the section list.
 * @author Peter
 */
public final class DeleteSectionMenuTask extends ControlledMenuTask {
    
    public DeleteSectionMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Delete section"; }

    @Override
    public String tip() { return "Removes the current section from the section list"; }

    @Override
    public String title() { return "Delete Section"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasSections()) { return false; }
        return (controller.getSelectedCurrentSection()!=null);
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask user for confirmation:
        int response = Dialogs.confirm(controller,"Are you sure you want to remove the current section?",title());
        if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        // Store the selections in the group selector objects for use later:
        int ind = controller.getSelectedCurrentSectionIndex();
        SectionVector selectedOtherSections = controller.getSelectedOtherSections();
        // Change those selections to remove the current section:
        Section currentSection = controller.getSelectedCurrentSection();
        if (selectedOtherSections!=null) { selectedOtherSections.remove(currentSection); }
        // Remove the section:
        RemoveSectionCommand com = new RemoveSectionCommand(controller,currentSection); com.execute();
        controller.undoVectorAdd(com);
        // Reset the selections:
        if (ind==0) {
            controller.clearCurrentSectionSelection(); // there are no longer any sections loaded
        } else {
            controller.setSelectedCurrentSectionIndex(ind-1); // positions the current section to that before the one deleted
        }
        if (selectedOtherSections!=null) { controller.setSelectedOtherSections(selectedOtherSections); }
        // Tell the controller that the section selection has changed:
        controller.sectionSelectionChanged(true);
    }
    
}
