package AST

import czechtina.GrammarToken
import czechtina.czechtina

class ASTForNode : ASTNode {
    var begin: ASTNode
    var condition: ASTNode
    var step: ASTNode
    var body: ASTNode

    constructor(begin: ASTNode, condition: ASTNode, step: ASTNode, body: ASTNode) {
        this.begin = begin
        this.condition = condition
        this.step = step
        this.body = body
    }

    constructor(variable: ASTNode, type: ASTNode, min: ASTNode, rangeComparation: String , max: ASTNode, body: ASTNode) {
        this.begin = ASTOperandNode(czechtina[GrammarToken.OPERATOR_ASSIGN]!!, ASTBinaryNode(ASTBinaryTypes.VAR_DEFINITION, type, variable), min)

        val operand = when (rangeComparation) {
            "az" -> czechtina[GrammarToken.OPERATOR_LESS_OR_EQUAL]!!
            "do" -> czechtina[GrammarToken.OPERATOR_LESS]!!
            else -> ""
        }

        this.condition = ASTOperandNode(operand, variable, max)
        this.step = ASTOperandNode(czechtina[GrammarToken.OPERATOR_ASSIGN]!!, variable, ASTOperandNode(czechtina[GrammarToken.OPERATOR_PLUS]!!, variable, ASTUnaryNode(ASTUnaryTypes.LITERAL, 1)))
        this.body = body
    }

    override fun toString(): String {
        return "For: \nbegin=${begin.toString().replace("\n","\n\t")}, \ncondition=${condition.toString().replace("\n","\n\t")}, \nstep=${step.toString().replace("\n","\n\t")}, \nbody=${body.toString().replace("\n","\n\t")}"
    }

    override fun toC(): String = "for (${begin.toC()} ${condition.toC()}; ${step.toC()}) ${body.toC()}"
}