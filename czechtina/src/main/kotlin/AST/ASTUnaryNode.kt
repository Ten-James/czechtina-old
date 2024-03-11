package AST

import compiler.Compiler
import compiler.types.InvalidType
import compiler.types.Type
import compiler.types.VoidType
import czechtina.grammar.GrammarToken

enum class ASTUnaryTypes {
    LITERAL,
    VARIABLE,
    TYPE,
    TYPE_POINTER,
    MINUS,
    RETURN,
    IMPORT,
    IMPORT_C,
    BRACKET,
    CURLY,
    CURLY_UNSCOPE,
    SEMICOLON,
    JUST_C,
    ARRAY,
    STRING,
    IF,
    ELSE,
    ELSE_IF,
    WHILE,
    NEW_LINE,
    NO_PARAM_CALL,
    POST_INCREMENT,
    POST_DECREMENT,
    PRE_INCREMENT,
    PRE_DECREMENT,
}

class ASTUnaryNode(type: ASTUnaryTypes, data: Any, expressionType: Type = InvalidType()) :
    ASTNode(expressionType) {
    var type: ASTUnaryTypes? = type
    var data: Any? = data

    override fun getType(): Type {
        if (type == ASTUnaryTypes.RETURN && super.getType() is InvalidType)
            return VoidType()
        if (type == ASTUnaryTypes.TYPE)
            return Compiler.tryGetType(data.toString()) ?: super.getType()
        if (type == ASTUnaryTypes.TYPE_POINTER)
            return super.getType()
        if (data is ASTNode)
            return (data as ASTNode).getType()
        return super.getType()
    }


    override fun toString(): String = when (type) {
        ASTUnaryTypes.TYPE -> getType().toString()
        ASTUnaryTypes.CURLY -> "$data"
        ASTUnaryTypes.CURLY_UNSCOPE -> "$data"
        ASTUnaryTypes.SEMICOLON -> data.toString()
        ASTUnaryTypes.ARRAY -> "{$data}"
        ASTUnaryTypes.LITERAL -> "LIT[ $data ]"
        else -> "'$type', data=$data, exp=${getType()}"
    }


    override fun retype(map: Map<Type, Type>) {
        if (data is ASTNode)
            (data as ASTNode).retype(map)
        if (type == ASTUnaryTypes.TYPE) {
            for (m in map) {
                //data = data.toString().replace(m.key, m.value.typeString)
                if (expType == m.key)
                    expType = m.value
            }
        }

    }

    override fun copy(): ASTUnaryNode {
        return ASTUnaryNode(type!!, data!!, expType)
    }

    override fun toC(sideEffect: Boolean): String = when (type) {
        ASTUnaryTypes.LITERAL -> data.toString()
        ASTUnaryTypes.VARIABLE -> data.toString()
        ASTUnaryTypes.TYPE -> getType().toC() //TODO
        ASTUnaryTypes.TYPE_POINTER -> "${(data as ASTNode).toC()}*"
        ASTUnaryTypes.RETURN -> "${Compiler.grammar[GrammarToken.KEYWORD_RETURN]} ${(data as ASTNode).toC()}"
        ASTUnaryTypes.IMPORT -> "//xd ${data.toString()}"
        ASTUnaryTypes.IMPORT_C -> "#include<${data.toString()}.h>"
        ASTUnaryTypes.BRACKET -> "(${(data as ASTNode).toC()})"
        ASTUnaryTypes.ARRAY -> "{${(data as ASTNode).toC()}}"
        ASTUnaryTypes.CURLY -> {
            val body = "${Compiler.scopePush()}${(data as ASTNode).toC()}"
            if (body.contains("return"))
                "{\n\t${body.replace("\n", "\n\t").replace("return", "${Compiler.scopePop(true, "", "")}return")}\n}"
            else
                "{\n\t${body.replace("\n", "\n\t")}${Compiler.scopePop(true)}\n}"
        }

        ASTUnaryTypes.CURLY_UNSCOPE -> {
            val body = (data as ASTNode).toC()
            if (body.contains("return"))
                "{\n\t${
                    body.replace("\n", "\n\t").replace("return", "${Compiler.scopePop(true, "", "")}return")
                }\n}"
            else
                "{\n\t${body.replace("\n", "\n\t")}${Compiler.scopePop(true)}\n}"
        }

        ASTUnaryTypes.SEMICOLON -> "${(data as ASTNode).toC()};"
        ASTUnaryTypes.MINUS -> "-${(data as ASTNode).toC()}"
        ASTUnaryTypes.JUST_C -> (data as ASTNode).toC()
        ASTUnaryTypes.STRING -> "\"${data.toString()}\""
        ASTUnaryTypes.IF -> "${Compiler.grammar[GrammarToken.KEYWORD_IF]} (${(data as ASTNode).toC()})"
        ASTUnaryTypes.WHILE -> "${Compiler.grammar[GrammarToken.KEYWORD_WHILE]} (${(data as ASTNode).toC()})"
        ASTUnaryTypes.ELSE -> "${Compiler.grammar[GrammarToken.KEYWORD_ELSE]}"
        ASTUnaryTypes.ELSE_IF -> "${Compiler.grammar[GrammarToken.KEYWORD_ELSE]} ${Compiler.grammar[GrammarToken.KEYWORD_IF]} (${(data as ASTNode).toC()})"
        ASTUnaryTypes.NEW_LINE -> "\n\t${(data as ASTNode).toC().replace("\n", "\n\t")}"
        ASTUnaryTypes.NO_PARAM_CALL -> "${(data as ASTNode).toC()}()"
        ASTUnaryTypes.POST_INCREMENT -> "${(data as ASTNode).toC()}++"
        ASTUnaryTypes.POST_DECREMENT -> "${(data as ASTNode).toC()}--"
        ASTUnaryTypes.PRE_INCREMENT -> "++${(data as ASTNode).toC()}"
        ASTUnaryTypes.PRE_DECREMENT -> "--${(data as ASTNode).toC()}"
        else -> ""
    }
}