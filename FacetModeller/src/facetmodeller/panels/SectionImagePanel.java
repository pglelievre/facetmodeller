package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.groups.GroupVector;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.gui.ZoomerDefault;
import facetmodeller.plc.Facet;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.Region;
import facetmodeller.plc.RegionVector;
import facetmodeller.sections.Section;
import facetmodeller.sections.SectionVector;
import facetmodeller.sections.SnapshotSection;
import fileio.FileUtils;
import fileio.SessionIO;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPoint3D;
import gui.ImagePanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import paint.PaintingUtils;

/** A zoomable panel for drawing a section image and measurement overlays.
 * Modelled on Class "RasterPanel" from the book "Java programming for spatial sciences" by Jo Wood.
 * @author Peter Lelievre
 */
public final class SectionImagePanel extends ImagePanel implements SessionIO { // { implements Zoomer { // extends PanningPannel
    // TODO: implement Zoomer once Java allows implementing multiple interfaces
    private static final long serialVersionUID = 1L;

    // ------------------- Properties ------------------

    private final FacetModeller controller;
    private final ZoomerDefault zoomer;

    // Lists of things that have been painted:
    private final NodeVector paintedNodes = new NodeVector(); // the nodes painted
    private final MyPoint2DVector paintedNodePoints = new MyPoint2DVector(); // the node points painted (may be shifted)
    private final FacetVector paintedFacets = new FacetVector(); // the facets painted
    private final MyPoint2DVector paintedFacetCentroids = new MyPoint2DVector(); // the facet centroids painted
    private final RegionVector paintedRegions = new RegionVector(); // the region specifications painted
    private final MyPoint2DVector paintedRegionPoints = new MyPoint2DVector(); // the region points painted (may be shifted)

    // Origin when zoomed:
    private MyPoint2D origin = null;
    
    // For keeping track of the cursor:
    private boolean mouseInside = false;
    
    // ------------------ Constructor ------------------

    /** Creates the panel.
     * @param con The FacetModeller controller object.
     */
    public SectionImagePanel(FacetModeller con) {
        super();
        zoomer = new ZoomerDefault(-1,-2,2000,1.2d); // init, min, max, factor
        controller = con;
        // Set mouse and keyboard listeners:
        addMouseListener(new MouseClickMonitor()); // listens for mouse clicks
        addMouseMotionListener(new MouseMoveMonitor()); // listens for mouse motion
        addComponentListener(new PanelSizeMonitor()); // listens for panel resizing
    }

    // -------------------- Getters --------------------

    public NodeVector getPaintedNodes() { return paintedNodes; }
    public MyPoint2DVector getPaintedNodePoints() { return paintedNodePoints; }
    public FacetVector getPaintedFacets() { return paintedFacets; }
    public MyPoint2DVector getPaintedFacetCentroids() { return paintedFacetCentroids; }
    public RegionVector getPaintedRegions() { return paintedRegions; }
    public MyPoint2DVector getPaintedRegionPoints() { return paintedRegionPoints; }
    public boolean getMouseInside() { return mouseInside; }
    
    // -------------------- Setters --------------------
    
    public void setOrigin(MyPoint2D p) {
        origin = p;
        repaint();
    }
    
    // -------------------- Implemented Zoomer Methods --------------------
    
//    @Override
    public void zoomIn() {
        zoomer.zoomIn();
        repaint();
    }
//    @Override
    public void zoomOut() {
        zoomer.zoomOut();
        repaint();
    }
//    @Override
    public void zoomReset() {
        zoomer.zoomReset();
        origin = null;
//        setPanX(0);
//        setPanY(0);
        repaint();
    }

    // -------------------- Overridden Methods --------------------

    @Override
    protected void resetScroller() {
        controller.resetScroller();
    }
    
    /** Paints graphics on the panel.
     * @param g Graphics context in which to draw.
     */
    @Override
    public void paintComponent(Graphics g) {

        // Paint background:
        super.paintComponent(g);

        // Clear the lists of painted nodes, node points, etc.:
        paintedNodes.clear();
        paintedNodePoints.clear();
        paintedFacets.clear();
        paintedFacetCentroids.clear();
        paintedRegions.clear();
        paintedRegionPoints.clear();

        // Return if no sections exist:
        if (!controller.hasSections()) { return; }
        
        // Return if no current section exists:
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return; }
        
