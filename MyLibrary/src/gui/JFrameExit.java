package gui;

import dialogs.Dialogs;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/** A JFrame with exit confirmation and about dialogs.
 * @author Peter Lelievre
 */
@SuppressWarnings("ProtectedField")
public abstract class JFrameExit extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // ------------------ Properties ------------------
    
    private final String name; // the name of the program
    
    // ------------------ Constructor ------------------
    
    public JFrameExit(String title, String n) {
        super(title);
        name = n;
        finish();
    }
    // Methods to avoid warning of "Overridable method call inconstructor":
    private void finish() {
        // Set it to do nothing when closed so that the window listener is used:
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // Add the window listener:
        addWindowListener(new ExitWindowMonitor());
    }
    
    // -------------------- Public dialog methods --------------------
    
    protected abstract String versionString();
    protected abstract String rulesString();
    protected abstract String authorString();
    protected abstract String contactString();
    
    public final String aboutString() {
        String t = name + " version " + versionString() + System.lineSeparator() + System.lineSeparator()
                + rulesString() + System.lineSeparator() + System.lineSeparator()
                + authorString() + System.lineSeparator()
                + contactString() + System.lineSeparator();
        return t;
    }
    
    /** Displays the about dialog. */
    public final void about() {
        String title = "About " + name;
        Dialogs.inform(this,aboutString(),title);
    }

    /** Asks for exit confirmation and exits if requested. */
    public void exit(){
        // Ask for confirmation:
        String title = "Exit " + name;
        int response = Dialogs.exitConfirmation(this,title);
        // Check answer:
        if (response == Dialogs.YES_OPTION) {
            // Run before quitting:
            runBeforeExit();
            // Quit:
            System.out.println("Goodbye.");
            System.exit(0);
        }
    }
    
    protected void runBeforeExit() {};

    // ------------------- Listeners and related methods -------------------

    /** Monitors window closing event and performs a clean exit. */
    @SuppressWarnings("PublicInnerClass")
    private class ExitWindowMonitor extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent event) {
            exit();
        }
    }

}
