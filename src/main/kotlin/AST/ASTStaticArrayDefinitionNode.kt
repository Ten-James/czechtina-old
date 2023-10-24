package AST

import compiler.DefinedType

class ASTStaticArrayDefinitionNode : ASTVarDefinitionNode {
    var size: String

    constructor(type: ASTTypedNode, variable: ASTVariableNode, size: String): super( variable,type) {
        expType = DefinedType("array-$type-$size")
        this.size = size
    }

    override fun toString(): String {
        return "Static array definition: \ntype=${type.toString().replace("\n","\n\t")}, \nvariable=${variable.toString().replace("\n","\n\t")}, \nsize=${size}"
    }

    override fun copy(): ASTStaticArrayDefinitionNode {
        return ASTStaticArrayDefinitionNode(type.copy(), variable.copy(), size)
    }

    override fun toC(sideEffect:Boolean): String = "${type.toC()} ${variable.data}[${size}]"
}