package facetmodeller.commands;

import facetmodeller.groups.Group;

/** Command to change a group's name.
 * @author Peter
 */
public final class ChangeGroupNameCommand extends Command {
    
    private final Group group;
    private final String oldName, newName;
    
    public ChangeGroupNameCommand(Group g, String n) {
        super("Change Group Name");
        group = g;
        oldName = g.getName();
        newName = n;
    }
    
    @Override
    public void execute() {
        group.setName(newName);
    }
    
    @Override
    public void undo() {
        group.setName(oldName);
    }
    
    
    
}
