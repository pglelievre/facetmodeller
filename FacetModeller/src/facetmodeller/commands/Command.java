package facetmodeller.commands;

/** An executable and undoable command with a name.
 * @author Peter
 */
public abstract class Command {
    
    private String name;
    
    public Command(String n) {
        setName(n);
    }
    
    public final String getName(){ return name; };
    public final void setName(String n){ name = n; };
    
    public abstract void execute();
    
    public abstract void undo();
    
//    public abstract void redo();
    
}
