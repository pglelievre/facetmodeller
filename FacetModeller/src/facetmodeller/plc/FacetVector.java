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
    
    public static final int MAX_UNIQUE_ATTRIBUTES = 16; // file reading hardwire

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
    
    /** Combines facet vectors only if the groups match.
     * @param v
     * @param g */
    public void addAll(FacetVector v, Group g) {
        for (int i=0 ; i<v.size() ; i++ ) {
            Facet f = v.get(i);
            if ( f.getGroup() == g ) {
                add(f); // passing through this method makes sure there are no duplicates
            }
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
    
    /** Returns true if any of the facets are marked as being on the boundary.
     * @return */
    public boolean anyMarked() {
        for (int i=0 ; i<size() ; i++ ) {
            if ( get(i).getBoundaryMarker() ) { return true; }
        }
        return false;
    }

    /** Sets all boundary markers to true or false.
     * @param b */
    public void setMarkers(boolean b) {
        for (int i=0 ; i<size() ; i++ ) {
            get(i).setBoundaryMarker(b);
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
        if (info==null) { return null; }
        return info.nodes;
    }
    
    /** Find the facets on the boundary of a triangulated surface.
     * @return The facets found or null if any facets were not triangles.
     */
    public FacetVector findBoundaryFacets() {
        BoundaryInfo info = findBoundaryInfo(false);
        if (info==null) { return null; }
        return info.facets;
    }
    
    private BoundaryInfo findBoundaryInfo(boolean doNodes) {
        
        NodeVector boundaryNodes = new NodeVector(); // will store nodes on the boundary
        FacetVector boundaryFacets = new FacetVector(); // will store facets on the boundary
        
        // Loop over each facet and check for triangular facets:
        for (int i=0 ; i<size() ; i++ ) {
            // Get the ith facet:
            Facet facet = get(i);
            // Get the nodes for the ith facet:
            NodeVector facetNodes = facet.getNodes();
            // Check for a triangular facet:
            int nn = facetNodes.size();
            if ( nn!=3 ) { return null; }
        }
        
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

    // -------------------- Static Public Methods -------------------
    
    public static FacetVector intersection(FacetVector v1,FacetVector v2) {
        // Initialize return object:
        FacetVector fint = new FacetVector();
        // Loop over each facet in v1:
        for ( int i=0 ; i<v1.size() ; i++ ) {
            // Get the ith facet in v1:
            Facet f = v1.get(i);
            // Check if that facet is also in v2:
            if (v2.contains(f)) {
                // Add that facet to the list:
                fint.add(f);
            }
        }
        // Return the result:
        return fint;
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
     * @param startingIndex
     * @param nodes
     * @param ndim
     * @param verbose
     * @return null if user cancels; errmsg!=null if error occurs */
    public ReadFacetsReturnObject readEle(FacetModeller con, String title, File file, int startingIndex, NodeVector nodes, int ndim, boolean verbose) {

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return new ReadFacetsReturnObject("Could not open .ele file for reading."); }

        // Read the header:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) {
            FileUtils.close(reader);
            return new ReadFacetsReturnObject("Problem reading .ele file header.");
        }
        if (textLine.contains("\t")) { return new ReadFacetsReturnObject("Tab character encountered in .ele file."); }
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+",4);
        int nfacets, npf, nat; // number of facets, nodes per facet, attributes
        try {
            nfacets = Integer.parseInt(ss[0].trim()); // converts to integer
            npf  = Integer.parseInt(ss[1].trim()); // converts to integer
            nat  = Integer.parseInt(ss[2].trim()); // converts to integer
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            FileUtils.close(reader);
            return new ReadFacetsReturnObject("Problem reading .ele file header.");
        }
        
        // Check the number of nodes:
        if (nfacets<=0) {
            FileUtils.close(reader);
            return new ReadFacetsReturnObject("Number of facets in .ele file is non-positive.");
        }

        // Check for variable cells:
        boolean isvar = (npf==0);
        boolean loadNonTri = false;
        String prompt;
        if (isvar) {
            if (verbose) {
                if (ndim==3) {
                    prompt = "Do you want to load any non-triangular facets?";
                    int response = Dialogs.question(con,prompt,title);
                    if (response==Dialogs.CANCEL_OPTION) { FileUtils.close(reader); return null; }
                    loadNonTri = ( response == Dialogs.YES_OPTION );
                } else {
                    prompt = "Only line-element facets will be read from the file (any others ignored).";
                    int response = Dialogs.confirm(con,prompt,title);
                    if (response!=Dialogs.OK_OPTION) { FileUtils.close(reader); return null; }
                }
            }
        } else {
            // Check the number of nodes per facet:
            if (npf!=ndim) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("Number of nodes per facet in .ele file is inconsistent with number of dimensions in model.");
            }
        }
        
        // Keep track of unique integer attributes (if they exist):
        ArrayList<Integer> uniqueAttributes = new ArrayList<>();
        boolean doAtts = (nat==1);
        if (doAtts) {
            nat = 1;
        } else {
            nat = 0;
        }

        // Get the number of nodes in the supplied node vector:
        int nnodes = nodes.size();

        // Loop over each facet:
        for (int i=0 ; i<nfacets ; i++ ) {
            
            // Read next line from the file:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("Not enough lines in .ele file.");
            }
            if (textLine.contains("\t")) { return new ReadFacetsReturnObject("Tab character encountered in .ele file."); }
            textLine = textLine.trim();
            
            // Determine the number of nodes for the ith facet:
            if (isvar) {
                try {
                    ss = textLine.split("[ ]+",3);
                    npf = Integer.parseInt(ss[1].trim()); // converts to integer
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    FileUtils.close(reader);
                    return new ReadFacetsReturnObject("Problem in .ele file: number of nodes for variable facet "+(i+1)+".");
                }
                // Check for number of nodes not equal to number of dimensions:
                if ( npf!=ndim && !loadNonTri ) {
                    continue;
                }
            }// else {
            //    npf = ndim;
            //}
            
            // Split line from file:
            if (isvar) {
                ss = textLine.split("[ ]+",npf+nat+3);
            } else {
                ss = textLine.split("[ ]+",npf+nat+2);
            }
            
            // Check the starting index:
            if (i==0) {
                try {
                    int si = Integer.parseInt(ss[0].trim()); // converts to integer
                    if ( si!=0 && si!=1 ) {
                        FileUtils.close(reader);
                        return new ReadFacetsReturnObject("Unexpected starting index in node file (not 0 or 1).");
                    }
                    if ( si != startingIndex ) {
                        FileUtils.close(reader);
                        return new ReadFacetsReturnObject("Unexpected starting index in ele file (does not match the node file).");
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    FileUtils.close(reader);
                    return new ReadFacetsReturnObject("Problem in .ele file: starting index.");
                }
            }
            
            // Read the node indices for the ith facet:
            int[] indices = new int[npf];
            double a=0;
            try {
                int n; // number of values to skip at the left of the current line of the file
                if (isvar) {
                    n=2; // because we want to skip over the facet index and npf value
                } else {
                    n=1; // because we want to skip over the facet index
                }
                // Loop over each expected node index and read them:
                for ( int j=0 ; j<npf ; j++ ) {
                    // Convert split string element to integer:
                    indices[j] = Integer.parseInt(ss[n+j].trim());
                    // Adjust the indices as necessary based on the starting index:
                    if (startingIndex!=0) { // the indices are 1-indexed (or perhaps something else)
                        // Need to subtract the starting index from those indices for referencing into the 0-indexed Java "newNodes" object:
                        indices[j] -= startingIndex;
                    }
                    // Check node indices are consistent with the number of nodes in the supplied node vector:
                    if ( indices[j]<0 || indices[j]>=nnodes ) {
                        FileUtils.close(reader);
                        return new ReadFacetsReturnObject("Inconsistent node index encountered in .ele file.");
                    }
                }
                // Read the attribute:
                if (doAtts) {
                    a = Double.parseDouble(ss[n+npf].trim());
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                FileUtils.close(reader);
                return new ReadFacetsReturnObject("Problem in .ele file: node indices for facet "+(i+1)+".");
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
            }
            
            // Create a new facet object containing the appropriate nodes and add it to the facet vector:
            Facet facet = new Facet(); // section and group membership will be added later
            NodeVector newNodes = new NodeVector();
            for ( int j=0 ; j<npf ; j++ ) {
                newNodes.add(nodes.get(indices[j]));
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
        
        // Check for single unique attribute value:
        //if (uniqueAttributes.size()<=1) {
        //    doAtts = false;
        //    uniqueAttributes.clear();
        //}

        // Close the file and return:
        FileUtils.close(reader);
        return new ReadFacetsReturnObject(doAtts,isvar,uniqueAttributes.size());

    }
    
    @SuppressWarnings("PublicInnerClass")
    public static class ReadFacetsReturnObject {
        
        private boolean doAtts=false;
        private boolean doRem=false;
        private int n=0;
        private String errmsg=null;
        
        //public ReadFacetsReturnObject() {}
        public ReadFacetsReturnObject(boolean ba, boolean br, int na) {
            doAtts = ba;
            doRem = br;
            n = na;
        }
        public ReadFacetsReturnObject(String s) {
            errmsg = s;
        }
        
        public boolean getDoAtts() { return doAtts; }
        public boolean getDoRem() { return doRem; }
        public int getN() { return n; }
        public String getErrmsg() { return errmsg; }
        
    }

}