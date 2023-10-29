package AST

import compiler.Compiler
import compiler.DefinedType

open class ASTVarDefinitionNode : ASTNode {
    var variable:ASTVariableNode
    var type:ASTNode


    override fun retype(map: Map<String, DefinedType>) {
        type.retype(map)
        variable.retype(map)
        for (m in map)
            if (expType.typeString == m.key)
                expType = m.value
    }


    override fun getType(): DefinedType {
        return variable.getType()
    }

    override fun copy(): ASTVarDefinitionNode {
        return ASTVarDefinitionNode(variable.copy(), type.copy())
    }

    constructor(variable:ASTVariableNode, type : ASTNode): super(variable.expType) {
        this.type = type
        this.variable = variable.addType(type.getType())
    }

    override fun toString(): String {
        return "Var def for $variable with ${getType()}"
    }

    override fun toC(sideEffect:Boolean): String = if (Compiler.controlDefinedVariables(variable.data)) variable.toDefineC() else ""
}