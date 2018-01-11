package facetmodeller.plc;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.comparators.FacetNodeIDComparator;
import facetmodeller.groups.Group;
//import facetmodeller.sections.Section;
import fileio.FileUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/** A Vector of Facet objects.
 * Many of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class FacetVector {

    // -------------------- Properties -------------------
    
    public static final int MAX_UNIQUE_ATTRIBUTES = 10; // file reading hardwire

    // Favour composition over inheritence!
    private ArrayList<Facet> vector = new ArrayList<>();

    // ------------------- Constructor ------------------

    public void FacetVector() {}

    // ------------------- Copy Methods ------------------

    // Returns a new FacetVector object with copied Facets that link to existing nodes.
    public FacetVector deepCopy() {
        FacetVector facets = new FacetVector();
        for ( int i=0 ; i<size() ; i++ ) {
            Facet f = get(i).copy(); // A new Facet object with links to existing nodes.
            facets.add(f);
        }
        return facets;
    }
//    public FacetVector shallowCopy() {
//        FacetVector facets = new FacetVector();
//        facets.addAll(this);
//        return facets;
//    }

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
    
    public int indexOf(Facet f) { return vector.indexOf(f); }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public Facet get(int i) {
        return vector.get(i);
    }

    /** Returns true if the supplied facet is in the list.
     * @param f
     * @return  */
    public boolean contains(Facet f) {
        return vector.contains(f);
    }

    /** Adds an element to the end of the vector if not already in the list.
     * @param f The Facet object to add to the end of the vector.
     */
    public void add(Facet f) {
        if (contains(f)) { return; }
        vector.add(f);
    }
    
    /** Combines facet vectors.
     * @param v */
    public void addAll(FacetVector v) {
        //vector.addAll(v.vector);
        for (int i=0 ; i<v.size() ; i++ ) {
            add(v.get(i)); // passing through this method makes sure there are no duplicates
        }
    }

    /* Returns true if the vector contains the supplied facet.
     * @param p The supplied facet.
    public boolean contains(Facet f) {
        return vector.contains(f);
    }
     */

//    /** Removes the ith facet in the list. */
//    public void remove(int i) {
//        vector.remove(i);
//    }

    /** Finds the facet object in the vector and removes it (if found).
     * @param f The facet object to remove.
     */
    public void remove(Facet f) {
        vector.remove(f);
    }

    /** Resets the ID values from 0 to the size of the vector in the order listed. */
    public void resetIDs() {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setID(i);
        }
    }

    /** Sorts the facets based on their node ID values. */
    public void sortByNodeIDs() {
        Collections.sort(vector,new FacetNodeIDComparator());
    }

//    /** Adds a section to the section membership for all facets.
//     * @param section */
//    public void addSection(Section section) {
//        for (int i=0 ; i<size() ; i++ ) {
//            get(i).addSection(section);
//        }
//    }

    /** Sets the group membership for all facets.
     * @param group */
    public void setGroup(Group group) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setGroup(group);
        }
    }

