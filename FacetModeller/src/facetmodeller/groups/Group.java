package facetmodeller.groups;

import fileio.SessionIO;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.HasID;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import fileio.FileUtils;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** A group to associated with nodes, facets and a region.
 * @author Peter
 */
public class Group extends HasID implements SessionIO {

    // ------------------ Properties -------------------

    private String name = null;

    private Color nodeColor = Color.BLACK;
    private Color facetColor = Color.BLACK;
    private Color regionColor = Color.BLACK;

    private NodeVector nodes = new NodeVector();
    private FacetVector facets = new FacetVector();
    private RegionVector regions = new RegionVector();

    // ------------------ Constructor -------------------

    public Group() { super(); }
    
    public Group(String n) {
        super();
        name = n;
    }
    
    public Group(String n, Color col) {
        super();
        name = n;
        setColor(col);
    }
    
    // ------------------ Copy -------------------

//    public Group deepCopy() {
//        Group newGroup = new Group(name);
//        int r,g,b,a;
//        r = this.nodeColor.getRed();
//        g = this.nodeColor.getGreen();
//        b = this.nodeColor.getBlue();
//        a = this.nodeColor.getAlpha();
//        newGroup.setNodeColor( new Color(r,g,b,a) );
//        r = this.facetColor.getRed();
//        g = this.facetColor.getGreen();
//        b = this.facetColor.getBlue();
//        a = this.facetColor.getAlpha();
//        newGroup.setFacetColor( new Color(r,g,b,a) );
//        r = this.regionColor.getRed();
//        g = this.regionColor.getGreen();
//        b = this.regionColor.getBlue();
//        a = this.regionColor.getAlpha();
//        newGroup.setRegionColor( new Color(r,g,b,a) );
//        newGroup.addNodes( this.nodes.deepCopy() );
//        newGroup.addFacets( this.facets.deepCopy() );
//        newGroup.addRegions( this.regions.deepCopy() );
//        return newGroup;
//    }
//    public Group undoCopy() {
//        Group newGroup = new Group(name);
//        int r,g,b,a;
//        r = this.nodeColor.getRed();
//        g = this.nodeColor.getGreen();
//        b = this.nodeColor.getBlue();
//        a = this.nodeColor.getAlpha();
//        newGroup.setNodeColor( new Color(r,g,b,a) );
//        r = this.facetColor.getRed();
//        g = this.facetColor.getGreen();
//        b = this.facetColor.getBlue();
//        a = this.facetColor.getAlpha();
//        newGroup.setFacetColor( new Color(r,g,b,a) );
//        r = this.regionColor.getRed();
//        g = this.regionColor.getGreen();
//        b = this.regionColor.getBlue();
//        a = this.regionColor.getAlpha();
//        newGroup.setRegionColor( new Color(r,g,b,a) );
//        newGroup.addNodes( this.nodes.shallowCopy() );
//        newGroup.addFacets( this.facets.shallowCopy() );
//        newGroup.addRegions( this.regions.shallowCopy() );
//        return newGroup;
//    }
    
    // ------------------ Getters -------------------

    public String getName() { return name; }

    public Color getNodeColor() { return nodeColor; }
    public Color getFacetColor() { return facetColor; }
    public Color getRegionColor() { return regionColor; }

    public NodeVector getNodes() { return nodes; }
    public FacetVector getFacets() { return facets; }
    public RegionVector getRegions() { return regions; }
    public Node getNode(int i) { return nodes.get(i); }
    public Facet getFacet(int i) { return facets.get(i); }
    public Region getRegion(int i) { return regions.get(i); }
    
    public int numberOfNodes() { return nodes.size(); }
    public int numberOfFacets() { return facets.size(); }
    public int numberOfRegions() { return regions.size(); }
    
    public boolean hasFacets() { return facets!=null && !facets.isEmpty(); }

    // ------------------ Setters -------------------

    public void setName(String n) { name = n; }
    
    public final void setColor(Color c) {
        setNodeColor(c);
        setFacetColor(c);
        setRegionColor(c);
    }
    public void setNodeColor(Color c) { nodeColor = c; }
    public void setFacetColor(Color c) { facetColor = c; }
    public void setRegionColor(Color c) { regionColor = c; }

//    public void setRegion(Region r) { region = r; } // use setRegion(null) to nullify the region.

    // -------------------- Private Methods --------------------

//    private void addNodes(NodeVector n) {
//        nodes.addAll(n);
//    }
//    private void addFacets(FacetVector f) {
//        facets.addAll(f);
//    }
//    private void addRegions(RegionVector r) {
//        regions.addAll(r);
//    }

    // -------------------- Public Methods --------------------

    public void addNode(Node n) {
        nodes.add(n);
    }
    public void addFacet(Facet f) {
        facets.add(f);
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

//    /** Clears all plc information. */
//    public void clearPLC() {
//        nodes.clear();
//        facets.clear();
//        regions.clear();
////        region = null;
//    }
//    public void clearFacets() {
//        facets.clear();
//    }
    
//    /** Makes a plc object containing the group's nodes and facets.
//     * @return  */
//    public PLC makePLC() {
//        PLC plc = new PLC();
//        for (int i=0 ; i<this.numberOfNodes() ; i++ ) {
//            plc.addNode(this.getNode(i));
//        }
//        for (int i=0 ; i<this.numberOfFacets(); i++ ) {
//            plc.addFacet(this.getFacet(i));
//        }
//        return plc;
//    }

    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write the group name:
        String textLine = name + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write the group colours:
        textLine = Integer.toString(nodeColor.getRGB()) + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        textLine = Integer.toString(facetColor.getRGB()) + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        textLine = Integer.toString(regionColor.getRGB()) + "\n";
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read the group name:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading group name line."; }
        name = textLine.trim();
        // Read the group colours:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading group node colour line."; }
        try {
            nodeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing group node colour."; }
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading group facet colour line."; }
        try {
            facetColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing group facet colour."; }
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading group region colour line."; }
        try {
            regionColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing group region colour."; }
        // Return successfully:
        return null;
    }

}
