import java.util.ArrayList;

/********************Starter Code
 * 
 * This represents the Node for search algorithms (state, parent_node, path_cost, depth)
 *
 */

public class Successor {

	private ArrayList<Coord> nextStates;
	private ArrayList<Integer> priorities;

	public Successor(ArrayList<Coord> nextStates, ArrayList<Integer> priorities) {
		this.nextStates = nextStates;
		this.priorities = priorities;
	}

	public ArrayList<Coord> getNextStates() {
		return nextStates;
	}
	public ArrayList<Integer> getPriorities() {
		return priorities;
	}

}
