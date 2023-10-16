package AST

import compiler.Compiler
import czechtina.cTypeFromCzechtina

class ASTOperandNode : ASTTypedNode {
    var operand:String
    var left: ASTTypedNode
    var right: ASTTypedNode

    constructor(operand:String, left:ASTTypedNode, right:ASTTypedNode) : super(Compiler.calcBinaryType(left, right, operand)) {
        this.operand = operand
        this.left = left
        this.right = right
    }

    override fun toString(): String {
        return "'$operand', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(): String = when (operand) {
        else -> "${left?.toC()} ${Compiler.typeFromCzechtina(operand)} ${right?.toC()}"
    }
}