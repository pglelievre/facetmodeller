package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;
import facetmodeller.sections.Section;
import geometry.MyPoint2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/** Reduces the resolution of an image by removing every second pixel.
 * @author Peter
 */
public final class ReduceSectionImageMenuTask extends ControlledMenuTask {
    
    public ReduceSectionImageMenuTask(FacetModeller con) { super(con); }
    
    @Override
    public String text() { return "Reduce section image resolution"; }

    @Override
    public String tip() { return "Reduces the resolution of a section image and saves a new .jpg file."; }

    @Override
    public String title() { return "Reduce Section Image Resolution"; }

    @Override
    public boolean check() {
        if (!controller.hasSections()) { return false; }
        Section currentSection = controller.getSelectedCurrentSection();
        if ( currentSection == null ) { return false; }
        return currentSection.hasImage();
    }

    @Override
//    @SuppressWarnings("null")
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the current section and image:
        Section section = controller.getSelectedCurrentSection();
        BufferedImage oldImage = section.getImage();
        File oldFile = section.getImageFile();
        
        // Ask user for confirmation:
        int response = Dialogs.continueCancel(controller,"This can not be undone. Are you sure you want to continue?",title());
        if (response!=Dialogs.OK_OPTION) { return; }
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save reduced .jpg image file as?");
        chooser.setMultiSelectionEnabled(false);
        //File dir = controller.getSaveDirectory();
        File dir = oldFile.getParentFile();
        if (dir!=null) {
            chooser.setCurrentDirectory(dir);
        }
        response = chooser.showSaveDialog(controller);
        
        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File newFile = chooser.getSelectedFile();
        if (newFile==null) { return; }
        
        // Check for file overwrite:
        if (newFile.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title());
            if (response != Dialogs.OK_OPTION) { return; }
        }
        
        // Tell user to be patient:
        if (oldFile.length() >= 1000000) { // 1Mb
            Dialogs.inform(controller,"Reducing and saving image file. This may take a while for large images. Please patiently wait for the confirmation dialog.",title());
        }
        
        // Reduce the size of the image:
        int w = oldImage.getWidth();
        int h = oldImage.getHeight();
        int w2 = w / 2;
        int h2 = h / 2;
        BufferedImage newImage = new BufferedImage( w2, h2, oldImage.getType() );
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
        g.drawImage( oldImage, 0, 0, w2, h2, 0, 0, w, h, null );
        g.dispose();
        
        // Calculate the exact rescaling factors:
        double sx = w2 / (double)w;
        double sy = h2 / (double)h;

        // Save the image to disk:
        boolean ok = true;
        try {
            ImageIO.write(newImage, "jpg", newFile);
        } catch (IOException ex) {
            ok = false;
        }
        if (!ok) {
            Dialogs.error(controller,"Failed to reduce the image.",title());
            return;
        }
        
        // Check for calibration:
        MyPoint2D c1=null, c2=null;
        if (section.isCalibrated()) {
            // Get the clicked calibration points:
            c1 = section.getClicked1();
            c2 = section.getClicked2();
            if ( c1==null || c2==null ) {
                Dialogs.error(controller,"Failed to reduce the image.",title());
                return;
            }
        }
        
        // Reset the image file name, calibration points and node coordinates in the section object:
        section.setImageFile(newFile);
        if (section.isCalibrated()) {
            c1.times(sx,sy);
            c2.times(sx,sy);
        }
        
        // Check for nodes:
        NodeVector nodes = section.getNodes();
        if (!nodes.isEmpty()) {
            // Change the node coordinates (image pixels):
            for (int i=0; i<nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node.isOff()) { continue; } // skip off-section nodes
                MyPoint2D p = node.getPoint2D();
                if (p==null) { continue; } // skip this unexpected error
                p.times(sx,sy);
            }
        }
        
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Redraw:
        controller.redraw();
        
        // Display message of success or error:
        Dialogs.inform(controller,"Image reduced successfully.",title());
        
    }
    
}
