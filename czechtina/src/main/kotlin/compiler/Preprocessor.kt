package compiler

import compiler.Compiler.undefinedFunction
import GetFileLinkedFilePath
import generateFileInfo
import utils.ArgsProvider
import java.io.File

object Preprocessor {
    val prependAfterCompilation = mutableListOf<String>()


    fun addUndefineFile(line: String):String {
        undefinedFunction.add(line.split(" ")[1])
        return ""
    }

    fun appendUseCode(text:String):String {
        if (!ArgsProvider.useCache) return text
        val file = File(".czcache")
        val cache = generateFileInfo(file.readLines())
        val lines =text.lines().map {
            if (it.startsWith("use")) {
                val packageName = it.split(" ")[1].trim()
                val files = cache.values.filter { it.packageName == packageName }.map { packagePath ->
                    val filetext = File(packagePath.path).readText()
                    prependAfterCompilation.add(Compiler.compileTextDefinition(filetext, packagePath.path))
                    ""
                }
                files.joinToString("\n")
            }
            else {
                it
            }
        }
        return lines.joinToString("\n")
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
        var isBlockComment = false
        for (i in bylines.indices) {
            if (isBlockComment) {
                if (bylines[i].contains("*/")) {
                    bylines[i] = bylines[i].substringAfter("*/")
                    isBlockComment = false
                }
                else
                    bylines[i] = ""
            }
            else if (bylines[i].contains("/*")) {
                isBlockComment = true;
                bylines[i] = bylines[i].substringBefore("/*")
            }
            else if (bylines[i].isBlank())
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

        return appendUseCode(bylines.joinToString("\n").replace("#\$#CZECHTINAOPEMN\$#\$", "{").replace("#\$#CZECHTINACLOSE\$#\$", "}"))
    }

    fun removeDuplicateImport(text: String):String {
        val rem = "%@$#@REMOVEME@#%"
        val imports = mutableListOf<String>()
        val lines = text.lines().map {
            if (it.startsWith("#include")) {
                 if (imports.contains(it)) rem
                else {
                    imports.add(it)
                    it
                }
            }
            else
                it
        }
        return recursiveReplace(lines.filter { it != rem }.joinToString("\n"), "\n\n\n", "\n\n");
    }

    fun recursiveReplace(text: String, from: String, to: String): String =
        if (text.contains(from))
            recursiveReplace(text.replace(from, to),from, to)
        else
            text

}