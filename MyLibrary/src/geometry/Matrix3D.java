package geometry;

import fileio.FileUtils;
import fileio.SessionIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Arrays;

/** 3-by-3 matrix for use multiplying MyPoint3D objects.
 *
 * @author Peter
 */
public final class Matrix3D extends Matrix implements SessionIO {

    // -------------------- Constructors -------------------
    
    public Matrix3D() {
        super(3,3); // initializes the matrix to a zero-matrix
    }
    
    // Pulls information out of the d1 array along rows then columns;
    public Matrix3D(double[] d1) {
        super(3,3);
        int k = -1;
        for (int i=0 ; i<3 ; i++) { // loop over rows
            for (int j=0 ; j<3 ; j++) { // loop over columns
                k++;
                set(i,j,d1[k]); // d[i][j] = d1[k];
            }
        }
    }
    
    // ------------------- Deep Copy ------------------
    
    public Matrix3D deepCopy() {
        // Construct new object:
        Matrix3D mat = new Matrix3D();
        // Copy over all properties not set during construction:
        for (int i=0 ; i<3 ; i++) { // loop over rows
            for (int j=0 ; j<3 ; j++) { // loop over columns
                mat.set(i,j, get(i,j) );
            }
        }
        // Return the new object:
        return mat;
    }

    // -------------------- Public Methods -------------------
    
    public Matrix3D inv() {
        // http://en.wikipedia.org/wiki/Invertible_matrix#Inversion_of_3.C3.973_matrices
        double det = d[0][0]*( d[1][1]*d[2][2] - d[1][2]*d[2][1] )
                   - d[0][1]*( d[2][2]*d[1][0] - d[1][2]*d[2][0] )
                   + d[0][2]*( d[1][0]*d[2][1] - d[1][1]*d[2][0] );
        if (det==0.0) { return null; }
        double A = d[1][1]*d[2][2] - d[1][2]*d[2][1];
        double D = d[0][2]*d[2][1] - d[0][1]*d[2][2];
        double G = d[0][1]*d[1][2] - d[0][2]*d[1][1];
        double B = d[1][2]*d[2][0] - d[1][0]*d[2][2];
        double E = d[0][0]*d[2][2] - d[0][2]*d[2][0];
        double H = d[0][2]*d[1][0] - d[0][0]*d[1][2];
        double C = d[1][0]*d[2][1] - d[1][1]*d[2][0];
        double F = d[0][1]*d[2][0] - d[0][0]*d[2][1];
        double I = d[0][0]*d[1][1] - d[0][1]*d[1][0];
        Matrix3D mat = new Matrix3D();
        mat.set(0,0, A / det );
        mat.set(1,0, B / det );
        mat.set(2,0, C / det );
        mat.set(0,1, D / det );
        mat.set(1,1, E / det );
        mat.set(2,1, F / det );
        mat.set(0,2, G / det );
        mat.set(1,2, H / det );
        mat.set(2,2, I / det );
        return mat;
    }
    
    /** Multiplies y=M*x
     * @param x
     * @return  */
    public MyPoint3D times(MyPoint3D x) {
        return new MyPoint3D( timesRow(0,x), timesRow(1,x), timesRow(2,x) );
    }

    // -------------------- Static Methods -------------------
    
    /** Multiplies C=A*B
     * @param A
     * @param B
     * @return  */
    public static Matrix3D times(Matrix3D A, Matrix3D B) {
        Matrix3D C = new Matrix3D();
        for (int i=0 ; i<=2 ; i++) { // loop over each row of A
            for (int j=0 ; j<=2 ; j++) { // loop over each column of B
                // Calculate the dot product of the ith row of A with the jth column of B:
                double v = 0;
                for (int k=0 ; k<=2 ; k++) { // loop over each item in the row/column
                    v += A.d[i][k] * B.d[k][j];
                }
                C.set(i,j,v);
            }
        }
        return C;
    }
    
    // Identity matrix.
    public static Matrix3D ident() {
        Matrix3D m = new Matrix3D();
        for (double[] row: m.d) { Arrays.fill(row,0.0); }
        m.d[0][0] = 1.0;
        m.d[1][1] = 1.0;
        m.d[2][2] = 1.0;
        return m;
    }
    
    // THESE ARE BODY ROTATIONS!
    // Rotation around x axis.
    public static Matrix3D rotX(double theta) {
        Matrix3D m = new Matrix3D();
        m.set(1,1, Math.cos(theta));
        m.set(1,2,-Math.sin(theta));
        m.set(2,1, Math.sin(theta));
        m.set(2,2, Math.cos(theta));
        m.set(0,0, 1.0);
        return m;
    }
    // Rotation around y axis.
    public static Matrix3D rotY(double theta) {
        Matrix3D m = new Matrix3D();
        m.set(0,0, Math.cos(theta));
        m.set(0,2, Math.sin(theta));
        m.set(2,0,-Math.sin(theta));
        m.set(2,2, Math.cos(theta));
        m.set(1,1, 1.0);
        return m;
    }
    // Rotation around z axis.
    public static Matrix3D rotZ(double theta) {
        Matrix3D m = new Matrix3D();
        m.set(0,0, Math.cos(theta));
        m.set(0,1,-Math.sin(theta));
        m.set(1,0, Math.sin(theta));
        m.set(1,1, Math.cos(theta));
        m.set(2,2, 1.0);
        return m;
    }
    
    // -------------------- Private Methods -------------------
    
    /** Multiplies y(i)=M_i*x (dot product with a single row). */
    private double timesRow(int i,MyPoint3D x) {
        return d[i][0]*x.getX() + d[i][1]*x.getY() + d[i][2]*x.getZ();
    }

    // -------------------- SessionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write the rotation matrix:
        String textLine = null;
        for (int i=0 ; i<3 ; i++) {
            for (int j=0 ; j<3 ; j++) {
                if (i==0 && j==0) {
                    textLine = Double.toString(d[i][j]);
                } else {
                    textLine += " " + d[i][j];
                }
            }
        }
        //       d[0][0] + " " + d[0][1] + " " + d[0][2] +
        //     + d[1][0] + " " + d[1][1] + " " + d[1][2] +
        //     + d[2][0] + " " + d[2][1] + " " + d[2][2];
        textLine += "\n";
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read the rotation matrix:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading rotation matrix line."; }
        return readSessionInformation(reader,textLine);
    }
    
    public String readSessionInformation(BufferedReader reader, String textLineIn) {
        // Read the rotation matrix:
        String textLine = textLineIn;
        if (textLine==null) { return "Reading rotation matrix line."; }
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+",10);
        if (ss.length<9) { return "Not enough values on rotation matrix line."; }
        try {
            int k = -1;
            for (int i=0 ; i<3 ; i++) {
                for (int j=0 ; j<3 ; j++) {
                    k++;
                    d[i][j] = Double.parseDouble(ss[k].trim()); // converts to Double
                }
            }
        } catch (NumberFormatException e) { return "Parsing rotation matrix elements."; }
        // Return successfully:
        return null;
    }
    
}
