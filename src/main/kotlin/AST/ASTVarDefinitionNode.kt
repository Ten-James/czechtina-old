package AST

import compiler.Compiler
import compiler.DefinedType
import czechtina.GrammarToken
import czechtina.czechtina

class ASTVarDefinitionNode : ASTTypedNode {
    var variable:ASTVariableNode
    var type:ASTTypedNode


    override fun retype(map: Map<String, DefinedType>) {
        type.retype(map)
        variable.retype(map)
        for (m in map)
            if (expType.typeString == m.key)
                expType = m.value
    }

    override fun copy(): ASTVarDefinitionNode {
        return ASTVarDefinitionNode(variable.copy(), type.copy())
    }

    constructor(variable:ASTVariableNode, type : ASTTypedNode): super(type.expType) {
        this.type = type
        this.variable = variable

        if (variable.isLocal)
            Compiler.variables[Compiler.variables.size-1] += mapOf(variable.data to getType())
    }

    override fun toString(): String {
        return "Var def for $variable with ${getType()}"
    }

    override fun toC(): String = if (Compiler.controlDefinedVariables(variable.data)) variable.toDefineC() else ""
}