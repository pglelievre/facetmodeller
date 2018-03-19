package facetmodeller.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

/** A panel containing a 3-by-3 grid of shift buttons.
 * @author Peter
 */
public final class ShiftButtonPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // TODO: use the ShiftablePanel interface to define the controller once Java allows implementing multiple interfaces
    private final ShiftOrPanPanel controller; // should contain methods changeShift and clearShift
    private final JButton[][] buttons;

    public ShiftButtonPanel(ShiftOrPanPanel con) {
        
        // Set the controller:
        controller = con;
        
        // Create the shift buttons:
        buttons = new JButton[3][3];
        MyActionListener actionListener = new MyActionListener();
        for (int i=0 ; i<3 ; i++) {
            for (int j=0 ; j<3 ; j++) {
                JButton b = new JButton();
                b.setVerticalTextPosition(AbstractButton.CENTER);
                b.setHorizontalTextPosition(AbstractButton.CENTER);
                b.addActionListener(actionListener);
                //b.setPreferredSize(new Dimension(20,10));
                buttons[i][j] = b;
            }
        }
        buttons[0][1].setText("^"); // -y (up)
        buttons[0][1].setToolTipText("Shift up");
        buttons[2][1].setText("v"); // +y (down)
        buttons[2][1].setToolTipText("Shift down");
        buttons[1][0].setText("<"); // -x (left)
        buttons[1][0].setToolTipText("Shift left");
        buttons[1][2].setText(">"); // +x (right)
        buttons[1][2].setToolTipText("Shift right");
        buttons[1][1].setText("x"); // clear
        buttons[1][1].setToolTipText("Clear shift");
        buttons[0][0].setToolTipText("Shift up and left");
        buttons[0][2].setToolTipText("Shift up and right");
        buttons[2][0].setToolTipText("Shift down and left");
        buttons[2][2].setToolTipText("Shift down and right");
        
        // Add the shift buttons to this panel:
        setLayout(new GridLayout(3,3));
        for (int i=0 ; i<3 ; i++) {
            for (int j=0 ; j<3 ; j++) {
                add(buttons[i][j]);
            }
        }
       
    }

    /** Action listener for the buttons. */
    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            if (src == buttons[0][0]) { // up and left
                controller.changeShift(-1,-1);
            } else if (src == buttons[0][1]) { // up
                controller.changeShift( 0,-1);
            } else if (src == buttons[0][2]) { // up and right
                controller.changeShift( 1,-1);
            } else if (src == buttons[1][0]) { // left
                controller.changeShift(-1, 0);
            } else if (src == buttons[1][1]) { // clear
                controller.clearShift();
            } else if (src == buttons[1][2]) { // right
                controller.changeShift( 1, 0);
            } else if (src == buttons[2][0]) { // down and left
                controller.changeShift(-1, 1);
            } else if (src == buttons[2][1]) { // down
                controller.changeShift( 0, 1);
            } else if (src == buttons[2][2]) { // down and right
                controller.changeShift( 1, 1);
            } else {
               // do nothing
            }
        }
    }
    
    public void setButtonText(int nx, int ny) {
        // Change the text on the buttons:
        if (nx>0) {
            buttons[1][2].setText(Integer.toString(Math.abs(nx))); // +x (right)
        } else if (nx<0) {
            buttons[1][0].setText(Integer.toString(Math.abs(nx))); // -x (left)
        } else {
            buttons[1][0].setText("<"); // -x (left)
            buttons[1][2].setText(">"); // +x (right)
        }
        if (ny>0) {
            buttons[2][1].setText(Integer.toString(Math.abs(ny))); // +y (down)
        } else if (ny<0) {
            buttons[0][1].setText(Integer.toString(Math.abs(ny))); // -y (up)
        } else {
            buttons[0][1].setText("^"); // -y (up)
            buttons[2][1].setText("v"); // +y (down)
        }
    }
    
}
