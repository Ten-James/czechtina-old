package czechtina.lesana

import AST.*
import compiler.Compiler
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import czechtina.*


fun czechtinaLesana() = lesana<ASTNode> {
    val types = NodeID<ASTUnaryNode>("types")
    val variables = NodeID<ASTVariableNode>("variables")
    val operands = NodeID<String>("operands")
    val endOfLine = re(";|kafe")
    val main = NodeID<ASTFunctionNode>("main")
    val tFunction = NodeID<ASTFunctionNode>("function")
    val blockCode = NodeID<ASTUnaryNode>("blockCode")
    val programLines = NodeID<ASTProgramLines>("programLines")
    val line = NodeID<ASTNode>("line")
    val typeDefinition = NodeID<ASTBinaryNode>("typeDefinition")
    val varDefinition = NodeID<ASTTypedNode>("varDefinition")
    val listableDefinition = include(listAble(listOf(varDefinition)))
    val import = NodeID<ASTUnaryNode>("import")
    val r_expression = include(expression(variables, types))
    val program = NodeID<ASTProgramNode>("program")



    typeDefinition to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_TYPE_DEFINITION))),
        variables,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        types,
        endOfLine
    ) { (_, v,_, t,_) ->
        if (Compiler.definedTypes.contains(v.toC()))
            throw Exception("Type ${v.toC()} is already defined")
        else if (Compiler.addToDefinedTypes(v.toC()))
            ASTBinaryNode(ASTBinaryTypes.TYPE_DEFINITION, t, v.addType(t.getType()))
        else
            throw Exception("Error")
    }

    types to def(re("&"),types) { (_, t) -> ASTUnaryNode(ASTUnaryTypes.TYPE,t.data!!,t.getType().replace("pointer","dynamic")) }

    types to def(re(czechtina[GrammarToken.TYPE_POINTER]!!),
        re("<"),
        types,
        re(">"))
    { (_, _, t2, _) -> ASTUnaryNode(ASTUnaryTypes.TYPE_POINTER, t2, "pointer-${t2.getType()}") }

    types to def(re(cAndCzechtinaRegex(Alltypes))) { ASTUnaryNode(ASTUnaryTypes.TYPE, it.v1, cTypeFromCzechtina(it.v1)) }

    operands to def(re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN)))) { it.v1 }

    // FOR LOOP

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

    line to def (
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
        variables,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
        re(czechtina[GrammarToken.KEYWORD_RANGE_DEFINITION]!!),
        r_expression,
        blockCode
    )
    { (_, v, _, min, def, max, block) -> ASTForNode(v, ASTUnaryNode(ASTUnaryTypes.TYPE, min.getType(), min.getType()), min, def, max, block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
        line,
        r_expression,
        endOfLine,
        r_expression,
        blockCode
    ) {(_, begin,cond,_,step, block) -> ASTForNode(begin, cond, step, block) }

    // inline for

    line to def (
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
    { (_, v, _, t, _, min, def, max, _,block) -> ASTForNode(v, t, min, def, max, ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)) }

    line to def (
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
        variables,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
        re(czechtina[GrammarToken.KEYWORD_RANGE_DEFINITION]!!),
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    )
    { (_, v, _, min, def, max, _,block) -> ASTForNode(v, ASTUnaryNode(ASTUnaryTypes.TYPE, min.getType(), min.getType()), min, def, max, ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_FOR))),
        line,
        r_expression,
        endOfLine,
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    ) {(_, begin,cond,_,step,_, block) -> ASTForNode(begin, cond, step,ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)) }


    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        re(czechtina[GrammarToken.TYPE_ARRAY]!!),
        re("<"),
        types,
        re(">")
    ) { (v, _,_,_, t,_) -> ASTStaticArrayDefinitionNode(t, v.addType("array-${t.getType()}-"), "") }

    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        re(czechtina[GrammarToken.TYPE_ARRAY]!!),
        re("<"),
        types,
        re(","),
        re("[0-9]+"),
        re(">")
    ) { (v, _,_,_, t,_, s, _) -> ASTStaticArrayDefinitionNode(t, v.addType("array-${t.getType()}-$s"), s) }


    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        types
    ) { (v, _, t) -> ASTVarDefinitionNode(v.addType(t.getType()), t) }


    varDefinition to def(
        variables,
        re(czechtina[GrammarToken.KEYWORD_VAR_DEFINITION]!!),
        variables
    ) { (v, _, t) -> if (Compiler.definedTypes.contains(t.toC())) ASTVarDefinitionNode( v,t) else throw Exception("Variable ${t.toC()} is not defined as an type") }

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
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_RETURN))),
        endOfLine
    ) { ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, ASTUnaryNode(ASTUnaryTypes.LITERAL, ""))) }

    // IF

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        blockCode
    ) { (_, exp, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.IF,exp), block)}

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    ) { (_, exp,_, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.IF,exp), ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block) )}

    // ELSE IF

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        blockCode
    ) { (_,_,exp, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.ELSE_IF, exp), block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_IF))),
        r_expression,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        line
    ) { (_,_,exp,_, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.ELSE_IF, exp), ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)) }

    // ELSE

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        blockCode
    ) { (_, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.ELSE, ""), block) }

    line to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_ELSE))),
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        blockCode
    ) { (_, _, block) -> ASTBinaryNode(ASTBinaryTypes.FLOW_CONTROL, ASTUnaryNode(ASTUnaryTypes.ELSE, ""), ASTUnaryNode(ASTUnaryTypes.NEW_LINE, block)) }



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
            listOf(varDef), ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, lines)
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
            varDefs.nodes,  ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, lines)
        )
    }

    // inline function
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        varDefinition,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        types,
        r_expression,
    )
    { (funName, varDef, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            listOf(varDef),
            ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE,  ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
        )
    }

    // without type
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        varDefinition,
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
    )
    { (funName, varDef, _, line) ->
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE, line.getType()),
            funName,
            listOf(varDef),
            ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE,  ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
        )
    }

    // multiple params
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        listableDefinition,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        types,
        r_expression,
    )
    { (funName, varDefs, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            varDefs.nodes,
            ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
        )
    }

    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        listableDefinition,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        r_expression,
    )
    { (funName, varDefs, _, line) ->
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE, line.getType()),
            funName,
            varDefs.nodes,
            ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE, ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
        )
    }

    // no params
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        types,
        r_expression,
    )
    { (funName, _, retType, line) ->
        ASTFunctionNode(
            retType,
            funName,
            listOf(),
            ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE,  ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
        )
    }

    // without type
    tFunction to def(
        re("[a-zA-Z][a-zA-Z0-9]*"),
        re(czechtina[GrammarToken.OPERATOR_ITERATE]!!),
        r_expression,
    )
    { (funName, _, line) ->
        ASTFunctionNode(
            ASTUnaryNode(ASTUnaryTypes.TYPE, line.getType()),
            funName,
            listOf(),
            ASTUnaryNode(ASTUnaryTypes.CURLY_UNSCOPE,  ASTUnaryNode(ASTUnaryTypes.SEMICOLON, ASTUnaryNode(ASTUnaryTypes.RETURN, line)))
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
    variables to def(re("[a-zA-Z][a-zA-Z0-9]*")) { ASTVariableNode(it.v1) }


    program to def(import, program) { (import, program) -> program.appendImport(import) }
    program to def(typeDefinition, program) { (typ, program) -> program.appendTypeDefinition(typ) }
    program to def(program, typeDefinition) { (program, typ) -> program.appendTypeDefinition(typ) }
    program to def(tFunction, program) { (func, program) -> program.appendFunction(func) }
    program to def(program, tFunction) { (program, func) -> program.appendFunction(func) }

    program to def(main) { ASTProgramNode(listOf(), listOf(), it.v1) }

    line to def (blockCode) { it.v1 }

    setTopNode(program)
    ignoreRegexes("\\s")
    onUnexpectedToken { err ->
        println(err.got.toString())
        println("excepted: " + err.expectedIDs)
        println("------------------")
        println(err)
    }
}.getLesana()
