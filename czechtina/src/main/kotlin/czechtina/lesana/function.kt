package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.LesanaBuilder
import cz.j_jzk.klang.parse.NodeID
import czechtina.grammar.GrammarToken
import czechtina.grammar.czechtina


fun LesanaBuilder<ASTNode>.inlineFunction(
    tFunction: NodeID<ASTFunctionNode>,
    varDefinition: NodeID<ASTNode>,
    r_expression: NodeID<ASTNode>,
    konec: NodeID<String>,
    listableDefinition: NodeID<ASTListNode>
) {
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        varDefinition,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
        konec
    )
    { (funName, varDef, _, line) ->
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE,data= "", expressionType =  line.getType()),
            funName,
            listOf(varDef),
            ASTUnaryNode(
                ASTUnaryTypes.CURLY_UNSCOPE,
                ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
            )
        )
    }
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        listableDefinition,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
        konec
    )
    { (funName, varDefs, _, line) ->
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE,data="", expressionType =  line.getType()),
            funName,
            varDefs.nodes,
            ASTUnaryNode(
                ASTUnaryTypes.CURLY_UNSCOPE,
                ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
            )
        )
    }

    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
        konec
    )
    { (funName, _, line) ->
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE,data="", expressionType =  line.getType()),
            funName,
            listOf(),
            ASTUnaryNode(
                ASTUnaryTypes.CURLY_UNSCOPE,
                ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
            )
        )
    }
}

fun LesanaBuilder<ASTNode>.inlineTypedFunction(
    tFunction: NodeID<ASTFunctionNode>,
    varDefinition: NodeID<ASTNode>,
    types: NodeID<ASTUnaryNode>,
    r_expression: NodeID<ASTNode>,
    konec: NodeID<String>,
    listableDefinition: NodeID<ASTListNode>
) {
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        varDefinition,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        types,
        r_expression,
        konec
    )
    { (funName, varDef, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            listOf(varDef),
            ASTUnaryNode(
                ASTUnaryTypes.CURLY_UNSCOPE,
                ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
            )
        )
    }

    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        types,
        r_expression,
        konec
    )
    { (funName, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            listOf(),
            ASTUnaryNode(
                ASTUnaryTypes.CURLY_UNSCOPE,
                ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
            )
        )
    }

    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        listableDefinition,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        types,
        r_expression,
        konec
    )
    { (funName, varDefs, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            varDefs.nodes,
            ASTUnaryNode(
                ASTUnaryTypes.CURLY_UNSCOPE,
                ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
            )
        )
    }
}

fun LesanaBuilder<ASTNode>.blockFunction(
    tFunction: NodeID<ASTFunctionNode>,
    varDefinition: NodeID<ASTNode>,
    types: NodeID<ASTUnaryNode>,
    programLines: NodeID<ASTProgramLines>,
    listableDefinition: NodeID<ASTListNode>
) {
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        varDefinition,
        re("{"),
        types,
        programLines,
        re("}")
    )
    { (funName, varDef, _, retType, lines, _) ->
        ASTFunctionNode(
            retType, funName,
            listOf(varDef), ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, lines)
        )
    }
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        re("{"),
        types,
        programLines,
        re("}")
    )
    { (funName, _, retType, lines, _) ->
        ASTFunctionNode(
            retType, funName,
            listOf(), ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, lines)
        )
    }

    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        listableDefinition,
        re("{"),
        types,
        programLines,
        re("}")
    )
    { (funName, varDefs, _, retType, lines, _) ->
        ASTFunctionNode(
            retType, funName,
            varDefs.nodes, ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, lines)
        )
    }
}