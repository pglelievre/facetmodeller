package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public abstract class MoveGroupDownMenuTask extends MoveGroupMenuTask {
    
    public MoveGroupDownMenuTask(FacetModeller con, int i) { super(con,i); }

    @Override
    public String title() { return "Move Group Down"; }
    
}
