public enum TokenType {
    DUCK_ID,      // [a-z]+
    DUCK_BOOL,         // QUACK_QUACK|Quaak
    WEBBED_FEET,         // [0-9]+
    DUCK_POND_DEPTH,          // [0-9]+[\.0-9]*
    FEATHER_CODE,           // [a-zA-Z0-9]
    DUCK_MATH,       // ADD|SUB|MUL|DIV|MOD|POW
    DUCK_GLOBAL,     // [Nest_Egg a-z]+
    DUCK_COMMENT_SINGLE, // ~QUACK.*
    DUCK_COMMENT_MULTI,  // {.*}
    DUCK_KEYWORD,        // QUACK_PRINT|QUACK_INPUT
    WHITESPACE,     // [ \t\n\r]+
    INVALID         // Any unrecognized character
}