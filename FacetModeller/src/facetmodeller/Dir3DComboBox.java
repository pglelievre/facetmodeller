package facetmodeller;

import geometry.Dir3D;
import javax.swing.JComboBox;

/** A JComboBox connected to the Dir3D enum class.
 * @author Peter
 */
public class Dir3DComboBox extends JComboBox<String>{
    private static final long serialVersionUID = 1L;
    
    private static final String[] DIRECTION_STRINGS = {"unspecified","X","Y","Z"};
    
    public Dir3DComboBox() {
        super(DIRECTION_STRINGS);
    }
    
    /** This method should always be used instead of getSelectedIndex.
     * @return  */
    public Dir3D getSelectedDirection() {
        int i = getSelectedIndex();
        switch (i) {
            case 1:
                return Dir3D.X;
            case 2:
                return Dir3D.Y;
            case 3:
                return Dir3D.Z;
            default: // 0
                return null;
        }
    }
    
    /** This method should always be used instead of setSelectedIndex.
     * @param dir */
    public void setSelectedDirection(Dir3D dir) {
        if (dir==null) {
            setSelectedIndex(0);
            return;
        }
        switch (dir) {
            case X:
                setSelectedIndex(1);
                return;
            case Y:
                setSelectedIndex(2);
                return;
            case Z:
                setSelectedIndex(3);
                return;
            default: // null
                setSelectedIndex(0);
        }
    }
    
}
