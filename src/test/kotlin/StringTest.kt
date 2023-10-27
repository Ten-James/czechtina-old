import cz.j_jzk.klang.input.InputFactory
import czechtina.lesana.literals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import AST.ASTUnaryNode

class StringTest {
    @Test
    fun testWhitespaceRaw() {
        val lsn = lesana<ASTUnaryNode> {
            val expr = NodeID<ASTUnaryNode>("expr")
            expr to def(include(literals())) { it.v1 }
            setTopNode(expr)

            ignoreRegexes("\\s")

            onUnexpectedToken { println("$it; (expected one of: ${it.expectedIDs}") }
        }.getLesana()

        fun checkString(source: String, expectedResult: String) =
            assertEquals(
                expectedResult,
                (lsn.parse(InputFactory.fromString(source, "str")) as ASTUnaryNode).data as String
            )
        
        checkString("\"\"", "")
        checkString("\"ahoj\"", "ahoj")
        checkString("\"ahoj mami\"", "ahoj mami")
        checkString("\"ahoj mami\n\"", "ahoj mami\n")

    }

    @Test
    fun testWhitespaceInProgram() {

    }
}
