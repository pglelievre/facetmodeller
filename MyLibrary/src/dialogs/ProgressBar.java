package dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/** Modeless simple progress bar.
 * @author Peter Lelievre.
 */
public class ProgressBar extends JDialog {
    private static final long serialVersionUID = 1L;

  // -------------------- Properties -------------------

  private boolean cancelled = false;
  private final int limit;
  private final JLabel textArea;

  // -------------------- Constructor -------------------

  public ProgressBar(Frame parent, String title, int n) {

      // Create the JDialog:
      super(parent,title); // modeless
      
      // Set the limit:
      limit = n;
      
      // Initial steps:
      Container contentPane = startUp(parent);

      // Create the text area:
      textArea = new JLabel("0%",JLabel.CENTER);
      textArea.setSize(200,50);

      // Create the cancel button:
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new CancelListener());
      
      // Set layout options:
      contentPane.setLayout(new BorderLayout());

      // Place the individual items where they need to go:
      contentPane.add(textArea,BorderLayout.NORTH);
      contentPane.add(cancelButton,BorderLayout.SOUTH);

      // Finish up:
      finishUp();
      
  }
  private Container startUp(Frame parent) {
      // Add window listener:
      addWindowListener(closeWindow);
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

  public void setProgress(int i) {
      float f = (i+1) / (float)limit;
      String s = (int)f + "%";
      textArea.setText(s);
  }
  
  public boolean isCancelled() { return cancelled; }
  
  // -------------------- Listeners -------------------
  
  private class CancelListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
          cancelled = true;
          dispose();
          setVisible(false);
      }
  }
  
  private final WindowListener closeWindow = new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
          cancelled = true;
          e.getWindow().dispose();
      }
  };
  
}