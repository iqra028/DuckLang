import java.util.*;

public class ThompsonConstruction {

    private int stateCount = 0;
    private Stack<NFA> stack = new Stack<>();

    public NFA reToNFA(String regex) {
        String trimmedRegex = regex.trim();
        stack.clear(); // Clear the stack before processing a new regex

        Stack<NFA> localUnionStack = new Stack<>(); // Local stack for union operations
        NFA prevNFA = null; // Keep track of the previous NFA for concatenation

        for (int i = 0; i < trimmedRegex.length(); i++) {
            char c = trimmedRegex.charAt(i);

            if (c == '|') {
                // Handle union operator
                if (prevNFA != null) {
                    localUnionStack.push(prevNFA); // Push the previous NFA to the local stack
                    prevNFA = null; // Reset prevNFA for the next NFA
                }
            } else if (c == '*') {
                // Handle Kleene star
                if (!stack.isEmpty()) {
                    applyKleeneStar();
                }
            } else if (c == '+') {
                // Handle plus operator
                if (!stack.isEmpty()) {
                    applyPlusOperator();
                }
            } else if (c == '[') {
                // Handle character class
                i = handleCharacterClass(trimmedRegex, i);
            } else if (c == ' ') {
                // Ignore spaces in regex unless explicitly needed
                continue;
            } else {
                // Create a basic NFA for the character
                NFA charNFA = createBasicNFA(c);

                // If there's a previous NFA, concatenate it with the new one
                if (prevNFA != null) {
                    stack.push(prevNFA);
                    stack.push(charNFA);
                    applyConcatenation();
                    prevNFA = stack.pop(); // Store the result for further concatenation
                } else {
                    prevNFA = charNFA;
                }
            }
        }

        // Push the last concatenated NFA onto the stack if necessary
        if (prevNFA != null) {
            localUnionStack.push(prevNFA);
        }

        // After processing the entire regex, apply any remaining unions
        while (localUnionStack.size() > 1) {
            NFA nfa2 = localUnionStack.pop();
            NFA nfa1 = localUnionStack.pop();
            applyUnion(nfa1, nfa2);
        }

        // If there's still one NFA left in the local stack, push it to the main stack
        if (!localUnionStack.isEmpty()) {
            stack.push(localUnionStack.pop());
        }

        // After processing the entire regex, apply any remaining concatenations
        while (stack.size() > 1) {
            applyConcatenation();
        }

        return stack.isEmpty() ? null : stack.pop();
    }

    private void applyUnion(NFA nfa1, NFA nfa2) {
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
    public NFA createDuckBoolNFA() {
        NFA quackQuackNFA = reToNFA("QUACK QUACK");
        NFA quaakNFA = reToNFA("Quaak");

        // Ensure NFAs are valid before pushing to stack
        if (quackQuackNFA != null) stack.push(quackQuackNFA);
        if (quaakNFA != null) stack.push(quaakNFA);

        // Ensure the stack has at least two NFAs before applying union
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough NFAs for union");
        }

        applyUnion();
        return stack.pop();
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