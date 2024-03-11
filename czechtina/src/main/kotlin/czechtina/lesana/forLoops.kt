package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.LesanaBuilder
import cz.j_jzk.klang.parse.NodeID
import czechtina.grammar.GrammarToken
import czechtina.grammar.cAndCzechtinaRegex
import czechtina.grammar.czechtina

fun LesanaBuilder<ASTNode>.forLoops(
    line: NodeID<ASTNode>,
    variables: NodeID<ASTVariableNode>,
    types: NodeID<ASTUnaryNode>,
    r_expression: NodeID<ASTNode>,
    blockCode: NodeID<ASTUnaryNode>,
    endOfLine: NodeID<String>
) {
    line to rangedTypedFor(variables, types, r_expression, blockCode)
    line to rangedFor(variables, r_expression, blockCode)
    line to codedFor(line, r_expression, endOfLine, blockCode)
    line to inlineTypedRangedFor(variables, types, r_expression, line)
    line to inlineRangedFor(variables, r_expression, line)
    line to inlineCodedFor(line, r_expression, endOfLine)
}

fun LesanaBuilder<ASTNode>.rangedTypedFor(
    variables: NodeID<ASTVariableNode>,
    types: NodeID<ASTUnaryNode>,
    r_expression: NodeID<ASTNode>,
    blockCode: NodeID<ASTUnaryNode>
): LesanaBuilder.IntermediateNodeDefinition<ASTNode> = def(
    re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
    variables,
    re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
    types,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    r_expression,
    re(czechtina[GrammarToken.KEYWORD_RANGE_DEFINITION]!!),
    r_expression,
    blockCode
)
{ (_, v, _, t, _, min, def, max, block) -> ASTForNode(v.addType(t.getType()), t, min, def, max, block) }

fun LesanaBuilder<ASTNode>.inlineCodedFor(
    line: NodeID<ASTNode>,
    r_expression: NodeID<ASTNode>,
    endOfLine: NodeID<String>
): LesanaBuilder.IntermediateNodeDefinition<ASTNode> = def(
    re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
    line,
    r_expression,
    endOfLine,
    r_expression,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    line
) { (_, begin, cond, _, step, _, block) -> ASTForNode(begin, cond, step, ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)) }

fun LesanaBuilder<ASTNode>.rangedFor(
    variables: NodeID<ASTVariableNode>,
    r_expression: NodeID<ASTNode>,
    blockCode: NodeID<ASTUnaryNode>
): LesanaBuilder.IntermediateNodeDefinition<ASTNode> = def(
    re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
    variables,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    r_expression,
    re(czechtina[GrammarToken.KEYWORD_RANGE_DEFINITION]!!),
    r_expression,
    blockCode
)
{ (_, v, _, min, def, max, block) ->
    ASTForNode(
        v,
        ASTUnaryNode(ASTUnaryTypes.TYPE, min.getType(), min.getType()),
        min,
        def,
        max,
        block
    )
}

fun LesanaBuilder<ASTNode>.codedFor(
    line: NodeID<ASTNode>,
    r_expression: NodeID<ASTNode>,
    endOfLine: NodeID<String>,
    blockCode: NodeID<ASTUnaryNode>
): LesanaBuilder.IntermediateNodeDefinition<ASTNode> = def(
    re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
    line,
    r_expression,
    endOfLine,
    r_expression,
    blockCode
) { (_, begin, cond, _, step, block) -> ASTForNode(begin, cond, step, block) }

fun LesanaBuilder<ASTNode>.inlineRangedFor(
    variables: NodeID<ASTVariableNode>,
    r_expression: NodeID<ASTNode>,
    line: NodeID<ASTNode>
): LesanaBuilder.IntermediateNodeDefinition<ASTNode> = def(
    re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
    variables,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    r_expression,
    re(czechtina[GrammarToken.KEYWORD_RANGE_DEFINITION]!!),
    r_expression,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    line
)
{ (_, v, _, min, def, max, _, block) ->
    ASTForNode(
        v,
        ASTUnaryNode(ASTUnaryTypes.TYPE, min.getType(), min.getType()),
        min,
        def,
        max,
        ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)
    )
}

fun LesanaBuilder<ASTNode>.inlineTypedRangedFor(
    variables: NodeID<ASTVariableNode>,
    types: NodeID<ASTUnaryNode>,
    r_expression: NodeID<ASTNode>,
    line: NodeID<ASTNode>
): LesanaBuilder.IntermediateNodeDefinition<ASTNode> = def(
    re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
    variables,
    re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
    types,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    r_expression,
    re(czechtina[GrammarToken.KEYWORD_RANGE_DEFINITION]!!),
    r_expression,
    re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
    line
)
{ (_, v, _, t, _, min, def, max, _, block) ->
    ASTForNode(
        v,
        t,
        min,
        def,
        max,
        ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)
    )
}