        // Set some local variables:
        int shiftX = controller.getShiftingX();
        int shiftY = controller.getShiftingY();
        int mode = controller.getClickMode();
        boolean showImage = controller.getShowImage();

        // Get the other sections being displayed (if any exist):
        SectionVector otherSections;
//        if (controller.getShowOther()) {
            otherSections = controller.getSelectedOtherSections();
//        } else {
//            otherSections = null;
//        }

        // Determine the facets to paint:
        FacetVector facetsToPaint = new FacetVector();
        GroupVector groups = controller.getSelectedFacetGroups();
        if (groups!=null) {
        for (int i=0 ; i<groups.size(); i++ ) { // loop over every selected facet group
            FacetVector facets = groups.get(i).getFacets();
            for (int j=0 ; j<facets.size() ; j++ ) { // loop over every facet in the ith group
                Facet facet = facets.get(j);
                // Make sure there is more than one node in the facet:
                int nn = facet.size(); // number of nodes in the facet
                if (nn<=1) { continue; }
                // Make sure the facet belongs to no other sections than those selected:
                boolean ok = true;
                for (int k=0 ; k<nn ; k++ ) { // loop over each node in the facet
                    // Check if the kth node in the facet should be plotted: it must be either
                    // 1) in the current section, which must be calibrated if its the topo section; OR
                    // 2) in one of the other selected sections, which must be calibrated:
                    Node node = facet.getNode(k); // kth node in the facet
                    Section s = node.getSection(); // section for the kth node
                    if ( s.equals(currentSection) ) { // node is in current section
                        if (node.getPoint2D()==null) { // node not paintable
                            ok = false;
                            break;
                        }
                    } else { // node not in current section
                        if ( otherSections==null || !otherSections.contains(s) || !s.isCalibrated() ) {
                            // (calibration required for projection)
                            ok = false;
                            break;
                        }
                        if (node.getPoint3D()==null) { // node not paintable
                            ok = false;
                            break;
                        }
                    }
                }
                if (!ok) { continue; }
                // Add the facet to the list of facets to paint:
                facetsToPaint.add(facet);
            }
        }}

        // Determine the nodes to paint in the current section:
        NodeVector nodesToPaintCurrent = new NodeVector();
        NodeVector nodes = currentSection.getNodes();
        for (int i=0 ; i<nodes.size() ; i++ ) {
            Node node = nodes.get(i);
            // Make sure the node is in one of the selected groups to paint:
            if (!controller.isSelectedNodeGroup(node.getGroup())) { continue; } // cycle to next node
            // Make sure the node is paintable:
            if (node.getPoint2D()==null) { continue; } // cycle to next node
            // Add the node to the list of nodes to paint:
            nodesToPaintCurrent.add(node);
        }
        
        // Determine the nodes to paint in the other sections:
        NodeVector nodesToPaintOther = new NodeVector();
        if (otherSections!=null) {
            if (!otherSections.isEmpty()) {
                for (int j=0 ; j<otherSections.size() ; j++ ) {
                    // Skip the section if it is the current section:
                    Section s = otherSections.get(j);
                    if (s.equals(currentSection)) { continue; }
                    // Skip the section if it is not calibrated (calibration is required for projection):
                    if (!s.isCalibrated()) { continue; }
                    // Loop over the nodes:
                    nodes = s.getNodes();
                    for (int i=0 ; i<nodes.size() ; i++ ) {
                        Node node = nodes.get(i);
                        // Make sure the node is in one of the selected groups to paint:
                        if (!controller.isSelectedNodeGroup(node.getGroup())) { continue; } // cycle to next node
                        // Make sure the node is paintable:
                        if (node.getPoint3D()==null) { continue; } // cycle to next node
                        // Add the node to the list of nodes to paint:
                        nodesToPaintOther.add(node);
                    }
                }
            }
        }

