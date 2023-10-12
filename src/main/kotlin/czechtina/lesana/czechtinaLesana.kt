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
    val import = NodeID<ASTUnaryNode>("import")
    val r_expression = include(rightExpression(variables))
    val program = NodeID<ASTProgramNode>("program")



    types to def(re(cAndCzechtinaRegex(Alltypes))) { ASTUnaryNode(ASTUnaryTypes.TYPE, it.v1) }

    operands to def(re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN)))) { it.v1 }

    line to def(
        types,
        variables,
        endOfLine
    ) { (t, v) -> ASTBinaryNode(ASTBinaryTypes.VAR_DEFINITION, t, v) }

    line to def(
        types,
        variables,
        operands,
        r_expression,
        endOfLine
    ) { (t, v, o, l) -> ASTOperandNode(o, ASTBinaryNode(ASTBinaryTypes.VAR_DEFINITION, t, v), l) }

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

    line to def (
        r_expression,
        endOfLine
    ) { (l) -> l }

    // MAIN FUNCTION
    main to def(
        re("main"), re("{"), programLines, re("}"))
    {
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE, "cele"), it.v1,
            emptyList(), it.v3
        )
    }



    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        types,
        variables,
        re("{"),
        types,
        programLines,
        re("}")
    )
    {
        ASTFunctionNode(
            it.v5, it.v1,
            listOf(ASTBinaryNode(ASTBinaryTypes.VAR_DEFINITION, it.v2, it.v3)), it.v6
        )
    }

    // inline function
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        types,
        variables,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        types,
        line,
    )
    { (funName, parTyp, parName, _, retType, line) -> ASTFunctionNode(retType, funName, listOf(
        ASTBinaryNode(
            ASTBinaryTypes.VAR_DEFINITION, parTyp, parName)
    ), ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line))
    ) }



    programLines to def(line, programLines) { ASTProgramLines(listOf(it.v1) + it.v2.programLines) }
    programLines to def(line) { ASTProgramLines(listOf(it.v1)) }

    //import to def(re(czechtinaRegex(listOf(GrammarToken.KEYWORD_IMPORT))), re("^(.+)\\\\/([^\\\\/]+)\$")) { ASTUnaryNode(ASTUnaryTypes.IMPORT, it.v2) }
    import to def(re(czechtinaRegex(listOf(GrammarToken.KEYWORD_IMPORT_C))), re("[a-zA-Z][a-zA-Z0-9]*")) { ASTUnaryNode(ASTUnaryTypes.IMPORT_C, it.v2) }
    variables to def(re("[a-zA-Z][a-zA-Z0-9]*")) { ASTUnaryNode(ASTUnaryTypes.VARIABLE, it.v1) }

    program to def(import, program) {(import, program) -> program.appendImport(import)}
    program to def(tFunction, program) {(func, program) -> program.appendFunction(func)}
    program to def(program, tFunction) {(program, func) -> program.appendFunction(func)}

    program to def(main) { ASTProgramNode(listOf(), listOf(),it.v1) }

    setTopNode(program)
    ignoreRegexes("\\s")
    onUnexpectedToken { err ->
        println(err)
    }
}.getLesana()
