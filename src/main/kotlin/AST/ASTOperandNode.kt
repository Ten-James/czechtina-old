package AST

import compiler.Compiler
import compiler.DefinedType
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

    override fun retype(map: Map<String, DefinedType>) {
        left.retype(map)
        right.retype(map)
        expType = Compiler.calcBinaryType(left, right, operand)
    }

    override fun toString(): String {
        return "'$operand', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}\n"
    }

    override fun copy(): ASTOperandNode {
        return ASTOperandNode(operand, left.copy(), right.copy())
    }

    override fun toC(): String {
        expType = Compiler.calcBinaryType(left, right, operand)
        return "${left?.toC()} ${Compiler.typeFromCzechtina(operand)} ${right?.toC()}"
    }
}