package compiler

import compiler.Compiler.undefinedFunction
import utils.GetFileLinkedFilePath
import java.io.File

object Preprocessor {

    fun addUndefineFile(line: String):String {
        undefinedFunction.add(line.split(" ")[1])
        return ""
    }

    fun preprocessText (text:String, filePath: String): String {
        val splitedCode = text.split("\"").toMutableList()

        for (i in 0 until splitedCode.size) {
            if (i % 2 == 0) continue
            splitedCode[i] = splitedCode[i].replace("\\n", "\\\\n")
            splitedCode[i] = splitedCode[i].replace("\\t", "\\t")
            splitedCode[i] = splitedCode[i].replace(" ", "#\$#CZECHTINAMEZERA\$#\$")
            splitedCode[i] = splitedCode[i].replace("{", "#\$#CZECHTINAOPEMN\$#\$")
            splitedCode[i] = splitedCode[i].replace("}", "#\$#CZECHTINACLOSE\$#\$")
        }
        val code = splitedCode.joinToString("\"").trim()



        val bylines = code.trim().lines().map { it.substringBefore("//") }.toTypedArray()

        var blocklevel = 0
        for (i in bylines.indices) {
            if (bylines[i].isBlank())
                continue
            else if (bylines[i].contains("{"))
                blocklevel += 1
            else if (bylines[i].contains("}"))
                blocklevel -= 1
            else if (bylines[i].endsWith("->"))
                continue
            else if (bylines[i].startsWith("pripoj c"))
                continue
            else if (bylines[i].startsWith("#undefine"))
                bylines[i] = addUndefineFile(bylines[i])
            else if (bylines[i].startsWith("pripoj"))
                bylines[i] = File(GetFileLinkedFilePath(filePath, bylines[i].split(" ")[1])).readText()
            else if (!bylines[i].endsWith("\\") && blocklevel >0)
                bylines[i] = "${bylines[i]};".replace(";;",";")
            else if (!bylines[i].endsWith("\\"))
                bylines[i] = "${bylines[i]} konec".replace(";;",";")
            else if (bylines[i].endsWith("\\"))
                bylines[i] = bylines[i].substringBeforeLast("\\")
        }

        return bylines.joinToString("\n").replace("#\$#CZECHTINAOPEMN\$#\$", "{").replace("#\$#CZECHTINACLOSE\$#\$", "}")
    }
}