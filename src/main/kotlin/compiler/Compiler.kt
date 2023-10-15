package compiler

import czechtina.*

object Compiler {
    var compilingTo = "C"
    var definedTypes = mutableListOf<String>()
    var grammar: Map<GrammarToken,String> = C
    var buildPath:String = ""

    fun typeFromCzechtina(czechType: String): String = when (compilingTo) {
        "C" -> cTypeFromCzechtina(czechType)
        "CZ" -> czTypeFromCzechtina(czechType)
        else -> ""
    }

    fun setToC() {
        compilingTo = "C"
        grammar = C
    }

    fun addToDefinedTypes(type: String) = definedTypes.add(type)

    fun setToCZ() {
        compilingTo = "CZ"
        grammar = CZ
    }

}