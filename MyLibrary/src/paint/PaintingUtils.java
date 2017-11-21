package paint;

import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/** Contains static methods for performing some common painting tasks.
 * @author Peter Lelievre
 */
public class PaintingUtils {

    /** Transforms and paints 2D points as circles of a specified width.
     * @param g2 The graphics object to paint with.
     * @param trans The transform to use.
     * @param points The points to paint.
     * @param w The width for painting.
     * @param filled Set to true to fill the circles, false otherwise.
     */
    public static void paintPoints(Graphics2D g2, AffineTransform trans, MyPoint2DVector points, int w, boolean filled) {

        for ( int i=0 ; i<points.size() ; i++ ) {
            paintPoint(g2,trans,points.get(i),w,filled);
        }

    }

    /** Transforms and paints a 2D point as a circle of a specified width.
     * @param g2 The graphics object to paint with.
     * @param trans The transform to use.
     * @param pt The point to paint.
     * @param w The width for painting.
     * @param filled Set to true to fill the circle, false otherwise.
     */
    public static void paintPoint(Graphics2D g2, AffineTransform trans, MyPoint2D pt, int w, boolean filled) {

        MyPoint2D p = pt.deepCopy(); // need to make a new point object so that the transform doesn't alter the point in the coordinate list
        if (trans!=null) { p.transform(trans); }
        if (filled) {
            g2.fillOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
        } else {
            g2.drawOval( (int)p.getX() - w/2 , (int)p.getY() - w/2 , w , w );
        }

    }

}
