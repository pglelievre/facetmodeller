package facetmodeller.comparators;

import facetmodeller.plc.Node;
import java.util.Comparator;

/** Comparator for node objects that compares the ID values.
 * @author Peter
 */
public class NodeIDComparator implements Comparator<Node> {
   @Override
   public int compare(Node n1, Node n2) {
        int di = n1.getID() - n2.getID();
        return (int)Math.signum(di);
        // If n1<n2 then d<0.
        // If n1=n2 then d=0.
        // If n1>n2 then d>0.
    }
}
