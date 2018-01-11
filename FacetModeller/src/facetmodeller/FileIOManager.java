package facetmodeller;

import dialogs.Dialogs;
import facetmodeller.commands.AddFacetCommandVector;
import facetmodeller.commands.AddGroupCommand;
import facetmodeller.commands.AddNodeCommandVector;
import facetmodeller.filters.SessionFilter;
import facetmodeller.filters.VTUFilter;
import facetmodeller.groups.Group;
import facetmodeller.groups.GroupVector;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;
import facetmodeller.plc.PLC;
import facetmodeller.sections.NoImageDepthSection;
import facetmodeller.sections.Section;
import fileio.FileUtils;
import fileio.PreviousSession;
import filters.EleFilter;
import filters.NodeFilter;
import filters.PolyFilter;
import geometry.Dir3D;
import geometry.MyPoint3D;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.JFileChooser;

/** Manages file reading and writing tasks.
 * @author Peter
 */
public final class FileIOManager extends PreviousSession {
    
    // ------------------ Properties ------------------
    
    public static final int EXPORT_ALL       = 0;
    public static final int EXPORT_CURRENT   = 1;
    public static final int EXPORT_DISPLAYED = 2;
    
    private final FacetModeller controller;
    
    // ------------------ Constructor ------------------
    
    public FileIOManager(FacetModeller con) {
        super( Paths.get(System.getProperty("user.dir"),"FacetModellerPreviousSessionFile.txt").toFile() );
        controller = con;
    }
    
    // -------------------- Public methods --------------------
    
    /** Loads a previously saved FacetModeller session.
     * @param prev If true then the previous session is opened without asking user for the file. */
    public void openSession(boolean prev) {

        String title = "Open Session"; // a title for some dialogs
        
        // Ask for the name of the saved session file:
        if ( !prev || getSessionFile()==null ) {
            boolean ok = openSession(controller,title,new SessionFilter());
            if (!ok) { return; }
        }

        // Ask if they want to overwrite or merge into the currently loaded session:
        boolean merge;
        final int ndim = controller.numberOfDimensions();
        if ( ndim==2 || ( controller.plcIsEmpty() && !controller.hasSections() && !controller.hasGroups() ) ) {
            merge = false;
        } else {
            int response = Dialogs.question(controller,"Overwrite or Merge with currently loaded session?", title,"Overwrite","Merge","Cancel","Overwrite");
            switch (response) {
                case Dialogs.YES_OPTION:
                    // "Overwrite"
                    merge = false;
                    break;
                case Dialogs.NO_OPTION:
                    // "Merge"
                    merge = true;
                    break;
                default:
                    return; // user cancelled
            }
        }

        // Load the session file:
        SessionLoader.LoadSessionReturnObject out = SessionLoader.loadSessionAscii(controller, getSessionFile(),merge);

        // Check for error:
        if (out.message!=null) {
            String s = "Failed to open session.";
            if (!out.message.isEmpty()) {
                s = s + " Error message follows:" + System.lineSeparator() + out.message;
            }
            Dialogs.error(controller,s,title);
            return;
        }

        // Don't allow the number of dimensions to change:
        if (ndim!=controller.numberOfDimensions()) { 
            Dialogs.error(controller,"The number of dimensions was not correct.",title);
            return;
        }
        
        // May need to perform some further processing:
        String errmsg=null;
        if (out.version==2) {
            // Loop over each section:
            for (int i=0 ; i<controller.numberOfSections() ; i++ ) {
                // Check for NoImageDepthSection:
                Section section = controller.getSection(i);
                if (section instanceof NoImageDepthSection) {
                    NoImageDepthSection noImageDepthSection = (NoImageDepthSection) section; // cast
                    // Read the node and ele file and do all the required processing:
                    errmsg = loadNodeAndEle(noImageDepthSection);
                    if (errmsg!=null) { break; }
                }
            }
        }
        
        // Load the new section image:
        //resetSectionImage();
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
        // Set the window title bar to include the name of the session:
        controller.resetTitle();

        // Show success dialog:
        if (errmsg==null) {
            if (out.version==controller.versionInt()) {
                Dialogs.inform(controller,"Session loaded successfully.",title);
            } else {
                Dialogs.warning(controller,"Session loaded successfully from earlier version " + out.version + " of FacetModeller.",title);
            }
        } else {
            String s = "Failed to open session completely from earlier" + System.lineSeparator()
                    + "version 2 of FacetModeller. Error message follows:" + System.lineSeparator() + errmsg;
            Dialogs.warning(controller,s,title);
        }
        
    }
    
