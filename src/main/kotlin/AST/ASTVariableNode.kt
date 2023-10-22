package AST

import compiler.Compiler
import compiler.DefinedType

open class ASTVariableNode : ASTTypedNode {
    val data: String
    val isLocal:Boolean

    constructor(data:String, expressionType: DefinedType, isLocal: Boolean = true) : super(expressionType) {
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

    fun addType(type: DefinedType ): ASTVariableNode {
        this.expType = type
        return this
    }

    override fun getType(): DefinedType {
        val compType = Compiler.getVariableType(data)
        if (compType != null)
            return compType
        return super.getType()
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
        if (getType().typeString.contains("pointer")){
            val s = getType().typeString.split("-")
            return "${s[1]} *$data"
        }
        if (getType().isHeap){
            val s = getType().typeString.split("-")
            return "${s[1]} *$data"
        }

        return "${getType().typeString} $data"
    }
    override fun toC(): String = if (Compiler.isDefined(data)) data else toDefineC()
}