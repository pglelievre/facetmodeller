package facetmodeller.plc;

import facetmodeller.VOI;
import facetmodeller.commands.CommandVector;
import facetmodeller.commands.DuplicateNodeInfo;
import facetmodeller.commands.MoveNodeCommand;
import facetmodeller.comparators.FacetNodeIDComparator;
import facetmodeller.comparators.NodeXYZComparator;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.SceneInfo;
import fileio.FileUtils;
import geometry.Dir3D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.io.*;

/** A piecewise linear complex.
 * @author Peter Lelievre
 */
public class PLC {

    // -------------------- Properties -------------------

    private static final double TOLZERO = 1.0E-9; // if any node coordinates are closer than this to zero then they are written as zero
    
    private final NodeVector nodes = new NodeVector(); // list of nodes
    private final FacetVector facets = new FacetVector(); // list of facets
    private final RegionVector regions = new RegionVector(); // list of regions
    //private String header = "# Poly file written by FacetModeller (Java implementation)";

    // ------------------- Constructor ------------------

    public void PLC() {}
    
    // -------------------- Copy -------------------
    
//    public PLC deepCopy() {
//        PLC p = new PLC();
//        p.setNodes( nodes.deepCopy() );
//        p.setFacets( facets.deepCopy() );
//        p.setRegions( regions.deepCopy() );
//        return p;
//    }
//    public PLC shallowCopy() {
//        PLC p = new PLC();
//        p.setNodes( nodes.shallowCopy() );
//        p.setFacets( facets.shallowCopy() );
//        p.setRegions( regions.shallowCopy() );
//        return p;
//    }

    // -------------------- Checkers -------------------

    public boolean isEmpty() {
        return ( nodes.isEmpty() && facets.isEmpty() && regions.isEmpty() );
    }

    // -------------------- Finders -------------------
    
    public int indexOfNode(Node n) { return nodes.indexOf(n); }
    public int indexOfFacet(Facet f) { return facets.indexOf(f); }
    public int indexOfRegion(Region r) { return regions.indexOf(r); }
    public boolean containsNode(Node n) { return nodes.contains(n); }
    
    // -------------------- Getters -------------------

    public NodeVector getNodes() { return nodes; }
    public FacetVector getFacets() { return facets; }
    public RegionVector getRegions() { return regions; }

    public Node getNode(int i) {
        return nodes.get(i);
    }

    public Facet getFacet(int i) {
        return facets.get(i);
    }

    public Region getRegion(int i) {
        return regions.get(i);
    }
    
    // -------------------- Private Methods -------------------
    
//    public void setNodes(NodeVector n) { this.nodes = n; }
//    public void setFacets(FacetVector f) { this.facets = f; }
//    public void setRegions(RegionVector r) { this.regions = r; }
    
    // -------------------- Public Methods -------------------

    public boolean hasNodes() { return !nodes.isEmpty(); }
    public boolean hasFacets() { return !facets.isEmpty(); }
    public boolean hasRegions() { return !regions.isEmpty(); }
    public int numberOfNodes() { return nodes.size(); }
    public int numberOfNodesInFacet(int i) { return getFacet(i).size(); }
    public int numberOfFacets() { return facets.size(); }
    public int numberOfRegions() { return regions.size(); }
    public int numberOfRegionPoints() { return regions.numberOfRegionPoints(); }
    public int numberOfControlPoints() { return regions.numberOfControlPoints(); }

    public void clear() {
        nodes.clear();
        facets.clear();
        regions.clear();
    }
    
    /** Combines PLCs.
     * @param p */
    public void addAll(PLC p) {
        nodes.addAll(p.getNodes());
        facets.addAll(p.getFacets());
        regions.addAll(p.getRegions());
    }

    public void addNode(Node n) {
        nodes.add(n);
    }
    public void addNodes(NodeVector n) {
        nodes.addAll(n);
    }

    public void addFacet(Facet f) {
        facets.add(f);
    }
    public void addFacets(FacetVector f) {
        facets.addAll(f);
    }

    public void addRegion(Region r) {
        regions.add(r);
    }

