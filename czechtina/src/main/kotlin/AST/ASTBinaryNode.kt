package AST

import compiler.Compiler
import compiler.types.InvalidType
import compiler.types.Type
import czechtina.grammar.GrammarToken


enum class ASTBinaryTypes {
    FLOW_CONTROL,
    TYPE_DEFINITION,
}

open class ASTBinaryNode(var type: ASTBinaryTypes, var left: ASTNode, var right: ASTNode) :
    ASTNode(InvalidType()) {


    override fun retype(map: Map<Type, Type>) {
        left.retype(map)
        right.retype(map)
    }

    override fun copy(): ASTBinaryNode {
        return ASTBinaryNode(type, left.copy(), right.copy())
    }

    override fun toString(): String {
        return "Binary: '$type', \nleft=\n  ${left.toString().replace("\n","\n  ")}, \nright=\n  ${right.toString().replace("\n","\n  ")}"
    }

    override fun toC(sideEffect:Boolean): String = when (type) {
        ASTBinaryTypes.FLOW_CONTROL -> "${left.toC()} ${right.toC()}"
        ASTBinaryTypes.TYPE_DEFINITION -> "${Compiler.grammar[GrammarToken.KEYWORD_TYPE_DEFINITION]} ${left.toC()} ${right.toC()};"
    }
}