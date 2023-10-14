package AST

import compiler.Compiler
import czechtina.cTypeFromCzechtina

class ASTOperandNode : ASTBinaryNode {
    var operand:String

    constructor(operand:String, left:ASTNode, right:ASTNode) : super(null, left, right) {
        this.operand = operand
    }

    override fun toString(): String {
        return "'$operand', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(): String = when (operand) {
        else -> "${left?.toC()} ${Compiler.typeFromCzechtina(operand)} ${right?.toC()}"
    }
}