package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public final class MoveGroupUp2MenuTask extends MoveGroupUpMenuTask {
    
    public MoveGroupUp2MenuTask(FacetModeller con) {
        super(con,2);
    }
    
    @Override
    public String text() { return "Move group up 2"; }

    @Override
    public String tip() { return "Moves the current group up two positions in the list"; }
    
}
