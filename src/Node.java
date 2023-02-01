import java.util.*;

/********************Starter Code
 * 
 * This represents the Node for search algorithms (state, parent_node, path_cost, depth)
 *
 */

public class Node {

	private Coord state;
	private Node parent_node;
	private double path_cost;
	private double f_cost;
	private int depth;
	private int priority;

	public Node(Node parent_node, Coord child_state, Coord goal, A1main.SearchAlgorithm alg) {
		this.state = child_state;
		this.parent_node = parent_node;
		if (this.parent_node != null) {
//			this.action();
//			child_state = parent_node.getState();
			this.path_cost = parent_node.path_cost + getCost(parent_node.state, child_state);
			if (goal != null) {
				switch (alg) {
					case BestF:
						f_cost = getCost(this.state, goal);
						priority = state.getPriority();
						break;
					case AStar:
						f_cost = getCost(this.state, goal) + this.path_cost;
						priority = state.getPriority();
						break;
				}
			}
			this.depth = parent_node.depth + 1;
		}
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
	public double getFCost() {
		return f_cost;
	}
	public int getDepth() {
		return depth;
	}
	public int getPriority() {
		return priority;
	}

	public void action() {
		state = parent_node.state;
	}

	public double getCost(Coord parent_node_state, Coord child_state) {
		ArrayList<Integer> pList = getDistanceList(parent_node_state);
		ArrayList<Integer> cList = getDistanceList(child_state);
		int a = cList.get(0) - pList.get(0);
		int b = cList.get(1) - pList.get(1);
		int c = cList.get(2) - pList.get(2);
		return Math.abs(a) + Math.abs(b) + Math.abs(c);
	}

	public ArrayList<Integer> getDistanceList(Coord state) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int dir = (state.getC() + state.getR()) % 2 == 0 ? 0 : 1;
		int a = -state.getR();
		int b = (state.getR() + state.getC() - dir) / 2;
		int c = (state.getR() + state.getC() - dir) / 2 - state.getR() + dir;
		list.add(a);
		list.add(b);
		list.add(c);
		return list;
	}

	public int compareTo(Node n) {

		if(this.priority<n.priority){
			return -1;
		}else if(this.priority>n.priority){
			return 1;
		}else{
			return 0;
		}

	}
}
