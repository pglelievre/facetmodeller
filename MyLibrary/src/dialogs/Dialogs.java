package dialogs;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Contains static methods for generating dialogs.
 * Most of them are just wrappers for the JOptionPane methods.
 * @author Peter Lelievre
 */
public class Dialogs {

    /** Value returned when a yes button is clicked. */
    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    /** Value returned when a no button is clicked. */
    public static final int NO_OPTION = JOptionPane.NO_OPTION;
    /** Value returned when an ok button is clicked. */
    public static final int OK_OPTION = JOptionPane.OK_OPTION;
    /** Value returned when a cancel button is clicked. */
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
    /** Value returned when a dialog is closed. */
    public static final int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;

    /** Code error message dialog.
     * The title of the dialog is "Code Error".
     * @param parent Object for centering the dialog on.
     * @param message The message to provide.
     */
    public static void codeError(Component parent, String message) {
        String msg = message + " PLEASE INFORM THE DEVELOPERS!";
        error(parent,msg,"Code Error");
    }

    /** Code warning message dialog.
     * The title of the dialog is "Code Warning".
     * @param parent Object for centering the dialog on.
     * @param message The message to provide.
     */
    public static void codeWarning(Component parent, String message) {
        String msg = message + " PLEASE INFORM THE DEVELOPERS!";
        error(parent,msg,"Code Warning");
    }

    /** Error message dialog.
     * @param parent Object for centering the dialog on.
     * @param message The message to provide.
     * @param title The title of the dialog.
     */
    public static void error(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,message,title,JOptionPane.ERROR_MESSAGE);
    }

    /** Warning message dialog.
     * @param parent Object for centering the dialog on.
     * @param message The message to provide.
     * @param title The title of the dialog.
     */
    public static void warning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,message,title,JOptionPane.WARNING_MESSAGE);
    }

    /** Information message dialog.
     * @param parent Object for centering the dialog on.
     * @param message The message to provide.
     * @param title The title of the dialog.
     */
    public static void inform(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,message,title,JOptionPane.INFORMATION_MESSAGE);
    }
    public static void informScroll(Component parent, String message, String title) {
        JTextArea jta = new JTextArea(message);
        JScrollPane jsp = new JScrollPane(jta){
            private static final long serialVersionUID = 1L; // avoids compiler warning
            @Override
            public Dimension getPreferredSize() {
                Dimension win = getToolkit().getScreenSize();
                return new Dimension((int)(0.5*win.width),(int)(0.5*win.height));
            }
        };
        JOptionPane.showMessageDialog(parent,jsp,title,JOptionPane.INFORMATION_MESSAGE);
    }

    /** Asks for exit confirmation.
     * The message displayed is "Are you sure you want to exit?"
     * @param parent Object for centering the dialog on.
     * @param title The title of the dialog.
     * @return Dialogs.YES_OPTION or Dialogs.NO_OPTION.
     */
    public static int exitConfirmation(Component parent, String title){
        return JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to exit?",title,
                JOptionPane.YES_NO_OPTION);
    }

    /** Question dialog with ok and cancel buttons.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @return Dialogs.OK_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int confirm(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent,message,title,JOptionPane.OK_CANCEL_OPTION);
    }

    /** Question dialog with yes and no buttons.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @return Dialogs.YES_OPTION or Dialogs.NO_OPTION.
     */
    public static int yesno(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent,message,title,JOptionPane.YES_NO_OPTION);
    }

    /** Question dialog with continue and cancel buttons.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @return Dialogs.OK_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int continueCancel(Component parent, String message, String title) {
        String[] options = new String[2];
        options[0] = "Continue";
        options[1] = "Cancel";
        return JOptionPane.showOptionDialog(parent,message,title,JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
    }

    /** Question dialog with yes, no and cancel buttons. Default is whatever the Java default is.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @return Dialogs.YES_OPTION, Dialogs.NO_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int question(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent,message,title,JOptionPane.YES_NO_CANCEL_OPTION);
    }

    /** Question dialog with yes, no and cancel buttons. Yes button is default.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @return Dialogs.YES_OPTION, Dialogs.NO_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int questionYes(Component parent, String message, String title) {
        return question(parent,message,title,"Yes","No","Cancel","Yes");
    }

    /** Question dialog with yes, no and cancel buttons. No button is default.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @return Dialogs.YES_OPTION, Dialogs.NO_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int questionNo(Component parent, String message, String title) {
        return question(parent,message,title,"Yes","No","Cancel","No");
    }

    /** Question dialog with altered text on the yes, no and cancel buttons.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @param yesText The text to place on the yes button.
     * @param noText The text to place on the no button.
     * @param cancelText The text to place on the cancel button.
     * @return Dialogs.YES_OPTION, Dialogs.NO_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int question(Component parent, String message, String title, String yesText, String noText, String cancelText) {
        String[] options = new String[3];
        options[0] = yesText;
        options[1] = noText;
        options[2] = cancelText;
        return JOptionPane.showOptionDialog(parent,message,title,JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
    }

    /** Question dialog with altered text on the yes, no and cancel buttons and initial selection.
     * @param parent Object for centering the dialog on.
     * @param message The question to ask.
     * @param title The title of the dialog.
     * @param yesText The text to place on the yes button.
     * @param noText The text to place on the no button.
     * @param cancelText The text to place on the cancel button.
     * @param init The initial selection (must be equal to yesText, noText or cancelText).
     * @return Dialogs.YES_OPTION, Dialogs.NO_OPTION or Dialogs.CANCEL_OPTION.
     */
    public static int question(Component parent, String message, String title, String yesText, String noText, String cancelText, String init) {
        String[] options = new String[3];
        options[0] = yesText;
        options[1] = noText;
        options[2] = cancelText;
        return JOptionPane.showOptionDialog(parent,message,title,JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,null,options,init);
    }

    /** Single selection dialog.
     * @param parent Object for centering the dialog on.
     * @param message The instructions to provide.
     * @param title The title of the dialog.
     * @param options The options available to select from.
     * @param i0 The index of the initial selection.
     * @return The index of the selected option.
     */
    public static int selection(Component parent, String message, String title, String[] options, int i0) {

        // Display the input dialog:
        String selection = (String) JOptionPane.showInputDialog(parent,message,title,
                JOptionPane.PLAIN_MESSAGE,null,options,options[i0]);

        // Check for user cancelling:
        if (selection==null) { return -1; }

        // Figure out the index of the selection:
        int ind = -1;
        for ( int i=0 ; i<options.length ; i++ ) {
            if ( options[i].equals(selection) ) {
                ind = i;
                break;
            }
        }

        // Return the index:
        return ind;

    }

    /** Input dialog with no initial text in the text entry field.
     * @param parent Object for centering the dialog on.
     * @param message The instructions to provide.
     * @param title The title of the dialog.
     * @return The text input by the user.
     */
    public static String input(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent,message,title,JOptionPane.PLAIN_MESSAGE);
    }

    /** Input dialog with initial text in the text entry field.
     * @param parent Object for centering the dialog on.
     * @param message The instructions to provide.
     * @param title The title of the dialog.
     * @param text The initial text.
     * @return The text input by the user.
     */
    public static String input(Component parent, String message, String title, String text) {
        return (String) JOptionPane.showInputDialog(parent,message,title,JOptionPane.PLAIN_MESSAGE,
                        null,null,text);
    }

}
