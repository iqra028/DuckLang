import java.util.*;


class DFAState {
    int id;
    boolean isFinal;
    Map<Character, DFAState> transitions;

    public DFAState(int id, boolean isFinal) {
        this.id = id;
        this.isFinal = isFinal;
        this.transitions = new HashMap<>();
    }
}
