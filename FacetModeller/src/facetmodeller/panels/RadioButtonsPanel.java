package facetmodeller.panels;

import facetmodeller.FacetModeller;
import fileio.FileUtils;
import fileio.SessionIO;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/** A panel containing several radio buttons that control what is painted.
 * @author Peter
 */
public final class RadioButtonsPanel extends JPanel implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    private FacetModeller controller;
    private JRadioButton showImageButton, showOutlinesButton, showAllButton, showVOIButton, showFacesButton, showRegionsButton, nodeColorButton;
    
    public RadioButtonsPanel(FacetModeller con, int ndim) {
        
        // Set the controller:
        controller = con;
        
        // Create the show image radio buttion:
        MyActionListener actionListener = new MyActionListener();
        showImageButton = new JRadioButton("");
        showImageButton.setVerticalTextPosition(AbstractButton.CENTER);
        showImageButton.setHorizontalTextPosition(AbstractButton.LEFT);
        showImageButton.setText("Show image in 2D");
        showImageButton.setToolTipText("Show image for the current section in the 2D viewer?");
        showImageButton.addActionListener(actionListener);
        showImageButton.setSelected(true);
        
        // Create the buttons required by the 3D window:
        if (ndim==3) {
    //        showOtherButton = new JRadioButton("");
    //        showOtherButton.setVerticalTextPosition(AbstractButton.CENTER);
    //        showOtherButton.setHorizontalTextPosition(AbstractButton.LEFT);
    //        showOtherButton.setText("Show overlays");
    //        showOtherButton.setToolTipText("Show overlays for other sections?");
    //        showOtherButton.addActionListener(actionListener);
    //        showOtherButton.setSelected(true);
            showOutlinesButton = new JRadioButton("");
            showOutlinesButton.setVerticalTextPosition(AbstractButton.CENTER);
            showOutlinesButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showOutlinesButton.setText("Show sections in 3D");
            showOutlinesButton.setToolTipText("Show outlines of the selected other sections in the 3D viewer?");
            showOutlinesButton.addActionListener(actionListener);
            showOutlinesButton.setSelected(true);
            showAllButton = new JRadioButton("");
            showAllButton.setVerticalTextPosition(AbstractButton.CENTER);
            showAllButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showAllButton.setText("Show all objects in 3D");
            showAllButton.setToolTipText("Show all sections in the 3D viewer or only those for the selected sections?");
            showAllButton.addActionListener(actionListener);
            showAllButton.setSelected(true);
            showVOIButton = new JRadioButton("");
            showVOIButton.setVerticalTextPosition(AbstractButton.CENTER);
            showVOIButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showVOIButton.setText("Show VOI");
            showVOIButton.setToolTipText("Show the VOI in the 3D viewer?");
            showVOIButton.addActionListener(actionListener);
            showVOIButton.setSelected(true);
            showFacesButton = new JRadioButton("");
            showFacesButton.setVerticalTextPosition(AbstractButton.CENTER);
            showFacesButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showFacesButton.setText("Show faces in 3D");
            showFacesButton.setToolTipText("Draw facet faces and edges or just the edges in the 3D viewer?");
            showFacesButton.addActionListener(actionListener);
            showFacesButton.setSelected(true);
            showRegionsButton = new JRadioButton("");
            showRegionsButton.setVerticalTextPosition(AbstractButton.CENTER);
            showRegionsButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showRegionsButton.setText("Show regions");
            showRegionsButton.setToolTipText("Show the regions?");
            showRegionsButton.addActionListener(actionListener);
            showRegionsButton.setSelected(true);
            nodeColorButton = new JRadioButton("");
            nodeColorButton.setVerticalTextPosition(AbstractButton.CENTER);
            nodeColorButton.setHorizontalTextPosition(AbstractButton.LEFT);
            nodeColorButton.setText("Colour nodes by section");
            nodeColorButton.setToolTipText("Colour the nodes by section instead of group in the 2D viewer?");
            nodeColorButton.addActionListener(actionListener);
            nodeColorButton.setSelected(false);
        }
        
        // Add the radio buttons to this panel:
        if (ndim==3) {
            setLayout(new GridLayout(7,1));
            add(showImageButton);
    //        add(showOtherButton);
            add(showOutlinesButton);
            add(showAllButton);
            add(showVOIButton);
            add(showFacesButton);
            add(showRegionsButton);
            add(nodeColorButton);
        } else {
            add(showImageButton);
        }
        
    }

    /** Action listener for the buttons. */
    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
