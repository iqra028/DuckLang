import java.util.ArrayList;
import java.util.List;

public class State {
    int id;
    List<State>[] transitions;
    boolean isFinal = false;

    @SuppressWarnings("unchecked")
    State(int id) {
        this.id = id;
        transitions = new List[256]; // ASCII range
        for (int i = 0; i < 256; i++) {
            transitions[i] = new ArrayList<>();
        }
    }

    void addTransition(char c, State state) {
        transitions[c].add(state);
    }
}
