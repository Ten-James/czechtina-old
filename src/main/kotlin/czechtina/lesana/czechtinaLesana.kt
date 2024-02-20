package czechtina.lesana

import AST.*
import compiler.Compiler
import compiler.DefinedType
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import czechtina.grammar.*
import utils.ArgsProvider


fun czechtinaLesana() = lesana<ASTNode> {
    val types = NodeID<ASTUnaryNode>("types")
    val variables = NodeID<ASTVariableNode>("variables")
    val endOfLine = re(";|kafe")
    val konec = re("konec")
    val main = NodeID<ASTFunctionNode>("main")
    val tFunction = NodeID<ASTFunctionNode>("function")
    val blockCode = NodeID<ASTUnaryNode>("blockCode")
    val programLines = NodeID<ASTProgramLines>("programLines")
    val line = NodeID<ASTNode>("line")
    val typeDefinition = NodeID<ASTBinaryNode>("typeDefinition")
    val varDefinition = NodeID<ASTNode>("varDefinition")
    val listableDefinition = include(listAble(listOf(varDefinition)))
    val import = NodeID<ASTUnaryNode>("import")
    val program = NodeID<ASTProgramNode>("program")
    val structHead = NodeID<ASTStructureNode>("structure header")
    val structure = NodeID<ASTStructureNode>("structure")



    variableDefinition(varDefinition, variables, types)

    structHead to def(
        re(czechtina[GrammarToken.KEYWORD_STRUCT]!!),
        types,
        re("{")
    ) {ASTStructureNode(it.v2.data.toString(), emptyList()).defineItSelf()}

    structHead to def (
        structHead,
        varDefinition,
        endOfLine
    ) {it.v1.addProperty(it.v2 as ASTVarDefinitionNode)}

    structHead to def (
        structHead,
        tFunction,
    ) {it.v1.addFunction(it.v2)}

    structure to def (
        structHead,
        re("}")
    ) {it.v1}

    typeDefinition to def(
        re(cAndCzechtinaRegex(listOf(GrammarToken.KEYWORD_TYPE_DEFINITION))),
        variables,
        re(cAndCzechtinaRegex(listOf(GrammarToken.OPERATOR_ASSIGN))),
        types,
        endOfLine
    ) { (_, v,_, t,_) ->
        if (Compiler.definedTypes.contains(v.toC()))
            throw Exception("Type ${v.toC()} is already defined")
        else if (Compiler.addToDefinedTypes(v.toC(),t.getType()))
            ASTBinaryNode(ASTBinaryTypes.TYPE_DEFINITION, t, v.addType(t.getType()))
        else
            throw Exception("Error")
    }

    types to def(re("@"),types) { (_, t) -> ASTUnaryNode(ASTUnaryTypes.TYPE,t.data!!,t.getType().toConst()) }

    types to def(re("&"),types) { (_, t) -> ASTUnaryNode(ASTUnaryTypes.TYPE,t.data!!,t.getType().toHeap()) }

    types to def(re(czechtina[GrammarToken.TYPE_POINTER]!!),
        re("<"),
        types,
        re(">"))
    { (_, _, t2, _) -> ASTUnaryNode(ASTUnaryTypes.TYPE_POINTER, t2, t2.getType().toPointer()) }

    types to def(re(cAndCzechtinaRegex(Alltypes) +"|T[0-9]*|[A-Z][a-zA-Z]*")) { ASTUnaryNode(ASTUnaryTypes.TYPE, if (Regex("T[0-9]*").matches(it.v1)) "*${it.v1}" else it.v1, DefinedType(if (Regex("T[0-9]*").matches(it.v1)) "*${it.v1}" else if (Regex("[A-Z]+").matches(it.v1)) it.v1 else cTypeFromCzechtina(it.v1) )) }


    val r_expression = include(expression(variables, types))


    programLine(line, variables, types, r_expression, blockCode, endOfLine, varDefinition, programLines)


    program to def(re("package"), re("[a-zA-Z][a-zA-Z0-9]*"), konec) { ASTPackageNode(it.v2) }

    // MAIN FUNCTION
    main to def(
        re("main"), blockCode
    )
    {
       ASTFunctionNode(
           ASTUnaryNode(ASTUnaryTypes.TYPE, "cele", DefinedType("int")), it.v1,
           emptyList(), it.v2
       )
    }

    blockFunction(tFunction, varDefinition, types, programLines, listableDefinition)
    inlineTypedFunction(tFunction, varDefinition, types, r_expression, konec, listableDefinition)

    inlineFunction(tFunction, varDefinition, r_expression, konec, listableDefinition)




    import to def(re(czechtinaRegex(listOf(GrammarToken.KEYWORD_IMPORT_C))), re("[a-zA-Z][a-zA-Z0-9]*")) {
        ASTUnaryNode(
            ASTUnaryTypes.IMPORT_C,
            it.v2
        )
    }


    variables to def(re("[a-zA-Z][a-zA-Z0-9]*")) { ASTVariableNode(it.v1, DefinedType("none")) }


    program to def(import, program) { (import, program) -> program.appendImport(import) }
    program to def(typeDefinition, program) { (typ, program) -> program.appendTypeDefinition(typ) }
    program to def(program, typeDefinition) { (program, typ) -> program.appendTypeDefinition(typ) }
    program to def(tFunction, program) { (func, program) -> program.appendFunction(func) }
    program to def(program, tFunction) { (program, func) -> program.appendFunction(func) }
    program to def(main) { ASTProgramNode(listOf(), listOf(
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "stdio"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "stdlib"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "malloc"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "string"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "stdbool"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "math"),
    ), it.v1) }


    program to def(program, structure) { (program, structure) -> program.appendStructure(structure) }
    program to def(structure, program) { (structure, program) -> program.appendStructure(structure) }

    line to def (blockCode) { it.v1 }

    setTopNode(program)
    ignoreRegexes("\\s")
    onUnexpectedToken { err ->
        Compiler.getCurrentCodeLine(err.got.position.character)
        if (ArgsProvider.debug) {
            println(err)
        }
        throw Exception( "CZECHTINA ERROR")
    }
}.getLesana()
