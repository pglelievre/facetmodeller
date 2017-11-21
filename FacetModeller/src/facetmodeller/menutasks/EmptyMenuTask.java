package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

/** 
 * @author Peter
 */
public final class EmptyMenuTask extends ControlledMenuTask {
    
    public EmptyMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return ""; }

    @Override
    public String tip() { return ""; }

    @Override
    public String title() { return ""; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
    }
    
}
