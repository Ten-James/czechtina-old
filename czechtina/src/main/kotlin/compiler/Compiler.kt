package compiler

import AST.*
import cz.j_jzk.klang.input.InputFactory
import czechtina.grammar.*
import czechtina.header.createCzechtinaDefineFile
import czechtina.lesana.czechtinaLesana
import utils.ArgsProvider
import Printer
import java.io.File
import kotlin.system.exitProcess

object Compiler {
    private const val VERSION = "0.1.6.5"
    var isParsed = false
    private var compilingTo = "C"
    var definedTypes = mutableMapOf<String, DefinedType>()
    var undefinedFunction = mutableListOf<String>()
    var definedFunctions = initDefinedFunction()
    var definedStructures = mutableMapOf<String, DefinedStructure>()
    var variables = mutableListOf(mutableMapOf<String, DefinedType>())
    var grammar: Map<GrammarToken,String> = C
    var buildPath:String = ""

    fun typeFromCzechtina(czechType: String): String = when (compilingTo) {
        "C" -> cTypeFromCzechtina(czechType)
        "CZ" -> czTypeFromCzechtina(czechType)
        else -> ""
    }

    private fun setToC() {
        compilingTo = "C"
        grammar = C
    }

    fun addToDefinedTypes(type: String, definedType: DefinedType): Boolean {
        definedTypes += mapOf(type to definedType)
        return true
    }

    fun tryGetDefinedType(type: String) : DefinedType? {
        if (definedTypes.containsKey(type))
            return definedTypes[type]!!
        return null
    }

    fun controlDefinedVariables(varName: String): Boolean {
        if (definedTypes.keys.any { it == varName })
            throw Exception("Variable $varName is defined as type")
        if (definedFunctions.containsKey(varName))
            throw Exception("Variable $varName is defined as function")
        if (variables.any { it.containsKey(varName) })
            throw Exception("Variable $varName is already defined")
        return true
    }

    fun setNewVariableType(varName: String, type: DefinedType) {
        if (varName == "")
            return
        Printer.info("Adding variable '$varName' with type $type")
        if (variables[variables.size - 1].containsKey(varName)) {
            setVariableType(varName, type)
            return
        }
        variables[variables.size - 1] += mapOf(varName to type)
    }

    fun setVariableType(varName: String, type: DefinedType) {
        for (variable in variables.reversed()){
            if (variable.containsKey(varName)) {
                Printer.info("Setting variable '$varName' with type $type")
                variable[varName] = type
                return
            }
        }
    }

    fun getVariableType(varName: String): DefinedType? {
        for (variable in variables.reversed())
            if (variable.containsKey(varName))
                return variable[varName]!!
        return null
    }

    fun isDefined(varName: String) : Boolean {
        if (definedFunctions.containsKey(varName))
            return true
        if (variables.any{ it.containsKey(varName) && it[varName]!!.dealocated })
            throw Exception("Variable $varName is dealocated")
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
            if (it.value.isDynamic() && !it.value.dealocated)
                retVal += "if(${it.key})free(${it.key});\n\t"
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
        "string" -> 5
        "double" -> 4
        "float" -> 3
        "int" -> 2
        "char" -> 1
        "bool" -> 1
        "void" -> 0
        else -> if (type.contains("-")) 0 else throw Exception("Unhandled Type $type")
    }

