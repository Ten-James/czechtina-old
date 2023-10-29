package AST

import compiler.Compiler
import compiler.DefinedType

class ASTStaticArrayDefinitionNode : ASTVarDefinitionNode {
    var size: String

    constructor(type: ASTNode, variable: ASTVariableNode, size: String): super( variable,type) {
        variable.addType(type.getType().toArray(size))
        this.size = size
    }

    override fun toString(): String {
        return "Static array definition: \ntype=${type.toString().replace("\n","\n\t")}, \nvariable=${variable.toString().replace("\n","\n\t")}, \nsize=${size}"
    }

    override fun copy(): ASTStaticArrayDefinitionNode {
        return ASTStaticArrayDefinitionNode(type.copy(), variable.copy(), size)
    }

    override fun toC(sideEffect:Boolean): String = if (Compiler.controlDefinedVariables(variable.data)) variable.toDefineC() else ""
}