    public void removeNode(Node n) {
        nodes.remove(n);
    }

    public void removeFacet(Facet f) {
        facets.remove(f);
    }

    public void removeRegion(Region r) {
        regions.remove(r);
    }

    public void resetIDs() {
        nodes.resetIDs();
        facets.resetIDs();
        regions.resetIDs();
    }
    
    public Node findClosestNode(Node node) {
        return nodes.findClosest(node);
    }
    
    public SceneInfo getSceneInfo(MyPoint3D origin) {
    
        // Check for empty PLC:
        if (numberOfNodes()==0) { return null; }
        
        // Calculate x,y,z coordinate ranges for all node and region vertices in the plc:
        MyPoint3D p0 = origin;
        MyPoint3D p = getNode(0).getPoint3D();
        MyPoint3D p1 = p.deepCopy();
        MyPoint3D p2 = p.deepCopy();
        double r = 0.0; // will hold largest node distance from origin
        if (p0!=null) {
            r = p0.distanceToPoint(p);
        }
        for (int i=1 ; i<numberOfNodes() ; i++ ) {
            p = getNode(i).getPoint3D();
            if (p==null) { continue; } // if the node's section is not calibrated
            p1.min(p);
            p2.max(p);
            if (p0!=null) {
                r = Math.max( r , p0.distanceToPoint(p) );
            }
        }
        for (int i=0 ; i<numberOfRegions() ; i++ ) {
            p = getRegion(i).getPoint3D();
            if (p==null) { continue; } // if the region's section is not calibrated
            p1.min(p);
            p2.max(p);
            if (p0!=null) {
                r = Math.max( r , p0.distanceToPoint(p) );
            }
        }
        
        // Calculate the centroid and width of the coordinate ranges found above:
        SceneInfo info = new SceneInfo();
        if (p0==null) {
            // Set centroid to middle of node/region range:
            p0 = MyPoint3D.plus(p1,p2);
            p0.times(0.5);
        }
        info.setOrigin(p0);
        p = MyPoint3D.minus(p2,p1);
        info.setDimensions(p);
        if ( origin==null || r==0.0 ) {
            r = p.max();
        } else {
            r *= 2.0;
        }
        if (r==0.0) {
            info.setScaling(0.0);
        } else {            
            info.setScaling(1.0d/r);
        }
        
        // Recalculate the scaling if required:
        if (origin!=null) { return info; }
        r = 0.0;
        for (int i=0 ; i<numberOfNodes() ; i++ ) {
            p = getNode(i).getPoint3D();
            if (p==null) { continue; }
            r = Math.max( r , p0.distanceToPoint(p) );
        }
        for (int i=0 ; i<numberOfRegions() ; i++ ) {
            p = getRegion(i).getPoint3D();
            if (p==null) { continue; }
            r = Math.max( r , p0.distanceToPoint(p) );
        }
        r *= 2.0;
        if (r==0.0) {
            info.setScaling(0.0);
        } else {
            info.setScaling(1.0d/r);
        }
        
        // Return:
        return info;
        
    }
    
    /** Snaps points to VOI.
     * @param voi The VOI to snap to.
     * @param snappingDistance Snap distance.
     * @param groups Only snap nodes in these groups.
     * @param doH Snap in horizontal direction?
     * @param doV Snap in vertical direction?
     * @return Commands that were executed to change the node positions.
     */
    public CommandVector snapToVOI(VOI voi, double snappingDistance, GroupVector groups, boolean doH, boolean doV) {
        MyPoint3D p1 = voi.getMinimumLimits();
        MyPoint3D p2 = voi.getMaximumLimits();
        return nodes.snapToPoints(p1,p2,snappingDistance,groups,doH,doV);
    }
    
