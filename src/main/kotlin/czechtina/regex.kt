package czechtina

enum class GrammarToken {
    TYPE_VOID,
    TYPE_INTEGER,
    TYPE_DECIMAL,
    TYPE_BOOLEAN,
    TYPE_CHAR,
    TYPE_POINTER,
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
    KEYWORD_IMPORT,
    KEYWORD_IMPORT_C,
    VARIABLE,
}

val Alltypes = listOf(
    GrammarToken.TYPE_VOID,
    GrammarToken.TYPE_INTEGER,
    GrammarToken.TYPE_DECIMAL,
    GrammarToken.TYPE_BOOLEAN,
    GrammarToken.TYPE_CHAR,
)

val czechtina = mapOf<GrammarToken, String>(
    GrammarToken.TYPE_VOID to "void",
    GrammarToken.TYPE_INTEGER to "cel[eay]",
    GrammarToken.TYPE_DECIMAL to "desetinn[eay]",
    GrammarToken.TYPE_BOOLEAN to "bool",
    GrammarToken.TYPE_CHAR to "znak",
    GrammarToken.TYPE_POINTER to "ukazatel",
    GrammarToken.OPERATOR_PLUS to "plus",
    GrammarToken.OPERATOR_MINUS to "minus",
    GrammarToken.OPERATOR_MULTIPLY to "krat",
    GrammarToken.OPERATOR_DIVIDE to "deleno",
    GrammarToken.OPERATOR_MODULO to "modulo",
    GrammarToken.OPERATOR_ASSIGN to "je",
    GrammarToken.OPERATOR_EQUAL to "je presne",
    GrammarToken.OPERATOR_NOT_EQUAL to "neni presne",
    GrammarToken.OPERATOR_LESS to "mensi",
    GrammarToken.OPERATOR_LESS_OR_EQUAL to "mensi nebo rovno",
    GrammarToken.OPERATOR_GREATER to "vetsi",
    GrammarToken.OPERATOR_GREATER_OR_EQUAL to "vetsi nebo rovno",
    GrammarToken.OPERATOR_AND to "azaroven",
    GrammarToken.OPERATOR_OR to "anebo",
    GrammarToken.OPERATOR_NOT to "neni|ne",
    GrammarToken.KEYWORD_IF to "kdyz|pokud",
    GrammarToken.KEYWORD_ELSE to "jinak|nebo",
    GrammarToken.KEYWORD_WHILE to "dokud",
    GrammarToken.KEYWORD_FOR to "opakuj",
    GrammarToken.KEYWORD_RETURN to "vrat",
    GrammarToken.KEYWORD_BREAK to "veget",
    GrammarToken.KEYWORD_CONTINUE to "pokracuj",
    GrammarToken.KEYWORD_IMPORT to "pripoj cz",
    GrammarToken.KEYWORD_IMPORT_C to "pripoj c",
    GrammarToken.VARIABLE to "[a-zA-Z][a-zA-Z0-9]*",
)

val C = mapOf<GrammarToken,String>(
    GrammarToken.TYPE_VOID to "void",
    GrammarToken.TYPE_INTEGER to "int",
    GrammarToken.TYPE_DECIMAL to "double",
    GrammarToken.TYPE_BOOLEAN to "bool",
    GrammarToken.TYPE_CHAR to "char",
    GrammarToken.TYPE_POINTER to "\\*",
    GrammarToken.OPERATOR_PLUS to "\\+",
    GrammarToken.OPERATOR_MINUS to "-",
    GrammarToken.OPERATOR_MULTIPLY to "\\*",
    GrammarToken.OPERATOR_DIVIDE to "/",
    GrammarToken.OPERATOR_MODULO to "%",
    GrammarToken.OPERATOR_ASSIGN to "=",
    GrammarToken.OPERATOR_EQUAL to "==",
    GrammarToken.OPERATOR_NOT_EQUAL to "!=",
    GrammarToken.OPERATOR_LESS to "<",
    GrammarToken.OPERATOR_LESS_OR_EQUAL to "<=",
    GrammarToken.OPERATOR_GREATER to ">",
    GrammarToken.OPERATOR_GREATER_OR_EQUAL to ">=",
    GrammarToken.OPERATOR_AND to "&&",
    GrammarToken.OPERATOR_OR to "||",
    GrammarToken.OPERATOR_NOT to "!",
    GrammarToken.KEYWORD_IF to "if",
    GrammarToken.KEYWORD_ELSE to "else",
    GrammarToken.KEYWORD_WHILE to "while",
    GrammarToken.KEYWORD_FOR to "for",
    GrammarToken.KEYWORD_RETURN to "return",
    GrammarToken.KEYWORD_BREAK to "break",
    GrammarToken.KEYWORD_CONTINUE to "continue",
    GrammarToken.VARIABLE to "[a-zA-Z][a-zA-Z0-9]*",
)


