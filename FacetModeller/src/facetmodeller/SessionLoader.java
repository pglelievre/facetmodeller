package facetmodeller;

import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.Projector3D;
import facetmodeller.plc.Facet;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.plc.PLC;
import facetmodeller.plc.Region;
import facetmodeller.sections.ImageCrossSection;
import facetmodeller.sections.NoImageCrossSection;
import facetmodeller.sections.NoImageDepthSection;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import fileio.FileUtils;
import geometry.Dir3D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import geometry.Matrix3D;

/** Static methods for loading a previously saved session.
 * Every time the floored version number (e.g. 1.*) is changed, a new load method should be added to this class.
 * The floored version number should only be changed if a new load method is completely required!
 * The names of the older static methods should be suffixed with their version numbers.
 * @author Peter Lelievre
 */
@SuppressWarnings("PublicInnerClass")
public class SessionLoader {

    private static final int LARGE_MODEL=3000; // HARDWIRE
    
    /** Loads a previously saved ascii session file.
     * @param controller
     * @param file
     * @param merge
     * @return Less than or equal to 0 if an error occurs.
     */
    public static LoadSessionReturnObject loadSessionAscii(FacetModeller controller, File file, boolean merge) {
        // First try the current version:
        LoadSessionReturnObject out = loadSessionAscii3(controller,file,merge);
        if (out.message!=null) { return out; }
        // Now try older versions:
        if (out.version==2) {
            if (!loadSessionAscii2(controller,file,merge)) {
                out.message = "Failed to open session from earlier version 2 of FacetModeller.";
            }
        } else if (out.version==1) {
            if (!loadSessionAscii1(controller,file,merge)) {
                out.message = "Failed to open session from earlier version 1 of FacetModeller.";
            }
        }
        return out;
    }
    
    @SuppressWarnings("PublicField")
    public static class LoadSessionReturnObject {
        public int version = 0;
        public String message = null; // set to non-null if there is a problem
        public LoadSessionReturnObject(int v, String s) {
            version = v;
            message = s;
        }
    }

    private static LoadSessionReturnObject loadSessionAscii3(FacetModeller controller, File file, boolean merge) {
        
        int loadVersion = 3; // THIS VALUE MUST BE THE SAME AS THE METHOD PREFIX

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) {
            return new LoadSessionReturnObject(0,"Opening file for reading.");
        }

