package facetmodeller.menutasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.Projector3D;
import facetmodeller.sections.Section;
import facetmodeller.sections.SnapshotSection;

public final class ResetSnapshotSectionMenuTask extends ControlledMenuTask {
    
    public ResetSnapshotSectionMenuTask(FacetModeller con) { super(con); }

    @Override
    public String text() { return "Reset snapshot section view"; }

    @Override
    public String tip() { return "Resets the view for the current snapshot section"; }

    @Override
    public String title() { return "Reset Snapshot Section View"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        // Check that the current section is a snapshot section:
        Section currentSection = controller.getSelectedCurrentSection();
        if (currentSection==null) { return false; }
        return (currentSection instanceof SnapshotSection);
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Get the projection information from the 3D viewer:
        Projector3D proj = controller.getProjector3D().deepCopy();
        // Reset the projector information:
        Section currentSection = controller.getSelectedCurrentSection();
        SnapshotSection snapshotSection = (SnapshotSection) currentSection; // cast
        snapshotSection.setProjector(proj);
        // Reset the zooming on the 2D view:
        controller.zoomReset2D();
        // Repaint:
        controller.redraw();
    }
    
}
