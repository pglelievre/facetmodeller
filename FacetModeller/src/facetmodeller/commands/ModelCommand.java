package facetmodeller.commands;

import facetmodeller.ModelManager;

/** A command connected to a Model.
 * @author Peter
 */
public abstract class ModelCommand extends Command {
    
    @SuppressWarnings("ProtectedField")
    protected ModelManager model;
    
    public ModelCommand(ModelManager mod, String t) {
        super(t);
        model = mod;
    }
    
}
