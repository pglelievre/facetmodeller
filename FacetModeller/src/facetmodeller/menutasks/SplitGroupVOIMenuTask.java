package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.SplitGroupCommand;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import geometry.MyPoint3D;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Splits the current group by the VOI.
 * Nodes inside or on the VOI are kept in the current group, all others are moved to a new group.
 * Facets with only nodes in or on the VOI are kept in the current group, all others are moved to a new group.
 * @author Peter
 */
public final class SplitGroupVOIMenuTask extends ControlledMenuTask {
    
    public SplitGroupVOIMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Split group by VOI"; }

    @Override
    public String tip() { return "Nodes outside the VOI are moved to a new group. Facets with any nodes outside the VOI are also moved to that new group."; }

    @Override
    public String title() { return "Split Group by VOI"; }

    @Override
    public boolean check() {
        if (!controller.is3D()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasVOI();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the nodes for the current group:
        Group currentGroup = controller.getSelectedCurrentGroup();
        NodeVector nodes = currentGroup.getNodes();
        
        // Find any nodes in the current group outside of the VOI:
        NodeVector nodesOutside = new NodeVector();
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node n = nodes.get(i);
            MyPoint3D p = n.getPoint3D();
            if (p==null) {
                Dialogs.error(controller,"Please make sure all sections are calibrated first.",title());
                return;
            }
            if ( !controller.inOrOn(p) ) {
                nodesOutside.add(n);
            }
        }
        
        // Check for nodes outside:
        if ( nodesOutside.size() == 0 ) {
            Dialogs.inform(controller,"No nodes were found outside the VOI for the current group.",title());
            return;
        }
        
        // Ask for the colour of the new group:
        Color col = JColorChooser.showDialog(controller,title(),currentGroup.getNodeColor());
        // Check response:
        if (col == null) { return; }
        
        // The name of the current group will change to prefix "_inside_VOI":
        String name = currentGroup.getName();
        String newName = name + "_inside_VOI";
        
        // Make a new group with prefix "_outside_VOI" and selected colour:
        Group newGroup = new Group( name + "_outside_VOI" , col );
        
        // Find any facets in the group that are attached to the outside nodes:
        FacetVector facetsOutside = new FacetVector();
        for (int i=0 ; i<nodesOutside.size() ; i++ ) {
            Node n = nodesOutside.get(i); // ith outside node
            FacetVector facets = n.getFacets(); // facets attached to the ith outside node
            for (int j=0 ; j<facets.size() ; j++ ) { // loop over each of those facets
                Facet f = facets.get(j); // jth facet attached to the ith outside node
                if ( f.getGroup() == currentGroup ) { // the jth facet is in the current group
                    facetsOutside.add(f); // jth facet marked as outside
                }
            }
        }
        
        // Get the index of the current group object:
        int ind = controller.getSelectedCurrentGroupIndex();
        
        // Get the currently selected node and facet groups, so we can maintain the selections,
        // and adjust them as required:
        GroupVector selectedNodeGroups = controller.getSelectedNodeGroups();
        GroupVector selectedFacetGroups = controller.getSelectedFacetGroups();
        
        // Split the group, adding the new group object to the list of groups just after the current group:
        SplitGroupCommand com = new SplitGroupCommand(controller,currentGroup,newName,newGroup,nodesOutside,facetsOutside,ind+1,title()); com.execute();
        controller.undoVectorAdd(com);
        
        // Add the new group to the group selections:
        selectedNodeGroups.add(newGroup);
        selectedFacetGroups.add(newGroup);
        controller.setSelectedCurrentGroupIndex(ind);
        controller.setSelectedNodeGroups(selectedNodeGroups);
        controller.setSelectedFacetGroups(selectedFacetGroups);
        
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
    }
    
}
