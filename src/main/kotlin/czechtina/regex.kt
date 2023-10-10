package czechtina

enum class GrammarToken {
    TYPE_VOID,
    TYPE_INTEGER,
    TYPE_DECIMAL,
    TYPE_BOOLEAN,
    TYPE_CHAR,
    TYPE_POINTER,
    TYPES,
    OPERATOR_PLUS,
    OPERATOR_MINUS,
    OPERATOR_MULTIPLY,
    OPERATOR_DIVIDE,
    OPERATOR_MODULO,
    OPERATOR_ASSIGN,
    OPERATOR_EQUAL,
    OPERATOR_NOT_EQUAL,
    OPERATOR_LESS,
    OPERATOR_LESS_OR_EQUAL,
    OPERATOR_GREATER,
    OPERATOR_GREATER_OR_EQUAL,
    OPERATOR_AND,
    OPERATOR_OR,
    OPERATOR_NOT,
    KEYWORD_IF,
    KEYWORD_ELSE,
    KEYWORD_WHILE,
    KEYWORD_FOR,
    KEYWORD_RETURN,
    KEYWORD_BREAK,
    KEYWORD_CONTINUE,
    VARIABLE,
}


fun czechtina(type: GrammarToken): String = when (type) {
    GrammarToken.TYPE_VOID -> "void"
    GrammarToken.TYPE_INTEGER -> "cel[eay]"
    GrammarToken.TYPE_DECIMAL -> "desetinn[eay]"
    GrammarToken.TYPE_BOOLEAN -> "bool"
    GrammarToken.TYPE_CHAR -> "znak"
    GrammarToken.TYPE_POINTER -> "ukazatel"
    GrammarToken.TYPES -> listOf(
        GrammarToken.TYPE_VOID,
        GrammarToken.TYPE_INTEGER,
        GrammarToken.TYPE_DECIMAL,
        GrammarToken.TYPE_BOOLEAN,
        GrammarToken.TYPE_CHAR,
        GrammarToken.TYPE_POINTER
    ).map(::czechtina).joinToString("|")

    else -> throw IllegalArgumentException("Unknown token type: $type")
}
