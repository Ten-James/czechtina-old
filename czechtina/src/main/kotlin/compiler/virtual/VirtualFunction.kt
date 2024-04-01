package compiler.virtual

import AST.*
import Printer
import compiler.Compiler
import compiler.types.*
import czechtina.grammar.GrammarToken
import czechtina.grammar.czechtina


interface VirtualFunction {
    val name: String
    fun getReturnType(params: ASTNode?): Type
    fun toC(params: ASTNode?):String
}

class NewFunction : VirtualFunction {
    override val name = "new"

    private fun getReturnTypeInternal (params: ASTNode?) = when {
        params!!.getType() is StructureType -> DynamicStructureType((params.getType() as StructureType).getStructName());
        params is ASTUnaryNode && params.type == ASTUnaryTypes.TYPE -> DynamicPointerType(params.getType());
        else -> DynamicPointerType(VoidType());
    }

    override fun getReturnType(params: ASTNode?) = when {
        params is ASTListNode && params.nodes.size == 2 && (params.nodes[0] as ASTUnaryNode).type == ASTUnaryTypes.TYPE -> DynamicPointerType(params.nodes[0].getType());
        params is ASTListNode && params.nodes.size == 2 && (params.nodes[0] as ASTUnaryNode).type == ASTUnaryTypes.TYPE_POINTER -> DynamicPointerType(params.nodes[0].getType());
        else -> getReturnTypeInternal(params)
    }


    override fun toC(params: ASTNode?): String = when {
        params is ASTListNode && params.nodes.size == 2 && (params.nodes[0] as ASTUnaryNode).type == ASTUnaryTypes.TYPE -> "(${params.nodes[0].getType().toC()}*)malloc(${params.nodes[1].toC()} * sizeof(${params.nodes[0].getType().toC()}))"
        params is ASTListNode && params.nodes.size == 2 && (params.nodes[0] as ASTUnaryNode).type == ASTUnaryTypes.TYPE_POINTER -> "(${params.nodes[0].getType().toC()}*)malloc(${params.nodes[1].toC()} * sizeof(${params.nodes[0].getType().toC()}))"
        else -> toCInternal(params)
    }

    private fun toCInternal(params: ASTNode?) = when {
        params!!.getType() is StructureType -> "(${params.getType().toC()})malloc(sizeof(${(params.getType()as StructureType).getStructName()}))"
        params is ASTUnaryNode && params.type == ASTUnaryTypes.TYPE ->"(${params.getType().toC()} *)malloc(sizeof(${params.getType().toC()}))"
        else -> "malloc(${params.toC()})"
    }

}

class InCFuntion : VirtualFunction {
    override val name = "inC"
    override fun getReturnType(params: ASTNode?) = InvalidType()
    override fun toC(params: ASTNode?): String = (params as ASTUnaryNode).data.toString() ?: ""
}

class PredejFunction : VirtualFunction {
    override val name = "predej"
    override fun getReturnType(params: ASTNode?) = DynamicPointerType(((params!! as ASTVariableNode).getType() as PointerType).toDereference())
    override fun toC(params: ASTNode?):String {
        val body = "${params?.toC()}"
        Compiler.variables[Compiler.variables.size - 1].remove(params?.toC()!!)
        return body
    }
}

class HodnotaFunction : VirtualFunction {
    override val name = czechtina[GrammarToken.TYPE_VALUE]!!
    override fun getReturnType(params: ASTNode?) = when  {
        params!!.getType() is PointerType -> (params.getType() as PointerType).toDereference()
        else -> throw Exception("cant use in pointer type")
    }
    override fun toC(params: ASTNode?) = "*(${params!!.toC()})"
}

class AdresaFunction : VirtualFunction {
    override val name = czechtina[GrammarToken.TYPE_ADDRESS]!!
    override fun getReturnType(params: ASTNode?) = PointerType(params!!.getType())
    override fun toC(params: ASTNode?) = "&${params!!.toC()}"
}

class ConstFunction: VirtualFunction {
    override val name = "const"
    override fun getReturnType(params: ASTNode?) = TODO()
    override fun toC(params: ASTNode?): String {
        if (params is ASTVariableNode){
            if (params.getType() !is PointerType)
                throw Exception("Const can be applied only to objects")
        return params.toC()
    }
        throw Exception("Const can be applied only to variables")

    }
}
class PrintfFunction: VirtualFunction {
    override val name = "printf"
    override fun getReturnType(params: ASTNode?) = InvalidType()
    override fun toC(params: ASTNode?) = "printf(${params!!.toC()})"
}

class PrintFunction: VirtualFunction {
    override val name = "print"
    override fun getReturnType(params: ASTNode?) = InvalidType()
    override fun toC(params: ASTNode?) = when {
        params is ASTListNode -> params.nodes.joinToString(";\n") {otherToC(it)}
        else -> otherToC(params)
    }
    fun otherToC(params: ASTNode?):String {
        val type = params!!.getType()

        if (type is PrimitiveType) {
            when {
                type.toC() == "int" -> return "printf(\"%d\",${params.toC()})"
                type.toC() == "double" -> return "printf(\"%f\",${params.toC()})"
                type.toC() == "bool" -> return "(${params.toC()}? fputs(\"true\",stdout): fputs(\"false\",stdout))"
                type.toC() == "char" -> return "printf(\"%c\",${params.toC()})"
                type.toC() == "string" -> return "fputs(${params.toC()},stdout)"
                else -> return "/*${params.toC()}*/"
            }
        }
        if (type is PointerType) {
            return "printf(\"%x\",${params.toC()})"
        }

        Printer.err("Invalid print type: $type ${type.toC()}")

        return "/*INVALID PRINT*/"
    }
}

class TypeOfFunction: VirtualFunction {
    override val name = "typeof"
    override fun getReturnType(params: ASTNode?) = PrimitiveType("string")
    override fun toC(params: ASTNode?) = "\"${params!!.getType()}\""
}

class PrintlnFunction: VirtualFunction {
    override val name = "println"
    override fun getReturnType(params: ASTNode?) = InvalidType()
    override fun toC(params: ASTNode?) = PrintFunction().toC(params)+";fputs(\"\\n\",stdout)"
}


class ThrowFunction: VirtualFunction {
    override val name = "throw"
    override fun getReturnType(params: ASTNode?) = InvalidType()
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
    PrintlnFunction(),
    TypeOfFunction()
)

fun getVirtualFunction(name: String): VirtualFunction? {
    for (f in AllVirtualFunction)
        if (f.name == name)
            return f
    return null
}