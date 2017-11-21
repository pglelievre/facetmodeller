package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public final class MoveGroupDown1MenuTask extends MoveGroupDownMenuTask {
    
    public MoveGroupDown1MenuTask(FacetModeller con) {
        super(con,1);
    }
    
    @Override
    public String text() { return "Move group down 1"; }

    @Override
    public String tip() { return "Moves the current group down one position in the list"; }
    
}
