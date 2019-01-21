package facetmodeller.commands;

import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.sections.Section;

/** Command to remove a section.
 * @author Peter
 */
public final class RemoveSectionCommand extends ControlledCommandVector {
    
    private final Section section;
    
    public RemoveSectionCommand(FacetModeller con, Section s) {
        super(con,"Remove Section");
        section = s;
        // Create the remove nodes/facets/regions commands:
        ModelManager mod = con.getModelManager();
        add( new RemoveNodeCommandVector(mod,s.getNodes(),"") );
        //add( new RemoveFacetCommandVector(mod,s.getFacets(),"") );
        add( new RemoveRegionCommandVector(mod,s.getRegions()) );
    }
    
    @Override
    public void execute() {
        // Remove the nodes/facets/regions associated with the section:
        super.execute();
        // Remove the section from the section vector:
        controller.removeSection(section);
        // Update the clickable lists:
        controller.updateSectionSelectors();
    }
    
    @Override
    public void undo() {
        // Undo all the remove nodes/facets/regions commands:
        super.undo();
        // Add the section to the section vector:
        controller.addSection(section);
        // Update the clickable lists:
        controller.updateSectionSelectors();
    }
    
}