//    /** Finds the closest facet centroid to a supplied point.
//     * @param p The supplied point.
//     * @return The index of the facet with closest centroid or -1 if no facets exist.
//     */
//    public int findClosest(MyPoint2D p) {
//
//        // Check facets exist:
//        if (size()==0) { return -1; }
//
//        // Loop over each facet:
//        int ibest = 0; // index of best (closest) coordinate so far
//        double dbest = get(0).distanceToPoint(p); // distance of best (closest) coordinate so far
//        for (int i=1 ; i<size() ; i++ ) {
//            double d = get(i).distanceToPoint(p);
//            if ( d < dbest ) {
//                ibest = i;
//                dbest = d;
//            }
//        }
//
//        // Return the index of the closest node:
//        return ibest;
//
//    }
    
    /** Find the nodes on the boundary of a triangulated surface.
     * @return The nodes found or null if any facets were not triangles.
     */
    public NodeVector findBoundaryNodes() {
        BoundaryInfo info = findBoundaryInfo(true);
        return info.nodes;
    }
    
    /** Find the facets on the boundary of a triangulated surface.
     * @return The facets found or null if any facets were not triangles.
     */
    public FacetVector findBoundaryFacets() {
        BoundaryInfo info = findBoundaryInfo(false);
        return info.facets;
    }
    
    private BoundaryInfo findBoundaryInfo(boolean doNodes) {
        
        NodeVector boundaryNodes = new NodeVector(); // will store nodes on the boundary
        FacetVector boundaryFacets = new FacetVector(); // will store facets on the boundary
        
        // Loop over each facet:
        for (int i=0 ; i<size() ; i++ ) {
            
            // Get the ith facet:
            Facet facet = get(i);
            // Get the nodes for the ith facet:
            NodeVector facetNodes = facet.getNodes();
            // Check for a triangular facet:
            int nn = facetNodes.size();
            if ( nn!=3 ) { return null; }
            
            // Loop over each edge in the ith facet (equivalent to a loop over each node):
            for (int j=0 ; j<nn ; j++ ) {
                // (the logic here assumes triangular surface facets)
                
                // Get the current pair of nodes on the jth edge:
                Node node1 = facetNodes.get(j);
                Node node2;
                if ((j+1)==nn) {
                    node2 = facetNodes.get(0);
                } else {
                    node2 = facetNodes.get(j+1);
                }
                
                // Get the facets for each of those nodes (these are all candidate neighbouring facets):
                FacetVector nodeFacets1 = node1.getFacets();
                FacetVector nodeFacets2 = node2.getFacets();
                
                // Count the facets shared by each of those nodes:
                int nf = 1; // counter for shared facets (initialized to 1 because they both share the ith facet)
                for (int k=0 ; k<nodeFacets1.size() ; k++ ) { // loop over each facet for the first node in the pair
                    Facet f = nodeFacets1.get(k);
                    if (f==facet) { continue; } // the kth facet is the ith facet (already dealt with by initializing nf to 1)
                    if (!contains(f)) { continue; } // the kth facet is not in the supplied list of facets
                    if (!nodeFacets2.contains(f)) { continue; } // the kth facet is not shared by each node in the pair
                    // Increment the counter of shared facets:
                    nf++;
                }
                
                // Check for a boundary edge:
                if (nf==1) { // the only shared edge of the pair of nodes is the ith facet
                    if (doNodes) {
                        // Add the pair of nodes to the list of boundary nodes:
                        boundaryNodes.add(node1);
                        boundaryNodes.add(node2);
                    } else {
                        // Add the facet to the list of boundary facets:
                        boundaryFacets.add(facet);
                        break; // from for j
                    }
                }
                
            } // for j
            
        } // for i
        
        // Return the boundary information:
        BoundaryInfo info = new BoundaryInfo();
        if (doNodes) {
            info.nodes = boundaryNodes;
        } else {
            info.facets = boundaryFacets;
        }
        return info;
        
    }
    private class BoundaryInfo {
        public NodeVector nodes;
        public FacetVector facets;
    }

    // -------------------- File I/O -------------------

