package facetmodeller.panels;

import facetmodeller.FacetModeller;
import facetmodeller.ZoomableSessionIO;
import facetmodeller.gui.Projector3D;
import geometry.Matrix3D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Holds the View3DPanel and associated ZoomBar.
 * @author Peter
 */
public final class Panel3D extends JPanel implements ZoomableSessionIO {
    // TODO: implement Zoomable and SessionIO separately once Java allows implementing multiple interfaces
    private static final long serialVersionUID = 1L;
    
    private final FacetModeller controller;
    private final ViewBar viewBar;
    private final View3DPanel viewPanel; // the 3D viewer panel
    private final ZoomBar zoomBar;
    
    public Panel3D(FacetModeller con) {
        // Set the controller:
        controller = con;
        // Create the 3D view panel:
        viewPanel = new View3DPanel(con);
        // Create the 3D view button bar:
        viewBar = new ViewBar(viewPanel);
        // Create the zoom bar:
        zoomBar = new ZoomBar(this);
        // Add the objects to a single panel:
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        add(viewPanel,BorderLayout.CENTER);
        add(viewBar,BorderLayout.NORTH);
        add(zoomBar,BorderLayout.SOUTH);
    }
    
    // Wrappers for the View3DPanel class:
    public void redraw() { viewPanel.repaint(); }
    public Projector3D getProjector() { return viewPanel.getProjector(); }
    public Matrix3D getRotationMatrix() { return viewPanel.getRotationMatrix(); }
    public void setRotationMatrix(Matrix3D m) { viewPanel.setRotationMatrix(m); }
    public void setBackgroundColor(Color col) { viewPanel.setBackground(col); }
    @Override
    public void zoomIn() { viewPanel.zoomIn(); }
    @Override
    public void zoomOut() { viewPanel.zoomOut(); }
    @Override
    public void zoomReset() {
        viewPanel.zoomReset();
        controller.resetShiftButtonText();
    }
    public double getZoomFactor(){ return viewPanel.getZoomFactor(); }
    public void setZoomFactor(double d) { viewPanel.setZoomFactor(d); }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        return viewPanel.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        return viewPanel.readSessionInformation(reader,merge);
    }
    
}
