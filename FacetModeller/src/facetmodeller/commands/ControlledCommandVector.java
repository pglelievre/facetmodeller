package facetmodeller.commands;

import facetmodeller.FacetModeller;

/** A command that is connected to a controller, has a name and requires a list of subcommands.
 * @author Peter
 */
public abstract class ControlledCommandVector extends CommandVector {
    
    @SuppressWarnings("ProtectedField")
    protected FacetModeller controller;
    
    public ControlledCommandVector(FacetModeller con, String t) {
        super(t);
        controller = con;
    }
    
}
