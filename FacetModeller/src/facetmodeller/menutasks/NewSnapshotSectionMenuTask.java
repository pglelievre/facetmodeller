package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.gui.PaintingOptions;
import facetmodeller.gui.Projector3D;
import facetmodeller.sections.Section;
import facetmodeller.sections.SnapshotSection;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Makes a new snapshot section based on the current 3D view.
 * @author Peter
 */
public final class NewSnapshotSectionMenuTask extends ControlledMenuTask {
    
    public NewSnapshotSectionMenuTask(FacetModeller con) { super(con); }

    @Override
    public String text() { return "New snapshot section from current 3D view"; }

    @Override
    public String tip() { return "Create new snapshot section from current 3D view"; }

    @Override
    public String title() { return "New Snapshot Section"; }

    @Override
    public boolean check() { return controller.is3D(); }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Ask for a name for the new section:
        String name = Dialogs.input(controller,"Enter a name for the section:",title());
        if (name==null) { return; } // user cancelled
        name = name.trim();
        if (name.isEmpty()) { return; } // user entered empty string
        
        // Ask for a color for the new section:
        Color col = JColorChooser.showDialog(controller,"Choose Color for New Section",PaintingOptions.DEFAULT_SECTION_COLOR); //imagePanel.getBackground());
        if (col == null) { return; }
        
        // Get the projection information from the 3D viewer:
        Projector3D proj = controller.getProjector3D().deepCopy();
        
        // Create a new snapshot section object:
        Section section = new SnapshotSection(name,col,proj);

        // Add the new section to the end of the list:
        controller.addSection(section);

        // Update the clickable lists:
        controller.updateSectionSelectors();
        controller.setSelectedCurrentSection(section); // the new section becomes the current section
        controller.clearOtherSectionSelection(); // no other section selected

        // Repaint:
        controller.redraw();
        
    }
    
}