        // Calculate the range, in image panel units, across which we'll need to plot:
        MyPoint2D p1 = MyPoint2D.zero(); // will hold minimum coordinate values
        MyPoint2D p2 = new MyPoint2D(currentSection.getWidth(),currentSection.getHeight()); // will hold maximum coordinate values
        RegionVector regions = currentSection.getRegions();
        if (!(currentSection instanceof SnapshotSection)) {
            for (int i=0 ; i<facetsToPaint.size(); i++ ) { // loop over facets
                Facet facet = facetsToPaint.get(i);
                for (int j=0 ; j<facet.size() ; j++ ) {
                    Node node = facet.getNode(j);
                    MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
                    if (p==null) { // shouldn't happen, but I'll check for it anyway
                        facetsToPaint.remove(facet);
                        continue;
                    }
                    p1.min(p);
                    p2.max(p);
                }
            }
            for (int i=0 ; i<nodesToPaintCurrent.size() ; i++ ) { // loop over nodes for current section
                Node node = nodesToPaintCurrent.get(i);
                MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
                if (p==null) { // shouldn't happen, but I'll check for it anyway
                    nodesToPaintCurrent.remove(node);
                    continue;
                }
                p1.min(p);
                p2.max(p);
            }
            for (int i=0 ; i<nodesToPaintOther.size() ; i++ ) { // loop over nodes for other section
                Node node = nodesToPaintOther.get(i);
                MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
                if (p==null) {  // shouldn't happen, but I'll check for it anyway
                    nodesToPaintOther.remove(node);
                    continue;
                }
                p1.min(p);
                p2.max(p);
            }
            if (controller.getShowRegions()) {
                for (int i=0 ; i<regions.size() ; i++ ) { // loop over regions
                    Region region = regions.get(i);
                    MyPoint2D p = region.getPoint2D();
                    p1.min(p);
                    p2.max(p);
                }
            }
        }
        MyPoint2D dp = MyPoint2D.minus(p2,p1); // coordinate ranges
        if (dp.min()<=0.0) { return; }
        
        // Tightly fit the plot inside the panel but maintain aspect ratio:
        calculateTightFitTransform(p1,dp);
        boolean hasImage = currentSection.hasImage();
        if (hasImage && showImage) {
            // Get the current section image:
            BufferedImage image = currentSection.getImage();
            // Check the image exists:
            if (image==null) { return; }
            // Calculate the tight fit transform:
            tightFitImage(g,image);
        }
        
