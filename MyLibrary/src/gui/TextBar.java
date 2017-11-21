package gui;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** A panel for writing a line of text.
 * @author Peter Lelievre
 */
public class TextBar extends JPanel {
    private static final long serialVersionUID = 1L;

    // ------------------ Properties ------------------

    private JTextArea textArea;

    // ------------------ Constructor ------------------

    public TextBar() {

        super();

        // Create the text area:
        textArea = new JTextArea();
        textArea.setEditable(false);
        setTextAreaBackground();

        // Set minimum and maximum sizes so that the text will alway be displayed:
        // (I found I had to do this because the border layout manager wasn't working well)
        setSizes();

        // Add the text area to the status bar:
        finishUp();

    }
    private void setTextAreaBackground() {
        textArea.setBackground(this.getBackground());
    }
    private void setSizes() {
        Font f = new Font(null); // should be default font
        int pad = 4;
        Dimension dim = getToolkit().getScreenSize();
        Dimension minSize = new Dimension(0,f.getSize()+pad);
        Dimension maxSize = new Dimension(dim.width,f.getSize()+pad);
        textArea.setMinimumSize(minSize);
        textArea.setMaximumSize(maxSize);
        setMinimumSize(minSize);
        setMaximumSize(maxSize);
    }
    private void finishUp() {
        add(textArea);
    }

    // -------------------- Public Methods --------------------

    /** Writes a string inside the panel.
     * @param s
     */
    public void setText(String s) {
        // Display the text.
        textArea.setText(s);
    }

    @Override
    public void setToolTipText(String s) {
        // Display the text.
        textArea.setToolTipText(s);
    }

}
