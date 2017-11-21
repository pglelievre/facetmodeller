package paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/** Class for an object that can be painted.
 * @author Peter Lelievre
 */
public interface Paintable {

    // ------------------ Properties -------------------

    // Drawing styles:
    public static final int DEFAULT_LINE_WIDTH = 2; /** A default line width for painting. */
    public static final int DEFAULT_POINT_WIDTH = 6; /** A default point width for painting. */

    // -------------------- Getters -------------------

    /** Provides a primary painting colour.
     * @return A primary painting colour.
     */
    public Color getPrimaryColour();

    /** Provides a secondary painting colour.
     * @return A secondary painting colour.
     */
    public Color getSecondaryColour();

    /** Provides the line width for painting.
     * @return The line width for painting.
     */
    public int getLineWidth();

    /** Provides the point width for painting.
     * @return The point width for painting.
     */
    public int getPointWidth();

    // -------------------- Setters -------------------

    /** Sets the primary painting colour.
     * @param c The primary painting colour.
     */
    public void setPrimaryColour(Color c);

    /** Sets the secondary painting colour.
     * @param c The secondary painting colour.
     */
    public void setSecondaryColour(Color c);

    /** Sets the line width for painting.
     * @param w
     */
    public void setLineWidth(int w);

    /** Sets the point width for painting.
     * @param w
     */
    public void setPointWidth(int w);

    // -------------------- Methods -------------------

    /** Indicates whether or not the object uses the secondary colour when painting itself.
     * This will be used to determine whether or not to ask the user for the
     * secondary painting colour in some GUI's.
     * @return True if the object needs the secondary painting colour, false otherwise.
     */
    public boolean usesSecondaryColour();

    /** This method should perform any calculations that are required prior to painting.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     * @return False if some problem occurs.
     */
    public boolean runBeforePainting(boolean measuring);

    /** Paints the paintable object.
     * @param g2 A Graphics2D object to paint with.
     * @param trans An AffineTransform to use while painting.
     * @param scal A scaling value involved in the transform.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     */
    public void paint(Graphics2D g2, AffineTransform trans, double scal, boolean measuring);

}
