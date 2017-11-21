package tasks;

/** Interface for a task performed, e.g. by FacetModeller or JMorph.
 * @author Peter
 */
public interface MenuTask {
    
    public String text(); // suggested text for menu items or buttons
    public String tip(); // tool tip string for menu items or buttons
    public String title(); // title for dialogs
    public boolean check(); // return true if all the required information is available
    public void execute(); // execute the task
    
}
