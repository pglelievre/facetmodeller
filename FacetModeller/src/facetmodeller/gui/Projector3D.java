package facetmodeller.gui;

import fileio.SessionIO;
import fileio.FileUtils;
import geometry.Matrix3D;
import geometry.MyPoint3D;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** Code for 3D projection.
 * @author Peter
 */
public final class Projector3D implements SessionIO {
    
    // ------------------- Properties ------------------
    
    // Properties required for projection:
    private final double cameraDistance=2000.0; // distance from camera to screen in pixels (approximate)
    private final double clipDistance=10.0; // any points this distance from camera or less are clipped
    private MyPoint3D spaceOrigin; // the origin of the model (spatial coordinates)
    private MyPoint3D imageOrigin; // the centre of the image (pixel coordinates)
    private double sceneAndZoomScaling; // spaceToImage scaling associated with scene information and zooming
    private double imageSizeScaling; // spaceToImage scaling associated with image size
    private Matrix3D rotationMatrix, rotationMatrixInv; // rotation matrices for the projection
    private boolean perspective; // perspective (true) or parallel (false) projection
    private double verticalExaggeration=1.0; // this is a temporary option, not used everywhere
    
    // ------------------- Constructors ------------------

    public Projector3D() {} // required by the SessionLoader (should not be used elsewhere)
    
    public Projector3D(boolean persp) {
        viewDefault(); // sets rotationMatrix and rotationMatrixInv
        perspective = persp;
    }
    
    // ------------------- Deep Copy ------------------
    
    public Projector3D deepCopy() {
        // Construct new object:
        Projector3D proj = new Projector3D(perspective);
        // Copy over all properties not set during construction:
        proj.setRotationMatrix( rotationMatrix.deepCopy() );
        proj.setSpaceOrigin(spaceOrigin);
        proj.setImageOrigin(imageOrigin);
        proj.setSceneAndZoomScaling(sceneAndZoomScaling);
        proj.setImageSizeScaling(imageSizeScaling);
        proj.setRotationMatrix(rotationMatrix);
        // Return the new object:
        return proj;
    }
    
    // ------------------- Getters ------------------
    
    public double getSpaceOriginZ() { return spaceOrigin.getZ(); }
    public MyPoint3D getImageOrigin() { return imageOrigin; }
    private double getScaling() { return sceneAndZoomScaling*imageSizeScaling; }
    public Matrix3D getRotationMatrix() { return rotationMatrix; }
    public Matrix3D getRotationMatrixInv() { return rotationMatrixInv; }
    public double getVerticalExaggeration() { return verticalExaggeration; }
    
    // ------------------- Setters ------------------
    
    public void setSpaceOrigin(MyPoint3D p) { spaceOrigin = p; }
    public void setImageOrigin(MyPoint3D p) { imageOrigin = p; }
    public void setSceneAndZoomScaling(double s) { sceneAndZoomScaling = s; }
    public void setImageSizeScaling(double s) { imageSizeScaling = s; }
    public void setRotationMatrix(Matrix3D m) {
        rotationMatrix = m;
        rotationMatrixInv = rotationMatrix.inv();
    }
    public void setVerticalExaggeration(double d) { verticalExaggeration = d; }
    
    // ------------------- Methods to change the view ------------------
    
    private void viewDefault() {
        rotationMatrix = viewDefaultMatrix();
        rotationMatrixInv = rotationMatrix.inv();
    }
    public static Matrix3D viewDefaultMatrix() {
        // Looking from above and South:
        return Matrix3D.rotX(-Math.PI/4);
    }
    
    public void viewDown() {
        // Looking down from above (-z):
        rotationMatrix = Matrix3D.ident();
        rotationMatrixInv = rotationMatrix.inv();
    }
    
    public void viewUp() {
        // Looking up from below (+z):
        rotationMatrix = Matrix3D.rotY(Math.PI);
        rotationMatrixInv = rotationMatrix.inv();
    }
    
    public void viewNorth() {
        // Looking towards North (+y):
        rotationMatrix = Matrix3D.rotX(-Math.PI/2.0);
        rotationMatrixInv = rotationMatrix.inv();
    }
    
    public void viewSouth() {
        // Looking towards South (-y):
        Matrix3D m1 = Matrix3D.rotZ(Math.PI);
        Matrix3D m2 = Matrix3D.rotX(Math.PI/2.0);
        rotationMatrix = Matrix3D.times(m1,m2);
        rotationMatrixInv = rotationMatrix.inv();
        // Let A represent an axis rotation and B represent a body rotation,
        // for which   A = B'
        // We want    (A2*A1)' = A1'*A2' = B1*B2
        // so the order of multiplication is reversed!!!
    }
    
    public void viewEast() {
        // Looking towards East (+x):
        Matrix3D m1 = Matrix3D.rotZ(Math.PI/2.0);
        Matrix3D m2 = Matrix3D.rotY(Math.PI/2.0);
        rotationMatrix = Matrix3D.times(m1,m2);
        rotationMatrixInv = rotationMatrix.inv();
    }
    
    public void viewWest() {
        // Looking towards West (-x):
        Matrix3D m1 = Matrix3D.rotZ(-Math.PI/2.0);
        Matrix3D m2 = Matrix3D.rotY(-Math.PI/2.0);
        rotationMatrix = Matrix3D.times(m1,m2);
        rotationMatrixInv = rotationMatrix.inv();
    }
    
