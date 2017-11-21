package facetmodeller.gui;

import fileio.FileUtils;
import fileio.SessionIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/** An object to help management of shifting or panning.
 * @author Peter
 */
public class Shifter implements SessionIO {
    
    private int x = 0;
    private int y = 0;
    private int scale = 1;
    
    public Shifter(int s) {
        setScale(s);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getScale() { return scale; }
    
    public int getScaledX() { return x*scale; }
    public int getScaledY() { return y*scale; }
    
    public final void setScale(int s) { scale = s; }
    
    public void clear() { x=0; y=0; }
    
    public void add(int dx, int dy) {
        x += Math.signum(dx);
        y += Math.signum(dy);
    }
    
    // -------------------- SectionIO Methods --------------------
    
    @Override
    public boolean writeSessionInformation(BufferedWriter writer) {
        // Write x, y and scale on a single line:
        String textLine = Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(scale) + "\n";
        return FileUtils.writeLine(writer,textLine);
    }
    
    @Override
    public String readSessionInformation(BufferedReader reader, boolean merge) {
        // Read x, y and scale from a single line:
        String textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Reading shift information line"; }
        textLine = textLine.trim();
        String[] s = textLine.split("[ ]+");
        if (s.length<3) { return "Not enough values on shift information line"; }
        try {
            x     = Integer.parseInt(s[0]);
            y     = Integer.parseInt(s[1]);
            scale = Integer.parseInt(s[2]);
        } catch (NumberFormatException e) { return "Parsing shift information line"; }
        // Return successfully:
        return null;
    }
    
}
