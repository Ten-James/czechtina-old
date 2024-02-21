package AST

import compiler.Compiler
import compiler.DefinedType

class ASTOperandNode(var operand: String, var left: ASTNode, var right: ASTNode) :
    ASTNode(Compiler.calcBinaryType(left, right, operand)) {

    override fun getType(): DefinedType {
        return Compiler.calcBinaryType(left, right, operand)
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

    override fun toC(sideEffect:Boolean): String {
        if (operand == "="){
            val rString = right.toC();
            expType = Compiler.calcBinaryType(left, right, operand)
            return "${left.toC()} = $rString"
        }

        expType = Compiler.calcBinaryType(left, right, operand)
        return "${left.toC()} ${Compiler.typeFromCzechtina(operand)} ${right?.toC()}"
    }
}