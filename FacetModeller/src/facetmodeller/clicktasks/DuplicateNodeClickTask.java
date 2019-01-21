package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.commands.AddNodeCommand;
import facetmodeller.groups.Group;
import facetmodeller.gui.ClickModeManager;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeOffSection;
import facetmodeller.plc.NodeOnSection;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;
import geometry.MyPoint3D;

public final class DuplicateNodeClickTask extends ControlledClickTask {
    
    public DuplicateNodeClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_DUPLICATE_NODES; }

    @Override
    public String text() { return ClickTaskUtil.DUPLICATE_NODE_TEXT; }

    @Override
    public String tip() { return "Make a node at the same location and in the same section as another, in the current group, not connected to any facets"; }

    @Override
    public String title() { return ClickTaskUtil.DUPLICATE_NODE_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        if (!controller.hasGroups()) { return false; }
        if (controller.getSelectedCurrentSection()==null) { return false; }
        if (controller.getSelectedCurrentGroup()==null) { return false; }
        return controller.hasNodes();
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Calculate the closest node to the clicked point:
        if (!controller.calculateClosestNode(p)) { return; }
        Node node = controller.getClosestNode(); // just in case the closestNode object gets nullified by a mouse move (not sure if that is possible but better safe than sorry)
        if (node==null) { return; }
        // Make a new node at the same location and in the same section as the closest node, and in the current group:
        Section section = node.getSection();
        Group group = controller.getSelectedCurrentGroup();
        Node newNode;
        if (node.isOff()) {
            MyPoint3D p3 = node.getPoint3D();
            if (p3==null) { return; }
            p3 = p3.deepCopy();
            newNode = new NodeOffSection(p3,section,group);
        } else {
            MyPoint2D p2 = node.getPoint2D();
            if (p2==null) { return; }
            p2 = p2.deepCopy();
            newNode = new NodeOnSection(p2,section,group);
        }
        AddNodeCommand com = new AddNodeCommand(controller.getModelManager(),newNode,title()); com.execute();
        controller.undoVectorAdd(com);
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.clearClosestNode(); // (or else the old closest node point will be painted)
        controller.redraw();
    }
    
    @Override
    public void mouseMove(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Clear temporary overlays:
        controller.clearClosestTemporaryOverlays();
        // Calculate the closest node to the cursor position:
        controller.calculateClosestNode(p);
        // Redraw:
        controller.redraw();
        // Use the cursor bar to show information for the closest node:
        controller.updateClosestBar(p);
    }
    
}
