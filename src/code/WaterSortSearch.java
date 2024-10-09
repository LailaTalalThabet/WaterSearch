package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {
	private int numberOfBottles;
	private int bottleCapacity;
	private Bottle[] initialState;

	public WaterSortSearch(String s) {

		String[] parts = s.split(";");
		numberOfBottles = Integer.parseInt(parts[0]);
		bottleCapacity = Integer.parseInt(parts[1]);
		initialState = new Bottle[numberOfBottles];
		for (int i = 0; i < numberOfBottles; i++) {
			String[] letters = (parts[i + 2]).split(",");
			Stack<Character> st = new Stack<>();
			for (int j = bottleCapacity - 1; j >= 0; j--) {
				if (letters[j].charAt(0) != 'e')
					st.push(letters[j].charAt(0));
			}

			Bottle b = new Bottle(bottleCapacity, st);
			initialState[i] = b;

		}

	}

	// Method to parse the initial state string

	@Override
	public String search(Bottle[] initialState, String strategy) {
		switch (strategy) {
		case "BF":
			return bfs(initialState);
		case "DF":
			return bfs(initialState);

		default:
			return "Invalid Strategy";
		}
	}

	private String bfs(Bottle[] initialState) {
		Queue<Node> frontier = new LinkedList<>();
		Set<Bottle[]> explored = new HashSet<>();

		Node root = new Node(initialState, null, null, 0, 0);
		frontier.add(root);

		while (!frontier.isEmpty()) {

			Node node = frontier.poll();

			if (isGoal(node.state)) {
				return constructSolution(node);
			}

			explored.add(node.state);

			for (String action : getActions(node.state)) {
				System.out.println(action);

				Bottle[] newState = new Bottle[node.state.length];
				for (int i = 0; i < node.state.length; i++)
					newState[i] = (Bottle) node.state[i].clone();

				newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);
				if (!explored.contains(newState)) {
					Node child = new Node(newState, node, action, node.pathCost + 1, node.depth + 1);
					frontier.add(child);
				}
			}
			
		}

		return "NOSOLUTION";
	}
	private String dfs(Bottle[] initialState) {
	    Stack<Node> frontier = new Stack<>(); // Use Stack for DFS
	    Set<Bottle[]> explored = new HashSet<>(); // Set to track explored states

	    Node root = new Node(initialState, null, null, 0, 0);
	    frontier.push(root); // Push the root node onto the stack

	    while (!frontier.isEmpty()) {
	        Node node = frontier.pop(); // Pop the top node from the stack

	        if (isGoal(node.state)) {
	            return constructSolution(node); // Goal found
	        }

	        explored.add(node.state); // Mark the current state as explored

	        // Iterate through all possible actions from the current state
	        for (String action : getActions(node.state)) {
	            System.out.println(action);

	            // Create a new state by cloning the current state
	            Bottle[] newState = new Bottle[node.state.length];
	            for (int i = 0; i < node.state.length; i++) {
	                newState[i] = (Bottle) node.state[i].clone(); // Clone each Bottle
	            }

	            // Perform the action
	            newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);
	            
	            // Check if the new state has not been explored
	            if (!explored.contains(newState)) {
	                Node child = new Node(newState, node, action, node.pathCost + 1, node.depth + 1);
	                frontier.push(child); // Push the new node onto the stack
	            }
	        }
	    }

	    return "NOSOLUTION"; // Return if no solution is found
	}


	public static void main(String[] args) {
		String init = "5;4;" + "b,y,r,b;" + "b,y,r,r;" + "y,r,b,y;" + "e,e,e,e;" + "e,e,e,e;";
		WaterSortSearch w = new WaterSortSearch(init);
		System.out.print(w.search(w.initialState, "DF"));

	}

}
