package paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/** For 3D rendering of a scene using z-buffer strategy.
 * @author Peter Lelievre
 */
public class ZBuffer {
    
    // -------------------- Properties -------------------
    
    private final double[][] zbuf; // z values (projected)
    private final BufferedImage image; // the image that will be drawn
    
    // -------------------- Constructor -------------------
    
    public ZBuffer(int w, int h, double z0, Color col) {
        // Initialize the z values to the supplied z0 value:
        zbuf = new double[w][h];
        for (double[] row: zbuf) { Arrays.fill(row,z0); }
        // Initialize the image to the supplied color:
        image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(col);
        g.fillRect(0,0,w,h);
        //for (int i=0 ; i<w ; i++) {
        //    for (int j=0 ; j<h ; j++) {
        //        image.setRGB(i,j,col);
        //    }
        //}
    }
    
    // -------------------- Getters -------------------
    
    public int getWidth() { return image.getWidth(); }
    public int getHeight() { return image.getHeight(); }
    public BufferedImage getImage() { return image; }
    public double getZ(int i, int j) { return zbuf[i][j]; }
    
    // -------------------- Setters -------------------
    
    public boolean setPixel(int i, int j, double z, Color col) {
        // Check i and j:
        if ( i<0 || i>=getWidth() || j<0 || j>=getHeight() ) { return false; }
        // Check z is better (larger):
        if ( z <= zbuf[i][j] ) { return false; }
        // Reset the pixel z value:
        zbuf[i][j] = z;
        // Change the colour for the pixel:
        image.setRGB(i,j,col.getRGB());
        return true;
    }
    
}
