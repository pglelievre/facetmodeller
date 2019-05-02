package facetmodeller.gui;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.plc.Node;
import facetmodeller.sections.Section;
import fileio.FileUtils;
import fileio.SessionIO;
import geometry.MyPoint3D;
import gui.CommonPaintingOptions;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JColorChooser;

/** Manages the painting options.
 * @author Peter
 */
public final class PaintingOptions extends CommonPaintingOptions implements SessionIO {
    
    public static final Color DEFAULT_SECTION_COLOR = Color.WHITE;
    public static final Color DEFAULT_EDGE_COLOR = Color.BLACK;
    public static final Color DEFAULT_DEFINE_EDGE_COLOR = Color.WHITE;
    public static final Color DEFAULT_NORMAL_COLOR = Color.BLACK;
    public static final double DEFAULT_TRANSPARENCY = 0.5;
    public static final double DEFAULT_NORMAL_LENGTH = 10.0;
    public static final boolean DEFAULT_NORMAL_THICK = true;
    public static final boolean DEFAULT_EDGE_THICK = false;
    
    private final FacetModeller controller;
    
    private Color edgeColor = DEFAULT_EDGE_COLOR; // painting colour for edge overlays
    private Color defineFacetEdgeColor = DEFAULT_DEFINE_EDGE_COLOR; // painting colour for edge overlays when defining facets
    private Color normalColor = DEFAULT_NORMAL_COLOR; // painting colour for facet normals (in 3D panel)
    private MyPoint3D origin3D=null; // the origin of the 3D viewer
    private Node originNode3D=null; // a node to use as the origin of the 3D viewer
    private double transparency=DEFAULT_TRANSPARENCY; // overlay transparency for 2D viewer
    private double normalLength=DEFAULT_NORMAL_LENGTH; // spatial units
    private boolean normalThick=DEFAULT_NORMAL_THICK; // draw normal vectors thick with aliasing (in 3D panel)?
    private boolean edgeThick=DEFAULT_EDGE_THICK; // draw edges thick with aliasing (in 3D panel)?
    
    public PaintingOptions(FacetModeller con) {
        super(con);
        controller = con;
    }
    
    public Color getEdgeColor() { return edgeColor; }
    public Color getDefineFacetEdgeColor() { return defineFacetEdgeColor; }
    public Color getNormalColor() { return normalColor; }
    public MyPoint3D getOrigin3D() { return origin3D; }
    public Node getOriginNode3D() { return originNode3D; }
    public double getVerticalExaggeration() {
        return controller.getProjector3D().getVerticalExaggeration();
    }
    public double getTransparency() { return transparency; }
    public double getNormalLength() { return normalLength; }
    public boolean getNormalThick() { return normalThick; }
    public boolean getEdgeThick() { return edgeThick; }
    
    public void setEdgeColor(Color col) { edgeColor = col; }
    public void setDefineFacetEdgeColor(Color col) { defineFacetEdgeColor = col; }
    private void setNormalColor(Color col) { normalColor = col; }
    public void setOrigin3D(Node node) {
        originNode3D = node;
        origin3D = node.getPoint3D();
    }
    public void setVerticalExaggeration(double d) {
        controller.getProjector3D().setVerticalExaggeration(d);
    }
    public void setTransparency(double d) { transparency = d; }
    public void setNormalLength(double d) { normalLength = d; }
    public void setNormalThick(boolean b) { normalThick = b; }
    public void setEdgeThick(boolean b) { edgeThick = b; }
    
    public void clearOrigin3D() {
        originNode3D = null;
        origin3D = null;
    }

    /** Allows the user to select a paint colour for painting facet edges. */
    public void selectEdgeColor() {
        Color col = JColorChooser.showDialog(controller,"Change Facet Edge Color",getEdgeColor());
        if (col == null) { return; }
        setEdgeColor(col);
        controller.redraw();
    }

