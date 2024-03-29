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

	public Node(Node parentNode, Coord childState, Coord goal, Algorithms alg, int priority) {
		this.state = childState;
		this.parentNode = parentNode;
		this.priority = priority;
		if (this.parentNode != null) {
			this.pathCost = parentNode.pathCost + getCost(parentNode.state, childState);
			this.depth = parentNode.depth + 1;
		} else {
			this.pathCost = 0;
			this.depth = 0;
		}
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
		if (alg == Algorithms.Bidirectional) {
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

	/**
	 * Calculate H-Cost based on Manhattan distance heuristic on triangular grid.
	 */
	public double getCost(Coord fromState, Coord toState) {

		ArrayList<Integer> fromCoordinates = getNewCoordinates(fromState);
		ArrayList<Integer> toCoordinates = getNewCoordinates(toState);

		// Manhattan distance = | fromCoordinates(0) − toCoordinates(0) | + | fromCoordinates(1) − toCoordinates(1) | + | fromCoordinates(2) − toCoordinates(2) |
		int a = fromCoordinates.get(0) - toCoordinates.get(0);
		int b = fromCoordinates.get(1) - toCoordinates.get(1);
		int c = fromCoordinates.get(2) - toCoordinates.get(2);

		// H-distance = | a | + | b | + | c |
		return Math.abs(a) + Math.abs(b) + Math.abs(c);
	}

	/**
	 * Calculate distance list based on Manhattan distance heuristic on triangular grid.
	 */
	public ArrayList<Integer> getNewCoordinates(Coord state) {

		ArrayList<Integer> newCoordinates = new ArrayList<>();

		// dir = 0 if upwards triangle, dir = 1 if downwards triangle
		int dir = (state.getC() + state.getR()) % 2 == 0 ? 0 : 1;

		// a = −row b = (row + col − dir) / 2 c = (row + col − dir) / 2 − row + dir
		int a = - state.getR();
		int b = (state.getR() + state.getC() - dir) / 2;
		int c = (state.getR() + state.getC() - dir) / 2 - state.getR() + dir;
		newCoordinates.add(a);
		newCoordinates.add(b);
		newCoordinates.add(c);

		return newCoordinates;
	}

}
