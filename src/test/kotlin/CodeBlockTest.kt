import AST.ASTProgramNode
import compiler.Compiler
import cz.j_jzk.klang.input.InputFactory
import czechtina.lesana.czechtinaLesana
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CodeBlockTest {

    @Test
    fun testHelloWorldCompilation() {
        val code = """
            main {
                printf "Hello world!"
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """int main() {
	     printf("Hello world!");
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testBasicNumericOperations() {
        val code = """
            timesTwo x:int -> x * 2
            main {
                x = 5 + 5 * 3 / 2 -1
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            int timesTwo(int x);
            
            int timesTwo(int x) {
            return x * 2;
            }
            
            int main() {
	     int x = 5 + 5 * 3 / 2 - 1;
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testInputOutputForStaticVariable() {
        val code = """
            main {
                x:int
                scanf "%d", (adresa x)
                printf "%d", x
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            int main() {
            int x;
            scanf("%d",&x);
            printf("%d",x);
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())

    }

    @Test
    fun testCreatingPointerToStaticVariable() {
        val code = """
            main {
                x:int
                p = adresa x
                h = hodnota p
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            int main() {
            int x;
            int *p = &x;
            int h = *(p);
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testCreatingHardCodedStaticArray() {
        val code = """
            main {
                x:pole<int,3> = [1,2,3]
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            int main() {
            int x[3] = {1,2,3};
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testCreatingDeducedStaticArray() {
        val code = """
            main {
                x = [1,2,3]
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            int main() {
            int x[3] = {1,2,3};
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testPointerArithmetic() {
        val code = """
            main {
                x:pointer<int>
                y = x + 3
                z = x - y
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            int main() {
            int *x;
            int *y = x + 3;
            int z = x - y;
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }
    @Test
    fun testStructures() {
        val code = """
            struct DATA {
                x:int
                y:DATA
            }
            
            main {
                b = new DATA
                b.x = 5
                a = b.x
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            typedef struct { int x; DATA *y; } DATA; 
            int main() { 
                DATA *b = (DATA *)malloc(sizeof(DATA));
                b->x = 5;
                int a = b->x;
            }
        """.trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }
}