package dialogs;

import gui.TextBar;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

/** Dialog for inputting several items, fit onto a window with a grid layout.
 * @author Peter Lelievre.
 */
public class GridInputDialog extends JDialog {
    private static final long serialVersionUID = 1L;

  // -------------------- Properties -------------------

  private final int n; // the number of input strings
  private String[] inputs = null; // the strings the user has input
  private final JTextField[] fields; // the input fields

  // -------------------- Constructor -------------------

  /**
   * @param parent Object used to centre the dialog.
   * @param prompt Instructions to provide.
   * @param title Title for the dialog.
   * @param descriptions List of descriptions that go with the text fields.
   * @param defaults List of default strings to place in the text fields.
   * @param enabled List of booleans to indicate whether or not the text fields are enabled.
   */
  public GridInputDialog(Frame parent, String prompt, String title, String[] descriptions, String[] defaults, boolean[] enabled) {

      // Create the JDialog:
      super(parent,title,true); // the true makes it modal
      
      // Initial steps:
      Container contentPane = startUp(parent);

      // Create a panel to add the text fields to:
      Panel textPanel = new Panel();
      n = descriptions.length;
      textPanel.setLayout(new GridLayout(n,2));
      
      // Create the description and input text objects and add to the textPanel:
      fields = new JTextField[n];
      for (int i=0 ; i<n ; i++) {
          TextBar textBar = new TextBar();
          textBar.setText(descriptions[i]);
          fields[i] = new JTextField();
          if (defaults!=null) { fields[i].setText(defaults[i]); }
          if (enabled!=null) { fields[i].setEnabled(enabled[i]); }
          textPanel.add(textBar);
          textPanel.add(fields[i]);
      }
      
      // Create the buttons and add to a buttonPanel:
      JButton okayButton = new JButton("OK");
      okayButton.addActionListener(new OkayListener());
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new CancelListener());
      Panel buttonPanel = new Panel();
      buttonPanel.setLayout(new FlowLayout());
      buttonPanel.add(okayButton);
      buttonPanel.add(cancelButton);

      // Place the panels into the content pane:
      contentPane.setLayout(new BorderLayout());
      contentPane.add(textPanel,BorderLayout.NORTH);
      contentPane.add(buttonPanel,BorderLayout.SOUTH);

      // Finish up:
      finishUp();
      
  }
  private Container startUp(Frame parent) {
      // Specify location of the dialog window:
      setLocationRelativeTo(parent);
      // Get content pane to stick stuff in:
      return getContentPane();
  }
  private void finishUp() {
      setResizable(false);
      pack();
      //validate();
      setVisible(true);
  }

  // -------------------- Public Methods -------------------

  /** Getter for the strings entered by the user.
   * @return 
   */
  public String[] getInputs() {
      String[] s = new String[n];
      System.arraycopy(inputs, 0, s, 0, n);
      return s;
  }

  // -------------------- Private Methods -------------------

  /** Called from the action handlers to set the array of entered strings. */
  private void fillInputs() {
      inputs = new String[n];
      for (int i=0 ; i<n ; i++) {
          inputs[i] = fields[i].getText();
      }
  }

  // -------------------- ActionListeners for buttons -------------------
  
  private class OkayListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
          // Fill the result (string array):
          fillInputs();
          dispose();
          setVisible(false);
      }
  }
  
  private class CancelListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
          // Set result to null:
          inputs = null;
          dispose();
          setVisible(false);
      }
  }
  
}