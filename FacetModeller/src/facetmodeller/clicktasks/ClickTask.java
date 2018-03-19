package facetmodeller.clicktasks;

import geometry.MyPoint2D;

/** Interface for a task performed by FacetModeller that requires a clicked point to execute.
 * @author Peter
 */
public interface ClickTask {
    
    public int mode(); // returns a click mode associated with the task
    public String text(); // text for menu items and in pull-down list
    public String tip(); // tool tip string for menu items or buttons
    public String title(); // title for dialogs
    public boolean check(); // return true if all the required information is available
    public void mouseClick(MyPoint2D p); // execute whatever task is required when the mouse is clicked
    public void mouseDrag(MyPoint2D p); // execute whatever task is required when the mouse is dragged
    public void mouseMove(MyPoint2D p); // execute whatever task is required when the mouse is moved
    
}