        // Get drawing styles for overlays:
        final int nodeWidth = controller.getPointWidth();
        final int edgeWidth = controller.getLineWidth();
        final int centroidWidth = (int)Math.ceil(nodeWidth/2.0); // centroids drawn half the node width
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f); // transparency factor
//        final boolean showPickingRadius = controller.getShowPickingRadius();
//        final int pickingRadius = (int)( controller.getPickingRadius() * scaling ); // panel pixel width
        // The space-to-image transform may not be equidistance (may have different scalings for horizontal and vertical panel directions)
        // so, without knowing how best to show a picking radius in spatial units (it used to be in image pixel units but that caused some issues)
        // I have just gotten rid of the plotting of the picking radius.
        
        // Use Java2D graphics for overlays:
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(composite);
        if (!hasImage || !showImage) {
            double imageWidth  = currentSection.getWidth();
            double imageHeight = currentSection.getHeight();
            double scaledWidth  = scaling*imageWidth;
            double scaledHeight = scaling*imageHeight;
            Color col = currentSection.getColor();
            if (col==null) { col = getBackground(); }
            if (!hasImage && showImage) {
                g2.setColor(col);
                g2.fillRect((int)translation.getX(),(int)translation.getY(),(int)scaledWidth,(int)scaledHeight);
            }
            g2.setColor(Color.black);
            g2.drawRect((int)translation.getX(),(int)translation.getY(),(int)scaledWidth,(int)scaledHeight);
        }
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(edgeWidth)); // line style
        
        // Get number of dimensions:
        int ndim = controller.numberOfDimensions();
                    
        // Paint the patches, edges and centroids of the facets:
        for (int i=0 ; i<facetsToPaint.size(); i++ ) { // loop over every facet that needs to be painted
            Facet facet = facetsToPaint.get(i);
            // Create a path around possibly shifted node coordinates:
            HasPathAndCentroid tmp = addFacetToPath(facet,currentSection,shiftX,shiftY);
            if (tmp==null) { continue; } // shouldn't happen, but I'll check for it anyway
            // Close the path:
            tmp.path.closePath();
            // Transform the path from section to panel coordinates:
            tmp.path.transform(imageToPanel);
            // Greate a filled, semi-transparent polygonal patch:
            Color color = facet.getColor();
            if (facet.size()>2) { // (no patch exists for edge-element facets)
                g2.setPaint(color);
                g2.fill(tmp.path);
            }
            // Draw the path (i.e. the facet edges):
            if (ndim==3) {
                color = controller.getEdgeColor();
            }// else {
            //    color = facet.getColor();
            //}
            g2.setPaint(color);
            g2.draw(tmp.path);
            // Draw a small point at the facet centroid:
            if (ndim==2) {
                color = controller.getEdgeColor();
            }
            g2.setPaint(color);
            PaintingUtils.paintPoint(g2,imageToPanel,tmp.centroid,centroidWidth,true); // filled
            // Add to the list of painted facets and centroids:
            paintedFacets.add(facet);
            paintedFacetCentroids.add(tmp.centroid);
        }

        // Paint the edges of the current facet being defined:
        BasicStroke dashedStroke = new BasicStroke(edgeWidth+1,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,
                                                   (float)10.0,new float[]{2f,16f},(float)0.0);
        Facet currentFacet = controller.getCurrentFacet();
        if (currentFacet!=null) {
            // Create a path around the node coordinates:
            if (currentFacet.size()>1) {
                HasPathAndCentroid tmp = addFacetToPath(currentFacet,currentSection,shiftX,shiftY);
                if (tmp!=null) { // null shouldn't happen, but I'll check for it anyway
                    // May want to close the path:
                    if (mode==ClickModeManager.MODE_DEFINE_TRI_FACETS || mode==ClickModeManager.MODE_ADD_NODES_IN_FACETS) {
                        tmp.path.closePath();
                    }
                    // Transform the path from section to panel coordinates:
                    tmp.path.transform(imageToPanel);
                    // Draw the path:
                    g2.setStroke(dashedStroke); // line style changed
                    g2.setPaint(controller.getDefineFacetEdgeColor());
                    g2.draw(tmp.path);
                    g2.setStroke(new BasicStroke(edgeWidth)); // line style reset
                }
            }
        }

        // Paint the edges of the facet closest to the cursor position:
        if (mouseInside && currentFacet==null) {
            Facet facet = controller.getClosestFacet();
            if (facet!=null) {
                // Create a path around the node coordinates:
                if (facet.size()>1) {
                    HasPathAndCentroid tmp = addFacetToPath(facet,currentSection,shiftX,shiftY);
                    if (tmp!=null) { // null shouldn't happen, but I'll check for it anyway
                        // Close the path:
                        tmp.path.closePath();
                        // Transform the path from section to panel coordinates:
                        tmp.path.transform(imageToPanel);
                        // Draw the path:
                        g2.setStroke(dashedStroke); // line style changed
                        g2.setPaint(controller.getDefineFacetEdgeColor());
                        g2.draw(tmp.path);
                        g2.setStroke(new BasicStroke(edgeWidth)); // line style reset
                    }
                }
            }
        }
        
        // Get the drawing option for colouring the nodes:
        boolean colorBySection = controller.getNodeColorBySection();

        // Paint the nodes for the current section:
        for (int i=0 ; i<nodesToPaintCurrent.size() ; i++ ) {
            Node node = nodesToPaintCurrent.get(i);
            // Paint the node:
            MyPoint2D p = node.getPoint2D();
            if (p==null) { continue; } // shouldn't happen, but I'll check for it anyway
            Color col;
            if (colorBySection) {
                col = node.getSection().getColor();
            } else {
                col = node.getColor();
            }
            g2.setPaint(col);
            PaintingUtils.paintPoint(g2,imageToPanel,p,nodeWidth,true); // filled
            // Add to the list of painted nodes and node points:
            paintedNodes.add(node);
            paintedNodePoints.add(p);
        }

        // Paint the nodes for the other sections:
        for (int i=0 ; i<nodesToPaintOther.size() ; i++ ) {
            Node node = nodesToPaintOther.get(i);
            // Paint the possibly shifted node:
            MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
            if (p==null) { continue; } // shouldn't happen, but I'll check for it anyway
            Color col;
            if (colorBySection) {
                col = node.getSection().getColor();
            } else {
                col = node.getColor();
            }
            g2.setPaint(col);
            PaintingUtils.paintPoint(g2,imageToPanel,p,nodeWidth,false); // not filled
            // Add to the list of painted nodes and node points:
            paintedNodes.add(node);
            paintedNodePoints.add(p);
        }

        // Paint the region points for the current section:
        if (controller.getShowRegions()) {
            for (int i=0 ; i<regions.size() ; i++ ) {
                Region region = regions.get(i);
                MyPoint2D p = region.getPoint2D();
                if (p==null) { continue; } // shouldn't happen, but I'll check for it anyway
                g2.setPaint(region.getColor());
                PaintingUtils.paintPoint(g2,imageToPanel,p,nodeWidth,true);
                // Add to the list of painted regions and region points:
                paintedRegions.add(region);
                paintedRegionPoints.add(p);
            }
        }

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
                Section s = node.getSection();
                if ( s.equals(currentSection) || ( otherSections!=null && otherSections.contains(s) ) ) {
                   MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
                   if (p==null) { continue; }
                   PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false);
                }
            }
        }

        // Paint circles around the nodes of the facet closest to the cursor position:
        if (mouseInside && currentFacet==null) {
            Facet facet = controller.getClosestFacet();
            if (facet!=null) {
                // Paint circles around the node coordinates:
                g2.setPaint(Color.WHITE);
                for (int i=0 ; i<facet.size() ; i++ ) {
                    Node node = facet.getNode(i);
                    MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
                    if (p==null) { continue; }
                    PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false);
                }
                // If in MODE_INFO or MODE_REVERSE_FACETS then paint a thicker circle around the first and possibly second node:
                if ( mode==ClickModeManager.MODE_INFO || mode==ClickModeManager.MODE_REVERSE_FACETS ) {
                    Node node = facet.getNode(0);
                    MyPoint2D p = shiftNode(node,currentSection,shiftX,shiftY);
                    if (p!=null) {
                        g2.setStroke(new BasicStroke(ndim*edgeWidth));
                        PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false);
                        g2.setStroke(new BasicStroke(edgeWidth)); // line style reset
                    }
                    if (ndim==3) {
                        node = facet.getNode(1);
                        p = shiftNode(node,currentSection,shiftX,shiftY);
                        if (p!=null) {
                            g2.setStroke(new BasicStroke(2*edgeWidth));
                            PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false);
                            g2.setStroke(new BasicStroke(edgeWidth)); // line style reset
                        }
                    }
                }
    //            // Paint a white circle around the centroid:
    //            if (showPickingRadius) {
    //                HasPathAndCentroid tmp = addFacetToPath(facet,currentSection,shiftX,shiftY);
    //                if (tmp!=null) { // shouldn't happen, but I'll check for it anyway
    //                    tmp.path.closePath();
    //                    tmp.path.transform(imageToPanel);
    //                    PaintingUtils.paintPoint(g2,imageToPanel,tmp.centroid,2*centroidWidth,false); // not filled
    //                    g2.setPaint(Color.BLACK);
    //                    PaintingUtils.paintPoint(g2,imageToPanel,tmp.centroid,2*pickingRadius,false); // not filled
    //                }
    //            }
            }
        }

        // Paint the calibration points (if not topography):
        //boolean isTopo = currentSection.isTopo();
        if (hasImage && currentSection.isCalibrated()) {
            g2.setPaint(controller.getCalibrationColor());
            PaintingUtils.paintPoint(g2,imageToPanel,currentSection.getClicked1(),nodeWidth,false);
            PaintingUtils.paintPoint(g2,imageToPanel,currentSection.getClicked2(),nodeWidth,false);
        }
        
