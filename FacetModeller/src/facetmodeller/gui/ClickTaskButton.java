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
    
    private final ClickTask task;
    
    public ClickTaskButton(ClickTask t, String s) {
        super(s);
        task = t;
        setToolTipText(t.tip());
    }
    
    public ClickTaskButton(ClickTask t, Icon icon) {
        super(icon);
        task = t;
        setToolTipText(t.tip());
    }
    
    public int getMode() { return task.mode(); }
    
    @Override
    public void checkEnabled() { setEnabled(task.check()); }
    
}
