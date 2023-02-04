import java.util.*;

/********************
 * 
 * This represents the Node for search algorithms (state, parentNode, pathCost, fCost, depth, priority)
 *
 */

public class Node {

	private Coord state;
	private Node parentNode;
	private double pathCost;
	private double fCost;
	private int depth;
	// Right = 1, Down = 2, Left = 3, Up = 4
	private int priority;
	private boolean isVisited;

	public Node(Node parentNode, Coord child_state, Coord goal, A1main.SearchAlgorithm alg, int priority) {
		this.state = child_state;
		this.parentNode = parentNode;
		this.priority = priority;
		if (this.parentNode != null) {
//			this.action();
//			child_state = parent_node.getState();
			this.pathCost = parentNode.pathCost + getCost(parentNode.state, child_state);
			// Set fCost for informed search
			if (goal != null) {
				switch (alg) {
					case BestF:
						fCost = getCost(this.state, goal);
						break;
					case AStar:
						fCost = getCost(this.state, goal) + this.pathCost;
						break;
				}
			}
			this.depth = parentNode.depth + 1;
		}
		if (alg == A1main.SearchAlgorithm.Bidirectional) {
			isVisited = true;
		}
	}

	/**
	 * Getters.
	 */

	public Coord getState() {
		return state;
	}
	public Node getParentNode() {
		return parentNode;
	}
	public double getPathCost() {
		return pathCost;
	}
	public double getFCost() {
		return fCost;
	}
	public int getDepth() {
		return depth;
	}
	public int getPriority() {
		return priority;
	}
	public boolean getIsVisited() {
		return isVisited;
	}

	public void setIsVisited() {
		this.isVisited = true;
	}

	public void action() {
		state = parentNode.state;
	}

	/**
	 * Calculate cost based on Manhattan distance heuristic on triangular grid.
	 */
	public double getCost(Coord parent_node_state, Coord child_state) {

		ArrayList<Integer> pList = getDistanceList(parent_node_state);
		ArrayList<Integer> cList = getDistanceList(child_state);

		// Manhattan distance = | cList(0) − pList(0) | + | cList(1) − pList(1) | + | cList(2) − pList(2) |
		int a = cList.get(0) - pList.get(0);
		int b = cList.get(1) - pList.get(1);
		int c = cList.get(2) - pList.get(2);

		// H-distance = | a | + | b | + | c |
		return Math.abs(a) + Math.abs(b) + Math.abs(c);
	}

	/**
	 * Calculate distance list based on Manhattan distance heuristic on triangular grid.
	 */
	public ArrayList<Integer> getDistanceList(Coord state) {

		ArrayList<Integer> list = new ArrayList<>();

		// dir = 0 if upwards triangle, dir = 1 if downwards triangle
		int dir = (state.getC() + state.getR()) % 2 == 0 ? 0 : 1;

		// a = −row b = (row + col − dir) / 2 c = (row + col − dir) / 2 − row + dir
		int a = -state.getR();
		int b = (state.getR() + state.getC() - dir) / 2;
		int c = (state.getR() + state.getC() - dir) / 2 - state.getR() + dir;
		list.add(a);
		list.add(b);
		list.add(c);

		return list;
	}

}