        // Put everything below in an infinite loop that we can break out of when something goes wrong:
        boolean ok = true;
        String message = "Unspecified file format error.";
        while(true) {
            String textLine;
            String[] ss;

            // Skip the first line if commented:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Skipping first line."; break; }
            if (textLine.startsWith("#")) { // (skip the first line if commented)
                textLine = FileUtils.readLine(reader);
            }
            
            // Read the floored version number:
            if (textLine==null) { ok=false; message="Reading floored version number."; break; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { ok=false; message="Not enough values on floored version line."; break; }
            int version;
            try {
                version = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { ok=false; message="Parsing floored version number."; break; }
            if (!ok) { break; }

            // Check the version number:
            if ( version != loadVersion ) {
                // Close the file:
                FileUtils.close(reader);
                // Return unsuccessfully:
                return new LoadSessionReturnObject(version,"Unexpected version number.");
            }
            
            // ---------- Read the model information: ----------
            
            message = controller.getModelManager().readSessionInformation(reader,merge);
            if (message!=null) { ok=false; break; }
            
            // Reset the vectors in the selector objects:
            // (this must happen before reading the display options, which contains information about the selections)
            if (controller.numberOfDimensions()==3) {
                controller.setSectionVector(controller.getModelManager().getSections());
            }
            controller.setGroupVector(controller.getModelManager().getGroups());
            
            // ---------- Read the display options: ----------

            // Can stop reading now if merging:
            if (merge) { break; }
            
            // Read the commented start of the display options:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Skipping start of display options."; break; }
            
            // Check the format of the display options:
            if (textLine.startsWith("# DISPLAY OPTIONS")) { // old display options format

                // We will be saving some information to set later:
                Color calibrationColor, edgeColor, defineFacetEdgeColor;
                int pointWidth, lineWidth, shiftStep2D, panStep2D; //, panStep3D;
                double pickingDistance, autoFacetFactor;
                Matrix3D rotationMatrix3D = Projector3D.viewDefaultMatrix();
            
                // Read painting colours, etc.:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading calibration colour."; break; }
                try {
                    calibrationColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; message="Parsing calibration colour."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading edge colour."; break; }
                try {
                    edgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; message="Parsing edge colour."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading defining facets edge colour."; break; }
                try {
                    defineFacetEdgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; message="Parsing defining facets edge colour."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading point size."; break; }
                try {
                    pointWidth = Integer.parseInt(textLine.trim());
                } catch (NumberFormatException e) { ok=false; message="Parsing point size."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading line width."; break; }
                try {
                    lineWidth = Integer.parseInt(textLine.trim());
                } catch (NumberFormatException e) { ok=false; message="Parsing line width."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading picking distance."; break; }
                try {
                    pickingDistance = Double.parseDouble(textLine.trim());
                } catch (NumberFormatException e) { ok=false; message="Parsing picking distance."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading auto facet factor."; break; }
                try {
                    autoFacetFactor = Double.parseDouble(textLine.trim());
                } catch (NumberFormatException e) { ok=false; message="Parsing auto facet factor."; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { break; } // shift step may not be present (late addition to saved session file for version 3)
                try {
                    shiftStep2D = Integer.parseInt(textLine.trim());
                } catch (NumberFormatException e) { ok=false; message="Parsing shift step."; break; }
                // The next line might be "panStep2D" or "panStep2D panStep3D":
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { break; } // 2D & 3D pan step may not be present (late addition to saved session file for version 3)
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",3);
                if (ss.length<1) { ok=false; message="Reading 2D pan step."; break; }
                try {
                    panStep2D = Integer.parseInt(ss[0]);
                } catch (NumberFormatException e) { ok=false; message="Parsing 2D pan step."; break; }
    //            if (ss.length>1) { // panStep3D exists on the line
    //            try {
    //                panStep3D = Integer.parseInt(ss[1]);
    //            } catch (NumberFormatException e) { ok=false; message="Parsing 3D pan step."; break; }

                // The next line might contain panStep3D (a single integer)
                // or contain 9 double values for the 3D viewing angle:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { break; } // this line may not be present (late addition to saved session file for version 3)
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",10);
                if (ss.length<9) {
                    // Read the 3D pan step:
    //                try {
    //                    panStep3D = Integer.parseInt(ss[1]);
    //                } catch (NumberFormatException e) { ok=false; message="Parsing 3D pan step."; break; }
                } else {
                    // Read 3D viewing angle information:
                    message = rotationMatrix3D.readSessionInformation(reader,textLine);
                    if (message!=null) { ok=false; break; }
                }
                
                // Reset the display options:
                controller.setCalibrationColor(calibrationColor);
                controller.setEdgeColor(edgeColor);
                controller.setDefineFacetEdgeColor(defineFacetEdgeColor);
                controller.setPointWidth(pointWidth);
                controller.setLineWidth(lineWidth);
                controller.setPickingDistance(pickingDistance);
                controller.setAutoFacetFactor(autoFacetFactor);
                controller.setShiftStep2D(shiftStep2D);
                controller.setPanStep2D(panStep2D);
    //            controller.setPanStep3D(panStep3D);
                controller.setRotationMatrix3D(rotationMatrix3D);
            
            } else { // new display options format
                
                // Read view manager options:
                message = controller.getViewManager().readSessionInformation(reader,merge); // merge used in at least one place
                if (message!=null) { ok=false; break; }

                // Skip the commented start of the interaction options:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Skipping start of interaction options."; break; }

                // Read interaction manager options:
                message = controller.getInteractionManager().readSessionInformation(reader,merge); // merge not used
                if (message!=null) { ok=false; break; }

                // Skip the commented start of the file i/o options:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { break; } // accept this because the file i/o options were not previously written to the session file
                
                // Read file i/o manager options:
                message = controller.getFileIOManager().readSessionInformation(reader,merge); // merge not used
                if (message!=null) { ok=false; break; }
                
            }
            //if (!ok) { break; }
            
            // Always break from while here:
            break;

        }

        // Close the file:
        FileUtils.close(reader);

        // Check for a problem:
        if (!ok) { return new LoadSessionReturnObject(0,message); }
        
        // If it is a large model then don't display anything:
        if (controller.numberOfNodes()>=LARGE_MODEL) {
            controller.clearGroupSelections();
        }

        // Return successfully:
        return new LoadSessionReturnObject(loadVersion,null);

    }

    private static boolean loadSessionAscii2(FacetModeller controller, File file, boolean merge) {
        int loadVersion = 2;
        
        // We will be constructing some new objects as we read the file:
        PLC plc = new PLC();
        SectionVector sections = new SectionVector();
        GroupVector groups = new GroupVector();

        // We will be saving some information to set later:
        int ndim;
        Color calibrationColor = Color.CYAN; // these colours will be overwritten
        Color edgeColor = Color.BLACK;
        Color defineFacetEdgeColor = Color.WHITE;
        int pointWidth = 5;
        int lineWidth = 1;

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return false; }

        // Put everything below in an infinite loop that we can break out of when something goes wrong:
        boolean ok = true;
        while(true) {

            // Read the floored version number:
            String textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            textLine = textLine.trim();
            String[] ss = textLine.split("[ ]+",2);
            int version;
            try {
                version = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { ok=false; break; }
            if (!ok) { break; }

            // Check the version number:
            if ( version != loadVersion ) {
                // Close the file:
                FileUtils.close(reader);
                // Return unsuccessfully:
                return false;
            }

            // Read the number of dimensions, nodes, facets, regions, samples and groups:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            int nnodes, nregions, nfacets, nsections, ngroups;
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",7);
            try {
                ndim      = Integer.parseInt(ss[0].trim()); // converts to integer
                nnodes    = Integer.parseInt(ss[1].trim()); // converts to integer
                nfacets   = Integer.parseInt(ss[2].trim()); // converts to integer
                nregions  = Integer.parseInt(ss[3].trim()); // converts to integer
                nsections = Integer.parseInt(ss[4].trim()); // converts to integer
                ngroups   = Integer.parseInt(ss[5].trim()); // converts to integer
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
            if (!ok) { break; }
            
            // Check ndim:
            if (ndim!=controller.numberOfDimensions()) { ok=false; break; }

            // ---------- IN THE FIRST PASS I READ ALL INFORMATION OTHER THAN ID'S AND CREATE NEW OBJECTS ----------

            // Loop over each node:
            for (int i=0 ; i<nnodes ; i++ ) {
                // Read the coordinates of the ith node:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                boolean isTopo;
                double x,y,z=0.0; // initialization of z is needed to avoid compiler warning
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",4);
                try {
                    isTopo = Boolean.parseBoolean(ss[0].trim());
                    x = Double.parseDouble(ss[1].trim()); // converts to Double
                    y = Double.parseDouble(ss[2].trim()); // converts to Double
                    if (isTopo) { z = Double.parseDouble(ss[3].trim()); }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                if (!ok) { break; }
                // Add a new node to the plc:
                if (isTopo) {
                    plc.addNode( new NodeOffSection(x,y,z) );
                } else {
                    plc.addNode( new NodeOnSection(x,y) );
                } // section and group membership will be added later
            }
            if (!ok) { break; }

            // Loop over each facet:
            for (int i=0 ; i<nfacets ; i++ ) {
                // Add a new empty facet to the plc (these facets are filled later):
                plc.addFacet( new Facet() );
            }

            // Loop over each region:
            for (int i=0 ; i<nregions ; i++ ) {
                // Read the coordinates of the ith region and the isControl information:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                double x,y;
                boolean isCon;
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",4);
                // Try parsing coordinates (must be able to do this):
                try {
                    x = Double.parseDouble(ss[0].trim()); // converts to Double
                    y = Double.parseDouble(ss[1].trim()); // converts to Double
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                if (!ok) { break; }
                // Check for iscontrol information:
                if (ss.length<3) {
                    isCon = false;
                } else {
                   if (ss[2].trim().isEmpty()) { // missing from file (old version of session saver was used)
                       isCon = false;
                   } else {
                       isCon = Boolean.parseBoolean(ss[2].trim()); // converts to Boolean
                   }
                }
                // Add a new region to the plc:
                plc.addRegion( new Region(isCon,x,y) ); // section and group membership will be added later
            }
            if (!ok) { break; }

            for (int i = 0; i<nsections; i++) {
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                int sectionType;
                try {
                    sectionType = Integer.parseInt(textLine);
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                Section section=null;
                switch (sectionType) {
                    case 1:
                        // Read the file name:
                        textLine = FileUtils.readLine(reader);
                        if (textLine==null) {
                            ok=false;
                            break;
                        }
                        textLine = textLine.trim();
                        File imageFile;
                        if (textLine.startsWith("null")) {
                            imageFile = null;
                        } else {
                            try {
                                URI uri = new URI(textLine);
                                imageFile = new File(uri); // image file or .node file
                            } catch (URISyntaxException e) {
                                ok=false;
                                break;
                            }
                        }
                        // Make a new HasImage object associated with the file:
                        section = new ImageCrossSection(imageFile);
                        break;
                    case 3:
                        // Read the section name:
                        textLine = FileUtils.readLine(reader);
                        if (textLine==null) {
                            ok=false;
                            break;
                        }
                        String name = textLine.trim();
                        // Read the image height:
                        textLine = FileUtils.readLine(reader);
                        if (textLine==null) {
                            ok=false;
                            break;
                        }
                        textLine = textLine.trim();
                        int height;
                        try {
                            height = Integer.parseInt(textLine);
                        } catch (NumberFormatException e) {
                            ok=false;
                            break;
                        }
                        // Read the image color:
                        Color color;
                        try {
                            color = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                        } catch (NumberFormatException e) {
                            ok=false;
                            break;
                        }
                        section = new NoImageCrossSection(name,color);
                        break;
                    case 2:
                        File nodeFile=null;
                        File eleFile=null;
                        // Read the node file name:
                        textLine = FileUtils.readLine(reader);
                        if (textLine==null) {
                            ok=false;
                            break;
                        }
                        textLine = textLine.trim();
                        if (textLine.startsWith("null")) {
                            nodeFile = null;
                        } else {
                            URI uri=null;
                            try {
                                uri = new URI(textLine);
                            } catch (URISyntaxException e) { ok=false; }
                            if (!ok) {
                                break;
                            }
                            try {
                                nodeFile = new File(uri); // image file or .node file
                            } catch (IllegalArgumentException e) { ok=false; }
                        }
                        if (!ok) { break; }
                        // Read the ele file name:
                        textLine = FileUtils.readLine(reader);
                        if (textLine==null) {
                            ok=false;
                            break;
                        }
                        textLine = textLine.trim();
                        if (textLine.startsWith("null")) {
                            eleFile = null;
                        } else {
                            try {
                                URI uri = new URI(textLine);
                                eleFile = new File(uri); // image file or .node file
                            } catch (URISyntaxException e) { ok=false; }
                        }
                        if (!ok) { break; }
                        section = new NoImageDepthSection(nodeFile,eleFile); // formerly a TopoSection
                        break;
                    default:
                        ok=false;
                        break;
                }
                if (!ok) { break; }
                if (section==null) { ok=false; break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                Dir3D sliceDirection;
                try {
                    int idir = Integer.parseInt(ss[0].trim()); // converts to integer
                    sliceDirection = Dir3D.fromInt(idir);
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                double loc;
                try {
                    loc = Double.parseDouble(ss[0].trim()); // converts to Double
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                double x,y;
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setTyped1( new MyPoint3D(x,y,sliceDirection,loc) );
                }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setTyped2( new MyPoint3D(x,y,sliceDirection,loc) );
                }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setClicked1(new MyPoint2D(x,y));
                }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setClicked2(new MyPoint2D(x,y));
                }
                sections.add(section);
            }
            if (!ok) { break; }

            // Loop over each group:
            for (int i=0 ; i<ngroups ; i++ ) {
                // Create the group object:
                Group group = new Group();
                // Read the group name:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                group.setName(textLine.trim());
                // Read the group colours:
                Color col;
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                try {
                    col = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; break; }
                group.setNodeColor(col);
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                try {
                    col = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; break; }
                group.setFacetColor(col);
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                try {
                    col = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; break; }
                group.setRegionColor(col);
                // Add the group to the list of groups:
                groups.add(group);
            }
            if (!ok) { break; }

            // ---------- IN THE SECOND PASS I READ THE ID'S AND SET THE CROSS-LINKAGES ----------

            // Loop over each node:
            for (int i=0 ; i<nnodes ; i++ ) {
                Node node = plc.getNode(i);
                // The node gets linked to the facets in the loop over each facet below.
                // Read the section id and group id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",3);
                int sid, gid; // section and group id
                try {
                    sid = Integer.parseInt(ss[0].trim()); // converts to integer
                    gid = Integer.parseInt(ss[1].trim()); // converts to integer
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                // Cross-link the node and section:
                node.setSection( sections.get(sid) );
                sections.get(sid).addNode(node);
                // Cross-link the node and group:
                node.setGroup( groups.get(gid) );
                groups.get(gid).addNode(node);
            }
            if (!ok) { break; }

            // Loop over each facet:
            for (int i=0 ; i<nfacets ; i++ ) {
                Facet facet = plc.getFacet(i);
                // Read the node id's and link those nodes to the facet:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+");
                int n; // number of nodes
                try {
                    n = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                for (int j=0 ; j<n ; j++ ) {
                    int id; // node id
                    try {
                        id = Integer.parseInt(ss[j+1].trim()); // converts to integer
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    // Cross-link the facet and node:
                    facet.addNode( plc.getNode(id) );
                    plc.getNode(id).addFacet( facet );

                }
                // Read the section id's:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                /*
                textLine = textLine.trim();
                ss = textLine.split("[ ]+");
                try {
                    n = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                for (int j=0 ; j<n ; j++ ) {
                    int id;
                    try {
                        id = Integer.parseInt(ss[j+1].trim()); // converts to integer
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    // Cross-link the facet and section:
//                    facet.addSection( sections.get(id) ); // no longer necessary because facet sections defined by the facet nodes
                    sections.get(id).addFacet(facet);
                }
                */
                // Read the group id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                int id;
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the facet and group:
                facet.setGroup( groups.get(id) );
                groups.get(id).addFacet(facet);
            }
            if (!ok) { break; }

            // Loop over each region:
            for (int i=0 ; i<nregions ; i++ ) {
                Region region = plc.getRegion(i);
                // Read the section id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                int id;
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the region and section:
                region.setSection( sections.get(id) );
                sections.get(id).addRegion(region);
                // Read the group id and link that group to the node:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the region and group:
                region.setGroup( groups.get(id) );
                //groups.get(id).setRegion(region);
                groups.get(id).addRegion(region);
            }
            if (!ok) { break; }

            // ---------- Finish with the rest of the information: ----------

            // Read painting colours, etc.:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                calibrationColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                edgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                defineFacetEdgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                pointWidth = Integer.parseInt(textLine.trim());
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                lineWidth = Integer.parseInt(textLine.trim());
            } catch (NumberFormatException e) { ok=false; break; }

            // Always break from while here:
            break;

        }

        // Close the file:
        FileUtils.close(reader);

        // Check for a problem:
        if (!ok) { return false; }

        // Reset the FacetModeller plc, section lists and group lists:
        controller.resetPLC(plc,merge);
        controller.resetSectionVector(sections,merge);
        controller.resetGroupVector(groups,merge);
        
        // If it is a large model then don't display anything:
        if (plc.numberOfNodes()>=LARGE_MODEL) {
            controller.clearGroupSelections();
        }

        // Reset some other information:
        if (!merge) {
            controller.setCalibrationColor(calibrationColor);
            controller.setEdgeColor(edgeColor);
            controller.setDefineFacetEdgeColor(defineFacetEdgeColor);
            controller.setPointWidth(pointWidth);
            controller.setLineWidth(lineWidth);
        }

        // Return successfully:
        return true;

    }

    private static boolean loadSessionAscii1(FacetModeller controller, File file, boolean merge) {
        int loadVersion = 1;
        
        // We will be constructing some new objects as we read the file:
        PLC plc = new PLC();
        SectionVector sections = new SectionVector();
        GroupVector groups = new GroupVector();

        // We will be saving some information to set later:
        int ndim;
        Dir3D sliceDirection = Dir3D.X;
        Color calibrationColor = Color.CYAN; // these colours will be overwritten
        Color edgeColor = Color.BLACK;
        Color defineFacetEdgeColor = Color.WHITE;
        int pointWidth = 5;
        int lineWidth = 1;

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return false; }

        // Put everything below in an infinite loop that we can break out of when something goes wrong:
        boolean ok = true;
        while(true) {

            // Read the floored version number:
            String textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            textLine = textLine.trim();
            String[] ss = textLine.split("[ ]+",2);
            int version;
            try {
                version = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { ok=false; break; }
            if (!ok) { break; }

            // Check the version number:
            if ( version != loadVersion ) {
                // Close the file:
                FileUtils.close(reader);
                // Return unsuccessfully:
                return false;
            }

            // Read the number of dimensions, nodes, facets, regions, samples and groups:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            int nnodes, nregions, nfacets, nsections, ngroups;
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",7);
            try {
                ndim      = Integer.parseInt(ss[0].trim()); // converts to integer
                nnodes    = Integer.parseInt(ss[1].trim()); // converts to integer
                nfacets   = Integer.parseInt(ss[2].trim()); // converts to integer
                nregions  = Integer.parseInt(ss[3].trim()); // converts to integer
                nsections = Integer.parseInt(ss[4].trim()); // converts to integer
                ngroups   = Integer.parseInt(ss[5].trim()); // converts to integer
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
            if (!ok) { break; }
            
            // Check ndim:
            if (ndim!=controller.numberOfDimensions()) { ok=false; break; }

            // ---------- IN THE FIRST PASS I READ ALL INFORMATION OTHER THAN ID'S AND CREATE NEW OBJECTS ----------

            // Loop over each node:
            for (int i=0 ; i<nnodes ; i++ ) {
                // Read the coordinates of the ith node:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                double x,y;
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",3);
                try {
                    x = Double.parseDouble(ss[0].trim()); // converts to Double
                    y = Double.parseDouble(ss[1].trim()); // converts to Double
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                if (!ok) { break; }
                // Add a new node to the plc:
                plc.addNode( new NodeOnSection(x,y) ); // section and group membership will be added later
            }
            if (!ok) { break; }

            // Loop over each facet:
            for (int i=0 ; i<nfacets ; i++ ) {
                // Add a new empty facet to the plc (these facets are filled later):
                plc.addFacet( new Facet() );
            }

            // Loop over each region:
            for (int i=0 ; i<nregions ; i++ ) {
                // Read the coordinates of the ith region:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                double x,y;
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",3);
                try {
                    x = Double.parseDouble(ss[0].trim()); // converts to Double
                    y = Double.parseDouble(ss[1].trim()); // converts to Double
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                if (!ok) { break; }
                // Add a new region to the plc:
                plc.addRegion( new Region(false,x,y) ); // section and group membership will be added later
            }
            if (!ok) { break; }

            // Loop over each section:
            for (int i=0 ; i<nsections ; i++ ) {
                // Read the file name:
                textLine = FileUtils.readLine(reader);
                File f;
                if (textLine==null) { ok=false; break; }
                try {
                    URI uri = new URI(textLine);
                    f = new File(uri);
                } catch (URISyntaxException e) { ok=false; break; }
                if (!ok) {break;}
                // Create a new Section object linked to that file:
                Section section = new ImageCrossSection(f);
                // Read the location coordinate:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                double loc;
                try {
                    loc = Double.parseDouble(ss[0].trim()); // converts to Double
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                // Read the typed and clicked points:
                double x,y;
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setTyped1( new MyPoint3D(x,y,sliceDirection,loc) );
                }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setTyped2( new MyPoint3D(x,y,sliceDirection,loc) );
                }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setClicked1(new MyPoint2D(x,y));
                }
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                if (!textLine.startsWith("null")) {
                    ss = textLine.split("[ ]+",3);
                    try {
                        x = Double.parseDouble(ss[0].trim()); // converts to Double
                        y = Double.parseDouble(ss[1].trim()); // converts to Double
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    if (!ok) { break; }
                    section.setClicked2(new MyPoint2D(x,y));
                }
                // Add the section to the list of sections:
                sections.add(section);
            }
            if (!ok) { break; }

            // Loop over each group:
            for (int i=0 ; i<ngroups ; i++ ) {
                // Create the group object:
                Group group = new Group();
                // Read the group name:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                group.setName(textLine.trim());
                // Read the group colours:
                Color col;
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                try {
                    col = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; break; }
                group.setNodeColor(col);
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                try {
                    col = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; break; }
                group.setFacetColor(col);
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                try {
                    col = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
                } catch (NumberFormatException e) { ok=false; break; }
                group.setRegionColor(col);
                // Add the group to the list of groups:
                groups.add(group);
            }
            if (!ok) { break; }

            // ---------- IN THE SECOND PASS I READ THE ID'S AND SET THE CROSS-LINKAGES ----------

            // Loop over each node:
            for (int i=0 ; i<nnodes ; i++ ) {
                Node node = plc.getNode(i);
                // The node gets linked to the facets in the loop over each facet below.
                // Read the section id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                int id; // section id
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the node and section:
                node.setSection( sections.get(id) );
                sections.get(id).addNode(node);
                // Read the group id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the node and group:
                node.setGroup( groups.get(id) );
                groups.get(id).addNode(node);
            }
            if (!ok) { break; }

            // Loop over each facet:
            for (int i=0 ; i<nfacets ; i++ ) {
                Facet facet = plc.getFacet(i);
                // Read the node id's and link those nodes to the facet:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+");
                int n; // number of nodes
                try {
                    n = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                for (int j=0 ; j<n ; j++ ) {
                    int id; // node id
                    try {
                        id = Integer.parseInt(ss[j+1].trim()); // converts to integer
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    // Cross-link the facet and node:
                    facet.addNode( plc.getNode(id) );
                    plc.getNode(id).addFacet( facet );

                }
                // Read the section id's:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                /*
                textLine = textLine.trim();
                ss = textLine.split("[ ]+");
                try {
                    n = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                if (!ok) { break; }
                for (int j=0 ; j<n ; j++ ) {
                    int id;
                    try {
                        id = Integer.parseInt(ss[j+1].trim()); // converts to integer
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; break; }
                    // Cross-link the facet and section:
//                    facet.addSection( sections.get(id) ); // no longer necessary because facet sections defined by the facet nodes
                    sections.get(id).addFacet(facet);
                }
                */
                // Read the group id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                int id;
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the facet and group:
                facet.setGroup( groups.get(id) );
                groups.get(id).addFacet(facet);
            }
            if (!ok) { break; }

            // Loop over each region:
            for (int i=0 ; i<nregions ; i++ ) {
                Region region = plc.getRegion(i);
                // Read the section id:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                int id;
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the region and section:
                region.setSection( sections.get(id) );
                sections.get(id).addRegion(region);
                // Read the group id and link that group to the node:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                try {
                    id = Integer.parseInt(ss[0].trim()); // converts to integer
                } catch (NumberFormatException e) { ok=false; break; }
                // Cross-link the region and group:
                region.setGroup( groups.get(id) );
                //groups.get(id).setRegion(region);
                groups.get(id).addRegion(region);
            }
            if (!ok) { break; }

            // ---------- Finish with the rest of the information: ----------

            // Read slice direction:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            //textLine = textLine.trim();
            //ss = textLine.split("[ ]+",2);
            //try {
            //    int idir = Integer.parseInt(ss[0].trim()); // converts to integer
            //    sliceDirection = Dir3D.fromInt(idir);
            //} catch (NumberFormatException e) { ok=false; break; }

            // Read painting colours, etc.:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                calibrationColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                edgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                defineFacetEdgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                pointWidth = Integer.parseInt(textLine.trim());
            } catch (NumberFormatException e) { ok=false; break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; break; }
            try {
                lineWidth = Integer.parseInt(textLine.trim());
            } catch (NumberFormatException e) { ok=false; break; }

            // Always break from while here:
            break;

        }

        // Close the file:
        FileUtils.close(reader);

        // Check for a problem:
        if (!ok) { return false; }

        // Reset the FacetModeller plc, section lists and group lists:
        controller.resetPLC(plc,merge);
        controller.resetSectionVector(sections,merge);
        controller.resetGroupVector(groups,merge);
        
        // If it is a large model then don't display anything:
        if (plc.numberOfNodes()>=LARGE_MODEL) {
            controller.clearGroupSelections();
        }

        // Reset some other information:
        if (!merge) {
            controller.setCalibrationColor(calibrationColor);
            controller.setEdgeColor(edgeColor);
            controller.setDefineFacetEdgeColor(defineFacetEdgeColor);
            controller.setPointWidth(pointWidth);
            controller.setLineWidth(lineWidth);
        }

        // Return successfully:
        return true;

    }

}
