import javax.swing.*;
import java.util.*;

/********************Starter Code
 * 
 * This class contains some examples on how to handle the required inputs and outputs 
 * and other debugging options
 * 
 * @author at258
 * 
 * run with 
 * java A1main <Algo> <ConfID>
 * 
 */


public class A1main {

	public enum SearchAlgorithm {
		BFS, DFS, AStar, BestF
	}

	public static void main(String[] args) {
		//Example: java A1main BFS JCONF03

		/* 
		 * 
		 * Retrieve input and configuration
		 * and run search algorithm
		 *
		 */

		Conf conf = Conf.valueOf(args[1]);

		System.out.println("Configuration:"+args[1]);
		System.out.println("Map:");
		printMap(conf.getMap(), conf.getS(), conf.getG());
		System.out.println("Departure port: Start (r_s,c_s): "+conf.getS());
		System.out.println("Destination port: Goal (r_g,c_g): "+conf.getG());
		System.out.println("Search algorithm: "+args[0]);
		System.out.println();

		//run your search algorithm 
		runSearch(args[0],conf.getMap(),conf.getS(),conf.getG());
	}

	private static void runSearch(String algo, Map map, Coord start, Coord goal) {
		switch(algo) {
		case "BFS": //run BFS
			search(map, start, goal, SearchAlgorithm.BFS);
			break;
		case "DFS": //run DFS
			search(map, start, goal, SearchAlgorithm.DFS);
			break;
		case "BestF": //run BestF
			search(map, start, goal, SearchAlgorithm.BestF);
			break;
		case "AStar": //run AStar
			search(map, start, goal, SearchAlgorithm.AStar);
			break;
		}
	}

	public static void search(Map problem, Coord initial_state, Coord goal, SearchAlgorithm alg) {

		Node initialNode = new Node(null, initial_state, null, alg);
		Deque<Node> frontier = new ArrayDeque<>();

		switch (alg) {
			case BFS:
				frontier.addLast(initialNode);
				break;
			case DFS:
				frontier.addFirst(initialNode);
				break;
			case BestF, AStar:
				frontier.add(initialNode);
				break;
		}
		Deque<Node> explored = new ArrayDeque<>();

		for (;;) {
			// Output the coordinate of frontier
			boolean isInformed = (alg == SearchAlgorithm.BestF || alg == SearchAlgorithm.AStar);
			outputFrontier(frontier, isInformed);
			if (frontier.isEmpty()) {
				// Output result in case of failure
				System.out.println("fail");
				System.out.println(explored.size());
				return;
			}
			Node nd;
			switch (alg) {
				case BestF, AStar:
					nd = removeLowestF(frontier);
					break;
				default:
					nd = frontier.removeFirst();
					break;
			}
			explored.add(nd);
			if (goal.equals(nd.getState())) {
				// Output result in case of success
				outputResult(nd, explored);
				return;
			} else {
				for (Node n : expand(nd, problem, frontier, explored, goal, alg)) {
					switch (alg) {
						case BFS:
							frontier.addLast(n);
							break;
						case DFS:
							frontier.addFirst(n);
							break;
						default:
							frontier.add(n);
					}
				}
			}
		}
	}

	public static void outputFrontier(Deque<Node> frontier, boolean isInformed) {
		if (!frontier.isEmpty()) {
			String frontierOutput = "";
			frontierOutput += "[";
			for (Node n : frontier) {
				frontierOutput += isInformed ?
					"(" + n.getState().getR() + "," + n.getState().getC() + "):" + n.getFCost() + "," :
					"(" + n.getState().getR() + "," + n.getState().getC() + "),";
			}
			frontierOutput = frontierOutput.substring(0, frontierOutput.lastIndexOf(","));
			frontierOutput += "]";
			System.out.println(frontierOutput);
		}
	}

	public static void outputResult(Node nd, Deque<Node> explored) {

		ArrayList<Node> search_nodes = new ArrayList<Node>();
		Node search_node = nd;
		search_nodes.add(nd);

		for (;;) {
			if (search_node.getParentNode() == null) {
				break;
			}
			search_nodes.add(search_node.getParentNode());
			search_node = search_node.getParentNode();
		}

		String path = "";
		String path_string = "";
		Collections.reverse(search_nodes);
		for (Node e : search_nodes) {
			path += "("+e.getState().getR()+","+e.getState().getC()+")";
			path_string += e.getState().getDirection() + " ";
		}
		path_string = path_string.trim();

		// Output result in case of success
		System.out.println(path);
		System.out.println(path_string);
		System.out.println(nd.getPathCost());
		System.out.println(explored.size());
	}

	public static Node removeLowestF(Deque<Node> frontier) {

		Node minN = frontier.getFirst();

		for (Node n : frontier) {
			if (n.getFCost() < minN.getFCost()) {
				minN = n;
			}
		}
		frontier.remove(minN);
		return minN;
	}

