package facetmodeller.gui;

import gui.CheckableEnabled;
import facetmodeller.clicktasks.ClickTask;
import javax.swing.JMenuItem;

/** A JMenu item connected to a ClickTask.
 * @author Peter
 */
public final class ClickTaskMenuItem extends JMenuItem implements CheckableEnabled {
    private static final long serialVersionUID = 1L;
    
    private ClickTask task;
    
    public ClickTaskMenuItem(ClickTask t) {
        super(t.tip()); // tip=text
        task = t;
    }
    
    @Override
    public void checkEnabled() { setEnabled(task.check()); }
    
}
