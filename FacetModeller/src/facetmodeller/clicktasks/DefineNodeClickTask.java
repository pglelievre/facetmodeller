package facetmodeller.clicktasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;

public final class DefineNodeClickTask extends ControlledClickTask {
    
    public DefineNodeClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_ADD_NODES; }

    @Override
    public String tip() { return "Add new nodes on the current section"; }

    @Override
    public String title() { return ClickTaskUtil.DEFINE_NODE_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        return ( controller.getSelectedCurrentGroup() != null );
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Check that nodes can be added to the current section:
        Section section = controller.getSelectedCurrentSection();
        if (!section.canAddNodesOnSection()) {
            Dialogs.error(controller,"Nodes can not be added to the current section.",title());
            return;
        }
        // Create a new node object linked to the current section and current group:
        Group group = controller.getSelectedCurrentGroup();
        Node node = new NodeOnSection(p,section,group); // new node is not added to the section or group yet
        // Add the node:
        AddNodeCommand com = new AddNodeCommand(controller.getModelManager(),node,title()); com.execute(); // adds the node to the section and group
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
    }
    
}