	public static ArrayList<Node> expand(Node node, Map problem, Deque<Node> frontier, Deque<Node> explored, Coord goal, SearchAlgorithm alg) {

		ArrayList<Coord> next_states = successor(node.getState(), problem);
		ArrayList<Node> successors = new ArrayList<Node>();

		for (Coord state : next_states) {
			Node nd;
			switch (alg) {
				case BestF:
					nd = new Node(node, state, goal, alg);
					if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
						successors.add(nd);
					}
					break;
				case AStar:
					nd = new Node(node, state, goal, alg);
					if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
						successors.add(nd);
					} else if (checkExistenceOfState(nd.getState(), frontier)) {
						frontier = replaceOldNodeWithNewOnes(nd, frontier);
					}
					break;
				case BFS, DFS:
					nd = new Node(node, state, null, alg);
					if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
						successors.add(nd);
					}
					break;
			}
		}
		return successors;
	}

	// Check state is not contained in a node of explored or frontier and replace old node with new one.
	public static Deque<Node> replaceOldNodeWithNewOnes(Node nd, Deque<Node> deque) {

		Deque<Node> frontier = new ArrayDeque<>();

		for (Node n : deque) {
			if (nd.getState().equals(n.getState())) {
				if (nd.getFCost() > n.getFCost()) {
					frontier.addFirst(nd);
				}
			} else {
				frontier.addFirst(n);
			}
		}
		return frontier;
	}

	// Check state is not contained in a node of explored or frontier
	public static boolean checkExistenceOfState(Coord state, Deque<Node> deque) {
		for (Node n : deque) {
			if (state.equals(n.getState())) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Coord> successor(Coord node_state, Map problem) {
		int dir = (node_state.getC() + node_state.getR()) % 2 == 0 ? 0 : 1;
		ArrayList<Coord> next_states = getNextStates(problem, node_state, dir);
		return next_states;
	}

	public static ArrayList<Coord> getNextStates(Map problem, Coord node_state, int dir) {

		ArrayList<Coord> next_states = new ArrayList<Coord>();

		// Add right node
		if (checkRightOrBottomNodeIsValid(node_state.getC(), problem.getMap().length)) {
			int fi = node_state.getR();
			int si = node_state.getC() + 1;
			if (checkNextValueIsValid(problem, fi, si)) {
				Coord coord = new Coord(fi, si);
				coord.setDirection("Right");
				next_states.add(coord);
			}
		}
		// Upper case
		if (dir == 0) {
			// Add bottom node
			if (checkRightOrBottomNodeIsValid(node_state.getR(), problem.getMap().length)) {
				int fi = node_state.getR() + 1;
				int si = node_state.getC();
				if (checkNextValueIsValid(problem, fi, si)) {
					Coord coord = new Coord(fi, si);
					coord.setDirection("Down");
					next_states.add(coord);
				}
			}
		}
		// Add left node
		if (checkLeftOrTopNodeIsValid(node_state.getC())) {
			int fi = node_state.getR();
			int si = node_state.getC() - 1;
			if (checkNextValueIsValid(problem, fi, si)) {
				Coord coord = new Coord(fi, si);
				coord.setDirection("Left");
				next_states.add(coord);
			}
		}
		// Upper case
		if (dir == 1) {
			// Add top node
			if (checkLeftOrTopNodeIsValid(node_state.getR())) {
				int fi = node_state.getR() - 1;
				int si = node_state.getC();
				if (checkNextValueIsValid(problem, fi, si)) {
					Coord coord = new Coord(fi, si);
					coord.setDirection("Up");
					next_states.add(coord);
				}
			}
		}
		return next_states;
	}

	// Check if the next state is valid
	public static boolean checkNextValueIsValid(Map problem, int fi, int si) {
		return problem.getMap()[fi][si] != 1;
	}

	// Check if index - 1 < 0
	public static boolean checkLeftOrTopNodeIsValid(int index) {
		return index - 1 >= 0;
	}

	// Check if index + 1 < upper limit of index
	public static boolean checkRightOrBottomNodeIsValid(int index, int length) {
		return index + 1 <= length - 1;
	}

	private static void printMap(Map m, Coord init, Coord goal) {

		int[][] map=m.getMap();

		System.out.println();
		int rows=map.length;
		int columns=map[0].length;

		//top row
		System.out.print("  ");
		for(int c=0;c<columns;c++) {
			System.out.print(" "+c);
		}
		System.out.println();
		System.out.print("  ");
		for(int c=0;c<columns;c++) {
			System.out.print(" -");
		}
		System.out.println();

		//print rows 
		for(int r=0;r<rows;r++) {
			boolean right;
			System.out.print(r+"|");
			if(r%2==0) { //even row, starts right [=starts left & flip right]
				right=false;
			}else { //odd row, starts left [=starts right & flip left]
				right=true;
			}
			for(int c=0;c<columns;c++) {
				System.out.print(flip(right));
				if(isCoord(init,r,c)) {
					System.out.print("S");
				}else {
					if(isCoord(goal,r,c)) {
						System.out.print("G");
					}else {
						if(map[r][c]==0){
							System.out.print(".");
						}else{
							System.out.print(map[r][c]);
						}
					}
				}
				right=!right;
			}
			System.out.println(flip(right));
		}
		System.out.println();


	}

	private static boolean isCoord(Coord coord, int r, int c) {
		//check if coordinates are the same as current (r,c)
		if(coord.getR()==r && coord.getC()==c) {
			return true;
		}
		return false;
	}



	public static String flip(boolean right) {
        //prints triangle edges
		if(right) {
			return "\\"; //right return left
		}else {
			return "/"; //left return right
		}

	}

}
