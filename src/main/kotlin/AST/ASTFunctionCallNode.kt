package AST

import compiler.Compiler
import compiler.DefinedType
import czechtina.GrammarToken
import czechtina.czechtina

class ASTFunctionCallNode : ASTVariableNode {
    var function:ASTTypedNode
    var params:ASTNode? = null



    constructor(function:ASTTypedNode, params:ASTNode? = null): super("", DefinedType("none")) {
        this.function = function
        this.params = params
    }

    override fun getType(): DefinedType {
        if (Compiler.definedFunctions.containsKey(function.toC())) {
            val paramsTypes = mutableListOf<DefinedType>()
            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTTypedNode)
                paramsTypes.add((params as ASTTypedNode).getType())

            if (function.toC() == "predej")
                return (params!! as ASTVariableNode).getType().toDynamic()
            if (function.toC() == "const")
                return (params!! as ASTVariableNode).getType().toConst()
            val variantIndex = Compiler.definedFunctions[function.toC()]!!.validateParams(paramsTypes)
            if (variantIndex != -1)
                return Compiler.definedFunctions[function.toC()]!!.getReturnType(variantIndex)
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

    override fun toC(): String {
        if (function?.toC().equals(czechtina[GrammarToken.TYPE_ADDRESS]!!))
            return "&${params?.toC()}"

        if (function?.toC().equals(czechtina[GrammarToken.TYPE_VALUE]!!))
            return "*(${params?.toC()})"

        if (function?.toC().equals("predej")) {
            val body = "${params?.toC()}"
            Compiler.variables[Compiler.variables.size-1][params?.toC()!!]!!.dealocated = true
            return body
        }
        if (function?.toC().equals("const")) {
            if (params is ASTVariableNode){
                if (!(params as ASTVariableNode).getType().isPointer())
                    throw Exception("Const can be applied only to objects")
                return "${params?.toC()}"
            }
            throw Exception("Const can be applied only to variables")
        }

        if (Compiler.definedFunctions.containsKey(function.toC())) {
            val paramsTypes = mutableListOf<DefinedType>()
            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTTypedNode)
                paramsTypes.add((params as ASTTypedNode).getType())

            val variantIndex = Compiler.definedFunctions[function.toC()]!!.validateParams(paramsTypes)
            if (variantIndex != -1) {
                Compiler.definedFunctions[function.toC()]!!.variants[variantIndex].timeUsed++
                return "${Compiler.definedFunctions[function.toC()]!!.variants[variantIndex].translatedName}(${params?.toC()})"
            }
            throw Exception("Function ${function.toC()} with params ${paramsTypes.joinToString(",")} not found")
        }

        throw Exception("Function ${function.toC()} not found")
        return "${function.toC()}(${params?.toC()})"
    }
}