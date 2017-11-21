package facetmodeller.commands;

import facetmodeller.ModelManager;

/** A command that is connected to a Model, has a name and requires a list of subcommands.
 * @author Peter
 */
public abstract class ModelCommandVector extends CommandVector {
    
    @SuppressWarnings("ProtectedField")
    protected ModelManager model;
    
    public ModelCommandVector(ModelManager mod, String t) {
        super(t);
        model = mod;
    }
    
}
