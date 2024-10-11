package code;

import java.util.*;

public abstract class GenericSearch {

	// Abstract search method
	public abstract String search(Bottle[] initialState, String strategy);

	// Helper methods for search algorithms (e.g., goal test, action generation)
	protected boolean isGoal(Bottle[] state) {

		for (Bottle b : state) {
			if (b.isSameColor() == false) {
				return false;
			}
		}
		return true;

	}

	// This method generates a list of valid actions from a given state
	protected List<String> getActions(Bottle[] state) {
		ArrayList<String> r= new ArrayList<String>();

		for (int i = 0; i < state.length; i++) {
			
			Bottle b = state[i];
			
			if (!b.stack.isEmpty()) {
				for(int j=0;j<state.length;j++) {
					if(i!=j) {
						
						if((state[j].isEmpty())||((!state[j].isFull() )&& ((state[j].stack.peek())).equals((state[i].stack.peek()) ))) {
							String s="pour_"+i+"_"+j;
							r.add(s);
						}
					}
				}

			}
		}
		return r;

	}

	// This method returns the new state after applying an action
	public Bottle[] pour(Bottle[] state, String action) {
		int i=action.charAt(5);
		int j=action.charAt(7);
		state[i].pour(state[j]);
		return state;
	}

	// This method reconstructs the solution path once the goal is reached
	protected String constructSolution(Node node,int ex) {
		List<String> actions = new ArrayList<>();
		int pathCost = node.pathCost;

		while (node.parent != null) {
			actions.add(node.action);
			node = node.parent;
		}

		Collections.reverse(actions);
		return String.join(",", actions) + ";" + pathCost + ";"+ex;
	}
}
