package compiler

import AST.ASTListNode
import AST.ASTNode
import AST.ASTProgramNode
import AST.ASTTypedNode
import cz.j_jzk.klang.input.InputFactory
import czechtina.*
import czechtina.header.createCzechtinaDefineFile
import czechtina.lesana.czechtinaLesana
import java.io.File

object Compiler {
    val VERSION = "0.1.5"
    var compilingTo = "C"
    var definedTypes = mutableListOf<String>()
    var definedFunctions = mutableMapOf<String, DefinedFunction>(
        "printf" to DefinedFunction("printf",DefinedType("void"), listOf(DefinedFunctionVariant("printf", listOf("string"))), virtual = true),
        "new" to DefinedFunction("new",DefinedType("dynamic-void", true), listOf(DefinedFunctionVariant("malloc", listOf("int"))), virtual = true),
        "predej" to DefinedFunction("predej",DefinedType("dynamic-void"), listOf(DefinedFunctionVariant("", listOf("dynamic"))), virtual = true),
    )
    var variables = mutableListOf(mutableMapOf<String, DefinedType>())
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
        if (definedTypes.any { it == varName })
            throw Exception("Variable $varName is defined as type")
        if (definedFunctions.containsKey(varName))
            throw Exception("Variable $varName is defined as function")
        if (variables.any { it.containsKey(varName) })
            throw Exception("Variable $varName is already defined")
        return true
    }

    fun getVariableType(varName: String): String {
        for (variable in variables.reversed())
            if (variable.containsKey(varName))
                return variable[varName]!!.typeString
        return ""
    }

    fun isDefined(varName: String) : Boolean {
        if (definedFunctions.containsKey(varName))
            return true
        if (variables.any{ it.containsKey(varName) })
            return true
        return false
    }

    fun scopePush(): String {
        variables.add(mutableMapOf())
        return ""
    }

    fun scopePop(write:Boolean = false, init: String = "\n\t", end: String = ""): String {
        var retVal = init
        if (variables.size == 1)
            throw Exception("Cant pop global scope")
        variables[variables.size -1].forEach {
            if (it.value.isHeap && !it.value.dealocated)
                retVal += "free(${it.key});\n\t"
        }
        variables.removeAt(variables.size - 1)
        if (retVal != init)
            retVal += "\n\t"
        if (!write)
            return ""
        return retVal.substringBeforeLast("\n") + end
    }


    fun setToCZ() {
        compilingTo = "CZ"
        grammar = CZ
    }

    private fun calcTypePriority(type: String) : Int = when (type) {
        "none" -> 100
        "pointer" -> 10
        "dynamic" -> 10
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
        if (left.getType().contains("*"))
            return left.getType()
        if (right.getType().contains("*"))
            return right.getType()
        if (operand == "=")
            return right.getType()

        try {
            val leftWeight = calcTypePriority(left.getType())
            val rightWeight = calcTypePriority(right.getType())
            val maxW = maxOf(leftWeight, rightWeight)
            val minW = minOf(leftWeight, rightWeight)

            if (maxW == 10 && minW == 2)
                throw Exception("cant do $operand operation with pointer")

            if (operand == "%" && listOf(leftWeight, rightWeight).any { it == 3 || it == 4 })
                throw Exception("Modulo cant be made with floating point number")

            if (Regex(cAndCzechtinaRegex(AllComparation)).matches(operand))
                return "bool";

            if (leftWeight > rightWeight)
                return left.getType()
            return right.getType()

        } catch (e: Exception) {
            throw Exception("Error in calcBinaryType: ${e.message}")
        }
    }

    override fun toString(): String {
        return "Compiler(compilingTo='$compilingTo', definedTypes=$definedTypes, definedFunctions=$definedFunctions, variables=$variables,)"
    }

    fun isAnyUsedFunctionUndefined(): Boolean {
        for (function in definedFunctions)
            if (function.value.variants.any { !it.defined && it.timeUsed > 0 })
                return true
        return false
    }

    fun compileFile(path: String, args: Array<String>) {
        var code = Preprocessor.preprocess(path)
        var withoutExtension = path.substring(0, path.length - 3)
        val czechtina = czechtinaLesana()


        val tree = czechtina.parse(InputFactory.fromString(code, "code")) as ASTProgramNode

        if (args.any() { it == "--show-tree" }) {
            println(tree.toString())
        }

        variables.clear()
        variables.add(mutableMapOf())

        var cCode = tree.toC()



        while (isAnyUsedFunctionUndefined()) {
            for (function in definedFunctions){
                val fce = function.value
                if (fce.virtual)
                    continue
                val variants = listOf(fce.variants).flatten().filter { !it.defined && it.timeUsed > 0 }
                for (variant in variants){
                    val functionASTref = tree.functions.find { it.name == function.key }
                        ?: throw Exception("Function ${function.key} not found")
                    Compiler.scopePush()
                    var functionAST = functionASTref.copy()
                    Compiler.scopePop()
                    val paramsTypes = mutableListOf<String>()
                    for (param in functionAST.parameters)
                        if (param is ASTTypedNode)
                            paramsTypes.add(param.getType())
                    val abstractIndex = fce.validateParams(paramsTypes)
                    var retypeMap = mutableMapOf<String,String>()
                    for (i in 0 until variant.params.size){
                        var old =fce.variants[abstractIndex].params[i]
                        var new = variant.params[i]

                        if (old.contains("pointer")){
                            val newOld = old.replace("pointer", "dynamic")
                            if (newOld == new) {
                                retypeMap += mapOf(old to newOld)
                                continue
                            }
                        }


                        if (!old.contains("*"))
                            continue
                        if (old.contains("-"))
                            old = old.split("-").find { it.contains("*") }!!


                        retypeMap += mapOf(old to new)
                    }
                    functionAST.name = variant.translatedName
                    functionAST.retype(retypeMap)
                    cCode = cCode.replace("//${function.key}_Declaration_CZECHTINA ANCHOR", "//${function.key}_Declaration_CZECHTINA ANCHOR\n${functionAST.toCDeclarationNoSideEffect()}")
                    cCode = cCode.replace("//${function.key}_CZECHTINA ANCHOR", "//${function.key}_CZECHTINA ANCHOR\n${functionAST.toCNoSideEffect()}")
                    variant.defined = true
                }
            }
        }

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

        cCode = cCode.lines().filter { !it.contains("CZECHTINA ANCHOR") }.joinToString("\n")

        cCode= "// Czechtina ${Compiler.VERSION}\n$cCode"

        if (args.any { it == "--write-code" }) {
            cCode = "/*\n\t${Preprocessor.lastReadFile.replace("\n", "\n\t")}\n*/\n$cCode"
        }
        File("$buildPath$withoutExtension.c").writeText(cCode)
    }
}