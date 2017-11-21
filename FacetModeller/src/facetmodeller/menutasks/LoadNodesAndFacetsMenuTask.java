package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.commands.AddFacetCommandVector;
import facetmodeller.commands.AddNodeCommandVector;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SnapshotSection;
import filters.EleFilter;
import filters.NodeFilter;
import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;

/** Loads nodes from a .node file, and optional facets from a .ele file, into the current section and group.
 * @author Peter
 */
public final class LoadNodesAndFacetsMenuTask extends ControlledMenuTask {
    
    public LoadNodesAndFacetsMenuTask(FacetModeller con) { super(con); }

    @Override
    public String text() { return "Load from .node/.ele files"; }

    @Override
    public String tip() { return "Loads node and facet information from a pair of .node/.ele files."; }

    @Override
    public String title() { return "Load From .node/.ele File(s)"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        return (controller.getSelectedCurrentGroup()!=null);
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the current section and group:
        Section currentSection = controller.getSelectedCurrentSection();
        Group currentGroup = controller.getSelectedCurrentGroup();
        
        // Check the current section is appropriate for adding off-section nodes to:
        if (currentSection instanceof SnapshotSection) {
            Dialogs.error(controller,"You can't add nodes to a snapshot section.",title());
            return;
        }
        
        // Ask for the name of the .node file:
        JFileChooser chooser = new JFileChooser();
        NodeFilter nodeFilter = new NodeFilter();
        chooser.setCurrentDirectory(controller.getOpenDirectory());
        chooser.addChoosableFileFilter(nodeFilter);
        chooser.setFileFilter(nodeFilter);
        chooser.setDialogTitle("Select .node file");
        chooser.setMultiSelectionEnabled(false);
        int response = chooser.showOpenDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File nodeFile = chooser.getSelectedFile();

        // Set the load directory to the chosen directory:
        File loadDirectory = chooser.getCurrentDirectory();
        controller.setOpenDirectory(loadDirectory);
        
        // Check for .ele file of similar name to .node file:
        File eleFile = null;
        String s = nodeFile.getName();
        int idot = s.indexOf(".");
        if (idot>=0) {
            s = s.substring(0,idot+1) + "ele"; // replaces .node extension with .ele
            eleFile = new File(nodeFile.getParent(),s);
            if (!eleFile.exists()) {
                eleFile = null;
            }
        }

        // Ask for the name of the .ele file:
        chooser = new JFileChooser();
        EleFilter eleFilter = new EleFilter();
        chooser.setCurrentDirectory(loadDirectory);
        chooser.addChoosableFileFilter(eleFilter);
        chooser.setFileFilter(eleFilter);
        chooser.setDialogTitle("Select .ele file (cancel if none)");
        chooser.setMultiSelectionEnabled(false);
        if (eleFile!=null) {
            chooser.setSelectedFile(eleFile);
        }
        response = chooser.showOpenDialog(controller);

        // Check response:
        if (response == JFileChooser.APPROVE_OPTION) {
            // Get the selected file:
            eleFile = chooser.getSelectedFile();
            // Set the load directory to the chosen directory:
            controller.setOpenDirectory(chooser.getCurrentDirectory());
        } else {
            // User cancelled - take this to mean that there is no ele file to load:
            eleFile = null;
        }

        // Read the node file:
        NodeVector nodes = new NodeVector();
        NodeVector.ReadNodesReturnObject readNodesReturnObj = nodes.readNodes(nodeFile,-2);
        if (readNodesReturnObj==null) {
            Dialogs.error(controller,"Failed to read .node file.",title());
            return;
        }
        
        // Check for attributes:
        boolean doNodeAtts = false;
        if (readNodesReturnObj.doAtts()) {
            String prompt = "Do you want to use the NODE attributes to define new NODE groups?";
            response = Dialogs.question(controller,prompt,title());
            if (response==Dialogs.CANCEL_OPTION) { return; }
            doNodeAtts = ( response == Dialogs.YES_OPTION );
        }
        
        // Get number of dimensions:
        int ndim = controller.numberOfDimensions();

        // Read the ele file (unless user cancelled when asked for .ele file):
        FacetVector facets = new FacetVector();
        boolean doFacetAtts = false;
        boolean doNodeAttsFromFacetAtts = false;
        boolean doNodeAttsFromFacetDefs = false;
        FacetVector.ReadFacetsReturnObject readFacetsReturnObj = null;
        if (eleFile!=null) {
            readFacetsReturnObj = facets.readEle(controller,title(),eleFile,nodes,ndim,true);
            if (readFacetsReturnObj==null) { return; } // user cancelled
            s = readFacetsReturnObj.getErrmsg();
            if (s!=null) {
                Dialogs.error(controller, "Failed to read .ele file: " + s ,title());
                return;
            }
            // Check for attributes:
            if (readFacetsReturnObj.getDoAtts()) {
                String prompt = "Do you want to use the FACET attributes to define new FACET groups?";
                response = Dialogs.question(controller,prompt,title());
                if (response==Dialogs.CANCEL_OPTION) { return; }
                doFacetAtts = ( response == Dialogs.YES_OPTION );
                if (!doNodeAtts) {
                    prompt = "Do you want to use the FACET attributes to define new NODE groups?";
                    response = Dialogs.question(controller,prompt,title());
                    if (response==Dialogs.CANCEL_OPTION) { return; }
                    doNodeAttsFromFacetAtts = ( response == Dialogs.YES_OPTION );
                    doNodeAttsFromFacetDefs = doNodeAttsFromFacetAtts;
                }
            } else {
                if (ndim==3) {
                    String prompt = "Do you want to use the FACET definitions to define a NODE boundary group?";
                    response = Dialogs.question(controller,prompt,title());
                    if (response==Dialogs.CANCEL_OPTION) { return; }
                    doNodeAttsFromFacetDefs = ( response == Dialogs.YES_OPTION );
                }
            }
            // Delete any unrequired nodes:
            if (readFacetsReturnObj.getDoRem()) {
                nodes.removeUnused();
            }
        }
        
        // Define new node groups:
        GroupVector newNodeGroups = null;
        if (doNodeAtts) {
            newNodeGroups = new GroupVector();
            // Loop over the new groups:
            int n = readNodesReturnObj.getN();
            for (int i=0 ; i<n ; i++ ) {
                // Create a new group object with default name:
                String name = currentGroup.getName() + "_NodeAtts" + (i+1);
                Group g = new Group(name);
                // Set colours to equal those for current group:
                g.setNodeColor(currentGroup.getNodeColor());
                g.setFacetColor(currentGroup.getFacetColor());
                g.setRegionColor(currentGroup.getRegionColor());
                // Add the group object to the list of new groups:
                newNodeGroups.add(g);
            }
            // Set the node group memberships:
            for (int i=0 ; i<nodes.size() ; i++ ) {
                Node node = nodes.get(i);
                int id = node.getID(); // the ID links to the new group objects
                Group g = newNodeGroups.get(id);
                node.setGroup(g);
            }
        }
        
        // Define new facet groups:
        GroupVector newFacetGroups = null;
        if ( doFacetAtts && readFacetsReturnObj!=null ) { // && ... avoids compiler warning
            newFacetGroups = new GroupVector();
            // Loop over the new groups:
            int n = readFacetsReturnObj.getN();
            for (int i=0 ; i<n ; i++ ) {
                // Create a new group object with default name:
                String name = "_FacetAtts" + (i+1);
                Group g = new Group(name); // default colour will be black
                // Add the group object to the list of new groups:
                newFacetGroups.add(g);
            }
            // Set the facet group memberships:
            for (int i=0 ; i<facets.size() ; i++ ) {
                Facet facet = facets.get(i);
                int id = facet.getID(); // the ID links to the new group objects
                Group g = newFacetGroups.get(id);
                facet.setGroup(g);
            }
        }
        
        // Define new NODE groups from FACET attributes:
        Group newGroupWithin = null;
        Group newGroupBetween = null;
        if (doNodeAttsFromFacetAtts) {
            // Define two new groups:
            newGroupWithin = new Group("NodeWithin"); // default colour will be black
            newGroupBetween = new Group("NodeBetween");
            newGroupBetween.setNodeColor(Color.WHITE);
            // Loop over each node:
            for (int i=0 ; i<nodes.size() ; i++ ) {
                // Get all the facets for the current node:
                Node node = nodes.get(i);
                FacetVector nodeFacets = node.getFacets();
                // Check if all the ID's/groups for those facets are the same:
                boolean same = true;
                Group g0 = nodeFacets.get(0).getGroup();
                for (int k=1 ; k<nodeFacets.size() ; k++ ) {
                    Group gk = nodeFacets.get(k).getGroup();
                    if (!g0.equals(gk)) {
                        same = false;
                        break;
                    }
                }
                // Add the node to the appropriate group (and add that group to the node):
                Group g;
                if (same) { // all facets the same so must be within a region
                    g = newGroupWithin;
                } else { // facets different so must be on boundary of a region
                    g = newGroupBetween;
                }
                g.addNode(node);
                node.setGroup(g);
            }
        }
        
        // Define new NODE boundary group from FACET definitions:
        Group newGroupBoundary = null;
        if (doNodeAttsFromFacetDefs) {
            // Define a new group:
            String name = currentGroup.getName() + "_NodeBoundary";
            newGroupBoundary = new Group(name);
            newGroupBoundary.setNodeColor(Color.WHITE);
            // Figure out the other group to stick the non-boundary nodes into:
            Group otherGroup;
            if (doNodeAttsFromFacetAtts) {
                otherGroup = null; // node groups already set above (I'll be overwriting them below)
            } else {
                otherGroup = currentGroup;
            }
            
            // Find the boundary nodes:
            NodeVector boundaryNodes = facets.findBoundaryNodes();
            
            // Add those nodes to the appropriate group (and add that group to the nodes):
            for (int i=0 ; i<nodes.size() ; i++ ) {
                Node node = nodes.get(i);
                Group g;
                if ( boundaryNodes!=null && boundaryNodes.contains(node) ) {
                    g = newGroupBoundary;
                } else {
                    g = otherGroup;
                }
                if (g!=null) {
                    g.addNode(node);
                    node.setGroup(g);
                }
            }
        }
        
        // Add section and group membership to the new nodes and optional facets:
        nodes.setSection(currentSection);
        if ( !doNodeAtts && !doNodeAttsFromFacetAtts && !doNodeAttsFromFacetDefs ) {
            nodes.setGroup(currentGroup);
        }
        if (!doFacetAtts) {
            facets.setGroup(currentGroup);
        }
        
        if (doNodeAtts) {
            // Add all the new groups to the main list of groups:
            controller.addGroups(newNodeGroups);
        }
        if ( doFacetAtts && readFacetsReturnObj!=null ) { // && ... avoids compiler warning
            // Add all the new groups to the main list of groups:
            controller.addGroups(newFacetGroups);
        }
        if (doNodeAttsFromFacetAtts) {
            // Add the new groups to the main list of groups:
            controller.addGroup(newGroupWithin);
            controller.addGroup(newGroupBetween); // this new group should be added second (see code below when updating selector objects)
        }
        if (doNodeAttsFromFacetDefs) {
            // Add the new group to the main list of groups:
            controller.addGroup(newGroupBoundary);
        }
        
        // Add the new nodes and optional facets to the plc, section, group:
        ModelManager model = controller.getModelManager();
        new AddNodeCommandVector(model,nodes,"").execute();
        new AddFacetCommandVector(model,facets).execute();
        
        // Update the graphical selector objects:
        if ( doNodeAtts || doFacetAtts || doNodeAttsFromFacetAtts || doNodeAttsFromFacetDefs ) {
            controller.updateGroupSelectors();
            int n = controller.numberOfGroups() - 1;
            controller.setSelectedCurrentGroupIndex(n); // sets the selection to the last group
            controller.setSelectedNodeGroupIndex(n); // sets the selection to the last group
            controller.clearFacetGroupSelection(); // nothing selected
        }

        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
    }
    
}
