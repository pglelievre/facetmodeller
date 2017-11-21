package facetmodeller;

import dialogs.Dialogs;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

/** Contains static methods for some dialogs that interact with the user.
 * @author Peter
 */
public class InteractionDialogs {
    
    public static int askNumberOfDimensions(String about) {
        String t = about + "\n\n" + "What type of model do you want to create?";
        int response = Dialogs.question(null,t,"FacetModeller","2D","3D","Cancel");
        int ndim;
        switch (response) {
            case Dialogs.YES_OPTION:
                ndim = 2;
                break;
            case Dialogs.NO_OPTION:
                ndim = 3;
                break;
            default:
                ndim = 0; // to indicate the user cancelled
                break;
        }
        return ndim;
    }
    
    public static void imageFileError(Component con) {
        Dialogs.inform(con,"The image file could not be read. Please check the file format.","File Read Error");
    }
    
    public static File imageFileRequest(Component con, File file) {
        JFileChooser chooser = new JFileChooser();
        filters.ImageFilter imageFilter = new filters.ImageFilter();
        chooser.setCurrentDirectory(file.getParentFile());
        chooser.addChoosableFileFilter(imageFilter);
        chooser.setFileFilter(imageFilter);
        //chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle("Please locate file: " + file.getName());
        chooser.setMultiSelectionEnabled(false);
        int response = chooser.showOpenDialog(con);
        if (response != JFileChooser.APPROVE_OPTION) { return null; }
        return chooser.getSelectedFile();
    }
    
}
