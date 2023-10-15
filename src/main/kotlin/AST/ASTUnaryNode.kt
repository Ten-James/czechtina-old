package AST

import compiler.Compiler
import czechtina.C
import czechtina.GrammarToken
import czechtina.cTypeFromCzechtina

enum class ASTUnaryTypes {
    LITERAL,
    VARIABLE,
    TYPE,
    TYPE_POINTER,
    RETURN,
    IMPORT,
    IMPORT_C,
    BRACKET,
    CURLY,
    SEMICOLON,
    JUST_C,
    ARRAY,
    STRING,
    IF,
    ELSE,
    ELSE_IF,
    NO_PARAM_CALL
}
class ASTUnaryNode : ASTTypedNode {
    var type:ASTUnaryTypes? = null
    var data:Any? = null

    constructor(type:ASTUnaryTypes, data:Any, expressionType: String = "none") : super(expressionType) {
        this.type = type
        this.data = data
    }

    override fun toString(): String {
        return "'$type', data=$data"
    }

    override fun toC(): String = when (type) {
        ASTUnaryTypes.LITERAL -> data.toString()
        ASTUnaryTypes.VARIABLE -> data.toString()
        ASTUnaryTypes.TYPE -> Compiler.typeFromCzechtina(data.toString())
        ASTUnaryTypes.TYPE_POINTER -> "${(data as ASTNode).toC()}*"
        ASTUnaryTypes.RETURN -> "${Compiler.grammar[GrammarToken.KEYWORD_RETURN]} ${(data as ASTNode).toC()}"
        ASTUnaryTypes.IMPORT -> "//xd ${data.toString()}"
        ASTUnaryTypes.IMPORT_C -> "#include \"${data.toString()}.h\""
        ASTUnaryTypes.BRACKET -> "(${(data as ASTNode).toC()})"
        ASTUnaryTypes.ARRAY -> "{${(data as ASTNode).toC()}}"
        ASTUnaryTypes.CURLY -> "{\n\t${(data as ASTNode).toC().replace("\n","\n\t")}\n}"
        ASTUnaryTypes.SEMICOLON -> "${(data as ASTNode).toC()};"
        ASTUnaryTypes.JUST_C -> (data as ASTNode).toC()
        ASTUnaryTypes.STRING -> "\"${data.toString()}\""
        ASTUnaryTypes.IF -> "${Compiler.grammar[GrammarToken.KEYWORD_IF]} (${(data as ASTNode).toC()})"
        ASTUnaryTypes.ELSE -> "${Compiler.grammar[GrammarToken.KEYWORD_ELSE]}"
        ASTUnaryTypes.ELSE_IF -> "${Compiler.grammar[GrammarToken.KEYWORD_ELSE]} ${Compiler.grammar[GrammarToken.KEYWORD_IF]} (${(data as ASTNode).toC()})"
        ASTUnaryTypes.NO_PARAM_CALL -> "${(data as ASTNode).toC()}()"
        else -> ""
    }
}