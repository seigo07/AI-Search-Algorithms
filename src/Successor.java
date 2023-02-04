import java.util.ArrayList;

/********************
 * 
 * This represents the Successor for search algorithms (nextStates, priorities)
 *
 */

public class Successor {

	private ArrayList<Coord> nextStates;
	private ArrayList<Integer> priorities;

	public Successor(ArrayList<Coord> nextStates, ArrayList<Integer> priorities) {
		this.nextStates = nextStates;
		this.priorities = priorities;
	}

	/**
	 * Getters.
	 */

	public ArrayList<Coord> getNextStates() {
		return nextStates;
	}
	public ArrayList<Integer> getPriorities() {
		return priorities;
	}

}
