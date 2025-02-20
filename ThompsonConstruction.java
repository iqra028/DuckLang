import java.util.*;

public class ThompsonConstruction {
    private int stateCount = 0;
    private Stack<NFA> stack = new Stack();

    public ThompsonConstruction() {
    }

    public Map<Integer, Map<Character, Set<Integer>>> createStateTable(NFA nfa) {
        Map<Integer, Map<Character, Set<Integer>>> table = new HashMap<>();
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();
        queue.add(nfa.start);

        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            if (visited.contains(currentState)) {
                continue;
            }
            visited.add(currentState);

            Map<Character, Set<Integer>> transitions = new HashMap<>();
            for (State nextState : currentState.nextStates) {
                char transitionChar = nextState.transition;
                transitions.computeIfAbsent(transitionChar, k -> new HashSet<>()).add(nextState.id);
                queue.add(nextState);
            }
            table.put(currentState.id, transitions);
        }
        return table;
    }

    public void printStateTable(Map<Integer, Map<Character, Set<Integer>>> table) {
        System.out.println("State Table:");

        // 1. Determine all unique input symbols
        Set<Character> inputSymbols = new TreeSet<>(); // Use TreeSet for sorted output
        for (Map<Character, Set<Integer>> transitions : table.values()) {
            inputSymbols.addAll(transitions.keySet());
        }

        // 2. Print header row
        System.out.print("State\t\t\t");
        for (char symbol : inputSymbols) {
            System.out.print(symbol + "\t\t\t");
        }
        System.out.println();

        // 3. Print table rows
        for (int stateId : new TreeSet<>(table.keySet())) { // Use TreeSet for sorted state IDs
            System.out.print(stateId + "\t\t\t");
            Map<Character, Set<Integer>> transitions = table.get(stateId);
            for (char symbol : inputSymbols) {
                Set<Integer> nextStates = transitions.getOrDefault(symbol, Collections.emptySet());
                System.out.print(nextStates + "\t\t\t\t");
            }
            System.out.println();
        }
    }

    private NFA createWildcardNFA() {
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        State middle = new State(this.stateCount++, '.');
        start.addTransition(middle);
        middle.addTransition(end);
        end.isFinal = true;
        return new NFA(start, end);
    }

    public NFA createMultilineCommentNFA() {
        NFA openBraceNFA = this.createBasicNFA('{');
        NFA anyCharNFA = this.createBasicNFAForAnyExcept('}');
        this.stack.push(anyCharNFA);
        this.applyKleeneStar();
        NFA loopAnyCharNFA = (NFA) this.stack.pop();
        NFA closeBraceNFA = this.createBasicNFA('}');
        this.stack.push(openBraceNFA);
        this.stack.push(closeBraceNFA);
        this.applyConcatenation();
        NFA multilineCommentNFA = (NFA) this.stack.pop();
        this.stack.push(multilineCommentNFA);
        this.stack.push(closeBraceNFA);
        this.applyConcatenation();
        return (NFA) this.stack.pop();
    }

    private NFA createBasicNFAForAnyExcept(char excludeChar) {
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');

        for (char c = 0; c < 128; ++c) {
            if (c != excludeChar) {
                State middle = new State(this.stateCount++, c);
                start.addTransition(middle);
                middle.addTransition(end);
            }
        }

        end.isFinal = true;
        return new NFA(start, end);
    }

    public NFA createAnyUntilNewlineNFA() {
        State start = new State(stateCount++, 'ε');
        State end = new State(stateCount++, 'ε');
        end.isFinal = true;

        // Create transitions for all characters except newline
        for (char c = 0; c < 128; c++) {
            if (c != '\n') {
                State charState = new State(stateCount++, c);
                start.addTransition(charState);
                charState.addTransition(end);
            }
        }

        return new NFA(start, end);
    }

    public NFA reToNFA(String regex) {
        String trimmedRegex = regex.trim();
        stack.clear();
        Stack<NFA> localUnionStack = new Stack<>();
        NFA prevNFA = null;

        for (int i = 0; i < trimmedRegex.length(); ++i) {
            char c = trimmedRegex.charAt(i);

            if (trimmedRegex.startsWith("~QUACK", i)) {
                // Create the NFA for "~QUACK" directly
                NFA quack = createBasicNFA('~');
                for (int j = 1; j < "~QUACK".length(); j++) {
                    NFA nextChar = createBasicNFA("~QUACK".charAt(j));
                    stack.push(quack);
                    stack.push(nextChar);
                    applyConcatenation();
                    quack = stack.pop();
                }

                NFA anyUntilNewline = createAnyUntilNewlineNFA();
                stack.push(anyUntilNewline);
                applyKleeneStar();
                NFA anyUntilNewlineKleene = stack.pop();
                stack.push(quack);
                stack.push(anyUntilNewlineKleene);
                applyConcatenation();
                prevNFA = stack.pop();
                i += "~QUACK".length() - 1;
                break;
            } else if (c == '.') {
                State start = new State(stateCount++, 'ε');
                State end = new State(stateCount++, 'ε');
                end.isFinal = true;

                for (char ch = 0; ch < 128; ++ch) {
                    State charState = new State(stateCount++, ch);
                    start.addTransition(charState);
                    charState.addTransition(end);
                }

                NFA anyChar = new NFA(start, end);
                stack.push(anyChar);
            }
            else if (c == '*') {
                if (!stack.isEmpty()) {
                    applyKleeneStar();
                }
            }
            else if (c == '{') {
                NFA openBrace = createBasicNFA('{');
                NFA anyCharExceptCloseBrace = createBasicNFAForAnyExcept('}');
                stack.push(anyCharExceptCloseBrace);
                applyKleeneStar();
                NFA loopAnyChar = stack.pop();
                NFA closeBrace = createBasicNFA('}');
                stack.push(openBrace);
                stack.push(loopAnyChar);
                applyConcatenation();
                stack.push(closeBrace);
                applyConcatenation();
                prevNFA = stack.pop();
                int braceCount = 1;
                i++;
                while (i < trimmedRegex.length() && braceCount > 0) {
                    if (trimmedRegex.charAt(i) == '{') {
                        braceCount++;
                    } else if (trimmedRegex.charAt(i) == '}') {
                        braceCount--;
                    }
                    i++;
                }
                i--;
            } else if (c == '|') {
                if (prevNFA != null) {
                    localUnionStack.push(prevNFA);
                    prevNFA = null;
                }
            } else if (c == '*') {
                if (!stack.isEmpty()) {
                    applyKleeneStar();
                }
            } else if (c == '+') {
                if (!stack.isEmpty()) {
                    applyPlusOperator();
                }
            } else if (c == '[') {
                i = handleCharacterClass(trimmedRegex, i);
            } else if (c == '-') {
                if (i + 1 < trimmedRegex.length() && trimmedRegex.charAt(i + 1) == '?') {
                    stack.push(createBasicNFA('-'));
                    applyOptional();
                    i++;
                } else {
                    stack.push(createBasicNFA('-'));
                }
            } else if (c == '\\') {
                ++i;
                NFA wildcardNFA = createBasicNFA(trimmedRegex.charAt(i));
                stack.push(wildcardNFA);
            } else if (c != '.') {
                NFA wildcardNFA = createBasicNFA(c);
                if (prevNFA != null) {
                    stack.push(prevNFA);
                    stack.push(wildcardNFA);
                    applyConcatenation();
                    prevNFA = stack.pop();
                } else {
                    prevNFA = wildcardNFA;
                }
            } else if (i + 1 < trimmedRegex.length() && trimmedRegex.charAt(i + 1) == '*') {
                State start = new State(stateCount++, 'ε');
                State end = new State(stateCount++, 'ε');
                end.isFinal = true;

                for (char ch = 0; ch < 128; ++ch) {
                    if (ch != '\n') {
                        State charState = new State(stateCount++, ch);
                        start.addTransition(charState);
                        charState.addTransition(start);
                    }
                }

                start.addTransition(end);
                NFA anyUntilNewlineNFA = new NFA(start, end);
                stack.push(anyUntilNewlineNFA);
                ++i;
            } else {
                NFA wildcardNFA = createWildcardNFA();
                stack.push(wildcardNFA);
            }
        }

        if (prevNFA != null) {
            localUnionStack.push(prevNFA);
        }

        while (localUnionStack.size() > 1) {
            NFA nfa2 = localUnionStack.pop();
            NFA nfa1 = localUnionStack.pop();
            localUnionStack.push(applyUnion(nfa1, nfa2));
        }

        if (!localUnionStack.isEmpty()) {
            stack.push(localUnionStack.pop());
        }

        while (stack.size() > 1) {
            applyConcatenation();
        }

        if (stack.isEmpty()) {
            System.out.println("No NFA constructed for the regex: " + regex);
            return null;
        } else {
            return stack.pop();
        }
    }

        private NFA applyUnion(NFA nfa1, NFA nfa2) {
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
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
        ++index;
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        end.isFinal = true;

        while(true) {
            while(regex.charAt(index) != ']') {
                char startChar = regex.charAt(index);
                if (index + 2 < regex.length() && regex.charAt(index + 1) == '-') {
                    char endChar = regex.charAt(index + 2);

                    for(char c = startChar; c <= endChar; ++c) {
                        State charState = new State(this.stateCount++, c);
                        charState.addTransition(end);
                        start.addTransition(charState);
                    }

                    index += 3;
                } else {
                    State charState = new State(this.stateCount++, startChar);
                    charState.addTransition(end);
                    start.addTransition(charState);
                    ++index;
                }
            }

            this.stack.push(new NFA(start, end));
            return index;
        }
    }

    private NFA createBasicNFA(char c) {
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        State middle = new State(this.stateCount++, c);
        start.addTransition(middle);
        middle.addTransition(end);
        end.isFinal = true;
        return new NFA(start, end);
    }

    private void applyConcatenation() {
        NFA nfa2 = (NFA)this.stack.pop();
        NFA nfa1 = (NFA)this.stack.pop();
        nfa1.end.addTransition(nfa2.start);
        nfa1.end.isFinal = false;
        this.stack.push(new NFA(nfa1.start, nfa2.end));
    }

    private void applyKleeneStar() {
        NFA nfa = (NFA)this.stack.pop();
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        start.addTransition(nfa.start);
        start.addTransition(end);
        nfa.end.addTransition(nfa.start);
        nfa.end.addTransition(end);
        nfa.end.isFinal = false;
        end.isFinal = true;
        this.stack.push(new NFA(start, end));
    }

    private void applyPlusOperator() {
        NFA nfa = (NFA)this.stack.pop();
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        start.addTransition(nfa.start);
        nfa.end.addTransition(nfa.start);
        nfa.end.addTransition(end);
        nfa.end.isFinal = false;
        end.isFinal = true;
        this.stack.push(new NFA(start, end));
    }

    public void printNFA(NFA nfa) {
        Queue<State> queue = new LinkedList();
        Set<State> visited = new HashSet();
        queue.add(nfa.start);

        while(true) {
            State state;
            do {
                if (queue.isEmpty()) {
                    return;
                }

                state = (State)queue.poll();
            } while(visited.contains(state));

            visited.add(state);
            System.out.print("State " + state.id);
            if (state.isFinal) {
                System.out.print(" (Final)");
            }

            System.out.println();
            Iterator var5 = state.nextStates.iterator();

            while(var5.hasNext()) {
                State nextState = (State)var5.next();
                System.out.println("  -(" + nextState.transition + ")-> State " + nextState.id);
                queue.add(nextState);
            }
        }
    }

    private void applyOptional() {
        NFA nfa = (NFA)this.stack.pop();
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        start.addTransition(nfa.start);
        start.addTransition(end);
        nfa.end.addTransition(end);
        nfa.end.isFinal = false;
        end.isFinal = true;
        this.stack.push(new NFA(start, end));
    }

}
