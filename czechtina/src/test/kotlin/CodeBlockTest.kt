import AST.ASTProgramNode
import compiler.Compiler
import cz.j_jzk.klang.input.InputFactory
import czechtina.lesana.czechtinaLesana
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.ArgsProvider

class CodeBlockTest {

    @Test
    fun testHelloWorldCompilation() {
        ArgsProvider.debug = true
        val code = """
            main {
                printf "Hello world!"
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() {
	     printf("Hello world!");
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testBasicNumericOperations() {
        ArgsProvider.debug = true
        val code = """
            timesTwo x:int -> x * 2
            main {
                x = 5 + 5 * 3 / 2 -1
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
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
        ArgsProvider.debug = true
        val code = """
            main {
                x:int
                scanf "%d", (adresa x)
                printf "%d", x
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() {
            int x;
            scanf("%d",&x);
            printf("%d",x);
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())

    }

    @Test
    fun testCreatingPointerToStaticVariable() {
        ArgsProvider.debug = true
        val code = """
            main {
                x:int
                p = adresa x
                h = hodnota p
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() {
            int x;
            int* p = &x;
            int h = *(p);
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testCreatingHardCodedStaticArray() {
        ArgsProvider.debug = true
        val code = """
            main {
                x:pole<int,3> = [1,2,3]
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() {
            int* x = {1,2,3};
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testCreatingDeducedStaticArray() {
        ArgsProvider.debug = true
        val code = """
            main {
                x = [1,2,3]
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() {
            int* x = {1,2,3};
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testPointerArithmetic() {
        ArgsProvider.debug = true
        val code = """
            main {
                x:pointer<int>
                y = x + 3
                z = x - y
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            
            int main() {
            int* x;
            int* y = x + 3;
            int z = x - y;
        }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testMorePointersArithmetic() {
        ArgsProvider.debug = true
        val code = """
            main {
                x = (new 5) as pointer<znak>
                z = x[3]
                x[3] = 5
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            int main() { char* x = (char*)(malloc(5)); char z = x[3]; x[3] = 5; if(x)free(x); }""".trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }


    @Test
    fun testHistogramProgram() {
        ArgsProvider.debug = true
        val code = """
            zpracuj histo:pointer<int>,minimum:int { void
                c:int = 0
                scanf "%d", (adresa c)
                for i:int -> 0 do 9 {
                    if c == i+minimum{
                        histo[i] = histo[i] + 1
                        vrat
                    }
                }
                histo[9] = histo[9] + 1
            }
            main {
                t:char
                histo:pointer<int> = [0,0,0,0,0,0,0,0,0,0]
                scanf "%c", (adresa t)
                if 'v' != t != 'h' {
                    printf "Neplatny mod vykresleni\n"
                    vrat 1
                }
                n:int
                minimum:int
                scanf "%d", (adresa n)
                scanf "%d", (adresa minimum)
                for i:cele -> 0 do n {
                    zpracuj histo, minimum
                }
                vrat 0
            }
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
#include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            
void zpracuj(int* histo, int minimum); 

void zpracuj(int* histo, int minimum) {
	int c = 0;
	scanf("%d",&c);
	for  (int i = 0; i < 9; i = i + 1) {
		if (c == i + minimum) {
			histo[i] = histo[i] + 1;
			return ;
		}
	}
	histo[9] = histo[9] + 1;
}

int main() {
	char t;
	int* histo = {0,0,0,0,0,0,0,0,0,0};
	scanf("%c",&t);
	if ('v' != t && t != 'h') {
		printf("Neplatny mod vykresleni\n");
		return 1;
	}
	int n;
	int minimum;
	scanf("%d",&n);
	scanf("%d",&minimum);
	for  (int i = 0; i < n; i = i + 1) {
		zpracuj(histo,minimum);
	}
	return 0;
}
        """.trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }
}