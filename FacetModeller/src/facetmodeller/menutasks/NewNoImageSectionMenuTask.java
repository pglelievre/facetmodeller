package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.gui.PaintingOptions;
import facetmodeller.sections.NoImageCrossSection;
import facetmodeller.sections.NoImageDepthSection;
import facetmodeller.sections.Section;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Makes a new cross section without an image associated with it.
 * @author Peter
 */
public final class NewNoImageSectionMenuTask extends ControlledMenuTask {
    
    private final boolean iscross;
    
    public NewNoImageSectionMenuTask(FacetModeller con, boolean b) {
        super(con);
        iscross = b;
    }
    

    @Override
    public String text() {
        String t;
        if (iscross) {
            t = "New cross section without image";
        } else {
            t = "New depth section without image";
        }
        return t;
    }

    @Override
    public String tip() { return ""; }

    @Override
    public String title() {
        String t;
        if (iscross) {
            t = "New Cross Section";
        } else {
            t = "New Depth Section";
        }
        return t;
    }

    @Override
    public boolean check() { return true; }

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
        
        // Create a new cross-section object without an image file:
        Section section;
        if (iscross) {
            section = new NoImageCrossSection(name,col); // (size will get changed after calibration)
        } else {
            section = new NoImageDepthSection(name,col); // (size will get changed after calibration)
        }
        
        // Add the new section to the end of the list:
        controller.addSection(section);

        // Update the clickable lists:
        controller.updateSectionSelectors();
        controller.setSelectedCurrentSection(section); // the new section becomes the current section
        controller.clearOtherSectionSelection(); // no other section selected

        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
        // Calibrate the section:
        controller.startCalibration();
                
        // Repaint:
        controller.redraw();
        
    }
    
}
