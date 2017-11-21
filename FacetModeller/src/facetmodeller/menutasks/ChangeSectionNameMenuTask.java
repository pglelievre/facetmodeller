package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.sections.Section;

public final class ChangeSectionNameMenuTask extends ControlledMenuTask {
    
    public ChangeSectionNameMenuTask(FacetModeller con) { super(con); }

    @Override
    public String text() { return "Change section name"; }

    @Override
    public String tip() { return "Change the name of the currently selected section"; }

    @Override
    public String title() { return "Change Section Name"; }

    @Override
    public boolean check() {
        if ( !controller.hasSections() ) { return false; }
        return ( controller.getSelectedCurrentSection() != null );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Make sure the section name can change:
        Section currentSection = controller.getSelectedCurrentSection();
        if (!currentSection.canChangeName()) {
            Dialogs.error(controller,"Sorry, you can't change the name of the current section.",title());
            return;
        }
        // Ask for the name of the section:
        String newName = Dialogs.input(controller,"Enter the new name for the current section:",title(),currentSection.shortName());
        // Check response:
        if (newName==null) { return; } // user cancelled
        // Set the section name to that spectified:
        currentSection.setName(newName);
        // Store the selections in the selector objects:
        int selectedCurrentSectionIndex = controller.getSelectedCurrentSectionIndex();
        int[] selectedOtherSectionIndices = controller.getSelectedOtherSectionIndices();
        // Update the graphical selector objects:
        controller.updateSectionSelectors();
        // Reset the selections to whatever they were:
        if (selectedCurrentSectionIndex>=0) { controller.setSelectedCurrentSectionIndex(selectedCurrentSectionIndex); }
        if (selectedOtherSectionIndices!=null) { controller.setSelectedOtherSectionIndices(selectedOtherSectionIndices); }
    }
    
}
