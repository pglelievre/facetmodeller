package facetmodeller;

import dialogs.Dialogs;
import facetmodeller.commands.CommandVector;
import facetmodeller.commands.DuplicateNodeInfo;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.SceneInfo;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.PLC;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import facetmodeller.sections.ImageCrossSection;
import facetmodeller.sections.ImageDepthSection;
import facetmodeller.sections.NoImageCrossSection;
import facetmodeller.sections.NoImageDepthSection;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import facetmodeller.sections.SnapshotSection;
import fileio.FileUtils;
import fileio.SessionIO;
import geometry.Dir3D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

/** The FacetModeller Model component of the MVC architecture.
/** Contains all the model information: PCL, groups, sections.
 * @author Peter
 */
public class ModelManager implements SessionIO {
    
    // Properties:
    private int nDimensions = 0;
    private VOI voi = null;
    private PLC plc = new PLC();
    private SectionVector sections = new SectionVector();
    private GroupVector groups = new GroupVector();
    
    // Constructor:
    public ModelManager(int ndim) { nDimensions = ndim; }
    
    // Getters that should be used sparingly by the controller only: TODO: check they are
    public VOI getVOI() { return voi; }
    public PLC getPLC() { return plc; }
    public SectionVector getSections() { return sections; }
    public GroupVector getGroups() { return groups; }
    
    // Getters for the number of items:
    public boolean hasVOI() { return (voi!=null); }
    public boolean hasNodes() { return plc.hasNodes(); }
    public boolean hasFacets() { return plc.hasFacets(); }
    public boolean hasRegions() { return plc.hasRegions(); }
    public boolean plcIsEmpty() { return plc.isEmpty(); }
    public boolean hasSections() { return !sections.isEmpty(); }
    public boolean hasGroups() { return !groups.isEmpty(); }
    public boolean is2D() { return (nDimensions==2); }
    public boolean is3D() { return (nDimensions==3); }
    public int numberOfDimensions() { return nDimensions; }
    public int numberOfNodes() { return plc.numberOfNodes(); }
    public int numberOfNodesInFacet(int i) { return plc.numberOfNodesInFacet(i); }
    public int numberOfFacets() { return plc.numberOfFacets(); }
    public int numberOfRegions() { return plc.numberOfRegions(); }
    public int numberOfRegionPoints() { return plc.numberOfRegionPoints(); }
    public int numberOfControlPoints() { return plc.numberOfControlPoints(); }
    public int numberOfSections() { return sections.size(); }
    public int numberOfGroups() { return groups.size(); }
    public boolean writeGroupDefinitions(File file) { return groups.writeFile(file); }
    
    // Getters for specific items: TODO: make sure these are used sparingly
    public NodeVector getNodes() { return plc.getNodes(); }
    public FacetVector getFacets() { return plc.getFacets(); }
    public RegionVector getRegions() { return plc.getRegions(); }
    public Node getNode(int i) { return plc.getNode(i); }
    public Facet getFacet(int i) { return plc.getFacet(i); }
    public Region getRegion(int i) { return plc.getRegion(i); }
    public Section getSection(int i) { return sections.get(i); }
    public Group getGroup(int i) { return groups.get(i); }
    
    // Setters that should be used sparingly by the controller only: TODO: check they are
    public void addPLC(PLC p) { plc.addAll(p); }
    public void setPLC(PLC p) { plc = p; }
    public void addSections(SectionVector sv) { sections.addAll(sv); }
    public void setSections(SectionVector sv) { sections = sv; }
    public void setGroups(GroupVector gv) { groups = gv; }
    
    // Methods for adding and removing items:
    public void addSection(Section s) { sections.add(s); }
    public void removeSection(Section s) { sections.remove(s); }
    public void addGroup(Group g) { groups.add(g); }
    public void addGroup(Group g, int i) { groups.add(g,i); }
    public void addGroups(GroupVector gv) { groups.addAll(gv); }
    public void removeGroup(Group g) { groups.remove(g); }
    
    // Methods for clearing and resetting:
    public void clearPLC() { plc.clear(); }
    public void clearSections() { sections.clear(); }
    public void clearGroups() { groups.clear(); }
    public void resetIDs() {
        plc.resetIDs();
        sections.resetIDs();
        groups.resetIDs();
    }
    
