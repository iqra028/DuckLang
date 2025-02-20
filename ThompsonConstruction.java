import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class ThompsonConstruction {
    private int stateCount = 0;
    private Stack<NFA> stack = new Stack();

    public ThompsonConstruction() {
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
        NFA loopAnyCharNFA = (NFA)this.stack.pop();
        NFA closeBraceNFA = this.createBasicNFA('}');
        this.stack.push(openBraceNFA);
        this.stack.push(closeBraceNFA);
        this.applyConcatenation();
        NFA multilineCommentNFA = (NFA)this.stack.pop();
        this.stack.push(multilineCommentNFA);
        this.stack.push(closeBraceNFA);
        this.applyConcatenation();
        return (NFA)this.stack.pop();
    }

    private NFA createBasicNFAForAnyExcept(char excludeChar) {
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');

        for(char c = 0; c < 128; ++c) {
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
        State start = new State(this.stateCount++, 'ε');
        State end = new State(this.stateCount++, 'ε');
        end.isFinal = true;

        for(char c = 0; c < 128; ++c) {
            if (c != '\n') {
                State charState = new State(this.stateCount++, c);
                start.addTransition(charState);
                charState.addTransition(end);
            }
        }

        return new NFA(start, end);
    }

    public NFA reToNFA(String regex) {
        String trimmedRegex = regex.trim();
        this.stack.clear();
        Stack<NFA> localUnionStack = new Stack();
        NFA prevNFA = null;

        NFA wildcardNFA;
        for(int i = 0; i < trimmedRegex.length(); ++i) {
            char c = trimmedRegex.charAt(i);
            if (c == '|') {
                if (prevNFA != null) {
                    localUnionStack.push(prevNFA);
                    prevNFA = null;
                }
            } else if (c == '*') {
                if (!this.stack.isEmpty()) {
                    this.applyKleeneStar();
                }
            } else if (c == '+') {
                if (!this.stack.isEmpty()) {
                    this.applyPlusOperator();
                }
            } else if (c == '[') {
                i = this.handleCharacterClass(trimmedRegex, i);
            } else if (c == '-') {
                this.stack.push(this.createBasicNFA('-'));
                if (i + 1 < trimmedRegex.length() && trimmedRegex.charAt(i + 1) == '?') {
                    this.applyOptional();
                    i += 2;
                }
            } else if (c == '\\') {
                ++i;
                c = trimmedRegex.charAt(i);
                wildcardNFA = this.createBasicNFA(c);
                this.stack.push(wildcardNFA);
            } else if (c != '.') {
                wildcardNFA = this.createBasicNFA(c);
                if (prevNFA != null) {
                    this.stack.push(prevNFA);
                    this.stack.push(wildcardNFA);
                    this.applyConcatenation();
                    prevNFA = (NFA)this.stack.pop();
                } else {
                    prevNFA = wildcardNFA;
                }
            } else if (i + 1 < trimmedRegex.length() && trimmedRegex.charAt(i + 1) == '*') {
                System.out.println("oooooooooooooooooooooooooooooo");
                State start = new State(this.stateCount++, 'ε');
                State end = new State(this.stateCount++, 'ε');
                end.isFinal = true;

                for(char ch = 0; ch < 128; ++ch) {
                    if (ch != '\n') {
                        State charState = new State(this.stateCount++, ch);
                        start.addTransition(charState);
                        charState.addTransition(start);
                    }
                }

                start.addTransition(end);
                NFA anyUntilNewlineNFA = new NFA(start, end);
                this.stack.push(anyUntilNewlineNFA);
                ++i;
            } else {
                wildcardNFA = this.createWildcardNFA();
                this.stack.push(wildcardNFA);
            }
        }

        if (prevNFA != null) {
            localUnionStack.push(prevNFA);
        }

        while(localUnionStack.size() > 1) {
            NFA nfa2 = (NFA)localUnionStack.pop();
            NFA nfa1 = (NFA)localUnionStack.pop();
            wildcardNFA = this.applyUnion(nfa1, nfa2);
            localUnionStack.push(wildcardNFA);
        }

        if (!localUnionStack.isEmpty()) {
            this.stack.push((NFA)localUnionStack.pop());
        }

        while(this.stack.size() > 1) {
            this.applyConcatenation();
        }

        if (this.stack.isEmpty()) {
            System.out.println("No NFA constructed for the regex: " + regex);
            return null;
        } else {
            return (NFA)this.stack.pop();
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
