import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ThompsonConstruction {
    private int stateCount = 0;
    private Stack<NFA> stack = new Stack<>();
    private List<Integer> unionPositions = new ArrayList<>();
    private List<NFA> nfaList = new ArrayList<>();

    private State createState() {
        State state = new State(stateCount++);
        return state;
    }
    public NFA reToNFA(String regex) {
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '*':
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid regex: no NFA to apply Kleene star.");
                    }
                    applyKleeneStar();
                    break;
                case '|':
                    // Push a special marker onto the stack to indicate the union operator
                    stack.push(null);
                    break;
                default:
                    // Create an NFA for the single character and push it onto the stack
                    stack.push(singleCharNFA(c));
                    break;
            }
        }

        // Process the stack to handle unions and concatenations
        Stack<NFA> tempStack = new Stack<>();
        while (!stack.isEmpty()) {
            NFA nfa = stack.pop();
            if (nfa == null) {
                // Union operator encountered
                if (tempStack.size() < 2) {
                    throw new IllegalArgumentException("Invalid regex: not enough NFAs for union operation.");
                }
                NFA rightNFA = tempStack.pop();
                NFA leftNFA = tempStack.pop();
                tempStack.push(applyUnion(leftNFA, rightNFA));
            } else {
                tempStack.push(nfa);
            }
        }

        // Apply concatenation to any remaining NFAs on the stack
        while (tempStack.size() > 1) {
            NFA rightNFA = tempStack.pop();
            NFA leftNFA = tempStack.pop();
            tempStack.push(applyConcatenation(leftNFA, rightNFA));
        }

        // At the end, there should be exactly one NFA left
        if (tempStack.size() != 1) {
            throw new IllegalStateException("Invalid regex: stack should contain exactly one NFA at the end.");
        }
        return tempStack.pop();
    }

    private NFA singleCharNFA(char c) {
        State start = createState();
        State end = createState();
        start.addTransition(c, end);
        return new NFA(start, end, Arrays.asList(start, end));
    }
    private NFA applyConcatenation(NFA left, NFA right) {
        // Connect the end of the left NFA to the start of the right NFA
        left.end.addTransition('\0', right.start);

        // Combine the states of the left and right NFAs
        List<State> states = new ArrayList<>();
        states.addAll(left.states);
        states.addAll(right.states);

        // Return the new concatenated NFA
        return new NFA(left.start, right.end, states);
    }
    private void applyKleeneStar() {
        if (nfaList.isEmpty()) {
            throw new IllegalArgumentException("Invalid regex: no NFA to apply Kleene star.");
        }
        NFA nfa = nfaList.get(nfaList.size() - 1); // Get the last NFA
        State start = createState();
        State end = createState();

        // Add epsilon transitions for Kleene star
        start.addTransition('\0', nfa.start); // Start to NFA start
        start.addTransition('\0', end);       // Start to end (zero occurrences)
        nfa.end.addTransition('\0', nfa.start); // NFA end to NFA start (loop)
        nfa.end.addTransition('\0', end);       // NFA end to end

        // Create the new NFA and replace the old one
        List<State> states = new ArrayList<>();
        states.add(start);
        states.add(end);
        states.addAll(nfa.states);
        nfaList.set(nfaList.size() - 1, new NFA(start, end, states));
    }

    private NFA applyUnion(NFA left, NFA right) {
        State start = createState();
        State end = createState();
        start.addTransition('\0', left.start);
        start.addTransition('\0', right.start);
        left.end.addTransition('\0', end);
        right.end.addTransition('\0', end);

        // Combine the states of the left and right NFAs
        List<State> states = new ArrayList<>();
        states.add(start);
        states.add(end);
        states.addAll(left.states);
        states.addAll(right.states);

        return new NFA(start, end, states);
    }
}