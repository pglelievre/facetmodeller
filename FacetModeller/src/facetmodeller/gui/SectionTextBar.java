package facetmodeller.gui;

import facetmodeller.sections.Section;
import gui.TextBar;
//import java.awt.Dimension;

/** The TextBar object that provides information about the current section.
 * @author Peter
 */
public final class SectionTextBar extends TextBar {
    private static final long serialVersionUID = 1L;
    
    public SectionTextBar() {
        super();
        //this.setPreferredSize(new Dimension(100,50));
    }
    
    public void updateText(int numberOfDimensions, int numberOfSections, int index, Section section) {

        // Create the text and tool tip strings:
        String text,tip;
        if (numberOfSections==0 || section==null) {
            text = "No sections loaded.";
            tip = text;
        } else {
            if (numberOfDimensions==3) {
               // Section i of n: name
               text = "Section " + (index+1) + " of " + numberOfSections + ": " + section.shortName();
               tip  = "Section " + (index+1) + " of " + numberOfSections + ": " + section.longName();
            } else {
                // Section: name
               text = "Section : " + section.shortName();
               tip  = "Section : " + section.longName();
            }
        }

        // Display the text in the section bar:
        setText(text);
        
        // Set the tool tip text in case the window is resized too small to see the whole path:
        setToolTipText(tip);
        
    }
    
}
