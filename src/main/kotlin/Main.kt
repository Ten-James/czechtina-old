import AST.*
import compiler.Compiler
import compiler.Preprocessor
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.useful.list
import czechtina.*
import czechtina.header.createCzechtinaDefineFile
import czechtina.lesana.czechtinaLesana
import utils.ArgsProvider
import utils.Filer
import utils.Printer
import utils.RemoveFileExtension
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main(args: Array<String>) {
    if (args.any() { it == "--help" })
        return println("""Usage: java -jar czechtina.jar [input file].cz [options]
            |Options:
            |   --help          Show this help
            |   --no-compile    Do not compile the output C code, it will be created in the same directory as the input file
            |   --show-tree     Show the AST tree
            |   --write-code    Write Code in comment before C code
            |   --fpeterek      Uses macros from old czechtina.h file
            |   --friendly      Generate valid C without macros in comment bellow code
            |   --set-dir       Set dir for file creation
            |   --debug         Show debug info
        """.trimMargin())

    ArgsProvider.processArgs(args)

    if (ArgsProvider.fpeterek) {
        Compiler.setToCZ()
    }

    if (ArgsProvider.setDir) {
        Compiler.buildPath= ArgsProvider.dir
    }

    //create file with name of input file in current directory
    val file = args.firstOrNull() ?: return Printer.fatal("No input file specified")

    var text = ""

    try {
        text= Filer.readFromFile(file)
    }
    catch (e: Exception) {
        return Printer.fatal("Cannot read file $file")
    }

    try {
        Compiler.compile(text,file)
    }
    catch (e: Exception) {
        Printer.fatal("Cannot compile file $file")
        if (ArgsProvider.debug)
            e.printStackTrace()
        return

    }

    if (ArgsProvider.noCompilation) {
        return
    }


    val command = "gcc ${Compiler.buildPath}${RemoveFileExtension(file)}.c -o ${Compiler.buildPath}${RemoveFileExtension(file)}"

    try {
        val process = ProcessBuilder()
            .command("bash", "-c", command)
            .redirectErrorStream(true)
            .start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}