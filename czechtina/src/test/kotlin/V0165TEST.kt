import compiler.Compiler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.ArgsProvider

class V0165TEST {


    @Test
    fun testV0165Features() {
        ArgsProvider.debug = true
        val code = """
            main {
                b:int = 0
                b += 1
                ++b
                c = new int, 5
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() {
            int b = 0;
            b += 1;
            ++b;
            int *c = (int**)malloc(5 * sizeof(int *));
            if(c)free(c);
        }""".trimIndent()
        Assertions.assertEquals(
            excepted.replace("\\s+".toRegex(), " ").trim(),
            cCode.replace("\\s+".toRegex(), " ").trim()
        )
    }

}