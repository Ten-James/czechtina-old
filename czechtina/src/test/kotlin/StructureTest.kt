import AST.ASTProgramNode
import compiler.Compiler
import cz.j_jzk.klang.input.InputFactory
import czechtina.lesana.czechtinaLesana
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.ArgsProvider

class StructureTest {
    @Test
    fun testStructures() {
        ArgsProvider.debug = true
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
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            typedef struct { int x; DATA * y; } DATA; 
            int main() { 
                DATA * b = (DATA *)malloc(sizeof(DATA));
                b->x = 5;
                int a = b->x;
                if(b)free(b);
            }
        """.trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testStructArray() {
        ArgsProvider.debug = true
        val code = """
            struct DATA {
    data:int
    next:DATA
}

main {
    a = new DATA
    a.data = 1
    b = new DATA
    b.data = 2
    a.next = b
    arr = new DATA, 2
    arr[0] = a
    arr[1] = b
    printf "%d ", a.data
    printf "%d ", a.next.data
    printf "%d ", arr[0].data
    printf "%d ", arr[0].next.data
}
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
            typedef struct {
	int data;
	DATA * next;
 } DATA;

int main() {
	DATA * a = (DATA *)malloc(sizeof(DATA));
	a->data = 1;
	DATA * b = (DATA *)malloc(sizeof(DATA));
	b->data = 2;
	a->next = b;
	DATA ** arr = (DATA **)malloc(2 * sizeof(DATA *));
	arr[0] = a;
	arr[1] = b;
	printf("%d ",a->data);
	printf("%d ",a->next->data);
	printf("%d ",arr[0]->data);
	printf("%d ",arr[0]->next->data);
	if(a)free(a);
	if(b)free(b);
	if(arr)free(arr);
	
}
        """.trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

    @Test
    fun testStructAsParameter() {
        ArgsProvider.debug = true
        val code = """
struct DATA {
    data:int
    next:DATA
}

create b:DATA { void
    b.data = 2
}

main {
    a = new DATA
    create a
}
        """.trimIndent()
        val cCode = Compiler.compileText(code)
        val excepted = """
            #include "stdio.h" #include "stdlib.h" #include "malloc.h" #include "string.h" #include "stdbool.h" #include "math.h"
typedef struct {
	int data;
	DATA * next;
 } DATA;

void create(DATA * b); 

void create(DATA * b) {
	b->data = 2;
}

int main() {
	DATA * a = (DATA *)malloc(sizeof(DATA));
	create(a);
    if(a)free(a);
}
        """.trimIndent()
        assertEquals(excepted.replace("\\s+".toRegex(), " ").trim(), cCode.replace("\\s+".toRegex(), " ").trim())
    }

}