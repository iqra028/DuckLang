import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DuckLangRe {
    private static final String FEATHER_CODE = "[a-zA-Z0-9]";//char
    private static final String DUCK_ID = "[a-z]+";//varname
    private static final String DUCK_BOOL = "QUACK_QUACK|Quaak";//bool
    private static final String WEBBED_FEET = "-?[0-9]+";//int
    private static final String DUCK_POND_DEPTH = "[0-9]+[\\.0-9]*";//float
    private static final String DUCK_MATH = "ADD|SUB|MUL|DIV|MOD|POW";//ADD
    private static final String DUCK_GLOBAL = "Nest_Egg";
    private static final String DUCK_LOCAL = "[a-zA-Z0-9]+";
    private static final String DUCK_COMMENT_SINGLE = "~QUACK.*";
    private static final String DUCK_COMMENT_MULTI = "{.*}";
    private static final String DUCK_KEYWORD = "QUACK_PRINT|QUACK_INPUT|WEBBED_FEET|DUCK_BOOL|DUCK_POND_DEPTH|FEATHER_CODE";

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


/*
        public static void main(String[] args) {
            ThompsonConstruction tc = new ThompsonConstruction();
            NFA test=tc.reToNFA(DUCK_LOCAL);
            System.out.println(testNFA(test,"Hello"));
            SubsetConstructionMethod converter = new SubsetConstructionMethod();
            DFAminimization minimizer = new DFAminimization();

            // Create a map to store the DFAs for each token type
            Map<TokenType, DFA> tokenDFAs = new HashMap<>();

            // Map of regex patterns to token types
            Map<String, TokenType> patterns = new LinkedHashMap<>();
            patterns.put(DUCK_ID, TokenType.DUCK_ID);
            patterns.put(DUCK_LOCAL,TokenType.DUCK_LOCAL);
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
            WEBBED_FEET 1
            DUCK_POND_DEPTH 12.13
           DUCK_BOOL Quaak
           ADD 123 435
           Nest_Egg hh
           QUACK_PRINT hello iqra here 
           FEATHER_CODE A
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

            SymbolTable symbolTable = new SymbolTable();

            // After getting tokens from lexical analyzer
            System.out.println("Tokenizing...\n");
            System.out.println("Tokens found:");
            for (Token token : tokens) {
                if (token.getType() != TokenType.WHITESPACE) {
                    System.out.println(token);
                    symbolTable.processToken(token);
                }
            }

            // Print the symbol table
            symbolTable.printSymbolTable();

        }*/

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
    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        String filePath = "input.txt";
        String fileContent = readFile(filePath);

        if (fileContent != null) {
            testREtoDFA();
            testLexicalAnalysis();
            testErrorDetection();
            testSymbolTable();
        }
    }

    private static void testREtoDFA() {
        System.out.println("Test 1: Regular Expression to NFA to DFA Conversion");
        System.out.println("----------------------------------------------");

        ThompsonConstruction tc = new ThompsonConstruction();

        // Test cases for different patterns
        String[] patterns = {
                FEATHER_CODE,
                DUCK_ID,
                DUCK_BOOL, WEBBED_FEET ,
                DUCK_POND_DEPTH,DUCK_MATH
                ,DUCK_GLOBAL, DUCK_LOCAL,DUCK_COMMENT_SINGLE,DUCK_COMMENT_MULTI,DUCK_KEYWORD

        };



        for (int i = 0; i < patterns.length; i++) {
           // System.out.println("\nTesting pattern: " + patterns[i] + " (" + descriptions[i] + ")");

            // Convert RE to NFA
            NFA nfa = tc.reToNFA(patterns[i]);
            System.out.println("\nNFA State Transitions:");
            //tc.printNFA(nfa);

            System.out.println("\nShowing state tables:");
            tc.createStateTable(nfa);
            // Convert NFA to DFA
            SubsetConstructionMethod scm = new SubsetConstructionMethod();
            DFA dfa = scm.conversion(nfa);

            // Print DFA transition table
            System.out.println("\nDFA Transition Table:");
            scm.printTransitionTable(dfa);

            // Minimize DFA
            DFAminimization minimizer = new DFAminimization();
            DFA minimizedDFA = minimizer.minimization(dfa);

            System.out.println("\nMinimized DFA Transition Table:");
            scm.printTransitionTable(minimizedDFA);
            System.out.println("Total States: " + minimizedDFA.getTotalStates());
        }
    }

    private static void testLexicalAnalysis() {
        ThompsonConstruction tc = new ThompsonConstruction();
        SubsetConstructionMethod converter = new SubsetConstructionMethod();
        DFAminimization minimizer = new DFAminimization();

        // Create a map to store the DFAs for each token type
        Map<TokenType, DFA> tokenDFAs = new HashMap<>();

        // Map of regex patterns to token types
        Map<String, TokenType> patterns = new LinkedHashMap<>();
        patterns.put(DUCK_ID, TokenType.DUCK_ID);
        patterns.put(DUCK_LOCAL,TokenType.DUCK_LOCAL);
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
            WEBBED_FEET 1
            DUCK_POND_DEPTH 12.13
           DUCK_BOOL Quaak
           ADD 123 435
           Nest_Egg hh
           QUACK_PRINT hello iqra here 
           FEATHER_CODE A
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

        SymbolTable symbolTable = new SymbolTable();

        // After getting tokens from lexical analyzer
        System.out.println("Tokenizing...\n");
        System.out.println("Tokens found:");
        for (Token token : tokens) {
            if (token.getType() != TokenType.WHITESPACE) {
                System.out.println(token);
                symbolTable.processToken(token);
            }
        }

        // Print the symbol table
        symbolTable.printSymbolTable();
    }

    private static void testErrorDetection() {
        System.out.println("\nTest 3: Error Detection");
        System.out.println("----------------------");

        // Test case 1: Invalid characters in identifiers
        System.out.println("\nTesting invalid characters in identifiers:");
        String[] invalidIdentifiers = {
                "var@name",
                "123abc",
                "my_var!",
                "test#var"
        };

        for (String identifier : invalidIdentifiers) {
            ErrorHandler.reportError(1, "Invalid character in identifier: " + identifier);
        }

        // Test case 2: Invalid state transitions
        System.out.println("\nTesting invalid state transitions:");
        List<String> validStates = Arrays.asList("q0", "q1", "q2");
        List<Character> validSymbols = Arrays.asList('a', 'b', 'c');

        // Test undefined source state
        ErrorHandler.checkTransition("q3", 'a', "q1", validStates, validSymbols, 1);

        // Test undefined target state
        ErrorHandler.checkTransition("q0", 'a', "q4", validStates, validSymbols, 2);

        // Test invalid symbol
        ErrorHandler.checkTransition("q0", 'x', "q1", validStates, validSymbols, 3);

        // Test case 3: Invalid tokens
        System.out.println("\nTesting invalid tokens:");
        List<String> invalidTokens = Arrays.asList("@var", "123..", "!invalid", "#test");
        List<Character> validChars = Arrays.asList('a', 'b', 'c', '1', '2', '3', '_');
        ErrorHandler.checkInvalidTokens(invalidTokens, validChars, 4);

        // Test case 4: Undefined states
        System.out.println("\nTesting undefined states:");
        String[] undefinedStates = {"q5", "q6", "q7"};
        for (String state : undefinedStates) {
            ErrorHandler.checkUndefinedState(state, validStates, 5);
        }
    }

    private static void testSymbolTable() {
        System.out.println("\nTest 4: Symbol Table");
        System.out.println("------------------");

        SymbolTable symbolTable = new SymbolTable();

        // Test adding different types of symbols
        symbolTable.addSymbol("x", SymbolType.CONSTANT_LOCAL, "DUCK_INT", "42", "local", 1);
        symbolTable.addSymbol("PI", SymbolType.CONSTANT_GLOBAL, "DUCK_POND_DEPTH", "3.14159", "global", 2);
        symbolTable.addSymbol("isValid", SymbolType.CONSTANT_LOCAL, "DUCK_BOOL", "QUACK_QUACK", "local", 3);
        symbolTable.addSymbol("ADD", SymbolType.ARITHMETIC_OP, "operator", null, "global", 4);

        // Test scope handling
        symbolTable.enterScope();
        symbolTable.addSymbol("temp", SymbolType.CONSTANT_LOCAL, "DUCK_INT", "10", "local", 5);
        symbolTable.exitScope();

        // Print the symbol table
        System.out.println("\nFinal Symbol Table:");
        symbolTable.printSymbolTable();
    }
}
