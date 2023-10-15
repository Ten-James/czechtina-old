package AST

import compiler.Compiler
import czechtina.GrammarToken
import czechtina.czechtina

class ASTFunctionCallNode : ASTTypedNode {
    var function:ASTTypedNode
    var params:ASTNode? = null



    constructor(function:ASTTypedNode, params:ASTNode? = null): super("") {
        this.function = function
        this.params = params
    }

    override fun getType(): String {
        if (Compiler.definedFunctions.containsKey(function?.toC())) {
            return Compiler.definedFunctions[function?.toC()]!!
        }
        return "void"
    }

    override fun toString(): String {
        return "CALL FUNCTION, \nfunction=${function.toString().replace("\n","\n\t")}, \nparams=${params.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(): String = when {
        function?.toC().equals(czechtina[GrammarToken.TYPE_ADDRESS]!!) -> "&${params?.toC()}"
        function?.toC().equals(czechtina[GrammarToken.TYPE_VALUE]!!) -> "*(${params?.toC()})"
        else -> "${params?.toC()}(${params?.toC()})"
    }
}