    fun calcBinaryType(left: ASTNode,  right: ASTNode, operand: String): DefinedType {
        if (left.getType().isTemplate())
            return DefinedType(left.getType())
        if (right.getType().isTemplate())
            return DefinedType(right.getType())
        if (operand == "=") {
            if (left.getType().isConst && isParsed)
                throw Exception("Cannot change const type of variable $left")
            if (left is ASTVariableNode)
                left.addType(right.getType())
            return DefinedType(right.getType())
        }
        if (Regex(cAndCzechtinaRegex(AllComparation)).matches(operand))
            return DefinedType("bool")

        if (Regex(cAndCzechtinaRegex(listOf( GrammarToken.OPERATOR_MINUS))).matches(operand)) {
            if (left.getType().isAddress() && right.getType().isAddress())
               return DefinedType("int")
            if (left.getType().isAddress() && right.getType().getPrimitive().contains("int"))
                return DefinedType(left.getType())
            if (left.getType().getPrimitive().contains("int") && right.getType().isAddress())
                return DefinedType(right.getType())
        }
        if (Regex(cAndCzechtinaRegex(listOf( GrammarToken.OPERATOR_PLUS))).matches(operand)) {
            if (left.getType().isAddress() && right.getType().getPrimitive().contains("int")){
                return DefinedType(left.getType())
            }
            if (left.getType().getPrimitive().contains("int") && right.getType().isAddress())
                return DefinedType(right.getType())
        }

        try {
            val leftWeight = calcTypePriority(left.getType().typeString)
            val rightWeight = calcTypePriority(right.getType().typeString)
            val maxW = maxOf(leftWeight, rightWeight)
            val minW = minOf(leftWeight, rightWeight)

            if (maxW == 10 && minW == 2)
                throw Exception("cant do $operand operation with pointer")

            if (operand == "%" && listOf(leftWeight, rightWeight).any { it == 3 || it == 4 })
                throw Exception("Modulo cant be made with floating point number")


            if (leftWeight > rightWeight)
                return DefinedType(left.getType())
            return DefinedType(right.getType())

        } catch (e: Exception) {
            println(operand)
            println(left.getType())
            println(right.getType())
            throw Exception("Error in calcBinaryType: ${e.message}")
        }
    }

    override fun toString(): String {
        return "Compiler(compilingTo='$compilingTo', definedTypes=$definedTypes, definedFunctions=$definedFunctions, variables=$variables,)"
    }

    private fun addFunctionVariants(code:String, tree: ASTProgramNode) : String {
        var cCode = code
        while (isAnyUsedFunctionUndefined()) {
            for (function in definedFunctions){
                val fce = function.value
                if (fce.virtual)
                    continue
                val variants = listOf(fce.variants).flatten().filter { !it.defined && it.timeUsed > 0 }
                for (variant in variants){
                    val functionASTree = tree.functions.find { it.name == function.key }
                        ?: throw Exception("Function ${function.key} not found")
                    scopePush()
                    val functionAST = functionASTree.copy()
                    scopePop()
                    val paramsTypes = mutableListOf<DefinedType>()
                    for (param in functionAST.parameters)
                        paramsTypes.add(param.getType())
                    val abstractIndex = fce.validateParams(paramsTypes)
                    val retypeMap = mutableMapOf<String,DefinedType>()
                    for (i in 0 until variant.params.size){
                        val old =fce.variants[abstractIndex].params[i]
                        val new = DefinedType(variant.params[i])

                        if (old.isPointer()){
                            if (old.typeString.replace("pointer", "dynamic") == new.typeString) {
                                retypeMap += mapOf(old.typeString to new)
                                continue
                            }
                            if(!old.isConst && new.isConst) {
                                retypeMap += mapOf(old.typeString to new)
                                continue
                            }
                        }


                        if (!old.isTemplate())
                            continue
                        retypeMap += mapOf(old.getTemplate() to new)
                    }
                    functionAST.name = variant.translatedName
                    functionAST.retype(retypeMap)

                    if (!variant.virtual) {
                        cCode = cCode.replace("//${function.key}_Declaration_CZECHTINA ANCHOR", "//${function.key}_Declaration_CZECHTINA ANCHOR\n${functionAST.toCDeclarationNoSideEffect()}")
                        cCode = cCode.replace("//${function.key}_CZECHTINA ANCHOR", "//${function.key}_CZECHTINA ANCHOR\n${functionAST.toCNoSideEffect()}")
                    }
                    else {
                        functionAST.toCNoSideEffect()
                    }

                    variant.defined = true
                }
            }
        }
        return cCode
    }



