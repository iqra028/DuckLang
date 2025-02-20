import java.util.*;

public class DuckLangRe {
    private static final String DUCK_ID = "[a-z]+";
    private static final String DUCK_BOOL = "QUACK_QUACK|Quaak";
    private static final String WEBBED_FEET = "[0-9]+";
    private static final String DUCK_POND_DEPTH = "[0-9]+[\\.0-9]*";
    private static final String FEATHER_CODE = "[a-zA-Z0-9]";
    private static final String DUCK_MATH = "ADD|SUB|MUL|DIV|MOD|POW";
    private static final String DUCK_GLOBAL = "[Nest_Egg a-z]+";
    private static final String DUCK_LOCAL = "[a-z]+";
    private static final String DUCK_COMMENT_SINGLE = "~QUACK.*";
    private static final String DUCK_COMMENT_MULTI = "{.*}";
    private static final String DUCK_KEYWORD = "QUACK_PRINT|QUACK_INPUT";

    public DuckLangRe() {
    }
    private static void epsilonClosure(State state, Set<State> states) {
        if (state != null && !states.contains(state)) {
            states.add(state);
            for (State next : state.nextStates) {
                if (next.transition == 'ε') {
                    epsilonClosure(next, states);
                }
            }
        }
    }
    public static boolean testNFA(NFA nfa, String input) {
        Set<State> currentStates = new HashSet<>();
        epsilonClosure(nfa.start, currentStates);

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            Set<State> nextStates = new HashSet<>();

            for (State state : currentStates) {
                for (State next : state.nextStates) {
                    if (next.transition == c) {
                        epsilonClosure(next, nextStates);
                    }
                }
            }

            if (nextStates.isEmpty()) {
                return false;
            }
            currentStates = nextStates;
        }

        return currentStates.stream().anyMatch(state -> state.isFinal);
    }




    public static void main(String[] args) {
        String regex = "[a-z]+";
        ThompsonConstruction tc = new ThompsonConstruction();
        NFA nfa = tc.reToNFA(regex);
        Map<Integer, Map<Character, Set<Integer>>> stateTable = tc.createStateTable(nfa);

        // Print the state table
        tc.printStateTable(stateTable);

        System.out.println("NFA construction completed.");
        String input = "bcd";
        boolean result = testNFA(nfa, input);
        System.out.println("Input '" + input + "' matches regex: " + result);
        ThompsonConstruction tc1 = new ThompsonConstruction();
        new ThompsonConstruction();
        NFA webbedfeet = tc1.reToNFA("-?[0-9]+");
        Map<Integer, Map<Character, Set<Integer>>> stateTable1 = tc.createStateTable(webbedfeet);

        // Print the state table
        tc.printStateTable(stateTable1);
        String input4 = "-1";
        System.out.println("Input '" + input4 + "' matches: " + testNFA(webbedfeet, input4));
        NFA dd = tc1.reToNFA("[0-9]+[\\.0-9]*");
        String input5 = "1.1";
        System.out.println("Input '" + input5 + "' matches: " + testNFA(dd, input5));
        NFA chartype = tc1.reToNFA("[a-zA-Z0-9]");
        String input6 = "1";
        System.out.println("Input '" + input6 + "' matches: " + testNFA(chartype, input6));
        NFA op = tc1.reToNFA("[Nest_Egg a-z]+");
        String input7 = "Nest_Egg abc";
        System.out.println("Input '" + input7 + "' matches: " + testNFA(op, input7));
        NFA ops = tc1.reToNFA("[a-z]+");
        String input8 = "quack";
        System.out.println("Input '" + input8 + "' matches: " + testNFA(ops, input8));
        String input1 = "QUACK_QUACK";
        String input3 = "Quaak";
        NFA duckBoolNFA = tc1.reToNFA("QUACK_QUACK|Quaak");
        System.out.println("Input '" + input1 + "' matches: " + testNFA(duckBoolNFA, input1));
        System.out.println("Input '" + input3 + "' matches: " + testNFA(duckBoolNFA, input3));
        ThompsonConstruction thompsonMethod = new ThompsonConstruction();
        NFA nfas = thompsonMethod.reToNFA("ADD|SUB|MUL|DIV|MOD|POW");
        String input9 = "POW";
        System.out.println("Input '" + input9 + "' matches: " + testNFA(nfas, input9));

        NFA reservewords = tc.reToNFA("QUACK_PRINT|QUACK_INPUT");
        tc.printNFA(reservewords);
        String input11 = "QUACK_PRINT";
        System.out.println("Input '" + input11 + "' matches: " + testNFA(reservewords, input11));



        NFA singleLineComment = tc.reToNFA("~QUACK.*"); // Corrected regex
        System.out.println("Single-line comment NFA:");
        tc.printNFA(singleLineComment);
        String input10 = "~QUACK  hello its iqra here 123\n";
        System.out.println("Input '" + input10 + "' matches: " + testNFA(singleLineComment, input10));
        Map<Integer, Map<Character, Set<Integer>>> stateTable2 = tc.createStateTable(singleLineComment);

        // Print the state table
        tc.printStateTable(stateTable2);
        ThompsonConstruction tc2 = new ThompsonConstruction();
        NFA singleLineComment1 = tc2.reToNFA("~QUACK.*");
        System.out.println("\nTesting single-line comments:");

        // Test cases for single-line comments
        String[] testCases = {
                "~QUACK hello its iqra here 123",           // Should match
                "~QUACK",                                   // Should match
                "~QUACK hello its iqra here 123\n",        // Should not match
                "~QUACK test\nmore text",                  // Should not match
        };

        for (String test : testCases) {
            System.out.println("Result: " + testNFA(singleLineComment1, test));
        }

        NFA multipleLineComment = tc.reToNFA(DUCK_COMMENT_MULTI); // Corrected regex
        System.out.println("Single-line comment NFA:");
        tc.printNFA(multipleLineComment);
        String input13 = "{helo its iqra here 123 \n what to do}";
        System.out.println("Input '" + input13 + "' matches: " + testNFA(multipleLineComment, input13));
    }
}
