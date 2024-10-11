package facetmodeller.menutasks;

import dialogs.Dialogs;
import facetmodeller.FacetModeller;
import facetmodeller.ModelManager;
import facetmodeller.SessionLoader;
import facetmodeller.commands.AddFacetCommandVector;
import facetmodeller.commands.AddGroupCommand;
import facetmodeller.commands.AddNodeCommandVector;
import facetmodeller.groups.Group;
import facetmodeller.plc.FacetVector;
import facetmodeller.plc.NodeVector;
import facetmodeller.sections.NoImageDepthSection;
import facetmodeller.sections.Section;
import geometry.MyPoint3D;
import java.io.File;

/**
 * @author Peter
 */
public final class LoadSessionMenuTask extends ControlledMenuTask {
    
    boolean loadLast;
    
    public LoadSessionMenuTask(FacetModeller con, boolean loadLast) {
        super(con);
        this.loadLast = loadLast;
    }
    
    @Override
    public String text() {
        if (loadLast) {
            return "Load last session";
        } else {
            return "Load session";
        }
    }

    @Override
    public String tip() {
        if (loadLast) {
            return "Load the last loaded FacetModeller session";
        } else {
            return "Load a previously saved FacetModeller session";
        }
    }

    @Override
    public String title() {
        if (loadLast) {
            return "Load Last Session";
        } else {
            return "Load Session";
        }
    }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Ask for the name of the saved session file, if needed:
        File sessionFile = controller.getSessionFile();
        if ( !loadLast || sessionFile==null ) {
            boolean ok = controller.chooseOpenSession(title());
            if (!ok) { return; }
        }

        // Ask if they want to overwrite or merge into the currently loaded session:
        boolean merge;
        final int ndim = controller.numberOfDimensions();
        if ( ndim==2 || ( controller.plcIsEmpty() && !controller.hasSections() && !controller.hasGroups() ) ) {
            merge = false;
        } else {
            int response = Dialogs.question(controller,"Overwrite or Merge with currently loaded session?", title(),"Overwrite","Merge","Cancel","Overwrite");
            switch (response) {
                case Dialogs.YES_OPTION -> // "Overwrite"
                    merge = false;
                case Dialogs.NO_OPTION -> // "Merge"
                    merge = true;
                default -> {
                    return; // user cancelled
                }
            }
        }

        // Tell user to be patient:
        Dialogs.inform(controller,"This may take a while if there are large section images. Please patiently wait for the confirmation dialog.",title());
        
        // Load the session file:
        SessionLoader.LoadSessionReturnObject out = SessionLoader.loadSessionAscii(controller,sessionFile,merge);

        // Check for error:
        if (out.message!=null) {
            String s = "Failed to load session.";
            if (!out.message.isEmpty()) {
                s = s + " Error message follows:" + System.lineSeparator() + out.message;
            }
            Dialogs.error(controller,s,title());
            return;
        }

        // Don't allow the number of dimensions to change:
        if (ndim!=controller.numberOfDimensions()) { 
            Dialogs.error(controller,"The number of dimensions was not correct.",title());
            return;
        }
        
        // May need to perform some further processing:
        String errmsg=null;
        if (out.version==2) {
            // Loop over each section:
            for (int i=0 ; i<controller.numberOfSections() ; i++ ) {
                // Check for NoImageDepthSection:
                Section section = controller.getSection(i);
                if (section instanceof NoImageDepthSection noImageDepthSection) { // cast
                    // Read the node and ele file and do all the required processing:
                    errmsg = loadNodeAndEle(noImageDepthSection);
                    if (errmsg!=null) { break; }
                }
            }
        }
        
        // Show or hide panels as indicated in the session file:
        controller.showOrHidePanels();
        
        // Load the new section image:
        //resetSectionImage();
        // Enable or disable menu items:
        controller.checkItemsEnabled();
        // Repaint:
        controller.redraw();
        
        // Set the window title bar to include the name of the session:
        controller.resetTitle();
        
        // Indicate that a session has been saved:
        controller.setSaved(true);
        
        // Clear the undo information:
        controller.undoVectorClear();
        
        // Enable or disable menu items:
        controller.checkItemsEnabled();

        // Show success dialog:
        if (errmsg==null) {
            if (out.version==controller.versionInt()) {
                Dialogs.inform(controller,"Session loaded successfully.",title());
            } else {
                Dialogs.warning(controller,"Session loaded successfully from earlier version " + out.version + " of FacetModeller.",title());
            }
        } else {
            String s = "Failed to open session completely from earlier" + System.lineSeparator()
                    + "version 2 of FacetModeller. Error message follows:" + System.lineSeparator() + errmsg;
            Dialogs.warning(controller,s,title());
        }
        
    }
    
    private String loadNodeAndEle(NoImageDepthSection section) {
        
        // Get the node and ele files:
        File nodeFile = section.getNodeFile();
        File eleFile = section.getEleFile();
        
        // Check for a node file:
        if (nodeFile==null) { return null; }
        
        // Read the node file:
        NodeVector nodes = new NodeVector();
        NodeVector.ReadNodesReturnObject readNodesReturnObj = nodes.readNodes(nodeFile,-2);
        String errmsg = readNodesReturnObj.getErrmsg();
        if (errmsg!=null) {
            return errmsg + System.lineSeparator() + nodeFile;
        }
        
        // Get number of dimensions:
        int ndim = controller.numberOfDimensions();

        // Read the ele file if present:
        FacetVector facets = new FacetVector();
        if (eleFile!=null) {
            int startInd = readNodesReturnObj.getStartingIndex();
            FacetVector.ReadFacetsReturnObject readFacetsReturnObj = facets.readEle(controller,"",eleFile,startInd,nodes,ndim,false);
            errmsg = readFacetsReturnObj.getErrmsg();
            if (errmsg!=null) {
                return errmsg + System.lineSeparator() + eleFile;
            }
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
    
}
