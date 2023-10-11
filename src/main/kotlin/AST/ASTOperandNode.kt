package AST

import czechtina.cTypeFromCzechtina

class ASTOperandNode : ASTBinaryNode {
    var operand:String

    constructor(operand:String, left:ASTNode, right:ASTNode) : super(null, left, right) {
        this.operand = operand
    }

    override fun toString(): String {
        return "\n'$operand', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}"
    }

    override fun toC(): String = when (operand) {
        else -> "${left?.toC()} ${cTypeFromCzechtina(operand)} ${right?.toC()}"
    }
}