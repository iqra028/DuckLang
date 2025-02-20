import java.util.*;

class DFA {
    DFAState startState;
    List<DFAState> states;

    public DFA(DFAState startState) {
        this.startState = startState;
        this.states = new ArrayList<>();
    }
}