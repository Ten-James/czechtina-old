package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.LesanaBuilder
import cz.j_jzk.klang.parse.NodeID
import czechtina.grammar.GrammarToken
import czechtina.grammar.cAndCzechtinaRegex


fun LesanaBuilder<ASTNode>.programLine(
    line: NodeID<ASTNode>,
    variables: NodeID<ASTVariableNode>,
    types: NodeID<ASTUnaryNode>,
    r_expression: NodeID<ASTNode>,
    blockCode: NodeID<ASTUnaryNode>,
    endOfLine: NodeID<String>,
    varDefinition: NodeID<ASTNode>,
    programLines: NodeID<ASTProgramLines>
) {
    // FOR LOOP
    forLoops(line, variables, types, r_expression, blockCode, endOfLine)



    line to def(
        varDefinition,
        endOfLine
    ) { (v, _) -> ASTUnaryNode(ASTUnaryTypes.SEMICOLON, v) }

    line to def(
        varDefinition,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        r_expression,
        endOfLine
    ) { (v, o, l) -> ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTOperandNode(o, v, l)) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_RETURN))),
        r_expression,
        endOfLine
    ) { ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, it.v2)) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_RETURN))),
        endOfLine
    ) {
        ASTUnaryNode(
            ASTUnaryTypes.SEMICOLON,
            ASTUnaryNode(ASTUnaryTypes.RETURN, ASTUnaryNode(ASTUnaryTypes.LITERAL, ""))
        )
    }


    flowControl(line, r_expression, blockCode)



    line to def(
        r_expression,
        endOfLine
    ) { (l) -> ASTUnaryNode(ASTUnaryTypes.SEMICOLON, l) }




    blockCode to def(re("{"), programLines, re("}")) {
        ASTUnaryNode(ASTUnaryTypes.CURLY, it.v2)
    }

    programLines to def(line, programLines) { ASTProgramLines(listOf(it.v1) + it.v2.programLines) }
    programLines to def(line) { ASTProgramLines(listOf(it.v1)) }
}
