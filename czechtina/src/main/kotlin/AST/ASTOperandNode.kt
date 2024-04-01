package AST

import compiler.Compiler
import compiler.types.Type

class ASTOperandNode(var operand: String, var left: ASTNode, var right: ASTNode) :
    ASTNode(Compiler.calcBinaryType(left, right, operand)) {

    override fun getType(): Type {
        return Compiler.calcBinaryType(left, right, operand)
    }

    override fun retype(map: Map<Type, Type>) {
        left.retype(map)
        right.retype(map)
        //expType = Compiler.calcBinaryType(left, right, operand)
    }

    override fun toString(): String {
        return "Operand: '$operand', \nleft=\n  ${left.toString().replace("\n","\n  ")}, \nright=\n  ${right.toString().replace("\n","\n  ")}"
    }

    override fun copy(): ASTOperandNode {
        return ASTOperandNode(operand, left.copy(), right.copy())
    }

    override fun toC(sideEffect:Boolean): String {
        if (operand == "="){
            val rString = right.toC();
            expType = Compiler.calcBinaryType(left, right, operand)
            return "${left.toC()} = $rString"
        }

        expType = Compiler.calcBinaryType(left, right, operand)
        return "${left.toC()} ${Compiler.typeFromCzechtina(operand)} ${right.toC()}"
    }
}