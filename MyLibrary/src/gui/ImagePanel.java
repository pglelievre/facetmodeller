package gui;

import geometry.MyPoint2D;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/** A panel in which an image is displayed.
 * @author Peter
 */
@SuppressWarnings("ProtectedField")
public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Transform information for fitting the image within the panel tightly while maintaining aspect ratio:
    protected AffineTransform imageToPanel, panelToImage;
    protected double scaling=1.0;
    protected MyPoint2D translation = MyPoint2D.zero();
    
    protected void tightFitImage(Graphics g, BufferedImage image) {
        double imageWidth  = image.getWidth();
        double imageHeight = image.getHeight();
        double scaledWidth  = scaling*imageWidth;
        double scaledHeight = scaling*imageHeight;
        g.drawImage(image,(int)translation.getX(),(int)translation.getY(),(int)scaledWidth,(int)scaledHeight,this);
    }
    
    protected void createTightFitTransform() {
        // Create forward affine transformation:
        // (note that new transformations are added to the right of the matrix
        //  and therefore come earlier, which is completely non-intuitive)
        imageToPanel = new AffineTransform();
        imageToPanel.translate(translation.getX(),translation.getY());
        imageToPanel.scale(scaling,scaling);
        // Create inverse affine transformation:
        panelToImage = new AffineTransform();
        panelToImage.scale(1.0/scaling,1.0/scaling);
        panelToImage.translate(-translation.getX(),-translation.getY());
    }
    
    protected void resetScroller() {};
    
    /** Listens for panel resizing. */
    @SuppressWarnings("PublicInnerClass")
    public class PanelSizeMonitor extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            // Call the paintComponent method:
            repaint();
            // May need to update a scroller. If so, the resetScroller method should be overridden.
            resetScroller();
        }
    }
    
}
