package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.VOI;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.gui.Projector3D;
import facetmodeller.gui.SceneInfo;
import facetmodeller.gui.ZBuffer3D;
import facetmodeller.gui.ZoomerDefault;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.Region;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import fileio.FileUtils;
import fileio.SessionIO;
import geometry.Circle;
import geometry.Matrix3D;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** A 3D view panel using simple z-buffer approach.
 * @author Peter Lelievre
 */
public class View3DPanel extends PanningPanel implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    // ------------------- Properties ------------------

    private final FacetModeller controller;
    private ZoomerDefault zoomer = null;
    private ZBuffer3D zbuf = null; // z-buffer object for painting the scene onto an image
    private MyPoint3D[] projectedPoints = null; // working object for projecting nodes onto the current view
    private boolean drawn; // used to signal to the monitors that the scene is drawn
    
    // Properties required for projection:
    //private final double cameraDistance=2000.0; // distance from viewer to screen in pixels (approximate)
    //private MyPoint3D spaceOrigin; // the origin of the model (spatial coordinates)
    //private MyPoint3D imageOrigin; // the centre of the image (pixel coordinates)
    //private double spaceToImageScaling; // scaling between spatial and image coordinates
    //private Matrix3D rotationMatrix, rotationMatrixInv; // rotation matrices for the projection
    //private boolean perspective = true; // perspective (true) or parallel (false) projection
    private final Projector3D projector;
    
    // Properties required for dragging:
    private final double dragSensitivity = Math.toRadians(1.0); // radians per pixel
    private Circle dragSphere; // the scene rotation sphere across which mouse drag events operate
    
    // Viewing options:
    private final int DRAG_MODE_PAN=1; // dragging pans instead of rotates
    private final int DRAG_MODE_SIMPLE=2; // rotate using principal axes
    private final int DRAG_MODE_FANCY=3; // rotate around a control sphere
    private int dragMode = DRAG_MODE_SIMPLE;
    private boolean showAxes = true;
    private boolean centreAxes = false;
    
    // Drawing hardwires: TODO: remove these hardwires
    private final int AXIS_LENGTH = 25; // length in pixels
    
    // ------------------- Constructor ------------------

    public View3DPanel(FacetModeller con) {
        
        super();
        controller = con;
        zoomer = new ZoomerDefault(0,-2,2000,1.2d); // the initial view fits the model to the panel width
        
        // Set mouse and keyboard listeners:
        addListeners();
        
        // Set projector with default view:
        projector = new Projector3D(true);
        
        // Set drawn flag to false: (set to true once actually finished drawing)
        drawn = false;
        
    }
    private void addListeners() {
        addMouseListener(new View3DPanel.MouseClickMonitor()); // listens for mouse clicks
        addMouseMotionListener(new View3DPanel.MouseMoveMonitor()); // listens for mouse motion
        addComponentListener(new PanelSizeMonitor()); // listens for panel resizing
    }
    
    // ------------------- Getters ------------------
    
    public Projector3D getProjector() { return projector; }
    public Matrix3D getRotationMatrix() { return projector.getRotationMatrix(); }
    
    // ------------------- Setters ------------------
    
    public void setRotationMatrix(Matrix3D m) { projector.setRotationMatrix(m); }
    
    // -------------------- Implemented Zoomer Methods --------------------
    
    public void zoomIn() {
        zoomer.zoomIn();
        repaint();
    }
    public void zoomOut() {
        zoomer.zoomOut();
        repaint();
    }
    public void zoomReset() {
        zoomer.zoomReset();
        setPanX(0);
        setPanY(0);
        repaint();
    }
    
    // --------------------Methods associated with the ViewBar class --------------------
    
