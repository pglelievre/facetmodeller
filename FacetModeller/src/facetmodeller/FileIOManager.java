package facetmodeller;

import dialogs.Dialogs;
import facetmodeller.filters.VTUFilter;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.PLC;
import fileio.FileUtils;
import fileio.PreviousSession;
import fileio.SessionIO;
import filters.EleFilter;
import filters.NodeFilter;
import filters.PolyFilter;
import geometry.Dir3D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.JFileChooser;

/** Manages file reading and writing tasks.
 * @author Peter
 */
public final class FileIOManager extends PreviousSession implements SessionIO {
    
    // ------------------ Properties ------------------
    
    public static final int EXPORT_ALL       = 0;
    public static final int EXPORT_CURRENT   = 1;
    public static final int EXPORT_DISPLAYED = 2;
    
    private final FacetModeller controller;
    private int startingIndex = 1; // output starting index (0 or 1)
    private int precision = 6; // output precision, number of decimal places
    private boolean saved = false; // set to true once a session has been saved or loaded
    
    // ------------------ Constructor ------------------
    
    public FileIOManager(FacetModeller con) {
        super( Paths.get(System.getProperty("user.dir"),"FacetModellerPreviousSessionFile.txt").toFile() );
        controller = con;
    }
    
    // ------------------ Getters ------------------
    
    public int getStartingIndex() { return startingIndex; }
    public int getPrecision() { return precision; }
    public boolean getSaved() { return saved; }
    
    // ------------------ Setters ------------------
    
    public void setSaved(boolean s) { saved = s; }
    
    // -------------------- Public methods --------------------
    
    public void exportOptionsStartingIndex() {
        String title = "Export Starting Index"; // a title for some dialogs
        String message = "Would you like the indices to start from 0 or 1?";
        String def;
        if (startingIndex==0) {
            def = "0";
        } else {
            def = "1";
        }
        int response = Dialogs.question(controller,message,title,"0","1","Cancel",def);
        switch (response) {
            case Dialogs.YES_OPTION -> {
                startingIndex = 0;
            }
            case Dialogs.NO_OPTION -> {
                startingIndex = 1;
            }
            default -> {
                // user cancelled (do nothing)
            }
        }
    }
    
    public void exportOptionsPrecision() {
        String title = "Export Coordinate Precision"; // a title for some dialogs
        String message = "Enter the number of decimal places for exporting coordinates?";
        String input = Dialogs.input(controller,message,title,Integer.toString(precision));
        if (input==null) { return; } // user cancelled
        input = input.trim();
        // Check an appropriate value was entered:
        int p;
        try {
            p = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Dialogs.error(controller,"You must enter a non-negative integer value. Please try again.",title);
            return;
        }
        if (p<0) {
            Dialogs.error(controller,"You must enter a non-negative integer value. Please try again.",title);
            return;
        }
        // Set the value:
        precision = p;
    }
    
    /** Exports to a poly file.
     * @param whatToExport Specifies what to export using one of the EXPORT_* integers defined in this class. */
    public void exportPoly(int whatToExport) {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }

        String title = "Export Poly"; // a title for some dialogs

        // Get or make the required plc and reset the ID's:
        PLC plc;
        controller.resetIDs();
        switch (whatToExport) {
            case EXPORT_CURRENT -> {
                Group g = controller.getSelectedCurrentGroup();
                plc = new PLC();
                plc.addNodes(g.getNodes());
                plc.addFacets(g.getFacets());
                plc.resetIDs();
            }
            case EXPORT_DISPLAYED -> {
                GroupVector gn = controller.getSelectedNodeGroups();
                GroupVector gf = controller.getSelectedFacetGroups();
                plc = new PLC();
                plc.addNodes(gn.getNodes());
                plc.addFacets(gf.getFacets());
                plc.resetIDs();
            }
            default -> plc = controller.getPLC();
        }

        // Check nodes and facets exist:
        if (!plc.hasNodes()) {
            Dialogs.error(controller,"There are no nodes to export.",title);
            return;
        }
        if (!plc.hasFacets()) {
            Dialogs.error(controller,"There are no facets to export.",title);
            return;
        }
        
