package AST

import compiler.Compiler
import compiler.DefinedType
import czechtina.grammar.GrammarToken


enum class ASTBinaryTypes {
    FLOW_CONTROL,
    TYPE_DEFINITION,
}

open class ASTBinaryNode(var type: ASTBinaryTypes, var left: ASTNode, var right: ASTNode) :
    ASTNode(DefinedType("none")) {


    override fun retype(map: Map<String, DefinedType>) {
        left.retype(map)
        right.retype(map)
    }

    override fun copy(): ASTBinaryNode {
        return ASTBinaryNode(type, left.copy(), right.copy())
    }

    override fun toString(): String {
        return "'$type', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(sideEffect:Boolean): String = when (type) {
        ASTBinaryTypes.FLOW_CONTROL -> "${left.toC()} ${right.toC()}"
        ASTBinaryTypes.TYPE_DEFINITION -> "${Compiler.grammar[GrammarToken.KEYWORD_TYPE_DEFINITION]} ${left.toC()} ${right.toC()};"
    }
}