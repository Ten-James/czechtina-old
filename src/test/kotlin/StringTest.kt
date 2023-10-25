import compiler.Compiler
import cz.j_jzk.klang.input.InputFactory
import czechtina.lesana.czechtinaLesana
import czechtina.lesana.literals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import AST.ASTTypedNode
import AST.ASTUnaryNode

class StringTest {
    @Test
    fun testWhitespaceRaw() {
        val lsn = lesana<ASTUnaryNode> {
            val expr = NodeID<ASTUnaryNode>()
            expr to def(include(literals())) { it.v1 }
            setTopNode(expr)

            ignoreRegexes("\\s")

            onUnexpectedToken(::println)
        }.getLesana()

        fun checkString(source: String, expectedResult: String) =
            assertEquals(
                expectedResult,
                (lsn.parse(InputFactory.fromString(source, "str")) as ASTUnaryNode).data as String
            )
        
        checkString("\"\"", "")
        checkString("\"ahoj\"", "ahoj")
        checkString("\"ahoj mami\"", "ahoj mami")

    }

    @Test
    fun testWhitespaceInProgram() {

    }
}