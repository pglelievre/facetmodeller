package facetmodeller.panels;

/** Defines parent panel of a ShiftButtonPanel.
 * @author Peter
 */
public interface ShiftablePanel {
    
    public void changeShift(int dx, int dy);
    public void clearShift();
    
}
