package facetmodeller.menutasks;

import facetmodeller.FacetModeller;

public final class MoveGroupDown2MenuTask extends MoveGroupDownMenuTask {
    
    public MoveGroupDown2MenuTask(FacetModeller con) {
        super(con,2);
    }
    
    @Override
    public String text() { return "Move group down 2"; }

    @Override
    public String tip() { return "Moves the current group down two positions in the list"; }
    
}
