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

    public static boolean testNFA(NFA nfa, String input) {
        Set<State> currentStates = new HashSet<>();
        epsilonClosure(nfa.start, currentStates);
        char[] var3 = input.toCharArray();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            Set<State> nextStates = new HashSet<>();
            Iterator var8 = currentStates.iterator();

            while (var8.hasNext()) {
                State state = (State) var8.next();
                Iterator var10 = state.nextStates.iterator();

                while (var10.hasNext()) {
                    State next = (State) var10.next();
                    if (next.transition == c || next.transition == '.') { // Handle any character
                        epsilonClosure(next, nextStates);
                    }
                }
            }

            currentStates = nextStates;
        }

        Iterator var12 = currentStates.iterator();

        State state;
        do {
            if (!var12.hasNext()) {
                return false;
            }

            state = (State) var12.next();
        } while (!state.isFinal);

        return true;
    }

    private static void epsilonClosure(State state, Set<State> states) {
        if (state != null && !states.contains(state)) {
            states.add(state);
            Iterator var2 = state.nextStates.iterator();

            while(var2.hasNext()) {
                State next = (State)var2.next();
                if (next.transition == 949) {
                    epsilonClosure(next, states);
                }
            }

        }
    }

    public static boolean simulateNFA(State startState, String input) {
        List<State> currentStates = new ArrayList();
        currentStates.add(startState);
        char[] var3 = input.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            List<State> nextStates = new ArrayList();
            Iterator var8 = currentStates.iterator();

            label43:
            while(var8.hasNext()) {
                State state = (State)var8.next();
                Iterator var10 = state.nextStates.iterator();

                while(true) {
                    State nextState;
                    do {
                        if (!var10.hasNext()) {
                            continue label43;
                        }

                        nextState = (State)var10.next();
                    } while(nextState.transition != c && nextState.transition != 0);

                    nextStates.add(nextState);
                }
            }

            currentStates = nextStates;
        }

        Iterator var12 = currentStates.iterator();

        State state;
        do {
            if (!var12.hasNext()) {
                return false;
            }

            state = (State)var12.next();
        } while(!state.isFinal);

        return true;
    }

    public static boolean isDuckComment(String line) {
        return line.trim().startsWith("~QUACK");
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

        //NFA multipleLineComment = tc.reToNFA(DUCK_COMMENT_MULTI); // Corrected regex
        //System.out.println("Single-line comment NFA:");
        //tc.printNFA(multipleLineComment);
        //String input13 = "{helo its iqra here 123 \n what to do}";
        //System.out.println("Input '" + input13 + "' matches: " + testNFA(multipleLineComment, input13));
    }
}
