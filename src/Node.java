/********************Starter Code
 * 
 * This represents the solution for search algorithms (path_found, path, path_string, path_cost, n_explored)
 *
 */

public class Node {

	private Coord state;
	private Node parent_node;
	private double path_cost;
	private int depth;

	public Node(Node parent_node, Coord child_state) {
		this.state = child_state;
		this.parent_node = parent_node;
		this.action();
		this.path_cost = parent_node.path_cost + getCost(parent_node.state, child_state);
		this.depth = parent_node.depth + 1;
	}

	public Coord getState() {
		return state;
	}
	public Node getParentNode() {
		return parent_node;
	}
	public double getPathCost() {
		return path_cost;
	}
	public int getDepth() {
		return depth;
	}

	public void action() {
		state = parent_node.state;
	}

	public double getCost(Coord parent_node_state, Coord child_state) {
		return 0.0;
	}
}
