package facetmodeller.gui;

import geometry.Circle;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import geometry.ZBary;
import geometry.ZPlane;
import java.awt.Color;
import java.awt.Polygon;
import paint.ZBuffer;

/** For 3D rendering of a scene using z-buffer strategy.
 * @author Peter Lelievre
 */
public class ZBuffer3D extends ZBuffer {
    
    // -------------------- Constructor -------------------
    
    public ZBuffer3D(int w, int h, double z0, Color col) {
        super(w,h,z0,col);
    }
    
    // -------------------- Public methods -------------------
    
    /** Processes a node.
     * @param p3
     * @param col
     * @param d */
    public void putNode(MyPoint3D p3, Color col, double d) {
        // Create a circle object:
        double r = d/2.0;
        Circle circle = new Circle( p3.getX(), p3.getY(), r );
        // Get the bounding box:
        BBox box = getBBox(p3,r);
        // Loop over each pixel in the bounding box:
        for (int i=box.i1 ; i<box.i2 ; i++) {
            for (int j=box.j1 ; j<box.j2 ; j++) {
                MyPoint2D p = new MyPoint2D(i,j);
                // Check if the pixel is inside the projected sphere:
                if (!circle.inside(p)) { continue; }
                // Calculate the height of the pixel projected onto the sphere's surface:
                double z = p3.getZ() + circle.interpolate(p); // addition means closer to viewer
                // Compare against the z value in the ZBuffer:
                setPixel(i,j,z,col); // the checking is done inside this method
            }
        }
    }
    
    /** Processes a facet.
     * @param facet
     * @param faceColor Set to null to only draw edges.
     * @param edgeColor */
    public void putFacet(MyPoint3D[] facet, Color faceColor, Color edgeColor) {
        // Get the number of nodes in the facet:
        int n = facet.length;
        // Check if the face needs to be painted:
        if ( faceColor != null ) {
            if (n==3) {
                putTri(facet,faceColor);
            } else if (n>3) {
                putPoly(facet,faceColor);
            }
        }
        // Loop over each edge in the facet:
        for (int i=0 ; i<n ; i++) {
            int i2 = i + 1;
            if (i2>=n) { i2 = 0; }
            putEdge(facet[i],facet[i2],edgeColor);
        }
    }
    
