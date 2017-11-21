package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public final class MoveGroupBottomMenuTask extends MoveGroupDownMenuTask {
    
    public MoveGroupBottomMenuTask(FacetModeller con) {
        super(con,999); // I'm assuming the user will have less than this many groups!
    }
    
    @Override
    public String text() { return "Move group to bottom"; }

    @Override
    public String tip() { return "Moves the current group to the bottom of the list"; }
    
}
