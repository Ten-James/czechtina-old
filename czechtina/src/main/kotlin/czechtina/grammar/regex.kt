package czechtina.grammar


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
    throw Exception("Unknown type $czechType")
}


fun cAndCzechtinaRegex (list: List<GrammarToken>): String = list.joinToString("|") { czechtina[it]!! + "|" + C[it]!! }

fun czechtinaRegex (list: List<GrammarToken>): String = list.joinToString("|") { czechtina[it]!! }