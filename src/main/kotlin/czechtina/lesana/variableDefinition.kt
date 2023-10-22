package czechtina.lesana

import AST.*
import compiler.Compiler
import compiler.DefinedType
import cz.j_jzk.klang.lesana.LesanaBuilder
import cz.j_jzk.klang.parse.NodeID
import czechtina.GrammarToken
import czechtina.czechtina

fun LesanaBuilder<ASTNode>.variableDefinition(
    varDefinition: NodeID<ASTTypedNode>,
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
    ) { (v, _, _, _, t, _) -> ASTStaticArrayDefinitionNode(t, v.addType(DefinedType("array-${t.getType().typeString}-")), "") }

    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        re(czechtina[GrammarToken.TYPE_ARRAY]!!),
        re("<"),
        types,
        re(","),
        re("[0-9]+"),
        re(">")
    ) { (v, _, _, _, t, _, s, _) -> ASTStaticArrayDefinitionNode(t, v.addType(DefinedType("array-${t.getType().typeString}-$s")), s) }


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
        if (Compiler.definedTypes.contains(t.toC())) ASTVarDefinitionNode(
            v,
            t
        ) else throw Exception("Variable ${t.toC()} is not defined as an type")
    }
}