//        // If calibrating then draw large cross-hairs across the image:
//        if ( controller.getMode() == FacetModeller.MODE_CALIBRATE ) {
//            p = new MyPoint2D(getMousePosition());
//            g2.setColor(Color.white);
//            g2.setStroke(new BasicStroke(1)); // line style reset
//            double x = p.getX();
//            double y = p.getY();
//            double w = getWidth()/2.0;
//            g2.drawLine((int)(x-w),(int)y,(int)(x+w),(int)y);
//            g2.drawLine((int)x,(int)(y-w),(int)x,(int)(y+w));
//        }
        
        // Paint a large circle around the origin node of the same colour as the node if it is present in those painted:
        Node originNode = controller.getOriginNode3D();
        if (originNode!=null) {
            int i = paintedNodes.indexOf(originNode);
            if (i>=0) {
                MyPoint2D p = shiftNode(originNode,currentSection,shiftX,shiftY);
                if (p!=null) { // shouldn't happen, but I'll check for it anyway
                    Color col;
                    if (colorBySection) {
                        col = originNode.getSection().getColor();
                    } else {
                        col = originNode.getColor();
                    }
                    g2.setPaint(col);
                    //g2.setStroke(new BasicStroke(2*edgeWidth));
                    PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false);
                    //g2.setStroke(new BasicStroke(edgeWidth)); // line style reset
                }
            }
        }
        
        // Paint a white circle around the current node (e.g. being moved or first in an edge being flipped):
        Node currentNode = controller.getCurrentNode();
        if (currentNode!=null) {
            MyPoint2D p = shiftNode(currentNode,currentSection,shiftX,shiftY);
            if (p!=null) {
                g2.setPaint(Color.WHITE);
                PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false); // not filled
            }
        }

        // Paint a white circle around the node closest to the cursor position (e.g. or that being moved):
        MyPoint2D p = controller.getClosestNodePoint();
        if (mouseInside && p!=null) {
            g2.setPaint(Color.WHITE);
            PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false); // not filled
