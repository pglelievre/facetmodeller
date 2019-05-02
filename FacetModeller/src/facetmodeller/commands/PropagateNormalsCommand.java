package facetmodeller.commands;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.clicktasks.ClickTaskUtil;
import facetmodeller.groups.Group;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

/** Command to propagate facet node ordering, and therefore normal vector orientation, outwards from a selected facet.
 * Propagation occurs across triangular facet neighbours in the same facet group.
 * @author Peter
 */
public final class PropagateNormalsCommand extends ControlledCommand {
    
    private final Facet initialFacet; // the initial facet
    private final FacetVector facetsReordered = new FacetVector(); // the facets reordered
    private boolean success = true;
    
    public PropagateNormalsCommand(FacetModeller con, Facet f) {
        super(con,ClickTaskUtil.PROPAGATE_NORMALS_TITLE);
        initialFacet = f;
    }
    
    @Override
    public void execute() {
        // This code translated from Fortran code ugrid_march_normals although some alterations have been made.
        
        // Check the initial facet is triangular if using the 3D version of the GUI (or a line element for the 2D version):
        int ndim = controller.numberOfDimensions();
        if ( initialFacet.size() != ndim ) {
            Dialogs.error(controller,"Propagation can only begin from a triangular facet.",ClickTaskUtil.PROPAGATE_NORMALS_TITLE);
            return;
        }
        
        // Define working variables:
        FacetVector visited = new FacetVector(); // lists the facets we've already looked at
        FacetVector front = new FacetVector(); // lists the facets in the marching front
        FacetVector front2 = new FacetVector(); // a working object for creating the next front
        
        // Add the initial facet to the visited and front lists:
        visited.add(initialFacet);
        front.add(initialFacet);
        
        // Inform user that this may take a while:
        Dialogs.inform(controller,"This may take a while for large models. Please wait for the confirmation dialog before continuing.",ClickTaskUtil.PROPAGATE_NORMALS_TITLE);
        
        // Loop until finished:
        for ( int k=0 ; k<controller.numberOfFacets() ; k++ ) { // this is the maximum possible number of times we'd have to loop but we'll skip out early
         
            // Loop over all the facets in the current front:
            for ( int i=0 ; i<front.size() ; i++ ) {
                
                // Sanity check:
                Facet fi = front.get(i);
                if (!visited.contains(fi)) {
                    Dialogs.error(controller,ClickTaskUtil.PROPAGATE_NORMALS_TITLE,"Propagation failed (1).");
                    return;
                }
                
                // Find the facets neighbouring the ith facet in the front within the same group:
                FacetVector neighbours = facetNeighbours(fi);
                if (!success) {
                    Dialogs.error(controller,ClickTaskUtil.PROPAGATE_NORMALS_TITLE,"Propagation failed (2).");
                    return;
                }
                if (neighbours==null) { continue; }
                if (neighbours.isEmpty()) { continue; }
                
                // Loop over each neighbour of the ith facet in the current front:
                for ( int j=0 ; j<neighbours.size() ; j++ ) {
                    
                    Facet fj = neighbours.get(j); // jth neighbour of the ith facet in the current front
                    
                    // Skip neighbouring facets already visited:
                    if (visited.contains(fj)) { continue; } // to next iteration of for j loop
                    
                    // Mark the neighbouring facet visited:
                    visited.add(fj);
                    
                    // Check if the jth neighbour is an appropriate facet (correct number of nodes):
                    if ( fj.size() != ndim ) { continue; } // to next iteration of for j loop
                    
                    // Check if the jth neighbour is in the same group as the ith facet:
                    // (this is no longer necessary given the code in facetNeighbours checks the group)
                    //Group gj = fj.getGroup();
                    //if ( gj != group ) { continue; } // to next iteration of for j loop
                    
                    // Add the jth neighbour to the new front if not already in it:
                    front2.add(fj);
                    
                    // Reorder the jth neighbour if required:
                    if ( reorderFacet(fi,fj) ) {
                        if (!success) {
                            Dialogs.error(controller,ClickTaskUtil.PROPAGATE_NORMALS_TITLE,"Propagation failed (3).");
                            return;
                        }
                        fj.reverse();
                        facetsReordered.add(fj);
                    }
                    
                } // end for j loop
                
            } // end for i loop
         
            // Reset the front to the new front:
            front.clear();
            front.addAll(front2);
            front2.clear();
            
            // Check if we are done:
            if ( visited.size() >= controller.numberOfFacets() ) { return; }
            if ( front.isEmpty() ) { return; }
            
        } // end for k loop
        
        // Inform user that this task finished successfully:
        Dialogs.inform(controller,"Finished.",ClickTaskUtil.PROPAGATE_NORMALS_TITLE);
        
    }
    
