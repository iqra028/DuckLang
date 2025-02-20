import java.util.*;

public class LexicalAnalyzer {
    private final Map<TokenType, DFA> tokenDFAs;
    private final String input;
    private int position;
    private int line;
    private int column;

    public LexicalAnalyzer(Map<TokenType, DFA> dfas, String input) {
        this.tokenDFAs = dfas;
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }

    public List<Token> analyze() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            Token token = getNextToken();
            if (token != null) {
                tokens.add(token);
                if (token.getType() == TokenType.WHITESPACE && token.getValue().contains("\n")) {
                    line++;
                    column = 1;
                } else {
                    column += token.getValue().length();
                }
            }
        }

        return tokens;
    }

    private Token getNextToken() {
        if (position >= input.length()) {
            return null;
        }

        // Skip whitespace
        if (Character.isWhitespace(input.charAt(position))) {
            int start = position;
            while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
                position++;
            }
            return new Token(TokenType.WHITESPACE, input.substring(start, position), line, column);
        }

        // Try to match each token type
        int longestMatch = 0;
        TokenType matchedType = null;
        String matchedValue = null;

        for (Map.Entry<TokenType, DFA> entry : tokenDFAs.entrySet()) {
            TokenType type = entry.getKey();
            DFA dfa = entry.getValue();

            int matchLength = simulateDFA(dfa, position);
            if (matchLength > longestMatch) {
                longestMatch = matchLength;
                matchedType = type;
                matchedValue = input.substring(position, position + matchLength);
            }
        }

        if (longestMatch > 0) {
            position += longestMatch;
            return new Token(matchedType, matchedValue, line, column);

        }

        // If no match found, consume one character as invalid token
        return new Token(TokenType.INVALID, input.substring(position - 1, position), line, column);
    }

    private int simulateDFA(DFA dfa, int startPos) {
        DFAState currentState = dfa.startState;
        int maxMatchLength = 0;
        int currentLength = 0;

        for (int i = startPos; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!currentState.transitions.containsKey(c)) {
                break;
            }
            currentState = currentState.transitions.get(c);
            currentLength++;
            if (currentState.isFinal) {
                maxMatchLength = currentLength;
            }
        }

        return maxMatchLength;
    }
}