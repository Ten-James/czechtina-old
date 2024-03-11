package czechtina.lesana

import AST.*
import compiler.Compiler
import compiler.types.*
import cz.j_jzk.klang.lesana.LesanaBuilder
import cz.j_jzk.klang.parse.NodeID
import czechtina.grammar.GrammarToken
import czechtina.grammar.czechtina

fun LesanaBuilder<ASTNode>.variableDefinition(
    varDefinition: NodeID<ASTNode>,
    variables: NodeID<ASTVariableNode>,
    types: NodeID<ASTUnaryNode>
) {
    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        re(czechtina[GrammarToken.TYPE_ARRAY]!!),
        re("<"),
        types,
        re(">")
    ) { (v, _, _, _, t, _) -> ASTStaticArrayDefinitionNode(t, v.addType(StaticArrayType(t.getType(),"")), "") }

    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        re(czechtina[GrammarToken.TYPE_ARRAY]!!),
        re("<"),
        types,
        re(","),
        re("[0-9]+"),
        re(">")
    ) { (v, _, _, _, t, _, s, _) -> ASTStaticArrayDefinitionNode(t, v.addType( StaticArrayType(t.getType(),s)), s) }


    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        types
    ) { (v, _, t) -> ASTVarDefinitionNode(v.addType(t.getType()), t) }


    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        variables
    ) { (v, _, t) ->
        if (Compiler.Types.contains(t.data)) ASTVarDefinitionNode(
            v.addType(Compiler.tryGetType(t.data) ?: throw Exception("ERROR")),
            t
        ) else throw Exception("[${Compiler.Types}]:Variable ${t.toC()}/is not defined as an type")
    }
}