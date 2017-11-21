package geometry;

import java.util.Arrays;

/** A matrix of doubles.
 * @author Peter
 */
public class Matrix {

    // -------------------- Properties -------------------
    
    //private final int m; // number of rows
    //private final int n; // number of columns
    protected final double[][] d; // m-by-n array

    // -------------------- Constructors -------------------
    
    public Matrix(int m, int n) {
        //this.m = m;
        //this.n = n;
        this.d = new double[m][n];
        for (double[] row: d) { Arrays.fill(row,0.0); }
    }

    // -------------------- Getters -------------------
    
    public double get(int i, int j) { return d[i][j]; }

    // -------------------- Setters -------------------
    
    public void set(int i, int j, double k) { d[i][j] = k; }
    public void add(int i, int j, double k) { d[i][j] += k; }
    
}
