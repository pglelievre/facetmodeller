package facetmodeller.commands;

import facetmodeller.plc.Facet;

/** Command to reverse the node order of a facet.
 * @author Peter
 */
public final class ReverseFacetCommand extends Command {
    
    private Facet facet;
    
    public ReverseFacetCommand(Facet f) {
        super("Reverse Facet Node Order");
        facet = f;
    }
    
    @Override
    public void execute() {
        facet.reverse();
    }
    
    @Override
    public void undo() {
        facet.reverse();
    }
    
}
