package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public final class MoveGroupTopMenuTask extends MoveGroupUpMenuTask {
    
    public MoveGroupTopMenuTask(FacetModeller con) {
        super(con,999); // I'm assuming the user will have less than this many groups!
    }
    
    @Override
    public String text() { return "Move group to top"; }

    @Override
    public String tip() { return "Moves the current group to the top of the list"; }
    
}
