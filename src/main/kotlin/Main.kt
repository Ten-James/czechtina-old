import AST.*
import compiler.Compiler
import cz.j_jzk.klang.input.InputFactory
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.useful.list
import czechtina.*
import czechtina.header.createCzechtinaDefineFile
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
    var withoutExtension = file.substring(0, file.length - 3)
    var code = File(file).readText()

    val splitedCode = code.split("\"").toMutableList()

    for (i in 0 until splitedCode.size) {
        if (i % 2 == 0) continue
        splitedCode[i] = splitedCode[i].replace("\\n", "\\\\n")
        splitedCode[i] = splitedCode[i].replace("\\t", "\\t")
        splitedCode[i] = splitedCode[i].replace(" ", "#\$#CZECHTINAMEZERA\$#\$")
    }
    code = splitedCode.joinToString("\"")


    val czechtina = czechtinaLesana()
    try {

    } catch (e: Exception)
    {
        e.printStackTrace()
        return
    }
    val tree = czechtina.parse(InputFactory.fromString(code, "code")) as ASTNode

    if (args.any() { it == "--show-tree" }) {
        println(tree.toString())
    }

    var cCode = tree.toC()

    if (Compiler.compilingTo == "CZ") {
        cCode = "#define \"czechtina.h\"\n$cCode"
        createCzechtinaDefineFile()
    }

    if (args.any { it == "--friendly" }) {
        if (Compiler.compilingTo != "C") {
            Compiler.setToC()
            cCode += "\n/*\n ${tree.toC()} \n*/"
        }
    }

    cCode = cCode.replace("#\$#CZECHTINAMEZERA\$#\$", " ")

    File("${Compiler.buildPath}$withoutExtension.c").writeText(cCode)
    if (args.any() { it == "--no-compile" }) {
        return
    }




    val command = "gcc ${Compiler.buildPath}$withoutExtension.c -o ${Compiler.buildPath}$withoutExtension"

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