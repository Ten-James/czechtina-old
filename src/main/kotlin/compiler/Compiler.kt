package compiler

import AST.ASTNode
import AST.ASTTypedNode
import com.ibm.dtfj.java.JavaVMInitArgs
import cz.j_jzk.klang.input.InputFactory
import czechtina.*
import czechtina.header.createCzechtinaDefineFile
import czechtina.lesana.czechtinaLesana
import java.io.File

object Compiler {
    val VERSION = "0.1.5"
    var compilingTo = "C"
    var definedTypes = mutableListOf<String>()
    var definedFunctions = mutableMapOf<String, String>()
    var globalVariables = mutableMapOf<String, String>()
    var localVariable = mutableMapOf<String,String>()
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

    fun controlDefinedVariables(varName: String): Boolean {
        if (localVariable.containsKey(varName))
            throw Exception("Variable $varName is defined in local scope")
        if (globalVariables.containsKey(varName))
            throw Exception("Variable $varName is defined in global scope")
        if (definedFunctions.containsKey(varName))
            throw Exception("Variable $varName is defined as function")
        return true
    }

    fun getVariableType(varName: String): String {
        if (localVariable.containsKey(varName))
            return localVariable[varName]!!
        if (globalVariables.containsKey(varName))
            return globalVariables[varName]!!
        return ""
    }

    fun isDefined(varName: String) : Boolean {
        if (localVariable.containsKey(varName))
            return true
        if (globalVariables.containsKey(varName))
            return true
        if (definedFunctions.containsKey(varName))
            return true
        return false
    }

    fun setToCZ() {
        compilingTo = "CZ"
        grammar = CZ
    }

    private fun calcTypePriority(type: String) : Int = when (type) {
        "none" -> 100
        "pointer" -> 10
        "string" -> 5
        "double" -> 4
        "float" -> 3
        "int" -> 2
        "char" -> 1
        "bool" -> 1
        "void" -> 0
        else -> if (type.contains("array")) 0 else throw Exception("Unhandled Type $type")
    }

    fun calcBinaryType(left: ASTTypedNode,  right: ASTTypedNode, operand: String): String {
        if (operand == "=")
            return right.getType()

        try {
            val leftWeight = calcTypePriority(left.getType())
            val rightWeight = calcTypePriority(right.getType())
            val maxW = maxOf(leftWeight, rightWeight)
            val minW = minOf(leftWeight, rightWeight)

            if (maxW == 10 && minW == 2)
                throw Exception("cant do this operation with pointer")

            if (operand == "%" && listOf(leftWeight, rightWeight).any { it == 3 || it == 4 })
                throw Exception("Modulo cant be made with floating point number")

            if (Regex(cAndCzechtinaRegex(AllComparation)).matches(operand))
                return "bool";

            if (leftWeight > rightWeight)
                return left.getType()
            return right.getType()

        } catch (e: Exception) {
            println("------------")
            println(this)
            println("operand: $operand")
            println("left: $left ${left.getType()}")
            println("right: $right ${right.getType()}")
            throw Exception("Error in calcBinaryType: ${e.message}")
        }
    }

    override fun toString(): String {
        return "Compiler(compilingTo='$compilingTo', definedTypes=$definedTypes, definedFunctions=$definedFunctions, globalVariables=$globalVariables, localVariable=$localVariable)"
    }


    fun compileFile(path: String, args: Array<String>) {
        val code = Preprocessor.preprocess(path)
        var withoutExtension = path.substring(0, path.length - 3)
        val czechtina = czechtinaLesana()

        val tree = czechtina.parse(InputFactory.fromString(code, "code")) as ASTNode

        if (args.any() { it == "--show-tree" }) {
            println(tree.toString())
        }

        Compiler.localVariable.clear()

        var cCode = tree.toC()

        if (Compiler.compilingTo == "CZ") {
            cCode = "#define \"czechtina.h\"\n$cCode"
            createCzechtinaDefineFile()
        }

        if (args.any { it == "--friendly" }) {
            if (Compiler.compilingTo != "C") {
                Compiler.setToC()
                cCode += "\n/*\n ${tree.toC()} \n*/"
            }
        }

        cCode = cCode.replace("#\$#CZECHTINAMEZERA\$#\$", " ")

        cCode= "// Czechtina ${Compiler.VERSION}\n$cCode"

        if (args.any { it == "--write-code" }) {
            cCode = "/*\n\t${Preprocessor.lastReadFile.replace("\n", "\n\t")}\n*/\n$cCode"
        }
        File("$buildPath$withoutExtension.c").writeText(cCode)
    }
}