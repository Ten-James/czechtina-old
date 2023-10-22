package AST

import compiler.Compiler
import czechtina.GrammarToken
import czechtina.czechtina

class ASTFunctionCallNode : ASTVariableNode {
    var function:ASTTypedNode
    var params:ASTNode? = null



    constructor(function:ASTTypedNode, params:ASTNode? = null): super("") {
        this.function = function
        this.params = params
    }

    override fun getType(): String {
        if (Compiler.definedFunctions.containsKey(function.toC())) {
            val paramsTypes = mutableListOf<String>()
            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTTypedNode)
                paramsTypes.add((params as ASTTypedNode).getType())

            if (function.toC() == "predej")
                return paramsTypes.first()
            if (Compiler.definedFunctions[function.toC()]!!.validateParams(paramsTypes) != -1)
                return Compiler.definedFunctions[function.toC()]!!.returnType.typeString
        }
        return "void"
    }

    override fun retype(map: Map<String, String>) {
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
            Compiler.variables[Compiler.variables.size-1][params?.toC()!!]!!.dealocated = true
            return "${params?.toC()}"
        }

        if (Compiler.definedFunctions.containsKey(function.toC())) {
            val paramsTypes = mutableListOf<String>()
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