        // Ask what to write for region attributes:
        boolean byIndex = true;
        if (plc.hasRegions()) {
            String message = "The region attributes can be the region indices or region group IDs. Which would you like to use?";
            int response = Dialogs.question(controller,message,title,"Indices","Group IDs","Cancel","Indices");
            switch (response) {
                case Dialogs.YES_OPTION -> byIndex = true;
                case Dialogs.NO_OPTION -> byIndex = false;
                default -> {
                    // user cancelled
                    return;
                }
            }
        }

        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        PolyFilter filter = new PolyFilter();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File( root + "." + PolyFilter.POLY );
            chooser.setSelectedFile(file);
        }
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Give the file the .poly extension:
        String root = FileUtils.getRoot(file);
        file = new File( root + "." + PolyFilter.POLY );

        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());

        // Check for file overwrite:
        if (file.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }
        
        // Write the poly file:
        Dir3D dir = null;
        final int ndim = controller.numberOfDimensions();
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok = plc.writePoly(file,startingIndex,precision,ndim,dir,byIndex);

        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".poly file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .poly file.",title);
        }

    }

    /** Exports to node and ele files.
     * @param whatToExport Specifies what to export using one of the EXPORT_* integers defined in this class. */
    public void exportPair(int whatToExport) {

        if ( !controller.hasSections() ) { return; }

        String title = "Export .node/.ele Pair"; // a title for some dialogs

        // Get or make the required plc and reset the ID's:
        PLC plc;
        controller.resetIDs();
        switch (whatToExport) {
            case EXPORT_CURRENT -> {
                Group g = controller.getSelectedCurrentGroup();
                plc = new PLC();
                plc.addNodes(g.getNodes());
                plc.addFacets(g.getFacets());
                plc.resetIDs();
            }
            case EXPORT_DISPLAYED -> {
                GroupVector gn = controller.getSelectedNodeGroups();
                GroupVector gf = controller.getSelectedFacetGroups();
                if (gn==null && gf==null) {
                    Dialogs.error(controller,"There is nothing selected to export.",title);
                    return;
                }
                plc = new PLC();
                if (gn!=null) { plc.addNodes(gn.getNodes()); }
                if (gf!=null) { plc.addFacets(gf.getFacets()); }
                plc.resetIDs();
            }
            default -> plc = controller.getPLC();
        }

        // Check nodes and facets exist:
        if (!plc.hasNodes()) {
            Dialogs.error(controller,"There are no nodes to export.",title);
            return;
        }
        if (!plc.hasFacets()) {
            Dialogs.warning(controller,"There are no facets to export: no .ele file will be written.",title);
        }
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File(root);
            chooser.setSelectedFile(file);
        }
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Get the root of the file:
        String root = FileUtils.getRoot(file);

        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());
        
        // Check for file overwrite:
        File file1 = new File( root + "." + NodeFilter.NODE );
        File file2 = new File( root + "." + EleFilter.ELE );
        if ( file1.exists() || file2.exists() ) {
            response = Dialogs.confirm(controller,"Overwrite the existing file(s)?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }
        
        // Write the files:
        Dir3D dir = null;
        final int ndim = controller.numberOfDimensions();
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok;
        ok = plc.writeNodes(file1,startingIndex,precision,ndim,dir);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .node file.",title);
        }
        if (!plc.hasFacets()) { return; } // don't write .ele file if there are no facets
        ok = plc.writeFacets(file2,startingIndex,precision,ndim,true); // write non-standard variable facet type .ele file if required
        if (!ok) {
           Dialogs.error(controller,"Failed to save .ele file.",title);
        }

    }

    public void exportNodes(boolean force3D) {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }
        
        String title;  // a title for some dialogs
        if (force3D) {
            title = "Export 3D Nodes";
        } else {
            title = "Export Nodes";
        }

        // Check nodes exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasNodes()) {
            Dialogs.error(controller,"There are no nodes to export.",title);
            return;
        }
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        NodeFilter filter = new NodeFilter();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File( root + "." + NodeFilter.NODE );
            chooser.setSelectedFile(file);
        }
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Give the file the .node extension:
        String root = FileUtils.getRoot(file);
        file = new File( root + "." + NodeFilter.NODE );

        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());

        // Check for file overwrite:
        if (file.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }

        // Reset the id's:
        controller.resetIDs();

        // Write the node file:
        Dir3D dir = null;
        int ndim = controller.numberOfDimensions();
        if ( ndim==2 && force3D ) { ndim=3; }
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok = model.writeNodes(file,startingIndex,precision,ndim,dir);

        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".node file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .node file.",title);
        }

    }

    public void exportFacets() {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }

        String title = "Export Facets"; // a title for some dialogs

        // Check facets exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasFacets()) {
            Dialogs.error(controller,"There are no facets to export.",title);
            return;
        }
        
        // Check for variable facet types and supply a warning if required:
        int n0 = model.numberOfNodesInFacet(0); // nodes-per-facet (npf) for the first facet
        boolean isvar = false; // set to true if any facets have npf different from the first facet
        for (int i=1 ; i<model.numberOfFacets() ; i++ ) {
            int n = model.numberOfNodesInFacet(i); // npf for the ith facet
            if ( n != n0 ) {
                isvar = true;
                break;
            }
        }
        boolean writevar;
        int ndim = controller.numberOfDimensions();
        if (isvar) {
            String message = "There are a variable number of nodes per facet. "
                    + "You can write a standard .ele file that ignores facets without " + ndim + " nodes "
                    + "or you can write a non-standard variable facet type .ele file.";
            int response = Dialogs.question(controller,message,title,"Standard","Non-Standard","Cancel");
            switch (response) {
                case Dialogs.YES_OPTION -> // standard
                    writevar = false;
                case Dialogs.NO_OPTION -> // non-standard
                    writevar = true;
                default -> {
                    return;
                }
            }
        } else {
            writevar = false; // standard
        }

        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        EleFilter filter = new EleFilter();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File( root + "." + EleFilter.ELE );
            chooser.setSelectedFile(file);
        }
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Give the file the .ele extension:
        String root = FileUtils.getRoot(file);
        file = new File( root + "." + EleFilter.ELE );

        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());

        // Check for file overwrite:
        if (file.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }

        // Reset the id's:
        controller.resetIDs();

        // Write the ele file:
        boolean ok = model.writeFacets(file,startingIndex,precision,ndim,writevar);
        
        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".ele file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .ele file.",title);
        }
    }

    public void exportRegionsNode() {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }
        
        String title = "Export Regions to .node file"; // a title for some dialogs

        // Check regions exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasRegions()) {
            Dialogs.error(controller,"There are no regions to export.",title);
            return;
        }
        
        // Ask what to write for region attributes:
        boolean byIndex;
        String message = "The region attributes can be the region indices or region group IDs. Which would you like to use?";
        int response = Dialogs.question(controller,message,title,"Indices","Group IDs","Cancel","Indices");
        switch (response) {
            case Dialogs.YES_OPTION -> byIndex = true;
            case Dialogs.NO_OPTION -> byIndex = false;
            default -> {
                // user cancelled
                return;
            }
        }
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        NodeFilter filter = new NodeFilter();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle( title + " (suffixes \"_regions\" and \"_controls\" will be added to the file name specified)");
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File( root + "." + NodeFilter.NODE );
            chooser.setSelectedFile(file);
        }
        response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }
        
        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());

        // Count number of regions:
        int nRegion = model.numberOfRegionPoints();
        int nControl = model.numberOfControlPoints();
        
        // There may be two files written:
        String root = FileUtils.getRoot(file);
        File file1 = new File( root + "_regions." + NodeFilter.NODE );
        File file2 = new File( root + "_controls." + NodeFilter.NODE );

        // Check for file overwrite:
        if (( nRegion>0 && file1.exists() )||( nControl>0 && file2.exists() )) {
            response = Dialogs.confirm(controller,"Overwrite the existing file(s)?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }

        // Reset the id's:
        controller.resetIDs();

        // Only write the node files if there are regions to write in each:
        Dir3D dir = null;
        final int ndim = controller.numberOfDimensions();
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok = true;
        if (nRegion>0) {
            ok = model.writeRegionsNode(file1,startingIndex,precision,ndim,dir,false,byIndex);
        }
        if ( ok && nControl>0 ) {
            ok = model.writeRegionsNode(file2,startingIndex,precision,ndim,dir,true,byIndex);
        }

        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".node file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .node file(s).",title);
        }

    }

    public void exportRegionsVTU() {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }
        
        String title = "Export Regions to .vtu file"; // a title for some dialogs

        // Check regions exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasRegions()) {
            Dialogs.error(controller,"There are no regions to export.",title);
            return;
        }
        
        // Ask if the coordinate should have z flipped:
        int response = Dialogs.questionNo(controller,"Do you want to flip the z-axis?",title);
        if (response==Dialogs.CANCEL_OPTION) { return; }
        boolean flipz = (response==Dialogs.YES_OPTION);
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        VTUFilter filter = new VTUFilter();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File( root + "." + VTUFilter.VTU );
            chooser.setSelectedFile(file);
        }
        response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }
        
        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());

        // Count number of regions:
        int nRegion = model.numberOfRegionPoints();
        int nControl = model.numberOfControlPoints();
        
        // There may be two files written:
        String root = FileUtils.getRoot(file);
        File file1 = new File( root + "_regions." + VTUFilter.VTU );
        File file2 = new File( root + "_controls." + VTUFilter.VTU );

        // Check for file overwrite:
        if (( nRegion>0 && file1.exists() )||( nControl>0 && file2.exists() )) {
            response = Dialogs.confirm(controller,"Overwrite the existing file(s)?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }

        // Reset the id's:
        controller.resetIDs();

        // Only write the vtu files if there are regions to write in each:
        Dir3D dir = null;
        final int ndim = controller.numberOfDimensions();
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok = true;
        if (nRegion>0) {
            ok = model.writeRegionsVTU(file1,startingIndex,precision,ndim,dir,false,flipz); // doControl=false
        }
        if ( ok && nControl>0 ) {
            ok = model.writeRegionsVTU(file2,startingIndex,precision,ndim,dir,true,flipz); // doControl=true
        }
        
        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".vtu file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .vtu file(s).",title);
        }

    }

    public void exportVTU() {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }

        String title = "Export VTU"; // a title for some dialogs

        // Check nodes exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasNodes()) {
            Dialogs.error(controller,"There are no nodes in the model.",title);
            return;
        }
        
        // Ask if the coordinate should have z flipped:
        int response = Dialogs.questionNo(controller,"Do you want to flip the z-axis?",title);
        if (response==Dialogs.CANCEL_OPTION) { return; }
        boolean flipz = (response==Dialogs.YES_OPTION);
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        VTUFilter filter = new VTUFilter();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File( root + "." + VTUFilter.VTU );
            chooser.setSelectedFile(file);
        }
        response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Give the file the .vtu extension:
        String root = FileUtils.getRoot(file);
        file = new File( root + "." + VTUFilter.VTU );

        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());

        // Check for file overwrite:
        if (file.exists()) {
            response = Dialogs.confirm(controller,"Overwrite the existing file?",title);
            if (response != Dialogs.OK_OPTION) { return; }
        }

        // Reset the id's:
        controller.resetIDs();

        // Write the vtu file:
        boolean ok = model.writeVTU(file,precision,flipz);

        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".vtu file saved successfully.",title);
        //} else {
        if (!ok) {
           Dialogs.error(controller,"Failed to save .vtu file.",title);
        }

    }

    /** Exports to all possible formats. Does not save the session. */
    public void exportAll() {

        if ( !controller.hasSections() ) { return; }

        String title = "Export All"; // a title for some dialogs
        
        // Check nodes exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasNodes()) {
            int response = Dialogs.continueCancel(controller,"There are no nodes in the model.",title);
            if (response!=Dialogs.OK_OPTION) { return; } // user cancelled
        }
        
        // Ask if the coordinate should have z flipped:
        int response = Dialogs.questionNo(controller,"Do you want to flip the z-axis?",title);
        if (response==Dialogs.CANCEL_OPTION) { return; }
        boolean flipz = (response==Dialogs.YES_OPTION);
        
        // Ask what to write for region attributes:
        boolean byIndex = true;
        if (model.hasRegions()) {
            String message = "The region attributes can be the region indices or region group IDs. Which would you like to use?";
            response = Dialogs.question(controller,message,title,"Indices","Group IDs","Cancel","Indices");
            switch (response) {
                case Dialogs.YES_OPTION -> byIndex = true;
                case Dialogs.NO_OPTION -> byIndex = false;
                default -> {
                    // user cancelled
                    return;
                }
            }
        }
        
        // Ask for the file name for saving:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(getSaveDirectory());
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        File file = getSessionFile();
        if (file!=null) {
            String root = FileUtils.getRoot(file);
            file = new File(root);
            chooser.setSelectedFile(file);
        }
        response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Get the root of the file:
        String root = FileUtils.getRoot(file);

        // Set the save directory to the chosen directory:
        setSaveDirectory(chooser.getCurrentDirectory());
        
        // Check for file overwrite:
        response = Dialogs.confirm(controller,"Overwrite any existing files?",title);
        if (response != Dialogs.OK_OPTION) { return; }

        // Count number of regions:
        int nRegion = model.numberOfRegionPoints();
        int nControl = model.numberOfControlPoints();
        
        // Reset the id's:
        controller.resetIDs();
        
        // Write the files:
        Dir3D dir = null;
        final int ndim = controller.numberOfDimensions();
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok;
        //file = new File( root + "." + SessionFilter.fms );
        //ok = SessionSaver.saveSessionAscii(this,file);
        //if (!ok) {
        //   Dialogs.error(this,"Failed to save .fms file.",title);
        //}
        file = new File( root + "." + PolyFilter.POLY );
        ok = model.writePoly(file,startingIndex,precision,ndim,dir,byIndex);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .poly file.",title);
        }
        file = new File( root + "." + NodeFilter.NODE );
        ok = model.writeNodes(file,startingIndex,precision,ndim,dir);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .node file.",title);
        }
        if (ndim==2) {
            file = new File( root + "_3D." + NodeFilter.NODE );
            ok = model.writeNodes(file,startingIndex,precision,3,null); // ndim=3, dir=null (hardwired in this call)
            if (!ok) {
               Dialogs.error(controller,"Failed to save 3D .node file.",title);
            }
        }
        file = new File( root + "." + EleFilter.ELE );
        ok = model.writeFacets(file,startingIndex,precision,ndim,true); // write non-standard variable facet type .ele file if required
        if (!ok) {
           Dialogs.error(controller,"Failed to save .ele file.",title);
        }
        file = new File( root + "_regions." + NodeFilter.NODE );
        if (nRegion>0) { ok = model.writeRegionsNode(file,startingIndex,precision,ndim,dir,false,byIndex); }
        if (!ok) {
           Dialogs.error(controller,"Failed to save regions .node file.",title);
        }
        file = new File( root + "_controls." + NodeFilter.NODE );
        if (nControl>0) { ok = model.writeRegionsNode(file,startingIndex,precision,ndim,dir,true,byIndex); }
        if (!ok) {
           Dialogs.error(controller,"Failed to save controls .node file.",title);
        }
        file = new File( root + "_regions." + VTUFilter.VTU );
        if (nRegion>0) { ok = model.writeRegionsVTU(file,startingIndex,precision,ndim,dir,false,flipz); }
        if (!ok) {
           Dialogs.error(controller,"Failed to save regions .vtu file.",title);
        }
        file = new File( root + "_controls." + VTUFilter.VTU );
        if (nControl>0) { ok = model.writeRegionsVTU(file,startingIndex,precision,ndim,dir,true,flipz); }
        if (!ok) {
           Dialogs.error(controller,"Failed to save controls .vtu file.",title);
        }
        file = new File( root + "." + VTUFilter.VTU );
        ok = model.writeVTU(file,precision,flipz);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .vtu file.",title);
        }

    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write everything on a single line:
        String textLine = startingIndex + " " + precision;
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Write everything from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading export options line."; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<2) { return "Not enough values on export options line."; }
        try {
            startingIndex = Integer.parseInt(s[0]);
            precision     = Integer.parseInt(s[1]);
        } catch (NumberFormatException e) { return "Parsing export options."; }
        // Return successfully:
        return null;
    }
    
}
