package facetmodeller.panels;

import facetmodeller.ZoomableSessionIO;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

/** A panel with three zoom buttons.
 * @author Peter
 */
public final class ZoomBar extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // TODO: use the Zoomable interface to define the controller once Java allows implementing multiple interfaces
    private final ZoomableSessionIO controller;
    private final JButton zoomIn, zoomOut, zoomReset;

    public ZoomBar(ZoomableSessionIO con) {
        
        // Set the controller:
        controller = con;
        
        // Create the buttons:
        ZoomBarActionListener actionListener = new ZoomBarActionListener();
        zoomIn = new JButton("+");
        zoomIn.setToolTipText("Zoom in");
        zoomIn.setVerticalTextPosition(AbstractButton.CENTER);
        zoomIn.setHorizontalTextPosition(AbstractButton.CENTER);
        zoomIn.addActionListener(actionListener);
        zoomOut = new JButton("-");
        zoomOut.setToolTipText("Zoom out");
        zoomOut.setVerticalTextPosition(AbstractButton.CENTER);
        zoomOut.setHorizontalTextPosition(AbstractButton.CENTER);
        zoomOut.addActionListener(actionListener);
        zoomReset = new JButton("x");
        zoomReset.setToolTipText("Reset zoom");
        zoomReset.setVerticalTextPosition(AbstractButton.CENTER);
        zoomReset.setHorizontalTextPosition(AbstractButton.CENTER);
        zoomReset.addActionListener(actionListener);
        
        // Add the buttons to this panel:
        setLayout(new GridLayout(1,3));
        add(zoomOut);
        add(zoomReset);
        add(zoomIn);
        //setBorder(BorderFactory.createEtchedBorder());
        
    }

    /** Action listener for zoom buttons. */
    private class ZoomBarActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            if (src == zoomIn) {
                controller.zoomIn();
            } else if (src == zoomOut) {
                controller.zoomOut();
            } else if (src == zoomReset) {
                controller.zoomReset();
            } else {
               // do nothing
            }
        }
    }
    
}
