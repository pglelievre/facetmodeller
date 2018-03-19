package facetmodeller.commands;

import facetmodeller.plc.Node;
import facetmodeller.plc.NodeVector;

public class DuplicateNodeInfo {
    
    private final NodeVector nodesToRemove = new NodeVector();
    private final NodeVector nodesToKeep = new NodeVector();
    // nodesToRemove.get(i) is a duplicate of nodesToKeep.get(i)
    // (there may be duplicated nodes in nodesToKeep)
    
    public void clear() {
        this.nodesToRemove.clear();
        this.nodesToKeep.clear();
    }
    
    public int size() {
        return nodesToRemove.size();
    }
    
    public NodeVector getNodesToRemove() {
        return nodesToRemove;
    }
    
    public Node getNodeToRemove(int i) {
        return nodesToRemove.get(i);
    }
    
    public Node getNodeToKeep(int i) {
        return nodesToKeep.get(i);
    }
    
    public void add(Node node1, Node node2) {
        nodesToKeep.addDup(node1);
        nodesToRemove.addDup(node2);
    }
    
}