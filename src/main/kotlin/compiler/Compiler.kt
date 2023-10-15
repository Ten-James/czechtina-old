package compiler

import AST.ASTNode
import AST.ASTTypedNode
import czechtina.*

object Compiler {
    var compilingTo = "C"
    var definedTypes = mutableListOf<String>()
    var definedFunctions = mapOf<String, String>()
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

    private fun calcTypePriority(type: String) : Int = when (type) {
        "pointer" -> 10
        "string" -> 5
        "double" -> 4
        "float" -> 3
        "int" -> 2
        "char" -> 1
        else -> if (type.contains("array")) 0 else throw Exception("Unhandled Type $type")
    }

    fun calcBinaryType(left: ASTTypedNode,  right: ASTTypedNode, operand: String): String {
        val leftWeight = calcTypePriority(left.expType)
        val rightWeight = calcTypePriority(right.expType)
        val maxW = maxOf(leftWeight, rightWeight)
        val minW = minOf(leftWeight, rightWeight)

        if (maxW == 10 && minW == 2)
            throw Exception("cant do this operation with pointer")

        if (operand == "%" && listOf(leftWeight, rightWeight).any { it == 3 || it == 4 })
            throw Exception("Modulo cant be made with floating point number")

        if (leftWeight > rightWeight)
            return left.expType
        return right.expType
    }
}