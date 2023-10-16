package AST

import compiler.Compiler
import czechtina.GrammarToken
import czechtina.czechtina

class ASTVarDefinitionNode : ASTTypedNode {
    var variable:ASTVariableNode
    var type:ASTTypedNode



    constructor(variable:ASTVariableNode, type : ASTTypedNode): super(type.getType()) {
        this.type = type
        this.variable = variable

        if (variable.isLocal)
            Compiler.localVariable += mapOf(variable.data to getType())
    }

    override fun toString(): String {
        return "Var def for $variable with ${getType()}"
    }

    override fun toC(): String = if (Compiler.controlDefinedVariables(variable.data)) variable.toDefineC() else ""
}