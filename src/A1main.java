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
		BFS, DFS, AStar, BestF, Bidirectional
	}

	public static void main(String[] args) {

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
		case "Bidirectional": //run Bidirectional
			bidirectionalSearch(map, start, goal, SearchAlgorithm.Bidirectional);
			break;
		}
	}

	/**
	 * Main TREE-SEARCH function.
	 */
	public static void search(Map problem, Coord initialState, Coord goal, SearchAlgorithm alg) {

		// Create initial node with initialState
		Node initialNode = new Node(null, initialState, null, alg, 0);
		Deque<Node> frontier = new ArrayDeque<>();
		Deque<Node> explored = new ArrayDeque<>();

		switch (alg) {
			case BFS:
				// Using Deque as que
				frontier.addLast(initialNode);
				break;
			case DFS:
				// Using Deque as stack
				frontier.addFirst(initialNode);
				break;
			case BestF, AStar:
				frontier.add(initialNode);
				break;
		}

		while (!frontier.isEmpty()) {

			// Output each coordinate of frontier while loop
			boolean isInformed = (alg == SearchAlgorithm.BestF || alg == SearchAlgorithm.AStar);
			outputFrontier(frontier, isInformed);

			// Remove and get first node from frontier (This is already sorted by priority for informed search algorithms)
			Node nd = frontier.removeFirst();
			explored.add(nd);

			// Output result and finish if the node is equal to goal
			if (goal.equals(nd.getState())) {
				// Output result in case of success
				outputResult(nd, explored);
				return;
			// Continue to search until we can get the goal.
			} else {
				switch (alg) {
					case BFS:
						for (Node n : expand(nd, problem, frontier, explored, goal, alg)) {
							// Using Deque as que
							frontier.addLast(n);
						}
						break;
					case DFS:
						for (Node n : expand(nd, problem, frontier, explored, goal, alg)) {
							// Using Deque as stack
							frontier.addFirst(n);
						}
						break;
					case BestF, AStar:
						// Add all node to frontier in the order of priority
						frontier.addAll(expand(nd, problem, frontier, explored, goal, alg));
						ArrayList<Node> list = new ArrayList<>(frontier);
						frontier.clear();
						Collections.sort(list, Comparator.comparing(Node::getFCost)
							.thenComparing(Node::getPriority)
							.thenComparing(Node::getDepth));
						frontier.addAll(list);
						break;
				}
			}
		}
		// Output result in case of failure
		System.out.println("fail");
		System.out.println(explored.size());
	}

	/**
	 * BIDIRECTIONAL-SEARCH algorithm.
	 */
	public static void bidirectionalSearch(Map problem, Coord initialState, Coord goal, SearchAlgorithm alg) {

		// Create initial node with initialState
		Node startNode = new Node(null, initialState, null, alg, 0);
		Node goalNode = new Node(null, goal, null, alg, 0);
		Deque<Node> startFrontier = new ArrayDeque<>();
		Deque<Node> goalFrontier = new ArrayDeque<>();
		Deque<Node> startExplored = new ArrayDeque<>();
		Deque<Node> goalExplored = new ArrayDeque<>();

		startFrontier.addLast(startNode);
		goalFrontier.addLast(goalNode);

		while (!startFrontier.isEmpty() && !goalFrontier.isEmpty()) {

			System.out.print("startFrontier\n");
			outputFrontier(startFrontier, false);
			Node sn = startFrontier.removeFirst();
			Node intersectNodeFromStart = getIntersectNode(sn, goalExplored);

			if (intersectNodeFromStart != null) {
				System.out.print("Intersection at: ("+intersectNodeFromStart.getState().getR()+","+intersectNodeFromStart.getState().getC()+")\n");
				outputResult(sn, startExplored);
				outputResult(intersectNodeFromStart, startExplored);
				return;
			}

			startExplored.add(sn);

			// Using Deque as que
			startFrontier.addAll(expandForBidirectional(sn, problem, startFrontier, startExplored, goal, alg));

			System.out.print("goalFrontier\n");
			outputFrontier(goalFrontier, false);
			Node gn = goalFrontier.removeFirst();
			Node intersectNodeFromGoal = getIntersectNode(gn, startExplored);

			if (intersectNodeFromGoal != null) {
				System.out.print("Intersection at: ("+intersectNodeFromGoal.getState().getR()+","+intersectNodeFromGoal.getState().getC()+")\n");
				outputResult(intersectNodeFromGoal, startExplored);
				outputResult(gn, goalExplored);
				return;
			}

			goalExplored.add(gn);
			// Using Deque as que
			goalFrontier.addAll(expandForBidirectional(gn, problem, goalFrontier, goalExplored, initialState, alg));
		}
		// Output result in case of failure
		System.out.println("fail");
		System.out.println(startFrontier.size());
		System.out.println(goalFrontier.size());
	}

	/**
	 * Get intersect node.
	 */
	public static Node getIntersectNode(Node node, Deque<Node> queue) {
		for (Node n : queue) {
			if (node.getState().equals(n.getState())) {
				if (node.getIsVisited() && n.getIsVisited()) {
					return n;
				}
			}
		}
		return null;
	}

	/**
	 * Get successors while the search expand until the algorithm finds the goal.
	 */
	public static ArrayList<Node> expandForBidirectional(Node node, Map problem, Deque<Node> frontier, Deque<Node> explored, Coord goal, SearchAlgorithm alg) {

		// Get successors
		Successor successor = getSuccessor(node.getState(), problem);
		ArrayList<Node> successors = new ArrayList<>();

		// Add node to successors
		for (int i = 0; i < successor.getNextStates().size(); i++) {
			Node nd = new Node(node, successor.getNextStates().get(i), null, alg, successor.getPriorities().get(i));
			// Check if the state is not contained in a node of explored or frontier
			if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
				successors.add(nd);
			}
		}
		return successors;
	}

	/**
	 * Output each coordinate of frontier.
	 * Display coordinate with F_COST in the case of informed search.
	 */
	public static void outputFrontier(Deque<Node> frontier, boolean isInformed) {
		if (!frontier.isEmpty()) {
			String frontierOutput = "[";
			// Separate messages between uninformed and informed searches.
			for (Node n : frontier) {
				frontierOutput += isInformed ?
					"(" + n.getState().getR() + "," + n.getState().getC() + "):" + n.getFCost() + "," :
					"(" + n.getState().getR() + "," + n.getState().getC() + "),";
			}
			// Remove the last ,
			frontierOutput = frontierOutput.substring(0, frontierOutput.lastIndexOf(","));
			frontierOutput += "]";
			System.out.println(frontierOutput);
		}
	}

	/**
	 * Output result if the search succeeds.
	 */
	public static void outputResult(Node nd, Deque<Node> explored) {

		ArrayList<Node> searchNodes = new ArrayList<>();
		Node searchNode = nd;
		searchNodes.add(nd);

		// Get the final path by tracing parentNodes from the goal node to root node
		while (searchNode.getParentNode() != null) {
			searchNodes.add(searchNode.getParentNode());
			searchNode = searchNode.getParentNode();
		}

		String path = "";
		String pathString = "";
		Collections.reverse(searchNodes);

		// Organize path and the direction of path by priority
		for (Node n : searchNodes) {
			path += "("+n.getState().getR()+","+n.getState().getC()+")";
			switch (n.getPriority()) {
				case 1:
					pathString += "Right";
					break;
				case 2:
					pathString += "Down";
					break;
				case 3:
					pathString += "Left";
					break;
				case 4:
					pathString += "Up";
					break;
			}
			pathString += " ";
		}
		// Trim the space when loop finishes
		pathString = pathString.trim();

		// Output result
		System.out.println(path);
		System.out.println(pathString);
		System.out.println(nd.getPathCost());
		System.out.println(explored.size());
	}

	/**
	 * Get successors while the search expand until the algorithm finds the goal.
	 */
	public static ArrayList<Node> expand(Node node, Map problem, Deque<Node> frontier, Deque<Node> explored, Coord goal, SearchAlgorithm alg) {

		// Get successors
		Successor successor = getSuccessor(node.getState(), problem);
		ArrayList<Node> successors = new ArrayList<>();

		// Add node to successors
		for (int i = 0; i < successor.getNextStates().size(); i++) {
			Node nd;
			switch (alg) {
				case BFS, DFS:
					nd = new Node(node, successor.getNextStates().get(i), null, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of explored or frontier
					if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
						successors.add(nd);
					}
					break;
				case BestF:
					nd = new Node(node, successor.getNextStates().get(i), goal, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of explored or frontier
					if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
						successors.add(nd);
					}
					break;
				case AStar:
					nd = new Node(node, successor.getNextStates().get(i), goal, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of explored or frontier
					if (!checkExistenceOfState(nd.getState(), explored) && !checkExistenceOfState(nd.getState(), frontier)) {
						successors.add(nd);
					// Check if the state is in a node in frontier but with higher PATH-COST
					} else if (checkExistenceOfState(nd.getState(), frontier)) {
						// Replace old node with nd
						frontier = replaceOldNodeWithNewOnes(nd, frontier);
					}
					break;
			}
		}
		return successors;
	}

	/**
	 * Get successor instance with nextStates and their priorities.
	 */
	public static Successor getSuccessor(Coord nodeState, Map problem) {

		ArrayList<Coord> nextStates = new ArrayList<>();
		ArrayList<Integer> priorities = new ArrayList<>();

		// dir = 0 if upwards triangle, dir = 1 if downwards triangle
		int dir = (nodeState.getC() + nodeState.getR()) % 2 == 0 ? 0 : 1;

		// Check if the index of next state is out of bounds or not.
		if (checkRightOrBottomNodeIsValid(nodeState.getC(), problem.getMap().length)) {
			int fi = nodeState.getR();
			int si = nodeState.getC() + 1;
			// Check if the next value of node is 1 or not
			if (checkNextValueIsValid(problem, fi, si)) {
				// Add right node
				nextStates.add(new Coord(fi, si));
				priorities.add(1);
			}
		}

		// Upwards pointing triangles
		if (dir == 0) {
			// Check if the index of next state is out of bounds or not.
			if (checkRightOrBottomNodeIsValid(nodeState.getR(), problem.getMap().length)) {
				int fi = nodeState.getR() + 1;
				int si = nodeState.getC();
				// Check if the next value of node is 1 or not
				if (checkNextValueIsValid(problem, fi, si)) {
					// Add bottom node
					nextStates.add(new Coord(fi, si));
					priorities.add(2);
				}
			}
		}

		// Check if the index of next state is out of bounds or not.
		if (checkLeftOrTopNodeIsValid(nodeState.getC())) {
			int fi = nodeState.getR();
			int si = nodeState.getC() - 1;
			// Check if the next value of node is 1 or not
			if (checkNextValueIsValid(problem, fi, si)) {
				// Add left node
				nextStates.add(new Coord(fi, si));
				priorities.add(3);
			}
		}

		// Downwards pointing triangles
		if (dir == 1) {
			// Check if the index of next state is out of bounds or not.
			if (checkLeftOrTopNodeIsValid(nodeState.getR())) {
				int fi = nodeState.getR() - 1;
				int si = nodeState.getC();
				// Check if the next value of node is 1 or not
				if (checkNextValueIsValid(problem, fi, si)) {
					// Add top node
					nextStates.add(new Coord(fi, si));
					priorities.add(4);
				}
			}
		}
		return new Successor(nextStates, priorities);
	}

	/**
	 * Check state is not contained in a node of explored or frontier and replace old node with new one.
	 */
	public static Deque<Node> replaceOldNodeWithNewOnes(Node nd, Deque<Node> deque) {

		Deque<Node> frontier = new ArrayDeque<>();

		for (Node n : deque) {
			if (nd.getState().equals(n.getState())) {
				if (nd.getFCost() < n.getFCost()) {
					frontier.addFirst(nd);
					break;
				} else {
					frontier.addFirst(n);
				}
			} else {
				frontier.addFirst(n);
			}
		}
		return frontier;
	}

	/**
	 * Check state is not contained in a node of explored or frontier.
	 */
	public static boolean checkExistenceOfState(Coord state, Deque<Node> deque) {
		for (Node n : deque) {
			if (state.equals(n.getState())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the next state is valid (check the value of next node is 0 or 1).
	 */
	public static boolean checkNextValueIsValid(Map problem, int fi, int si) {
		return problem.getMap()[fi][si] != 1;
	}

	/**
	 * Check if index - 1 < 0.
	 */
	public static boolean checkLeftOrTopNodeIsValid(int index) {
		return index - 1 >= 0;
	}

	/**
	 * Check if index + 1 < upper limit of index.
	 */
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
