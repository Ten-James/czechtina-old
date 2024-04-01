import compiler.Compiler
import utils.*
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {
    if (args.any() { it == "--help" })
        return Printer.printHelp()

    ArgsProvider.processArgs(args)


    if (ArgsProvider.setDir) {
        Compiler.buildPath= ArgsProvider.dir
    }

    //create file with name of input file in current directory
    val file = args.firstOrNull()
    if (file == null ){
        Printer.fatal("No input file specified")
        println("Try --help for more information")
        return
    }

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

    if (ArgsProvider.outputName.isEmpty()) {
        return
    }

    val command = "gcc ${Compiler.buildPath}${RemoveFileExtension(file)}.c -o ${Compiler.buildPath}${ArgsProvider.outputName}"

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

    Printer.success("Compiled $file to ${Compiler.buildPath}${ArgsProvider.outputName}")
}