    // Methods that wrap PLC and VOI methods:
    public SceneInfo getSceneInfo3D(MyPoint3D p) {
        if (hasVOI()) {
            return voi.getSceneInfo(p);
        } else {
            if (hasNodes()) {
                return plc.getSceneInfo(p);
            } else {
                return null;
            }
        }
    }
    
    // Methods that wrap VOI methods:
    public MyPoint3D[][] getVOIEdges() { return voi.getEdges(); }
    public MyPoint2D getEastingLimits() { return voi.getEastingLimits(); }
    public MyPoint2D getNorthingLimits() { return voi.getNorthingLimits(); }
    public MyPoint2D getElevationLimits() { return voi.getElevationLimits(); }
    public boolean inOrOn(MyPoint3D p) { return voi.inOrOn(p); }
    public MyPoint3D[] getVOICorners() { return voi.getCorners(); }
    
    // Methods that wrap PLC methods:
    public boolean containsNode(Node n) { return plc.containsNode(n); }
    public int indexOfNode(Node n) { return plc.indexOfNode(n); }
    public int indexOfFacet(Facet f) { return plc.indexOfFacet(f); }
    public int indexOfRegion(Region r) { return plc.indexOfRegion(r); }
    public void addNode(Node n) { plc.addNode(n); }
    public void removeNode(Node n) { plc.removeNode(n); }
    public void addFacet(Facet f) { plc.addFacet(f); }
    public void removeFacet(Facet f) { plc.removeFacet(f); }
    public void addRegion(Region r) { plc.addRegion(r); }
    public void removeRegion(Region r) { plc.removeRegion(r); }
    public Node findClosestNode(Node n) { return plc.findClosestNode(n); }
    public NodeVector findUnusedNodes() { return plc.findUnusedNodes(); }
    public DuplicateNodeInfo findDuplicateNodes() { return plc.findDuplicateNodes(); }
    public FacetVector findBadFacets(int ndim) { return plc.findBadFacets(ndim); }
    public FacetVector findHoles() { return plc.findHoles(); }
    public CommandVector snapToVOI(double snappingDistance, GroupVector groups, boolean doH, boolean doV) {
        return plc.snapToVOI(voi,snappingDistance,groups,doH,doV);
    }
    public CommandVector snapToGrid(double m, GroupVector groups, boolean doH, boolean doV) {
        return plc.snapToGrid(m,groups,doH,doV);
    }
    public CommandVector translate(MyPoint3D p, GroupVector groups) {
        return plc.translate(p,groups);
    }
    public boolean writePoly(File file, int ndim, Dir3D dir, boolean byIndex) {
        return plc.writePoly(file,ndim,dir,byIndex);
    }
    public boolean writeNodes(File file, int ndim, Dir3D dir) {
        return plc.writeNodes(file,ndim,dir);
    }
    public boolean writeFacets(File file, int ndim, boolean writevar) {
        return plc.writeFacets(file,ndim,writevar);
    }
    public boolean writeRegions(File file, int ndim, Dir3D dir, boolean doControl, boolean byIndex) {
        return plc.writeRegions(file,ndim,dir,doControl,byIndex);
    }
    public boolean writeVTU(File file, boolean flipz) {
        return plc.writeVTU(file,flipz);
    }
    
    // Methods that wrap Section methods:
    public void addSectionsFromFiles(File[] files, boolean iscross) { sections.addFromFiles(files,iscross); }
    public void copyCalibration(Section s, double step) { sections.copyCalibration(s,step); }
    public CommandVector snapToCalibration(double pickingRadius, GroupVector groups, boolean doH, boolean doV) {
        return sections.snapToCalibration(pickingRadius,groups,doH,doV);
    }
    public void scalePixels(double factor) { sections.scalePixels(factor); }
    public NodeVector removeNodesCalibrationRange() { return sections.removeNodesRange(); }
    public boolean areAllSectionsCalibrated() { return sections.areAllSectionsCalibrated(); }
    
    /** Displays information about the plc.
     * @param con */
    public void showPLCInfo(Component con) {
        String t = "PLC has " + numberOfNodes() + " nodes, " + numberOfFacets() + " facets.";
        for (int i=0 ; i<numberOfGroups() ; i++ ) {
            Group g = getGroup(i);
            t = t + System.lineSeparator() + "Group " + g.getName() + " has "
                    + g.numberOfNodes() + " nodes, "
                    + g.numberOfFacets() + " facets, "
                    + g.numberOfRegions() + " regions, ";
            //if (g.hasRegion()) {
            //   t += "region defined.";
            //} else {
            //   t += "region not defined.";
            //}
        }
        for (int i=0 ; i<numberOfSections() ; i++ ) {
            Section s = getSection(i);
            //t = t + System.lineSeparator() + "Section " + s.shortName() + " has " + s.numberOfNodes() + " nodes, " + s.numberOfFacets() + " facets, " + s.numberOfRegions() + " regions.";
            t = t + System.lineSeparator() + "Section " + s.shortName() + " has " + s.numberOfNodes() + " nodes, " + s.numberOfRegions() + " regions.";
        }
        Dialogs.inform(con,t,"PLC Information");
    }
    
