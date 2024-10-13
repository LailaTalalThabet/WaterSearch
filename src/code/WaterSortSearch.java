package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {
	private static int numberOfBottles;
	private static int bottleCapacity;
	private static Bottle[] initialState;

	public WaterSortSearch() {

	}

	public static String solve(String state, String strategy, Boolean visualize) {

		String[] parts = state.split(";");
		numberOfBottles = Integer.parseInt(parts[0]);
		bottleCapacity = Integer.parseInt(parts[1]);
		initialState = new Bottle[numberOfBottles];
		for (int i = 0; i < numberOfBottles; i++) {
			String[] letters = (parts[i + 2]).split(",");
			Stack<Character> st = new Stack<>();
			// we do not enter the letter 'e' we instead leave an empty spot
			for (int j = bottleCapacity - 1; j >= 0; j--) {
				if (letters[j].charAt(0) != 'e')
					st.push(letters[j].charAt(0));
			}

			Bottle b = new Bottle(bottleCapacity, st);
			initialState[i] = b;

		}

		WaterSortSearch w = new WaterSortSearch();

		return w.search(initialState, strategy);

	}
	// Method to parse the initial state string

	@Override
	public String search(Bottle[] initialState, String strategy) {
		switch (strategy) {
		case "BF":
			return bfs(initialState);
		case "DF":
			return dfs(initialState);
		case "ID":
			return id(initialState);
		case "GR1":
			return greedySearch(initialState,"GR1");
		case "GR2":
			return greedySearch(initialState,"GR2");
		case "AS1":
			return AStarSearch(initialState,"GR1");
		case "AS2":
			return AStarSearch(initialState,"AS2");

		default:
			return "Invalid Strategy";
		}
	}

	private String bfs(Bottle[] initialState) {
		Queue<Node> frontier = new LinkedList<>();
		Map<String, Node> explored = new HashMap<>();
		int ex = 0;

		Node root = new Node(initialState, null, null, 0, 0);
		frontier.add(root);

		while (!frontier.isEmpty()) {

			Node node = frontier.poll();

			if (isGoal(node.state)) {
				return constructSolution(node, ex);
			}

			ex++;

			for (String action : getActions(node.state)) {

				Bottle[] newState = new Bottle[node.state.length];
				for (int i = 0; i < node.state.length; i++)
					newState[i] = (Bottle) node.state[i].clone();

				int x = newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);

				Node Child = new Node(newState, node, action, node.pathCost + x, node.depth + 1);
				String stateKey = Child.toStringState();
				if (explored.containsKey(stateKey)) {
					Node existingNode = explored.get(stateKey);
					if (Child.pathCost < existingNode.pathCost) {
						// Replace the existing node with the new one if it's cheaper
						explored.put(stateKey, Child);
						frontier.add(Child); // Add the new node to the frontier
					}

				} else {
					frontier.add(Child);
					explored.put(Child.toStringState(), Child);

				}
			}

		}

		return "NOSOLUTION";
	}

	private String dfs(Bottle[] initialState) {
		LinkedList<Node> frontier = new LinkedList<>();
		Map<String, Node> explored = new HashMap<>();

		int ex = 0;

		Node root = new Node(initialState, null, null, 0, 0);
		frontier.addFirst(root);
		explored.put(root.toStringState(), root);

		while (!frontier.isEmpty()) {

			Node node = frontier.removeFirst();

			if (isGoal(node.state)) {
				return constructSolution(node, ex);
			}

			ex++;

			for (String action : getActions(node.state)) {

				Bottle[] newState = new Bottle[node.state.length];
				for (int i = 0; i < node.state.length; i++)
					newState[i] = (Bottle) node.state[i].clone();

				int x = newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);
				Node Child = new Node(newState, node, action, node.pathCost + x, node.depth + 1);
				String stateKey = Child.toStringState();
				if (explored.containsKey(stateKey)) {
					Node existingNode = explored.get(stateKey);
					if (Child.pathCost < existingNode.pathCost) {
						// Replace the existing node with the new one if it's cheaper
						explored.put(stateKey, Child);
						frontier.addFirst(Child); // Add the new node to the frontier
					}

				} else {
					frontier.addFirst(Child);
					explored.put(Child.toStringState(), Child);

				}

			}

		}

		return "NOSOLUTION";
	}

	public String DepthLimitedSearch(Bottle[] initialState, int l) {
		LinkedList<Node> frontier = new LinkedList<>();
		Map<String, Node> explored = new HashMap<>();

		int ex = 0;

		Node root = new Node(initialState, null, null, 0, 0);
		frontier.addFirst(root);
		explored.put(root.toStringState(), root);

		while (!frontier.isEmpty()) {

			Node node = frontier.removeFirst();

			if (isGoal(node.state)) {
				return constructSolution(node, ex);
			}

			ex++;

			for (String action : getActions(node.state)) {

				Bottle[] newState = new Bottle[node.state.length];
				for (int i = 0; i < node.state.length; i++)
					newState[i] = (Bottle) node.state[i].clone();

				int x = newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);
				Node Child = new Node(newState, node, action, node.pathCost + x, node.depth + 1);
				String stateKey = Child.toStringState();
				if (Child.depth <= l) {
					if (explored.containsKey(stateKey)) {
						Node existingNode = explored.get(stateKey);
						if (Child.pathCost < existingNode.pathCost) {
							// Replace the existing node with the new one if it's cheaper
							explored.put(stateKey, Child);
							frontier.addFirst(Child); // Add the new node to the frontier
						}

					} else {
						frontier.addFirst(Child);
						explored.put(Child.toStringState(), Child);

					}
				}

			}

		}

		return "NOSOLUTION";

	}

	public String id(Bottle[] initialstate) {
		int i = 1;
		while (true) {
			if (DepthLimitedSearch(initialstate, i).equals("NOSOLUTION")) {
				i = i + 1;
			} else {
				return DepthLimitedSearch(initialstate, i);
			}
		}
	}

