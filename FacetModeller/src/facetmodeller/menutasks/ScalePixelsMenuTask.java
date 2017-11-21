package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.ScalePixelsCommand;

/** Scales all image pixel coordinates (Point2D nodes, regions, section calibration).
 * This is helpful for when an image has been resized.
 * @author Peter
 */
public final class ScalePixelsMenuTask extends ControlledMenuTask {
    
    public ScalePixelsMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Scale image pixel coordinates"; }

    @Override
    public String tip() { return "Scales the image pixel coordinates. This is helpful for when an image has been resized."; }

    @Override
    public String title() { return "Scale Image Pixel Coordinates"; }

    @Override
    public boolean check() {
        return ( controller.hasNodes() || controller.hasRegions() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the scaling factor:
        String response = Dialogs.input(controller,"Enter the scaling factor:",title());
        // Check response:
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split(" ");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numerical value. Please try again.","Error");
            return;
        }
        double factor;
        try {
            factor = Double.parseDouble(ss[0].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(controller,"You must enter a single numerical value. Please try again.","Error");
            return;
        }
        ScalePixelsCommand com = new ScalePixelsCommand(controller.getModelManager(),factor); com.execute();
        controller.undoVectorAdd(com);
        controller.scalePixels(factor);
        // Repaint:
        controller.redraw();
    }
    
}
