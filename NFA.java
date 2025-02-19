import java.util.*;

public class NFA {
    State start;
    State end;
    List<State> states;

    NFA(State start, State end, List<State> states) {
        this.start = start;
        this.end = end;
        this.states = states;
    }
    void printNFA() {
        boolean[] visited = new boolean[states.size()];
        Queue<State> queue = new LinkedList<>();
        queue.add(start);
        visited[start.id] = true;

        while (!queue.isEmpty()) {
            State current = queue.poll();
            for (int i = 0; i < 256; i++) {
                for (State next : current.transitions[i]) {
                    if (i == '\0') {
                        System.out.println("State " + current.id + " --(ε)--> State " + next.id);
                    } else {
                        System.out.println("State " + current.id + " --(" + (char) i + ")--> State " + next.id);
                    }
                    if (!visited[next.id]) {
                        visited[next.id] = true;
                        queue.add(next);
                    }
                }
            }
        }
    }
}