package AST

import compiler.Compiler
import compiler.types.InvalidType
import compiler.types.Type
import czechtina.grammar.GrammarToken
import czechtina.grammar.czechtina
import compiler.virtual.getVirtualFunction

class ASTFunctionCallNode : ASTVariableNode {
    var function:ASTVariableNode
    var params:ASTNode? = null



    constructor(function:ASTVariableNode, params:ASTNode? = null): super("", InvalidType()) {
        this.function = function
        this.params = params
    }

    override fun getType(): Type {
        val virFun = getVirtualFunction(function.data)
        if (virFun != null)
            return virFun.getReturnType(params)

        var funName = function.data

        if (function is ASTStructureAccessNode)
            funName = (function as ASTStructureAccessNode).getFunctionName()

        if (Compiler.definedFunctions.containsKey(funName)) {
            val paramsTypes = mutableListOf<Type>()

            if (function is ASTStructureAccessNode)
                paramsTypes.add((function as ASTStructureAccessNode).struct.getType())

            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTNode)
                paramsTypes.add((params as ASTNode).getType())


            val variantIndex = Compiler.definedFunctions[funName]!!.validateParams(paramsTypes)
            if (variantIndex != -1)
                return Compiler.definedFunctions[funName]!!.getReturnType(variantIndex)
        }
        return InvalidType()
    }

    override fun retype(map: Map<Type, Type>) {
        function.retype(map)
        params?.retype(map)
    }

    override fun toString(): String {
        return "Function Call:\n  func='${function.toC(false)}',\n  params=${params.toString().replace("\n","\n  ")}"
    }

    override fun copy(): ASTFunctionCallNode {
        return ASTFunctionCallNode(function.copy(), params?.copy())
    }

    override fun toC(sideEffect:Boolean): String {
        val virFun = getVirtualFunction(function.data)
        if (virFun != null)
            return virFun.toC(params)

        var funName = function.data

        if (function is ASTStructureAccessNode) {
            funName = (function as ASTStructureAccessNode).getFunctionName()

            params = if (params != null)
                ASTListNode(listOf((function as ASTStructureAccessNode).struct, params!!));
            else
                (function as ASTStructureAccessNode).struct;
        }

        if (Compiler.definedFunctions.containsKey(funName)) {
            val paramsTypes = mutableListOf<Type>()
            if (function is ASTStructureAccessNode)
                paramsTypes.add((function as ASTStructureAccessNode).struct.getType())
            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTNode)
                paramsTypes.add((params as ASTNode).getType())

            val variantIndex = Compiler.definedFunctions[funName]!!.validateParams(paramsTypes)
            if (variantIndex != -1) {
                Compiler.definedFunctions[funName]!!.variants[variantIndex].timeUsed++
                return "${Compiler.definedFunctions[funName]!!.variants[variantIndex].translatedName}(${params?.toC()})"
            }
            throw Exception("Function ${funName} with params ${paramsTypes.joinToString(",")} not found")
        }
        if (Compiler.undefinedFunction.contains(funName))
            return "${funName}(${params?.toC()})"
        throw Exception("Function ${funName} not found")
    }
}