//	Heuristic 1 (GR1): Number of Non-Homogeneous Bottles
//	This heuristic counts the number of bottles that are not yet homogeneous (i.e., bottles where all the liquids aren't the same color or aren't empty).
//	Admissibility: This is admissible because, at a minimum, each non-homogeneous bottle will require at least one move to sort. It won't overestimate the number of moves needed.
//	
	public int heuristicGR1(Bottle[] state) {
		int nonHomogeneousBottles = 0;

		for (Bottle bottle : state) {
			if (!bottle.isSameColor()) {
				nonHomogeneousBottles++;
			}
		}

		return nonHomogeneousBottles;
	}

//	Heuristic 2 (GR2): Number of Individual Colors Out of Place
//	This heuristic counts the total number of misplaced liquids across all bottles. Each color in a bottle that doesn’t match the rest of the bottle’s contents or isn’t in its final, sorted position is considered "out of place."
//
//	Admissibility: Since each misplaced liquid will need to be moved at least once, this heuristic is also admissible. It provides an underestimate of the true number of moves required.
//	
	public int heuristicGR2(Bottle[] state) {
		int misplacedColors = 0;

		for (Bottle bottle : state) {
			misplacedColors += bottle.getMisplacedCount();
		}

		return misplacedColors;
	}

	public String greedySearch(Bottle[] initialState, String heuristicType) {
		//the next line creates a comparator that compares two Node objects based on the integer value
		//returned by the getHeuristic() function. This comparison is what determines the priority order in the queue.
		PriorityQueue<Node> frontier = new PriorityQueue<>(
				Comparator.comparingInt(node -> getHeuristic(node.state, heuristicType)));
		Map<String, Node> explored = new HashMap<>();
        int ex = 0;

        Node root = new Node(initialState, null, null, 0, 0);
        frontier.add(root);
        while(!frontier.isEmpty()) {
        	 Node node = frontier.poll();

             // If the goal is reached
             if (isGoal(node.state)) {
                 return constructSolution(node, ex);
             }

             ex++;
             explored.put(node.toStringState(), node);
             for (String action : getActions(node.state)) {

 				Bottle[] newState = new Bottle[node.state.length];
 				for (int i = 0; i < node.state.length; i++)
 					newState[i] = (Bottle) node.state[i].clone();

 				int x = newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);

 				Node Child = new Node(newState, node, action, node.pathCost + x, node.depth + 1);
 				String stateKey = Child.toStringState();
 				if (explored.containsKey(stateKey)) {
 					Node existingNode = explored.get(stateKey);
 					if (Child.pathCost < existingNode.pathCost) {
 						// Replace the existing node with the new one if it's cheaper
 						explored.put(stateKey, Child);
 						frontier.add(Child); // Add the new node to the frontier
 					}

 				} else {
 					frontier.add(Child);
 					explored.put(Child.toStringState(), Child);

 				}
 			}


        	
        }
		return "NOSOLUTION";

	}
	// Helper method to select and return the appropriate heuristic value
    private int getHeuristic(Bottle[] state, String heuristicType) {
        switch (heuristicType) {
            case "GR1":
                return heuristicGR1(state);  
            case "GR2":
                return heuristicGR2(state);  
            case "AS1":
                return heuristicGR1(state);  
            case "AS2":
                return heuristicGR2(state);  
            default:
                throw new IllegalArgumentException("Invalid heuristic type: " + heuristicType);
        }
    }
    public String AStarSearch(Bottle[] initialState, String heuristicType) {
    	//same as greedy but we add the path cost to the heuristic function value
    	PriorityQueue<Node> frontier = new PriorityQueue<>(
    	        Comparator.comparingInt(node -> node.pathCost + getHeuristic(node.state, heuristicType)) // f(n) = g(n) + h(n)
    	    );
    	Map<String, Node> explored = new HashMap<>();
        int ex = 0;

        Node root = new Node(initialState, null, null, 0, 0);
        frontier.add(root);
        while(!frontier.isEmpty()) {
        	 Node node = frontier.poll();

             // If the goal is reached
             if (isGoal(node.state)) {
                 return constructSolution(node, ex);
             }

             ex++;
             explored.put(node.toStringState(), node);
             for (String action : getActions(node.state)) {

 				Bottle[] newState = new Bottle[node.state.length];
 				for (int i = 0; i < node.state.length; i++)
 					newState[i] = (Bottle) node.state[i].clone();

 				int x = newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);

 				Node Child = new Node(newState, node, action, node.pathCost + x, node.depth + 1);
 				String stateKey = Child.toStringState();
 				if (explored.containsKey(stateKey)) {
 					Node existingNode = explored.get(stateKey);
 					if (Child.pathCost < existingNode.pathCost) {
 						// Replace the existing node with the new one if it's cheaper
 						explored.put(stateKey, Child);
 						frontier.add(Child); // Add the new node to the frontier
 					}

 				} else {
 					frontier.add(Child);
 					explored.put(Child.toStringState(), Child);

 				}
 			}


        	
        }
    	return "NOSOLUTION";
    }
}
