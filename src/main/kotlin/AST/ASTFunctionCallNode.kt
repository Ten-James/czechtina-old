package AST

import compiler.Compiler
import compiler.DefinedType
import czechtina.GrammarToken
import czechtina.czechtina

class ASTFunctionCallNode : ASTVariableNode {
    var function:ASTVariableNode
    var params:ASTNode? = null



    constructor(function:ASTVariableNode, params:ASTNode? = null): super("", DefinedType("none")) {
        this.function = function
        this.params = params
    }

    override fun getType(): DefinedType {
        if (function.data == "inC")
            return DefinedType("none")
        if (function.data == "predej")
            return (params!! as ASTVariableNode).getType().toDynamic()
        if (function.data == "hodnota")
            return (params!! as ASTNode).getType().toDereference()
        if (function.data == "adresa")
            return (params!! as ASTNode).getType().toPointer()
        if (function.data == "const")
            return (params!! as ASTVariableNode).getType().toConst()

        if (function.data == "new" && (params as ASTNode).getType().isStructured)
            return (params as ASTNode).getType().toDynamic()

        if (Compiler.definedFunctions.containsKey(function.toC())) {
            val paramsTypes = mutableListOf<DefinedType>()
            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTNode)
                paramsTypes.add((params as ASTNode).getType())

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

    override fun toC(sideEffect:Boolean): String {

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

        if (Compiler.definedFunctions.containsKey(function.data)) {
            val paramsTypes = mutableListOf<DefinedType>()
            if (params is ASTListNode)
                for (param in (params as ASTListNode).nodes)
                    paramsTypes.add(param.getType())
            else if (params is ASTNode)
                paramsTypes.add((params as ASTNode).getType())

            val variantIndex = Compiler.definedFunctions[function.toC()]!!.validateParams(paramsTypes)
            if (variantIndex != -1) {
                Compiler.definedFunctions[function.toC()]!!.variants[variantIndex].timeUsed++
                return "${Compiler.definedFunctions[function.toC()]!!.variants[variantIndex].translatedName}(${params?.toC()})"
            }
            throw Exception("Function ${function.toC()} with params ${paramsTypes.joinToString(",")} not found")
        }
        if (Compiler.undefinedFunction.contains(function.toC(false)))
            return "${function.toC(false)}(${params?.toC()})"
        throw Exception("Function ${function.toC()} not found")
    }
}