    /** Allows the user to select a paint colour for painting facet edges when defining facets. */
    public void selectDefineFacetEdgeColor() {
        Color col = JColorChooser.showDialog(controller,"Change Facet Edge Color When Defining",getDefineFacetEdgeColor());
        if (col == null) { return; }
        setDefineFacetEdgeColor(col);
        controller.redraw();
    }

    /** Allows the user to select a paint colour for painting facet normal vectors. */
    public void selectNormalColor() {
        Color col = JColorChooser.showDialog(controller,"Change Facet Normal Vector Color",getNormalColor());
        if (col == null) { return; }
        setNormalColor(col);
        controller.redraw();
    }

    /** Allows the user to change the vertical exaggeration for the 3D viewer. */
    public void selectVerticalExaggeration() {
        String response = Dialogs.input(controller,"Enter the vertical exaggeration:","Vertical Exaggeration",Double.toString(getVerticalExaggeration()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numeric value. Please try again.","Error");
            return;
        }
        double d;
        try {
            d = Double.parseDouble(ss[0].trim());
            if (d<=0.0) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive value. Please try again.","Error");
            return;
        }
        setVerticalExaggeration(d);
        controller.redraw();
    }

    /** Allows the user to select a colour for the background of the section image panel and 3D viewer panel. */
    public void selectBackgroundColor() {
        Color col = JColorChooser.showDialog(controller,"Choose Background Color",controller.getBackground());
        if (col == null) { return; }
        controller.setBackgroundColor(col);
    }

    /** Allows the user to select a colour for painting sections with no image. */
    public void selectSectionColor() {
        Section currentSection = controller.getSelectedCurrentSection();
        Color c = currentSection.getColor();
        if (c==null) { // shouldn't be possible
            controller.getBackgroundColor();
        }
        Color col = JColorChooser.showDialog(controller,"Choose Color for Current Section",c);
        if (col == null) { return; }
        currentSection.setColor(col);
        controller.redraw2D();
    }

    public void selectTransparency() {
        String response = Dialogs.input(controller,"Enter the transparency factor [0,1] for overlays in the 2D view:","Overlay Transparency",Double.toString(getTransparency()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numerical value. Please try again.","Error");
            return;
        }
        double d;
        try {
            d = Double.parseDouble(ss[0].trim());
            if ( d<0 || d>1 ) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a numerical value on [0,1]. Please try again.","Error");
            return;
        }
        setTransparency(d);
        controller.redraw2D();
    }

    public void selectNormalLength() {
        String response = Dialogs.input(controller,"Enter the plotting length for facet normal vectors (spatial units):","Facet Normal Length",Double.toString(getNormalLength()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single numerical value. Please try again.","Error");
            return;
        }
        double d;
        try {
            d = Double.parseDouble(ss[0].trim());
            if ( d<1 ) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive, non-zero, numerical value. Please try again.","Error");
            return;
        }
        setNormalLength(d);
        controller.redraw3D();
    }

    public void selectNormalThickness() {
        int response = Dialogs.question(controller,"Do you want to draw the normal vectors thickly in the 3D panel?","Facet Normal Thickness");
        switch (response) {
            case Dialogs.YES_OPTION:
                setNormalThick(true);
                break;
            case Dialogs.NO_OPTION:
                setNormalThick(false);
                break;
            default: // Dialogs.CANCEL_OPTION
                return;
        }
        controller.redraw3D();
    }

    public void selectEdgeThickness() {
        int response = Dialogs.question(controller,"Do you want to draw the facet edges thickly in the 3D panel?","Facet Edge Thickness");
        switch (response) {
            case Dialogs.YES_OPTION:
                setEdgeThick(true);
                break;
            case Dialogs.NO_OPTION:
                setEdgeThick(false);
                break;
            default: // Dialogs.CANCEL_OPTION
                return;
        }
        controller.redraw3D();
    }
    
    // Wrappers for the CommonPaintingOptions class:
    @Override
    public void selectCalibrationColor() {
        super.selectCalibrationColor();
        if (getSuccess()) { controller.redraw(); }
    }
    @Override
    public void selectPointWidth() {
        super.selectPointWidth();
        if (getSuccess()) { controller.redraw(); }
    }
    @Override
    public void selectLineWidth() {
        super.selectLineWidth();
        if (getSuccess()) { controller.redraw2D(); }
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write common painting options:
        if(!super.writeSessionInformation(writer)) { return false; }
        // Write edge colour:
        String textLine = Integer.toString(edgeColor.getRGB());
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write facet definition edge colour:
        textLine = Integer.toString(defineFacetEdgeColor.getRGB());
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write origin of the 3D viewer:
        if (origin3D==null) {
            textLine = "null";
        } else {
            textLine = origin3D.toString();
        }
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write the index of the node to use as the origin of the 3D viewer, and the normalLength, and the transparency factor, on the same line:
        if (originNode3D==null) {
            textLine = "null " + normalLength + " " + transparency + " " + normalThick + " " + edgeThick;
        } else {
            textLine = originNode3D.getID() + " " + normalLength + " " + transparency + " " + normalThick + " " + edgeThick;
        }
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write normal colour:
        textLine = Integer.toString(normalColor.getRGB());
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read common painting options:
        String msg = super.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        // Read edge colour:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading edge colour line."; }
        try {
            edgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing edge colour."; }
        // Read facet definition edge colour:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading facet definition edge colour line."; }
        try {
            defineFacetEdgeColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
        } catch (NumberFormatException e) { return "Parsing facet definition edge colour."; }
        // Read origin of the 3D viewer:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading 3D origin coordinates line."; }
        textLine = textLine.trim();
        if (textLine.startsWith("null")) {
            origin3D = null;
        } else {
            String[] s = textLine.split("[ ]+");
            if (s.length<3) { return "Not enough values on 3D origin coordinates line."; }
            try {
                double x = Double.parseDouble(s[0]);
                double y = Double.parseDouble(s[1]);
                double z = Double.parseDouble(s[2]);
                origin3D = new MyPoint3D(x,y,z);
            } catch (NumberFormatException e) { return "Parsing 3D origin coordinates."; }
        }
        // Read the index of the node to use as the origin of the 3D viewer, and the normalLength, from the same line:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading 3D origin node ID line."; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<1) { return "Not enough values on 3D node origin / normal length line."; }
        if (s[0].startsWith("null")) {
            originNode3D = null;
        } else {
            try {
                int id = Integer.parseInt(s[0].trim());
                originNode3D = controller.getNode(id);
                if (originNode3D==null) { return "3D origin node ID not found."; }
                // if ( originNode3D.getID() != id ) { return "3D origin node ID not matched."; }
            } catch (NumberFormatException e) { return "Parsing 3D origin node ID."; }
        }
        if (s.length>1) {
            try {
                normalLength = Double.parseDouble(s[1].trim());
            } catch (NumberFormatException e) { return "Parsing normal length."; }
        }
        if (s.length>2) {
            try {
                transparency = Double.parseDouble(s[2].trim());
            } catch (NumberFormatException e) { return "Parsing transparency factor."; }
        }
        if (s.length>3) {
            try {
                normalThick = Boolean.parseBoolean(s[3].trim());
            } catch (NumberFormatException e) { return "Parsing normal thick."; }
        }
        if (s.length>4) {
            try {
                edgeThick = Boolean.parseBoolean(s[4].trim());
            } catch (NumberFormatException e) { return "Parsing edge thick."; }
        }
        // Read normal colour:
        if (s.length>4) { // this additional line was added to the session file format in the same commit as the two items above 
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Reading normal colour line."; }
            try {
                normalColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { return "Parsing normal colour."; }
        }
        // Return successfully:
        return null;
    }
    
}
