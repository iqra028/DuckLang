enum SymbolType {
    DATATYPE,       // DUCK_INT, DUCK_BOOL, DUCK_STRING, DUCK_CHAR
    INPUT_OUTPUT,   // QUACK_PRINT, QUACK_INPUT
    STRING,         // String literals
    COMMENT_SINGLE, // Single line comments
    COMMENT_MULTI,  // Multi-line comments
    CONSTANT_GLOBAL,// Global constants (Nest_Egg...)
    CONSTANT_LOCAL, // Local constants
    ARITHMETIC_OP   // ADD, SUB, MUL, DIV, MOD, POW
}

class Symbol {
    private String name;
    private SymbolType type;
    private String dataType;
    private String value;
    private String scope; // Changed to String to store "local" or "global"
    private int line; // Added line number

    public Symbol(String name, SymbolType type, String dataType, String value, String scope, int line) {
        this.name = name;
        this.type = type;
        this.dataType = dataType;
        this.value = value;
        this.scope = scope;
        this.line = line;
    }

    public String getName() { return name; }
    public SymbolType getType() { return type; }
    public String getDataType() { return dataType; }
    public String getValue() { return value; }
    public String getScope() { return scope; } // Updated to return String
    public int getLine() { return line; }

    @Override
    public String toString() {
        return String.format("%-20s %-15s %-15s %-20s %-10s %d",
                name, type, dataType, (value != null ? value : "-"), scope, line);
    }
}