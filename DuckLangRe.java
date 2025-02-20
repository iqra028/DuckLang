import java.util.*;

public class DuckLangRe {
    private static final String DUCK_ID = "[a-z]+";
    private static final String DUCK_BOOL = "QUACK_QUACK|Quaak";
    private static final String WEBBED_FEET = "-?[0-9]+";
    private static final String DUCK_POND_DEPTH = "[0-9]+[\\.0-9]*";
    private static final String FEATHER_CODE = "[a-zA-Z0-9]";
    private static final String DUCK_MATH = "ADD|SUB|MUL|DIV|MOD|POW";
    private static final String DUCK_GLOBAL = "[Nest_Egga-z]+";
    private static final String DUCK_LOCAL = "[a-z]+";
    private static final String DUCK_COMMENT_SINGLE = "~QUACK.*";
    private static final String DUCK_COMMENT_MULTI = "{.*}";
    private static final String DUCK_KEYWORD = "QUACK_PRINT|QUACK_INPUT|DUCK_INT|DUCK_BOOL|DUCK_STRING|DUCK_CHAR";

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
            ThompsonConstruction tc = new ThompsonConstruction();
            SubsetConstructionMethod converter = new SubsetConstructionMethod();
            DFAminimization minimizer = new DFAminimization();

            // Create a map to store the DFAs for each token type
            Map<TokenType, DFA> tokenDFAs = new HashMap<>();

            // Map of regex patterns to token types
            Map<String, TokenType> patterns = new LinkedHashMap<>();
            patterns.put(DUCK_ID, TokenType.DUCK_ID);
            patterns.put(DUCK_BOOL, TokenType.DUCK_BOOL);
            patterns.put(WEBBED_FEET, TokenType.WEBBED_FEET);
            patterns.put(DUCK_POND_DEPTH, TokenType.DUCK_POND_DEPTH);
            patterns.put(FEATHER_CODE, TokenType.FEATHER_CODE);
            patterns.put(DUCK_MATH, TokenType.DUCK_MATH);
            patterns.put(DUCK_GLOBAL, TokenType.DUCK_GLOBAL);
            patterns.put(DUCK_COMMENT_SINGLE, TokenType.DUCK_COMMENT_SINGLE);
            patterns.put(DUCK_COMMENT_MULTI, TokenType.DUCK_COMMENT_MULTI);
            patterns.put(DUCK_KEYWORD, TokenType.DUCK_KEYWORD);

            // Convert each regex to DFA and store in tokenDFAs
            for (Map.Entry<String, TokenType> entry : patterns.entrySet()) {
                String regex = entry.getKey();
                TokenType type = entry.getValue();

                // Convert regex to NFA
                NFA nfa = tc.reToNFA(regex);

                // Convert NFA to DFA
                DFA dfa = converter.conversion(nfa);

                // Minimize DFA
                DFA minimizedDFA = minimizer.minimization(dfa);

                // Store the minimized DFA
                tokenDFAs.put(type, minimizedDFA);
            }

            // Test the lexical analyzer with sample input
            String testInput = """
            QUACK_PRINT hello
            ~QUACK This is a comment
            Nest_Egg myvar
            QUACK_QUACK
            ADD 123 456.789
            {This is a
            multiline comment}
            """;

            System.out.println("Testing Lexical Analyzer with input:\n" + testInput);
            System.out.println("\nTokenizing...");

            LexicalAnalyzer analyzer = new LexicalAnalyzer(tokenDFAs, testInput);
            List<Token> tokens = analyzer.analyze();

            System.out.println("\nTokens found:");
            for (Token token : tokens) {
                if (token.getType() != TokenType.WHITESPACE) {
                    System.out.println(token);
                }
            }

        }

        private static boolean testDFA(DFA dfa, String input) {
            DFAState currentState = dfa.startState;
            for (char c : input.toCharArray()) {
                if (!currentState.transitions.containsKey(c)) {
                    return false;
                }
                currentState = currentState.transitions.get(c);
            }
            return currentState.isFinal;
        }
}
