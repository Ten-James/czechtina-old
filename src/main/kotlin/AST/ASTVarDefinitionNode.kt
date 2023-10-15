package AST

import compiler.Compiler
import czechtina.GrammarToken
import czechtina.czechtina

class ASTVarDefinitionNode : ASTTypedNode {
    var variable:ASTNode
    var type:ASTTypedNode



    constructor(variable:ASTNode, type : ASTTypedNode): super(type.expType) {
        this.type = type
        this.variable = variable

    }

    override fun toString(): String {
        return "Var def for $variable with $expType"
    }

    override fun toC(): String = "$type $variable"
}