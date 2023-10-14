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
    val blockCode = NodeID<ASTUnaryNode>("blockCode")
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

    line to def (
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
    { (_, v, _, t, _, min, def, max, block) -> ASTForNode(v, t, min, def, max, block) }


    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        types
    ) { (v, _, t) -> ASTBinaryNode(ASTBinaryTypes.VAR_DEFINITION, t, v) }

    line to def(
        varDefinition,
        endOfLine
    ) { (v, _) -> ASTUnaryNode(ASTUnaryTypes.SEMICOLON, v) }

    line to def(
        varDefinition,
        operands,
        r_expression,
        endOfLine
    ) { (v, o, l) -> ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTOperandNode(o, v, l)) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_RETURN))),
        r_expression,
        endOfLine
    ) { ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, it.v2)) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        blockCode
    ) { (_, exp, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.IF,exp), block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
        line,
        r_expression,
        endOfLine,
        r_expression,
        blockCode
    ) {(_, begin,cond,_,step, block) -> ASTForNode(begin, cond, step, block) }

    line to def(
        r_expression,
        endOfLine
    ) { (l) -> ASTUnaryNode(ASTUnaryTypes.SEMICOLON, l) }


    blockCode to def(re("{"), programLines, re("}")) {
        ASTUnaryNode(ASTUnaryTypes.CURLY, it.v2)
    }


    // MAIN FUNCTION
    main to def(
        re("main"), blockCode
    )
    {
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE, "cele"), it.v1,
            emptyList(), it.v2
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
            listOf(varDef), ASTUnaryNode(ASTUnaryTypes.CURLY, lines)
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
            varDefs.nodes,  ASTUnaryNode(ASTUnaryTypes.CURLY, lines)
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
            ASTUnaryNode(ASTUnaryTypes.SEMICOLON,  ASTUnaryNode(ASTUnaryTypes.CURLY, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
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
            ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.CURLY, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
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

    line to def (blockCode) { it.v1 }

    setTopNode(program)
    ignoreRegexes("\\s")
    onUnexpectedToken { err ->
        println(err)
    }
}.getLesana()
