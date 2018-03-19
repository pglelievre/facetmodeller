package facetmodeller.panels;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.gui.Shifter;
import fileio.FileUtils;
import fileio.SessionIO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/** The panel with the shift buttons and associated radio buttons.
 * @author Peter
 */
public final class ShiftOrPanPanel extends JPanel implements SessionIO { // TODO: implement ShiftablePanel once Java allows implementing multiple interfaces
    private static final long serialVersionUID = 1L;

    // Modes for how the ShiftButtonPanel is used:
    public static final int BUTTON_GRID_MODE_SHIFT = 1;
    public static final int BUTTON_GRID_MODE_PAN2D = 2;
//    public static final int BUTTON_GRID_MODE_PAN3D = 3;
    
    // Default values for the different shift steps (Shifter scales):
    public static final int DEFAULT_SHIFT_STEP = 10; // image pixel units
    public static final int DEFAULT_PAN_STEP = 100; // image pixel units
    
    private final FacetModeller controller;
    private int buttonGridMode = BUTTON_GRID_MODE_SHIFT; // mode for how the ShiftButtonPanel is used
    private final Shifter shift2D = new Shifter(DEFAULT_SHIFT_STEP);
    private final Shifter pan2D = new Shifter(DEFAULT_PAN_STEP);
    private final JRadioButton shift2DButton, pan2DButton;
    private final ShiftButtonPanel shiftButtonPanel;
    
    public ShiftOrPanPanel(FacetModeller con, int ndim) {
        
        // Set the controller and button mode:
        controller = con;
        if (ndim==3) {
            buttonGridMode = BUTTON_GRID_MODE_SHIFT;
        } else {
            buttonGridMode = BUTTON_GRID_MODE_PAN2D;
        }
        
        // Make the radio buttons:
        MyActionListener actionListener = new MyActionListener();
        shift2DButton = new JRadioButton("Shift");
        shift2DButton.setVerticalTextPosition(AbstractButton.CENTER);
        shift2DButton.setHorizontalTextPosition(AbstractButton.LEFT);
        shift2DButton.setText("Shift");
        shift2DButton.setToolTipText("Use buttons below to shift the other section in the 2D viewer");
        shift2DButton.addActionListener(actionListener);
        shift2DButton.setSelected(buttonGridMode==BUTTON_GRID_MODE_SHIFT);
        pan2DButton = new JRadioButton("Pan 2D");
        pan2DButton.setVerticalTextPosition(AbstractButton.CENTER);
        pan2DButton.setHorizontalTextPosition(AbstractButton.LEFT);
        pan2DButton.setText("Pan 2D");
        pan2DButton.setToolTipText("Use buttons below to pan the 2D viewer");
        pan2DButton.addActionListener(actionListener);
        pan2DButton.setSelected(buttonGridMode==BUTTON_GRID_MODE_PAN2D);
        
        // Make the shift button panel:
        shiftButtonPanel = new ShiftButtonPanel(this);
        
        // Add the objects to this panel:
        if (ndim==3) {
            // Add the radio buttons to the same panel:
            JPanel radioButtonPanel = new JPanel();
            radioButtonPanel.setLayout(new GridLayout(1,2));
            radioButtonPanel.add(shift2DButton);
            radioButtonPanel.add(pan2DButton);
            // Add the radioButtonPanel and ShiftButtonPanel to this panel:
            setLayout(new BorderLayout());
            add(radioButtonPanel,BorderLayout.NORTH);
            add(shiftButtonPanel,BorderLayout.CENTER);
        } else {
            // This panel only contains the ShiftButtonPanel:
            add(shiftButtonPanel);
        }
        
    }

    /** Action listener for the buttons. */
    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            if ( src == shift2DButton ) {
                buttonGridMode = BUTTON_GRID_MODE_SHIFT;
                shift2DButton.setSelected(true);
                pan2DButton.setSelected(false);
                resetShiftButtonText();
            } else if ( src == pan2DButton ) {
                buttonGridMode = BUTTON_GRID_MODE_PAN2D;
                shift2DButton.setSelected(false);
                pan2DButton.setSelected(true);
                resetShiftButtonText();
            } else {
               // do nothing
            }
        }
    }

