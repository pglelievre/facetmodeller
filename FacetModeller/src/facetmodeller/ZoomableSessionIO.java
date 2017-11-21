package facetmodeller;

import fileio.SessionIO;

/** This is simply a combination of the Zoomable and SessionIO interfaces.
 * @author Peter
 */
public interface ZoomableSessionIO extends SessionIO {
    // TODO: delete this interface once Java allows implementing multiple interfaces
    
    public void zoomIn();
    public void zoomOut();
    public void zoomReset();
    
}