    /** Nodes are moved onto the closest grid points.
     * @param m Grid spacing (assumes the grid points start at 0, so m is like a modulus distance)
     * @param groups Only snap nodes in these groups.
     * @param doH Snap in horizontal direction?
     * @param doV Snap in vertical direction?
     * @return Commands that were executed to change the node positions.
     */
    public CommandVector snapToGrid(double m, GroupVector groups, boolean doH, boolean doV) {
        CommandVector commands = new CommandVector("");
        // Loop over each node in the PLC:
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node node = nodes.get(i);
            // Skip nodes not in the supplied groups:
            if ( groups!=null && !groups.contains(node.getGroup()) ) { continue; }
            // Get the 3D point for the node:
            MyPoint3D p = node.getPoint3D();
            if (p==null) { continue; } // section might not be calibrated
            // Calculate the closest grid point:
            double x = p.getX();
            double y = p.getY();
            double z = p.getZ();
            if (doH) {
                x = Math.round(x/m)*m;
                y = Math.round(y/m)*m;
            }
            if (doV) {
                z = Math.round(z/m)*m;
            }
            // Change the node coordinates:
            p = new MyPoint3D(x,y,z);
            MoveNodeCommand com = new MoveNodeCommand(node,null,p); com.execute(); // node.setPoint3D(p);
            commands.add(com);
        }
        return commands;
    }
    
    /** Translates all nodes.
     * @param t The translation to apply.
     * @param groups Only translate nodes in these groups.
     * @return Commands that were executed to change the node positions.
     */
    public CommandVector translate(MyPoint3D t, GroupVector groups) {
        CommandVector commands = new CommandVector("");
        // Loop over each node in the PLC:
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node node = nodes.get(i);
            // Skip nodes not in the supplied groups:
            if ( groups!=null && !groups.contains(node.getGroup()) ) { continue; }
            // Get the 3D point for the node:
            MyPoint3D p = node.getPoint3D();
            if (p==null) { continue; } // section might not be calibrated
            // Shift the node:
            p.plus(t);
            MoveNodeCommand com = new MoveNodeCommand(node,null,p); com.execute(); // node.setPoint3D(p);
        }
        return commands;
    }
    
    // -------------------- DataDoctor -------------------

    /** Finds any "bad" facets:
     * - duplicate node indices
     * - less than nDimensions unique nodes
     * - duplicate facets
     * - linear polygonal (n>3) facets
     * - non-planar facets
     * - intersecting facets NOT implemented yet
     * @param nDimensions
     * @return 
     */
    public FacetVector findBadFacets(int nDimensions) {
        
        // Hardwires:
        double tol = 1.0E-6;
        
        // Check facets are defined:
        if (numberOfFacets()==0) { return null; }
        
        // Need a record of all the facets to delete:
        FacetVector facetsToRemove = new FacetVector();
        
        // Need a comparator object for checking for duplicate facets:
        FacetNodeIDComparator comparator = new FacetNodeIDComparator();
        
        // Sort the facets by their node ID values:
        nodes.resetIDs(); // required for the sorting
        facets.sortByNodeIDs(); // (this sorts the nodes in each facet and all other required pre-processing)
        //facets.resetIDs(); // so ID's are as sorted and subsequent calls will therefore provide the same information in the dialog
        
        // ---------------- FIND FACETS TO REMOVE ----------------
        
        // Loop over each facet:
        for (int i=0 ; i<numberOfFacets() ; i++ ) {
            
            // Get the ith facet:
            Facet facet = getFacet(i);
            
            // ---------------- DUPLICATE NODE INDICES; NOT ENOUGH UNIQUE NODES ----------------
            
            // Get the nodes for the ith facet:
            NodeVector facetNodes = facet.getNodes();
            
            // Count the number of unique nodes:
            NodeVector unique = new NodeVector();
            int n = facetNodes.size(); // number of nodes (including possible duplicates)
            for (int j=0 ; j<n ; j++ ) {
                Node node = facetNodes.get(j);
                if (!unique.contains(node)) { // if the jth node is not already in the list of unique nodes ...
                    unique.add(node); // ... add it to the list of unique nodes
                }
            }
            int u = unique.size(); // number of unique nodes
            
            // Check if the facet needs to be removed:
            // - duplicate node indices
            // - less than nDimensions unique nodes
            if ( u!=n || u<nDimensions ) {
                // Mark the facet for removal:
                facetsToRemove.add(facet);
                continue; // to next for i iteration (next facet)
            }
            
            // ---------------- DUPLICATE FACETS ----------------
            
            // Only check for duplicate facets from facet number 1 because we are looking at pairs so need one less iteration:
            if (i>0) {
                // Get facet i-1:
                Facet prevFacet = getFacet(i-1);
                // Compare the node ID's in the two facets:
                int df = comparator.compare(prevFacet,facet);
                // Check for identical facets:
                if (df==0) {
                    // Mark the facet for removal: (it shouldn't matter which, but I'll keep facet i-1)
                    facetsToRemove.add(facet);
                    continue; // to next for i iteration (next facet)
                }
            }
            
            // ---------------- LINEAR OR NON-PLANAR POLYGONAL FACETS (n>3) ----------------
        
            // Check for trivial case (not enough nodes):
            if (n<=3) { // non-planar facets can't exist unless n>3
                continue; // to next for i iteration (next facet)
            }
            
            // Check we can obtain the spatial location of all nodes in the facet:
            boolean ok = true;
            for (int j=0 ; j<n ; j++ ) {
                Node node = facetNodes.get(j);
                if (node.getPoint3D()==null) {
                    ok = false;
                    break;
                }
            }
            
            if (!ok) { // 3D point not available for all nodes
                ok = true; // so that we don't remove the point after this if/else block
            } else { // 3D point available for all nodes
                // Calculate the vector between vertices 0 and 1:
                MyPoint3D v2 = null; // initialization avoids compiler warning
                MyPoint3D p0 = facetNodes.get(0).getPoint3D();
                MyPoint3D p1 = facetNodes.get(1).getPoint3D();
                MyPoint3D v1 = p0.vectorToPoint(p1);
                // Find another vector that is not parallel to v1:
                double d = 0.0d;
                int j = 2;
                while ( d<tol && j<n ) {
                    // Calculate the vector between vertices 0 and j:
                    MyPoint3D p2 = facetNodes.get(j).getPoint3D();
                    v2 = p0.vectorToPoint(p2);
                    // Calculate cross product of the two vectors:
                    MyPoint3D c = v1.cross(v2);
                    d = c.norm(); // the cross product of two parallel vectors is a zero vector
                    // Increment counter:
                    j++;
                }
                // Check for linear facet:
                ok = ( d>=tol ); // false for linear facet (could not find another vector non-parallel to v1
                // Check for non-planar facet:
                if (ok) { // can't perform this check for a linear facet
                    // Calculate the cross product of the two vectors found above:
                    MyPoint3D c1 = v1.cross(v2); // c1 is a normal vector for the plane
                    // Loop over every vertex from the next node onwards:
                    for (int k=j+1 ; k<n ; k++ ) {
                        // Calculate the vector between vertices 0 and k:
                        MyPoint3D p2 = facetNodes.get(k).getPoint3D();
                        v2 = p0.vectorToPoint(p2);
                        // Calculate the cross product of the two vectors:
                        MyPoint3D c2 = v1.cross(v2); // c2 is another normal vector for the plane
                        // Calculate cross product of the two normal vectors:
                        MyPoint3D c = c1.cross(c2);
                        d = c.norm(); // the cross product of two parallel vectors is a zero vector
                        // The two normal vectors should be parallel (or antiparallel):
                        if (d>=tol) {
                            ok = false;
                        } // false for non-planar facet
                        break; // from for k
                    }
                //} else { // (for debugging)
                //    ok = false; // (for debugging)
                } // for k
            }
            
            // Check if we should remove the facet or not:
            if (!ok) {
                // Mark the facet for removal:
                facetsToRemove.add(facet);
                //continue; // to next for i iteration (next facet)
            }
            
            /*
            // ---------------- INTERSECTING FACETS ----------------
            
            // Loop over each other facet:
            for (int j=i+1 ; j<plc.numberOfFacets() ; j++ ) {
                // Skip facets marked for removal:
                Facet otherFacet = plc.getFacet(j);
                if (facetsToRemove.contains(otherFacet)) { continue; } // continue to next for j iteration (next other facet)
                // Check if the two facets intersect:
                if (facet.intersects(otherFacet)) {
                   // Mark both facets for removal:
                   facetsToRemove.add(facet);
                   facetsToRemove.add(otherFacet);
                   break; // from for j loop
                }
            } // for j
            */
            
        } // for i
        
        // Return the facets to remove:
        return facetsToRemove;

    }

    /** Finds any edges that are not connected to two facets and returns their associated facets.
     * @return  */
    public FacetVector findHoles() {
        return facets.findBoundaryFacets();
    }
    
    /** Finds any nodes that are not found in facet definitions.
     * @return  */
    public NodeVector findUnusedNodes() {

        // Check nodes are defined:
        if (numberOfNodes()==0) { return null; }
        
        // Need a record of all the nodes to delete:
        NodeVector nodesToRemove = new NodeVector();
        
        // Loop over each node:
        for (int i=0 ; i<numberOfNodes() ; i++ ) {
            // Get the facets for the ith node:
            Node node = getNode(i);
            FacetVector nodeFacets = node.getFacets();
            // Check if the node belongs to any facets:
            if (nodeFacets.size()<=0) {
                // Mark the node for removal:
                nodesToRemove.add(node);
            }
        }
        
        // Return the nodes to remove:
        return nodesToRemove;
        
    }

