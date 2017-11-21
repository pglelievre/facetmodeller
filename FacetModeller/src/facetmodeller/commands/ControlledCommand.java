package facetmodeller.commands;

import facetmodeller.FacetModeller;

/** A command connected to a controller.
 * @author Peter
 */
public abstract class ControlledCommand extends Command {
    
    @SuppressWarnings("ProtectedField")
    protected FacetModeller controller;
    
    public ControlledCommand(FacetModeller con, String t) {
        super(t);
        controller = con;
    }
    
}
