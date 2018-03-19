package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.ZoomableSessionIO;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.RegionVector;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Holds the SectionImagePanel and associated ZoomBar.
 * @author Peter
 */
public final class Panel2D extends JPanel implements ZoomableSessionIO {
    // TODO: implement Zoomable and SessionIO separately once Java allows implementing multiple interfaces
    private static final long serialVersionUID = 1L;
    
    private final FacetModeller controller;
    private final JScrollPane imageScroller;
    private SectionImagePanel imagePanel; // where the section and node/facet overlays are drawn
    private final ZoomBar zoomBar;
    
    public Panel2D(FacetModeller con, boolean showScroller) {
        // Set the controller:
        controller = con;
        // Create a scroll pane to hold the imagePanel:
        imageScroller = new JScrollPane(imagePanel);
        //imageScroller.setViewportView(imagePanel);
        //imageScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //imageScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //imageScroller.getViewport().setBackground(Color.BLACK);
        // Create the 2D section image display panel:
        imagePanel = new SectionImagePanel(con);
        // Set the cursor to a cross-hair when over the imagePanel:
        imagePanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        // Create the zoom bar:
        zoomBar = new ZoomBar(this);
        // Add the objects to a single panel:
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        addThePanels(showScroller);
    }
    
    public void addThePanels(boolean showScroller) {
        if (showScroller) {
            add(imageScroller,BorderLayout.CENTER);
        } else {
            add(imagePanel,BorderLayout.CENTER);
        }
        add(zoomBar,BorderLayout.SOUTH);
    }
    
    public void showOrHideScroller(boolean show) {
        //removeAll();
        //addThePanels(show);
        if (show) {
            remove(imagePanel);
            add(imageScroller);
        } else {
            remove(imageScroller);
            add(imagePanel);
        }
        revalidate();
        zoomReset();
        // redraw2D(); // performed in zoomReset
    }
    
    // Wrappers for the SectionImagePanel class:
    public void redraw() { imagePanel.repaint(); }
    public NodeVector getPaintedNodes() { return imagePanel.getPaintedNodes(); }
    public MyPoint2DVector getPaintedNodePoints() { return imagePanel.getPaintedNodePoints(); }
    public FacetVector getPaintedFacets() { return imagePanel.getPaintedFacets(); }
    public MyPoint2DVector getPaintedFacetCentroids() { return imagePanel.getPaintedFacetCentroids(); }
    public RegionVector getPaintedRegions() { return imagePanel.getPaintedRegions(); }
    public MyPoint2DVector getPaintedRegionPoints() { return imagePanel.getPaintedRegionPoints(); }
    public boolean getMouseInside() { return imagePanel.getMouseInside(); }
    public Color getBackgroundColor() { return imagePanel.getBackground(); }
    public void setBackgroundColor(Color col) { imagePanel.setBackground(col); }
    public void setOrigin(MyPoint2D p) { imagePanel.setOrigin(p); }
    @Override
    public void zoomIn() {
        imagePanel.zoomIn();
        resetScroller();
    }
    @Override
    public void zoomOut() {
        imagePanel.zoomOut();
        resetScroller();
    }
    @Override
    public void zoomReset() {
        imagePanel.zoomReset();
        controller.clearPan2D();
        resetScroller();
        controller.resetShiftButtonText();
    }
    public void resetScroller() {
        if (controller.getShowScroller()) { imageScroller.getViewport().setView(imagePanel); }
    }
    ////////////////////////////////////////////////////////
//        JScrollBar h = imageScroller.getHorizontalScrollBar();
//        JScrollBar v = imageScroller.getVerticalScrollBar();
//        if (h!=null) {
//            int h1 = h.getMinimum();
//            int h2 = h.getMaximum();
//            h.setValue((h1+h2)/2);
//        }
//        if (v!=null) {
//            int v1 = v.getMinimum();
//            int v2 = v.getMaximum();
//            v.setValue((v1+v2)/2);
//        }
//    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        return imagePanel.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        return imagePanel.readSessionInformation(reader,merge);
    }
    
}
