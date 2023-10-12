package czechtina.lesana

import AST.*
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import czechtina.*


fun czechtinaLesana() = lesana<ASTNode> {
    val types = NodeID<ASTUnaryNode>("types")
    val variables = NodeID<ASTUnaryNode>("variables")
    val operands = NodeID<String>("operands")
    val endOfLine = re(";|kafe")
    val main = NodeID<ASTFunctionNode>("main")
    val tFunction = NodeID<ASTFunctionNode>("function")
    val programLines = NodeID<ASTProgramLines>("programLines")
    val line = NodeID<ASTNode>("line")
    val varDefinition = NodeID<ASTBinaryNode>("varDefinition")
    val listableDefinition = include(listAble(listOf(varDefinition)))
    val import = NodeID<ASTUnaryNode>("import")
    val r_expression = include(rightExpression(variables))
    val program = NodeID<ASTProgramNode>("program")



    types to def(re(czechtina[GrammarToken.TYPE_POINTER]!!),
        re("<"),
        types,
        re(">"))
    { (_, _, t2, _) -> ASTUnaryNode(ASTUnaryTypes.TYPE_POINTER, t2) }

    types to def(re(cAndCzechtinaRegex(Alltypes))) { ASTUnaryNode(ASTUnaryTypes.TYPE, it.v1) }

    operands to def(re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN)))) { it.v1 }

    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        types
    ) { (v, _, t) -> ASTBinaryNode(ASTBinaryTypes.VAR_DEFINITION, t, v) }



    line to def(
        varDefinition,
        endOfLine
    ) { (v, _) -> v }

    line to def(
        varDefinition,
        operands,
        r_expression,
        endOfLine
    ) { (v, o, l) -> ASTOperandNode(o, v, l) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_RETURN))),
        r_expression,
        endOfLine
    ) { ASTUnaryNode(ASTUnaryTypes.RETURN, it.v2) }

    line to def(
        variables,
        operands,
        r_expression,
        endOfLine
    ) { (v, o, l) -> ASTOperandNode(o, v, l) }

    line to def(
        r_expression,
        endOfLine
    ) { (l) -> l }

    // MAIN FUNCTION
    main to def(
        re("main"), re("{"), programLines, re("}")
    )
    {
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE, "cele"), it.v1,
            emptyList(), it.v3
        )
    }



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
            listOf(varDef), lines
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
            varDefs.nodes, lines
        )
    }

    // inline function
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        varDefinition,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        types,
        line,
    )
    { (funName, varDef, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            listOf(varDef),
            ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
        )
    }
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        listableDefinition,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        types,
        line,
    )
    { (funName, varDefs, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            varDefs.nodes,
            ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
        )
    }



    programLines to def(line, programLines) { ASTProgramLines(listOf(it.v1) + it.v2.programLines) }
    programLines to def(line) { ASTProgramLines(listOf(it.v1)) }

    //import to def(re(czechtinaRegex(listOf(GrammarToken.KEYWORD_IMPORT))), re("^(.+)\\\\/([^\\\\/]+)\$")) { ASTUnaryNode(ASTUnaryTypes.IMPORT, it.v2) }
    import to def(re(czechtinaRegex(listOf(GrammarToken.KEYWORD_IMPORT_C))), re("[a-zA-Z][a-zA-Z0-9]*")) {
        ASTUnaryNode(
            ASTUnaryTypes.IMPORT_C,
            it.v2
        )
    }
    variables to def(re("[a-zA-Z][a-zA-Z0-9]*")) { ASTUnaryNode(ASTUnaryTypes.VARIABLE, it.v1) }

    program to def(import, program) { (import, program) -> program.appendImport(import) }
    program to def(tFunction, program) { (func, program) -> program.appendFunction(func) }
    program to def(program, tFunction) { (program, func) -> program.appendFunction(func) }

    program to def(main) { ASTProgramNode(listOf(), listOf(), it.v1) }

    setTopNode(program)
    ignoreRegexes("\\s")
    onUnexpectedToken { err ->
        println(err)
    }
}.getLesana()
