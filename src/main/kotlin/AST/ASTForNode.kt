package AST

import compiler.Compiler
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

    constructor(variable: ASTVariableNode, type: ASTUnaryNode, min: ASTTypedNode, rangeComparation: String , max: ASTTypedNode, body: ASTNode) {
        this.begin = ASTUnaryNode(ASTUnaryTypes.SEMICOLON,ASTOperandNode(czechtina[GrammarToken.OPERATOR_ASSIGN]!!, variable.addType(type.getType()), min) )


        val operand = when (rangeComparation) {
            "az" -> czechtina[GrammarToken.OPERATOR_LESS_OR_EQUAL]!!
            "do" -> czechtina[GrammarToken.OPERATOR_LESS]!!
            else -> ""
        }

        this.condition = ASTOperandNode(operand, variable, max)
        this.step = ASTOperandNode(czechtina[GrammarToken.OPERATOR_ASSIGN]!!, variable, ASTOperandNode(czechtina[GrammarToken.OPERATOR_PLUS]!!, variable, ASTUnaryNode(ASTUnaryTypes.LITERAL, 1, "int")))
        this.body = body
    }

    override fun toString(): String {
        return "For: \nbegin=${begin.toString().replace("\n","\n\t")}, \ncondition=${condition.toString().replace("\n","\n\t")}, \nstep=${step.toString().replace("\n","\n\t")}, \nbody=${body.toString().replace("\n","\n\t")}"
    }

    override fun toC() = "for (${begin.toC()} ${condition.toC()}; ${step.toC()}) ${body.toC()}"
}