    private fun isAnyUsedFunctionUndefined(): Boolean {
        for (function in definedFunctions)
            if (function.value.variants.any { !it.defined && it.timeUsed > 0 })
                return true
        return false
    }

    val czechtina = czechtinaLesana()
    fun compileText(text:String): String {
        val preprocessed = Preprocessor.preprocessText(text, "")
        isParsed = false
        val tree: ASTProgramNode?
        var cCode: String
        try {
            currentCode = preprocessed
            tree = czechtina.parse(InputFactory.fromString(preprocessed, "code")) as ASTProgramNode
            isParsed = true
            for (function in tree.functions)
                function.precalculateType()
            variables.clear()
            variables.add(mutableMapOf())
            cCode = tree.toC()
        } catch (e:Exception) {
            Printer.warning("Variables: $variables")
            throw e
        }
        cCode = addFunctionVariants(cCode, tree)
        cCode = cCode.replace("#\$#CZECHTINAMEZERA\$#\$", " ")
        cCode = cCode.lines().filter { !it.contains("CZECHTINA ANCHOR") }.joinToString("\n")
        return cCode
    }



    var currentCode = ""

    var currentErrors = mutableMapOf<Int, MutableList<Int>>()

    fun getCurrentCodeLine(charIndex: Int) {
        var line = 1
        var char = 0
        for (i in 0 until charIndex) {
            if (currentCode[i] == '\n') {
                line++
                char = 0
            }
            else
                char++
        }

        if (currentErrors.containsKey(line))
            currentErrors[line]!!.add(char)
        else
            currentErrors += mapOf(line to mutableListOf(char))
    }



    fun compile(text: String, path: String) {
        Printer.info("Started compiling process -> ${path}")
        val code = Preprocessor.preprocessText(text, path)
        val withoutExtension = path.substring(0, path.length - 3)
        Printer.info(code)
        isParsed = false
        val tree: ASTProgramNode?
        try {
            currentCode = code
            tree = czechtina.parse(InputFactory.fromString(code, "code")) as ASTProgramNode
        } catch (e:Exception) {
            Printer.printCurrentErrors(Compiler.currentCode,currentErrors)
            Printer.fatal("There is syntax error in your code")
            Printer.fatal(e.message!!)
            if (ArgsProvider.debug)
                e.printStackTrace()
            exitProcess(1)
        }
        Printer.info("File ${path} has been parsed")
        isParsed = true


        if (ArgsProvider.showTree) {
            println(tree.toString())
        }

        variables.clear()
        variables.add(mutableMapOf())

        var cCode = ""

        try {
            cCode = tree.toC()
        } catch (e:Exception) {
            Printer.fatal(e.message!!)
            if (ArgsProvider.debug)
                e.printStackTrace()
            exitProcess(1)
        }

        cCode = addFunctionVariants(cCode, tree)

        if (compilingTo == "CZ") {
            cCode = "#define \"czechtina.h\"\n$cCode"
            createCzechtinaDefineFile()
        }

        if (ArgsProvider.friendly) {
            if (compilingTo != "C") {
                setToC()
                cCode += "\n/*\n ${tree.toC()} \n*/"
            }
        }

        cCode = cCode.replace("#\$#CZECHTINAMEZERA\$#\$", " ")

        cCode = cCode.lines().filter { !it.contains("CZECHTINA ANCHOR") }.joinToString("\n")

        cCode= "// Czechtina $VERSION\n$cCode"

        if (ArgsProvider.writeCode) {
            cCode = "/*\n\t${text.replace("\n", "\n\t")}\n*/\n$cCode"
        }
        File("$buildPath$withoutExtension.c").writeText(cCode)
        Printer.success("File was compiled into $buildPath$withoutExtension.c")
    }
}