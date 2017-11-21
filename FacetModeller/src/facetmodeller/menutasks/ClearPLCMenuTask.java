package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.CommandVector;
import facetmodeller.commands.RemoveFacetCommandVector;
import facetmodeller.commands.RemoveNodeCommandVector;
import facetmodeller.commands.RemoveRegionCommandVector;

/** 
 * @author Peter
 */
public final class ClearPLCMenuTask extends ControlledMenuTask {
    
    public ClearPLCMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Clear model"; }

    @Override
    public String tip() { return "Clear the model (remove all nodes, facets and regions from the PLC)."; }

    @Override
    public String title() { return "Clear Model"; }

    @Override
    public boolean check() { return !controller.plcIsEmpty(); }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get user confirmation:
        int response = Dialogs.confirm(controller,"Are you sure?",title());
        if (response!=Dialogs.OK_OPTION) { return; }
        // Remove all plc components:
        ModelManager model = controller.getModelManager();
        CommandVector commands = new CommandVector(title());
        commands.add( new RemoveFacetCommandVector(model,model.getFacets(),"") );
        commands.add( new RemoveNodeCommandVector(model,model.getNodes(),"") );
        commands.add( new RemoveRegionCommandVector(model,model.getRegions()) );
        commands.execute();
        controller.undoVectorAdd(commands);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
