object Printer {
    var debug = false;
    var whoami = "czechtina"
    private const val red = "\u001b[31m"
    private const val reset = "\u001b[0m"
    private const val yellow = "\u001b[33m"
    private const val green = "\u001b[32m"

    fun greenWrap (text:String) = "$green$text$reset"

    private val helpText = """
    Usage: ${greenWrap("czechtina [input file].cz [options] -o [output file]")}
        |Options:
        |   ${greenWrap("--help")}         Show this help
        |   ${greenWrap("--no-compile")}    Do not compile the output C code, it will be created in the same directory as the input file
        |   ${greenWrap("--show-tree")}     Show the AST tree
        |   ${greenWrap("--write-code")}    Write Code in comment before C code
        |   ${greenWrap("--fpeterek")}      Uses macros from old czechtina.h file
        |   ${greenWrap("--friendly")}      Generate valid C without macros in comment bellow code
        |   ${greenWrap("--set-dir")}       Set dir for file creation
        |   ${greenWrap("--debug")}         Show debug info
    """

    private val cliHelpText = """
    ${greenWrap("Czechtina CLI")}
        |Options:
        |   ${greenWrap("--help")}         Show this help
        |   ${greenWrap("init")}            Create a new project
        |      ${greenWrap("-n")}           Name of the project
        |      ${greenWrap("-d")}           Directory of the project
        |
        |   ${greenWrap("info")}            Show info about the project
        |   ${greenWrap("build")}           Build the project
        |   ${greenWrap("run")}             Run the project
        |   ${greenWrap("clean")}           Clean the project
    """.trimIndent()

    private fun infoTEXT(text:String):String = "[${whoami} - INFO]: ${text.replace("\n", "\n[INFO]: ")}"

    private fun successTEXT(text:String) = "$green[${whoami} - SUCCESS]: ${text.replace("\n","\\n[SUCCESS]: ")}$reset"

    private fun errTEXT(text:String) = "$red[${whoami} - ERR]: ${text.replace("\n","\\n[ERR]: ")}$reset"

    private fun warningTEXT(text:String) = "$yellow[${whoami} - WARNING]: ${text.replace("\n","\\n[WARNING]: ")}$reset"

    private fun fatalTEXT(text:String) = "$red[${whoami} - FATAL]: ${text.replace("\n","\\n[FATAL]: ")}$reset"


    fun printHelp() {
        println(helpText.trimMargin())
    }
    fun printCLIHelp() {
        println(cliHelpText.trimMargin())
    }

    fun info(text:String) {
        if (debug)
            println(infoTEXT(text))
    }

    fun setDebug() {
        debug = true
    }

    fun success(text:String) = println(successTEXT(text))

    fun err(text:String) {
        if (debug)
            println(errTEXT(text))
    }

    fun warning(text:String) {
        if (debug)
            println(warningTEXT(text))
    }

    fun fatal(text:String) = println(fatalTEXT(text))

    fun printCurrentErrors(currentCode: String, currentErrors: MutableMap<Int, MutableList<Int>>) {
        for (error in currentErrors) {
            println("$red[ERR]$reset: syntax error in line ${error.key} at char ${error.value.joinToString(",")}")
            println(currentCode.split("\n")[error.key - 1])
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