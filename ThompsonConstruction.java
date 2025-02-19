import java.util.*;

public class ThompsonConstruction {

    private int stateCount = 0;
    private Stack<NFA> stack = new Stack<>();
    private NFA createWildcardNFA() {
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');
        State middle = new State(stateCount++, '.'); // Representing any character

        start.addTransition(middle);
        middle.addTransition(end);
        end.isFinal = true;

        return new NFA(start, end);
    }
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
            } else if (c == '-') {
                // Handle optional `-` using the `applyOptional` method
                stack.push(createBasicNFA('-'));
                applyOptional();
                i=i+2;
            }else if (c == '\\') {
                System.out.println("iiiiicameeeeehereeeeee");

                // Handle wildcard character
                i++;
                c = trimmedRegex.charAt(i);
                NFA escapedNFA = createBasicNFA(c);
                stack.push(escapedNFA);
            }else if (c == '.') {
                // Handle wildcard character
                NFA wildcardNFA = createWildcardNFA();
                stack.push(wildcardNFA);
            }
            else if (c >= '0' && c <= '9') {
                // Handle `[0-9]+` dynamically
                State start = new State(stateCount++, 'ε'); // Start state
                State end = new State(stateCount++, 'ε');   // End state
                end.isFinal = true;

                // Create transitions for all digits (0-9)
                for (char digit = '0'; digit <= '9'; digit++) {
                    State digitState = new State(stateCount++, digit);
                    digitState.addTransition(end);
                    start.addTransition(digitState);
                }

                // Push `[0-9]` NFA to the stack
                NFA digitNFA = new NFA(start, end);
                stack.push(digitNFA);

                // Handle `+` dynamically
                applyKleeneStar(); // Apply `*` to `[0-9]`
                NFA kleeneStarNFA = stack.pop(); // Get the `[0-9]*` NFA

                stack.push(digitNFA);  // Push `[0-9]`
                stack.push(kleeneStarNFA); // Push `[0-9]*`
                applyConcatenation(); // Combine `[0-9]` and `[0-9]*` to form `[0-9]+`
            } else {
                // Create a basic NFA for any other character
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
            NFA unionNFA = applyUnion(nfa1, nfa2);
            localUnionStack.push(unionNFA);
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


    private NFA applyUnion(NFA nfa1, NFA nfa2) {
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');

        start.addTransition(nfa1.start);
        start.addTransition(nfa2.start);
        nfa1.end.addTransition(end);
        nfa2.end.addTransition(end);

        nfa1.end.isFinal = false;
        nfa2.end.isFinal = false;
        end.isFinal = true;

        return new NFA(start, end);
    }




    private int handleCharacterClass(String regex, int index) {
        index++; // Move past '['
        State start = new State(stateCount++, 'ε'); // Start state
        State end = new State(stateCount++, 'ε');   // Final state
        end.isFinal = true; // Mark end state as final

        while (regex.charAt(index) != ']') {
            char startChar = regex.charAt(index);

            if (index + 2 < regex.length() && regex.charAt(index + 1) == '-') {
                // Handle range like a-z
                char endChar = regex.charAt(index + 2);
                for (char c = startChar; c <= endChar; c++) {
                    State charState = new State(stateCount++, c);
                    charState.addTransition(end);
                    start.addTransition(charState);
                }
                index += 3; // Move past 'startChar-endChar'
            } else {
                // Handle single character
                State charState = new State(stateCount++, startChar);
                charState.addTransition(end);
                start.addTransition(charState);
                index++; // Move past the single character
            }
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
        end.isFinal = true; // Ensure the end state is marked as final

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
    private void applyOptional() {
        NFA nfa = stack.pop();
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');
        start.addTransition(nfa.start); // Transition to the NFA
        start.addTransition(end);      // Transition to skip the NFA
        nfa.end.addTransition(end);

        nfa.end.isFinal = false;
        end.isFinal = true;

        stack.push(new NFA(start, end));
    }


}