//    /** Finds duplicate nodes with some distance tolerance.
//     * @param tol The distance tolerance.
//     * @return  */
//    public DuplicateNodeInfo findDuplicateNodes(double tol) {
//        // Check for tolerance of zero (simpler case):
//        if (tol==0) {
//            return findDuplicateNodes();
//        }
//        // Loop over each node:
//        
//    }

    /** Finds duplicate nodes.
     * @return  */
    public DuplicateNodeInfo findDuplicateNodes() {
        
        // Check nodes are defined:
        if (numberOfNodes()==0) { return null; }
        
        // Need a record of all the nodes to delete:
        //NodeVector nodesToRemove = new NodeVector();
        DuplicateNodeInfo dupInfo = new DuplicateNodeInfo();
        
        // Need a comparator object for checking for duplicate nodes:
        NodeXYZComparator comparator = new NodeXYZComparator();
        
        // Sort the nodes by their coordinates:
        nodes.sortByXYZ();
        
        // Loop over each node:
        Node node1 = getNode(0); // will keep track of the first of a set of duplicates
        for (int i=1 ; i<numberOfNodes() ; i++ ) { // start from 1 because we are looking at pairs so need one less iteration
            // Get a new second node:
            Node node2 = getNode(i);
            // Compare the node coordinates:
            int d = comparator.compare(node1,node2);
            // Check for identical node coordinates:
            if (d==0) {
                // Mark second node for removal:
                //nodesToRemove.add(node2);
                dupInfo.add(node1,node2);
            } else {
                // Reset node1 to point to the second node:
                node1 = node2;
            }
        }
        
        // Return the nodes to remove:
        //return nodesToRemove;
        return dupInfo;

    }

    // -------------------- File I/O -------------------