//            if (showPickingRadius) {
//                g2.setPaint(Color.BLACK);
//                PaintingUtils.paintPoint(g2,imageToPanel,p,2*pickingRadius,false); // not filled
//            }
        }

        // Paint a white circle around the region closest to the cursor position:
        p = controller.getClosestRegionPoint();
        if (mouseInside && p!=null) {
            g2.setPaint(Color.WHITE);
            PaintingUtils.paintPoint(g2,imageToPanel,p,2*nodeWidth,false); // not filled
//            if (showPickingRadius) {
//                g2.setPaint(Color.BLACK);
//                PaintingUtils.paintPoint(g2,imageToPanel,p,2*pickingRadius,false); // not filled
//            }
        }

    }

    // -------------------- Private Methods --------------------

    /** Helper subroutine for the paintComponent method.
     * Projects the node onto the current section (image pixel coordinates) and shifts as required for plotting.
     * @return A new MyPoint2D object holding the image pixel coordinates of the projected and possibly shifted node.
     */
    private MyPoint2D shiftNode(Node node, Section currentSection, int shiftX, int shiftY) {

        // Check if we need to project (if node lies on the current section then we don't need to):
        MyPoint2D p2;
        boolean nodeSectionEqualsCurrentSection = node.getSection().equals(currentSection);
        if ( !node.isOff() && nodeSectionEqualsCurrentSection ) {
            p2 = node.getPoint2D(); // image pixel coordinates
        } else {
            // Get the node point in spatial coordinates:
            MyPoint3D p3 = node.getPoint3D(); // spatial coordinates
            if (p3==null) { return null; }
            // Project onto the current section:
            p2 = currentSection.projectOnto(p3); // image pixel coordinates
        }
        if (p2==null) { return null; }
        
        // Don't shift if the node lies on the current section:
        if ( nodeSectionEqualsCurrentSection ) { return p2; }
        
        // Shift the coordinates as necessary:
        p2.plus(shiftX,shiftY); // shifting is in image pixel coordinates
        // There is no way to make the shifting work in panel pixel coordinates!
        // The tight-fit transform, to go between the image and panel coordinate systems,
        // requires the plotting range is known, which depends on the shifting.
        // To make the shifting work in panel pixel coordinates, the tight-fit scaling would need to be known first,
        // which would be possible, but then we couldn't determine the plotting range accurately.

        // Return the 2D point object:
        return p2;

    }

    /** Helper subroutine for the paintComponent method.
     * Adds the facet node coordinates to a general path object for plotting.
     * The first point is added using path.moveTo() and the subsequent points are added using path.lineTo().
     * The path is not closed automatically.
     */
    private HasPathAndCentroid addFacetToPath(Facet facet, Section currentSection, int shiftX, int shiftY) {

        HasPathAndCentroid out = new HasPathAndCentroid();
        MyPoint2DVector points = new MyPoint2DVector();

        // Loop over the nodes in the facet:
        for (int i=0 ; i<facet.size() ; i++ ) {

            // Get possibly shifted node coordinates:
            MyPoint2D p = shiftNode(facet.getNode(i),currentSection,shiftX,shiftY);
            if (p==null) { return null; }

            // Add the point to the path:
            float x = (float)p.getX();
            float y = (float)p.getY();
            if (i==0) {
                out.path.moveTo(x,y);
            } else {
                out.path.lineTo(x,y);
            }

            // Add to the temporary list of nodes:
            points.add(new MyPoint2D(x,y));

        }

        // Calculate the centroid:
        out.centroid = points.centroid();

        // Return the output object:
        return out;

    }
    private class HasPathAndCentroid {
        public GeneralPath path = new GeneralPath();
        public MyPoint2D centroid; // = new MyPoint2D();
    }

    /** Calculates the transformations required to maintain the section image aspect ratio
     * and fit tightly within this panel, keeping zoom and origin information in mind. */
    private void calculateTightFitTransform(MyPoint2D sceneOriginIn, MyPoint2D sceneDimensions) {

        // Get the current section:
        Section section = controller.getSelectedCurrentSection();

        // If no current section exists then return:
        if (section==null) { return; }
        
        // Check if scroller bars are being included in the 2D panel:
        MyPoint2D panelDimensions;
        if (controller.getShowScroller()) {
            // Get width and height of scrolling panel in pixels:
            panelDimensions = new MyPoint2D( getParent().getWidth(), getParent().getHeight() );
        } else {
            // Get width and height of image panel in pixels:
            panelDimensions = new MyPoint2D( getWidth(), getHeight() );
        }

        // Adjust scene origin if required by user-specified viewing origin:
        MyPoint2D sceneOrigin;
        if (origin==null) {
            sceneOrigin = sceneOriginIn; // avoids compiler warning
        } else {
            MyPoint2D halfSceneDimensions = sceneDimensions.deepCopy();
            halfSceneDimensions.times(0.5);
            sceneOrigin = MyPoint2D.minus(origin,halfSceneDimensions);
        }
        
        // Calculate the scaling:
        int zoom = zoomer.getZoom();
        MyPoint2D scale = MyPoint2D.divide(panelDimensions,sceneDimensions);
        if (zoom<=-2) { // "loose-fit" transform
            scaling = scale.min()*0.98;
        } else if (zoom==-1) { // tight-fit transform
            scaling = scale.min();
        } else { // tight-fit to either width or height, whichever zooms largest
            scaling = scale.max();
            scaling *= zoomer.getScaling(); // zoom scaling applied
        }
        sceneDimensions.times(scaling);
        sceneOrigin.times(scaling);
        sceneOrigin.plus(-controller.getPanning2DX(),-controller.getPanning2DY()); // this does the panning via pan buttons
        //sceneOrigin.plus(-getPanX(),-getPanY()); // this does the panning via dragging
        
        // Determine translation required to centre the origin on the panel:
        if (controller.getShowScroller()) {
            // Set preferred size so that the scroll bars show up:
            setPreferredSize( sceneDimensions.toDimension() );
            // Determine translation:
            if (zoom>0) {
                translation = sceneOrigin;
                translation.neg();
            } else { // zoom<=0
                translation = MyPoint2D.minus(panelDimensions,sceneDimensions);
                translation.times(0.5);
                translation.minus(sceneOrigin);
                if (zoom==0) {
                    if (scale.getX()>scale.getY()) { // centering is only required horizontally
                        translation.setY(-sceneOrigin.getY());
                    } else { // centering is only required vertically
                        translation.setX(-sceneOrigin.getX());
                    }
                }
            }
        } else {
            // I used to have everything in the above if block happen for all situations but then at some point
            // I commented out everything except the part below. However, the part below doesn't work when the
            // scroll bars are used. The code above does seem to work when the scroll bars are used. Maybe the
            // code below is all that is required when the scroll bars are not being used?
            translation = MyPoint2D.minus(panelDimensions,sceneDimensions);
            translation.times(0.5);
            translation.minus(sceneOrigin);
        }

        // Create forward and inverse affine transformations:
        createTightFitTransform();
        
        // Make sure the scroll bars show up if needed:
        // (this wasn't happening when first drawn so this was the fix,
        // but it started messing up the mouse event triggering with JDK8 so I moved it)
        if (controller.getShowScroller()) {
            controller.resetScroller();
        }
        
    }

    // -------------------- Monitors --------------------

    /** Listens for mouse clicks. */
    private class MouseClickMonitor extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println("Clicked");
            // Transform the clicked point:
            MyPoint2D p = new MyPoint2D(e.getPoint());
            p.transform(panelToImage); // transform from panel to section image pixel coordinates
            // Tell the controller about the mouse click:
            controller.mouseClick(p);
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            mouseInside = true;
        }
        @Override
        public void mouseExited(MouseEvent e) {
            mouseInside = false;
            repaint();
        }
