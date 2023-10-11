package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.useful.list
import czechtina.GrammarToken
import czechtina.cAndCzechtinaRegex

fun rightExpression(variables: NodeID<ASTUnaryNode>) = lesana<ASTNode> {
    val literals = include(literals())
    val exp1 = NodeID<ASTNode>("expressions")
    val exp2 = NodeID<ASTNode>("expressions")
    val exp3 = NodeID<ASTNode>("expressions")
    val functionCalling = NodeID<ASTNode>("functionCalling")
    val sentence = NodeID<ASTNode>("sentence")
    val listexp3 = include(listAble(listOf(exp3, variables)))

    exp1 to def(literals) { it.v1 }

    exp2 to def(exp2, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_MULTIPLY, GrammarToken.OPERATOR_DIVIDE, GrammarToken.OPERATOR_MODULO))), exp1) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp2 to def(variables, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_MULTIPLY, GrammarToken.OPERATOR_DIVIDE, GrammarToken.OPERATOR_MODULO))), exp2) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp2 to def(exp2, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_MULTIPLY, GrammarToken.OPERATOR_DIVIDE, GrammarToken.OPERATOR_MODULO))), variables) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp2 to def(variables, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_MULTIPLY, GrammarToken.OPERATOR_DIVIDE, GrammarToken.OPERATOR_MODULO))), variables) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp2 to def(exp1) { it.v1 }

    exp3 to def(exp3, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_PLUS, GrammarToken.OPERATOR_MINUS))), exp2) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp3 to def(variables, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_PLUS, GrammarToken.OPERATOR_MINUS))), exp3) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp3 to def(exp3, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_PLUS, GrammarToken.OPERATOR_MINUS))), variables) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp3 to def(variables, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_PLUS, GrammarToken.OPERATOR_MINUS))), variables) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp3 to def(exp2) { it.v1 }

    exp1 to def (re("\\(") , sentence, re("\\)")) { ASTUnaryNode(ASTUnaryTypes.BRACKET, it.v2) }
    exp1 to def (re("\\(") , functionCalling, re("\\)")) { ASTUnaryNode(ASTUnaryTypes.JUST_C, it.v2) }

    functionCalling to def(variables, variables) { (v, e) -> ASTBinaryNode(ASTBinaryTypes.FUNCTION_CALL, v, e) }
    functionCalling to def(variables, exp3) { (v, e) -> ASTBinaryNode(ASTBinaryTypes.FUNCTION_CALL, v, e) }
    functionCalling to def(variables, listexp3) { (v, e) -> ASTBinaryNode(ASTBinaryTypes.FUNCTION_CALL, v, e) }


    sentence to def(functionCalling) { it.v1 }
    sentence to def(exp3) { it.v1 }
    sentence to def(variables) { it.v1 }
    inheritIgnoredREs()
    setTopNode(sentence)
}