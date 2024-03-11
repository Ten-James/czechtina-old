package AST

import compiler.Compiler
import compiler.types.DynamicPointerType
import compiler.types.Type

open class ASTVariableNode : ASTNode {
    val data: String
    val isLocal:Boolean

    constructor(data:String, expressionType: Type, isLocal: Boolean = true) : super(expressionType) {
        Compiler.setNewVariableType(data, expressionType.copy())
        this.data = data
        this.isLocal = isLocal

    }

    override fun retype(map: Map<Type, Type>){
        for (m in map)
            if (expType == m.key)
                expType = m.value.copy()
    }

    override fun copy(): ASTVariableNode {
        return ASTVariableNode(data, expType, isLocal)
    }

    open fun addType(type: Type ): ASTVariableNode {
        Compiler.setVariableType(data, type.copy())
        this.expType = type
        return this
    }

    override fun getType(): Type {
        val defined = Compiler.tryGetType(data)
        if (defined != null)
            return if ( defined is DynamicPointerType ) defined.toUndynamic() else defined.copy();
        val compType = Compiler.getVariableType(data)
        if (compType != null)
            return if ( compType is DynamicPointerType ) compType.toUndynamic() else compType.copy();
        return (expType as? DynamicPointerType)?.toUndynamic() ?: expType.copy()
    }

    override fun toString(): String {
        return "Variable=$data: ${getType()}"
    }

    fun toDefineC(): String {
        if (isLocal && !Compiler.isDefined(data))
            Compiler.variables[Compiler.variables.size-1] += mapOf(data to expType.copy())
        return "${getType().toC()} $data"
    }
    override fun toC(sideEffect:Boolean): String = if (Compiler.isDefined(data) || !sideEffect) data else toDefineC()
}