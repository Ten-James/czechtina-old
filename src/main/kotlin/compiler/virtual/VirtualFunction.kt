package compiler.virtual

import AST.*
import compiler.Compiler
import compiler.DefinedType
import czechtina.grammar.GrammarToken
import czechtina.grammar.czechtina


interface VirtualFunction {
    val name: String
    fun getReturnType(params: ASTNode?):DefinedType
    fun toC(params: ASTNode?):String
}

class NewFunction : VirtualFunction {
    override val name = "new"

    private fun getReturnTypeInternal (params: ASTNode?) = when {
        params is ASTUnaryNode && params.type == ASTUnaryTypes.TYPE -> DefinedType("dynamic-${params.getType().getPrimitive()}", true, false, false)
        params!!.getType().isStructured -> params.getType().toDynamic()
        else -> DefinedType("dynamic-void",true, false, false)
    }

    override fun getReturnType(params: ASTNode?) = when {
        params is ASTListNode && params.nodes.size == 2 && (params.nodes[0] as ASTUnaryNode).type == ASTUnaryTypes.TYPE -> DefinedType("dynamic-pointer-${params.nodes[0].getType().getPrimitive()}", true, false, true)
        else -> getReturnTypeInternal(params)
    }


    override fun toC(params: ASTNode?): String = when {
        params is ASTListNode && params.nodes.size == 2 && (params.nodes[0] as ASTUnaryNode).type == ASTUnaryTypes.TYPE -> "(${params.nodes[0].getType().getPrimitive()}**)malloc(${params.nodes[1].toC()} * sizeof(${params.nodes[0].getType().getPrimitive()} *))"
        else -> toCInternal(params)
    }

    private fun toCInternal(params: ASTNode?) = when {
        params is ASTUnaryNode && params.type == ASTUnaryTypes.TYPE ->"(${params.getType().getPrimitive()} *)malloc(sizeof(${params.getType().getPrimitive()}))"
        params!!.getType().isStructured -> "(${params.getType().getPrimitive()} *)malloc(sizeof(${params.getType().getPrimitive()}))"
        else -> "malloc(${params.toC()})"
    }
}

class InCFuntion : VirtualFunction {
    override val name = "inC"
    override fun getReturnType(params: ASTNode?) = DefinedType("none")
    override fun toC(params: ASTNode?) = params!!.toC()
}

class PredejFunction : VirtualFunction {
    override val name = "predej"
    override fun getReturnType(params: ASTNode?) = (params!! as ASTVariableNode).getType().toDynamic()
    override fun toC(params: ASTNode?):String {
        val body = "${params?.toC()}"
        Compiler.variables[Compiler.variables.size - 1][params?.toC()!!]!!.dealocated = true
        return body
    }
}

class HodnotaFunction : VirtualFunction {
    override val name = czechtina[GrammarToken.TYPE_VALUE]!!
    override fun getReturnType(params: ASTNode?) = params!!.getType().toDereference()
    override fun toC(params: ASTNode?) = "*(${params!!.toC()})"
}

class AdresaFunction : VirtualFunction {
    override val name = czechtina[GrammarToken.TYPE_ADDRESS]!!
    override fun getReturnType(params: ASTNode?) = params!!.getType().toPointer()
    override fun toC(params: ASTNode?) = "&${params!!.toC()}"
}

class ConstFunction: VirtualFunction {
    override val name = "const"
    override fun getReturnType(params: ASTNode?) = (params!! as ASTVariableNode).getType().toConst()
    override fun toC(params: ASTNode?): String {
        if (params is ASTVariableNode){
            if (!params.getType().isPointer())
                throw Exception("Const can be applied only to objects")
        return params.toC()
    }
        throw Exception("Const can be applied only to variables")

    }
}
class PrintfFunction: VirtualFunction {
    override val name = "printf"
    override fun getReturnType(params: ASTNode?) = DefinedType("none")
    override fun toC(params: ASTNode?) = "printf(${params!!.toC()})"
}

class PrintFunction: VirtualFunction {
    override val name = "print"
    override fun getReturnType(params: ASTNode?) = DefinedType("none")
    override fun toC(params: ASTNode?) = when {
        params is ASTListNode -> (params as ASTListNode).nodes.joinToString(";\n") {otherToC(it)} 
        else -> otherToC(params)
    }
    fun otherToC(params: ASTNode?) = when {
        params!!.getType().typeString == "int" -> "printf(\"%d\",${params.toC()})"
        params.getType().typeString == "bool" -> "(${params.toC()}? puts(\"true\"): puts(\"false\"))"
        params.getType().typeString == "char" -> "printf(\"%c\",${params.toC()})"
        params.getType().typeString == "string" -> "puts(${params.toC()})"
        else -> "/*${params.toC()}*/"
    }
}

class PrintlnFunction: VirtualFunction {
    override val name = "println"
    override fun getReturnType(params: ASTNode?) = DefinedType("none")
    override fun toC(params: ASTNode?) = PrintFunction().toC(params)+";\nputs(\"\\n\")"
}


class ThrowFunction: VirtualFunction {
    override val name = "throw"
    override fun getReturnType(params: ASTNode?) = DefinedType("none")
    override fun toC(params: ASTNode?) = "printf(${params?.toC()}); exit(1)"
}


val AllVirtualFunction = listOf(
    ConstFunction(),
    NewFunction(),
    InCFuntion(),
    PredejFunction(),
    HodnotaFunction(),
    AdresaFunction(),
    ThrowFunction(),
    PrintfFunction(),
    PrintFunction(),
    PrintlnFunction()
)

fun getVirtualFunction(name: String): VirtualFunction? {
    for (f in AllVirtualFunction)
        if (f.name == name)
            return f
    return null
}