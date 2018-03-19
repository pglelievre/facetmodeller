package facetmodeller.clicktasks;

import facetmodeller.FacetModeller;
import facetmodeller.gui.ClickModeManager;
import geometry.MyPoint2D;

public final class IgnoreClickTask extends ControlledClickTask {
    
    public IgnoreClickTask(FacetModeller con) { super(con); }
    
    @Override
    public int mode() { return ClickModeManager.MODE_NULL; }

    @Override
    public String text() { return ClickTaskUtil.IGNORE_TEXT; }

    @Override
    public String tip() { return ClickTaskUtil.IGNORE_TEXT; }

    @Override
    public String title() { return ClickTaskUtil.IGNORE_TITLE; }

    @Override
    public boolean check() { return ( controller.getClickMode() != mode() ); }

    @Override
    public void mouseClick(MyPoint2D p) { } // DO NOTHING!
    
}