    public String loadNodeAndEle(NoImageDepthSection section) {
        
        // Get the node and ele files:
        File nodeFile = section.getNodeFile();
        File eleFile = section.getEleFile();
        
        // Check for a node file:
        if (nodeFile==null) { return null; }
        
        // Read the node file:
        NodeVector nodes = new NodeVector();
        NodeVector.ReadNodesReturnObject readNodesReturnObj = nodes.readNodes(nodeFile,-2);
        if (readNodesReturnObj==null) { return "Failed to read node file" + System.lineSeparator() + nodeFile; }
        
        // Get number of dimensions:
        int ndim = controller.numberOfDimensions();

        // Read the ele file if present:
        FacetVector facets = new FacetVector();
        if (eleFile!=null) {
            FacetVector.ReadFacetsReturnObject readFacetsReturnObj = facets.readEle(controller,"",eleFile,nodes,ndim,false);
            String s = readFacetsReturnObj.getErrmsg();
            if (s!=null) { return "Failed to read ele file" + System.lineSeparator() + eleFile + System.lineSeparator() + s; }
            // Delete any unrequired nodes:
            if (readFacetsReturnObj.getDoRem()) {
                nodes.removeUnused();
            }
        }
        
        // Define a new group for the nodes and facets:
        Group group = new Group("TOPOGRAPHY");
        
        // Add section and group membership to the new nodes and optional facets:
        nodes.setSection(section);
        nodes.setGroup(group);
        facets.setGroup(group);
        
        // Calculate the range of the nodes:
        MyPoint3D p1 = nodes.rangeMin();
        MyPoint3D p2 = nodes.rangeMax();
        // Add a little padding:
        p1.times(1.1); // HARDWIRE
        p2.times(1.1); // HARDWIRE
        
        // Calibrate the section:
        double x1 = p1.getX();
        double x2 = p2.getX();
        double y1 = p1.getY();
        double y2 = p2.getY();
        double z1 = p1.getZ();
        double z2 = p2.getZ();
        section.setTyped1( new MyPoint3D(x1,y2,z1) ); // corresponds to top left pixel (0,0)
        section.setTyped2( new MyPoint3D(x2,y1,z2) ); // corresponds to bottom right pixel (height,width)
        
        // Add the new group:
        new AddGroupCommand(controller,group,0).execute();
        
        // Add the new nodes and optional facets to the plc, section, group:
        ModelManager model = controller.getModelManager();
        new AddNodeCommandVector(model,nodes,"").execute();
        new AddFacetCommandVector(model,facets).execute();
        
        // Return successfully:
        return null;
        
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
            case EXPORT_CURRENT:
                Group g = controller.getSelectedCurrentGroup();
                plc = new PLC();
                plc.addNodes(g.getNodes());
                plc.addFacets(g.getFacets());
                plc.resetIDs();
                break;
            case EXPORT_DISPLAYED:
                GroupVector gn = controller.getSelectedNodeGroups();
                GroupVector gf = controller.getSelectedFacetGroups();
                plc = new PLC();
                plc.addNodes(gn.getNodes());
                plc.addFacets(gf.getFacets());
                plc.resetIDs();
                break;
            default:
                plc = controller.getPLC();
                break;
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
        int response;
        if (plc.hasRegions()) {
            String message = "The region attributes can be the region indices or region group IDs. Which would you like to use?";
            response = Dialogs.question(controller,message,title,"Indices","Group IDs","Cancel","Indices");
            switch (response) {
                case Dialogs.YES_OPTION:
                    byIndex = true;
                    break;
                case Dialogs.NO_OPTION:
                    byIndex = false;
                    break;
                default: // user cancelled
                    return;
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
        response = chooser.showSaveDialog(controller);

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
        boolean ok = plc.writePoly(file,ndim,dir,byIndex);

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
            case EXPORT_CURRENT:
                Group g = controller.getSelectedCurrentGroup();
                plc = new PLC();
                plc.addNodes(g.getNodes());
                plc.addFacets(g.getFacets());
                plc.resetIDs();
                break;
            case EXPORT_DISPLAYED:
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
                break;
            default:
                plc = controller.getPLC();
                break;
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
        ok = plc.writeNodes(file1,ndim,dir);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .node file.",title);
        }
        if (!plc.hasFacets()) { return; } // don't write .ele file if there are no facets
        ok = plc.writeFacets(file2,ndim,true); // write non-standard variable facet type .ele file if required
        if (!ok) {
           Dialogs.error(controller,"Failed to save .ele file.",title);
        }

    }

    public void exportNodes() {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }

        String title = "Export Nodes"; // a title for some dialogs

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
        final int ndim = controller.numberOfDimensions();
        if (ndim==2) {
            dir = controller.getSelectedCurrentSection().getDir3D();
        }
        boolean ok = model.writeNodes(file,ndim,dir);

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
                case Dialogs.YES_OPTION:
                    // standard
                    writevar = false;
                    break;
                case Dialogs.NO_OPTION:
                    // non-standard
                    writevar = true;
                    break;
                default:
                    return;
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
        boolean ok = model.writeFacets(file,ndim,writevar);
        
        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".ele file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .ele file.",title);
        }
    }

    public void exportRegions() {

        // The filter doesn't seem to be working, but it is actually doing what it should
        // according to Mac look-and-feel guidelines, which I think are pretty silly here.

        if ( !controller.hasSections() ) { return; }
        
        String title = "Export Regions"; // a title for some dialogs

        // Check regions exist:
        ModelManager model = controller.getModelManager();
        if (!model.hasRegions()) {
            Dialogs.error(controller,"There are no regions to export.",title);
            return;
        }
        
        // Ask what to write for region attributes:
        boolean byIndex;
        int response;
        String message = "The region attributes can be the region indices or region group IDs. Which would you like to use?";
        response = Dialogs.question(controller,message,title,"Indices","Group IDs","Cancel","Indices");
        switch (response) {
            case Dialogs.YES_OPTION:
                byIndex = true;
                break;
            case Dialogs.NO_OPTION:
                byIndex = false;
                break;
            default: // user cancelled
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
        if (nRegion>0) { ok = model.writeRegions(file1,ndim,dir,false,byIndex); }
        if ( ok && nControl>0 ) { ok = model.writeRegions(file2,ndim,dir,true,byIndex); }

        // Display:
        //if (ok) {
        //    Dialogs.inform(this,".node file saved successfully.",title);
        //} else {
        if (!ok) {
            Dialogs.error(controller,"Failed to save .node file(s).",title);
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
        int flipz = Dialogs.question(controller,"Do you want to flip the z-axis?",title);
        if (flipz==Dialogs.CANCEL_OPTION) { return; }
        
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
        int response = chooser.showSaveDialog(controller);

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
        boolean ok;
        if (flipz==Dialogs.YES_OPTION) {
            ok = model.writeVTU(file,true);
        } else {
            ok = model.writeVTU(file,false);
        }

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
        int flipz = Dialogs.question(controller,"Do you want to flip the z-axis?",title);
        if (flipz==Dialogs.CANCEL_OPTION) { return; }
        
        // Ask what to write for region attributes:
        boolean byIndex = true;
        int response;
        if (model.hasRegions()) {
            String message = "The region attributes can be the region indices or region group IDs. Which would you like to use?";
            response = Dialogs.question(controller,message,title,"Indices","Group IDs","Cancel","Indices");
            switch (response) {
                case Dialogs.YES_OPTION:
                    byIndex = true;
                    break;
                case Dialogs.NO_OPTION:
                    byIndex = false;
                    break;
                default: // user cancelled
                    return;
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
        ok = model.writePoly(file,ndim,dir,byIndex);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .poly file.",title);
        }
        file = new File( root + "." + NodeFilter.NODE );
        ok = model.writeNodes(file,ndim,dir);
        if (!ok) {
           Dialogs.error(controller,"Failed to save .node file.",title);
        }
        file = new File( root + "." + EleFilter.ELE );
        ok = model.writeFacets(file,ndim,true); // write non-standard variable facet type .ele file if required
        if (!ok) {
           Dialogs.error(controller,"Failed to save .ele file.",title);
        }
        file = new File( root + "_regions." + NodeFilter.NODE );
        if (nRegion>0) { ok = model.writeRegions(file,ndim,dir,false,byIndex); }
        if (!ok) {
           Dialogs.error(controller,"Failed to save regions .node file.",title);
        }
        file = new File( root + "_controls." + NodeFilter.NODE );
        if (nControl>0) { ok = model.writeRegions(file,ndim,dir,true,byIndex); }
        if (!ok) {
           Dialogs.error(controller,"Failed to save controls .node file.",title);
        }
        file = new File( root + "." + VTUFilter.VTU );
        if (flipz==Dialogs.YES_OPTION) {
            ok = model.writeVTU(file,true);
        } else {
            ok = model.writeVTU(file,false);
        }
        if (!ok) {
           Dialogs.error(controller,"Failed to save .vtu file.",title);
        }

    }
    
}
