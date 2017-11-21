package facetmodeller.comparators;

import facetmodeller.plc.Node;
import geometry.MyPoint3D;
import java.util.Comparator;

/** Comparator for Node objects that compares their (x,y,z) coordinates.
 * @author Peter
 */
public class NodeXYZComparator implements Comparator<Node> {
    @Override
    public int compare(Node node1, Node node2) {
        
        // Get the coordinates of each node:
        MyPoint3D p1 = node1.getPoint3D();
        MyPoint3D p2 = node2.getPoint3D();
        
        // First check the x location:
        double x1 = p1.getX();
        double x2 = p2.getX();
        double dx = x1 - x2;
        if (dx!=0) { return (int)Math.signum(dx); }
        // If x1<x2 then d<0.
        // If x1=x2 then d=0.
        // If x1>x2 then d>0.
        
        // Now check the y location:
        double y1 = p1.getY();
        double y2 = p2.getY();
        double dy = y1 - y2;
        if (dy!=0) { return (int)Math.signum(dy); }
        
        // Finally check the z location:
        double z1 = p1.getZ();
        double z2 = p2.getZ();
        double dz = z1 - z2;
        return (int)Math.signum(dz);
        
    }
    
}
