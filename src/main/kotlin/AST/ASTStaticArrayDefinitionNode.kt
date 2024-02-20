package AST

import compiler.Compiler
import compiler.DefinedType

class ASTStaticArrayDefinitionNode(type: ASTNode, variable: ASTVariableNode, var size: String) :
    ASTVarDefinitionNode(variable, type) {

    init {
        variable.addType(type.getType().toArray(size))
    }

    override fun toString(): String {
        return "Static array definition: \ntype=${type.toString().replace("\n","\n\t")}, \nvariable=${variable.toString().replace("\n","\n\t")}, \nsize=${size}"
    }

    override fun copy(): ASTStaticArrayDefinitionNode {
        return ASTStaticArrayDefinitionNode(type.copy(), variable.copy(), size)
    }

    override fun toC(sideEffect:Boolean): String = if (Compiler.controlDefinedVariables(variable.data)) variable.toDefineC() else ""
}