package facetmodeller.gui;

import gui.CheckableEnabled;
import facetmodeller.clicktasks.ClickTask;
import javax.swing.Icon;
import javax.swing.JButton;

/** A JButton connected to a ClickTask.
 * @author Peter
 */
public final class ClickTaskButton extends JButton implements CheckableEnabled {
    private static final long serialVersionUID = 1L;
    
    private ClickTask task;
    
    public ClickTaskButton(ClickTask t, String s) {
        super(s);
        task = t;
    }
    
    public ClickTaskButton(ClickTask t, Icon icon) {
        super(icon);
        task = t;
    }
    
    public int getMode() { return task.mode(); }
    
    @Override
    public void checkEnabled() { setEnabled(task.check()); }
    
}
