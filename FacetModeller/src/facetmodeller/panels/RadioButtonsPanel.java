package facetmodeller.panels;

import facetmodeller.FacetModeller;
import fileio.FileUtils;
import fileio.SessionIO;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/** A panel containing several radio buttons that control what is painted.
 * @author Peter
 */
public final class RadioButtonsPanel extends JPanel implements SessionIO {
    private static final long serialVersionUID = 1L;
    
    public static final int COLOR_NODES_BY_GROUP = 1;
    public static final int COLOR_NODES_BY_SECTION = 2;
    public static final int COLOR_NODES_BY_MARKER = 3;
    public static final int COLOR_FACETS_BY_GROUP = 1;
    public static final int COLOR_FACETS_BY_MARKER = 2;
    
    private final FacetModeller controller;
    private final JRadioButton showImageButton, showImageOutlineButton, showRegionsButton;
    private JRadioButton showSectionOutlinesButton, showAllSectionsButton, showVOIButton, showFacesButton, 
            showNormalsButton, showNormalTailsButton, showNormalHeadsButton,
            nodeColorByGroupButton, nodeColorBySectionButton, nodeColorByMarkerButton,
            facetColorByGroupButton, facetColorByMarkerButton;
    
    public RadioButtonsPanel(FacetModeller con, int ndim) {
        
        // Set the controller:
        controller = con;
        
        // Create new MyActionListener object to assign to various GUI elements.
        MyActionListener actionListener = new MyActionListener();
        
        // Create the show image radio buttion:
        showImageButton = new JRadioButton("");
        showImageButton.setVerticalTextPosition(AbstractButton.CENTER);
        showImageButton.setHorizontalTextPosition(AbstractButton.LEFT);
        showImageButton.setText("Show image");
        showImageButton.setToolTipText("Show image for the current section in the 2D viewer?");
        showImageButton.addActionListener(actionListener);
        showImageButton.setSelected(true);
        
        // Create the show image outline radio buttion:
        showImageOutlineButton = new JRadioButton("");
        showImageOutlineButton.setVerticalTextPosition(AbstractButton.CENTER);
        showImageOutlineButton.setHorizontalTextPosition(AbstractButton.LEFT);
        showImageOutlineButton.setText("outline");
        showImageOutlineButton.setToolTipText("Show outline of image for the current section in the 2D viewer?");
        showImageOutlineButton.addActionListener(actionListener);
        showImageOutlineButton.setSelected(true);
        
        // Create the show regions button:
        showRegionsButton = new JRadioButton("");
        showRegionsButton.setVerticalTextPosition(AbstractButton.CENTER);
        showRegionsButton.setHorizontalTextPosition(AbstractButton.LEFT);
        showRegionsButton.setText("regions");
        showRegionsButton.setToolTipText("Show the regions?");
        showRegionsButton.addActionListener(actionListener);
        showRegionsButton.setSelected(true);
        
        // Create the colour-nodes/facets-by buttons that are common to both the 2D and 3D versions of FacetModeller:
        nodeColorByGroupButton = new JRadioButton("");
        nodeColorByGroupButton.setVerticalTextPosition(AbstractButton.CENTER);
        nodeColorByGroupButton.setHorizontalTextPosition(AbstractButton.LEFT);
        nodeColorByGroupButton.setText("G");
        nodeColorByGroupButton.setToolTipText("Colour the nodes by group in the 2D viewer?");
        nodeColorByGroupButton.addActionListener(actionListener);
        nodeColorByGroupButton.setSelected(true);
        nodeColorByMarkerButton = new JRadioButton("");
        nodeColorByMarkerButton.setVerticalTextPosition(AbstractButton.CENTER);
        nodeColorByMarkerButton.setHorizontalTextPosition(AbstractButton.LEFT);
        nodeColorByMarkerButton.setText("B");
        nodeColorByMarkerButton.setToolTipText("Colour the nodes by boundary marker in the 2D viewer?");
        nodeColorByMarkerButton.addActionListener(actionListener);
        nodeColorByMarkerButton.setSelected(false);
        facetColorByGroupButton = new JRadioButton("");
        facetColorByGroupButton.setVerticalTextPosition(AbstractButton.CENTER);
        facetColorByGroupButton.setHorizontalTextPosition(AbstractButton.LEFT);
        facetColorByGroupButton.setText("G");
        facetColorByGroupButton.setToolTipText("Colour the facets by group in the 2D viewer?");
        facetColorByGroupButton.addActionListener(actionListener);
        facetColorByGroupButton.setSelected(true);
        facetColorByMarkerButton = new JRadioButton("");
        facetColorByMarkerButton.setVerticalTextPosition(AbstractButton.CENTER);
        facetColorByMarkerButton.setHorizontalTextPosition(AbstractButton.LEFT);
        facetColorByMarkerButton.setText("B");
        facetColorByMarkerButton.setToolTipText("Colour the facets by boundary marker in the 2D viewer?");
        facetColorByMarkerButton.addActionListener(actionListener);
        facetColorByMarkerButton.setSelected(false);
        
        // Create the buttons required by the 3D version of FacetModeller:
        if (ndim==3) {
    //        showOtherButton = new JRadioButton("");
    //        showOtherButton.setVerticalTextPosition(AbstractButton.CENTER);
    //        showOtherButton.setHorizontalTextPosition(AbstractButton.LEFT);
    //        showOtherButton.setText("Show overlays");
    //        showOtherButton.setToolTipText("Show overlays for other sections?");
    //        showOtherButton.addActionListener(actionListener);
    //        showOtherButton.setSelected(true);
            showSectionOutlinesButton = new JRadioButton("");
            showSectionOutlinesButton.setVerticalTextPosition(AbstractButton.CENTER);
            showSectionOutlinesButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showSectionOutlinesButton.setText("sections");
            showSectionOutlinesButton.setToolTipText("Show outlines of the selected other sections in the 3D viewer?");
            showSectionOutlinesButton.addActionListener(actionListener);
            showSectionOutlinesButton.setSelected(true);
            showAllSectionsButton = new JRadioButton("");
            showAllSectionsButton.setVerticalTextPosition(AbstractButton.CENTER);
            showAllSectionsButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showAllSectionsButton.setText("all objects");
            showAllSectionsButton.setToolTipText("Show all sections in the 3D viewer or only those for the selected sections?");
            showAllSectionsButton.addActionListener(actionListener);
            showAllSectionsButton.setSelected(true);
            showVOIButton = new JRadioButton("");
            showVOIButton.setVerticalTextPosition(AbstractButton.CENTER);
            showVOIButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showVOIButton.setText("VOI");
            showVOIButton.setToolTipText("Show the VOI in the 3D viewer?");
            showVOIButton.addActionListener(actionListener);
            showVOIButton.setSelected(true);
            showNormalsButton = new JRadioButton("");
            showNormalsButton.setVerticalTextPosition(AbstractButton.CENTER);
            showNormalsButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showNormalsButton.setText("normals");
            showNormalsButton.setToolTipText("Draw lines representing facet normals in the 3D viewer?");
            showNormalsButton.addActionListener(actionListener);
            showNormalsButton.setSelected(false);
            showNormalTailsButton = new JRadioButton("");
            showNormalTailsButton.setVerticalTextPosition(AbstractButton.CENTER);
            showNormalTailsButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showNormalTailsButton.setText("T");
            showNormalTailsButton.setToolTipText("Draw circles representing facet normal tails in the 3D viewer?");
            showNormalTailsButton.addActionListener(actionListener);
            showNormalTailsButton.setSelected(false);
            showNormalTailsButton.setEnabled(false);
            showNormalHeadsButton = new JRadioButton("");
            showNormalHeadsButton.setVerticalTextPosition(AbstractButton.CENTER);
            showNormalHeadsButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showNormalHeadsButton.setText("H");
            showNormalHeadsButton.setToolTipText("Draw circles representing facet normal heads in the 3D viewer?");
            showNormalHeadsButton.addActionListener(actionListener);
            showNormalHeadsButton.setSelected(false);
            showNormalHeadsButton.setEnabled(false);
            showFacesButton = new JRadioButton("");
            showFacesButton.setVerticalTextPosition(AbstractButton.CENTER);
            showFacesButton.setHorizontalTextPosition(AbstractButton.LEFT);
            showFacesButton.setText("faces");
            showFacesButton.setToolTipText("Draw facet faces and edges or just the edges in the 3D viewer?");
            showFacesButton.addActionListener(actionListener);
            showFacesButton.setSelected(true);
            nodeColorBySectionButton = new JRadioButton("");
            nodeColorBySectionButton.setVerticalTextPosition(AbstractButton.CENTER);
            nodeColorBySectionButton.setHorizontalTextPosition(AbstractButton.LEFT);
            nodeColorBySectionButton.setText("S");
            nodeColorBySectionButton.setToolTipText("Colour the nodes by section in the 2D viewer?");
            nodeColorBySectionButton.addActionListener(actionListener);
            nodeColorBySectionButton.setSelected(false);
        }
        
        // Create the text and panel for how to colour the nodes and facets:
        JPanel nodeColorPanel = new JPanel();
        nodeColorPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        nodeColorPanel.add(new JLabel(" Node colour:"));
        nodeColorPanel.add(nodeColorByGroupButton);
        nodeColorPanel.add(nodeColorByMarkerButton);
        if (ndim==3) {
            nodeColorPanel.add(nodeColorBySectionButton);
        } else {
            nodeColorPanel.add(new JLabel(" "));
        }
        JPanel facetColorPanel = new JPanel();
        facetColorPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        facetColorPanel.add(new JLabel(" Facet colour:"));
        facetColorPanel.add(facetColorByGroupButton);
        facetColorPanel.add(facetColorByMarkerButton);
        facetColorPanel.add(new JLabel(" "));
        
        // Add the radio buttons to this panel:
        JPanel showImageRegionsPanel = new JPanel();
        showImageRegionsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        showImageRegionsPanel.add(showImageButton);
        showImageRegionsPanel.add(showImageOutlineButton);
        showImageRegionsPanel.add(showRegionsButton);
        if (ndim==3) {
            this.setLayout(new GridLayout(6,1));
    //        add(showOtherButton);
            JPanel showIn3DPanel1 = new JPanel();
            JPanel showIn3DPanel2 = new JPanel();
            JPanel showIn3DPanel3 = new JPanel();
            showIn3DPanel1.setLayout(new FlowLayout(FlowLayout.LEADING));
            showIn3DPanel2.setLayout(new FlowLayout(FlowLayout.TRAILING));
            showIn3DPanel3.setLayout(new FlowLayout(FlowLayout.TRAILING));
            showIn3DPanel1.add(new JLabel(" Show in 3D:"));
            showIn3DPanel1.add(showSectionOutlinesButton);
            showIn3DPanel1.add(showVOIButton);
            showIn3DPanel2.add(showAllSectionsButton);
            showIn3DPanel2.add(showFacesButton);
            showIn3DPanel3.add(showNormalsButton);
            showIn3DPanel3.add(showNormalTailsButton);
            showIn3DPanel3.add(showNormalHeadsButton);
            this.add(showImageRegionsPanel);
            this.add(showIn3DPanel1);
            this.add(showIn3DPanel2);
            this.add(showIn3DPanel3);
            this.add(nodeColorPanel);
            this.add(facetColorPanel);
        } else {
            this.setLayout(new GridLayout(3,1));
            this.add(showImageRegionsPanel);
            this.add(nodeColorPanel);
            this.add(facetColorPanel);
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
            if (src==showSectionOutlinesButton) {
                controller.redraw3D();
            } else if (src == showAllSectionsButton) {
                controller.redraw3D();
            } else if (src == showImageButton) {
                controller.redraw2D();
            } else if (src == showImageOutlineButton) {
                controller.redraw2D();
            } else if (src == showVOIButton) {
                controller.redraw3D();
            } else if (src == showFacesButton) {
                controller.redraw3D();
            } else if (src == showNormalsButton) {
                boolean ok = showNormalsButton.isSelected();
                showNormalTailsButton.setEnabled(ok);
                showNormalHeadsButton.setEnabled(ok);
                controller.redraw3D();
            } else if (src == showNormalTailsButton) {
                if (showNormalTailsButton.isSelected()) {
                    showNormalHeadsButton.setSelected(false);
                }
                controller.redraw3D();
            } else if (src == showNormalHeadsButton) {
                if (showNormalHeadsButton.isSelected()) {
                    showNormalTailsButton.setSelected(false);
                }
                controller.redraw3D();
            } else if ( src==nodeColorByGroupButton ) {
                if (nodeColorByGroupButton.isSelected()) {
                    if (nodeColorBySectionButton!=null) { nodeColorBySectionButton.setSelected(false); } // this button is not created when building a 2D model
                    nodeColorByMarkerButton.setSelected(false);
                } else {
                    nodeColorByGroupButton.setSelected(true); // this means the button can't be unselected
                }
                controller.redraw();
            } else if ( src==nodeColorBySectionButton ) {
                if (nodeColorBySectionButton.isSelected()) {
                    nodeColorByGroupButton.setSelected(false);
                    nodeColorByMarkerButton.setSelected(false);
                } else {
                    nodeColorBySectionButton.setSelected(true); // this means the button can't be unselected
                }
                controller.redraw();
            } else if ( src==nodeColorByMarkerButton ) {
                if (nodeColorByMarkerButton.isSelected()) {
                    nodeColorByGroupButton.setSelected(false);
                    if (nodeColorBySectionButton!=null) { nodeColorBySectionButton.setSelected(false); } // this button is not created when building a 2D model
                } else {
                    nodeColorByMarkerButton.setSelected(true); // this means the button can't be unselected
                }
                controller.redraw();
            } else if ( src==facetColorByGroupButton ) {
                if (facetColorByGroupButton.isSelected()) {
                    facetColorByMarkerButton.setSelected(false);
                } else {
                    facetColorByGroupButton.setSelected(true); // this means the button can't be unselected
                }
                controller.redraw();
            } else if ( src==facetColorByMarkerButton ) {
                if (facetColorByMarkerButton.isSelected()) {
                    facetColorByGroupButton.setSelected(false);
                } else {
                    facetColorByMarkerButton.setSelected(true); // this means the button can't be unselected
                }
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
    public boolean getShowImageOutline() {
        return showImageOutlineButton.isSelected();
    }
//    public boolean getShowOther() {
//        if (showOtherButton==null) {
//            return false;
//        } else {
//            return showOtherButton.isSelected();
//        }
//    }
    public boolean getShowSectionOutlines() {
        if (showSectionOutlinesButton==null) {
            return false;
        } else {
            return showSectionOutlinesButton.isSelected();
        }
    }
    public boolean getShowAllSections() {
        if (showAllSectionsButton==null) {
            return false;
        } else {
            return showAllSectionsButton.isSelected();
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
    public boolean getShowNormals() {
        if (showNormalsButton==null) {
            return false;
        } else {
            return showNormalsButton.isSelected();
        }
    }
    public boolean getShowNormalTails() {
        if (getShowNormals()==false) { return false; }
        if (showNormalTailsButton==null) {
            return false;
        } else {
            return showNormalTailsButton.isSelected();
        }
    }
    public boolean getShowNormalHeads() {
        if (getShowNormals()==false) { return false; }
        if (showNormalHeadsButton==null) {
            return false;
        } else {
            return showNormalHeadsButton.isSelected();
        }
    }
    public boolean getShowRegions() {
        if (showRegionsButton==null) {
            return false;
        } else {
            return showRegionsButton.isSelected();
        }
    }
    public int getNodeColorBy() {
        if (nodeColorByGroupButton.isSelected()) { return COLOR_NODES_BY_GROUP; }
        if (nodeColorBySectionButton!=null) { // this button is not created when building a 2D model
            if (nodeColorBySectionButton.isSelected()) { return COLOR_NODES_BY_SECTION; }
        }
        if (nodeColorByMarkerButton.isSelected()) { return COLOR_NODES_BY_MARKER; }
        return COLOR_NODES_BY_GROUP;
    }
    public int getFacetColorBy() {
        if (facetColorByGroupButton.isSelected()) { return COLOR_FACETS_BY_GROUP; }
        if (facetColorByMarkerButton.isSelected()) { return COLOR_FACETS_BY_MARKER; }
        return COLOR_FACETS_BY_GROUP;
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write booleans all on a single line:
        String textLine;
        if ( showSectionOutlinesButton != null ) { // then it's a 3D model
            textLine =
                    showImageButton.isSelected() + " " + 
                    showSectionOutlinesButton.isSelected() + " " + 
                    showAllSectionsButton.isSelected() + " " + 
                    showVOIButton.isSelected() + " " + 
                    showFacesButton.isSelected() + " " + 
                    showRegionsButton.isSelected() + " " + 
                    nodeColorBySectionButton.isSelected() + " " + 
                    showNormalsButton.isSelected() + " " + //  8th boolean is a later addition, hence the if statement below in readSessionInformat
                    showNormalTailsButton.isSelected() + " " + //  9th boolean is a later addition, hence the if statement below in readSessionInformat
                    showNormalHeadsButton.isSelected() + " " + // 10th boolean is a later addition, hence the if statement below in readSessionInformat
                    nodeColorByGroupButton.isSelected() + " " + // later addition, hence separated from related information in 7th line
                    nodeColorByMarkerButton.isSelected() + " " + // (later addition)
                    facetColorByGroupButton.isSelected() + " " + // (later addition)
                    facetColorByMarkerButton.isSelected() + " " + // (later addition)
                    showImageOutlineButton.isSelected() + " "; // (later addition)
        } else { // 2D model
            textLine =
                    showImageButton.isSelected() + " " +
                    false + " " +
                    false + " " +
                    false + " " +
                    false + " " +
                    showRegionsButton.isSelected() + " " +
                    false + " " +
                    false + " " +
                    false + " " +
                    false + " " +
                    nodeColorByGroupButton.isSelected() + " " +
                    nodeColorByMarkerButton.isSelected() + " " +
                    facetColorByGroupButton.isSelected() + " " +
                    facetColorByMarkerButton.isSelected() + " " +
                    showImageOutlineButton.isSelected();
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
            if (showSectionOutlinesButton!=null) { // if statements here and below are important to distinguish between 2D and 3D models
                is = Boolean.parseBoolean(s[1]); showSectionOutlinesButton.setSelected(is);
            }
            if (showAllSectionsButton!=null) {
                is = Boolean.parseBoolean(s[2]); showAllSectionsButton.setSelected(is);
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
            if (nodeColorBySectionButton!=null) {
                is = Boolean.parseBoolean(s[6]); nodeColorBySectionButton.setSelected(is);
            }
            boolean ok = false;
            if (showNormalsButton!=null) {
                if (s.length>7) {
                    is = Boolean.parseBoolean(s[7]); showNormalsButton.setSelected(is);
                } else {
                    showNormalsButton.setSelected(false);
                }
                ok = showNormalsButton.isSelected();
            }
            if (showNormalTailsButton!=null) {
                if (s.length>8) {
                    is = Boolean.parseBoolean(s[8]); showNormalTailsButton.setSelected(is);
                } else {
                    showNormalTailsButton.setSelected(false);
                }
                showNormalTailsButton.setEnabled(ok);
            }
            if (showNormalHeadsButton!=null) {
                if (s.length>9) {
                    is = Boolean.parseBoolean(s[9]); showNormalHeadsButton.setSelected(is);
                } else {
                    showNormalHeadsButton.setSelected(false);
                }
                showNormalHeadsButton.setEnabled(ok);
            }
            if (nodeColorByGroupButton!=null) {
                if (s.length>10) {
                    is = Boolean.parseBoolean(s[10]); nodeColorByGroupButton.setSelected(is);
                } else {
                    nodeColorByGroupButton.setSelected(false);
                }
                nodeColorByGroupButton.setEnabled(ok);
            }
            if (nodeColorByMarkerButton!=null) {
                if (s.length>11) {
                    is = Boolean.parseBoolean(s[11]); nodeColorByMarkerButton.setSelected(is);
                } else {
                    nodeColorByMarkerButton.setSelected(false);
                }
                nodeColorByMarkerButton.setEnabled(ok);
            }
            if (facetColorByGroupButton!=null) {
                if (s.length>12) {
                    is = Boolean.parseBoolean(s[12]); facetColorByGroupButton.setSelected(is);
                } else {
                    facetColorByGroupButton.setSelected(false);
                }
                facetColorByGroupButton.setEnabled(ok);
            }
            if (facetColorByMarkerButton!=null) {
                if (s.length>13) {
                    is = Boolean.parseBoolean(s[13]); facetColorByMarkerButton.setSelected(is);
                } else {
                    facetColorByMarkerButton.setSelected(false);
                }
                facetColorByMarkerButton.setEnabled(ok);
            }
            if (showImageOutlineButton!=null) {
                if (s.length>14) {
                    is = Boolean.parseBoolean(s[13]); showImageOutlineButton.setSelected(is);
                } else {
                    showImageOutlineButton.setSelected(true);
                }
            }
        } catch (NumberFormatException e) { return "Parsing radio button booleans."; }
        // Return successfully:
        return null;
    }
    
}
