package facetmodeller;

import facetmodeller.gui.SceneInfo;
import fileio.FileUtils;
import fileio.SessionIO;
import geometry.MyPoint2D;
import geometry.MyPoint3D;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Defines a 3D block volume of interest.
 * @author Peter
 */
public class VOI implements SessionIO {
    
    // -------------------- Properties -------------------
    
    public static final int N_EDGES=12;
    
    private double x1,x2,y1,y2,z1,z2;
    
    // -------------------- Constructors -------------------
    
    public VOI() {} // required by SessionLoader, should not be used elsewhere
    
    public VOI(MyPoint3D p1, MyPoint3D p2) {
        x1 = Math.min(p1.getX(),p2.getX());
        y1 = Math.min(p1.getY(),p2.getY());
        z1 = Math.min(p1.getZ(),p2.getZ());
        x2 = Math.max(p1.getX(),p2.getX());
        y2 = Math.max(p1.getY(),p2.getY());
        z2 = Math.max(p1.getZ(),p2.getZ());
    }
    
    public VOI(double x1,double x2,double y1,double y2,double z1,double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
    
    // -------------------- Deep Copy -------------------
    
//    public VOI deepCopy() {
//        return new VOI(x1,x2,y1,y2,z1,z2);
//    }
    
    // -------------------- Public Methods -------------------
    
    public MyPoint2D getEastingLimits() {
        return new MyPoint2D(x1,x2);
    }
    public MyPoint2D getNorthingLimits() {
        return new MyPoint2D(y1,y2);
    }
    public MyPoint2D getElevationLimits() {
        return new MyPoint2D(z1,z2);
    }
    
    public MyPoint3D getMinimumLimits() {
        return new MyPoint3D(x1,y1,z1);
    }
    public MyPoint3D getMaximumLimits() {
        return new MyPoint3D(x2,y2,z2);
    }
    
    public MyPoint3D[][] getEdges() {
        // Edges will be placed into an array:
        MyPoint3D[][] edges = new MyPoint3D[N_EDGES][2];
        // Edges in x direction:
        edges[ 0][0] = new MyPoint3D(x1,y1,z1);
        edges[ 0][1] = new MyPoint3D(x2,y1,z1);
        edges[ 1][0] = new MyPoint3D(x1,y2,z1);
        edges[ 1][1] = new MyPoint3D(x2,y2,z1);
        edges[ 2][0] = new MyPoint3D(x1,y1,z2);
        edges[ 2][1] = new MyPoint3D(x2,y1,z2);
        edges[ 3][0] = new MyPoint3D(x1,y2,z2);
        edges[ 3][1] = new MyPoint3D(x2,y2,z2);
        // Edges in y direction:
        edges[ 4][0] = new MyPoint3D(x1,y1,z1);
        edges[ 4][1] = new MyPoint3D(x1,y2,z1);
        edges[ 5][0] = new MyPoint3D(x1,y1,z2);
        edges[ 5][1] = new MyPoint3D(x1,y2,z2);
        edges[ 6][0] = new MyPoint3D(x2,y1,z1);
        edges[ 6][1] = new MyPoint3D(x2,y2,z1);
        edges[ 7][0] = new MyPoint3D(x2,y1,z2);
        edges[ 7][1] = new MyPoint3D(x2,y2,z2);
        // Edges in z direction:
        edges[ 8][0] = new MyPoint3D(x1,y1,z1);
        edges[ 8][1] = new MyPoint3D(x1,y1,z2);
        edges[ 9][0] = new MyPoint3D(x2,y1,z1);
        edges[ 9][1] = new MyPoint3D(x2,y1,z2);
        edges[10][0] = new MyPoint3D(x1,y2,z1);
        edges[10][1] = new MyPoint3D(x1,y2,z2);
        edges[11][0] = new MyPoint3D(x2,y2,z1);
        edges[11][1] = new MyPoint3D(x2,y2,z2);
        // Return the edges:
        return edges;
    }
    
    public MyPoint3D[] getCorners() {
        // Corners will be placed into an array:
        MyPoint3D[] corners = new MyPoint3D[8];
        corners[0] = new MyPoint3D(x1,y1,z1);
        corners[1] = new MyPoint3D(x2,y1,z1);
        corners[2] = new MyPoint3D(x1,y2,z1);
        corners[3] = new MyPoint3D(x2,y2,z1);
        corners[4] = new MyPoint3D(x1,y1,z2);
        corners[5] = new MyPoint3D(x2,y1,z2);
        corners[6] = new MyPoint3D(x1,y2,z2);
        corners[7] = new MyPoint3D(x2,y2,z2);
        // Return the corners:
        return corners;
    }
    
    public void scale(double f) {
        x1 *= f;
        y1 *= f;
        z1 *= f;
        x2 *= f;
        y2 *= f;
        z2 *= f;
    }
    
    public void padXY(double p) {
        x1 -= p;
        y1 -= p;
        z1 -= p;
        x2 += p;
        y2 += p;
        z2 += p;
    }
    
//    public double[] getDoubles() {
//        double[] d = new double[6];
//        d[0] = x1;
//        d[1] = x2;
//        d[2] = y1;
//        d[3] = y2;
//        d[4] = z1;
//        d[5] = z2;
//        return d;
//    }
    
    /** Returns true if the supplied point is within the VOI or on the boundary.
     * @param p
     * @return  */
    public boolean inOrOn(MyPoint3D p) {
        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();
        return ( x1<=x && x<=x2 && y1<=y && y<=y2 && z1<=z && z<=z2 );
    }
    
    /** Returns true if the supplied point is within the VOI depth extents.
     * @param z
     * @return  */
    public boolean inOrOnZ(double z) {
        return ( z1<=z && z<=z2 );
    }
    
    public SceneInfo getSceneInfo(MyPoint3D origin) {
        
        // Calculate x,y,z coordinate ranges for the VOI:
        MyPoint3D p0 = origin;
        double r = 0.0; // will hold largest node distance from origin
        if (p0!=null) {
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y1,z1) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y1,z1) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y2,z1) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y2,z1) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y1,z2) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y1,z2) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y2,z2) ) );
            r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y2,z2) ) );
        }
        
        // Calculate the centroid and width of the coordinate ranges:
        SceneInfo info = new SceneInfo();
        if (p0==null) {
            // Set centroid to middle of range:
            p0 = new MyPoint3D(0.5*(x1+x2),0.5*(y1+y2),0.5*(z1+z2));
        }
        info.setOrigin(p0);
        info.setDimensions( new MyPoint3D(x2-x1,y2-y1,z2-z1) );
        if ( origin==null || r==0.0 ) {
            r = 0.5*(x2-x1);
        } else {
            r *= 2.0;
        }
        if (r==0.0) {
            info.setScaling(0.0);
        } else {            
            info.setScaling(1.0d/r);
        }
        
        // Recalculate the scaling if required:
        if (origin!=null) { return info; }
        r = 0.0;
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y1,z1) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y1,z1) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y2,z1) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y2,z1) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y1,z2) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y1,z2) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x1,y2,z2) ) );
        r = Math.max(r, p0.distanceToPoint( new MyPoint3D(x2,y2,z2) ) );
        r *= 2.0;
        if (r==0.0) {
            info.setScaling(0.0);
        } else {
            info.setScaling(1.0d/r);
        }
        
        // Return:
        return info;
        
    }

    // -------------------- SectionIO Methods --------------------

    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write the values:
        String textLine = x1 + " " + x2 + " " + y1 + " " + y2 + " " + z1 + " " + z2 + "\n";
        return FileUtils.writeLine(writer,textLine);
    }

    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read the values:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading VOI coordinates line."; }
        textLine = textLine.trim();
        if (textLine.startsWith("null")) { return "Null line for VOI coordinates."; }
        String[] ss;
        ss = textLine.split("[ ]+");
        if (ss.length<6) { return "Not enough values on VOI coordinates line."; }
        try {
            x1 = Double.parseDouble(ss[0].trim());
            x2 = Double.parseDouble(ss[1].trim());
            y1 = Double.parseDouble(ss[2].trim());
            y2 = Double.parseDouble(ss[3].trim());
            z1 = Double.parseDouble(ss[4].trim());
            z2 = Double.parseDouble(ss[5].trim());
        } catch (NumberFormatException e) { return "Parsing VOI coordinates."; }
        // Return successfully:
        return null;
    }
    
}
