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
        """.trimMargin())

    if (args.any(){it == "--fpeterek"}) {
        Compiler.setToCZ()
    }

    val setDirIndex = args.indexOf("--set-dir")
    if (setDirIndex != -1 && setDirIndex != args.size -1) {
        Compiler.buildPath= args[setDirIndex+1] + "/"
    }

    //create file with name of input file in current directory
    val file = args.firstOrNull() ?: return println("No input file specified")

    try {
        Compiler.compileFile(file, args)
    }
    catch (e: Exception) {
        println("Error: variables scope ${Compiler.variables}")
        println("Error: ${e.message}")
        throw e
    }

    if (args.any() { it == "--no-compile" }) {
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