    /** Processes an edge.
     * @param p1
     * @param p2
     * @param col */
    public void putEdge(MyPoint3D p1, MyPoint3D p2, Color col) {
        // Modified Bresenham algorithm for drawing a pixellated line:
        // http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
        final int x1 = (int)p1.getX();
        final int y1 = (int)p1.getY();
        final int x2 = (int)p2.getX();
        final int y2 = (int)p2.getY();
        final double z1 = p1.getZ();
        final double z2 = p2.getZ();
        final int w = x2 - x1;
        final int h = y2 - y1;
        double d = Math.sqrt( Math.pow(w,2) + Math.pow(h,2) ); // distance between points in (x-y) plane
        //double d = p1.distanceToPointXY(p2); // distance between points in (x-y) plane
        if (d<1) { return; } // 
        final double zslope = (z2-z1)/d;
        int x = x1;
        int y = y1;
        int dx1=0, dy1=0, dx2=0, dy2=0;
        if (w<0) { dx1 = -1; } else if (w>0) { dx1 = 1; }
        if (h<0) { dy1 = -1; } else if (h>0) { dy1 = 1; }
        if (w<0) { dx2 = -1; } else if (w>0) { dx2 = 1; }
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        //if (!(longest>shortest)) {
        if ( longest <= shortest ) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h<0) { dy2 = -1; } else if (h>0) { dy2 = 1; }
            dx2 = 0;
        }
        int numerator = ( longest >> 1 );
        //int width = getWidth();
        //int height = getHeight();
        for (int i=0 ; i<=longest ; i++ ) {
            // Interpolate the z value:
            d = Math.sqrt( Math.pow(x-x1,2) + Math.pow(y-y1,2) ); // distance along the line
            double z = z1 + d*zslope;
            // Set the pixel and continue:
            setPixel(x,y,z+1,col); // the setPixel method checks that the pixel coordinates are inside the image
            numerator += shortest;
            //if (!(numerator<longest)) {
            if ( numerator >= longest ) {
                numerator -= longest;
                x += dx1;
                y += dy1;
            } else {
                x += dx2;
                y += dy2;
            }
        }
    }
    
    // -------------------- Private methods -------------------
    
    /** Processes a triangular facet (n=3). */
    private void putTri(MyPoint3D[] facet, Color col) {
        // Initialize barycentric information:
        MyPoint3D p0 = facet[0];
        MyPoint3D p1 = facet[1];
        MyPoint3D p2 = facet[2];
        ZBary bar = new ZBary(p0,p1,p2);
        // Check for collinear vertices:
        if (!bar.check()) { return; }
        // Get the bounding box:
        BBox box = getBBox(facet);
        // Loop over each pixel in the bounding box:
        for (int i=box.i1 ; i<=box.i2 ; i++) {
            for (int j=box.j1 ; j<=box.j2 ; j++) {
                // Calculate barycentric coordinates for the point:
                MyPoint2D p = new MyPoint2D(i,j);
                bar.calculate(p);
                // Check if the pixel is inside the projected triangle:
                if (!bar.inOrOn()) { continue; }
                // Interpolate the z value at the current pixel location:
                double z = bar.interpolate();
                // Compare against the z value in the ZBuffer:
                setPixel(i,j,z,col); // the checking is done inside this method
            }
        }
    }
    
    /** Processes a polygonal facet (n>3). */
    private void putPoly(MyPoint3D[] facet, Color col) {
        // Fit a plane to the polygon (assuming it is planar):
        ZPlane plane = fitZPlane(facet);
        // Check for collinear vertices:
        if (plane==null) { return; }
        if (!plane.check()) { return; }
        // Get the bounding box:
        BBox box = getBBox(facet);
        // Loop over each pixel in the bounding box:
        for (int i=box.i1 ; i<=box.i2 ; i++) {
            for (int j=box.j1 ; j<=box.j2 ; j++) {
                // Check if the pixel is inside the projected polygon:
                double x = i;
                double y = j;
                if (!inPoly(facet,x,y)) { continue; }
                // Interpolate the z value at the current pixel location (assuming the polygon is planar):
                double z = plane.interpolate(x,y);
                // Compare against the z value in the ZBuffer:
                setPixel(i,j,z,col); // the checking is done inside this method
            }
        }
    }

    /** Fits a plane to a polygonal facet (assuming it is planar).
     * The equation of the plane is z=a*x+b*y+c
     * @param facet
     * @return 
     */
    private ZPlane fitZPlane(MyPoint3D[] facet) {
        // The plotting works best if we calculate a plane using vectors that are as close to 90 degrees as possible.
        // Set tolerance on angle between the two vectors:
        double angleTol = Math.PI/3.0; // HARDWIRE: reduce to speed up, increase to improve plotting
        double angleBest = 0.0;
        int[] ibest = null;
        boolean ok = false; // set to true once we've found an appropriate set of nodes below
        // Loop over each node:
        for (int i0=0 ; i0<facet.length-2 ; i0++ ) {
            // Set initial node point:
            MyPoint3D p0 = facet[i0];
            // Loop over each node again:
            for (int i1=i0+1 ; i1<facet.length-1 ; i1++ ) {
                // Set next node point:
                MyPoint3D p1 = facet[i1];
                // Calculate vector between first pair of points:
                MyPoint3D v1 = p0.vectorToPoint(p1);
                // Loop over each node a last time:
                for (int i2=i1+1 ; i2<facet.length ; i2++ ) {
                    // Set final node point:
                    MyPoint3D p2 = facet[i2];
                    // Calculate vector between second pair of points:
                    MyPoint3D v2 = p0.vectorToPoint(p2);
                    // Check angle between vectors is appropriate (if too small then zbuffer painting issues can occur):
                    double angle = v1.angleToVector(v2); // angle in radians on [0,pi]
                    angle = Math.min( angle , (Math.PI-angle) ); // angle in radian on [0,pi/2]
                    if ( angle >= angleTol ) {
                        ibest = new int[]{i0,i1,i2};
                        ok = true;
                        break; // from inner for loop
                    }
                    // Keep track of the best angle so far:
                    if ( angle > angleBest ) {
                        angleBest = angle;
                        ibest = new int[]{i0,i1,i2};
                    }
                } // inner for loop
                // Check if we should exit from middle for loop:
                if (ok) { break; }
            } // middle for loop
            // Check if we should exit from outer for loop:
            if (ok) { break; }
        } // outer for loop
        // Calculate the normal to the plane using the two vectors:
        MyPoint3D p0 = facet[ibest[0]];
        MyPoint3D p1 = facet[ibest[1]];
        MyPoint3D p2 = facet[ibest[2]];
        MyPoint3D v1 = p0.vectorToPoint(p1);
        MyPoint3D v2 = p0.vectorToPoint(p2);
        MyPoint3D vn = v1.cross(v2); // vector normal to the plane
        // Create a new ZPlane object:
        ZPlane p = new ZPlane(p0,vn);
        // TODO: check for planar polygonal facet
        // Return the plane (or null if no suitable plane found):
        if (p.check()) {
            return p;
        } else {
            return null;
        }
    }
    
    /** Checks if a point is inside a rotated and projected polygon.
     * @param facet
     * @param x
     * @param y
     * @return 
     */
    private boolean inPoly(MyPoint3D[] facet, double x, double y) {
        
        // Create polygon object:
        Polygon p = new Polygon();
        for (MyPoint3D fi : facet) {
            int xi = (int)fi.getX();
            int yi = (int)fi.getY();
            p.addPoint(xi,yi);
        }
        return p.contains(x,y);
        
        /*
        boolean ok = p.contains(x,y);
        // That doesn't work! Use ray intersection method ...
        // Loop over the edges in the facet:
        int nedges = facet.length;
        int ncross = 0; // counter for the number of times the polygon crosses the +x ray
        for (int i=0 ; i<nedges ; i++ ) {
            // Get edge vertex coordinates:
            MyPoint3D p1 = facet[i]; // first vertex in current edge
            MyPoint3D p2; // second vertex in current edge
            if (i==(nedges-1)) { // reached last edge, between vertices [n-1] and [0]
                p2 = facet[0];
            } else {
                p2 = facet[i+1];
            }
            double x1 = p1.getX();
            double y1 = p1.getY();
            double x2 = p2.getX();
            double y2 = p2.getY();
            // Check for complicating case of the point lying on a vertex:
            if ( ( x1==x && y1==y ) || ( x2==x && y2==y ) ) { return true; } // doesn't really matter what value is returned
            // If the edge is not to the +x of the point (i.e. both vertices are not) then cycle:
            if ( x1<=x && x2<=x ) { continue; } // to next edge (next iteration of for i loop)
            // Check for complicating case of one or both vertices exactly on the y=0 line:
            //if ( y1==y || y2==y ) {
            // The intersection counts only if the second vertex of the side lies below the ray:
            // (see http://en.wikipedia.org/wiki/Point_in_polygon#Ray_casting_algorithm)
            if ( y1==y && y2<y ) {
                ncross++;
                continue;
            }
            // If the edge does not straddle the y=0 line then cycle:
            if ( Math.min(y1,y2)>=y || Math.max(y1,y2)<=y ) { continue; } // catches cases of vertex intersection not dealt with above
            // Determine the intersection with the y=0 line:
            double xi = x1 + (y-y1)*(x2-x1)/(y2-y1); // no danger of division by zero due to straddle check above
            // If the intersection is on the +x side of the y=0 line then increment the ray intersection counter:
            if ( xi>x ) { ncross++; }
        } // for i loop (over edges)
        // Check sign of ncross:
        boolean ok2 = ( ncross%2 != 0 );
        if ( ok != ok2 ) {
            int j=0;
        }
        if ( ok2 ) {
            int j=0;
        } else {
            int j=0;
        }
        return ok; // true (inside) if odd
        */
    }
    
    /** Finds the bounding box for a node. */
    private BBox getBBox(MyPoint3D p, double r) {
        // The nodes are plotted as spheres of radius r:
        BBox b = new BBox();
        //MyPoint3D p = node.getPoint3DRotated();
        double x = p.getX();
        double y = p.getY();
        b.i1 = (int)Math.ceil(x-r);
        b.i2 = (int)Math.floor(x+r);
        b.j1 = (int)Math.ceil(y-r);
        b.j2 = (int)Math.floor(y+r);
        int w = getWidth()  - 1;
        int h = getHeight() - 1;
        // Check for values out-of-bounds:
        b.inRange(w,h);
        // Return the object:
        return b;
    }
    
    /** Finds the bounding box for a facet. */
    private BBox getBBox(MyPoint3D[] facet) {
        // Initialization before max/min operations:
        int w = getWidth()  - 1;
        int h = getHeight() - 1;
        BBox b = new BBox();
        b.i1 = w;
        b.j1 = h;
        b.i2 = 0;
        b.j2 = 0;
        // Loop over each node in the facet:
        //for (int i=0 ; i<facet.length ; i++) {
        //    MyPoint3D p = facet[i];
        for (MyPoint3D p : facet) {
            double x = p.getX();
            double y = p.getY();
            int x1 = (int)Math.floor(x);
            int x2 = (int)Math.ceil(x);
            int y1 = (int)Math.floor(y);
            int y2 = (int)Math.ceil(y);
            b.i1 = Math.min(b.i1,x2);
            b.i2 = Math.max(b.i2,x1);
            b.j1 = Math.min(b.j1,y2);
            b.j2 = Math.max(b.j2,y1);
        }
        // Check for values out-of-bounds:
        b.inRange(w,h);
        // Return the object:
        return b;
    }
//    /** Finds the bounding box for an edge. */
//    private BBox getBBox(MyPoint3D p1, MyPoint3D p2) {
//        // Initialization before max/min operations:
//        int w = getWidth()  - 1;
//        int h = getHeight() - 1;
//        BBox b = new BBox();
//        b.i1 = (int)Math.floor( Math.min(p1.getX(),p2.getX()) );
//        b.i2 = (int)Math.floor( Math.max(p1.getX(),p2.getX()) );
//        b.j1 = (int)Math.floor( Math.min(p1.getY(),p2.getY()) );
//        b.j2 = (int)Math.floor( Math.max(p1.getY(),p2.getY()) );
//        // Check for values out-of-bounds:
//        b.inRange(w,h);
//        // Return the object:
//        return b;
//    }
    private class BBox {
        public int i1,i2,j1,j2;
        private void inRange(int w, int h) {
            i1 = inRange(i1,0,w);
            i2 = inRange(i2,0,w);
            j1 = inRange(j1,0,h);
            j2 = inRange(j2,0,h);
        }
        private int inRange(int i, int i1, int i2) {
            int j = i;
            j = Math.max(j,i1); // j>=i1
            j = Math.min(j,i2); // j<=i2
            return j;
        }
    }
    
}