//            if (src == showOtherButton) {
//                drawCurrentSection();
            if (src==showOutlinesButton) {
                controller.redraw3D();
            } else if (src == showAllButton) {
                controller.redraw3D();
            } else if (src == showImageButton) {
                controller.redraw2D();
            } else if (src == showVOIButton) {
                controller.redraw3D();
            } else if (src == showFacesButton) {
                controller.redraw3D();
            } else if (src == nodeColorButton) {
                controller.redraw();
            } else if (src == showRegionsButton) {
                //if (showView3DPanel) { view3DPanel.repaint(); }
                controller.redraw();
            } else {
               // do nothing
            }
        }
    }
    
    public boolean getShowImage() {
        return showImageButton.isSelected();
    }
//    public boolean getShowOther() {
//        if (showOtherButton==null) {
//            return false;
//        } else {
//            return showOtherButton.isSelected();
//        }
//    }
    public boolean getShowOutlines() {
        if (showOutlinesButton==null) {
            return false;
        } else {
            return showOutlinesButton.isSelected();
        }
    }
    public boolean getShowAll() {
        if (showAllButton==null) {
            return false;
        } else {
            return showAllButton.isSelected();
        }
    }
    public boolean getShowVOI() {
        if (showVOIButton==null) {
            return false;
        } else {
            return showVOIButton.isSelected();
        }
    }
    public boolean getShowFaces() {
        if (showFacesButton==null) {
            return false;
        } else {
            return showFacesButton.isSelected();
        }
    }
    public boolean getShowRegions() {
        if (showRegionsButton==null) {
            return false;
        } else {
            return showRegionsButton.isSelected();
        }
    }
    public boolean getNodeColorBySection() {
        if (nodeColorButton==null) {
            return false;
        } else {
            return nodeColorButton.isSelected();
        }
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write booleans all on a single line:
        String textLine;
        if ( showOutlinesButton != null ) {
            textLine =
                    showImageButton.isSelected() + " " + 
                    showOutlinesButton.isSelected() + " " + 
                    showAllButton.isSelected() + " " + 
                    showVOIButton.isSelected() + " " + 
                    showFacesButton.isSelected() + " " + 
                    showRegionsButton.isSelected() + " " + 
                    nodeColorButton.isSelected();
        } else {
            textLine =
                    showImageButton.isSelected() + " " +
                    true + " " + true + " " + true + " " +
                    true + " " + true + " " + false;
        }
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Write booleans all from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Radio button booleans line."; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<7) { return "Not enough radio button booleans."; }
        try {
            boolean is;
            // Read the booleans and set the radio button selections:
            is = Boolean.parseBoolean(s[0]); showImageButton.setSelected(is);
            if (showOutlinesButton!=null) {
                is = Boolean.parseBoolean(s[1]); showOutlinesButton.setSelected(is);
            }
            if (showAllButton!=null) {
                is = Boolean.parseBoolean(s[2]); showAllButton.setSelected(is);
            }
            if (showVOIButton!=null) {
                is = Boolean.parseBoolean(s[3]); showVOIButton.setSelected(is);
            }
            if (showFacesButton!=null) {
                is = Boolean.parseBoolean(s[4]); showFacesButton.setSelected(is);
            }
            if (showRegionsButton!=null) {
                is = Boolean.parseBoolean(s[5]); showRegionsButton.setSelected(is);
            }
            if (nodeColorButton!=null) {
                is = Boolean.parseBoolean(s[6]); nodeColorButton.setSelected(is);
            }
        } catch (NumberFormatException e) { return "Parsing radio button booleans."; }
        // Return successfully:
        return null;
    }
    
}
