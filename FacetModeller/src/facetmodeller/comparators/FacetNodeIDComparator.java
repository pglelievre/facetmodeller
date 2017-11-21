package facetmodeller.comparators;

import facetmodeller.plc.Facet;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import java.util.Comparator;

/** Comparator for facet objects that compares the node ID values.
 * @author Peter
 */
public class FacetNodeIDComparator implements Comparator<Facet> {
    @Override
    public int compare(Facet facet1, Facet facet2) {
        
        // First check if the number of nodes is the same:
        int dn = facet1.size() - facet2.size();
        if (dn!=0) { return (int)Math.signum(dn); }
        
        // The number of nodes is the same, so now check the node ID values ...
        
        // Need a comparator object to compare node IDs:
        NodeIDComparator comparator = new NodeIDComparator();
        
        // Make a copy of the nodes for each facet and sort the nodes by ID values:
        NodeVector nodes1 = facet1.getNodes().shallowCopy();
        NodeVector nodes2 = facet2.getNodes().shallowCopy();
        nodes1.sortByIDs();
        nodes2.sortByIDs();
        
        // Loop over each node in the facets:
        for (int i=0 ; i<nodes1.size() ; i++ ) {
            Node n1 = nodes1.get(i);
            Node n2 = nodes2.get(i);
            // Check the ith node ID values for each facet:
            double di = comparator.compare(n1,n2); // = n1.getID() - n2.getID();
            // Return early if non-zero:
            if (di!=0) { return (int)Math.signum(di); }
        }
        
        // If we haven't returned yet, then the two facets must have identical node ID values:
        return 0;
        
    }
}