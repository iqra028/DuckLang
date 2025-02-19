import java.util.HashSet;
import java.util.Set;

public class DuckLangRe {

    // Identifiers (Duck Variables & Functions) - only lowercase letters
    private static final String DUCK_ID = "[a-z]+";

    // Boolean (QUACK QUACK for true, Quaak for false)
    private static final String DUCK_BOOL = "QUACK_QUACK|Quaak";

    // Integer (Webbed Feet Count - Whole Numbers)
    private static final String WEBBED_FEET = "-?[0-9]+";

    // Decimal (Duck Pond Depth - Floating-point numbers)
    private static final String DUCK_POND_DEPTH = "-?[0-9]+\\.[0-9]{1,5}";

    // Character (Feather Code - Single Letter in Quotes)
    private static final String FEATHER_CODE = "'[a-zA-Z0-9]'";

    // Arithmetic Operations (Duck Math)
    private static final String DUCK_MATH = "[+\\-*/%]|\\^";

    // Constants (Eggs - Immutable Values)
    private static final String DUCK_GLOBAL = "Nest_Egg\\s+" + DUCK_ID;  // Global constants
    private static final String DUCK_LOCAL = DUCK_ID; // Local constants

    // Strings (Duck Song - Text Enclosed in Quotes)
    private static final String DUCK_SONG = "\"([^\"\\\\](\\\\.[^\"\\\\])*)\"";

    // Comments (Quack Notes)
    private static final String DUCK_COMMENT_SINGLE = "~QUACK.*";  // Single-line comments
    private static final String DUCK_COMMENT_MULTI = "\\{[\\s\\S]*?\\}";  // Multi-line comments

    // Whitespace Handling (Ignored Extra Spaces)
    private static final String DUCK_WHITESPACE = "\\s+";

    // Keywords (Control Statements, I/O, etc.)
    private static final String DUCK_KEYWORD = "\\b(QUACK_PRINT|QUACK_INPUT|Duck_If|Duck_Else|Duck_While|Duck_For|Duck_Return|Duck_Break|Duck_Continue)\\b";
    public static boolean testNFA(NFA nfa, String input) {
        Set<State> currentStates = new HashSet<>();
        epsilonClosure(nfa.start, currentStates);

        for (char c : input.toCharArray()) {
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                for (State next : state.nextStates) {
                    if (next.transition == c) {
                        epsilonClosure(next, nextStates);
                    }
                }
            }
            currentStates = nextStates;
        }

        for (State state : currentStates) {
            if (state.isFinal) return true;
        }
        return false;
    }

    private static void epsilonClosure(State state, Set<State> states) {
        if (state == null || states.contains(state)) return;
        states.add(state);
        for (State next : state.nextStates) {
            if (next.transition == 'ε') {
                epsilonClosure(next, states);
            }
        }
    }



    public static void main(String[] args) {
        String regex = "[a-z]+"; // Example regex for DUCK_ID
        ThompsonConstruction tc = new ThompsonConstruction();
        NFA nfa = tc.reToNFA(regex);
        System.out.println("NFA construction completed.");
        tc.printNFA(nfa);

        // Test the NFA with some input
        String input = "bcd";
        boolean result = testNFA(nfa, input);
        System.out.println("Input '" + input + "' matches regex: " + result);
        ThompsonConstruction tc1 = new ThompsonConstruction();
        String input1 = "QUACK_QUACK";
        String input2 = "Quaak";
        String input3 = "QUACK"; // Invalid
        NFA duckBoolNFA = tc1.reToNFA(DUCK_BOOL);
        tc1.printNFA(duckBoolNFA);
        System.out.println("Input '" + input1 + "' matches: " + testNFA(duckBoolNFA, input1));
        System.out.println("Input '" + input2 + "' matches: " + testNFA(duckBoolNFA, input2));
        System.out.println("Input '" + input3 + "' matches: " + testNFA(duckBoolNFA, input3));


    }
}