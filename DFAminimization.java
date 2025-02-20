
import java.util.*;
public class DFAminimization {

    public DFA minimization(DFA dfa){
        Set<DFAState> reachableStates = findReachableStates(dfa.startState);
        List<DFAState> states =dfa.states;
        Set<DFAState> finalStates = new HashSet<>();
        Set<DFAState> nonFinalStates = new HashSet<>();

        for (DFAState state : states) {
            if (state.isFinal) {
                finalStates.add(state);
            } else {
                nonFinalStates.add(state);
            }
        }
        return new DFA(dfa.startState);  // abhi just righting this to remove error before pushing , after completing minimized dfa ki start state will be passed
    }
    private static Set<DFAState> findReachableStates(DFAState startState) {
        Set<DFAState> reachableState = new HashSet<>();
        Queue<DFAState> queue = new LinkedList<>();
        queue.add(startState);
        reachableState.add(startState);

        while (!queue.isEmpty()) {
            DFAState state = queue.poll();
            for (DFAState nextState : state.transitions.values()) {
                if (!reachableState.contains(nextState)) {
                    reachableState.add(nextState);
                    queue.add(nextState);
                }
            }
        }
        return reachableState;
    }

}
