package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.gui.Projector3D;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.RegionVector;
import fileio.SessionIO;
import geometry.Matrix3D;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JPanel;

/** The panel containing the 2D and 3D view panels.
 * @author Peter
 */
public final class ViewsPanel extends JPanel implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    private Panel2D panel2D;
    private Panel3D panel3D;
    
    public ViewsPanel(FacetModeller con, int ndim, boolean showScroller, boolean showView3DPanel) {
        // Make the 2D panel:
        panel2D = new Panel2D(con,showScroller);
        // Make the 3D panel if required:
        if (ndim==3) { panel3D = new Panel3D(con); }
        // Add the two panels to the view panel:
        addThePanels( ndim==3 && showView3DPanel );
    }
    
    private void addThePanels(boolean show) {
        if (show) {
            setLayout(new GridLayout(1,2));
            add(panel2D);
            add(panel3D);
        } else {
            setLayout(new GridLayout(1,1));
            add(panel2D);
        }
    }
    
    public void showOrHideScroller(boolean show) {
        panel2D.showOrHideScroller(show);
    }
    
    public void showOrHideView3DPanel(boolean show) {
        if (panel3D==null) { return; }
        //removeAll();
        //addThePanels(show);
        if (show) {
            add(panel3D);
        } else {
            remove(panel3D);
        }
        revalidate();
        redraw3D();
    }
    
    // Wrappers:
    public void resetScroller() { panel2D.resetScroller(); }
    public void setOrigin2D(MyPoint2D p) { panel2D.setOrigin(p); }
    public NodeVector getPaintedNodes() { return panel2D.getPaintedNodes(); }
    public MyPoint2DVector getPaintedNodePoints() { return panel2D.getPaintedNodePoints(); }
    public FacetVector getPaintedFacets() { return panel2D.getPaintedFacets(); }
    public MyPoint2DVector getPaintedFacetCentroids() { return panel2D.getPaintedFacetCentroids(); }
    public RegionVector getPaintedRegions() { return panel2D.getPaintedRegions(); }
    public MyPoint2DVector getPaintedRegionPoints() { return panel2D.getPaintedRegionPoints(); }
    public boolean getMouseInside2D() { return panel2D.getMouseInside(); }
    public void zoomReset2D() { panel2D.zoomReset(); }
    public void redraw2D() { panel2D.redraw(); }
    public Color getBackgroundColor() { return panel2D.getBackgroundColor(); }
    public void setBackgroundColor(Color col) {
        panel2D.setBackgroundColor(col);
        if (panel3D!=null) { panel3D.setBackgroundColor(col); }
    }
    public Projector3D getProjector3D() { return panel3D.getProjector(); }
    public Matrix3D getRotationMatrix3D() { return panel3D.getRotationMatrix(); }
    public void setRotationMatrix3D(Matrix3D m) { panel3D.setRotationMatrix(m); }
    public void redraw3D() {
        if (panel3D!=null) { panel3D.redraw(); }
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        if (!panel2D.writeSessionInformation(writer)) { return false; }
        return !( panel3D!=null && !panel3D.writeSessionInformation(writer) );
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        String msg = panel2D.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        if ( panel3D!=null ) { return panel3D.readSessionInformation(reader,merge); }
        return null;
    }
    
}
