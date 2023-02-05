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
	// The estimated cost of the path from the state at node n to the goal
	private double hCost;
	private double fCost;
	private int depth;
	// Right = 1, Down = 2, Left = 3, Up = 4
	private int priority;
	private boolean isVisited;

	public Node(Node parentNode, Coord childState, Coord goal, A1main.SearchAlgorithm alg, int priority) {
		this.state = childState;
		this.parentNode = parentNode;
		this.priority = priority;
		if (this.parentNode != null) {
//			this.action();
//			child_state = parent_node.getState();
			this.pathCost = parentNode.pathCost + getCost(parentNode.state, childState);
			// Set fCost for informed search
			if (goal != null) {
				this.hCost = getCost(this.state, goal);
				switch (alg) {
					case BestF:
						fCost = this.hCost;
						break;
					case AStar:
						fCost = this.hCost + this.pathCost;
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
	 * Calculate H-Cost based on Manhattan distance heuristic on triangular grid.
	 */
	public double getCost(Coord parentNodeState, Coord childState) {

		ArrayList<Integer> parentCoordinates = getNewCoordinates(parentNodeState);
		ArrayList<Integer> childCoordinates = getNewCoordinates(childState);

		// Manhattan distance = | childCoordinates(0) − parentCoordinates(0) | + | childCoordinates(1) − parentCoordinates(1) | + | childCoordinates(2) − parentCoordinates(2) |
		int a = childCoordinates.get(0) - parentCoordinates.get(0);
		int b = childCoordinates.get(1) - parentCoordinates.get(1);
		int c = childCoordinates.get(2) - parentCoordinates.get(2);

		// H-distance = | a | + | b | + | c |
		return Math.abs(a) + Math.abs(b) + Math.abs(c);
	}

	/**
	 * Calculate distance list based on Manhattan distance heuristic on triangular grid.
	 */
	public ArrayList<Integer> getNewCoordinates(Coord state) {

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