val CZ = mapOf<GrammarToken,String>(
    GrammarToken.TYPE_VOID to "void",
    GrammarToken.TYPE_INTEGER to "cele",
    GrammarToken.TYPE_DECIMAL to "desetinne",
    GrammarToken.TYPE_BOOLEAN to "bool",
    GrammarToken.TYPE_CHAR to "znak",
    GrammarToken.TYPE_POINTER to "ukazatel",
    GrammarToken.OPERATOR_PLUS to "plus",
    GrammarToken.OPERATOR_MINUS to "minus",
    GrammarToken.OPERATOR_MULTIPLY to "krat",
    GrammarToken.OPERATOR_DIVIDE to "deleno",
    GrammarToken.OPERATOR_MODULO to "zbytkac",
    GrammarToken.OPERATOR_ASSIGN to "je",
    GrammarToken.OPERATOR_EQUAL to "jepresne",
    GrammarToken.OPERATOR_NOT_EQUAL to "nenipresne",
    GrammarToken.OPERATOR_LESS to "jemensi",
    GrammarToken.OPERATOR_LESS_OR_EQUAL to "jepresnemensi",
    GrammarToken.OPERATOR_GREATER to "jevetsi",
    GrammarToken.OPERATOR_GREATER_OR_EQUAL to "jepresnevetsi",
    GrammarToken.OPERATOR_AND to "azaroven",
    GrammarToken.OPERATOR_OR to "anebo",
    GrammarToken.OPERATOR_NOT to "ne",
    GrammarToken.KEYWORD_IF to "pokud",
    GrammarToken.KEYWORD_ELSE to "nebo",
    GrammarToken.KEYWORD_WHILE to "dokud",
    GrammarToken.KEYWORD_FOR to "opakuj",
    GrammarToken.KEYWORD_RETURN to "vrat",
    GrammarToken.KEYWORD_BREAK to "veget",
    GrammarToken.KEYWORD_CONTINUE to "pokracuj",
    GrammarToken.VARIABLE to "[a-zA-Z][a-zA-Z0-9]*",
)

fun cTypeFromCzechtina (czechType: String): String {
    for (type in GrammarToken.values()) {
        if (type == GrammarToken.VARIABLE)
            continue
        var cValue = C.entries.find { it.key == type }?.value ?: ""
        cValue = cValue.replace("\\", "")
        if (Regex(czechtina[type]!!).matches(czechType))
            return cValue

        if (czechType == cValue)
            return cValue
    }
    return ""
}


fun czTypeFromCzechtina (czechType: String): String {
    for (type in GrammarToken.values()) {
        if (type == GrammarToken.VARIABLE)
            continue
        var cValue = CZ.entries.find { it.key == type }?.value ?: ""
        cValue = cValue.replace("\\", "")
        if (Regex(czechtina[type]!!).matches(czechType))
            return cValue

        if (czechType == cValue)
            return cValue
    }
    return ""
}

fun cAndCzechtinaRegex (list: List<GrammarToken>): String = list.joinToString("|") { czechtina[it]!! + "|" + C[it]!! }

fun czechtinaRegex (list: List<GrammarToken>): String = list.joinToString("|") { czechtina[it]!! }