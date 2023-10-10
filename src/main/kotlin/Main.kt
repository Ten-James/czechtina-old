import AST.*
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import czechtina.r_expression

fun main(args: Array<String>) {

    val code = """main {
            char a;
            a = 't' + 1 + 2 * 3 / 1 - 4;
        }
        """.trimMargin()

    val czechtinaLesana = lesana<Any> {
        val types = NodeID<ASTUnaryNode>("types")
        val variables = NodeID<ASTUnaryNode>("variables")
        val operands = NodeID<String>("operands")
        val endOfLine = re(";")
        val program = NodeID<ASTNode>("program")
        val programLines = NodeID<ASTProgramLines>("programLines")
        val line = NodeID<ASTNode>("line")
        val r_expression = include(r_expression(variables))

        types to def(re("int|void|char|float|double")) { ASTUnaryNode("type", it.v1) }

        operands to def(re("=|\\+=|-=|/=|\\*=|%=")) { it.v1 }

        line to def(
            types,
            variables,
            endOfLine
        ) { (t, v) -> ASTBinaryNode("var_definition", t, v) }

        line to def(
            types,
            variables,
            operands,
            r_expression,
            endOfLine
        ) { (t, v, o, l) -> ASTOperandNode(o, ASTBinaryNode("var_definition", t, v), l) }

        line to def(
            variables,
            operands,
            r_expression,
            endOfLine
        ) { (v, o, l) -> ASTOperandNode(o, v, l) }

        programLines to def(line, programLines) { ASTProgramLines(listOf(it.v1) + it.v2.programLines) }
        programLines to def(line) { ASTProgramLines(listOf(it.v1)) }

        program to def(re("main"), re("{"), programLines, re("}")) { ASTUnaryNode("program", it.v3) }

        setTopNode(program)
        variables to def(re("[a-zA-Z][a-zA-Z0-9]*")) { ASTUnaryNode("variable", it.v1) }

        ignoreRegexes("[ \t\n]")

        onUnexpectedToken { err ->
            println(err)
        }
    }.getLesana()

    println(czechtinaLesana.parse(InputFactory.fromString(code, "code")))
}