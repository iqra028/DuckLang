import java.util.*;

class DFA {
    DFAState startState;
    List<DFAState> states;
    public int getTotalStates() {
        return states.size();
    }
    public DFA(DFAState startState) {
        this.startState = startState;
        this.states = new ArrayList<>();
    }
    public void displayTransitionTable() {
        if (states.isEmpty()) {
            System.out.println("DFA has no states.");
            return;
        }

        Set<Character> symbols = new TreeSet<>();
        for (DFAState state : states) {
            for (Character symbol : state.transitions.keySet()) {
                symbols.add(symbol);
            }
        }

        System.out.print("State\t");
        for (Character symbol : symbols) {
            System.out.print(symbol + "\t");
        }
        System.out.println();

        for (DFAState state : states) {
            System.out.print(state.id + "\t");
            for (Character symbol : symbols) {
                if (state.transitions.containsKey(symbol)) {
                    System.out.print(state.transitions.get(symbol).id + "\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
    }
}