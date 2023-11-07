package AST

import compiler.Compiler
import compiler.DefinedType
import czechtina.grammar.GrammarToken
import czechtina.grammar.czechtina
import compiler.virtual.getVirtualFunction

class ASTFunctionCallNode : ASTVariableNode {
    var function:ASTVariableNode
    var params:ASTNode? = null



    constructor(function:ASTVariableNode, params:ASTNode? = null): super("", DefinedType("none")) {
        this.function = function
        this.params = params
    }

    override fun getType(): DefinedType {
        val virFun = getVirtualFunction(function.data)
        if (virFun != null)
            return virFun.getReturnType(params)

        var funName = function.data

        if (function is ASTStructureAccessNode)
            funName = (function as ASTStructureAccessNode).getFunctionName()

        if (Compiler.definedFunctions.containsKey(funName)) {
            val paramsTypes = mutableListOf<DefinedType>()

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
        return DefinedType("none")
    }

    override fun retype(map: Map<String, DefinedType>) {
        function.retype(map)
        params?.retype(map)
    }

    override fun toString(): String {
        return "CALL FUNCTION, \nfunction=${function.toString().replace("\n","\n\t")}, \nparams=${params.toString().replace("\n","\n\t")}\n"
    }

    override fun copy(): ASTFunctionCallNode {
        return ASTFunctionCallNode(function.copy(), params?.copy())
    }

    override fun toC(sideEffect:Boolean): String {
        val virFun = getVirtualFunction(function.data)
        if (virFun != null)
            return virFun.toC(params)
        /*
        if (function.data == "throw")
            return "printf(${params?.toC()}); exit(1)"

        if (function.data == "inC")
            return "${(params as ASTUnaryNode).data}"

        if (function.data.equals(czechtina[GrammarToken.TYPE_ADDRESS]!!))
            return "&${params?.toC()}"

        if (function.data.equals(czechtina[GrammarToken.TYPE_VALUE]!!))
            return "*(${params?.toC()})"

        if (function.data.equals("new")){
            if ((params as ASTNode).getType().isStructured) {
                return "(${(params as ASTNode).getType().toC()})malloc(sizeof(${(params as ASTNode).getType().getPrimitive()}))"
            }
        }

        if (function.data.equals("predej")) {
            val body = "${params?.toC()}"
            Compiler.variables[Compiler.variables.size-1][params?.toC()!!]!!.dealocated = true
            return body
        }
        if (function.data.equals("const")) {
            if (params is ASTVariableNode){
                if (!(params as ASTVariableNode).getType().isPointer())
                    throw Exception("Const can be applied only to objects")
                return "${params?.toC()}"
            }
            throw Exception("Const can be applied only to variables")
        }
        */

        var funName = function.data

        if (function is ASTStructureAccessNode)
            funName = (function as ASTStructureAccessNode).getFunctionName()

        if (Compiler.definedFunctions.containsKey(funName)) {
            val paramsTypes = mutableListOf<DefinedType>()
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