package AST

import compiler.Compiler
import compiler.DefinedType
import czechtina.GrammarToken

class ASTVariableNode : ASTTypedNode {
    val data: String
    val isLocal:Boolean

    constructor(data:String, expressionType: String = "none", isLocal: Boolean = true) : super(expressionType) {
        this.data = data
        this.isLocal = isLocal

    }

    fun addType(type: String ): ASTVariableNode {
        this.expType = type
        return this
    }

    override fun getType(): String {
        val compType = Compiler.getVariableType(data)
        if (compType != "")
            return compType
        return super.getType()
    }

    override fun toString(): String {
        return "Variable=$data"
    }

    fun toDefineC(): String {
        if (isLocal && !Compiler.isDefined(data))
            Compiler.variables[Compiler.variables.size-1] += mapOf(data to DefinedType(getType(), getType().contains("dynamic")))

        if (getType().contains("array")){
            val s = getType().split("-")
            return "${s[1]} $data[${s[2]}]"
        }
        if (getType().contains("pointer")){
            val s = getType().split("-")
            return "${s[1]} *$data"
        }
        if (getType().contains("dynamic")){
            val s = getType().split("-")
            return "${s[1]} *$data"
        }

        return "${getType()} $data"
    }
    override fun toC(): String = if (Compiler.isDefined(data)) data else toDefineC()
}