//    public int readNodeEle(int ndim, File nodeFile, File eleFile) {
//        // Clear the plc:
//        clear();
//        // Read the node file:
//        boolean ok = nodes.readTopo(nodeFile,ndim);
//        if (!ok) { return 1; }
//        // Read the ele file:
//        if (eleFile==null) { return 0; }
//        ok = facets.readTopo(eleFile,nodes);
//        if (!ok) { return 2; }
//        // Return successfully:
//        return 0;
//    }
    
    /** Writes the information to a file in poly file format.
     * All ID's should be reset before calling this method.
     * @param file
     * @param ndim
     * @param dir
     * @param byIndex Set to true to write group index as the region attribute instead of the group ID.
     * @return 
     */
    public boolean writePoly(File file, int ndim, Dir3D dir, boolean byIndex) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return false; }

        // Write the nodes information line:
        String textLine = nodes.size() + " " + ndim + " 0 0"; // no attributes or boundary markers
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the node list:
        boolean ok = writeNodeList(writer,ndim,dir,false); // don't write node groups or indices
        if (!ok) { FileUtils.close(writer); return false; }
        
        // Write the facets information line:
        textLine = facets.size() + " 0"; // no boundary markers
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the facet list:
        ok = writeFacetList(writer,ndim,true,false,false,false); // poly format, don't write facet attributes, 2 dummy values
        if (!ok) { FileUtils.close(writer); return false; }

        // Write the hole information:
        textLine = "0"; // no holes
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the regions information line:
        textLine = Integer.toString( regions.numberOfRegionPoints() ); // the poly file should only contain the true region points
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the region list:
        ok = writeRegionList(writer,ndim,dir,false,byIndex); // only write true region points
        if (!ok) { FileUtils.close(writer); return false; }

        // Close the file:
        FileUtils.close(writer);

        return true;

    }

    /** Writes the node information to a file in node file format.
     * All ID's should be reset before calling this method.
     * @param file
     * @param ndim
     * @param dir
     * @return 
     */
    public boolean writeNodes(File file, int ndim, Dir3D dir) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) {
            return false;
        }

        // Write the nodes information line:
        String textLine = nodes.size() + " " + ndim + " 2 0 \"nodeGroup\",\"nodeIndex\""; // no boundary markers
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the node list:
        boolean ok = writeNodeList(writer,ndim,dir,true); // write node groups and indices
        if (!ok) { FileUtils.close(writer); return false; }

        // Close the file:
        FileUtils.close(writer);

        return true;

    }

    /** Writes the facet information to a file in ele file format.
     * All ID's should be reset before calling this method.
     * @param file
     * @param ndim
     * @param writevar true to write non-standard variable facet type .ele file if required
     * @return 
     */
    public boolean writeFacets(File file, int ndim, boolean writevar) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) {
            return false;
        }
        
        // Check for variable facet types:
        int nf = facets.size();
        int n0 = facets.get(0).getNodes().size(); // nodes-per-facet (npf) for the first facet
        boolean isvar = false; // set to true if any facets have npf different from the first facet
        for (int i=1 ; i<nf ; i++ ) {
            int n = facets.get(i).getNodes().size(); // npf for the ith facet
            if ( n != n0 ) {
                isvar = true;
                break;
            }
        }
        boolean dovar = ( isvar && writevar ); // (use dovar instead of writevar below!)

        // Write the facets information line:
        String textLine;
        if (dovar) { // a non-standard variable facet type .ele file will be written
            textLine = nf + " 0"; // 0 nodes-per-facet indicates variable cells
        } else { // a standard .ele file will be written
            if (isvar) { // facets with npf different than ndim will be excluded
                // Count the number of facets with ndim nodes:
                int nw = 0;
                for (int i=0 ; i<nf ; i++ ) {
                   int n = facets.get(i).getNodes().size(); // npf for the ith facet
                    if (n==ndim) { nw++; }
                }
                textLine = nw + " " + ndim; // ndim nodes-per-facet
            } else { // all facets are the same type (have the same npf)
                textLine = nf + " " + n0; // non-variable nodes-per-facet
            }
        }
        textLine += " 2 \"facetGroup\",\"facetIndex\""; // 2 attributes
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the facet list:
        boolean ok = writeFacetList(writer,ndim,false,true,dovar,isvar); // ele format (not poly format), write facet attributes
        if (!ok) { FileUtils.close(writer); return false; }

        // Close the file:
        FileUtils.close(writer);

        return true;

    }

    /** Writes the regions information to a file in node file format.
     * All ID's should be reset before calling this method.
     * @param file
     * @param ndim
     * @param dir
     * @param doControl Set to true to write control points only, false for true region points only.
     * @param byIndex Set to true to write group index as the region attribute instead of the group ID.
     * @return 
     */
    public boolean writeRegions(File file, int ndim, Dir3D dir, boolean doControl, boolean byIndex) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) {
            return false;
        }

        // Write the regions information line:
        String textLine;
        int n;
        if (doControl) {
            n = regions.numberOfControlPoints();
        } else {
            n = regions.numberOfRegionPoints();
        }
        textLine = n + " " + ndim + " 1 0"; // one attribute, no boundary markers
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the region list:
        boolean ok = writeRegionList(writer,ndim,dir,doControl,byIndex);
        if (!ok) { FileUtils.close(writer); return false; }

        // Close the file:
        FileUtils.close(writer);

        return true;

    }

    private boolean writeNodeList(BufferedWriter writer, int ndim, Dir3D dir, boolean writeAttributes) {

        // Write the node coordinates, group attributes and optional node indices:
        for (int i=0; i<nodes.size() ; i++ ) {
            Node node = nodes.get(i);
            String textLine = (i+1) + " "; // node index
            MyPoint3D p3 = node.getPoint3D(); // 3D coordinates
            if (ndim==3) {
                textLine += p3.toString(TOLZERO);
            } else {
                MyPoint2D p2;
                if (dir==null) {
                    //p2 = node.getPoint2D(); // image pixels is the best we can do at this stage
                    //p2.negY();
                    p2 = new MyPoint2D( p3.normXY() , p3.getZ() ); // x is projection in map space
                } else {
                    p2 = p3.getPoint2D(dir);
                } // 2D coordinates
                textLine += p2.toString(TOLZERO);
            }
            if (writeAttributes) {
                int gid = node.getGroup().getID() + 1;
                textLine += " " + gid + " " + (i+1); // node group and index
            }
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        
        return true;

    }

    private boolean writeFacetList(BufferedWriter writer, int ndim, boolean polyFormat, boolean writeAttributes, boolean dovar, boolean isvar) {

        // Write the facet definitions:
        for (int i=0; i<facets.size() ; i++ ) {
            Facet facet = facets.get(i);
            int n = facet.size();
            if ( ndim==2 && n!=2 ) { throw new IllegalArgumentException("Ancountered a facet in a 2D model that was not a line."); }
            if ( !polyFormat && !dovar && isvar && n!=ndim ) { continue; } // don't write the facet
            // (because in the header I write ndim as the number of nodes per cell for that situation)
            String textLine;
            if ( polyFormat && ndim==3 ) {
                textLine = "1 0"; // one polygon, no holes
                if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
                textLine = Integer.toString(n); // number of nodes
            } else {
                textLine = Integer.toString(i+1); // facet index
                if (!polyFormat && dovar) {
                    textLine += " " + n; // number of nodes in the variable facet
                }
            }
            for (int j=0 ; j<n ; j++ ) {
                int id = facet.getNode(j).getID() + 1;
                textLine += " " + id; // node IDs
            }
            if (writeAttributes) {
                int gid = facet.getGroup().getID() + 1;
                textLine += " " + gid + " " + (i+1); // facet group and index
            }
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }

        return true;

    }
    
    private boolean writeRegionList(BufferedWriter writer, int ndim, Dir3D dir, boolean doControl, boolean byIndex) {

        // Write the region coordinates:
        for (int i=0; i<regions.size() ; i++ ) {
            // Check type of region and skip if not the correct type (region or control point):
            Region region = regions.get(i);
            if ( region.getIsControl() != doControl ) { continue; } // skip
            // Write the information:
            String textLine = (i+1) + " ";
            MyPoint3D p3 = region.getPoint3D(); // 3D coordinates
            if (ndim==3) {
                textLine += p3.toString(TOLZERO);
            } else {
                MyPoint2D p2;
                if (dir==null) {
                    //p2 = region.getPoint2D(); // image pixels is the best we can do at this stage
                    //p2.negY();
                    p2 = new MyPoint2D( p3.normXY() , p3.getZ() ); // x is projection in map space
                } else {
                    p2 = p3.getPoint2D(dir);
                } // 2D coordinates
                textLine += p2.toString(TOLZERO);
            }
            if (byIndex) {
                textLine += " " + (i+1); // region index
            } else {
                int gid = region.getGroup().getID() + 1;
                textLine += " " + gid; // region group
            }
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        
        return true;
        
    }

    /** Writes the information to a file in vtu file format.
     * All ID's should be reset before calling this method.
     * @param file
     * @param flipz
     * @return 
     */
    public boolean writeVTU(File file, boolean flipz) {

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) {
            return false;
        }
        
        // Check for variable cells:
        boolean isvar=false;
        int n0 = facets.get(0).getNodes().size();
        for (int i=1; i<facets.size() ; i++ ) {
            int ni = facets.get(i).getNodes().size();
            if (ni!=n0) {
                isvar = true;
                break;
            }
        }

        // Write first 2 lines:
        String textLine = "<VTKFile type=\"UnstructuredGrid\" version=\"0.1\" byte_order=\"LittleEndian\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<UnstructuredGrid>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write piece information:
        textLine = "<Piece NumberOfPoints=\"" + nodes.size() + "\" NumberOfCells=\"" + facets.size() + "\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write the node coordinates:
        textLine = "<Points>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<DataArray type=\"Float32\" NumberOfComponents=\"3\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<nodes.size() ; i++ ) {
            Node node = nodes.get(i);
            MyPoint3D p = node.getPoint3D();
            if (flipz) {
                p = p.deepCopy();
                p.flipZ();
            }
            textLine = p.toString(TOLZERO);
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "</Points>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the facet specifications:
        textLine = "<Cells>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<DataArray type=\"Int32\" Name=\"connectivity\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<facets.size() ; i++ ) {
            NodeVector facetNodes = facets.get(i).getNodes();
            int ni = facetNodes.size();
            textLine = "";
            for (int j=0 ; j<ni ; j++ ) {
                textLine += " " + facetNodes.get(j).getID();
            }
            if (isvar) {
                textLine += " " + facetNodes.get(0).getID(); // explicitly closes the polygon
            }
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Write the offsets for each facet:
        textLine = "<DataArray type=\"Int32\" Name=\"offsets\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        int n = 0;
        for (int i=0; i<facets.size() ; i++ ) {
            int ni = facets.get(i).getNodes().size();
            n += ni;
            if (isvar) {
                n += 1; // because we are explicitly closing the polygon
            }
            textLine = Integer.toString(n);
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write the facet types:
        textLine = "<DataArray type=\"Int32\" Name=\"types\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<facets.size() ; i++ ) {
            if (isvar) {
                textLine = "7";
            } else {
                int ni = facets.get(i).getNodes().size();
                switch (ni) {
                    case 2:
                        // line element
                        textLine = "3"; // (VTK_LINE)
                        break;
                    case 3:
                        // triangular facet
                        textLine = "5"; // (VTK_TRIANGLE)
                        break;
                    default:
                        // closed polygon
                        textLine = "7"; // (VTK_POLYGON)
                        break;
                }
            }
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "</Cells>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write node groups and indices as attribute values:
        textLine = "<PointData Scalars=\"nodeGroup nodeIndex\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<DataArray type=\"Float32\" Name=\"nodeGroup\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<nodes.size() ; i++ ) {
            Node node = nodes.get(i);
            int gid = node.getGroup().getID() + 1;
            textLine = Integer.toString( gid );
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<DataArray type=\"Float32\" Name=\"nodeIndex\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<nodes.size() ; i++ ) {
            textLine = Integer.toString( i+1 );
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "</PointData>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write cell groups and indices as attribute values:
        textLine = "<CellData Scalars=\"facetGroup facetIndex\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<DataArray type=\"Float32\" Name=\"facetGroup\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<facets.size() ; i++ ) {
            Facet facet = facets.get(i);
            int gid = facet.getGroup().getID() + 1;
            textLine = Integer.toString( gid );
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "<DataArray type=\"Float32\" Name=\"facetIndex\" Format=\"ascii\">";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        for (int i=0; i<facets.size() ; i++ ) {
            textLine = Integer.toString( i+1 );
            if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        }
        textLine = "</DataArray>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "</CellData>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }

        // Write final 3 lines:
        textLine = "</Piece>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "</UnstructuredGrid>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        textLine = "</VTKFile>";
        if (!FileUtils.writeLine(writer,textLine)) { FileUtils.close(writer); return false; }
        
        // Close the file:
        FileUtils.close(writer);

        return true;

    }

}
