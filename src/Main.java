import java.util.*;

/********************Starter Code
 * 
 * This class contains some examples on how to handle the required inputs and outputs 
 * and other debugging options
 * 
 * @author at258
 * 
 * run with 
 * java Main <Algo> <ConfID>
 * 
 */


public class Main {

	public static void main(String[] args) {

		/*
		 *
		 * Retrieve input and configuration
		 * and run search algorithm
		 *
		 */

		Conf conf = Conf.valueOf(args[1]);

		//run your search algorithm
		runSearch(args[0],conf.getMap(),conf.getS(),conf.getG());
	}

	private static void runSearch(String algo, Map map, Coord start, Coord goal) {
		switch(algo) {
		case "BFS": //run BFS
			search(map, start, goal, Algorithms.BFS);
			break;
		case "DFS": //run DFS
			search(map, start, goal, Algorithms.DFS);
			break;
		case "BestF": //run BestF
			search(map, start, goal, Algorithms.BestF);
			break;
		case "AStar": //run AStar
			search(map, start, goal, Algorithms.AStar);
			break;
		case "Bidirectional": //run Bidirectional
			bidirectionalSearch(map, start, goal, Algorithms.Bidirectional);
			break;
		}
	}

	/**
	 * Main TREE-SEARCH function.
	 */
	public static void search(Map problem, Coord initialState, Coord goal, Algorithms alg) {

		// Create initial node with initialState
		Deque<Node> frontier = new ArrayDeque<>();
		Deque<Node> explored = new ArrayDeque<>();
		Node initialNode = new Node(null, initialState, goal, alg, 0);
		frontier.add(initialNode);

		while (!frontier.isEmpty()) {

			// Output each coordinate of frontier while loop
			boolean isInformed = (alg == Algorithms.BestF || alg == Algorithms.AStar);
			outputFrontier(frontier, isInformed);

			// Remove and get node from frontier (frontier queue is already sorted by priority for informed search algorithms)
			Node nd = alg == Algorithms.DFS ? frontier.removeLast() : frontier.removeFirst();
			explored.add(nd);

			// Output result and finish if the node is equal to goal
			if (goal.equals(nd.getState())) {
				// Output result in case of success
				outputResult(nd, explored);
				return;
			// Continue to search until we can get the goal.
			} else {
				switch (alg) {
					case BFS, DFS:
						for (Node n : expand(nd, problem, frontier, explored, goal, alg)) {
							frontier.addLast(n);
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
	public static Deque<Node> expand(Node node, Map problem, Deque<Node> frontier, Deque<Node> explored, Coord goal, Algorithms alg) {

		// Get successors
		Successor successor = getSuccessor(node.getState(), problem);
		Deque<Node> successors = new ArrayDeque<>();

		// Add node to successors
		for (int i = 0; i < successor.getNextStates().size(); i++) {
			Node nd;
			switch (alg) {
				case BFS:
					nd = new Node(node, successor.getNextStates().get(i), null, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of frontier or explored
					if (!isNodeExist(nd.getState(), frontier, explored)) {
						// Using Deque as queue
						successors.add(nd);
					}
					break;
				case DFS:
					nd = new Node(node, successor.getNextStates().get(i), null, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of frontier or explored
					if (!isNodeExist(nd.getState(), frontier, explored)) {
						// Using Deque as stack
						successors.addFirst(nd);
					}
					break;
				case BestF:
					nd = new Node(node, successor.getNextStates().get(i), goal, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of frontier or explored
					if (!isNodeExist(nd.getState(), frontier, explored)) {
						successors.add(nd);
					}
					break;
				case AStar:
					nd = new Node(node, successor.getNextStates().get(i), goal, alg, successor.getPriorities().get(i));
					// Check if the state is not contained in a node of frontier or explored
					if (!isNodeExist(nd.getState(), frontier, explored)) {
						successors.add(nd);
					// Check if the state is in a node in frontier but with higher PATH-COST
					} else if (isNodeExistInQueue(nd.getState(), frontier)) {
						// Replace old node with nd
						frontier = replaceNode(nd, frontier);
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
		if (isRightOrBottomStateValid(nodeState.getC(), problem.getMap().length)) {
			int fi = nodeState.getR();
			int si = nodeState.getC() + 1;
			// Check if the next value of node is 1 or not
			if (isNextStateValid(problem, fi, si)) {
				// Add right node
				nextStates.add(new Coord(fi, si));
				priorities.add(1);
			}
		}

		// Upwards pointing triangles
		if (dir == 0) {
			// Check if the index of next state is out of bounds or not.
			if (isRightOrBottomStateValid(nodeState.getR(), problem.getMap().length)) {
				int fi = nodeState.getR() + 1;
				int si = nodeState.getC();
				// Check if the next value of node is 1 or not
				if (isNextStateValid(problem, fi, si)) {
					// Add bottom node
					nextStates.add(new Coord(fi, si));
					priorities.add(2);
				}
			}
		}

		// Check if the index of next state is out of bounds or not.
		if (isLeftOrTopStateValid(nodeState.getC())) {
			int fi = nodeState.getR();
			int si = nodeState.getC() - 1;
			// Check if the next value of node is 1 or not
			if (isNextStateValid(problem, fi, si)) {
				// Add left node
				nextStates.add(new Coord(fi, si));
				priorities.add(3);
			}
		}

		// Downwards pointing triangles
		if (dir == 1) {
			// Check if the index of next state is out of bounds or not.
			if (isLeftOrTopStateValid(nodeState.getR())) {
				int fi = nodeState.getR() - 1;
				int si = nodeState.getC();
				// Check if the next value of node is 1 or not
				if (isNextStateValid(problem, fi, si)) {
					// Add top node
					nextStates.add(new Coord(fi, si));
					priorities.add(4);
				}
			}
		}
		return new Successor(nextStates, priorities);
	}

	/**
	 * Get boolean the value of the existence of node in frontier or explored.
	 */
	public static boolean isNodeExist(Coord state, Deque<Node> frontier, Deque<Node> explored) {
		return isNodeExistInQueue(state, frontier) || isNodeExistInQueue(state, explored);
	}

	/**
	 * Check state is not contained in a node of frontier or explored.
	 */
	public static boolean isNodeExistInQueue(Coord state, Deque<Node> deque) {
		for (Node n : deque) {
			if (state.equals(n.getState())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check state is not contained in a node of explored or frontier and replace old node with new one.
	 */
	public static Deque<Node> replaceNode(Node nd, Deque<Node> deque) {

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
	 * Check if the next state is valid (check the value of next node is 0 or 1).
	 */
	public static boolean isNextStateValid(Map problem, int fi, int si) {
		return problem.getMap()[fi][si] != 1;
	}

	/**
	 * Check if index - 1 < 0.
	 */
	public static boolean isLeftOrTopStateValid(int index) {
		return index - 1 >= 0;
	}

	/**
	 * Check if index + 1 < upper limit of index.
	 */
	public static boolean isRightOrBottomStateValid(int index, int length) {
		return index + 1 <= length - 1;
	}

	/**
	 * BIDIRECTIONAL-SEARCH algorithm.
	 */
	public static void bidirectionalSearch(Map problem, Coord initialState, Coord goal, Algorithms alg) {

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
			if (isIntersectNode("startFrontier", startFrontier, startExplored, goalExplored, Directions.Start, problem, goal, alg)) {
				return;
			}
			if (isIntersectNode("goalFrontier", goalFrontier, goalExplored, startExplored, Directions.Goal, problem, initialState, alg)) {
				return;
			}
		}
		// Output result in case of failure
		System.out.println("fail");
		System.out.println(startFrontier.size());
		System.out.println(goalFrontier.size());
	}

	/**
	 * Output result if the search succeeds.
	 */
	public static boolean isIntersectNode(String outputText, Deque<Node> frontier, Deque<Node> explored, Deque<Node> targetExplored, Directions dir, Map problem, Coord state, Algorithms alg) {
		System.out.print(outputText+"\n");
		outputFrontier(frontier, false);
		Node n = frontier.removeFirst();
		Node intersectNode = getIntersectNode(n, targetExplored);

		if (intersectNode != null) {
			outputResultForBidirectional(intersectNode, n, explored, dir);
			return true;
		}

		explored.add(n);
		frontier.addAll(expandForBidirectional(n, problem, frontier, explored, state, alg));
		return false;
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
	public static ArrayList<Node> expandForBidirectional(Node node, Map problem, Deque<Node> frontier, Deque<Node> explored, Coord goal, Algorithms alg) {

		// Get successors
		Successor successor = getSuccessor(node.getState(), problem);
		ArrayList<Node> successors = new ArrayList<>();

		// Add node to successors
		for (int i = 0; i < successor.getNextStates().size(); i++) {
			Node nd = new Node(node, successor.getNextStates().get(i), null, alg, successor.getPriorities().get(i));
			// Check if the state is not contained in a node of frontier or explored
			if (!isNodeExist(nd.getState(), frontier, explored)) {
				successors.add(nd);
			}
		}
		return successors;
	}

	/**
	 * Output result if the search succeeds.
	 */
	public static void outputResultForBidirectional(Node intersectNode, Node nd, Deque<Node> explored, Directions dir) {
		System.out.print("Intersection at: ("+intersectNode.getState().getR()+","+intersectNode.getState().getC()+")\n");
		switch (dir) {
			case Start:
				outputResult(nd, explored);
				outputResult(intersectNode, explored);
				break;
			case Goal:
				outputResult(intersectNode, explored);
				outputResult(nd, explored);
				break;
		}
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
