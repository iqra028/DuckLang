public class Token {
    private TokenType type;
    private String value;
    private int line;
    private int position;

    public Token(TokenType type, String value, int line, int position) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.position = position;
    }


    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }
    public String toString() {
        return String.format("Token{type=%s, value='%s'}",
                type, value);
    }
}