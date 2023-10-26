package compiler

import compiler.Compiler.undefinedFunction
import utils.GetFileLinkedFilePath
import java.io.File

object Preprocessor {
    var lastReadFile: String = ""

    fun readFile(filePath: String) {
        lastReadFile = File(filePath).readText()
    }

    fun addUndefineFile(line: String):String {
        undefinedFunction.add(line.split(" ")[1])
        println(undefinedFunction)
        return ""
    }

    fun preprocessText (text:String, filePath: String): String {
        val bylines = text.trim().lines().toTypedArray()

        var blocklevel = 0
        for (i in 0 until bylines.size) {
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
        }

        return bylines.joinToString("\n")
    }

    fun preprocess (filePath: String) :String {
        readFile(filePath)
        return preprocessText(lastReadFile, filePath)
    }
}