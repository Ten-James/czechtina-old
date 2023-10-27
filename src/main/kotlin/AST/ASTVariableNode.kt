package AST

import compiler.Compiler
import compiler.DefinedType

open class ASTVariableNode : ASTNode {
    val data: String
    val isLocal:Boolean

    constructor(data:String, expressionType: DefinedType, isLocal: Boolean = true) : super(expressionType) {
        Compiler.setNewVariableType(data, DefinedType(expressionType))
        this.data = data
        this.isLocal = isLocal

    }

    override fun retype(map: Map<String, DefinedType>){
        for (m in map)
            if (expType.typeString == m.key)
                expType = m.value
    }

    override fun copy(): ASTVariableNode {
        return ASTVariableNode(data, expType, isLocal)
    }

    open fun addType(type: DefinedType ): ASTVariableNode {
        Compiler.setVariableType(data, DefinedType(type))
        this.expType = type
        return this
    }

    override fun getType(): DefinedType {
        val defined = Compiler.tryGetDefinedType(data)
        if (defined != null)
            return defined.unDynamic()
        val compType = Compiler.getVariableType(data)
        if (compType != null)
            return compType.unDynamic()
        return expType.unDynamic()
    }

    override fun toString(): String {
        return "Variable=$data"
    }

    fun toDefineC(): String {
        if (isLocal && !Compiler.isDefined(data))
            Compiler.variables[Compiler.variables.size-1] += mapOf(data to DefinedType(expType))

        if (getType().typeString.contains("array")){
            val s = getType().typeString.split("-")
            return "${s[1]} $data[${s[2]}]"
        }
        if (getType().isAddress()){
            val s = getType().getPrimitive()
            return "${s} *$data"
        }
        return "${getType().toC()} $data"
    }
    override fun toC(sideEffect:Boolean): String = if (Compiler.isDefined(data) || !sideEffect) data else toDefineC()
}