package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.groups.Group;
import java.awt.Color;
import javax.swing.JColorChooser;

public final class ChangeGroupColorMenuTask extends ControlledMenuTask {
    
    public ChangeGroupColorMenuTask(FacetModeller con) { super(con); }

    @Override
    public String text() { return "Change group color"; }

    @Override
    public String tip() { return "Change the painting color for the group"; }

    @Override
    public String title() { return "Change Group Color"; }

    @Override
    public boolean check() {
        if ( !controller.hasGroups() ) { return false; }
        return ( controller.getSelectedCurrentGroup() != null );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the selected group:
        Group group = controller.getSelectedCurrentGroup();
        // Ask for the colour:
        Color col = JColorChooser.showDialog(controller,title(),group.getNodeColor());
        // Check response:
        if (col == null) { return; }
        // Set painting colours to that specified:
        group.setColor(col);
        // Repaint:
        controller.redraw();
    }
    
}
