package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.LesanaBuilder
import cz.j_jzk.klang.parse.NodeID
import czechtina.GrammarToken
import czechtina.cAndCzechtinaRegex
import czechtina.czechtina

fun LesanaBuilder<ASTNode>.flowControl(
    line: NodeID<ASTNode>,
    r_expression: NodeID<ASTNode>,
    blockCode: NodeID<ASTUnaryNode>
) {
    // while

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_WHILE))),
        r_expression,
        blockCode
    ) { (_, exp, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.WHILE, exp), block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_WHILE))),
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    ) { (_, exp, _, block) ->
        ASTBinaryNode(
            ASTBinaryTypes.FLOW_CONTROL,
            ASTUnaryNode(ASTUnaryTypes.WHILE, exp),
            ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)
        )
    }


    // if

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        blockCode
    ) { (_, exp, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.IF, exp), block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    ) { (_, exp, _, block) ->
        ASTBinaryNode(
            ASTBinaryTypes.FLOW_CONTROL,
            ASTUnaryNode(ASTUnaryTypes.IF, exp),
            ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)
        )
    }

    // ELSE IF

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        blockCode
    ) { (_, _, exp, block) ->
        ASTBinaryNode(
            ASTBinaryTypes.FLOW_CONTROL,
            ASTUnaryNode(ASTUnaryTypes.ELSE_IF, exp),
            block
        )
    }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    ) { (_, _, exp, _, block) ->
        ASTBinaryNode(
            ASTBinaryTypes.FLOW_CONTROL,
            ASTUnaryNode(ASTUnaryTypes.ELSE_IF, exp),
            ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)
        )
    }

    // ELSE

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        blockCode
    ) { (_, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.ELSE, ""), block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        blockCode
    ) { (_, _, block) ->
        ASTBinaryNode(
            ASTBinaryTypes.FLOW_CONTROL,
            ASTUnaryNode(ASTUnaryTypes.ELSE, ""),
            ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)
        )
    }
}