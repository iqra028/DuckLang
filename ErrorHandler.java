import java.util.HashSet;
import java.util.*;

public class ErrorHandler {
    public static void reportError(int lineNumber, String message) {
        System.out.println("Error at line " + lineNumber + ": " + message);
    }

    public static void checkInvalidTokens(List<String> tokens, List<Character> alphabet, int lineNumber) {
        Set<Character> alphabetSet = new HashSet<>(alphabet); // O(1) lookup
        for (String token : tokens) {
            for (char c : token.toCharArray()) {
                if (!alphabetSet.contains(c)) {
                    reportError(lineNumber, "Invalid token: " + c);
                }
            }
        }
    }

    public static void checkUndefinedState(String state, List<String> states, int lineNumber) {
        if (!new HashSet<>(states).contains(state)) {
            reportError(lineNumber, "Undefined state: " + state);
        }
    }

    public static void checkTransition(String currentState, char symbol, String nextState,
                                       List<String> states, List<Character> alphabet, int lineNumber) {
        Set<String> stateSet = new HashSet<>(states);
        Set<Character> alphabetSet = new HashSet<>(alphabet);

        if (!stateSet.contains(currentState)) {
            reportError(lineNumber, "Transition error: Undefined current state " + currentState);
        }
        if (!alphabetSet.contains(symbol)) {
            reportError(lineNumber, "Transition error: Undefined symbol " + symbol);
        }
        if (!stateSet.contains(nextState)) {
            reportError(lineNumber, "Transition error: Undefined next state " + nextState);
        }
    }
}
