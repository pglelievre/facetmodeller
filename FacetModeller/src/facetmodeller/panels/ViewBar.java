package facetmodeller.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

/** A panel with various view buttons.
 * @author Peter
 */
public class ViewBar extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final View3DPanel panel;
    private final JButton viewWest, viewEast, viewNorth, viewSouth, viewDown, viewUp, //viewDefault
                          viewParallel, viewPerspective, rotateSimple, rotateFancy, pan, clearPan,
                          showAxes, centreAxes;

    public ViewBar(View3DPanel p) {
        panel = p;
        // Make the buttons:
        ZoomBarActionListener actionListener = new ZoomBarActionListener();
//        viewDefault = new JButton("D");
//        viewDefault.setToolTipText("Default view (from above and South)");
//        viewDefault.setVerticalTextPosition(AbstractButton.CENTER);
//        viewDefault.setHorizontalTextPosition(AbstractButton.CENTER);
//        viewDefault.addActionListener(actionListener);
        viewWest = new JButton("W");
        viewWest.setToolTipText("View towards West (from East)");
        viewWest.setVerticalTextPosition(AbstractButton.CENTER);
        viewWest.setHorizontalTextPosition(AbstractButton.CENTER);
        viewWest.addActionListener(actionListener);
        viewEast = new JButton("E");
        viewEast.setToolTipText("View towards East (from West)");
        viewEast.setVerticalTextPosition(AbstractButton.CENTER);
        viewEast.setHorizontalTextPosition(AbstractButton.CENTER);
        viewEast.addActionListener(actionListener);
        viewNorth = new JButton("N");
        viewNorth.setToolTipText("View towards North (from South)");
        viewNorth.setVerticalTextPosition(AbstractButton.CENTER);
        viewNorth.setHorizontalTextPosition(AbstractButton.CENTER);
        viewNorth.addActionListener(actionListener);
        viewSouth = new JButton("S");
        viewSouth.setToolTipText("View towards South (from North)");
        viewSouth.setVerticalTextPosition(AbstractButton.CENTER);
        viewSouth.setHorizontalTextPosition(AbstractButton.CENTER);
        viewSouth.addActionListener(actionListener);
        viewDown = new JButton("D");
        viewDown.setToolTipText("View Down (from above)");
        viewDown.setVerticalTextPosition(AbstractButton.CENTER);
        viewDown.setHorizontalTextPosition(AbstractButton.CENTER);
        viewDown.addActionListener(actionListener);
        viewUp = new JButton("U");
        viewUp.setToolTipText("View Up (from below)");
        viewUp.setVerticalTextPosition(AbstractButton.CENTER);
        viewUp.setHorizontalTextPosition(AbstractButton.CENTER);
        viewUp.addActionListener(actionListener);
        viewParallel = new JButton("||");
        viewParallel.setToolTipText("Parallel projection");
        viewParallel.setVerticalTextPosition(AbstractButton.CENTER);
        viewParallel.setHorizontalTextPosition(AbstractButton.CENTER);
        viewParallel.addActionListener(actionListener);
        viewPerspective = new JButton("/\\");
        viewPerspective.setToolTipText("Perspective projection");
        viewPerspective.setVerticalTextPosition(AbstractButton.CENTER);
        viewPerspective.setHorizontalTextPosition(AbstractButton.CENTER);
        viewPerspective.addActionListener(actionListener);
        rotateSimple = new JButton("+");
        rotateSimple.setToolTipText("Dragging rotates on horizontal and vertical axes");
        rotateSimple.setVerticalTextPosition(AbstractButton.CENTER);
        rotateSimple.setHorizontalTextPosition(AbstractButton.CENTER);
        rotateSimple.addActionListener(actionListener);
        rotateFancy = new JButton("o");
        rotateFancy.setToolTipText("Dragging rotates a control sphere");
        rotateFancy.setVerticalTextPosition(AbstractButton.CENTER);
        rotateFancy.setHorizontalTextPosition(AbstractButton.CENTER);
        rotateFancy.addActionListener(actionListener);
        pan = new JButton("<>");
        pan.setToolTipText("Dragging pans");
        pan.setVerticalTextPosition(AbstractButton.CENTER);
        pan.setHorizontalTextPosition(AbstractButton.CENTER);
        pan.addActionListener(actionListener);
        clearPan = new JButton("x");
        clearPan.setToolTipText("Clear pan");
        clearPan.setVerticalTextPosition(AbstractButton.CENTER);
        clearPan.setHorizontalTextPosition(AbstractButton.CENTER);
        clearPan.addActionListener(actionListener);
        showAxes = new JButton("*");
        showAxes.setToolTipText("Toggle axes visibility");
        showAxes.setVerticalTextPosition(AbstractButton.CENTER);
        showAxes.setHorizontalTextPosition(AbstractButton.CENTER);
        showAxes.addActionListener(actionListener);
        centreAxes = new JButton(".");
        centreAxes.setToolTipText("Toggle axes position");
        centreAxes.setVerticalTextPosition(AbstractButton.CENTER);
        centreAxes.setHorizontalTextPosition(AbstractButton.CENTER);
        centreAxes.addActionListener(actionListener);
        // Add the buttons to the JPanel:
        addButtons();
    }
    private void addButtons() {
        setLayout(new GridLayout(2,7));
//        add(viewDefault);
        add(viewWest);
        add(viewNorth);
        add(viewUp);
        add(viewParallel);
        add(rotateSimple);
        add(pan);
        add(showAxes);
        add(viewEast);
        add(viewSouth);
        add(viewDown);
        add(viewPerspective);
        add(rotateFancy);
        add(clearPan);
        add(centreAxes);
        //setBorder(BorderFactory.createEtchedBorder());
    }

    /** Action listener for zoom buttons. */
    private class ZoomBarActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
//            if (src == viewDefault) {
//                panel.viewDefault();
            if (src == viewWest) {
                panel.viewWest();
            } else if (src == viewEast) {
                panel.viewEast();
            } else if (src == viewNorth) {
                panel.viewNorth();
            } else if (src == viewSouth) {
                panel.viewSouth();
            } else if (src == viewDown) {
                panel.viewDown();
            } else if (src == viewUp) {
                panel.viewUp();
            } else if (src == viewParallel) {
                panel.viewParallel();
            } else if (src == viewPerspective) {
                panel.viewPerspective();
            } else if (src == rotateSimple) {
                panel.rotateSimple();
            } else if (src == rotateFancy) {
                panel.rotateFancy();
            } else if (src == pan) {
                panel.pan();
            } else if (src == clearPan) {
                panel.clearPan();
            } else if (src == showAxes) {
                panel.toggleShowAxes();
            } else if (src == centreAxes) {
                panel.toggleCentreAxes();
            } else {
               // do nothing
            }
        }
    }
    
}