//    public boolean writeIDs(BufferedWriter writer) {
//        String textLine = Integer.toString(size());
//        for (int i=0 ; i<size() ; i++ ) {
//            textLine = textLine + " " + get(i).getID();
//        }
//        if (!FileUtils.writeLine(writer,textLine)) { return false; }
//        return true;
//    }
//
//    public boolean readIDs(BufferedReader reader) {
//        String textLine = FileUtils.readLine(reader);
//        if (textLine==null) { return false; }
//        textLine = textLine.trim();
//        String[] ss = textLine.split("[ ]+");
//        clear();
//        int n;
//        try {
//            n = Integer.parseInt(ss[0].trim()); // converts to integer
//        } catch (NumberFormatException e) {
//            return false;
//        }
//        for (int i=1 ; i<n ; i++ ) {
//            String s = ss[i];
//            if (s.trim().length()==0) { continue; }
//            int id;
//            try {
//                id = Integer.parseInt(ss[i].trim()); // converts to integer
//            } catch (NumberFormatException e) {
//                return false;
//            }
//            Facet f = new Facet();
//            f.setID(id);
//        }
//        return true;
//    }

    // -------------------- File I/O -------------------

    /** Reads facet definitions from a .ele file and adds them to the facet vector.
     * @param con
     * @param title
     * @param file
     * @param nodes
     * @param ndim
     * @param verbose
     * @return  */
    public ReadFacetsReturnObject readEle(FacetModeller con, String title, File file, NodeVector nodes, int ndim, boolean verbose) {

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return new ReadFacetsReturnObject("could not open file for reading."); }

        // Read the header:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { FileUtils.close(reader); return new ReadFacetsReturnObject("problem reading header."); }
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+",4);
        int nfacets, npf, nat; // number of facets, nodes per facet, attributes
        try {
            nfacets = Integer.parseInt(ss[0].trim()); // converts to integer
            npf  = Integer.parseInt(ss[1].trim()); // converts to integer
            nat  = Integer.parseInt(ss[2].trim()); // converts to integer
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            FileUtils.close(reader);
            return new ReadFacetsReturnObject("problem reading header.");
        }

        // Check for variable cells:
        boolean isvar = (npf==0);
        String prompt;
        if (isvar) {
            if (verbose) {
                if (ndim==3) {
                    prompt = "Only triangular facets will be read from the file (all others ignored).";
                } else {
                    prompt = "Only line-element facets will be read from the file (all others ignored).";
                }
                int response = Dialogs.confirm(con,prompt,title);
                if (response!=Dialogs.OK_OPTION) { FileUtils.close(reader); return null; }
            }
        } else {
            // Check the number of nodes per facet:
            if (npf!=ndim) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("incorrect number of nodes per facet.");
            }
        }
        
        // Keep track of unique integer attributes (if they exist):
        ArrayList<Integer> uniqueAttributes = new ArrayList<>();
        boolean doAtts = (nat==1);

        // Get the number of nodes in the supplied node vector:
        int nnodes = nodes.size();

        // Loop over each facet:
        for (int i=0 ; i<nfacets ; i++ ) {

            // Read the node indices for the ith facet:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("problem reading node indices for facet"+(i+1)+".");
            }
            int i1,i2,i3=1; // dummy i3 value lets the node index check pass for ndim=2
            double a=0;
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",7);
            try {
                int n=0;
                if (isvar) {
                    npf = Integer.parseInt(ss[1].trim()); // converts to integer
                    if (npf!=ndim) {
                        continue;
                    }
                    n=1;
                }
                i1 = Integer.parseInt(ss[n+1].trim()); // converts to integer
                i2 = Integer.parseInt(ss[n+2].trim());
                if (ndim==3) {
                    i3 = Integer.parseInt(ss[n+3].trim());
                    if (doAtts) {
                        a = Double.parseDouble(ss[n+4].trim());
                    }
                } else {
                    if (doAtts) {
                        a = Double.parseDouble(ss[n+3].trim());
                    }
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("problem reading node indices for facet"+(i+1)+".");
            }

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
                // Check for single unique attribute value:
                if (uniqueAttributes.size()<=1) {
                    doAtts = false;
                    uniqueAttributes.clear();
                }
            }

            // Check node indices are consistent with the number of nodes in the supplied node vector:
            if ( i1<1 || i2<1 || i3<1 || i1>nnodes || i2>nnodes || i3>nnodes ) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("inconsistent node indices encountered.");
            }

            // Need to subtract 1 from those indices for referencing into the nodes vector:
            i1 -= 1;
            i2 -= 1;
            i3 -= 1;
            
            // Create a new facet object containing the appropriate nodes and add it to the facet vector:
            Facet facet = new Facet(); // section and group membership will be added later
            NodeVector newNodes = new NodeVector();
            newNodes.add(nodes.get(i1));
            newNodes.add(nodes.get(i2));
            if (ndim==3) {
                newNodes.add(nodes.get(i3));
            }
            facet.addNodes(newNodes);
            vector.add(facet);
            
            // Link the facet to the nodes (this is required for some processing outside of this method so may as well do it now:
            newNodes.addFacet(facet);
            
            // Set the new facet ID to the index of the unique attribute (for use later, outside of this method):
            if (doAtts) {
                int k = uniqueAttributes.indexOf(att);
                facet.setID(k);
                // Add the facet to the new nodes:
                newNodes.addFacet(facet);
            }
            
        }

        // Close the file and return:
        FileUtils.close(reader);
        ReadFacetsReturnObject obj = new ReadFacetsReturnObject(doAtts,isvar,uniqueAttributes.size());
        //obj.setDoAtts(doAtts);
        //obj.setN( uniqueAttributes.size() );
        return obj;

    }
    
    @SuppressWarnings("PublicInnerClass")
    public static class ReadFacetsReturnObject {
        
        private boolean doAtts;
        private boolean doRem;
        private int n;
        private String errmsg;
        
        //public ReadFacetsReturnObject() {}
        public ReadFacetsReturnObject(boolean ba, boolean br, int i) {
            doAtts = ba;
            doRem = br;
            n = i;
            errmsg = null;
        }
        public ReadFacetsReturnObject(String s) {
            doAtts = false;
            doRem = false;
            n = 0;
            errmsg = s;
        }
        
        //private void setDoAtts(boolean doAtts) { // not sure why but netbeans won't let me set doAtts in the constructor
        //    this.doAtts = doAtts;
        //}
        //private void setN(int n) { this.n = n; }
        
        public boolean getDoAtts() { return doAtts; }
        public boolean getDoRem() { return doRem; }
        public int getN() { return n; }
        public String getErrmsg() { return errmsg; }
        
    }

}