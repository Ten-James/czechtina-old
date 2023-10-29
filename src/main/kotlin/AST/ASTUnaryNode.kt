package AST

import compiler.Compiler
import compiler.DefinedType
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
    NO_PARAM_CALL
}
class ASTUnaryNode : ASTNode {
    var type:ASTUnaryTypes? = null
    var data:Any? = null

    constructor(type:ASTUnaryTypes, data:Any, expressionType: DefinedType = DefinedType("none")) : super(expressionType) {
        this.type = type
        this.data = data
    }

    override fun getType(): DefinedType {
        if (type == ASTUnaryTypes.TYPE)
            return Compiler.tryGetDefinedType(data.toString()) ?: super.getType()
        if (type == ASTUnaryTypes.TYPE_POINTER)
            return expType
        if (data is ASTNode)
            return (data as ASTNode).getType()
        return super.getType()
    }


    override fun toString(): String {
        return "'$type', data=$data, exp=${getType()}"
    }

    override fun retype(map: Map<String, DefinedType>) {
        if (data is ASTNode)
            (data as ASTNode).retype(map)
        if (type == ASTUnaryTypes.TYPE){
            for (m in map){
                data = data.toString().replace(m.key, m.value.typeString)
                if (expType.typeString == m.key)
                    expType = m.value
            }
        }

    }

    override fun copy(): ASTUnaryNode {
        return ASTUnaryNode(type!!, data!!, expType)
    }
    override fun toC(sideEffect:Boolean): String = when (type) {
        ASTUnaryTypes.LITERAL -> data.toString()
        ASTUnaryTypes.VARIABLE -> data.toString()
        ASTUnaryTypes.TYPE -> if (getType().isTemplate()) getType().typeString else getType().toC()
        ASTUnaryTypes.TYPE_POINTER -> "${(data as ASTNode).toC()}*"
        ASTUnaryTypes.RETURN -> "${Compiler.grammar[GrammarToken.KEYWORD_RETURN]} ${(data as ASTNode).toC()}"
        ASTUnaryTypes.IMPORT -> "//xd ${data.toString()}"
        ASTUnaryTypes.IMPORT_C -> "#include \"${data.toString()}.h\""
        ASTUnaryTypes.BRACKET -> "(${(data as ASTNode).toC()})"
        ASTUnaryTypes.ARRAY -> "{${(data as ASTNode).toC()}}"
        ASTUnaryTypes.CURLY -> {
            val body = "${Compiler.scopePush()}${(data as ASTNode).toC()}"
            if (body.contains("return"))
                "\n\t${body.replace("\n","\n\t").replace("return","${Compiler.scopePop(true,"", "")}return")}\n}"
            else
                "{\n\t${body.replace("\n","\n\t")}${Compiler.scopePop(true)}\n}"
        }
        ASTUnaryTypes.CURLY_UNSCOPE -> {
            val body = (data as ASTNode).toC()
            if (body.contains("return"))
                "{\n\t${
                    body.replace("\n", "\n\t").replace("return", "${Compiler.scopePop(true,"","")}return")
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
        ASTUnaryTypes.NEW_LINE -> "\n\t${(data as ASTNode).toC().replace("\n","\n\t")}"
        ASTUnaryTypes.NO_PARAM_CALL -> "${(data as ASTNode).toC()}()"
        else -> ""
    }
}