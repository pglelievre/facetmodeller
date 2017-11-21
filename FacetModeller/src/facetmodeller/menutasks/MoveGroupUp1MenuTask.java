package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public final class MoveGroupUp1MenuTask extends MoveGroupUpMenuTask {
    
    public MoveGroupUp1MenuTask(FacetModeller con) {
        super(con,1);
    }
    
    @Override
    public String text() { return "Move group up 1"; }

    @Override
    public String tip() { return "Moves the current group up one position in the list"; }
    
}
