import AST.*
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.useful.list
import czechtina.*
import czechtina.lesana.czechtinaLesana
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
            |   --fpeterek      Uses macros from old czechtina.h file
        """.trimMargin())

    //create file with name of input file in current directory
    val file = args.firstOrNull() ?: return println("No input file specified")
    var withoutExtension = file.substring(0, file.length - 3)
    val code = File(file).readText()

    println(code)

    val czechtina = czechtinaLesana()
    val tree = czechtina.parse(InputFactory.fromString(code, "code")) as ASTNode

    if (args.any() { it == "--show-tree" }) {
        println(tree.toString())
    }

    val cCode = tree.toC()

    File("$withoutExtension.c").writeText(cCode)
    if (args.any() { it == "--no-compile" }) {
        return
    }




    val command = "gcc $withoutExtension.c -o $withoutExtension.exe"

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

        val exitCode = process.waitFor()
        println("Exited with error code $exitCode")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}