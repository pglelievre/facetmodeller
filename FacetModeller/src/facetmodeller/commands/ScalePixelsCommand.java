package facetmodeller.commands;

import facetmodeller.ModelManager;

/** Command to scale image pixel coordinates.
 * @author Peter
 */
public final class ScalePixelsCommand extends ModelCommand {
    
    private double factor;
    
    public ScalePixelsCommand(ModelManager mod, double f) {
        super(mod,"Scale Image Pixel Coordinates");
        factor = f;
    }
    
    @Override
    public void execute() {
        model.scalePixels(factor);
    }
    
    @Override
    public void undo() {
        model.scalePixels(1.0/factor);
    }
    
}
