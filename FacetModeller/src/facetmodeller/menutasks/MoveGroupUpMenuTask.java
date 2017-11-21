package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public abstract class MoveGroupUpMenuTask extends MoveGroupMenuTask {
    
    public MoveGroupUpMenuTask(FacetModeller con, int i) { super(con,-Math.abs(i)); }

    @Override
    public String title() { return "Move Group Up"; }
    
}
