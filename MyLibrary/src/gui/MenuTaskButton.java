package gui;

import tasks.MenuTask;
import javax.swing.Icon;
import javax.swing.JButton;

/** A JButton connected to a MenuTask.
 * @author Peter
 */
public final class MenuTaskButton extends JButton implements CheckableEnabled {
    private static final long serialVersionUID = 1L;
    
    private MenuTask task;
    
    public MenuTaskButton(MenuTask t, String s) {
        super(s);
        task = t;
    }
    
    public MenuTaskButton(MenuTask t, Icon icon) {
        super(icon);
        task = t;
    }
    
    @Override
    public void checkEnabled() { setEnabled(task.check()); }
    
    // Wrappers for the Task class:
    //public String text() { return task.text(); }
    //public String tip() { return task.tip(); }
    public void execute() { task.execute(); }
    
}