//    public void viewDefault() {
//        projector.viewDefault();
//        repaint();
//    }
    
    public void viewDown() {
        projector.viewDown();
        repaint();
    }
    
    public void viewUp() {
        projector.viewUp();
        repaint();
    }
    
    public void viewNorth() {
        projector.viewNorth();
        repaint();
    }
    
    public void viewSouth() {
        projector.viewSouth();
        repaint();
    }
    
    public void viewEast() {
        projector.viewEast();
        repaint();
    }
    
    public void viewWest() {
        projector.viewWest();
        repaint();
    }
    
    public void viewParallel() {
        projector.viewParallel();
        repaint();
    }
    
    public void viewPerspective() {
        projector.viewPerspective();
        repaint();
    }
    
    public void setDragMode(int mode) {
        dragMode = mode;
    }
    
    public void rotateSimple() {
        dragMode = DRAG_MODE_SIMPLE;
    }
    
    public void rotateFancy() {
        dragMode = DRAG_MODE_FANCY;
    }
    
    public void pan() {
        dragMode = DRAG_MODE_PAN;
    }
    
    @Override
    public void clearPan() {
        super.clearPan();
        repaint();
    }
    
    public void toggleShowAxes() {
        showAxes = !showAxes;
        repaint();
    }
    
    public void toggleCentreAxes() {
        centreAxes = !centreAxes;
        showAxes = true;
        repaint();
    }
    
    // -------------------- Overridden Methods --------------------

    /** Paints graphics on the panel.
     * @param g Graphics context in which to draw.
     */
    @Override
    public void paintComponent(Graphics g) {
        
        // Only draw 3D if panel is open and ndim==3:
        if ( !controller.is3D() || !controller.getShowView3DPanel() ) { return; }
        
        // Remove image from the panel:
        //removeAll(); 
        //updateUI();
        
        // Make sure the drawn flag is false:
        drawn = false;
        
        // Paint background:
        super.paintComponent(g);
        
        // Clear the list of projected nodes:
        projectedPoints = null;
        
        // Calculate the properties required for projection:
        SceneInfo info = controller.getSceneInfo3D();
        if (info==null) { return; } // no voi or no nodes in plc
        if (info.getScaling()==0.0) { return; }
        MyPoint3D spaceOrigin = info.getOrigin();
        projector.setSpaceOrigin(spaceOrigin);
        if (spaceOrigin==null) { return; }
        int w = getWidth(); // panel width
        int h = getHeight(); // panel height
        double x = w;
        double y = h;
        double imageSizeScaling = Math.min(x,y); // smallest panel dimension
        x /= 2.0; // half panel width
        y /= 2.0; // half panel height
        double r = Math.sqrt( x*x + y*y ); // radius of circle surrounding the panel
        MyPoint3D imageOrigin = new MyPoint3D(x,y,0.0);
        //imageOrigin.plus(controller.getPanX3D(),controller.getPanY3D(),0); // this does the panning via pan buttons
        imageOrigin.plus(getPanX(), getPanY(),0); // this does the panning via dragging
        projector.setImageOrigin(imageOrigin);
        double sceneAndZoomScaling = info.getScaling() * zoomer.getScaling();
        if (sceneAndZoomScaling==0.0) { return; }
        if (imageSizeScaling==0.0) { return; }
        projector.setSceneAndZoomScaling(sceneAndZoomScaling);
        projector.setImageSizeScaling(imageSizeScaling);
        dragSphere = new Circle(0,0,r);
        
        // Project all the nodes and reset the node IDs:
        ModelManager model = controller.getModelManager();
        int n = model.numberOfNodes();
        projectedPoints = new MyPoint3D[n];
        for (int i=0 ; i<n; i++ ) { // loop over each node
            Node node = model.getNode(i);
            // Set the node ID equal to i so I can use the ID's as indices into the projectedPoints array:
            node.setID(i);
            // Transform the point to image coordinates:
            MyPoint3D p = spaceToImage(node.getPoint3D()); // p is a deep copy of node.getPoint3D()
            // Save the transformed point in the list:
            projectedPoints[i] = p;
        }
        
        // If rotating then I'll only draw minimal information using 2D graphics,
        // otherwise I'll draw everything using a zbuffer. I'll deal with this below
        // by checking the status of the pt1 variable. I'll need some common information first:
//        boolean showOther = controller.getShowOther(); // if true then draws outlines around other sections
        boolean showOutlines = controller.getShowOutlines(); // if true then draws outlines around other sections
        boolean showAll = controller.getShowAll(); // if false then only show nodes/facets associated with selected section(s)
        
        // Get drawing styles for overlays:
        final int edgeWidth = controller.getLineWidth();
        final int nodeWidth = controller.getPointWidth();
        final int centroidWidth = (int)Math.ceil(nodeWidth/2.0); // centroids drawn half the node width
        final double normalLength = controller.getNormalLength();
        
        // Initialize the zbuffer or Graphics2D object:
        Graphics2D g2 = (Graphics2D) g;
        MyPoint3D pt1 = getPt1();
        if (pt1==null) {
           zbuf = new ZBuffer3D( w, h, -Double.MAX_VALUE, getBackground() );
        } else {
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            //g2.setStroke(new BasicStroke(edgeWidth)); // line style for facet edges
            w = nodeWidth;
        }
        
        // Get the other section(s) being displayed (if any exist):
        Section currentSection = controller.getSelectedCurrentSection();
        SectionVector otherSections;
//        if ( showOther || showAll ) {
        if ( showOutlines || showAll ) {
            otherSections = controller.getSelectedOtherSections();
        } else {
            otherSections = null;
        }
        
        // Add facets to the scene: (this should be done first so that
        // z-buffering can't draw a facet edge over a node)
        boolean showFaces = controller.getShowFaces();
        boolean showNormals = controller.getShowNormals();
        if (pt1==null) {
            GroupVector facetGroups = controller.getSelectedFacetGroups();
            if (facetGroups!=null) {
                for (int i=0 ; i<facetGroups.size() ; i++ ) { // loop over each group of facets to display
                    Group group = facetGroups.get(i);
                    FacetVector facets = group.getFacets();
                    for (int j=0 ; j<facets.size() ; j++ ) { // loop over each facet in the group
                        Facet facet = facets.get(j);
                        // Make sure that all the nodes in the facet are calibrated:
                        NodeVector nodes = facet.getNodes();
                        int nn = nodes.size();
                        if (nn<3) { continue; } // only 3D facets are drawn
                        boolean ok = true;
                        for (int k=0; k<nn ; k++) { // loop over each node in the facet
                            Node node = nodes.get(k);
                            MyPoint3D p = node.getPoint3D();
                            if (p==null) { ok=false; break; } // if the node's section is not calibrated
                            if (!showAll) {
                                // Make sure the facet belongs to no other sections than those selected:
                                Section s = node.getSection();
                                if (s==null) { ok=false; break; } // shouldn't be possible
                                if ( !s.equals(currentSection) && otherSections!=null && !otherSections.contains(s) ) { ok=false; break; }
                            }
                        }
                        if (!ok) { continue; }
                        // Place the required transformed points into a new object:
                        n = facet.size();
                        MyPoint3D[] pts = new MyPoint3D[n];
                        for (int k=0 ; k<n ; k++ ) { // loop over each node in the facet
                            int id = facet.getNode(k).getID(); // index into projectedPoints
                            pts[k] = projectedPoints[id];
                            if (pts[k]==null) { // node is behind camera so don't paint the facet
                                ok = false;
                                break;
                            }
                        }
                        if (!ok) { continue; }
                        // Calculate the shading:
                        MyPoint3D v = facet.getNormal();
                        if (v==null) { continue; }
                        v = v.deepCopy(); // normalized
                        if (v==null) { continue; }
                        v.rotate(projector.getRotationMatrix()); // rotated into projected coordinates
                        double d = Math.abs(v.getZ()); // dotted with the z axis (direction not important)
                        d = 1.0 - (1.0-d)*0.8; // too dark otherwise
                        Color col = facet.getColor();
                        float[] hsb = new float[3];
                        Color.RGBtoHSB(col.getRed(),col.getGreen(),col.getBlue(),hsb);
                        hsb[2] *= (float)d; // d is on [0,1]
                        col = Color.getHSBColor(hsb[0],hsb[1],hsb[2]);
                        // Process the facet through the zbuffer:
                        Color faceCol = null;
                        Color edgeCol;
                        if (showFaces) {
                            // Paint facet face as a coloured patch and paint edges as user requests:
                            faceCol = col;
                            edgeCol = controller.getEdgeColor();
                        } else {
                            // Paint edges of facet only, using facet colour:
                            //faceColor = null;
                            edgeCol = col;
                        }
                        zbuf.putFacet(pts,faceCol,edgeCol);
                        // Draw the facet normals:
                        if (showNormals) {
                            // Set up the points on either end of the normal vector line:
                            MyPoint3D p0 = facet.getCentroid().deepCopy(); // facet centroid (point at start of normal vector line)
                            if (p0==null) { continue; }
                            MyPoint3D p1 = facet.getNormal().deepCopy(); // normalized normal vector (length of one)
                            if (p1==null) { continue; }
                            p1.times(normalLength); // normal vector of spatial length normalLength
                            p1.plus(p0); // point at end of normal vector line
                            // Transform the points to image coordinates:
                            p0 = spaceToImage(p0);
                            p1 = spaceToImage(p1);
                            if (p0==null) { continue; }
                            if (p1==null) { continue; }
                            // Rescale the line to an image pixel length of axisLength:
                            //MyPoint3D vn = MyPoint3D.minus(p1,p0); // first minus second
                            //vn.normalize();
                            //vn.times(axisLength); // scaled normal vector
                            //p1 = p0.deepCopy();
                            //p1.plus(vn);
                            // Draw normal vectors as line objects:
                            if (pt1==null) {
                                zbuf.putNode(p0,edgeCol,centroidWidth);
                                zbuf.putEdge(p0,p1,edgeCol);
                            } else {
                                g2.setPaint(edgeCol);
                                g2.fillOval( (int)p0.getX() - centroidWidth/2 , (int)p0.getY() - centroidWidth/2 , centroidWidth , centroidWidth );
                                g2.drawLine( (int)p0.getX() , (int)p0.getY() , (int)p1.getX() , (int)p1.getY() );
                            }
                        }
                    } // for j
                } // for i
            } // if (facetGroups!=null) 
        } // if (pt1==null) // not rotating
        
        // Get the drawing option for colouring the nodes:
        boolean colorBySection = controller.getNodeColorBySection();
        
        // Add nodes to the scene:
        Composite defaultComposite = g2.getComposite();
        if (pt1!=null) {
            float alpha = (float)0.5;
            int type = AlphaComposite.SRC_OVER; 
            AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
            g2.setComposite(composite);
        }
        GroupVector nodeGroups = controller.getSelectedNodeGroups();
        if (nodeGroups!=null) {
        for (int i=0 ; i<nodeGroups.size() ; i++ ) { // loop over each group of nodes to display
            Group group = nodeGroups.get(i);
            NodeVector nodes = group.getNodes();
            for (int j=0 ; j<nodes.size() ; j++ ) { // loop over each node in the group
                Node node = nodes.get(j);
                Section s = node.getSection();
                if (s==null) { continue; } // shouldn't be possible
                if (!s.isCalibrated()) { continue; } // not possible but I check for it anyway
                if ( !showAll && !s.equals(currentSection) && otherSections!=null && !otherSections.contains(s) ) { continue; }
                int id = node.getID(); // index into projectedPoints
                Color col;
                if (colorBySection) {
                    col = node.getSection().getColor();
                } else {
                    col = node.getColor();
                }
                if (pt1==null) {
                    if ( projectedPoints[id] != null ) { // only paint if node is in front of the camera
                        zbuf.putNode(projectedPoints[id],col,controller.getPointWidth());
                    }
                } else {
                    MyPoint3D p = spaceToImage(node.getPoint3D());
                    if (p==null) { continue; } // to next iteration of for j
                    g2.setPaint(col);
                    g2.fillOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
                }
            } // for j
        } // for i
        }
        
        // Add regions to the scene:
        if (pt1==null) {
        if (controller.getShowRegions()) {
            for (int i=0 ; i<model.numberOfRegions() ; i++ ) {
                Region region = model.getRegion(i);
                Section s = region.getSection();
                if (s==null) { continue; } // shouldn't be possible
                if (!s.isCalibrated()) { continue; } // not possible but I check for it anyway
                if ( !showAll && !s.equals(currentSection) && otherSections!=null && !otherSections.contains(s) ) { continue; }
                // Project the region point:
                MyPoint3D p = spaceToImage(region.getPoint3D());
                if (p==null) { continue; } // region is behind camera
                // Process the region through the zbuffer:
                zbuf.putNode(p,region.getColor(),controller.getPointWidth());
            }
        }
        } // if (pt1==null) // not rotating
        
        // Draw the outlines of the selected sections:
        if (pt1==null) {
        SectionVector sections = controller.getSelectedOtherSections();
        //Section section = controller.getCurrentSection();
        //if (!sections.contains(section)) {
        //    sections.add(section);
        //}
//        if ( showOther && sections!=null ) {
        if ( showOutlines && sections!=null ) {
        for (int i=0 ; i<sections.size() ; i++ ) { // loop over each section to display
            Section section  = sections.get(i);
            // Construct the section outline in section image pixel coordinates:
            n = 4; // 4 corners
            MyPoint2D[] pts2D = new MyPoint2D[n];
            w = section.getWidth();
            h = section.getHeight();
            if ( w<=0 || h<=0 ) { continue; }
            w--;
            h--;
            pts2D[0] = new MyPoint2D(0,0);
            pts2D[1] = new MyPoint2D(w,0);
            pts2D[2] = new MyPoint2D(w,h);
            pts2D[3] = new MyPoint2D(0,h);
            // Transform the outline from 2D image pixels to space coordinates:
            MyPoint3D[] pts3D = new MyPoint3D[n];
            boolean ok = true;
            for (int j=0 ; j<n ; j++ ) {
                MyPoint3D p = section.imageCornerToSpace(pts2D[j]);
                if (p==null) { ok=false; break; } // null if section not calibrated
                pts3D[j] = p;
            }
            if (!ok) { continue; }
            // Project the outline (from space to plotting pixel coordinates):
            for (int j=0 ; j<n ; j++ ) {
                MyPoint3D p = spaceToImage(pts3D[j]);
                if (p==null) { ok=false; break; } // point is behind camera
                pts3D[j] = p;
            } // for j
            if (!ok) { continue; }
            // Draw as black line objects:
            for (int j=0 ; j<n ; j++ ) {
                int j2 = j + 1;
                if (j2==n) { j2 = 0; }
                zbuf.putEdge(pts3D[j],pts3D[j2],Color.BLACK);
            }
        } // for i
        }
        } // if (pt1==null) // not rotating
        
        // Draw the VOI:
        if (pt1!=null) {
            g2.setPaint(Color.BLACK);
            g2.setComposite(defaultComposite);
        }
        if (controller.getShowVOI()) {
            if (controller.hasVOI()) {
                MyPoint3D[][] edges = controller.getVOIEdges();
                for (int i=0 ; i<VOI.N_EDGES ; i++) {
                    MyPoint3D p1 = spaceToImage(edges[i][0]);
                    MyPoint3D p2 = spaceToImage(edges[i][1]);
                    if ( p1==null || p2==null ) { continue; } // point(s) behind camera
                    if (pt1==null) {
                        zbuf.putEdge(p1,p2,Color.BLACK);
                    } else {
                        g2.drawLine( (int)p1.getX() , (int)p1.getY() , (int)p2.getX() , (int)p2.getY() );
                    }
                } // for i
            }
        }
        
        // Draw the axes:
        if (showAxes) {
            // Set up axis points:
            MyPoint3D po = spaceOrigin.deepCopy(); // origin
            MyPoint3D px = MyPoint3D.plus( po , new MyPoint3D(AXIS_LENGTH,0,0) ); // x axis
            MyPoint3D py = MyPoint3D.plus( po , new MyPoint3D(0,AXIS_LENGTH,0) ); // y axis
            MyPoint3D pz = MyPoint3D.plus( po , new MyPoint3D(0,0,AXIS_LENGTH) ); // z axis
            // Project with no scaling:
            po = spaceToImage(po,1.0);
            px = spaceToImage(px,1.0);
            py = spaceToImage(py,1.0);
            pz = spaceToImage(pz,1.0);
            // (There are no checks for null because they shouldn't be able to be behind the camera.)
            // Move to bottom left corner if desired:
            if (!centreAxes) {
                po.minus(imageOrigin);
                px.minus(imageOrigin);
                py.minus(imageOrigin);
                pz.minus(imageOrigin);
                MyPoint3D p = new MyPoint3D(AXIS_LENGTH+5,getHeight()-AXIS_LENGTH-5,0.0);
                po.plus(p);
                px.plus(p);
                py.plus(p);
                pz.plus(p);
            }
            // Draw axes as coloured line objects:
            if (pt1==null) {
                zbuf.putEdge(po,px,Color.RED);
                zbuf.putEdge(po,py,Color.YELLOW);
                zbuf.putEdge(po,pz,Color.GREEN);
            } else {
                g2.setPaint(Color.RED);
                g2.drawLine( (int)po.getX() , (int)po.getY() , (int)px.getX() , (int)px.getY() );
                g2.setPaint(Color.YELLOW);
                g2.drawLine( (int)po.getX() , (int)po.getY() , (int)py.getX() , (int)py.getY() );
                g2.setPaint(Color.GREEN);
                g2.drawLine( (int)po.getX() , (int)po.getY() , (int)pz.getX() , (int)pz.getY() );
            }
        }
        
        // There is nothing more to draw if rotating:
        if (pt1!=null) { 
            drawn = true;
            return;
        }
        
        // Get the zbuffer image:
        BufferedImage image = zbuf.getImage();
        
        // Return if no image exists:
        if (image==null) { return; }
        
        // Tightly fit the image inside the panel (a very simple fitting since they are the same size!):
        tightFitImage(g,image);
        
        //////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////
        //////////////////////// OVERLAYS ////////////////////////
        //////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////

        // Return if no sections exist:
        if (!model.hasSections()) {
            drawn = true;
            return;
        }

        // Return if no current section exists:
        if (currentSection==null) { return; }
        
        // Figure out where the cursor is:
        boolean mouseInside2D = controller.getMouseInside2D();
        
        // Get mouse click mode:
        int mode = controller.getClickMode();
        
        // Redefine w for use below:
        w = 2*nodeWidth;
        
        // Set stroke for facet edge overlays:
        BasicStroke dashedStroke = new BasicStroke(edgeWidth+1,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,
                                                   (float)10.0,new float[]{2f,16f},(float)0.0);
        g2.setStroke(dashedStroke);
        g2.setPaint(controller.getDefineFacetEdgeColor());
        
        // Paint the edges of the current facet being defined:
        Facet currentFacet = controller.getCurrentFacet();
        if (currentFacet!=null) {
            // Create a path around the node coordinates:
            if (currentFacet.size()>1) {
                GeneralPath path = addFacetToPath(currentFacet);
                if (path!=null) {
                    // May want to close the path:
                    if (mode==ClickModeManager.MODE_DEFINE_TRI_FACETS || mode==ClickModeManager.MODE_ADD_NODES_IN_FACETS) {
                        path.closePath();
                    }
                    // Draw the path:
                    g2.draw(path);
                }
            }
        }

        // Paint the edges of the facet closest to the cursor position:
        if (mouseInside2D && currentFacet==null) {
            Facet facet = controller.getClosestFacet();
            if (facet!=null) {
                // Create a path around the node coordinates:
                if (facet.size()>1) {
                    GeneralPath path = addFacetToPath(facet);
                    if (path!=null) {
                        // Close the path:
                        path.closePath();
                        // Draw the path:
                        g2.draw(path);
                    }
                }
            }
        }
        
        // Set stroke for node overlays:
        g2.setStroke(new BasicStroke(edgeWidth));

        // Paint circles around the nodes of the current facet being defined:
        if (currentFacet!=null) {
            if (mode==ClickModeManager.MODE_DEFINE_TRI_FACETS) {
                g2.setPaint(Color.WHITE);
            } else {
                g2.setPaint(Color.BLACK);
            }
            for (int i=0 ; i<currentFacet.size() ; i++ ) {
                Node node = currentFacet.getNode(i);
                // Only paint the node if it is in the current or other sections:
                Section section = node.getSection();
                if ( section.equals(currentSection) || ( otherSections!=null && otherSections.contains(section) ) ) {
                   MyPoint3D p = spaceToImage(node.getPoint3D());
                   if (p==null) { continue; }
                   g2.drawOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
                }
            } // for i
        }

        // Paint circles around the nodes of the facet closest to the cursor position:
        if (mouseInside2D && currentFacet==null) {
            Facet facet = controller.getClosestFacet();
            if (facet!=null) {
                // Paint circles around the node coordinates:
                g2.setPaint(Color.WHITE);
                for (int i=0 ; i<facet.size() ; i++ ) {
                    Node node = facet.getNode(i);
                    MyPoint3D p = spaceToImage(node.getPoint3D());
                    if (p==null) { continue; }
                    g2.drawOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
                } // for i
            }
        }

        // Paint a white circle around the current node:
        Node node = controller.getCurrentNode();
        if (node!=null) {
            MyPoint3D p = spaceToImage(node.getPoint3D());
            if (p!=null) {
                g2.setPaint(Color.WHITE);
                g2.drawOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
            }
        }

        // Paint a white circle around the node closest to the cursor position:
        node = controller.getClosestNode();
        if (mouseInside2D && node!=null) {
            MyPoint3D p = spaceToImage(node.getPoint3D());
            if (p!=null) {
                g2.setPaint(Color.WHITE);
                g2.drawOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
            }
        }
        
        // Redefine w for use below:
        w = nodeWidth;

        // Paint a cursor location indicator at the position of the candidate node:
        node = controller.getCandidateNode();
        if (node!=null) {
            MyPoint3D p = spaceToImage(node.getPoint3D());
            if (p!=null) {
                g2.setPaint(Color.WHITE);
                int ix = (int)p.getX();
                int iy = (int)p.getY();
                g2.drawOval( ix - w/2 , iy - w/2 , w , w );
                g2.drawLine( ix - w/2 , iy , ix + w/2 , iy );
                g2.drawLine( ix , iy - w/2 , ix , iy + w/2 );
            }
        }
        
        // Set drawn flag to true:
        drawn = true;
        
    }
    
    /** Helper subroutine for the paintComponent method.
     * Adds the facet node coordinates to a general path object for plotting.
     * The first point is added using path.moveTo() and the subsequent points are added using path.lineTo().
     * The path is not closed automatically.
     */
    private GeneralPath addFacetToPath(Facet facet) {
        GeneralPath path = new GeneralPath();
        // Loop over the nodes in the facet:
        for (int i=0 ; i<facet.size() ; i++ ) {
            Node node = facet.getNode(i);
            // Transform the point to image coordinates:
            MyPoint3D p = spaceToImage(node.getPoint3D());
            if (p==null) { return null; }
            // Add the point to the path:
            float x = (float)p.getX();
            float y = (float)p.getY();
            if (i==0) {
                path.moveTo(x,y);
            } else {
                path.lineTo(x,y);
            }
        }
        // Return the path object:
        return path;
    }
    
    // -------------------- Private Methods --------------------
    
    // Convert 3D space coordinate to projected image coordinate:
    private MyPoint3D spaceToImage(MyPoint3D p) {
        return projector.spaceToImage(p);
    }
    private MyPoint3D spaceToImage(MyPoint3D p, double scaling) {
        return projector.spaceToImage(p,scaling);
    }
     
    /** Applies the simple mouse-drag rotation to the rotation matrix.
     * @param pt2
     */
    private void rotateSimple(MyPoint3D pt2) {
         
        // Make sure that everything required has been defined:
        MyPoint3D pt1 = getPt1();
        if (pt1==null) {
            return;
        } // should never happen
        Matrix3D rotationMatrix = projector.getRotationMatrix();
        Matrix3D rotationMatrixInv = projector.getRotationMatrixInv();
        if (rotationMatrix==null) {
            return;
        } // should never happen
        if (rotationMatrixInv==null) {
            return;
        } // should never happen
        
        // Calculate movement of mouse between the two points:
        double dx = dragSensitivity*( pt2.getX() - pt1.getX() );
        double dy = dragSensitivity*( pt2.getY() - pt1.getY() );
         
        // Check for no movement:
        if ( dx==0.0 && dy==0.0 ) { return; }
        
        // Calculate rotation matrices for x and y axes:
        Matrix3D mx = Matrix3D.rotX(dy);
        Matrix3D my = Matrix3D.rotY(dx);
        
        // Multiply them together:
        Matrix3D m = Matrix3D.times(mx,my);
        
        // Apply the rotation to the rotation matrix:
        m = Matrix3D.times(m,rotationMatrix);
        
        // Invert:
        Matrix3D minv = rotationMatrix.inv();
        if (minv==null) {
            return;
        }// not invertible
        
        // Set rotation matrix and inverse:
        projector.setRotationMatrix(m);
         
     }
     
     /** Applies the fancy mouse-drag rotation to the rotation matrix.
      * @param pt2
      */
     private void rotateFancy(MyPoint3D pt2) {
        
        // Make sure that everything required has been defined:
        MyPoint3D pt1 = getPt1();
        if (pt1==null) {
            return;
        } // should never happen
        Matrix3D rotationMatrix = projector.getRotationMatrix();
        Matrix3D rotationMatrixInv = projector.getRotationMatrixInv();
        if (rotationMatrix==null) {
            return;
        } // should never happen
        if (rotationMatrixInv==null) {
            return;
        } // should never happen
        
        // Calculate a normal vector to the plane of rotation:
        MyPoint3D n = MyPoint3D.cross(pt1,pt2);
        
        // Check for zero vector:
        if (n.norm()==0.0) {
            return;
        } // cross product is zero if the two clicked points are identical (should never happen)
        
        // Calculate angle between the two clicked points:
        double th = MyPoint3D.angleBetweenVectors(pt1,pt2);
        
        // Check for zero angle:
        if (th==0.0) {
            return;
        } // should never happen
        
        // Normalize the normal vector:
        n.normalize();
        
        // Rotate the normal vector into spatial coordinates:
        n.rotate(rotationMatrixInv);
        
        // Pull out the coordinates of the normal vector:
        double u = n.getX();
        double v = n.getY();
        double w = n.getZ();
        
        // http://inside.mines.edu/fs_home/gmurray/ArbitraryAxisRotation/#x1-10011
        // 5.2 The normalized matrix for rotations about the origin
        // (this is a body rotation so I want the transpose of that from the reference above)
        Matrix3D m = new Matrix3D(); // all zeros
        th = -th;
        double cost = Math.cos(th);
        double sint = Math.sin(th);
        double t = 1.0 - cost;
        double u2 = u*u;
        double v2 = v*v;
        double w2 = w*w;
        double uv = u*v*t;
        double vw = v*w*t;
        double wu = w*u*t;
        double us = u*sint;
        double vs = v*sint;
        double ws = w*sint;
        // Diagonal terms:
        m.set(0,0, u2 + (1.0-u2)*cost );
        m.set(1,1, v2 + (1.0-v2)*cost );
        m.set(2,2, w2 + (1.0-w2)*cost );
        // Other terms:
        m.set(1,0, uv - ws );
        m.set(0,1, wu + ws );
        m.set(2,0, wu + vs );
        m.set(0,2, wu - vs );
        m.set(2,1, vw - us );
        m.set(1,2, vw + us );
        
        // Apply the rotation to the rotation matrix:
        m = Matrix3D.times(rotationMatrix,m);
        
        // Invert:
        Matrix3D minv = rotationMatrix.inv();
        if (minv==null) {
            return;
        }// not invertible
        
        // Set rotation matrix and inverse:
        projector.setRotationMatrix(m);
         
     }
    
    // -------------------- Monitors --------------------

    /** Listens for mouse clicks. */
    private class MouseClickMonitor extends MouseAdapter {
        @Override
        public void mousePressed (MouseEvent e) {
            // Return if scene not drawn:
            if (!drawn) { return; }
            // Set 3D point:
            setPt1(monitorHelper(e));
        }
        @Override
        public void mouseReleased (MouseEvent e) {
            // Clear the mouse pressed point:
            setPt1(null);
            // Redraw:
            repaint();
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            repaint();
        }
    }

    /** Listens for mouse movement. */
    private class MouseMoveMonitor extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            // Return if scene not drawn:
            if (!drawn) { return; }
            // Return if mouse not pressed yet:
            if (getPt1()==null) { return; }
            // Get 3D point:
            MyPoint3D pt2 = monitorHelper(e);
            // Check we are far enough away from the first point:
            //double d = pt1.distanceToPoint(pt2);
            //if (d<10.0) { return; } // 10 pixel distance hardwired
            // Apply the specified pan or rotation:
            switch (dragMode) {
                case DRAG_MODE_PAN:
                    pan(pt2);
                    break;
                case DRAG_MODE_SIMPLE:
                    rotateSimple(pt2);
                    break;
                case DRAG_MODE_FANCY:
                    rotateFancy(pt2);
                    break;
                default: // (shouldn't happen)
                    // do nothing
            }
            // Reset the first clicked point to that moved to:
            setPt1(pt2);
            // Draw:
            repaint();
        }
    }
    
    private MyPoint3D monitorHelper(MouseEvent e) {
        // Get the current cursor location and its coordinates:
        MyPoint2D p = new MyPoint2D(e.getPoint());
        double x = p.getX();
        double y = p.getY();
        // Check for fancy dragging:
        double z;
        if ( dragMode == DRAG_MODE_FANCY ) {
            // Interpolate the z position on the drag-sphere:
            MyPoint3D imageOrigin = projector.getImageOrigin();
            x -= imageOrigin.getX();
            y -= imageOrigin.getY();
            y = -y;
            p = new MyPoint2D(x,y);
            if (!dragSphere.inside(p)) { // can happen if the mouse moves outside of the panel
                // Project back onto the circle:
                p = dragSphere.project(p);
                x = p.getX();
                y = p.getY();
                z = 0.0; // on edge of circle (side of sphere)
            } else {
                z = dragSphere.interpolate(p);
            }
        } else {
            z = 0.0; // dummy value is ignored
        }
        // Create 3D point:
        return new MyPoint3D(x,y,z);
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!zoomer.writeSessionInformation(writer)) { return false; }
        if (!projector.writeSessionInformation(writer)) { return false; }
        // Write the viewing options on a single line:
        String textLine = dragMode + " " + showAxes + " " + centreAxes;
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = zoomer.readSessionInformation(reader,merge); if (msg!=null) { return msg.trim() + " (3D panel)."; }
        msg = projector.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        // Read the drag mode, show axis and centre axis options from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "3D view options line."; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<3) { return "Not enough values on 3D view options line."; }
        try {
            dragMode = Integer.parseInt(s[0]);
            showAxes = Boolean.parseBoolean(s[1]);
            centreAxes = Boolean.parseBoolean(s[2]);
        } catch (NumberFormatException e) { return "Parsing 3D view options."; }
        // Return successfully:
        return null;
    }
    
}
