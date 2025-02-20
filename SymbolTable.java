import java.util.*;

public class SymbolTable {
    private Map<String, Symbol> symbols;
    private int currentScope;

    private static final Set<String> DATATYPES = new HashSet<>(Arrays.asList(
            "DUCK_INT", "DUCK_BOOL", "DUCK_STRING", "DUCK_CHAR"
    ));

    private static final Set<String> IO_OPERATIONS = new HashSet<>(Arrays.asList(
            "QUACK_PRINT", "QUACK_INPUT"
    ));

    private static final Set<String> ARITHMETIC_OPERATIONS = new HashSet<>(Arrays.asList(
            "ADD", "SUB", "MUL", "DIV", "MOD", "POW"
    ));

    public SymbolTable() {
        this.symbols = new HashMap<>();
        this.currentScope = 0;
        initializeSymbolTable();
    }

    private void initializeSymbolTable() {
        // Initialize datatypes
        for (String datatype : DATATYPES) {
            addSymbol(datatype, SymbolType.DATATYPE, "keyword", null, "global", 0);
        }

        // Initialize I/O operations
        for (String io : IO_OPERATIONS) {
            addSymbol(io, SymbolType.INPUT_OUTPUT, "keyword", null, "global", 0);
        }

        // Initialize arithmetic operations
        for (String op : ARITHMETIC_OPERATIONS) {
            addSymbol(op, SymbolType.ARITHMETIC_OP, "operator", null, "global", 0);
        }
    }

    public void enterScope() {
        currentScope++;
    }

    public void exitScope() {
        currentScope--;
    }

    public void addSymbol(String name, SymbolType type, String dataType, String value, String scope, int line) {
        symbols.put(name, new Symbol(name, type, dataType, value, scope, line));
    }

    public Symbol lookup(String name) {
        return symbols.get(name);
    }

    public void processToken(Token token) {
        switch (token.getType()) {
            case DUCK_ID:
                addSymbol(token.getValue(), SymbolType.CONSTANT_LOCAL, "identifier", null, "local", token.getLine());
                break;
            case DUCK_GLOBAL:
                addSymbol(token.getValue(), SymbolType.CONSTANT_GLOBAL, "global", null, "global", token.getLine());
                break;
            case DUCK_COMMENT_SINGLE:
                addSymbol("SingleLineComment_" + symbols.size(), SymbolType.COMMENT_SINGLE, "comment", token.getValue(), "global", token.getLine());
                break;
            case DUCK_COMMENT_MULTI:
                addSymbol("MultiLineComment_" + symbols.size(), SymbolType.COMMENT_MULTI, "comment", token.getValue(), "global", token.getLine());
                break;
            case DUCK_MATH:
                // Arithmetic operations are already initialized in the symbol table
                break;
            case DUCK_KEYWORD:
                // Keywords are already initialized in the symbol table
                break;
            case DUCK_BOOL:
                addSymbol(token.getValue(), SymbolType.CONSTANT_LOCAL, "DUCK_BOOL", token.getValue(), "local", token.getLine());
                break;
            case DUCK_POND_DEPTH:
                addSymbol(token.getValue(), SymbolType.CONSTANT_LOCAL, "DUCK_FLOAT", token.getValue(), "local", token.getLine());
                break;
        }
    }

    public void printSymbolTable() {
        System.out.println("\nSymbol Table Contents:");
        System.out.println("=====================");
        System.out.printf("%-20s %-15s %-15s %-20s %-10s %s%n",
                "Name", "Type", "DataType", "Value", "Scope", "Line");
        System.out.println("--------------------------------------------------------------------------------");

        // Sort symbols by type for better readability
        List<Symbol> sortedSymbols = new ArrayList<>(symbols.values());
        sortedSymbols.sort((a, b) -> a.getType().compareTo(b.getType()));

        for (Symbol symbol : sortedSymbols) {
            System.out.println(symbol);
        }
    }
}