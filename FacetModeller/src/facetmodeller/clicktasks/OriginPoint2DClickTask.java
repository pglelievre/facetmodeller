package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;
import geometry.MyPoint2D;

public final class OriginPoint2DClickTask extends ControlledClickTask {
    
    public OriginPoint2DClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_ORIGIN_2D; }

    @Override
    public String text() { return ClickTaskUtil.ORIGIN_POINT_2D_TEXT; }

    @Override
    public String tip() { return ClickTaskUtil.ORIGIN_POINT_2D_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.ORIGIN_POINT_2D_TITLE; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        return (controller.getSelectedCurrentSection()!=null);
    }

    @Override
    public void mouseClick(MyPoint2D p) {
        // Check for the required information:
        if (!check()) { return; }
        if (p==null) { return; }
        // Set the origin point:
        controller.setOrigin2D(p);
    }
    
}