    public void viewParallel() {
        perspective = false;
    }
    
    public void viewPerspective() {
        perspective = true;
    }
    
    // ------------------- Methods for coordinate conversion ------------------
    
    // Convert 3D space coordinate to projected image pixel coordinate:
    public MyPoint3D spaceToImage(MyPoint3D p0) {
        return spaceToImage(p0,getScaling());
    }
    public MyPoint3D spaceToImage(MyPoint3D p0, double scaling) {
        // Deep copy:
        MyPoint3D p = p0.deepCopy();
        // Translate the point relative to the user-defined model origin:
        p.minus(spaceOrigin); // point is now relative to the user-defined model origin
        // Apply vertical exaggeration:
        p.setZ( verticalExaggeration*p.getZ() );
        // Scale as required:
        p.times(scaling);
        // Rotation:
        p.rotate(rotationMatrix);
        // Perspective projection:
        if (perspective) {
            // Calculate distance from camera to the point (assuming origin lies on the screen):
            double z = cameraDistance - p.getZ();
            // If the point is close to or behind the camera then return null:
            if (z<clipDistance) {
                return null;
            }
            // Calculate scaling based on distance to viewer:
            double r = cameraDistance / z; // higher value for objects closer to viewer
            // Scale objects:
            p.timesXY(r);
            // I think timesXY was causing the zbuffer to fail.
            // It doesn't appear to any more. I didn't understand why. This was the fix:
            //p.times(r);
        }
        // Flip the y axis (y axis is +down on the image):
        p.negY();
        // Translate so that the model origin is at the centre of the image:
        p.plus(imageOrigin);
        return p;
    }
    
    public MyPoint3D imageToSpaceParallel(MyPoint3D p0) {
        // Deep copy:
        MyPoint3D p = p0.deepCopy();
        // Perform opposite of steps for spaceToImage and assume parallel projection:
        p.minus(imageOrigin);
        p.negY();
        p.rotate(rotationMatrixInv);
        p.divide(getScaling());
        p.plus(spaceOrigin);
        return p;
    }

    // -------------------- SessionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        
        // Write the camera distance (in case I want to allow it to change later):
        String textLine;
        textLine = cameraDistance + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // Write the space origin:
        if (spaceOrigin==null) {
            textLine = "null\n";
        } else {
            textLine = spaceOrigin.toString() + "\n";
        }
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // Write the image origin:
        if (imageOrigin==null) {
            textLine = "null\n";
        } else {
            textLine = imageOrigin.toString() + "\n";
        }
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // Write the scaling factors:
        textLine = sceneAndZoomScaling + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        textLine = imageSizeScaling + "\n";
        if (!FileUtils.writeLine(writer,textLine)) { return false; }
        
        // Write the rotation matrix:
        if (!rotationMatrix.writeSessionInformation(writer)) { return false; }
        
        // Write the projection:
        textLine = perspective + "\n";
        return FileUtils.writeLine(writer,textLine);
        
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        
        // Skip the camera distance line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Skipping camera distance line."; }
        
        // Read the space origin:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading space origin line."; }
        textLine = textLine.trim();
        String[] ss = textLine.split("[ ]+",4);
        double x,y,z;
        try {
            x = Double.parseDouble(ss[0].trim()); // converts to Double
            y = Double.parseDouble(ss[1].trim()); // converts to Double
            z = Double.parseDouble(ss[2].trim()); // converts to Double
        } catch (NumberFormatException e) { return "Parsing space origin."; }
        spaceOrigin = new MyPoint3D(x,y,z);
        
        // Read the image origin:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading image origin line."; }
        textLine = textLine.trim();
        ss = textLine.split("[ ]+",4);
        try {
            x = Double.parseDouble(ss[0].trim()); // converts to Double
            y = Double.parseDouble(ss[1].trim()); // converts to Double
            z = Double.parseDouble(ss[2].trim()); // converts to Double
        } catch (NumberFormatException e) { return "Parsing image origin."; }
        imageOrigin = new MyPoint3D(x,y,z);
        
        // Read the scaling factors:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading scene and zoom scaling factor line."; }
        textLine = textLine.trim();
        try {
            sceneAndZoomScaling = Double.parseDouble(textLine); // converts to Double
        } catch (NumberFormatException e) { return "Parsing scene and zoom scaling factor."; }
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading image size scaling factor line."; }
        textLine = textLine.trim();
        try {
            imageSizeScaling = Double.parseDouble(textLine); // converts to Double
        } catch (NumberFormatException e) { return "Parsing image size scaling factor."; }
        
        // Read the rotation matrix:
        rotationMatrix = new Matrix3D();
        String msg = rotationMatrix.readSessionInformation(reader,merge); if (msg!=null) { return msg; }
        rotationMatrixInv = rotationMatrix.inv();
        
        // Read the type of projection:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading projection line."; }
        textLine = textLine.trim();
        try {
            perspective = Boolean.parseBoolean(textLine); // converts to Boolean
        } catch (NumberFormatException e) { return "Parsing the type of projection."; }
        
        // Return successfully:
        return null;
        
    }
    
}