//    @Override
    public void changeShift(int dx, int dy) {
        // Change the shift or pan values:
        switch (buttonGridMode) {
            case BUTTON_GRID_MODE_SHIFT:
                shift2D.add(dx,dy);
                resetShiftButtonText();
                break;
            case BUTTON_GRID_MODE_PAN2D:
                pan2D.add(dx,dy);
                resetShiftButtonText();
                break;
            default:
                return;
        }
        controller.redraw2D();
    }

//    @Override
    public void clearShift() {
        // Clear the shift or pan values:
        switch (buttonGridMode) {
            case BUTTON_GRID_MODE_SHIFT:
                shift2D.clear();
                resetShiftButtonText();
                break;
            case BUTTON_GRID_MODE_PAN2D:
                pan2D.clear();
                resetShiftButtonText();
                break;
            default:
                return;
        }
        controller.redraw2D();
    }
    
    public void resetShiftButtonText() {
        // Change the text on the buttons:
        int nx;
        int ny;
        switch (buttonGridMode) {
            case BUTTON_GRID_MODE_SHIFT:
                nx = shift2D.getX();
                ny = shift2D.getY();
                break;
            case BUTTON_GRID_MODE_PAN2D:
                nx = pan2D.getX();
                ny = pan2D.getY();
                break;
            default:
                // shouldn't happen
                return;
        }
        shiftButtonPanel.setButtonText(nx,ny);
    }
    
    public int getShiftStep2D() { return shift2D.getScale(); }
    public int getPanStep2D() { return pan2D.getScale(); }
    public int getShiftingX() { return shift2D.getScaledX(); }
    public int getShiftingY() { return shift2D.getScaledY(); }
    public int getPanning2DX() { return pan2D.getScaledX(); }
    public int getPanning2DY() { return pan2D.getScaledY(); }
    public void setShiftStep2D(int i) { shift2D.setScale(i); }
    public void setPanStep2D(int i) { pan2D.setScale(i); }
    public void clearShift2D() { shift2D.clear(); }
    public void clearPan2D() { pan2D.clear(); }
    public void addPan2D(int dx, int dy) { pan2D.add(dx,dy); }

    /** Allows the user to change the shift step for overlays. */
    public void selectShiftStep2D() {
        String response = Dialogs.input(controller,"Enter the shift step for overlays (image pixel units):","Shift Step",Integer.toString(getShiftStep2D()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single integer value. Please try again.","Error");
            return;
        }
        int i;
        try {
            i = Integer.parseInt(ss[0].trim());
            if (i<=0) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive integer value. Please try again.","Error");
            return;
        }
        setShiftStep2D(i);
    }

    /** Allows the user to change the 2D pan step for overlays. */
    public void selectPanStep2D() {
        String response = Dialogs.input(controller,"Enter the 2D pan step (image pixel units):","2D Pan Step",Integer.toString(getPanStep2D()));
        if (response == null) { return; }
        response = response.trim();
        String[] ss = response.split("[ ]+");
        if (ss.length!=1) {
            Dialogs.error(controller,"You must enter a single integer value. Please try again.","Error");
            return;
        }
        int i;
        try {
            i = Integer.parseInt(ss[0].trim());
            if (i<=0) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a positive integer value. Please try again.","Error");
            return;
        }
        setPanStep2D(i);
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write the button grid mode:
        String textLine = Integer.toString(buttonGridMode);
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        // Write the shift information:
        if (!shift2D.writeSessionInformation(writer)) { return false; }
        return pan2D.writeSessionInformation(writer);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read the button grid mode:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading button grid mode line."; }
        try {
            buttonGridMode = Integer.parseInt(textLine.trim());
        } catch (NumberFormatException e) { return "Parsing button grid mode."; }
        // Read the shift information:
        String msg;
        msg = shift2D.readSessionInformation(reader,merge); if (msg!=null) { return msg + " (shifting)"; }
        msg = pan2D.readSessionInformation(reader,merge); if (msg!=null) { return msg + " (panning)"; }
        // Set the state of the radio buttons:
        boolean isShift = (buttonGridMode==BUTTON_GRID_MODE_SHIFT);
        shift2DButton.setSelected(isShift);
        pan2DButton.setSelected(!isShift);
        // Reset the text on the shift buttons:
        resetShiftButtonText();
        // Return successfully:
        return null;
    }
    
}
