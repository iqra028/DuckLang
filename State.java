

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class State {
    int id;
    List<State> nextStates;
    public char transition;
    boolean isFinal;

    public State(int id, char transition) {
        this.id = id;
        this.transition = transition;
        this.nextStates = new ArrayList();
        this.isFinal = false;
    }

    public void addTransition(State nextState) {
        this.nextStates.add(nextState);
    }

    public void addGeneralTransition(State target, Predicate<Character> condition) {
        for(char c = 0; c < 128; ++c) {
            if (condition.test(c)) {
                target.transition = c;
                this.addTransition(target);
            }
        }

    }
}
