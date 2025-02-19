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
    private static final String DUCK_POND_DEPTH = "[0-9]+[\\.0-9]*";

    // Character (Feather Code - Single Letter in Quotes)
    private static final String FEATHER_CODE = "[a-zA-Z0-9]";

    // Arithmetic Operations (Duck Math)
    private static final String DUCK_MATH = "ADD|MIN|MUL";

    // Constants (Eggs - Immutable Values)
    private static final String DUCK_GLOBAL = "Nest_Egg+" + DUCK_ID;  // Global constants
    private static final String DUCK_LOCAL = DUCK_ID; // Local constants

    // Comments (Quack Notes)
    private static final String DUCK_COMMENT_SINGLE = "~QUACK.*";  // Single-line comments
    private static final String DUCK_COMMENT_MULTI = "\\{[\\s\\S]*?\\}";  // Multi-line comments

    // Whitespace Handling (Ignored Extra Spaces)
    private static final String DUCK_WHITESPACE = "\\s+";

    // Keywords (Control Statements, I/O, etc.)
    private static final String DUCK_KEYWORD = "\\b(QUACK_PRINT|QUACK_INPUT)\\b";
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
        //tc.printNFA(nfa);

        // Test the NFA with some input
        String input = "bcd";
        boolean result = testNFA(nfa, input);
        System.out.println("Input '" + input + "' matches regex: " + result);
        ThompsonConstruction tc1 = new ThompsonConstruction();
        String input1 = "QUACK_QUACK";
        String input3 = "Quaak"; // Invalid
       NFA duckBoolNFA = tc1.reToNFA(DUCK_BOOL);
        //tc1.printNFA(duckBoolNFA);
        System.out.println("Input '" + input1 + "' matches: " + testNFA(duckBoolNFA, input1));
        System.out.println("Input '" + input3 + "' matches: " + testNFA(duckBoolNFA, input3));

        ThompsonConstruction thompson = new ThompsonConstruction();
        NFA webbedfeet = tc1.reToNFA(WEBBED_FEET);
        //thompson.printNFA(nfa);
        String input4 = "1"; // Invalid
        //tc1.printNFA(webbedfeet);
        System.out.println("Input '" + input4 + "' matches: " + testNFA(webbedfeet, input4));

        NFA dd = tc1.reToNFA(DUCK_POND_DEPTH);

        //tc1.printNFA(dd);
        String input5 = "11"; // Invalid
        //tc1.printNFA(dd);
        System.out.println("Input '" + input5 + "' matches: " + testNFA(dd, input5));

        NFA chartype = tc1.reToNFA(FEATHER_CODE);
        //tc1.printNFA(chartype);
        String input6 = "1"; // Valid input, should match
        //tc1.printNFA(chartype);
        System.out.println("Input '" + input6 + "' matches: " + testNFA(chartype, input6));


        NFA op = tc1.reToNFA(DUCK_GLOBAL);
        //tc1.printNFA(op);
        String input7 = "Nest_Egg Quack_123"; // Valid input, should match
        //tc1.printNFA(chartype);
        System.out.println("Input '" + input7 + "' matches: " + testNFA(op, input7));
        NFA ops = tc1.reToNFA(DUCK_LOCAL);
        //tc1.printNFA(op);
        String input8 = "Quack_123"; // Valid input, should match
        //tc1.printNFA(chartype);
        System.out.println("Input '" + input8 + "' matches: " + testNFA(ops, input8));
        NFA prints = tc1.reToNFA(DUCK_MATH);
        //tc1.printNFA(op);
        String input9 = "ADD"; // Valid input, should match
        tc1.printNFA(chartype);
        System.out.println("Input '" + input9 + "' matches: " + testNFA(prints, input8));
    }
}