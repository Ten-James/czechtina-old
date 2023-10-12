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
    SEMICOLON,
    JUST_C,
    ARRAY,
    STRING
}
class ASTUnaryNode : ASTNode {
    var type:ASTUnaryTypes? = null
    var data:Any? = null

    constructor(type:ASTUnaryTypes, data:Any) {
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
        ASTUnaryTypes.SEMICOLON -> "${(data as ASTNode).toC()};"
        ASTUnaryTypes.JUST_C -> (data as ASTNode).toC()
        ASTUnaryTypes.STRING -> "\"${data.toString()}\""
        else -> ""
    }
}