    /** Gets the user to define the volume of interest (VOI).
     * @param con
     * @return  */
    public boolean defineVOI(Component con) {
        
        // Define or get some starting information:
        String title = "Define VOI";
        String message = "Enter two numeric values.";
        String prompt;
        String input;
        String inputs[];
        MyPoint2D p;
        double x1,x2,y1,y2,z1,z2;
        
        // Ask for Easting limits:
        prompt = "Enter the Easting limits:";
        if (hasVOI()) {
            p = getEastingLimits();
            input = Dialogs.input(con,prompt,title,p.toString());
        } else {
            input = Dialogs.input(con,prompt,title);
        }
        if (input==null) { return false; } // user cancelled
        input = input.trim();
        inputs = input.split("[ ]+");
        if (inputs.length!=2) {
            Dialogs.error(con,"Please enter two numeric values.","Error");
            return false;
        }
        try {
            x1 = Double.parseDouble(inputs[0].trim());
            x2 = Double.parseDouble(inputs[1].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(con,message,title);
            return false;
        }
        
        // Ask for Northing limits:
        prompt = "Enter the Northing limits:";
        if (hasVOI()) {
            p = getNorthingLimits();
            input = Dialogs.input(con,prompt,title,p.toString());
        } else {
            input = Dialogs.input(con,prompt,title);
        }
        if (input==null) { return false; } // user cancelled
        input = input.trim();
        inputs = input.split("[ ]+");
        if (inputs.length!=2) {
            Dialogs.error(con,"Please enter two numeric values.","Error");
            return false;
        }
        try {
            y1 = Double.parseDouble(inputs[0].trim());
            y2 = Double.parseDouble(inputs[1].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(con,message,title);
            return false;
        }
        
        // Ask for Elevation limits:
        prompt = "Enter the Elevation limits:";
        if (hasVOI()) {
            p = getElevationLimits();
            input = Dialogs.input(con,prompt,title,p.toString());
        } else {
            input = Dialogs.input(con,prompt,title);
        }
        if (input==null) { return false; } // user cancelled
        input = input.trim();
        inputs = input.split("[ ]+");
        if (inputs.length!=2) {
            Dialogs.error(con,"Please enter two numeric values.","Error");
            return false;
        }
        try {
            z1 = Double.parseDouble(inputs[0].trim());
            z2 = Double.parseDouble(inputs[1].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Dialogs.error(con,message,title);
            return false;
        }
        
        // Make the new VOI:
        voi = new VOI(x1,x2,y1,y2,z1,z2);
        
        // Return successfully:
        return true;
        
    }
    
    public void reverseGroupOrder() { groups.reverseOrder(); }
    
    public int moveGroup(Group group, int move) {
        if (move==0) { return -1; }
        // Get the index of the group:
        int ind1 = groups.indexOf(group);
        if (ind1<0) { return -1; }
        // Determine the new index for the group:
        int ind2 = ind1 + move;
        int n = numberOfGroups() - 1;
        if ( ind2 < 0 ) { ind2 = 0; }
        if ( ind2 > n ) { ind2 = n; }
        // Check if the group will move:
        if ( ind2 == ind1 ) { return -1; }
        // Move the group in the list:
        removeGroup(group);
        addGroup(group,ind2);
        return ind2;
    }

    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        
        // Write the number of dimensions, nodes, facets, regions, sections and groups:
        int ndim = numberOfDimensions();
        int nnodes = numberOfNodes();
        int nfacets = numberOfFacets();
        int nregions =  numberOfRegions();
        int nsections = numberOfSections();
        int ngroups = numberOfGroups();
        String textLine = ndim + " " + nnodes + " " + nfacets + " " + nregions + " " + nsections + " " + ngroups;
        if (!FileUtils.writeLine(writer,textLine)) { return false; }

        // ---------- IN THE FIRST PASS I WRITE ALL INFORMATION OTHER THAN ID'S ----------

        // Comment start of node definitions:
        if (!FileUtils.writeLine(writer,"# NODES")) { return false; }

        // Loop over each node:
        for (int i=0 ; i<nnodes ; i++ ) {
            Node node = getNode(i);
            // Write node ID and indication of ith node type:
            textLine = node.getID() + " " + node.getType();
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            // Write the node information:
            if (!node.writeSessionInformation(writer)) { return false; }
        }

        // Comment start of region definitions:
        if (!FileUtils.writeLine(writer,"# REGIONS")) { return false; }

        // Loop over each region:
        for (int i=0 ; i<nregions ; i++ ) {
            Region region = getRegion(i);
            // Write the region information information:
            if (!region.writeSessionInformation(writer)) { return false; }
        }

        // Comment start of section definitions:
        if (!FileUtils.writeLine(writer,"# SECTIONS")) { return false; }

        // Loop over each section:
        for (int i=0 ; i<nsections ; i++ ) {
            Section section = getSection(i);
            // Comment start of ith section definition:
            textLine = "# Section " + section.getID();
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            // Write indication of the type of section:
            textLine = Integer.toString(section.getType());
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            // Write the section information:
            if (!section.writeSessionInformation(writer)) { return false; }
        }

        // Comment start of group definitions:
        if (!FileUtils.writeLine(writer,"# GROUPS")) { return false; }

        // Loop over each group:
        for (int i=0 ; i<ngroups ; i++ ) {
            Group group = getGroup(i);
            // Write the group information:
            if (!group.writeSessionInformation(writer)) { return false; }
        }

        // ---------- IN THE SECOND PASS I WRITE THE ID'S ----------

        // Comment start of node linkages:
        if (!FileUtils.writeLine(writer,"# NODE LINKS")) { return false; }

        // Loop over each node:
        for (int i=0 ; i<nnodes ; i++ ) {
            Node node = getNode(i);
            //FacetVector facets = node.getFacets();
            /* I don't need to write the facet id's here because
             * the same information is written below in the loop over each facet. */
            // Write the section id and group id:
            textLine = node.getSection().getID() + " " + node.getGroup().getID();
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        }

        // Comment start of node linkages:
        if (!FileUtils.writeLine(writer,"# FACET LINKS")) { return false; }

        // Loop over each facet:
        for (int i=0 ; i<nfacets ; i++ ) {
            Facet facet = getFacet(i);
            NodeVector nodes = facet.getNodes();
            SectionVector facetSections = facet.getSections();
            // Write the node id's:
            int n = nodes.size();
            textLine = Integer.toString(n);
            for (int j=0 ; j<n ; j++ ) {
                textLine = textLine + " " + nodes.get(j).getID();
            }
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            // Write the section id's:
            n = facetSections.size();
            textLine = Integer.toString(n);
            for (int j=0 ; j<n ; j++ ) {
                textLine = textLine + " " + facetSections.get(j).getID();
            }
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            // Write the group id:
            textLine = Integer.toString( facet.getGroup().getID() );
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        }

        // Comment start of node linkages:
        if (!FileUtils.writeLine(writer,"# REGION LINKS")) { return false; }

        // Loop over each region:
        for (int i=0 ; i<nregions ; i++ ) {
            Region region = getRegion(i);
            // Write the section id:
            textLine = Integer.toString( region.getSection().getID() );
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
            // Write the group id:
            textLine = Integer.toString( region.getGroup().getID() );
            if (!FileUtils.writeLine(writer,textLine)) { return false; }
        }

        /* I don't need to write the node/facet/region ID's for the sections or groups
         * because the same information is written above in the loops for each node/facet/region. */

        // ---------- Write the VOI information: ----------

        // Comment start of VOI information:
        if (!FileUtils.writeLine(writer,"# VOI")) { return false; }

        // Write the VOI:
        if (hasVOI()) {
            if (!getVOI().writeSessionInformation(writer)) { return false; }
        } else {
            if (!FileUtils.writeLine(writer,"null")) { return false; }
        }
        
        // Return true:
        return true;
        
    }

    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        
        // I need to construct new objects as I read the file:
        PLC plc2 = new PLC();
        SectionVector sections2 = new SectionVector();
        GroupVector groups2 = new GroupVector();
        
        // Read the number of dimensions, nodes, facets, regions, samples and groups:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading number of dimensions, etc."; }
        int ndim, nnodes, nregions, nfacets, nsections, ngroups;
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+");
        if (ss.length<6) { return "Not enough values on number of dimensions etc. line."; }
        try {
            ndim      = Integer.parseInt(ss[0].trim()); // converts to integer
            nnodes    = Integer.parseInt(ss[1].trim()); // converts to integer
            nfacets   = Integer.parseInt(ss[2].trim()); // converts to integer
            nregions  = Integer.parseInt(ss[3].trim()); // converts to integer
            nsections = Integer.parseInt(ss[4].trim()); // converts to integer
            ngroups   = Integer.parseInt(ss[5].trim()); // converts to integer
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing number of dimensions, etc."; }

        // Check ndim:
        if (ndim!=numberOfDimensions()) { return "Incorrect number of dimensions."; }

        // ---------- IN THE FIRST PASS I READ ALL INFORMATION OTHER THAN ID'S AND CREATE NEW OBJECTS ----------

        // Skip the commented start of node definitions:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of node definitions."; }

        // Loop over each node:
        for (int i = 0; i<nnodes; i++) {
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading node ID etc. line."; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<2) { return "Not enough values on node ID etc. line."; }
            int nodeType;
            try {
                int nodeID = Integer.parseInt(ss[0].trim());
                if ( nodeID != i ) { return "Unmatched node ID"; }
                nodeType = Integer.parseInt(ss[1].trim());
            } catch (NumberFormatException e) { return "Parsing node ID etc."; }
            Node node;
            switch (nodeType) {
                case Node.NODE_ON_SECTION:
                    node = new NodeOnSection();
                    break;
                case Node.NODE_OFF_SECTION:
                    node = new NodeOffSection();
                    break;
                default:
                    return "Unmatched node type.";
            }
            if (node==null) { return "Unexpected empty new Node created."; }
            String msg = node.readSessionInformation(reader,merge);
            if (msg!=null) { return "Reading information for node " + i + "." + System.lineSeparator() + msg.trim(); }
            plc2.addNode(node);
        }

        // Loop over each facet:
        for (int i=0 ; i<nfacets ; i++ ) {
            // Add a new empty facet to the plc (these facets are filled later):
            plc2.addFacet( new Facet() );
        }

        // Skip the commented start of region definitions:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of region definitions."; }

        // Loop over each region:
        for (int i=0 ; i<nregions ; i++ ) {
            // Make a new region object:
            Region region = new Region();
            // Read the region information:
            region.readSessionInformation(reader,merge);
            // Add the region to the plc:
            plc2.addRegion(region); // section and group membership will be added later
        }

        // Skip the commented start of section definitions:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of section definitions."; }

        for (int i = 0; i<nsections; i++) {
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Skipping start of ith section definition."; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading section type."; }
            textLine = textLine.trim();
            int sectionType;
            try {
                sectionType = Integer.parseInt(textLine);
            } catch (NumberFormatException e) { return "Parsing section type."; }
            Section section;
            switch (sectionType) {
                case Section.SECTION_IMAGE_CROSS:
                    section = new ImageCrossSection();
                    break;
                case Section.SECTION_IMAGE_DEPTH:
                    section = new ImageDepthSection();
                    break;
                case Section.SECTION_NOIMAGE_CROSS:
                    section = new NoImageCrossSection();
                    break;
                case Section.SECTION_NOIMAGE_DEPTH:
                    section = new NoImageDepthSection();
                    break;
                case Section.SECTION_SNAPSHOT:
                    section = new SnapshotSection();
                    break;
                default:
                    return "Unmatched section type.";
            }
            if (section==null) { return "Unexpected empty new Section created."; }
            String msg = section.readSessionInformation(reader,merge);
            if (msg!=null) { return "Reading information for section " + i + "." + System.lineSeparator() + msg.trim(); }
            sections2.add(section);
        }

        // Skip the commented start of group definitions:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of group definitions."; }

        // Loop over each group:
        for (int i=0 ; i<ngroups ; i++ ) {
            // Create a new group object:
            Group group = new Group();
            // Read the group information:
            String msg = group.readSessionInformation(reader,merge);
            if (msg!=null) { return "Reading information for group" + i + "." + System.lineSeparator() + msg.trim(); }
            // Add the group to the list of groups:
            groups2.add(group);
        }

        // ---------- IN THE SECOND PASS I READ THE ID'S AND SET THE CROSS-LINKAGES ----------

        // Skip the commented start of node linkages:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of node linkages."; }

        // Loop over each node:
        for (int i=0 ; i<nnodes ; i++ ) {
            Node node = plc2.getNode(i);
            // The node gets linked to the facets in the loop over each facet below.
            // Read the section id and group id:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading node section and group IDs line."; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<2) { return "Not enough values on node section and group IDs line."; }
            int sid, gid; // section and group id
            try {
                sid = Integer.parseInt(ss[0].trim()); // converts to integer
                gid = Integer.parseInt(ss[1].trim()); // converts to integer
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing node section and group IDs."; }
            // Cross-link the node and section:
            node.setSection( sections2.get(sid) );
            sections2.get(sid).addNode(node);
            // Cross-link the node and group:
            node.setGroup( groups2.get(gid) );
            groups2.get(gid).addNode(node);
        }

        // Skip the commented start of facet linkages:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of facet linkages."; }

        // Loop over each facet:
        for (int i=0 ; i<nfacets ; i++ ) {
            Facet facet = plc2.getFacet(i);
            // Read the node id's and link those nodes to the facet:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading facet node IDs line."; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { return "No values on facet node IDs line."; }
            int n; // number of nodes
            try {
                n = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { return "Parsing facet node length."; }
            if ( ss.length < n+1 ) { return "Not enough values on facet node IDs line."; }
            for (int j=0 ; j<n ; j++ ) {
                int id; // node id
                try {
                    id = Integer.parseInt(ss[j+1].trim()); // converts to integer
                } catch (NumberFormatException e) { return "Parsing facet node ID."; }
                // Cross-link the facet and node:
                facet.addNode( plc2.getNode(id) );
                plc2.getNode(id).addFacet( facet );
            }
            // Read the section id's:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading facet section ID line."; }
            /*
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { return "No values on facet section ID line."; }
            try {
                n = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { return "Parsing facet section length."; }
            if ( ss.length < n+1 ) { return "Not enough values on facet section ID line."; }
            for (int j=0 ; j<n ; j++ ) {
                int id;
                try {
                    id = Integer.parseInt(ss[j+1].trim()); // converts to integer
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { return "Parsing facet section ID."; }
                // Cross-link the facet and section:
//                    facet.addSection( sections.get(id) ); // no longer necessary because facet sections defined by the facet nodes
                sections2.get(id).addFacet(facet);
            }
            */
            // Read the group id:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading facet group ID line."; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { return "No values on facet group ID line."; }
            int id;
            try {
                id = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { return "Parsing facet group ID."; }
            // Cross-link the facet and group:
            facet.setGroup( groups2.get(id) );
            groups2.get(id).addFacet(facet);
        }

        // Skip the commented start of region linkages:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of region linkages."; }

        // Loop over each region:
        for (int i=0 ; i<nregions ; i++ ) {
            Region region = plc2.getRegion(i);
            // Read the section id:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading region section ID line."; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { return "No values on region section ID line."; }
            int id;
            try {
                id = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { return "Parsing region section ID."; }
            // Cross-link the region and section:
            region.setSection( sections2.get(id) );
            sections2.get(id).addRegion(region);
            // Read the group id and link that group to the node:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading region group ID line."; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { return "No values on region group ID line."; }
            try {
                id = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { return "Parsing region group ID."; }
            // Cross-link the region and group:
            region.setGroup( groups2.get(id) );
            //groups.get(id).setRegion(region);
            groups2.get(id).addRegion(region);
        }

        // ---------- Read the VOI information: ----------

        // Skip the commented start of the VOI information:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping start of VOI information."; }

        // Read the VOI:
        VOI voi2 = new VOI();
        String msg = voi2.readSessionInformation(reader,merge);
        if (msg!=null) {
            if (msg.startsWith("Null")) {
                // Assume encountered line with "null" in it:
                voi2 = null;
            } else {
                return msg;
            }
        }
        
        // If overwriting then set existing information to new information, otherwise combine the information:
        if (merge) {
            // Don't change the VOI!
            plc.addAll(plc2);
            groups.addAll(groups2);
        } else {
            plc.clear(); // these aren't necessary because the garbage collection should deal with them
            groups.clear();
            voi = voi2;
            plc = plc2;
            groups = groups2;
        }
        if (merge && ndim==3) {
            sections.addAll(sections2);
        } else {
            sections.clear();
            sections = sections2;
        }
        
        // Return successfully:
        return null;
        
    }
    
}
