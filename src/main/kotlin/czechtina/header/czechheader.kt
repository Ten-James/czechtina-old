package czechtina.header

import compiler.Compiler
import czechtina.C
import czechtina.CZ
import czechtina.GrammarToken
import czechtina.czechtina
import java.io.File


fun createCzechtinaDefineFile(defineName: String = "CZECHTINA") {
    val prefix = """
        #IFNDEF $defineName
        #DEFINE $defineName
    """.trimIndent()
    val postfix = """
        //CREATED BY czechtina compiler
        #ENDIF
    """.trimIndent()

    var code = prefix
    for (gt in GrammarToken.values())
    {
        if (gt == GrammarToken.VARIABLE){
            continue;
        }
        if (czechtina.containsKey(gt) && CZ.containsKey(gt) && C.containsKey(gt))
        {
            code += "\n#DEFINE ${CZ[gt]!!.replace("\\", "")} ${C[gt]!!.replace("\\", "")}"
        }
    }

    File("${Compiler.buildPath}czechtina.h").writeText(code)
}