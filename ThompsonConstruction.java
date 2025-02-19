import java.util.*;

public class ThompsonConstruction {

    private int stateCount = 0;
    private Stack<NFA> stack = new Stack<>();

    public NFA reToNFA(String regex) {
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (c == '*') {
                if (!stack.isEmpty()) applyKleeneStar();
            } else if (c == '|') {
                if (stack.size() > 1) applyUnion();
            } else if (c == '.') {
                if (stack.size() > 1) applyConcatenation();
            } else if (c == '+') {
                if (!stack.isEmpty()) applyPlusOperator();
            } else if (c == '[') {
                i = handleCharacterClass(regex, i);
            } else {
                stack.push(createBasicNFA(c));
            }
        }
        return stack.pop();
    }

    private int handleCharacterClass(String regex, int index) {
        index++; // Move past '['
        char startChar = regex.charAt(index);
        index += 2; // Skip '-'
        char endChar = regex.charAt(index);
        index++; // Move past ']'

        State start = new State(stateCount++, 'ε'); // Start state
        State end = new State(stateCount++, 'ε');   // Final state
        end.isFinal = true; // Mark end state as final

        for (char c = startChar; c <= endChar; c++) {
            State charState = new State(stateCount++, c);
            charState.addTransition(end); // Each character state transitions to the final state
            if(charState!=start)
            {
                charState.isFinal=true;
            }
            start.nextStates.add(charState); // Add transition from start to each character state
        }

        stack.push(new NFA(start, end)); // Push the new NFA to the stack
        return index;
    }




    private NFA createBasicNFA(char c) {
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');
        State middle = new State(stateCount++, c);

        start.addTransition(middle);
        middle.addTransition(end);
        end.isFinal = true;

        return new NFA(start, end);
    }

    private void applyConcatenation() {
        NFA nfa2 = stack.pop();
        NFA nfa1 = stack.pop();
        nfa1.end.addTransition(nfa2.start);
        nfa1.end.isFinal = false;
        stack.push(new NFA(nfa1.start, nfa2.end));
    }


    private void applyUnion() {
        NFA nfa2 = stack.pop();
        NFA nfa1 = stack.pop();
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');

        start.addTransition(nfa1.start);
        start.addTransition(nfa2.start);
        nfa1.end.addTransition(end);
        nfa2.end.addTransition(end);

        nfa1.end.isFinal = false;
        nfa2.end.isFinal = false;
        end.isFinal = true;

        stack.push(new NFA(start, end));
    }


    private void applyKleeneStar() {
        NFA nfa = stack.pop();
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');

        start.addTransition(nfa.start);
        start.addTransition(end);
        nfa.end.addTransition(nfa.start);
        nfa.end.addTransition(end);

        nfa.end.isFinal = false;
        end.isFinal = true;

        stack.push(new NFA(start, end));
    }

    private void applyPlusOperator() {
        NFA nfa = stack.pop();
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');

        start.addTransition(nfa.start);
        nfa.end.addTransition(nfa.start);
        nfa.end.addTransition(end);

        nfa.end.isFinal = false;
        end.isFinal = true;

        stack.push(new NFA(start, end));
    }

    public void printNFA(NFA nfa) {
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();
        queue.add(nfa.start);

        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (visited.contains(state)) continue;
            visited.add(state);

            System.out.print("State " + state.id);
            if (state.isFinal) System.out.print(" (Final)");
            System.out.println();

            for (State nextState : state.nextStates) { // Iterate over all transitions
                System.out.println("  -(" + nextState.transition + ")-> State " + nextState.id);
                queue.add(nextState);
            }
        }
    }

}