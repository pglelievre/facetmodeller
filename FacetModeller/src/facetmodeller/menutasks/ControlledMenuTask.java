package facetmodeller.menutasks;

import tasks.MenuTask;
import facetmodeller.FacetModeller;

/** A task connected to a FacetModeller controller.
 * @author Peter
 */
public abstract class ControlledMenuTask implements MenuTask {
    
    @SuppressWarnings("ProtectedField")
    protected FacetModeller controller;
    
    public ControlledMenuTask(FacetModeller con) {
        super();
        controller = con;
    }

}
