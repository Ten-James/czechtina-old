package AST

import compiler.Compiler
import compiler.types.Type

open class ASTVarDefinitionNode(variable: ASTVariableNode, var type: ASTNode) : ASTNode(variable.expType) {
    var variable:ASTVariableNode = variable.addType(type.getType())


    override fun retype(map: Map<Type, Type>) {
        type.retype(map)
        variable.retype(map)
        expType = expType.reType(map)
    }


    override fun getType(): Type {
        return variable.getType()
    }

    override fun copy(): ASTVarDefinitionNode {
        return ASTVarDefinitionNode(variable.copy(), type.copy())
    }

    override fun toString(): String {
        return "definintion:\n  $variable: ${getType()}"
    }

    override fun toC(sideEffect:Boolean): String = if (Compiler.controlDefinedVariables(variable.data)) variable.toDefineC() else ""
}