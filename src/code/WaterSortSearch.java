package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {
	private static int numberOfBottles;
	private static int bottleCapacity;
	private static Bottle[] initialState;

	public WaterSortSearch() {
		
	}
public static String solve(String state,String strategy,Boolean visualize) {


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
	
	return w.search(initialState,strategy);
	
}
	// Method to parse the initial state string

	@Override
	public String search(Bottle[] initialState, String strategy) {
		switch (strategy) {
		case "BF":
			return bfs(initialState);
		case "DF":
			return dfs(initialState);

		default:
			return "Invalid Strategy";
		}
	}

	private String bfs(Bottle[] initialState) {
		Queue<Node> frontier = new LinkedList<>();
		Map<String, Node> explored = new HashMap<>();
		int ex=0;

		Node root = new Node(initialState, null, null, 0, 0);
		frontier.add(root);
		

		while (!frontier.isEmpty()) {

			Node node = frontier.poll();

			if (isGoal(node.state)) {
				return constructSolution(node,ex);
			}

			ex++;

			for (String action : getActions(node.state)) {
				

				Bottle[] newState = new Bottle[node.state.length];
				for (int i = 0; i < node.state.length; i++)
					newState[i] = (Bottle) node.state[i].clone();

				int x=newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);

				
				Node Child=new Node(newState,node,action,node.pathCost + x,node.depth + 1);
				String stateKey = Child.toStringState();
				if (explored.containsKey(stateKey)) {
					Node existingNode = explored.get(stateKey);
					if (Child.pathCost < existingNode.pathCost) {
	                    // Replace the existing node with the new one if it's cheaper
	                    explored.put(stateKey, Child);
	                    frontier.add(Child); // Add the new node to the frontier
	                }
					
				}
				else {
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
		
		int ex=0;

		Node root = new Node(initialState, null, null, 0, 0);
		frontier.addFirst(root);
		explored.put(root.toStringState(), root);
		

		while (!frontier.isEmpty()) {

			Node node =frontier.removeFirst();
			

			if (isGoal(node.state)) {
				return constructSolution(node,ex);
			}

			ex++;

			for (String action : getActions(node.state)) {
				

				Bottle[] newState = new Bottle[node.state.length];
				for (int i = 0; i < node.state.length; i++)
					newState[i] = (Bottle) node.state[i].clone();

				int x=newState[action.charAt(5) - '0'].pour(newState[action.charAt(7) - '0']);				
				Node Child=new Node(newState,node,action,node.pathCost + x,node.depth + 1);
				String stateKey = Child.toStringState();
				if (explored.containsKey(stateKey)) {
					Node existingNode = explored.get(stateKey);
					if (Child.pathCost < existingNode.pathCost) {
	                    // Replace the existing node with the new one if it's cheaper
	                    explored.put(stateKey, Child);
	                    frontier.addFirst(Child); // Add the new node to the frontier
	                }
					
				}
				else {
					frontier.addFirst(Child);
					explored.put(Child.toStringState(), Child);
					
				}
				
			}
			
		}

		return "NOSOLUTION";
	}
	


	

}
