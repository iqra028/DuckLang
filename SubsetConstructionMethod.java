import java.util.*;


public class SubsetConstructionMethod {

    public static DFA conversion(NFA nfa)
    {
        Map<Set<State>,DFAState> DFAstates =new HashMap<>();
        Queue<Set<State>> queue =new LinkedList<>();
        List<DFAState> generatedDFAStates=new ArrayList<>();


        Set<State> startingSet =epsilonClosure(nfa.start);

        DFAState DFAstartState =new DFAState(DFAstates.size(),HasFinal(startingSet));
        DFAstates.put(startingSet,DFAstartState);
        queue.add(startingSet);
        generatedDFAStates.add(DFAstartState);

        while(!queue.isEmpty()){
            Set<State> current_set=queue.poll();
            DFAState current_DFA= DFAstates.get(current_set);

            Map<Character,Set<State>> transitions =new HashMap<>();
            for(State state:current_set){
                for(State next:state.nextStates){
                    if(next.transition!='ε')
                    {

                        transitions.computeIfAbsent(next.transition, k -> new HashSet<>()).addAll(epsilonClosure(next));
                    }
                }
            }
            for(Map.Entry<Character,Set<State>> entry:transitions.entrySet())
            {
                char symbol= entry.getKey();
                Set<State> goalSet=entry.getValue();

                DFAState goalDFA=DFAstates.get(goalSet);
                if(goalDFA == null)
                {
                    goalDFA = new DFAState(DFAstates.size(),HasFinal(goalSet));
                    DFAstates.put(goalSet,goalDFA);
                    queue.add(goalSet);
                    generatedDFAStates.add(goalDFA);
                }
                current_DFA.transitions.put(symbol,goalDFA);
            }
        }
        return new DFA(DFAstartState);
    }
    private static String printStateSet(Set<State> states) {
        StringBuilder sb = new StringBuilder("{ ");
        for (State s : states) {
            sb.append("q").append(s.id).append(" ");
        }
        sb.append("}");
        return sb.toString();
    }

    private static Set<State> epsilonClosure(State state) {
        Set<State> closure = new HashSet<>();
        Stack<State> stack = new Stack<>();
        stack.push(state);

        while (!stack.isEmpty()) {
            State s = stack.pop();
            if (closure.add(s)) { // Adds state only if not already present
                for (State next : s.nextStates) {
                    if (next.transition == 'ε') {
                        stack.push(next); // Always process ε-transitions
                    }
                }

            }
        }

        return closure;
    }

    private static Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>();
        for (State state : states) {
            closure.addAll(epsilonClosure(state)); // Expand for all states
        }
        return closure;
    }

    private static boolean HasFinal(Set<State> states) {
        for (State state : states) {
            if (state.isFinal) {
                return true;
            }
        }
        return false;
    }
    public void printTransitionTable(DFA dfa) {
        System.out.println("DFA Transition Table:");
        System.out.println("State | Symbol -> Next State");
        for (DFAState state : dfa.states) {
            for (Map.Entry<Character, DFAState> entry : state.transitions.entrySet()) {
                System.out.println(state.id + " | " + entry.getKey() + " -> " + entry.getValue().id);
            }
        }
    }

}
