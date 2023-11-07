package utils

import compiler.Compiler

object Printer {
    private const val red = "\u001b[31m"
    private const val reset = "\u001b[0m"
    private const val yellow = "\u001b[33m"
    private const val green = "\u001b[32m"

    private fun infoTEXT(text:String):String = "[INFO]: ${text.replace("\n", "\n[INFO]: ")}"

    private fun successTEXT(text:String) = "$green[SUCCESS]: ${text.replace("\n","\\n[SUCCESS]: ")}$reset"

    private fun errTEXT(text:String) = "$red[ERR]: ${text.replace("\n","\\n[ERR]: ")}$reset"

    private fun warningTEXT(text:String) = "$yellow[WARNING]: ${text.replace("\n","\\n[WARNING]: ")}$reset"

    private fun fatalTEXT(text:String) = "$red[FATAL]: ${text.replace("\n","\\n[FATAL]: ")}$reset"

    fun info(text:String) {
        if (ArgsProvider.debug)
            println(infoTEXT(text))
    }

    fun success(text:String) = println(successTEXT(text))

    fun err(text:String) {
        if (ArgsProvider.debug)
            println(errTEXT(text))
    }

    fun warning(text:String) {
        if (ArgsProvider.debug)
            println(warningTEXT(text))
    }

    fun fatal(text:String) = println(fatalTEXT(text))

    fun printCurrentErrors(currentErrors: MutableMap<Int, MutableList<Int>>) {
        for (error in currentErrors) {
            println("$red[ERR]$reset: syntax error in line ${error.key} at char ${error.value.joinToString(",")}")
            println(Compiler.currentCode.split("\n")[error.key - 1])
            val max = error.value.max()
            for (i in 0..max)
                if (error.value.contains(i))
                    print("$red^$reset")
                else
                    print(" ")
            println()
        }
    }
}