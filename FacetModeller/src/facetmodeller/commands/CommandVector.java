package facetmodeller.commands;

import java.util.ArrayList;

/** A command that has a name and requires a list of subcommands.
 * @author Peter
 */
public class CommandVector extends Command {

    // -------------------- Properties -------------------
    
    // Favour composition over inheritence!
    private final ArrayList<Command> vector = new ArrayList<>();
    
    public CommandVector(String t) {
        super(t);
    }
    
    public int size() {
        return vector.size();
    }

    public Command get(int i) {
        return vector.get(i);
    }

    public void add(Command c) {
        vector.add(c);
    }

    public void addAll(CommandVector v) {
        //vector.addAll(v.vector);
        for (int i=0 ; i<v.size() ; i++ ) {
            add(v.get(i)); // passing through this method avoids a compiler warning
        }
    }

    @Override
    public void execute() {
        for (int i=0 ; i<vector.size() ; i++ ) {
            Command com = vector.get(i);
            com.execute();
        }
    }
    
    @Override
    public void undo() {
        for (int i=vector.size()-1 ; i>=0 ; i-- ) { // reverse order to that executed
            Command com = vector.get(i);
            com.undo();
        }
    }
    
}
