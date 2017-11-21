package gui;

import tasks.MenuTask;
import javax.swing.JMenuItem;

/** A JMenu item connected to a MenuTask.
 * @author Peter
 */
public final class MenuTaskMenuItem extends JMenuItem implements CheckableEnabled {
    private static final long serialVersionUID = 1L;
    
    private MenuTask task;
    
    public MenuTaskMenuItem(MenuTask t) {
        super(t.text());
        this.setToolTipText(t.tip());
        task = t;
    }
    
    public MenuTaskMenuItem(MenuTask t, String s) {
        super(s); // use the supplied string for the text instead of the Task text
        task = t;
    }
    
    @Override
    public void checkEnabled() { setEnabled(task.check()); }
    
    // Wrappers for the Task class:
    //public String text() { return task.text(); }
    //public String tip() { return task.tip(); }
    public void execute() { task.execute(); }
    
}
