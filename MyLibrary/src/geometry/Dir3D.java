package geometry;

/** Defines possible 3D axis directions.
 * @author Peter Lelievre
 */
public enum Dir3D {
    
    // I'm using custom ordinals here to avoid having to change the FacetModeller session saver/loader.
    // If I change the version in the future then I can remove the custom ordinals.
    X(1), Y(2), Z(3);
    private final int i;
    private Dir3D(int i) { this.i = i; }
    
    /** Returns a custom ordinal representing the enum object.
     * @return  */
    public int toInt() {
        return i;
    }
    
    /** Returns an enum object associated with a particular custom ordinal.
     * If the integer supplied is not one of the custom ordinals then null is returned.
     * @param i
     * @return 
     */
    public static Dir3D fromInt(int i) {
        switch (i) {
            case 1:
                return X;
            case 2:
                return Y;
            case 3:
                return Z;
            default:
                return null;
        }
    }
    
    public String toChar() {
        switch (i) {
            case 1:
                return "X";
            case 2:
                return "Y";
            case 3:
                return "Z";
            default:
                return null;
        }
    }
    
    public boolean compare(Dir3D d) {
        return (i==d.toInt());
    }
    
    public double getCoord(MyPoint3D p) {
        switch (i) {
            case 1:
                return p.getX();
            case 2:
                return p.getY();
            case 3:
                return p.getZ();
            default:
                return 0.0;
        }
    }
    
    public MyPoint3D setCoord(MyPoint3D p, double d) {
        switch (i) {
            case 1:
                p.setX(d);
                break;
            case 2:
                p.setY(d);
                break;
            case 3:
                p.setZ(d);
                break;
            default:
                return null;
        }
        return p;
    }
    
}
