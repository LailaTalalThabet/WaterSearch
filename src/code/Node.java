package code;

public class Node {
    public Bottle[] state;   // Current state (e.g., bottle configurations)
    public Node parent;    // Parent node
    public String action;  // Action taken to reach this state
    public int pathCost;   // Total cost to reach this node
    public int depth;      // Depth in the search tree

    public Node(Bottle[] state, Node parent, String action, int pathCost, int depth) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.pathCost = pathCost;
        this.depth = depth;
    }
}