package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import czechtina.AllComparation
import czechtina.GrammarToken
import czechtina.cAndCzechtinaRegex
import czechtina.czechtina

fun rightExpression(variables: NodeID<ASTUnaryNode>) = lesana {
    val literals = include(literals())
    val exp1 = NodeID<ASTNode>("expressions")
    val exp2 = NodeID<ASTNode>("expressions")
    val exp3 = NodeID<ASTNode>("expressions")
    val functionCalling = NodeID<ASTNode>("functionCalling")
    var para1 = NodeID<ASTNode>("paragraph")
    var para3 = NodeID<ASTNode>("paragraph")
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


    exp3 to def (re("\\["), listexp3, re("\\]")) { ASTUnaryNode(ASTUnaryTypes.ARRAY, it.v2) }


    functionCalling to def(re(czechtina[GrammarToken.KEYWORD_FUNCTION_CALL]!!), variables) { ASTUnaryNode(ASTUnaryTypes.NO_PARAM_CALL, it.v2) }
    functionCalling to def(variables, variables) { (v, e) -> ASTBinaryNode(ASTBinaryTypes.FUNCTION_CALL, v, e) }
    functionCalling to def(variables, exp3) { (v, e) -> ASTBinaryNode(ASTBinaryTypes.FUNCTION_CALL, v, e) }
    functionCalling to def(variables, listexp3) { (v, e) -> ASTBinaryNode(ASTBinaryTypes.FUNCTION_CALL, v, e) }


    sentence to def(functionCalling) { it.v1 }
    sentence to def(exp3) { it.v1 }
    sentence to def(variables) { it.v1 }

    para1 to def(para1, re(cAndCzechtinaRegex(AllComparation)), para1 )
    {
        (e1, o, e2) ->
            if (e1 is ASTOperandNode && Regex(cAndCzechtinaRegex(AllComparation)).matches(e1.operand))
                ASTOperandNode(czechtina[GrammarToken.OPERATOR_AND]!!, e1, ASTOperandNode(o,e1.right!!, e2))
            else if (e2 is ASTOperandNode && Regex(cAndCzechtinaRegex(AllComparation)).matches(e2.operand))
                ASTOperandNode(czechtina[GrammarToken.OPERATOR_AND]!!, ASTOperandNode(o,e1, e2.left!!),e2)
            else
                ASTOperandNode(o, e1, e2)
    }
    para1 to def(sentence) {it.v1}


    para3 to def(para3, re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))), para3)
    {
        ASTOperandNode(it.v2, it.v1, it.v3)
    }

    para3 to def(para1) {it.v1}

    inheritIgnoredREs()
    setTopNode(para3)
}