    // Finds all neighbouring facets across the faces of a specified facet, but the groups have to match.
    private FacetVector facetNeighbours(Facet facet) {
        
        // Initialize return object:
        FacetVector neighbours = new FacetVector();
        
        // Check the specified facet is the correct type:
        int ndim = controller.numberOfDimensions();
        if ( facet.size() != ndim ) { return null; }
        
        // Get the nodes for the specified facet:
        NodeVector nodes = facet.getNodes();
        
        // Get the group for the specified facet:
        Group group = facet.getGroup();
        
        // For 2D this is simple, for 3D this is more complicated:
        if (controller.is2D()) {
            
            // Find any facets connected to any of those nodes:
            for ( int j=0 ; j<nodes.size() ; j++ ) {
                Node nj = nodes.get(j); // the jth node associated with the specified facet
                FacetVector fv = nj.getFacets(); // the facets associated with the jth node
                neighbours.addAll(fv,group);
            }
            
        } else { // 3D
            
            // Loop over each face of the specified facet:
            for ( int j=0 ; j<3 ; j++ ) {
                
                // Get the two nodes associated with the jth face of the specified facet:
                NodeVector face = new NodeVector();
                for ( int k=0 ; k<3 ; k++ ) { // loop over the nodes in the facet
                    if (k==j) { continue; } // skip the node opposite the current face
                    Node nk = nodes.get(k);
                    face.add(nk);
                }
                if (face.size()!=2) {
                    success = false;
                    return null;
                }
                
                // Find facets associated with that face (the two nodes found above):
                FacetVector fv0 = face.get(0).getFacets(); // facets associated with first node in the face
                FacetVector fv1 = face.get(1).getFacets(); // facets associated with second node in the face
                FacetVector neighj = FacetVector.intersection(fv0,fv1); // facets associated with both nodes in the face
                
                // Add those neighbouring facets to the list of neighbours (if the groups match):
                neighbours.addAll(neighj,group);
                
            }
            
        }
            
        // Remove the specified facet from the list of neighbours and return:
        neighbours.remove(facet);
        return neighbours;
        
    }
    
    // Checks if facet f2 needs to be reordered when compared to the ordering in facet i1.
    private boolean reorderFacet(Facet f1, Facet f2) {
        
        // Get the node(s) shared by the two facets ...
        NodeVector sharedNodes = Facet.sharedNodes(f1,f2);
        
        // Skip if there are not the correct number of shared nodes:
        int ndim = controller.numberOfDimensions();
        if ( sharedNodes.size() != ndim-1 ) {
            success = false;
            return false;
        }
        Node shared0 = sharedNodes.get(0);
        
        // Get the nodes for f1 and f2:
        NodeVector nodes1 = f1.getNodes();
        NodeVector nodes2 = f2.getNodes();
        
        // Find the indices of the shared node(s) in the definitions for each facet:
        int dc,dn;
        if (ndim==3) {
            Node shared1 = sharedNodes.get(1);
            int j0 = nodes1.indexOf(shared0);
            int j1 = nodes1.indexOf(shared1);
            if ( j0<0 || j1<0 ) {
                success = false;
                return false;
            }
            dc = j1 - j0; // will not work as required if they are (0,2) or (2,0) so have to to the following adjustment in that case
            if (Math.abs(dc)>1) { dc = (int)Math.signum(-dc); }
            j0 = nodes2.indexOf(shared0);
            j1 = nodes2.indexOf(shared1);
            if ( j0<0 || j1<0 ) {
                success = false;
                return false;
            }
            dn = j1 - j0; // will not work as required if they are (0,2) or (2,0) so have to to the following adjustment in that case
            if (Math.abs(dn)>1) { dn = (int)Math.signum(-dn); }
            if ( Math.abs(dc)!=1 || Math.abs(dn)!=1 ) {
                success = false;
                return false;
            }
            // (the values calculated above should be opposite sign, otherwise the neighbour needs to change)
        } else {
            dc = nodes1.indexOf( shared0 ); // =0 or =1
            dn = nodes2.indexOf( shared0 ); // =0 or =1
            if ( dc<0 || dn<0 ) {
                success = false;
                return false;
            }
            // (if the values calculated above are equal then the neighbour needs to change)
        }
        // Check if the ordering needs to change for the neighbour:
        return dc==dn;
        
    }
   
    @Override
    public void undo() {
        // Reverse the ordering for the affected facets:
        for ( int i=0 ; i<facetsReordered.size() ; i++ ) {
            Facet f = facetsReordered.get(i);
            f.reverse();
        }
    }
    
}
