package AST

class ASTOperandNode : ASTBinaryNode {
    var operand:String? = null

    constructor(operand:String, left:ASTNode, right:ASTNode) : super("Operand", left, right) {
        this.operand = operand
    }

override fun toString(): String {
        return "\n'$type' '$operand', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}"
    }
}