//        @Override
//        public void mousePressed (MouseEvent e) {
//            // Tell the controller about the mouse press:
//            controller.mousePress(e);
//        }
//        @Override
//        public void mouseReleased (MouseEvent e) {
//            // Tell the controller about the mouse release:
//            controller.mouseRelease();
//        }
    }

    /** Listens for mouse movement. */
    private class MouseMoveMonitor extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            //System.out.println("Moved");
            // Transform the current cursor location:
            MyPoint2D p = new MyPoint2D(e.getPoint());
            p.transform(panelToImage);
            // Tell the controller about the mouse move:
            controller.mouseMove(p);
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            //System.out.println("Dragged");
            // Transform the current cursor location:
            MyPoint2D p = new MyPoint2D(e.getPoint());
            p.transform(panelToImage);
            // Tell the controller about the mouse drag:
            controller.mouseDrag(p);
        }
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!zoomer.writeSessionInformation(writer)) { return false; }
        // Write the origin coordinates:
        String textLine;
        if (origin==null) {
            textLine = "null\n";
        } else {
            textLine = origin.toString() + "\n";
        }
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = zoomer.readSessionInformation(reader,merge); if (msg!=null) { return msg.trim() + " (2D panel)."; }
        // Read the origin coordinates:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "2D origin coordinates line."; }
        textLine = textLine.trim();
        if (textLine.startsWith("null")) {
            origin = null;
        } else {
            String[] s = textLine.split("[ ]+");
            if (s.length<2) { return "Not enough values on 2D origin coordinates line."; }
            try {
                double x = Double.parseDouble(s[0]);
                double y = Double.parseDouble(s[1]);
                origin = new MyPoint2D(x,y);
            } catch (NumberFormatException e) { return "Parsing 2D origin coordinates."; }
        }
        // Return successfully:
        return null;
    }
    
}
