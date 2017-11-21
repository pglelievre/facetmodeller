package facetmodeller.plc;

import facetmodeller.commands.CommandVector;
import facetmodeller.commands.MoveNodeCommand;
import facetmodeller.comparators.NodeIDComparator;
import facetmodeller.comparators.NodeXYZComparator;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import fileio.FileUtils;
import geometry.MyPoint3D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/** A Vector of Node objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class NodeVector {

    // -------------------- Properties -------------------
    
    public static final int MAX_UNIQUE_ATTRIBUTES = 10; // file reading hardwire

    // Favour composition over inheritence!
    private final ArrayList<Node> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void NodeVector() {}

    // ------------------- Copy ------------------

//    public NodeVector deepCopy() {
//        NodeVector nodes = new NodeVector();
//        for ( int i=0 ; i<size() ; i++ ) {
//            Node n = get(i).deepCopy();
//            nodes.add(n);
//        }
//        return nodes;
//    }
    public NodeVector shallowCopy() {
        NodeVector nodes = new NodeVector();
        nodes.addAll(this);
        return nodes;
    }

    // -------------------- Public Methods -------------------

    /** Clears the vector. */
    public void clear() {
        vector.clear();
    }

    /** Returns the size of the vector.
     * @return The size of the vector.
     */
    public int size() {
        return vector.size();
    }
    
    /** Returns true if the vector has no elements.
     * @return  */
    public boolean isEmpty() {
        return vector.isEmpty();
    }
    
    public int indexOf(Node n) { return vector.indexOf(n); }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public Node get(int i) {
        return vector.get(i);
    }

    /** Returns true if the supplied node is in the list.
     * @param n
     * @return  */
    public boolean contains(Node n) {
        return vector.contains(n);
    }

    /** Returns true if any of the nodes are in the supplied section.
     * @param s
     * @return  */
    public boolean containsSection(Section s) {
        for (int i=0 ; i<size() ; i++ ) {
            if ( get(i).getSection() == s ) { return true; }
        }
        return false;
    }

    /** Adds an element to the end of the vector if not already in the list.
     * @param n
     */
    public void add(Node n) {
        if (vector.contains(n)) { return; }
        vector.add(n);
    }

    /** Adds an element to the end of the vector regardless of whether or not it is already in the list.
     * @param n
     */
    public void addDup(Node n) {
        vector.add(n);
    }
    
    /** Combines node vectors.
     * @param v */
    public void addAll(NodeVector v) {
        //vector.addAll(v.vector);
        for (int i=0 ; i<v.size() ; i++ ) {
            add(v.get(i)); // passing through this method makes sure there are no duplicates
        }
    }

    /** Adds a facet to all the nodes.
     * @param f */
    public void addFacet(Facet f) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).addFacet(f);
        }
    }

    /** Finds the node object in the vector and removes it (if found).
     * @param n
     */
    public void remove(Node n) {
        vector.remove(n);
    }
    
    public void removeLast() {
        vector.remove( size() - 1 );
    }

    /** Removes a facet from any nodes that are linked to it.
     * @param f */
    public void removeFacet(Facet f) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).removeFacet(f);
        }
    }

    /** Removes any nodes that are not part of a facet definition. */
    public void removeUnused() {
        for (int i=(size()-1) ; i>=0 ; i-- ) {
            Node n = get(i);
            if (n.getFacets().isEmpty()) {
                this.remove(n);
            }
        }
    }

    /** Finds a node object in the vector and replaces it with another (if found).
     * @param n1 The node to remove.
     * @param n2 The node to replace the first with.
     * @return The result of indexOf(n1)
     */
    public int replace(Node n1, Node n2) {
        // Find the first node in the list:
        int i1 = vector.indexOf(n1);
        // Check the first node is in the list:
        if (i1>=0) {
            // Remove the first node from the list:
            vector.remove(n1);
            // Add the second node into the same place:
            vector.add(i1,n2);
        }
        return i1;
    }

    /** Removes any zero-length node connections (e.g. edges) by removing one of the identical nodes. */
    public void removeZeroEdges() {
        // Loop until we have checked all the edges:
        int i = 1; // edge counter
        while ( i < size() ) {
            // Get nodes on the current edge:
            Node node1 = vector.get(i-1);
            Node node2 = vector.get(i);
            // Check for identical nodes on the current edge:
            if ( node1 == node2 ) {
                // Remove one of those nodes (doesn't matter which):
                vector.remove(i);
                // Do not increment the counter in this situation because
                // the length of the vector has reduced from the remove operation above.
            } else {
                // Increment the counter:
                i++;
            }
        }
        // Check the first and last nodes:
        int n = size() - 1;
        Node node1 = vector.get(0);
        Node node2 = vector.get(n);
        if ( node1 == node2 ) { vector.remove(n); }
    }

    /** Resets the ID values from 0 to the size of the vector in the order listed. */
    public void resetIDs() {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setID(i);
        }
    }
    
    /** Reverses the order of the nodes. */
    public void reverse() {
        // First copy to a new node vector:
        NodeVector nodes = new NodeVector();
        int n = size();
        for (int i=0 ; i<n ; i++ ) {
            nodes.add(get(i));
        }
        // Now clear and copy back in reverse order:
        clear();
        for (int i=(n-1) ; i>=0 ; i-- ) {
            add(nodes.get(i));
        }
    }

    /** Sorts the nodes based on their ID values. */
    public void sortByIDs() {
        Collections.sort(vector,new NodeIDComparator());
    }

    /** Sorts the nodes based on their coordinates. */
    public void sortByXYZ() {
        Collections.sort(vector,new NodeXYZComparator());
    }

    /** Gets the section memberships for all nodes.
     * @return  */
    public SectionVector getSections() {
        SectionVector sections = new SectionVector();
        for (int i=0 ; i<size() ; i++ ) {
            Section s = get(i).getSection();
            sections.add(s); // duplicates are not added (checked in this call)
        }
        return sections;
    }

    /** Sets the section membership for all nodes.
     * @param section */
    public void setSection(Section section) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setSection(section);
        }
    }

    /** Sets the group membership for all nodes.
     * @param group */
    public void setGroup(Group group) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setGroup(group);
        }
    }

    /** Clears the facets in all nodes in the vector. */
    public void clearFacets() {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).clearFacets();
        }
    }

    /** Finds the closest node to the supplied node in full 3D coordinates.
     * @param n The supplied node.
     * @return The closest node to the supplied node.
     */
    public Node findClosest(Node n) {
        // Check nodes exist:
        if (size()==0) { return null; }
        // Get the 3D location of the supplied node:
        MyPoint3D p = n.getPoint3D();
        if (p==null) { return null; }
        // Loop over each node:
        Node bestNode = get(0); // the best (closest) node so far
        double dbest = bestNode.getPoint3D().distanceToPoint(p); // distance of best (closest) node so far
        for (int i=1 ; i<size() ; i++ ) {
            Node node = get(i);
            if (node==n) { continue; } // skip input node
            double d = node.getPoint3D().distanceToPoint(p);
            if ( d < dbest ) {
                bestNode = node;
                dbest = d;
            }
        }
        // Return the best node:
        return bestNode;
    }
    
    /** Finds the closest node to a supplied point in map-view projection (z ignored).
     * Will crash if the 3D point for the node can not be obtained (e.g. section not calibrated).
     * @param p The supplied point.
     * @return The closest node or null if no nodes exist.
    */
    public Node findClosestXY(MyPoint3D p) {
        // Check nodes exist:
        if (size()==0) { return null; }
        // Loop over each node:
        Node bestNode = get(0); // the best (closest) node so far
        double dbest = bestNode.getPoint3D().distanceToPointXY(p); // distance of best (closest) node so far
        for (int i=1 ; i<size() ; i++ ) {
            Node node = get(i);
            double d = node.getPoint3D().distanceToPointXY(p);
            if ( d < dbest ) {
                bestNode = node;
                dbest = d;
            }
        }
        // Return the best node:
        return bestNode;
    }
    
    /** Returns the minimum coordinates for the set of nodes.
     * @return 
     */
    public MyPoint3D rangeMin() {
        double x=0.0, y=0.0, z=0.0; // don't need to initialize but avoids compiler warning
        boolean ok = false; // set to true as soon as an off section node or node on calibrated section is encountered
        for (int i=0 ; i<size() ; i++ ) {
            MyPoint3D p = get(i).getPoint3D();
            if (p==null) { continue; } // node on uncalibrated section
            if (ok) {
                x = Math.min(x,p.getX());
                y = Math.min(y,p.getY());
                z = Math.min(z,p.getZ());
            } else {
                x = p.getX();
                y = p.getY();
                z = p.getZ();
                ok = true;
            }
        }
        if (ok) {
            return new MyPoint3D(x,y,z);
        } else {
            return null;
        }
    }
    
    /** Returns the maximum coordinates for the set of nodes.
     * @return 
     */
    public MyPoint3D rangeMax() {
        double x=0.0, y=0.0, z=0.0; // don't need to initialize but avoids compiler warning
        boolean ok = false; // set to true as soon as an off section node or node on calibrated section is encountered
        for (int i=0 ; i<size() ; i++ ) {
            MyPoint3D p = get(i).getPoint3D();
            if (p==null) { continue; } // node on uncalibrated section
            if (ok) {
                x = Math.max(x,p.getX());
                y = Math.max(y,p.getY());
                z = Math.max(z,p.getZ());
            } else {
                x = p.getX();
                y = p.getY();
                z = p.getZ();
                ok = true;
            }
        }
        if (ok) {
            return new MyPoint3D(x,y,z);
        } else {
            return null;
        }
    }
    
    /** Snaps nodes to the supplied points if close enough.
     * @param p1 The point to snap to.
     * @param p2 Another point to snap to.
     * @param snappingDistance Snap distance.
     * @param groups Only snap nodes in these groups.
     * @param doH Snap in horizontal direction?
     * @param doV Snap in vertical direction?
     * @return Commands that were executed to change the node positions.
     */
    public CommandVector snapToPoints(MyPoint3D p1, MyPoint3D p2, double snappingDistance, GroupVector groups, boolean doH, boolean doV) {
        CommandVector commands = new CommandVector("");
        double x1 = p1.getX();
        double y1 = p1.getY();
        double z1 = p1.getZ();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double z2 = p2.getZ();
        // Loop over each node:
        for (int i=0 ; i<size() ; i++ ) {
            Node node = get(i);
            // Skip nodes not in the supplied groups:
            if ( groups!=null && !groups.contains(node.getGroup()) ) { continue; }
            // Get the 3D point for the node:
            MyPoint3D p = node.getPoint3D();
            if (p==null) { continue; } // section might not be calibrated
            // Check if the node coordinates are close to the supplied coordinates:
            double x = p.getX();
            double y = p.getY();
            double z = p.getZ();
            if (doH) {
                if ( Math.abs(x-x1) <= snappingDistance ) { x = x1; }
                if ( Math.abs(x-x2) <= snappingDistance ) { x = x2; }
                if ( Math.abs(y-y1) <= snappingDistance ) { y = y1; }
                if ( Math.abs(y-y2) <= snappingDistance ) { y = y2; }
            }
            if (doV) {
                if ( Math.abs(z-z1) <= snappingDistance ) { z = z1; }
                if ( Math.abs(z-z2) <= snappingDistance ) { z = z2; }
            }
            // Change the node coordinates:
            p = new MyPoint3D(x,y,z);
            MoveNodeCommand com = new MoveNodeCommand(node,null,p); com.execute(); // node.setPoint3D(p);
            commands.add(com);
        }
        return commands;
    }

    // -------------------- File I/O -------------------

    /** Reads node points from a .node file and adds them to the node vector.
     * If ndimensions=-2 then reads 2D nodes but ignores 3rd and subsequent columns (so a 2D or 3D file can be used).
     * @param file
     * @param ndimensions
     * @return null if there is a problem reading the file. */
    public ReadNodesReturnObject readNodes(File file, int ndimensions) {
        
        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return null; }

        // Read the header:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { FileUtils.close(reader); return null; }
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+",4);
        int nnodes, ndim, nat; // number of nodes, dimensions, attributes, boundary markers
        try {
            nnodes = Integer.parseInt(ss[0].trim()); // converts to integer
            ndim   = Integer.parseInt(ss[1].trim()); // converts to integer
            nat   = Integer.parseInt(ss[2].trim()); // converts to integer
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { FileUtils.close(reader); return null; }
        
        // Check the number of dimensions:
        if (ndimensions>0) {
            if (ndim!=ndimensions) {
                FileUtils.close(reader);
                return null;
            }
        }
        
        // Keep track of unique integer attributes (if they exist):
        ArrayList<Integer> uniqueAttributes = new ArrayList<>();
        boolean doAtts = (nat==1);
        
        // Loop over each node:
        for (int i=0 ; i<nnodes ; i++ ) {

            // Read the coordinates of the ith node:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { FileUtils.close(reader); return null; }
            double x,y,z,a=0;
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",6);
            try {
                x = Double.parseDouble(ss[1].trim()); // converts to Double
                y = Double.parseDouble(ss[2].trim());
                if (ndim==3) {
                    z = Double.parseDouble(ss[3].trim());
                    if (doAtts) {
                        a = Double.parseDouble(ss[4].trim());
                    }
                } else {
                    z = y;
                    y = 0.0;
                    if (doAtts) {
                        a = Double.parseDouble(ss[3].trim());
                    }
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { FileUtils.close(reader); return null; }

            // Check for integer attributes:
            int att=0;
            if (doAtts) {
                att = (int)a;
                if ( att == a ) { // attribute is integer valued
                    if (!uniqueAttributes.contains(att)) {
                        uniqueAttributes.add(att);
                        if (uniqueAttributes.size()>MAX_UNIQUE_ATTRIBUTES) {
                            doAtts = false;
                            uniqueAttributes.clear();
                        }
                    }
                } else { // attribute is not integer valued
                    doAtts = false;
                    uniqueAttributes.clear();
                }
            }
            
            // Add a new node to the vector:
            Node node = new NodeOffSection(x,y,z);
            vector.add(node);
            // (section and group membership will be added later)
            
            // Set the node ID to the index of the unique attribute (for use later, outside of this method):
            if (doAtts) {
                int k = uniqueAttributes.indexOf(att);
                node.setID(k);
            }
            
        }

        // Close the file and return:
        FileUtils.close(reader);
        ReadNodesReturnObject obj = new ReadNodesReturnObject();
        obj.setDoAtts(doAtts);
        obj.setN( uniqueAttributes.size() );
        return obj;

    }
    
    @SuppressWarnings("PublicInnerClass")
    public static class ReadNodesReturnObject {
        private boolean doAtts;
        private int n;
        public void ReadNodesReturnObject() {}
        private void setDoAtts(boolean doAtts) { // not sure why but netbeans won't let me set doAtts in the constructor
            this.doAtts = doAtts;
        }
        private void setN(int n) { this.n = n; }
        public boolean doAtts() { return doAtts; }
        public int getN() { return n; }
    }

}