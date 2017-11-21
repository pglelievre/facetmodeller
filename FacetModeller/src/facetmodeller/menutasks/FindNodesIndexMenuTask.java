package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.ChangeNodeGroupCommandVector;
import facetmodeller.plc.NodeVector;

/** Helps the user find particular nodes by index.
 * @author Peter
 */
public final class FindNodesIndexMenuTask extends ControlledMenuTask {
    
    public FindNodesIndexMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Find nodes by index"; }

    @Override
    public String tip() { return "Moves nodes with specified indices into the current group"; }

    @Override
    public String title() { return "Find Nodes by Index"; }

    @Override
    public boolean check() {
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the node index to find:
        String response = Dialogs.input(controller,"Enter the indices of the nodes to find:",title());
        if (response==null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        NodeVector nodesFound = new NodeVector();
        int nnodes = controller.numberOfNodes();
        try {
            ModelManager model = controller.getModelManager();
            //for (int i=0 ; i<ss.length ; i++ ) {
            //    int ind = Integer.parseInt(ss[i].trim());
            for (String s : ss) {
                int ind = Integer.parseInt(s.trim());
                if ( ind<1 || ind>nnodes ) { throw new NumberFormatException(); }
                nodesFound.add( model.getNode(ind-1) ); // -1 because Java starts numbering from 0 but poly files from 1
            }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter integer values on [1,"+nnodes+"]. Please try again.","Error");
            return;
        }
        // Move the nodes to the current group:
        ChangeNodeGroupCommandVector com = new ChangeNodeGroupCommandVector(nodesFound,controller.getSelectedCurrentGroup(),title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}
