package dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/** Dialog for selecting one or several member(s) of a list.
 * @author Peter Lelievre.
 */
public class ListDialog extends JDialog {
    private static final long serialVersionUID = 1L;

  // -------------------- Properties -------------------

  private int[] selection = null; // the items the user has chosen

  /** The name chooser. */
  private final JList<String> nameChooser;

  // -------------------- Constructor -------------------

  /**
   * @param parent Object used to centre the dialog.
   * @param prompt Instructions to provide.
   * @param title Title for the dialog.
   * @param nameList List of options that can be selected.
   * @param multiselect Set to true to allow multiselection.
   * @param i0 Index of the initial selection.
   */
  public ListDialog(Frame parent, String prompt, String title, String[] nameList, boolean multiselect, int[] i0) {

      // Create the JDialog:
      super(parent,title,true); // the true makes it modal
      
      // Initial steps:
      Container contentPane = startUp(parent);

      // Create panels to stick into the content pane:
      Panel topPanel = new Panel();
      Panel buttonPanel = new Panel();

      // Create the prompt area:
      JLabel promptArea = new JLabel(prompt,JLabel.CENTER);
      promptArea.setSize(200, 50);

      // Create the name chooser:
      nameChooser = new JList<>(nameList);
      
      // Set the initial selection:
      if (multiselect) {
          nameChooser.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          if (i0!=null) {
              nameChooser.setSelectedIndices(i0);
          }
      } else {
          nameChooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          if (i0!=null) {
              nameChooser.setSelectedIndex(i0[0]);
          }
      }

      // Create a scrollable pane:
      JScrollPane scroller = new JScrollPane(nameChooser);

      // Create the buttons:
      JButton okayButton = new JButton("OK");
      okayButton.addActionListener(new OkayListener());
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new CancelListener());
      
      // Set layout options:
      topPanel.setLayout(new BorderLayout());
      buttonPanel.setLayout(new FlowLayout());
      contentPane.setLayout(new BorderLayout());

      // Place the individual items where they need to go:
      topPanel.add(promptArea,BorderLayout.NORTH);
      topPanel.add(scroller,BorderLayout.SOUTH);
      buttonPanel.add(cancelButton);
      buttonPanel.add(okayButton);
      contentPane.add(topPanel,BorderLayout.NORTH);
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

  /** Getter for the first (or single) option selected by the user.
   * @return The index of the first (or single) selected option or -1 if there is no such selection.
   */
  public int getSelectedIndex() {
      if (selection==null) {
          return -1;
      } else {
          return selection[0];
      }
  }

  /** Getter for the option(s) selected by the user.
   * @return The indices of the selected options.
   */
  public int[] getSelectedIndices() {
      int[] temp = selection; // (my way of getting around a compiler warning)
      return temp;
  }

  // -------------------- Private Methods -------------------

  /** Called from the action handlers to set the array of selected items. */
  private void fillSelection() {
      selection = nameChooser.getSelectedIndices();
  }

  // -------------------- ActionListeners for buttons -------------------
  
  private class OkayListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
          fillSelection();
          dispose();
          setVisible(false);
      }
  }
  
  private class CancelListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
          // Set result to null:
          selection = null;
          dispose();
          setVisible(false);
      }
  }
  
}