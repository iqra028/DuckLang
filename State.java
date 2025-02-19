import java.util.ArrayList;
import java.util.List;

public class State {
    int id;
    List<State> nextStates; // List to hold multiple next states
    char transition;
    boolean isFinal;

    public State(int id, char transition) {
        this.id = id;
        this.transition = transition;
        this.nextStates = new ArrayList<>(); // Initialize the list of next states
        this.isFinal = false;
    }

    // Method to add a transition to another state
    public void addTransition(State nextState) {
        nextStates.add(nextState);
    }
}